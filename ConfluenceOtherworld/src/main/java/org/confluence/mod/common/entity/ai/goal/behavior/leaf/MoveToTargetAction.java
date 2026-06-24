package org.confluence.mod.common.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.confluence.mod.common.entity.ai.goal.behavior.BTNode;

public class MoveToTargetAction extends BTNode {

    PathfinderMob mob;
    final int triggerDistance;
    int findInterval;
    final int _findInterval;
    public MoveToTargetAction(PathfinderMob mob, int triggerDistance, int findInterval) {
        this.mob = mob;
        this.triggerDistance = triggerDistance;
        this._findInterval = findInterval;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && mob.getTarget() != null;
    }

    /**
     * 执行动作
     * @return <p>SUCCESS：进入下一个执行任务</p>
     *         <p>RUNNING：继续执行当前任务</p>
     *         <p>FAILURE：任务失败</p>
     */
    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            return BTStatus.FAILURE;
        }

        if(--this.findInterval <= 0) {
            mob.getNavigation().moveTo(target, 1.0);
            this.findInterval = _findInterval;
        }

        float distance = mob.distanceTo(target);
        if (distance <= triggerDistance) {
            return BTStatus.SUCCESS;
        }

        return BTStatus.RUNNING;
    }

}
