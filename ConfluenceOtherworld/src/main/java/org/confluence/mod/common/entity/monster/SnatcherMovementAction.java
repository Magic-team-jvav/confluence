package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 抓人草的两阶段锚定摆动行为。
///
/// 一个完整周期持续 150 tick：普通伸展五秒，扩大伸展二点五秒。
/// 有目标时向伸展范围内的目标位置移动，无目标时围绕根部摆动；速度上限为 0.3。
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

    SnatcherMovementAction(Snatcher snatcher) {
        this.snatcher = snatcher;
    }

    @Override
    public void start() {
        direction = snatcher.getRestDirection();
        phase = 0;
    }

    @Override
    public BTStatus execute() {
        if (!snatcher.isAnchored()) {
            return BTStatus.FAILURE;
        }

        phase = (phase + 1) % CYCLE_TICKS;
        boolean extended = phase >= NORMAL_PHASE_TICKS;
        LivingEntity target = snatcher.getTarget();
        if (target == null) {
            tickIdleMovement();
            return BTStatus.RUNNING;
        }

        double reach = extended ? snatcher.extendedReach() : snatcher.normalReach();
        Vec3 targetPosition = target.position().add(0.0, target.getEyeHeight() * 0.5, 0.0);
        snatcher.faceCombatPosition(targetPosition, 12.0F, 85.0F);
        Vec3 fromAnchorToTarget = targetPosition.subtract(snatcher.getAnchor());
        Vec3 desiredPosition = snatcher.getAnchor().add(fromAnchorToTarget.lengthSqr() > reach * reach ? fromAnchorToTarget.normalize().scale(reach) : fromAnchorToTarget);
        Vec3 finalVelocity = desiredPosition.subtract(snatcher.position()).scale(0.1);
        if (finalVelocity.lengthSqr() > MAX_SPEED * MAX_SPEED) {
            finalVelocity = finalVelocity.normalize().scale(MAX_SPEED);
        }

        snatcher.setDeltaMovement(finalVelocity);
        snatcher.hasImpulse = true;
        return BTStatus.RUNNING;
    }

    /// 常态下围绕根部持续缓慢摆动和伸缩，不依赖受击或先取得战斗目标。
    private void tickIdleMovement() {
        Vec3 rest = snatcher.getRestDirection().normalize();
        Vec3 reference = Math.abs(rest.y) < 0.9 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
        Vec3 side = rest.cross(reference).normalize();
        Vec3 vertical = rest.cross(side).normalize();
        double time = snatcher.tickCount * 0.035;
        Vec3 desiredDirection = rest
                .add(side.scale(Math.sin(time) * 0.35))
                .add(vertical.scale(Math.cos(time * 0.73) * 0.25))
                .normalize();
        direction = direction.lerp(desiredDirection, 0.08).normalize();

        double distance = snatcher.normalReach() * (0.58 + Math.sin(time * 0.61) * 0.12);
        Vec3 desiredPosition = snatcher.getAnchor().add(direction.scale(distance));
        Vec3 velocity = desiredPosition.subtract(snatcher.position()).scale(0.08);
        double idleSpeed = 0.12;
        if (velocity.lengthSqr() > idleSpeed * idleSpeed)
            velocity = velocity.normalize().scale(idleSpeed);
        snatcher.setDeltaMovement(velocity);
        snatcher.faceCombatDirection(direction, 8.0F, 8.0F);
        snatcher.hasImpulse = true;
    }

}
