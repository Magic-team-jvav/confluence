package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;

/// 按周期逐步增强追击力度的飞行行为。
///
/// 初始阶段缓慢接近目标，随后围绕目标盘旋，最后进入不受距离限制的强追阶段。
/// 强追时如果当前速度方向与目标方向偏差过大，则重新开始蓄势，避免高速实体瞬间折返。
/// 强追不会因为计时降到零而自行结束；命中目标的实体可以调用
/// {@link #resetCycle()} 主动开启下一轮。
public final class PhasedFlyingPursuitAction extends BTNode {
    private final BaseMonster mob;
    private final int cycleTicks;
    private final int approachThreshold;
    private final int aggressiveThreshold;
    private final double approachSpeed;
    private final double aggressiveSpeed;
    private final double maximumSpeed;
    private final double minimumApproachDistance;
    private final double maximumAggressiveTurn;
    private int remainingTicks;
    private double orbitSign;

    public PhasedFlyingPursuitAction(
            BaseMonster mob,
            int cycleTicks,
            int approachThreshold,
            int aggressiveThreshold,
            double approachSpeed,
            double aggressiveSpeed,
            double maximumSpeed,
            double minimumApproachDistance,
            double maximumAggressiveTurn) {
        if (cycleTicks <= approachThreshold || approachThreshold <= aggressiveThreshold || aggressiveThreshold <= 0) {
            throw new IllegalArgumentException("Flying pursuit phase thresholds must be positive and ordered");
        }
        if (approachSpeed < 0.0 || aggressiveSpeed <= 0.0
                || maximumSpeed <= 0.0 || minimumApproachDistance < 0.0
                || maximumAggressiveTurn <= 0.0 || maximumAggressiveTurn > Math.PI) {
            throw new IllegalArgumentException("Flying pursuit movement parameters are outside their valid ranges");
        }
        this.mob = mob;
        this.cycleTicks = cycleTicks;
        this.approachThreshold = approachThreshold;
        this.aggressiveThreshold = aggressiveThreshold;
        this.approachSpeed = approachSpeed;
        this.aggressiveSpeed = aggressiveSpeed;
        this.maximumSpeed = maximumSpeed;
        this.minimumApproachDistance = minimumApproachDistance;
        this.maximumAggressiveTurn = maximumAggressiveTurn;
        resetCycle();
    }

    @Override
    public void start() {
        resetCycle();
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            resetCycle();
            return BTStatus.FAILURE;
        }

        remainingTicks--;
        Vec3 targetDirection = target.getEyePosition().subtract(mob.getEyePosition()).normalize();
        boolean aggressive = remainingTicks < aggressiveThreshold;
        boolean circling = !aggressive && remainingTicks < approachThreshold;
        Vec3 steeringDirection = circling ? orbitDirection(target) : targetDirection;
        Vec3 velocity = mob.getDeltaMovement();
        double acceleration = aggressive ? aggressiveSpeed : approachSpeed;
        Vec3 nextVelocity = velocity.add(steeringDirection.scale(acceleration));
        if (nextVelocity.lengthSqr() > maximumSpeed * maximumSpeed) {
            nextVelocity = nextVelocity.normalize().scale(maximumSpeed);
        }
        mob.faceCombatDirection(nextVelocity, 10.0F, 90.0F);
        mob.setDeltaMovement(nextVelocity);
        mob.hasImpulse = true;

        if (aggressive && nextVelocity.lengthSqr() > 1.0E-8) {
            double dot = Mth.clamp(nextVelocity.normalize().dot(targetDirection), -1.0, 1.0);
            if (Math.acos(dot) > maximumAggressiveTurn) {
                resetCycle();
            }
        }
        return BTStatus.RUNNING;
    }

    public void resetCycle() {
        remainingTicks = cycleTicks;
        orbitSign = mob.getRandom().nextBoolean() ? 1.0 : -1.0;
    }

    /// 用切向速度形成环绕，并根据半径和高度误差缓慢拉回目标附近。
    private Vec3 orbitDirection(LivingEntity target) {
        Vec3 offset = target.getEyePosition().subtract(mob.getEyePosition());
        Vec3 horizontal = new Vec3(offset.x, 0.0, offset.z);
        if (horizontal.lengthSqr() < 1.0E-7) horizontal = new Vec3(1.0, 0.0, 0.0);
        Vec3 radial = horizontal.normalize();
        Vec3 tangent = new Vec3(-radial.z * orbitSign, 0.0, radial.x * orbitSign);
        double radius = Math.max(1.0, minimumApproachDistance);
        double radialCorrection = Mth.clamp((horizontal.length() - radius) / radius, -0.6, 0.6);
        double verticalCorrection = Mth.clamp(offset.y / radius, -0.6, 0.6);
        return tangent.add(radial.scale(radialCorrection)).add(0.0, verticalCorrection, 0.0).normalize();
    }

}
