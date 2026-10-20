package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.entity.ai.SweptContactAttack;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;

import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.UUID;

/// 地面电矛、抬升、爬升加速和滑翔冲刺共用一套服务端状态机。
public final class GigazapperAf2 extends Gigazapper {
    public static final int DASH_COOLDOWN_TICKS = 30 * 20;
    private static final double CLOSE_DISTANCE = 10.0;
    private static final double RETREAT_DISTANCE = 30.0;
    private static final double DASH_DISTANCE = 20.0;
    private static final int MIN_TAKEOFF_TICKS = 8;
    private static final int MIN_ACCELERATION_TICKS = 4;
    private static final int MAX_FLIGHT_TICKS = 240;
    private static final EntityDataAccessor<Integer> FLIGHT_PHASE =
            SynchedEntityData.defineId(GigazapperAf2.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RETREATING =
            SynchedEntityData.defineId(GigazapperAf2.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation STAND = RawAnimation.begin().thenLoop("Stand");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    private static final RawAnimation STAND_AIM = RawAnimation.begin().thenLoop("Stand Aim");
    private static final RawAnimation WALK_AIM = RawAnimation.begin().thenLoop("Walk Aim");
    private static final RawAnimation FLYING = RawAnimation.begin().thenLoop("Flying");
    private static final RawAnimation FLYING_MOVE = RawAnimation.begin().thenLoop("Flying Move");
    private static final RawAnimation FLYING_DASH = RawAnimation.begin().thenLoop("Flying Dash");
    private int dashCooldown;
    private int flightTicks;
    private int phaseTicks;
    private double takeoffTravel;
    private double accelerationDistance;
    private int retreatPathCooldown;
    private int retreatRetryCooldown;
    private int retreatPathFailures;
    private UUID retreatTarget;

    public enum FlightPhase {
        GROUND, FLYING, FLYING_MOVE, FLYING_DASH
    }

    public GigazapperAf2(EntityType<? extends GigazapperAf2> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(FLIGHT_PHASE, FlightPhase.GROUND.ordinal());
        entityData.define(RETREATING, false);
    }

    public FlightPhase getFlightPhase() {
        return FlightPhase.values()[entityData.get(FLIGHT_PHASE)];
    }

    public int getDashCooldown() {
        return dashCooldown;
    }

    public boolean isFlying() {
        return getFlightPhase() != FlightPhase.GROUND;
    }

    public boolean isRetreating() {
        return entityData.get(RETREATING);
    }

    @Override
    public boolean canUseSpear() {
        return !isFlying() && !isRetreating();
    }

    @Override
    protected boolean hasEntityContactAttack() {
        /// 冲刺单独检测，不受普通身体接触的二十 tick 冷却影响，也不会重复结算。
        return super.hasEntityContactAttack() && getFlightPhase() != FlightPhase.FLYING_DASH;
    }

    @Override
    protected BTNode createLandBehavior() {
        BTNode flight = new BTNode() {
            @Override
            public boolean canStart() {
                return isFlying();
            }

            @Override
            public BTStatus execute() {
                if (!isFlying()) {
                    return BTStatus.FAILURE;
                }
                getNavigation().stop();
                return BTStatus.RUNNING;
            }
        };
        BTNode retreat = new BTNode() {
            @Override
            public boolean canStart() {
                return isRetreating();
            }

            @Override
            public BTStatus execute() {
                return isRetreating() ? BTStatus.RUNNING : BTStatus.FAILURE;
            }

            @Override
            public void stop() {
                getNavigation().stop();
            }
        };
        return SelectorNode.of(flight, retreat, super.createLandBehavior());
    }

    @Override
    public void travel(Vec3 input) {
        if (!isFlying()) {
            super.travel(input);
            return;
        }
        if (isEffectiveAi()) {
            move(MoverType.SELF, getDeltaMovement());
        }
        fallDistance = 0;
        calculateEntityAnimation(false);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void tick() {
        Vec3 previousPosition = position();
        Vec3 incomingVelocity = getDeltaMovement();
        boolean wasDashing = getFlightPhase() == FlightPhase.FLYING_DASH;
        super.tick();
        if (level().isClientSide || !isAlive()) {
            return;
        }
        if (isNoAi() || isPassenger()) {
            if (isFlying()) finishFlight();
            entityData.set(RETREATING, false);
            return;
        }
        if (dashCooldown > 0) {
            dashCooldown--;
        }
        if (retreatRetryCooldown > 0) {
            retreatRetryCooldown--;
        }
        if (wasDashing && hitDashTarget(previousPosition, incomingVelocity)) {
            finishFlight();
            return;
        }

        LivingEntity target = getTarget();
        if (!isFlying()) {
            tickFlightPreparation(target);
            return;
        }

        flightTicks++;
        phaseTicks++;
        if (!isValidFlightTarget(target) || flightTicks >= MAX_FLIGHT_TICKS
                || horizontalCollision || verticalCollision && (onGround() || getDeltaMovement().y > 0)
                || isInWaterOrBubble() || isInLava()) {
            finishFlight();
            return;
        }
        if (onGround() && flightTicks > 2) {
            finishFlight();
            return;
        }

        takeoffTravel += position().subtract(previousPosition).horizontalDistance();
        double distance = horizontalDistance(target);
        if (getFlightPhase() != FlightPhase.FLYING_DASH && distance <= CLOSE_DISTANCE) {
            setFlightPhase(FlightPhase.GROUND);
            setNoGravity(false);
            setDeltaMovement(getDeltaMovement().scale(0.2));
            fallDistance = 0;
            beginRetreat(target);
            return;
        }
        if (getFlightPhase() == FlightPhase.FLYING && phaseTicks >= MIN_TAKEOFF_TICKS
                && (takeoffTravel >= accelerationDistance || distance <= DASH_DISTANCE)) {
            setFlightPhase(FlightPhase.FLYING_MOVE);
        } else if (getFlightPhase() == FlightPhase.FLYING_MOVE && distance > CLOSE_DISTANCE && distance <= DASH_DISTANCE
                && phaseTicks >= MIN_ACCELERATION_TICKS) {
            setFlightPhase(FlightPhase.FLYING_DASH);
        }
        updateFlightVelocity(target, distance);
    }

    private boolean isValidFlightTarget(LivingEntity target) {
        return target != null && target.isAlive() && target.level() == level() && canAttack(target)
                && (!(target instanceof Player player) || !player.isCreative() && !player.isSpectator());
    }

    private double horizontalDistance(LivingEntity target) {
        return target.position().subtract(position()).horizontalDistance();
    }

    private void tickFlightPreparation(LivingEntity target) {
        if (dashCooldown > 0 || isNoAi() || isPassenger() || !(target instanceof Player)
                || !isValidFlightTarget(target) || isInWaterOrBubble() || isInLava()) {
            stopRetreat();
            return;
        }
        if (retreatTarget != null && !retreatTarget.equals(target.getUUID())) {
            stopRetreat();
        }
        double distance = horizontalDistance(target);
        if ((distance <= CLOSE_DISTANCE || retreatTarget != null) && !isRetreating()) {
            if (retreatRetryCooldown > 0) {
                return;
            }
            beginRetreat(target);
        }
        /// 进入撤退后保持至拉开三十格，避免刚越过十格就再次起飞。
        if (isRetreating() && distance <= RETREAT_DISTANCE) {
            tickRetreat(target);
            return;
        }
        stopRetreat();
        if (hasLineOfSight(target)) {
            beginFlight();
        }
    }

    private void beginRetreat(LivingEntity target) {
        entityData.set(RETREATING, true);
        retreatTarget = target.getUUID();
        retreatPathCooldown = 0;
        getNavigation().stop();
    }

    private void tickRetreat(LivingEntity target) {
        if (retreatPathCooldown-- > 0) {
            return;
        }
        retreatPathCooldown = 10;
        Vec3 away = position().subtract(target.position()).multiply(1, 0, 1);
        if (away.lengthSqr() < 1.0E-6) {
            away = getLookAngle().multiply(-1, 0, -1);
            if (away.lengthSqr() < 1.0E-6) {
                away = new Vec3(0, 0, -1);
            }
        }
        Vec3 destination = target.position().add(away.normalize().scale(RETREAT_DISTANCE + 2));
        Path path = getNavigation().createPath(BlockPos.containing(destination.x, getY(), destination.z), 0);
        if (path == null || !path.canReach()) {
            Vec3 fallback = DefaultRandomPos.getPosAway(this, 16, 7, target.position());
            path = fallback != null && fallback.subtract(target.position()).horizontalDistance() > horizontalDistance(target)
                    ? getNavigation().createPath(BlockPos.containing(fallback), 0) : null;
        }
        if (path != null && path.canReach() && getNavigation().moveTo(path, 1.2)) {
            retreatPathFailures = 0;
        } else if (++retreatPathFailures >= 3) {
            /// 无法拉开距离时短暂恢复地面战斗，之后重试，避免在死角永久停住。
            stopRetreat();
            retreatTarget = target.getUUID();
            retreatRetryCooldown = 40;
        }
    }

    private void stopRetreat() {
        if (isRetreating()) {
            getNavigation().stop();
            entityData.set(RETREATING, false);
        }
        retreatTarget = null;
        retreatPathFailures = 0;
        retreatPathCooldown = 0;
    }

    private void beginFlight() {
        stopRetreat();
        flightTicks = 0;
        takeoffTravel = 0;
        accelerationDistance = 10 + random.nextDouble() * 5;
        setFlightPhase(FlightPhase.FLYING);
        getNavigation().stop();
        setNoGravity(true);
        setDeltaMovement(0, 0.38, 0);
        fallDistance = 0;
    }

    private void setFlightPhase(FlightPhase phase) {
        entityData.set(FLIGHT_PHASE, phase.ordinal());
        phaseTicks = 0;
    }

    private void updateFlightVelocity(LivingEntity target, double distance) {
        Vec3 offset = target.getBoundingBox().getCenter().subtract(getBoundingBox().getCenter());
        Vec3 horizontal = new Vec3(offset.x, 0, offset.z).normalize();
        double approachDistance = Math.max(0, distance - CLOSE_DISTANCE - 1);
        Vec3 velocity;
        if (getFlightPhase() == FlightPhase.FLYING) {
            velocity = horizontal.scale(Math.min(0.22, approachDistance)).add(0, 0.38, 0);
        } else if (getFlightPhase() == FlightPhase.FLYING_MOVE) {
            double speed = Math.min(1.35, 0.45 + phaseTicks * 0.025);
            velocity = horizontal.scale(Math.min(speed, approachDistance)).add(0, 0.18, 0);
        } else {
            /// 向下滑翔把下降速度转成水平加速；每刻限制转向，允许玩家侧移躲闪。
            Vec3 current = getDeltaMovement();
            double speed = Math.min(2.8, Math.max(0.45, current.horizontalDistance())
                    + 0.035 + Math.max(0, -current.y) * 0.1);
            Vec3 heading = new Vec3(current.x, 0, current.z).normalize();
            if (heading.lengthSqr() < 1.0E-6) {
                heading = horizontal;
            }
            double currentYaw = Math.atan2(heading.z, heading.x);
            double desiredYaw = Math.atan2(horizontal.z, horizontal.x);
            float yaw = Mth.approachDegrees((float) Math.toDegrees(currentYaw), (float) Math.toDegrees(desiredYaw), 4);
            double radians = Math.toRadians(yaw);
            double descent = Mth.clamp(offset.y / Math.max(1, distance) * speed, -4.0, -0.08);
            /// 正上方俯冲时收小水平位移，避免零瞄准方向产生任意朝向的侧冲。
            double horizontalSpeed = speed * Math.min(1, distance);
            velocity = new Vec3(Math.cos(radians) * horizontalSpeed, descent, Math.sin(radians) * horizontalSpeed);
        }
        setDeltaMovement(velocity);
        faceCombatDirection(velocity, 30, 0);
        hasImpulse = true;
    }

    private boolean hitDashTarget(Vec3 previousPosition, Vec3 incomingVelocity) {
        Vec3 previousCenter = getBoundingBox().getCenter().add(previousPosition.subtract(position()));
        for (Entity entity : SweptContactAttack.findTargets(this, previousPosition, 0, 16,
                candidate -> candidate instanceof LivingEntity living && living.isAlive()
                        && !EnemyDamageRules.isProtectedEnemy(candidate) && canContactAttack(candidate))) {
            Vec3 direction = entity.getBoundingBox().getCenter().subtract(previousCenter);
            double bonus = projectedSpeedBonus(incomingVelocity, entity.getDeltaMovement(), direction);
            float damage = (float) (getAttributeValue(LibAttributes.getAttackDamage().get()) + bonus);
            entity.hurt(damageSources().mobAttack(this), damage);
            return true;
        }
        return false;
    }

    /// 速度使用 Minecraft 的格/tick 单位；仅计相互接近的速度分量，额外伤害不超过十。
    public static double projectedSpeedBonus(Vec3 attackerVelocity, Vec3 targetVelocity, Vec3 impactDirection) {
        Vec3 direction = impactDirection.lengthSqr() < 1.0E-8 ? attackerVelocity.normalize() : impactDirection.normalize();
        double projection = attackerVelocity.subtract(targetVelocity).dot(direction);
        return Double.isFinite(projection) ? Mth.clamp(projection, 0, 10) : 0;
    }

    private void finishFlight() {
        setFlightPhase(FlightPhase.GROUND);
        setNoGravity(false);
        dashCooldown = DASH_COOLDOWN_TICKS;
        setDeltaMovement(getDeltaMovement().scale(0.2));
        fallDistance = 0;
        getNavigation().stop();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<GigazapperAf2> controller = new AnimationController<>(this, "gigazapper", 3, state -> {
            RawAnimation animation = switch (getFlightPhase()) {
                case FLYING -> FLYING;
                case FLYING_MOVE -> FLYING_MOVE;
                case FLYING_DASH -> FLYING_DASH;
                case GROUND -> {
                    RawAnimation movement = state.isMoving() ? WALK : STAND;
                    RawAnimation aiming = state.isMoving() ? WALK_AIM : STAND_AIM;
                    yield isAiming() ? aiming : movement;
                }
            };
            return state.setAndContinue(animation);
        });
        controller.triggerableAnim("strike", RawAnimation.begin().thenPlay("attack.spear"));
        controllers.add(controller);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Af2DashCooldown", dashCooldown);
        tag.putBoolean("Af2WasFlying", isFlying());
        tag.putBoolean("Af2Retreating", isRetreating());
        tag.putInt("Af2RetreatRetryCooldown", retreatRetryCooldown);
        if (retreatTarget != null) {
            tag.putUUID("Af2RetreatTarget", retreatTarget);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        dashCooldown = Mth.clamp(tag.getInt("Af2DashCooldown"), 0, DASH_COOLDOWN_TICKS);
        /// 重载时安全取消未完成的飞行，不允许通过卸载区块跳过冲刺冷却。
        if (tag.getBoolean("Af2WasFlying")) {
            dashCooldown = DASH_COOLDOWN_TICKS;
            setDeltaMovement(Vec3.ZERO);
        }
        setFlightPhase(FlightPhase.GROUND);
        entityData.set(RETREATING, dashCooldown == 0 && tag.getBoolean("Af2Retreating"));
        retreatTarget = dashCooldown == 0 && tag.hasUUID("Af2RetreatTarget") ? tag.getUUID("Af2RetreatTarget") : null;
        retreatRetryCooldown = Mth.clamp(tag.getInt("Af2RetreatRetryCooldown"), 0, 40);
        retreatPathCooldown = 0;
        retreatPathFailures = 0;
        setNoGravity(false);
        fallDistance = 0;
    }
}
