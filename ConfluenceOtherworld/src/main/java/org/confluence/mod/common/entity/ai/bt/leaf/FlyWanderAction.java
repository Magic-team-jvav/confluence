package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

public class FlyWanderAction extends BTNode {
    protected final PathfinderMob mob;
    protected final double speed;
    protected final int range;
    protected int tick;
    protected Vec3 target;
    protected static final int TIMEOUT = 100;

    public FlyWanderAction(PathfinderMob mob, double speed, int range) {
        this.mob = mob;
        this.speed = speed;
        this.range = range;
    }

    @Override
    public void start() {
        tick = 0;
        RandomSource r = mob.getRandom();
        target = mob.position().add((r.nextFloat() - 0.5) * range * 2, (r.nextFloat() - 0.5) * range, (r.nextFloat() - 0.5) * range * 2);
    }

    @Override
    public BTStatus execute() {
        tick++;
        if (tick > TIMEOUT) return BTStatus.SUCCESS;
        Vec3 dir = target.subtract(mob.position()).normalize().scale(speed * 0.05);
        mob.setDeltaMovement(mob.getDeltaMovement().add(dir).scale(0.95));
        if (mob.position().distanceToSqr(target) < 1.0) return BTStatus.SUCCESS;
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        mob.setDeltaMovement(Vec3.ZERO);
    }
}
