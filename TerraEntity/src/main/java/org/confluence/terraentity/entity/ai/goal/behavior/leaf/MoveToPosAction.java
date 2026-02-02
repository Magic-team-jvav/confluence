package org.confluence.terraentity.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * 移动到目标
 */
public class MoveToPosAction extends BTNode {
    private final PathfinderMob mob;
    private final double speed;
    private double targetX, targetY, targetZ;

    public MoveToPosAction(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    public MoveToPosAction setTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        return this;
    }

    @Override
    public BTStatus execute() {
        if (mob.getNavigation().isDone()) {
            mob.getNavigation().moveTo(targetX, targetY, targetZ, speed);
        }

        if (mob.getNavigation().isDone()) {
            return BTStatus.SUCCESS;
        }

        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        super.stop();
    }

    @Nullable
    protected Vec3 getPosition() {
        return DefaultRandomPos.getPos(this.mob, 10, 7);
    }
}
