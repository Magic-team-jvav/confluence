package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/**
 * 恐慌逃跑：向远离玩家的方向随机跑。
 * 持续 RUNNING 直到到达目标或超时。
 */
public class PanicFleeAction extends BTNode {
    protected final PathfinderMob mob;
    protected final double speed;
    protected Vec3 fleeTarget;
    protected int tick;
    protected static final int TIMEOUT = 60;

    public PanicFleeAction(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public void start() {
        tick = 0;
        fleeTarget = DefaultRandomPos.getPosAway(mob, 10, 7, mob.position());
        if (fleeTarget != null) {
            mob.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, speed);
        }
    }

    @Override
    public BTStatus execute() {
        tick++;
        if (tick > TIMEOUT || mob.getNavigation().isDone()) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }
}
