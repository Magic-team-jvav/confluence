package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.confluence.mod.common.entity.projectile.DestroyerLaserProjectile;
import org.jetbrains.annotations.Nullable;

/// 毁灭者释放的独立飞行敌怪。
public final class TheDestroyerProbe extends BaseMonster implements BossOwnedEntity {
    // 泰拉以 60 帧和 16 像素一格计算；这里换算为 20 tick 和方块单位。
    private static final double FLIGHT_SPEED = 1.125, ACCELERATION = 0.028125;
    private static final double SEPARATION_DISTANCE = 6.0, SEPARATION_SPEED = 0.4;
    private static final int SHOT_INTERVAL = 40;
    private static final double ATTACK_RANGE = 64.0;
    private static final float LASER_SPEED = 2.2F;
    private final BossOwnerTracker<TheDestroyer> ownerTracker = new BossOwnerTracker<>(TheDestroyer.class);
    private Vec3 flight = Vec3.ZERO;
    private int shotTimer;
    private Vec3 maneuverPosition;
    private Vec3 maneuverTargetPosition;
    private int maneuverTicks;

    public TheDestroyerProbe(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
        noPhysics = true;
        // 保留战斗朝向的俯仰，避免原版视线控制器每 tick 将其归零。
        lookControl = new LookControl(this) {
            @Override
            protected boolean resetXRotOnTick() {return false;}
        };
    }

    public void attach(TheDestroyer owner, Vec3 position) {
        setPos(position);
        ownerTracker.bind(this, owner);
    }

