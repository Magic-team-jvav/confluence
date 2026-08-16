package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 按周期逐步增强追击力度的飞行行为。
///
/// <p>初始阶段先蓄势，再只对较远目标缓慢接近，最后进入不受距离限制的强追阶段。
/// 强追时如果当前速度方向与目标方向偏差过大，则重新开始蓄势，避免高速实体瞬间折返。
/// 强追不会因为计时降到零而自行结束；命中目标的实体可以调用
/// {@link #resetCycle()} 主动开启下一轮。</p>
public final class PhasedFlyingPursuitAction extends BTNode {
    private final PathfinderMob mob;
    private final int cycleTicks;
    private final int approachThreshold;
    private final int aggressiveThreshold;
    private final double approachSpeed;
    private final double aggressiveSpeed;
    private final double maximumSpeed;
    private final double minimumApproachDistanceSqr;
    private final double maximumAggressiveTurn;
    private int remainingTicks;

    public PhasedFlyingPursuitAction(
            PathfinderMob mob,
            int cycleTicks,
            int approachThreshold,
            int aggressiveThreshold,
            double approachSpeed,
            double aggressiveSpeed,
            double maximumSpeed,
            double minimumApproachDistance,
            double maximumAggressiveTurn) {
        if (cycleTicks <= approachThreshold
                || approachThreshold <= aggressiveThreshold
                || aggressiveThreshold <= 0) {
            throw new IllegalArgumentException(
                    "Flying pursuit phase thresholds must be positive and ordered");
        }
        this.mob = mob;
        this.cycleTicks = cycleTicks;
        this.approachThreshold = approachThreshold;
        this.aggressiveThreshold = aggressiveThreshold;
        this.approachSpeed = approachSpeed;
        this.aggressiveSpeed = aggressiveSpeed;
        this.maximumSpeed = maximumSpeed;
        this.minimumApproachDistanceSqr =
                minimumApproachDistance * minimumApproachDistance;
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
        mob.getLookControl().setLookAt(target, 10.0F, 90.0F);
        Vec3 targetDirection =
                target.getEyePosition().subtract(mob.getEyePosition()).normalize();
        boolean aggressive = remainingTicks < aggressiveThreshold;
        boolean shouldApproach = aggressive
                || remainingTicks < approachThreshold
                && mob.distanceToSqr(target) > minimumApproachDistanceSqr;
        if (!shouldApproach) {
            return BTStatus.RUNNING;
        }

        Vec3 velocity = mob.getDeltaMovement();
        double acceleration = aggressive ? aggressiveSpeed : approachSpeed;
        Vec3 nextVelocity = velocity.add(targetDirection.scale(acceleration));
        if (nextVelocity.lengthSqr() > maximumSpeed * maximumSpeed) {
            nextVelocity = nextVelocity.normalize().scale(maximumSpeed);
        }
        mob.setDeltaMovement(nextVelocity);

        if (aggressive && nextVelocity.lengthSqr() > 1.0E-8) {
            double dot = Mth.clamp(
                    nextVelocity.normalize().dot(targetDirection), -1.0, 1.0);
            if (Math.acos(dot) > maximumAggressiveTurn) {
                resetCycle();
            }
        }
        return BTStatus.RUNNING;
    }

    public void resetCycle() {
        remainingTicks = cycleTicks;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }
}
