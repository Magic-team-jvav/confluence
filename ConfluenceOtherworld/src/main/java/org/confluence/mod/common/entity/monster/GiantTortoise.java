package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.ForgeEventFactory;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 巨型陆龟使用独立的缩壳扑击循环，不与独角兽和李小骨的地面冲锋共用状态机。
public final class GiantTortoise extends BaseMonster {
    private static final String PHASE_TAG = "Phase";
    private static final String PHASE_TICKS_TAG = "PhaseTicks";
    private static final String COOLDOWN_TAG = "SpinCooldown";
    private static final int NORMAL_COOLDOWN = 133;
    private static final double POUNCE_GRAVITY = 0.08;
    private static final EntityDataAccessor<Byte> PHASE = SynchedEntityData.defineId(GiantTortoise.class, EntityDataSerializers.BYTE);
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation RETRACT = RawAnimation.begin().thenPlayAndHold("withdraw");
    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("scroll");
    private static final RawAnimation AIR_SPIN = RawAnimation.begin().thenLoop("scroll_sky");
    private static final RawAnimation EMERGE = RawAnimation.begin().thenPlayAndHold("drill_out");

    private int phaseTicks;
    private int spinCooldown = NORMAL_COOLDOWN;
    private int repathTicks;
    private int meleeCooldown;

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
    public void travel(Vec3 input) {
        if (getPhase() != Phase.SPINNING) {
            super.travel(input);
            return;
        }
        if (isEffectiveAi()) {
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().add(0.0, isNoGravity() ? 0.0 : -POUNCE_GRAVITY, 0.0));
        }
        calculateEntityAnimation(false);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
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
        if (phase != Phase.WALK) {
            getNavigation().stop();
            getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0.0);
            setSpeed(0.0F);
            setXxa(0.0F);
            setYya(0.0F);
            setZza(0.0F);
        }
        boolean spinning = phase == Phase.SPINNING || phase == Phase.DECELERATING;
        setSpecialState(Phase.SPINNING, spinning);
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        Phase[] phases = Phase.values();
        setPhase(phases[Math.max(0, Math.min(tag.getByte(PHASE_TAG), phases.length - 1))]);
        phaseTicks = Math.max(0, tag.getInt(PHASE_TICKS_TAG));
        spinCooldown = Math.max(0, tag.getInt(COOLDOWN_TAG));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Phase", 0, state -> {
            Phase phase = getPhase();
            state.getController().setAnimationSpeed(phase == Phase.RETRACTING || phase == Phase.EMERGING ? 1.5 : 1.0);
            return switch (phase) {
                case WALK ->
                        state.setAndContinue(Math.hypot(getX() - xo, getZ() - zo) > 0.002 ? WALK : IDLE);
                case RETRACTING -> state.setAndContinue(RETRACT);
                case WINDING_UP -> state.setAndContinue(SPIN);
                case SPINNING, DECELERATING -> state.setAndContinue(onGround() ? SPIN : AIR_SPIN);
                case EMERGING -> state.setAndContinue(EMERGE);
            };
        }));
    }

    private void breakSpinVegetation() {
        if (!ForgeEventFactory.getMobGriefingEvent(level(), this)) return;
        AABB sweptBox = getBoundingBox().expandTowards(getDeltaMovement());
        for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(sweptBox.minX, sweptBox.minY, sweptBox.minZ), BlockPos.containing(sweptBox.maxX, sweptBox.maxY, sweptBox.maxZ))) {
            var state = level().getBlockState(pos);
            if ((state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) && state.getDestroySpeed(level(), pos) >= 0.0F)
                level().destroyBlock(pos, true, this);
        }
    }

    private final class TortoiseAction extends BTNode {
        @Override
        public BTStatus execute() {
            phaseTicks++;
            if (meleeCooldown > 0) meleeCooldown--;
            if (getPhase() == Phase.SPINNING || getPhase() == Phase.DECELERATING)
                breakSpinVegetation();
            LivingEntity target = getTarget();
            switch (getPhase()) {
                case WALK -> updateWalking(target);
                case RETRACTING -> {
                    if (target == null || !target.isAlive() || !canAttack(target)) {
                        spinCooldown = NORMAL_COOLDOWN;
                        setPhase(Phase.EMERGING);
                    } else if (phaseTicks >= stateParameters(Phase.RETRACTING).duration())
                        setPhase(Phase.WINDING_UP);
                }
                case WINDING_UP -> {
                    if (target == null || !target.isAlive() || !canAttack(target)) {
                        spinCooldown = NORMAL_COOLDOWN;
                        setPhase(Phase.EMERGING);
                    } else if (phaseTicks >= stateParameters(Phase.WINDING_UP).duration())
                        beginSpin(target);
                }
                case SPINNING -> updateSpin(target);
                case DECELERATING -> updateDeceleration();
                case EMERGING -> {
                    getNavigation().stop();
                    if (phaseTicks >= stateParameters(Phase.EMERGING).duration())
                        setPhase(Phase.WALK);
                }
            }
            return BTStatus.RUNNING;
        }

        private void updateWalking(LivingEntity target) {
            if (target == null || !target.isAlive() || !canAttack(target)) {
                if (--repathTicks <= 0) {
                    Vec3 destination = LandRandomPos.getPos(GiantTortoise.this, 8, 4);
                    if (destination != null)
                        getNavigation().moveTo(destination.x, destination.y, destination.z, 0.45);
                    repathTicks = 40;
                }
                return;
            }
            if (--repathTicks <= 0) {
                getNavigation().moveTo(target, 0.45);
                repathTicks = 10;
            }
            double distance = distanceTo(target);
            if (meleeCooldown == 0 && getBoundingBox().inflate(0.3).intersects(target.getBoundingBox()) && canAttack(target) && hasLineOfSight(target)) {
                doHurtTarget(target);
                meleeCooldown = stateParameters(Phase.WALK).attackInterval();
            }
            boolean visible = hasLineOfSight(target);
            int maximumCooldown = distance > 37.5 && (visible || getY() - target.getY() <= 12.5) ? 12
                    : distance > 12.5 && visible ? 27 : NORMAL_COOLDOWN;
            spinCooldown = Math.min(spinCooldown, maximumCooldown);
            if (spinCooldown > 0) spinCooldown--;
            if (spinCooldown == 0 && (onGround() || isInWaterOrBubble())) beginRetraction(target);
        }

        private void beginRetraction(LivingEntity target) {
            faceCombatDirection(new Vec3(target.getX() - getX(), 0.0, target.getZ() - getZ()), 180.0F, 180.0F);
            setDeltaMovement(new Vec3(0.0, getDeltaMovement().y, 0.0));
            setPhase(Phase.RETRACTING);
        }

        private void beginSpin(LivingEntity target) {
            setPhase(Phase.SPINNING);
            Vec3 offset = target.position().subtract(position());
            double apex = Math.max(6.0, offset.y + 4.0);
            double upwardSpeed = Math.sqrt(2.0 * POUNCE_GRAVITY * apex);
            double flightTicks = (upwardSpeed + Math.sqrt(upwardSpeed * upwardSpeed - 2.0 * POUNCE_GRAVITY * offset.y)) / POUNCE_GRAVITY;
            Vec3 velocity = new Vec3(offset.x / flightTicks, upwardSpeed, offset.z / flightTicks);
            faceCombatDirection(new Vec3(offset.x, 0.0, offset.z), 180.0F, 180.0F);
            setOnGround(false);
            setDeltaMovement(velocity);
            hasImpulse = true;
        }

        private void updateSpin(LivingEntity target) {
            if (onGround() || (isInWaterOrBubble() && getDeltaMovement().y <= 0.0)) {
                setPhase(Phase.DECELERATING);
                return;
            }
            if (target != null && target.isAlive() && canAttack(target)) {
                Vec3 velocity = getDeltaMovement();
                double height = getY() - target.getY();
                double discriminant = velocity.y * velocity.y + 2.0 * POUNCE_GRAVITY * height;
                if (discriminant <= 0.0) return;
                double remainingTicks = (velocity.y + Math.sqrt(discriminant)) / POUNCE_GRAVITY;
                if (remainingTicks <= 6.0) return;
                Vec3 correction = new Vec3((target.getX() - getX()) / remainingTicks - velocity.x, 0.0, (target.getZ() - getZ()) / remainingTicks - velocity.z);
                double length = correction.length();
                if (length > 0.02) correction = correction.scale(0.02 / length);
                setDeltaMovement(velocity.add(correction));
            }
        }

        private void updateDeceleration() {
            LivingEntity target = getTarget();
            if (isInWaterOrBubble() && target != null && target.isAlive() && canAttack(target)) {
                beginRetraction(target);
                return;
            }
            Vec3 velocity = getDeltaMovement();
            setDeltaMovement(velocity.x * 0.72, velocity.y, velocity.z * 0.72);
            if ((onGround() || isInWater()) && velocity.horizontalDistanceSqr() < 0.01) {
                setPhase(Phase.EMERGING);
                spinCooldown = NORMAL_COOLDOWN;
            }
        }

    }

    public enum Phase {
        WALK,
        RETRACTING,
        SPINNING,
        DECELERATING,
        EMERGING,
        WINDING_UP
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.GIANT_TORTOISE_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.GIANT_TORTOISE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.GIANT_TORTOISE_DEATH.get();
    }

}