    /// 探测器的视线和射击原点位于模型中心。
    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.5F;
    }

    @Override
    public @Nullable TheDestroyer getBossOwner() {return ownerTracker.resolve(this);}

    @Override
    public int getMaxHeadYRot() {return 180;}

    // 目标由毁灭者同步，探测器不注册独立索敌或受击反击目标。
    @Override
    protected void registerGoals() {}

    @Override
    protected boolean usesSharedTargeting() {return false;}

    @Override
    protected boolean hasEntityContactAttack() {return true;}

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof TheDestroyerProbe || target instanceof TheDestroyer) && super.canAttack(target);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    @Override
                    public BTStatus execute() {
                        tickFlight();
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            TheDestroyer owner = ownerTracker.tickDependent(this, true, 100);
            if (isRemoved()) return;
            if (owner != null && getTarget() != owner.getTarget()) setTarget(owner.getTarget());
        }
        super.tick();
    }

    private void tickFlight() {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || target.isRemoved() || target.level() != level() || !canAttack(target)) {
            maneuverPosition = null;
            flight = flight.scale(0.95);
            setDeltaMovement(flight);
            return;
        }

        Vec3 center = position().add(0, getBbHeight() * 0.5, 0);
        Vec3 aim = target.position().add(0, target.getBbHeight() * 0.5, 0);
        // 玩家移出当前射击点的适用范围或视线被遮挡时，重新选择位置。
        Vec3 awayFromTarget = center.subtract(aim);
        double targetDistance = awayFromTarget.length();
        boolean blocked = !getSensing().hasLineOfSight(target);
        if (maneuverPosition == null || --maneuverTicks <= 0
                || maneuverTargetPosition.distanceToSqr(aim) > 36
                || blocked && tickCount % 10 == 0) {
            chooseManeuver(target, aim, center);
        }
        Vec3 destination = maneuverPosition.subtract(center);
        Vec3 desired = destination.lengthSqr() < 0.25 ? Vec3.ZERO
                : destination.normalize().scale(FLIGHT_SPEED * Math.min(1.0, destination.length() / 5.0));
        if (targetDistance < 5.0 && targetDistance > 1.0E-6) {
            desired = desired.add(awayFromTarget.scale((5.0 - targetDistance) * 0.18 / targetDistance));
        }
        Vec3 separation = Vec3.ZERO;
        for (TheDestroyerProbe other : level().getEntitiesOfClass(TheDestroyerProbe.class,
                getBoundingBox().inflate(SEPARATION_DISTANCE), other -> other != this && other.isAlive())) {
            Vec3 away = position().subtract(other.position());
            double distanceSquared = away.lengthSqr();
            if (distanceSquared >= SEPARATION_DISTANCE * SEPARATION_DISTANCE) continue;
            double distance = Math.sqrt(distanceSquared);
            if (distance < 1.0E-6) {
                // 同位置出生时给双方相反方向，避免零向量无法分离。
                double angle = (Math.min(getId(), other.getId()) * 2.399963229728653) % (Math.PI * 2);
                away = new Vec3(Math.cos(angle), 0, Math.sin(angle)).scale(getId() < other.getId() ? 1 : -1);
                distance = 0;
            } else away = away.scale(1.0 / distance);
            separation = separation.add(away.scale((SEPARATION_DISTANCE - Math.min(distance, SEPARATION_DISTANCE)) / SEPARATION_DISTANCE));
        }
        if (separation.lengthSqr() > 1.0) separation = separation.normalize();
        desired = desired.add(separation.scale(SEPARATION_SPEED));
        if (desired.lengthSqr() > FLIGHT_SPEED * FLIGHT_SPEED)
            desired = desired.normalize().scale(FLIGHT_SPEED);
        flight = new Vec3(approach(flight.x, desired.x), approach(flight.y, desired.y), approach(flight.z, desired.z));
        setDeltaMovement(flight);

        Vec3 facing = aim.subtract(center);
        float yaw = (float) (Mth.atan2(-facing.x, facing.z) * Mth.RAD_TO_DEG);
        float pitch = (float) (-Mth.atan2(facing.y, facing.horizontalDistance()) * Mth.RAD_TO_DEG);
        setYRot(getYRot() + Mth.clamp(Mth.wrapDegrees(yaw - getYRot()), -12.0F, 12.0F));
        setXRot(getXRot() + Mth.clamp(Mth.wrapDegrees(pitch - getXRot()), -12.0F, 12.0F));
        yHeadRot = getYRot();
        yBodyRot = getYRot();
        if (++shotTimer < SHOT_INTERVAL) return;
        shotTimer = 0;
        if (distanceToSqr(target) <= ATTACK_RANGE * ATTACK_RANGE && getSensing().hasLineOfSight(target)) {
            DestroyerLaserProjectile.fire(this, center.add(0, 0.1, 0), aim, LASER_SPEED);
        }
    }

    /// 在目标附近抽样射击位置，优先选择有视线且与其他探测器分开的点。
    private void chooseManeuver(LivingEntity target, Vec3 aim, Vec3 center) {
        Vec3 predicted = aim.add(target.getDeltaMovement().scale(10));
        Vec3 best = aim.add(0, 4, 0);
        double bestScore = -Double.MAX_VALUE;
        var neighbors = level().getEntitiesOfClass(TheDestroyerProbe.class,
                getBoundingBox().inflate(32), other -> other != this && other.isAlive());
        for (int i = 0; i < 12; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 7 + random.nextDouble() * 8;
            Vec3 candidate = predicted.add(Math.cos(angle) * radius, random.nextDouble() * 8 - 2, Math.sin(angle) * radius);
            boolean visible = level().clip(new ClipContext(candidate, aim, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
            double score = (visible ? 30 : 0) - candidate.distanceTo(center) * 0.3;
            for (TheDestroyerProbe neighbor : neighbors) {
                score -= Math.max(0, SEPARATION_DISTANCE - candidate.distanceTo(neighbor.position())) * 2;
            }
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        maneuverPosition = best;
        maneuverTargetPosition = aim;
        maneuverTicks = 35 + random.nextInt(46);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt) {
            shotTimer = 0;
            maneuverTicks = 0;
        }
        return hurt;
    }

    private static double approach(double value, double target) {
        return value + Mth.clamp(target - value, -ACCELERATION, ACCELERATION);
    }

    @Override
    public void travel(Vec3 input) {
        if (!level().isClientSide) move(MoverType.SELF, getDeltaMovement());
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return ownerTracker.getOwnerUUID() == null && super.removeWhenFarAway(distance);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ownerTracker.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerTracker.load(tag);
    }

    @Override
    public void remove(RemovalReason reason) {
        ownerTracker.unbind(this);
        super.remove(reason);
    }
}
