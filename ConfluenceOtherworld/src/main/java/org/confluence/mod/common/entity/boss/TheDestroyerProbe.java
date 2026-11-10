package org.confluence.mod.common.entity.boss;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.confluence.mod.common.entity.projectile.DestroyerLaserProjectile;
import org.jetbrains.annotations.Nullable;

/// 毁灭者释放的独立飞行敌怪。
public final class TheDestroyerProbe extends BaseMonster {
    private static final double FLIGHT_SPEED = 1.125, ACCELERATION = 0.028125;
    private static final double SEPARATION_DISTANCE = 4.0, SEPARATION_SPEED = 0.4;
    private static final int SHOT_INTERVAL = 40;
    private static final double ATTACK_RANGE = 64.0;
    private static final float LASER_SPEED = 2.2F;
    private @Nullable TheDestroyer owner;
    private Vec3 flight = Vec3.ZERO;
    private int shotTimer;

    public TheDestroyerProbe(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
        noPhysics = true;
        lookControl = new LookControl(this) {
            @Override
            protected boolean resetXRotOnTick() {return false;}
        };
    }

    public void attach(TheDestroyer owner, Vec3 position) {
        this.owner = owner;
        setPos(position);
        setTarget(owner.getTarget());
    }

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

    private void tickFlight() {
        if (owner != null && (owner.isRemoved() || !owner.isAlive() || owner.level() != level()))
            owner = null;
        if (owner != null) setTarget(owner.getTarget());
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || target.isRemoved() || target.level() != level() || !canAttack(target)) {
            flight = flight.scale(0.95);
            setDeltaMovement(flight);
            return;
        }

        // 朝目标中心追踪，保留惯性以形成自然的掠过和回转。
        Vec3 center = position().add(0, getBbHeight() * 0.5, 0);
        Vec3 aim = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 offset = aim.subtract(center);
        Vec3 desired = offset.lengthSqr() < 1.0E-6 ? Vec3.ZERO : offset.normalize().scale(FLIGHT_SPEED * Math.min(1.0, offset.length() / SEPARATION_DISTANCE));
        Vec3 separation = Vec3.ZERO;
        for (TheDestroyerProbe other : level().getEntitiesOfClass(TheDestroyerProbe.class,
                getBoundingBox().inflate(SEPARATION_DISTANCE), other -> other != this && other.isAlive())) {
            Vec3 away = position().subtract(other.position());
            double distance = away.length();
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

        getLookControl().setLookAt(aim.x, aim.y, aim.z, 12.0F, 12.0F);
        if (++shotTimer < SHOT_INTERVAL) return;
        shotTimer = 0;
        if (distanceToSqr(target) <= ATTACK_RANGE * ATTACK_RANGE && getSensing().hasLineOfSight(target)) {
            DestroyerLaserProjectile.fire(this, center.add(0, 0.1, 0), aim, LASER_SPEED);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt) shotTimer = 0;
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
        return owner == null || !owner.isAlive() ? super.removeWhenFarAway(distance) : false;
    }
}
