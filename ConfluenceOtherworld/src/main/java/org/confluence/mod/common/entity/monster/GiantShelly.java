package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/// 在行走、缩壳和翻滚之间循环的巨大卷壳虫。
///
/// 受到攻击会取消缩壳或翻滚；缩壳期间提高护甲，随后朝目标翻滚并以实体碰撞结算一次
/// 近战伤害。阶段与外观变种均同步并保存，客户端只根据同步状态选择动画，不参与战斗判定。
public final class GiantShelly extends BaseMonster {
    private static final String PHASE_TAG = "Phase";
    private static final String PHASE_TICKS_TAG = "PhaseTicks";
    private static final String SPIN_COOLDOWN_TAG = "SpinCooldown";
    private static final String LAUNCH_X_TAG = "LaunchX";
    private static final String LAUNCH_Y_TAG = "LaunchY";
    private static final String LAUNCH_Z_TAG = "LaunchZ";
    private static final String LAUNCH_SPEED_TAG = "LaunchSpeed";
    private static final String VARIANT_TAG = "Variant";
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(GiantShelly.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(GiantShelly.class, EntityDataSerializers.INT);
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ENTER_SHELL = RawAnimation.begin().thenPlayAndHold("shrinking_shell");
    private static final RawAnimation ROLL = RawAnimation.begin().thenLoop("turn");
    private static final RawAnimation RECOVER = RawAnimation.begin().thenPlayAndHold("turn2");
    private static final Phase[] PHASES = Phase.values();
    private static final int NORMAL_SPIN_COOLDOWN = 133;
    private static final int MEDIUM_RANGE_SPIN_COOLDOWN = 44;
    private static final int LONG_RANGE_SPIN_COOLDOWN = 27;
    // 距离仍决定翻滚初速度，但上限避免远距离目标把一次冲锋放大成瞬移。
    private static final double MAX_ROLL_SPEED = 1.2;
    private int phaseTicks;
    private int spinCooldown = NORMAL_SPIN_COOLDOWN;
    private int repathTicks;
    private boolean variantInitialized;
    private Vec3 wanderTarget;
    private Vec3 lockedLaunchDirection = Vec3.ZERO;
    private double lockedLaunchSpeed = MAX_ROLL_SPEED;

    public GiantShelly(EntityType<? extends GiantShelly> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(PHASE, Phase.FREE.ordinal());
        entityData.define(VARIANT, 0);
    }

    @Override
    public void onAddedToWorld() {
        if (!variantInitialized && !level().isClientSide) {
            setVariant(random.nextInt(2));
            variantInitialized = true;
        }
        super.onAddedToWorld();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        phaseTicks++;
        switch (getPhase()) {
            case FREE -> {
                getNavigation().stop();
                if (phaseTicks > stateParameters(Phase.FREE).duration()) {
                    setPhase(Phase.WALK);
                }
            }
            case WALK -> {
                updateWalking();
                LivingEntity target = getTarget();
                if (target != null && target.isAlive() && --spinCooldown <= 0) {
                    lockLaunchDirection(target);
                    setPhase(Phase.ENTERING_SHELL);
                } else if (target == null && phaseTicks > 60) {
                    setPhase(Phase.FREE);
                }
            }
            case ENTERING_SHELL -> {
                getNavigation().stop();
                if (phaseTicks >= stateParameters(Phase.ENTERING_SHELL).duration()) {
                    setPhase(Phase.ROLLING);
                }
            }
            case ROLLING -> {
                updateRolling();
            }
            case DECELERATING -> updateDeceleration();
            case RECOVERING -> {
                getNavigation().stop();
                if (phaseTicks >= stateParameters(Phase.RECOVERING).duration()) {
                    spinCooldown = spinCooldownForTarget();
                    setPhase(Phase.FREE);
                }
            }
        }
    }

    private void updateWalking() {
        LivingEntity target = getTarget();
        if (target != null && target.isAlive()) {
            if (--repathTicks <= 0 || getNavigation().isDone()) {
                getNavigation().moveTo(target, 1.0);
                repathTicks = 10;
            }
            return;
        }
        if (wanderTarget != null && (--repathTicks <= 0 || getNavigation().isDone())) {
            getNavigation().moveTo(wanderTarget.x, wanderTarget.y, wanderTarget.z, 1.0);
            repathTicks = 20;
        }
    }

    private void updateRolling() {
        if (phaseTicks == 1) {
            Vec3 direction = lockedLaunchDirection.lengthSqr() > 1.0E-8 ? lockedLaunchDirection : getForward();
            faceCombatDirection(direction, 180.0F, 180.0F);
            setDeltaMovement(direction.scale(lockedLaunchSpeed));
            hasImpulse = true;
        }
        if (phaseTicks > 1 && horizontalCollision) {
            setPhase(Phase.DECELERATING);
            return;
        }
        // 落地只是冲撞轨迹的一部分，不能像撞墙一样提前结束。地面摩擦会在几 tick 内
        // 吃光初速度，因此滚动阶段保持锁定方向的水平分量，完整持续三十刻后才减速。
        if (phaseTicks > 1 && onGround()) {
            Vec3 velocity = getDeltaMovement();
            Vec3 rollingVelocity = lockedLaunchDirection.scale(lockedLaunchSpeed);
            setDeltaMovement(rollingVelocity.x, velocity.y, rollingVelocity.z);
            hasImpulse = true;
        }
        LivingEntity target = getTarget();
        if (target != null && target.isAlive() && getY() > target.getY() && horizontalDistanceTo(target) < 2.0) {
            Vec3 velocity = getDeltaMovement();
            setDeltaMovement(velocity.x * 0.35, Math.min(velocity.y, -0.35), velocity.z * 0.35);
        }
        if (phaseTicks >= stateParameters(Phase.ROLLING).duration()) setPhase(Phase.DECELERATING);
    }

    private void updateDeceleration() {
        Vec3 velocity = getDeltaMovement();
        setDeltaMovement(velocity.x * 0.72, velocity.y, velocity.z * 0.72);
        if ((!onGround() && !isInWater()) || velocity.horizontalDistanceSqr() >= 0.01) return;
        if (isInWater() && getTarget() != null && getTarget().isAlive()) {
            spinCooldown = 0;
            setPhase(Phase.WALK);
        } else {
            setPhase(Phase.RECOVERING);
        }
    }

    private void lockLaunchDirection(LivingEntity target) {
        Vec3 direction = target.position().add(0.0, 1.0, 0.0).subtract(position());
        lockedLaunchDirection = direction.lengthSqr() > 1.0E-8 ? direction.normalize() : getForward();
        faceCombatDirection(lockedLaunchDirection, 180.0F, 180.0F);
        lockedLaunchSpeed = stateParameters(Phase.ROLLING).behavior()
                .chargeSpeedOr(Math.min(MAX_ROLL_SPEED, direction.length() * 0.5));
    }

    private double horizontalDistanceTo(LivingEntity target) {
        double x = target.getX() - getX();
        double z = target.getZ() - getZ();
        return Math.sqrt(x * x + z * z);
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return getPhase() == Phase.ROLLING || getPhase() == Phase.DECELERATING;
    }

    @Override
    protected double contactAttackInflation() {
        return 1.0;
    }

    private void setPhase(Phase phase) {
        entityData.set(PHASE, phase.ordinal());
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
        wanderTarget = phase == Phase.WALK ? LandRandomPos.getPos(this, 15, 7) : null;
        setSpinModifiers(phase == Phase.ENTERING_SHELL || phase == Phase.ROLLING || phase == Phase.DECELERATING,
                phase == Phase.ROLLING || phase == Phase.DECELERATING);
    }

    private void setSpinModifiers(boolean armored, boolean damaging) {
        setSpecialState(Phase.ENTERING_SHELL, false);
        setSpecialState(Phase.ROLLING, false);
        if (damaging) setSpecialState(Phase.ROLLING, true);
        else if (armored) setSpecialState(Phase.ENTERING_SHELL, true);
    }

    private int spinCooldownForTarget() {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) return NORMAL_SPIN_COOLDOWN;
        double distance = distanceTo(target);
        boolean visible = getSensing().hasLineOfSight(target);
        if (distance > 37.5 && (visible || getY() - target.getY() <= 12.5))
            return LONG_RANGE_SPIN_COOLDOWN;
        if (distance > 12.5 && visible) return MEDIUM_RANGE_SPIN_COOLDOWN;
        return NORMAL_SPIN_COOLDOWN;
    }

