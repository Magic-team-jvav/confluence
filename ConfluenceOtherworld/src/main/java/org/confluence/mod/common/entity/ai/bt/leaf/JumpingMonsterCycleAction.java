package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 驱动跳跳兽家族的定时起跳循环。
///
/// 有目标时先持续转头 15 tick，播放蓄力动画并等待 10 tick，然后以 2 格/tick
/// 的水平冲量跃向当前朝向。无目标时先随机观察 20 tick，以 0.5 格/tick 做一次短跳，
/// 落地前额外等待 30 tick 再开始下一轮。运动数值和时序与 1.21 的专用行为树一致，
/// 但状态集中在单一节点内，避免通用近战树先把距离缩短后反而让跃击条件失效。
public final class JumpingMonsterCycleAction extends BTNode {
    private static final int COMBAT_LOOK_TICKS = 15;
    private static final int IDLE_LOOK_TICKS = 20;
    private static final int WINDUP_TICKS = 10;
    private static final int IDLE_RECOVERY_TICKS = 30;
    private static final double COMBAT_IMPULSE = 2.0;
    private static final double IDLE_IMPULSE = 0.5;

    private final PathfinderMob mob;
    private final Runnable animationTrigger;
    private boolean combatCycle;
    private boolean animationTriggered;
    private boolean jumped;
    private int elapsedTicks;
    private Vec3 randomLookPosition = Vec3.ZERO;

    public JumpingMonsterCycleAction(PathfinderMob mob, Runnable animationTrigger) {
        this.mob = mob;
        this.animationTrigger = animationTrigger;
    }

    @Override
    public void start() {
        LivingEntity target = mob.getTarget();
        combatCycle = target != null && target.isAlive();
        animationTriggered = false;
        jumped = false;
        elapsedTicks = 0;

        double angle = mob.getRandom().nextDouble() * Math.PI * 2.0;
        randomLookPosition = mob.getEyePosition().add(Math.cos(angle), 0.0, Math.sin(angle));
    }

    @Override
    public BTStatus execute() {
        LivingEntity currentTarget = mob.getTarget();
        boolean hasLiveTarget = currentTarget != null && currentTarget.isAlive();
        /// 周期开始时的目标状态只决定本轮动作类型，不能阻止更高优先级的战斗状态接管。
        /// 无目标巡游途中发现玩家，或战斗途中目标失效时，立即结束旧周期并在下一刻重建。
        if (hasLiveTarget != combatCycle) {
            return BTStatus.SUCCESS;
        }

        elapsedTicks++;
        int lookTicks = combatCycle ? COMBAT_LOOK_TICKS : IDLE_LOOK_TICKS;

        if (elapsedTicks < lookTicks) {
            updateLookDirection();
            return BTStatus.RUNNING;
        }
        if (elapsedTicks == lookTicks) {
            updateLookDirection();
        }
        if (!animationTriggered) {
            animationTrigger.run();
            animationTriggered = true;
        }
        int jumpTick = lookTicks + WINDUP_TICKS - 1;
        if (elapsedTicks < jumpTick) {
            return BTStatus.RUNNING;
        }
        if (!jumped) {
            if (!mob.onGround()) {
                return BTStatus.RUNNING;
            }
            jumpForward(combatCycle ? COMBAT_IMPULSE : IDLE_IMPULSE);
            jumped = true;
            return combatCycle ? BTStatus.SUCCESS : BTStatus.RUNNING;
        }
        return elapsedTicks >= jumpTick + IDLE_RECOVERY_TICKS - 1
                ? BTStatus.SUCCESS : BTStatus.RUNNING;
    }

    private void updateLookDirection() {
        LivingEntity target = mob.getTarget();
        if (combatCycle && target != null && target.isAlive()) {
            mob.lookAt(target, 90.0F, 85.0F);
            mob.getLookControl().setLookAt(target);
            return;
        }
        mob.lookAt(EntityAnchorArgument.Anchor.EYES, randomLookPosition);
        mob.getLookControl().setLookAt(randomLookPosition);
    }

    private void jumpForward(double horizontalImpulse) {
        Vec3 direction = mob.getForward().multiply(1.0, 0.0, 1.0);
        mob.getJumpControl().jump();
        if (direction.lengthSqr() > 1.0E-8) {
            mob.addDeltaMovement(direction.normalize().scale(horizontalImpulse));
        }
        mob.hasImpulse = true;
    }
}
