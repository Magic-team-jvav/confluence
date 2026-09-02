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
        if (!Double.isFinite(speed) || speed <= 0.0 || !Double.isFinite(radius) || radius <= 0.0) {
            throw new IllegalArgumentException("Orbit speed and radius must be finite and positive");
        }
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
        var currentTarget = mob.getTarget();
        if (tick > DURATION) return BTStatus.SUCCESS;
        if (currentTarget == null || !currentTarget.isAlive()) return BTStatus.FAILURE;

        angle += 0.05;
        Vec3 target = currentTarget.position();
        Vec3 orbit = target.add(Math.cos(angle) * radius, 1.5, Math.sin(angle) * radius);
        Vec3 dir = orbit.subtract(mob.position()).normalize().scale(speed * 0.04);
        mob.setDeltaMovement(mob.getDeltaMovement().add(dir).scale(0.92));
        mob.hasImpulse = true;
        mob.getLookControl().setLookAt(target);
        return BTStatus.RUNNING;
    }
}
