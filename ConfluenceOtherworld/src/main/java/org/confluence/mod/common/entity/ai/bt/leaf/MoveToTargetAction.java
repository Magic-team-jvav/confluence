package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.PathfinderMob;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

public class MoveToTargetAction extends BTNode {
    protected final PathfinderMob mob;
    protected final double speed;
    protected final double closeEnough;

    public MoveToTargetAction(PathfinderMob mob, double speed, double closeEnough) {
        this.mob = mob;
        this.speed = speed;
        this.closeEnough = closeEnough;
    }

    @Override
    public void start() {
        if (mob.getTarget() != null) {
            mob.getNavigation().moveTo(mob.getTarget(), speed);
        }
    }

    @Override
    public BTStatus execute() {
        if (mob.getTarget() == null) return BTStatus.FAILURE;
        if (mob.distanceToSqr(mob.getTarget()) <= closeEnough * closeEnough) return BTStatus.SUCCESS;
        if (!mob.getNavigation().isDone()) return BTStatus.RUNNING;
        mob.getNavigation().moveTo(mob.getTarget(), speed);
        return BTStatus.RUNNING;
    }
}
