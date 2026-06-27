package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

public class CircleAroundTargetAction extends BTNode {
    protected final PathfinderMob mob;
    protected final double speed;
    protected final double radius;
    protected int tick;
    protected double angle;
    protected static final int DURATION = 80;

    public CircleAroundTargetAction(PathfinderMob mob, double speed, double radius) {
        this.mob = mob;
        this.speed = speed;
        this.radius = radius;
    }

    @Override
    public void start() {
        tick = 0;
        angle = mob.getRandom().nextDouble() * Math.PI * 2;
    }

    @Override
    public BTStatus execute() {
        tick++;
        if (tick > DURATION || mob.getTarget() == null) return BTStatus.SUCCESS;

        angle += 0.05;
        Vec3 target = mob.getTarget().position();
        Vec3 orbit = target.add(Math.cos(angle) * radius, 1.5, Math.sin(angle) * radius);
        Vec3 dir = orbit.subtract(mob.position()).normalize().scale(speed * 0.04);
        mob.setDeltaMovement(mob.getDeltaMovement().add(dir).scale(0.92));
        mob.getLookControl().setLookAt(target);
        return BTStatus.RUNNING;
    }
}
