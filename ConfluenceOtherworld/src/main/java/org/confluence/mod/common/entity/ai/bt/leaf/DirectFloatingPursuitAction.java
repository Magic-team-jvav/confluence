package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 复现幽灵和流星头使用的直接悬浮追击。
///
/// 这类生物没有游荡阶段。每个游戏刻先叠加轻微的上下浮动；存在有效目标且自身未处于
/// 受伤硬直时，立即把速度改为指向目标的固定向量。固定速度来自移动速度属性的八成，
/// 因此数据包或难度系统修改属性后无需同步调整行为参数。
///
/// 接触伤害仍由服务端按游戏时间限流。运动向量不做平滑插值是有意保留的 1.21 行为，
/// 不能与需要转向惯性的普通飞行敌怪共用追踪节点。
public final class DirectFloatingPursuitAction extends BTNode {
    private static final double SPEED_MULTIPLIER = 0.8;
    private static final double BOB_FREQUENCY = 0.2;
    private static final double BOB_STRENGTH = 0.008;

    private final PathfinderMob mob;

    public DirectFloatingPursuitAction(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public BTStatus execute() {
        mob.addDeltaMovement(new Vec3(0.0, Math.sin(mob.tickCount * BOB_FREQUENCY) * BOB_STRENGTH, 0.0));

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || mob.hurtTime > 0) {
            return BTStatus.RUNNING;
        }

        Vec3 offset = target.position().subtract(mob.position());
        if (offset.lengthSqr() > 1.0E-6) {
            double speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED)
                    * SPEED_MULTIPLIER;
            mob.setDeltaMovement(offset.normalize().scale(speed));
            mob.hasImpulse = true;
        }
        mob.getLookControl().setLookAt(target, 10.0F, 10.0F);

        return BTStatus.RUNNING;
    }
}
