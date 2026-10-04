package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.PathfinderMob;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

public class MoveToTargetAction extends BTNode {
    protected final TargetNavigation navigation;
    protected final double speed;
    protected final double closeEnough;
    private boolean moveStarted;
    private int repathTicks;

    public MoveToTargetAction(PathfinderMob mob, double speed, double closeEnough) {
        this(new PathfinderMobNavigation(mob), speed, closeEnough);
    }

    MoveToTargetAction(TargetNavigation navigation, double speed, double closeEnough) {
        if (speed <= 0.0 || closeEnough < 0.0) {
            throw new IllegalArgumentException("Target movement speed must be positive and stopping distance must be non-negative");
        }
        this.navigation = navigation;
        this.speed = speed;
        this.closeEnough = closeEnough;
    }

    @Override
    public boolean canStart() {
        return navigation.hasTarget();
    }

    @Override
    public void start() {
        moveStarted = false;
        repathTicks = 0;
    }

    @Override
    public BTStatus execute() {
        if (!navigation.hasTarget()) return BTStatus.FAILURE;
        if (navigation.distanceToTargetSqr() <= closeEnough * closeEnough && navigation.canSeeTarget())
            return BTStatus.SUCCESS;
        if (repathTicks-- <= 0) {
            moveStarted |= navigation.moveToTarget(speed);
            repathTicks = 9;
        }
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        if (moveStarted) navigation.stop();
        moveStarted = false;
        repathTicks = 0;
    }

    interface TargetNavigation {
        boolean hasTarget();

        double distanceToTargetSqr();

        boolean canSeeTarget();

        boolean moveToTarget(double speed);

        void stop();
    }

    private record PathfinderMobNavigation(PathfinderMob mob) implements TargetNavigation {
        @Override
        public boolean hasTarget() {
            return mob.getTarget() != null && mob.getTarget().isAlive() && mob.canAttack(mob.getTarget());
        }

        @Override
        public double distanceToTargetSqr() {
            return mob.distanceToSqr(mob.getTarget());
        }

        @Override
        public boolean canSeeTarget() {
            return mob.getSensing().hasLineOfSight(mob.getTarget());
        }

        @Override
        public boolean moveToTarget(double speed) {
            return mob.getNavigation().moveTo(mob.getTarget(), speed);
        }

        @Override
        public void stop() {
            mob.getNavigation().stop();
        }
    }
}
