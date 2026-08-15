package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.PathfinderMob;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

public class MoveToTargetAction extends BTNode {
    protected final TargetNavigation navigation;
    protected final double speed;
    protected final double closeEnough;
    private boolean moveAttempted;
    private boolean moveStarted;

    public MoveToTargetAction(PathfinderMob mob, double speed, double closeEnough) {
        this(new PathfinderMobNavigation(mob), speed, closeEnough);
    }

    MoveToTargetAction(TargetNavigation navigation, double speed, double closeEnough) {
        this.navigation = navigation;
        this.speed = speed;
        this.closeEnough = closeEnough;
    }

    @Override
    public void start() {
        moveAttempted = false;
        moveStarted = false;
        if (navigation.hasTarget()) {
            moveAttempted = true;
            moveStarted = navigation.moveToTarget(speed);
        }
    }

    @Override
    public BTStatus execute() {
        if (!navigation.hasTarget()) return BTStatus.FAILURE;
        if (navigation.distanceToTargetSqr() <= closeEnough * closeEnough) return BTStatus.SUCCESS;
        if (moveStarted && !navigation.isDone()) return BTStatus.RUNNING;
        if (moveAttempted && !moveStarted) return BTStatus.FAILURE;
        moveAttempted = true;
        moveStarted = navigation.moveToTarget(speed);
        return moveStarted ? BTStatus.RUNNING : BTStatus.FAILURE;
    }

    interface TargetNavigation {
        boolean hasTarget();

        double distanceToTargetSqr();

        boolean isDone();

        boolean moveToTarget(double speed);
    }

    private record PathfinderMobNavigation(PathfinderMob mob) implements TargetNavigation {
        @Override
        public boolean hasTarget() {
            return mob.getTarget() != null;
        }

        @Override
        public double distanceToTargetSqr() {
            return mob.distanceToSqr(mob.getTarget());
        }

        @Override
        public boolean isDone() {
            return mob.getNavigation().isDone();
        }

        @Override
        public boolean moveToTarget(double speed) {
            return mob.getNavigation().moveTo(mob.getTarget(), speed);
        }
    }
}
