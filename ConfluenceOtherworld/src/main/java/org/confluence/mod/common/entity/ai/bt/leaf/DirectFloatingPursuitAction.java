package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 复现幽灵和怨魂使用的直接悬浮追击。
///
/// 追击阶段不经过路径导航。每个游戏刻先叠加轻微的上下浮动；存在有效目标且自身未处于
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
        mob.hasImpulse = true;

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            // 对应 1.21 FloatAiGoal：无目标时该 Goal 依然持续运行，只是不覆盖
            // 当前水平速度。返回 RUNNING 可防止行为树转入普通飞行游荡。
            return BTStatus.RUNNING;
        }
        if (mob.hurtTime > 0) {
            return BTStatus.RUNNING;
        }

        mob.lookAt(target, 10.0F, 10.0F);
        // 1.21 使用实体原点而不是眼睛高度；否则贴近玩家后会反复校正高度，
        // 看起来像停顿或绕着头部打转。
        Vec3 offset = target.position().subtract(mob.position());
        double speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * SPEED_MULTIPLIER;
        mob.setDeltaMovement(offset.normalize().scale(speed));
        mob.hasImpulse = true;

        return BTStatus.RUNNING;
    }
}
