package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 随机漫步：选一个附近的随机位置然后走过去。
public class RandomStrollAction extends BTNode {
    protected final PathfinderMob mob;
    protected final double speed;
    protected final int horizontalRange;
    protected int tick;
    private boolean pathStarted;
    protected static final int TIMEOUT = 100;

    public RandomStrollAction(PathfinderMob mob, double speed, int horizontalRange) {
        this.mob = mob;
        this.speed = speed;
        this.horizontalRange = horizontalRange;
    }

    @Override
    public void start() {
        tick = 0;
        Vec3 target = DefaultRandomPos.getPos(mob, horizontalRange, 4);
        pathStarted = target != null
                && mob.getNavigation().moveTo(target.x, target.y, target.z, speed);
    }

    @Override
    public BTStatus execute() {
        if (!pathStarted) {
            return BTStatus.FAILURE;
        }
        tick++;
        if (tick > TIMEOUT || mob.getNavigation().isDone()) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        if (pathStarted) {
            mob.getNavigation().stop();
            pathStarted = false;
        }
    }
}
