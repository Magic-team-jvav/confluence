package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/// 巨型陆龟使用独立的缩壳扑击循环，不与独角兽和李小骨的地面冲锋共用状态机。
public final class GiantTortoise extends BaseMonster {
    private static final String PHASE_TAG = "Phase";
    private static final String PHASE_TICKS_TAG = "PhaseTicks";
    private static final String COOLDOWN_TAG = "SpinCooldown";
    private static final String LAUNCH_X_TAG = "LaunchX";
    private static final String LAUNCH_Y_TAG = "LaunchY";
    private static final String LAUNCH_Z_TAG = "LaunchZ";
    private static final int NORMAL_COOLDOWN = 133;
    private static final int RETRACT_TICKS = 10;
    private static final int SPIN_TICKS = 30;
    private static final int EMERGE_TICKS = 10;
    private static final AttributeModifier SHELL_ARMOR = new PortAttributeModifier(Confluence.asResource("giant_tortoise_shell_armor"), 30.0, PortAttributeModifier.Operation.ADD_VALUE).unwrap();
    private static final AttributeModifier SPIN_DAMAGE = new PortAttributeModifier(Confluence.asResource("giant_tortoise_spin_damage"), 1.0, PortAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).unwrap();
    private static final EntityDataAccessor<Byte> PHASE = SynchedEntityData.defineId(GiantTortoise.class, EntityDataSerializers.BYTE);
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RETRACT = RawAnimation.begin().thenPlayAndHold("shrinking_shell");
    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("turn");
    private static final RawAnimation EMERGE = RawAnimation.begin().thenPlayAndHold("turn2");
    private int phaseTicks;
    private int spinCooldown = NORMAL_COOLDOWN;
    private int repathTicks;
    private Vec3 lockedLaunchDirection = Vec3.ZERO;

