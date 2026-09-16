package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 抓人草的两阶段锚定摆动行为。
///
/// 一个完整周期持续 150 tick：普通伸展五秒，扩大伸展二点五秒。头部同时叠加
/// 朝向、往复摆动和根部回拉速度，最终速度限制为 0.3，形成连续的藤蔓式运动，而不是直接追逐一个被硬截断的目标点。
///
/// 该状态只保存在行为节点内，不写入实体存档；
/// 重新加载后从新周期开始。根部和初始方向仍由实体同步与持久化。
final class SnatcherMovementAction extends BTNode {
    private static final int NORMAL_PHASE_TICKS = 100;
    private static final int EXTENDED_PHASE_TICKS = 50;
    private static final int CYCLE_TICKS = NORMAL_PHASE_TICKS + EXTENDED_PHASE_TICKS;
    private static final double MAX_SPEED = 0.3;

    private final Snatcher snatcher;
    private Vec3 direction;
    private int phase;
    private int directionSwitchTicks;

    SnatcherMovementAction(Snatcher snatcher) {
        this.snatcher = snatcher;
    }

    @Override
    public void start() {
        direction = snatcher.getRestDirection();
        phase = 0;
        directionSwitchTicks = 100;
    }

    @Override
    public BTStatus execute() {
        if (!snatcher.isAnchored()) {
            return BTStatus.FAILURE;
        }

        phase = (phase + 1) % CYCLE_TICKS;
        boolean extended = phase >= NORMAL_PHASE_TICKS;
        LivingEntity target = snatcher.getTarget();
        Vec3 extraVelocity = target == null
                ? updateIdleDirection()
                : updateTargetDirection(target, extended);

        double frequencyMultiplier = target == null ? 1.0 : 2.0;
        Vec3 forward = direction.normalize().scale(0.2 * Math.sin(snatcher.tickCount * 0.05 * frequencyMultiplier));
        double reach = target != null && extended ? snatcher.extendedReach() : snatcher.normalReach();
        Vec3 returnPosition = snatcher.getAnchor().add(direction.scale(reach * 0.25 * (3.0 + Math.sin(snatcher.tickCount * 0.05 * frequencyMultiplier))));
        Vec3 returnVelocity = returnPosition.subtract(snatcher.position()).scale(0.1);
        Vec3 finalVelocity = extraVelocity.add(forward).add(returnVelocity);
        if (finalVelocity.lengthSqr() > MAX_SPEED * MAX_SPEED) {
            finalVelocity = finalVelocity.normalize().scale(MAX_SPEED);
        }

        snatcher.setDeltaMovement(finalVelocity);
        snatcher.hasImpulse = true;
        return BTStatus.RUNNING;
    }

    private Vec3 updateTargetDirection(LivingEntity target, boolean extended) {
        Vec3 targetPosition = target.position().add(0.0, target.getEyeHeight() * 0.5, 0.0);
        snatcher.faceCombatPosition(targetPosition, 12.0F, 85.0F);

        Vec3 fromHeadToAnchor = snatcher.getAnchor().subtract(snatcher.position());
        Vec3 fromHeadToTarget = targetPosition.subtract(snatcher.position());
        Vec3 fromAnchorToTarget = targetPosition.subtract(snatcher.getAnchor());
        /// 分母必须是“根部到目标”的局部距离。把单位方向与世界坐标相减会让
        /// 运动强度随世界原点距离变化，同一只抓人草换个坐标就会得到不同追踪表现。
        double divisor = fromAnchorToTarget.length();
        Vec3 perpendicular = fromHeadToAnchor.cross(fromHeadToTarget).cross(fromAnchorToTarget);
        Vec3 velocity = Vec3.ZERO;
        if (divisor > 1.0E-6 && perpendicular.lengthSqr() > 1.0E-8) {
            double scale = fromHeadToAnchor.dot(fromHeadToTarget)
                    / divisor * (extended ? 0.25 : 5.0);
            velocity = perpendicular.normalize().scale(-scale);
        }
        if (fromAnchorToTarget.lengthSqr() > 1.0E-8) {
            direction = fromAnchorToTarget.normalize();
        }
        return velocity;
    }

    private Vec3 updateIdleDirection() {
        if (--directionSwitchTicks > 0) {
            return Vec3.ZERO;
        }
        directionSwitchTicks = snatcher.getRandom1211().nextInt(200) + 100;
        Vec3 candidate = new Vec3(snatcher.getRandom1211().nextDouble() - 0.5, snatcher.getRandom1211().nextDouble() - 0.5, snatcher.getRandom1211().nextDouble() - 0.5);
        if (candidate.lengthSqr() < 1.0E-8) {
            return Vec3.ZERO;
        }
        candidate = candidate.normalize();
        BlockPos testPosition = BlockPos.containing(snatcher.position().add(candidate.scale(5.0)));
        BlockState state = snatcher.level().getBlockState(testPosition);
        if (state.isAir() && testPosition.getY() > -65) {
            direction = candidate;
        }
        return Vec3.ZERO;
    }
}