    /// 任何有效伤害都会取消正在准备或执行的翻滚，并重新计算完整冷却。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean accepted = super.hurt(source, amount);
        if (accepted && !level().isClientSide) {
            spinCooldown = NORMAL_SPIN_COOLDOWN;
            if (getPhase() != Phase.FREE && getPhase() != Phase.WALK) setPhase(Phase.WALK);
        }
        return accepted;
    }

    public Phase getPhase() {
        int id = entityData.get(PHASE);
        return PHASES[Math.max(0, Math.min(id, PHASES.length - 1))];
    }

    public int getVariant() {
        return entityData.get(VARIANT);
    }

    private void setVariant(int variant) {
        entityData.set(VARIANT, Math.floorMod(variant, 2));
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(PHASE_TAG, getPhase().ordinal());
        tag.putInt(PHASE_TICKS_TAG, phaseTicks);
        tag.putInt(SPIN_COOLDOWN_TAG, spinCooldown);
        tag.putDouble(LAUNCH_X_TAG, lockedLaunchDirection.x);
        tag.putDouble(LAUNCH_Y_TAG, lockedLaunchDirection.y);
        tag.putDouble(LAUNCH_Z_TAG, lockedLaunchDirection.z);
        tag.putDouble(LAUNCH_SPEED_TAG, lockedLaunchSpeed);
        tag.putInt(VARIANT_TAG, getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        int savedPhase = tag.getInt(PHASE_TAG);
        setPhase(PHASES[Math.max(0, Math.min(savedPhase, PHASES.length - 1))]);
        phaseTicks = Math.max(0, tag.getInt(PHASE_TICKS_TAG));
        spinCooldown = tag.contains(SPIN_COOLDOWN_TAG) ? Math.max(0, tag.getInt(SPIN_COOLDOWN_TAG)) : NORMAL_SPIN_COOLDOWN;
        lockedLaunchDirection = new Vec3(tag.getDouble(LAUNCH_X_TAG), tag.getDouble(LAUNCH_Y_TAG), tag.getDouble(LAUNCH_Z_TAG));
        lockedLaunchSpeed = tag.contains(LAUNCH_SPEED_TAG) ? Math.min(MAX_ROLL_SPEED, Math.max(0.0, tag.getDouble(LAUNCH_SPEED_TAG))) : MAX_ROLL_SPEED;
        setVariant(tag.getInt(VARIANT_TAG));
        variantInitialized = true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return random.nextBoolean()
                ? ModSoundEvents.GIANT_SHELLY_FREE_0.get()
                : ModSoundEvents.GIANT_SHELLY_FREE_1.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.GIANT_SHELLY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.GIANT_SHELLY_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "phase",
                3,
                state -> switch (getPhase()) {
                    case WALK -> state.setAndContinue(WALK);
                    case ENTERING_SHELL -> state.setAndContinue(ENTER_SHELL);
                    case ROLLING, DECELERATING -> state.setAndContinue(ROLL);
                    case RECOVERING -> state.setAndContinue(RECOVER);
                    case FREE -> PlayState.STOP;
                }));
    }

    public enum Phase {
        FREE,
        WALK,
        ENTERING_SHELL,
        ROLLING,
        DECELERATING,
        RECOVERING
    }
}