    public GiantTortoise(EntityType<? extends GiantTortoise> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(PHASE, (byte) Phase.WALK.ordinal());
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new TortoiseAction();
            }
        };
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return getPhase() == Phase.SPINNING || getPhase() == Phase.DECELERATING;
    }

    @Override
    protected double contactAttackInflation() {
        return 0.5;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide) {
            spinCooldown = NORMAL_COOLDOWN;
            if (getPhase() != Phase.WALK) {
                setPhase(Phase.WALK);
                setDeltaMovement(getDeltaMovement().multiply(0.35, 1.0, 0.35));
            }
        }
        return damaged;
    }

    private void setPhase(Phase phase) {
        entityData.set(PHASE, (byte) phase.ordinal());
        phaseTicks = 0;
        repathTicks = 0;
        boolean spinning = phase == Phase.SPINNING || phase == Phase.DECELERATING;
        setAttributeModifier(Attributes.ARMOR, SHELL_ARMOR, spinning);
        setAttributeModifier(Attributes.ATTACK_DAMAGE, SPIN_DAMAGE, spinning);
    }

    private void setAttributeModifier(net.minecraft.world.entity.ai.attributes.Attribute attribute, AttributeModifier modifier, boolean enabled) {
        var instance = getAttribute(attribute);
        if (instance == null) return;
        if (enabled && instance.getModifier(modifier.getId()) == null)
            instance.addTransientModifier(modifier);
        if (!enabled) instance.removeModifier(modifier.getId());
    }

    private Phase getPhase() {
        Phase[] phases = Phase.values();
        return phases[Math.max(0, Math.min(entityData.get(PHASE), phases.length - 1))];
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte(PHASE_TAG, (byte) getPhase().ordinal());
        tag.putInt(PHASE_TICKS_TAG, phaseTicks);
        tag.putInt(COOLDOWN_TAG, spinCooldown);
        tag.putDouble(LAUNCH_X_TAG, lockedLaunchDirection.x);
        tag.putDouble(LAUNCH_Y_TAG, lockedLaunchDirection.y);
        tag.putDouble(LAUNCH_Z_TAG, lockedLaunchDirection.z);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        Phase[] phases = Phase.values();
        setPhase(phases[Math.max(0, Math.min(tag.getByte(PHASE_TAG), phases.length - 1))]);
        phaseTicks = Math.max(0, tag.getInt(PHASE_TICKS_TAG));
        spinCooldown = Math.max(0, tag.getInt(COOLDOWN_TAG));
        lockedLaunchDirection = new Vec3(tag.getDouble(LAUNCH_X_TAG), tag.getDouble(LAUNCH_Y_TAG), tag.getDouble(LAUNCH_Z_TAG));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Phase", 2, state -> switch (getPhase()) {
            case WALK -> state.isMoving() ? state.setAndContinue(WALK) : PlayState.STOP;
            case RETRACTING -> state.setAndContinue(RETRACT);
            case SPINNING, DECELERATING -> state.setAndContinue(SPIN);
            case EMERGING -> state.setAndContinue(EMERGE);
        }));
    }

    private final class TortoiseAction extends BTNode {
        @Override
        public BTStatus execute() {
            phaseTicks++;
            LivingEntity target = getTarget();
            switch (getPhase()) {
                case WALK -> updateWalking(target);
                case RETRACTING -> {
                    getNavigation().stop();
                    if (phaseTicks >= RETRACT_TICKS) beginSpin();
                }
                case SPINNING -> updateSpin(target);
                case DECELERATING -> updateDeceleration();
                case EMERGING -> {
                    getNavigation().stop();
                    if (phaseTicks >= EMERGE_TICKS) setPhase(Phase.WALK);
                }
            }
            return BTStatus.RUNNING;
        }

        private void updateWalking(LivingEntity target) {
            if (target == null || !target.isAlive()) {
                if (--repathTicks <= 0 || getNavigation().isDone()) {
                    Vec3 destination = LandRandomPos.getPos(GiantTortoise.this, 8, 4);
                    if (destination != null)
                        getNavigation().moveTo(destination.x, destination.y, destination.z, 0.45);
                    repathTicks = 40;
                }
                return;
            }
            if (--repathTicks <= 0 || getNavigation().isDone()) {
                getNavigation().moveTo(target, 0.45);
                repathTicks = 10;
            }
            double distance = distanceTo(target);
            int maximumCooldown = distance > 32.0 && hasLineOfSight(target) ? 12
                    : distance > 12.0 && hasLineOfSight(target) ? 27 : NORMAL_COOLDOWN;
            spinCooldown = Math.min(spinCooldown, maximumCooldown);
            if (--spinCooldown <= 0 && onGround()) {
                Vec3 aim = target.getEyePosition().subtract(position());
                lockedLaunchDirection = new Vec3(aim.x, Math.max(2.5, aim.y + 3.0), aim.z).normalize();
                faceCombatDirection(lockedLaunchDirection, 180.0F, 180.0F);
                setPhase(Phase.RETRACTING);
            }
        }

        private void beginSpin() {
            setPhase(Phase.SPINNING);
            Vec3 direction = lockedLaunchDirection.lengthSqr() > 1.0E-6 ? lockedLaunchDirection : getForward();
            faceCombatDirection(direction, 180.0F, 180.0F);
            setDeltaMovement(direction.scale(1.35));
            hasImpulse = true;
        }

        private void updateSpin(LivingEntity target) {
            if (target != null && getY() > target.getY() && horizontalDistanceTo(target) < 2.0) {
                Vec3 velocity = getDeltaMovement();
                setDeltaMovement(velocity.x * 0.35, Math.min(velocity.y, -0.35), velocity.z * 0.35);
            }
            if (phaseTicks >= SPIN_TICKS || phaseTicks > 1 && (horizontalCollision || verticalCollision))
                setPhase(Phase.DECELERATING);
        }

        private void updateDeceleration() {
            Vec3 velocity = getDeltaMovement();
            setDeltaMovement(velocity.x * 0.72, velocity.y, velocity.z * 0.72);
            if ((onGround() || isInWater()) && velocity.horizontalDistanceSqr() < 0.01) {
                if (isInWater() && getTarget() != null && getTarget().isAlive()) {
                    spinCooldown = 0;
                    setPhase(Phase.WALK);
                    return;
                }
                setPhase(Phase.EMERGING);
                spinCooldown = NORMAL_COOLDOWN;
            }
        }

        private double horizontalDistanceTo(LivingEntity target) {
            double x = target.getX() - getX();
            double z = target.getZ() - getZ();
            return Math.sqrt(x * x + z * z);
        }
    }

    private enum Phase {
        WALK,
        RETRACTING,
        SPINNING,
        DECELERATING,
        EMERGING
    }
}
