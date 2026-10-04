package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;

public class CircleAroundTargetAction extends BTNode {
    protected final BaseMonster mob;
    protected final double speed;
    protected final double radius;
    protected int tick;
    protected double angle;
    private double direction;
    protected static final int DURATION = 80;

    public CircleAroundTargetAction(BaseMonster mob, double speed, double radius) {
        if (speed <= 0.0 || radius <= 0.0) {
            throw new IllegalArgumentException("Orbit speed and radius must be positive");
        }
        this.mob = mob;
        this.speed = speed;
        this.radius = radius;
    }

    @Override
    public void start() {
        tick = 0;
        LivingEntity target = mob.getTarget();
        angle = target == null ? mob.getRandom().nextDouble() * Math.PI * 2 : Math.atan2(mob.getZ() - target.getZ(), mob.getX() - target.getX());
        direction = mob.getRandom().nextBoolean() ? 1.0 : -1.0;
    }

    @Override
    public BTStatus execute() {
        tick++;
        var currentTarget = mob.getTarget();
        if (tick > DURATION) return BTStatus.SUCCESS;
        if (currentTarget == null || !currentTarget.isAlive()) return BTStatus.FAILURE;

        angle += 0.05 * direction;
        Vec3 target = currentTarget.position();
        Vec3 orbit = target.add(Math.cos(angle) * radius, 1.5, Math.sin(angle) * radius);
        Vec3 dir = orbit.subtract(mob.position()).normalize().scale(speed * 0.04);
        mob.setDeltaMovement(mob.getDeltaMovement().add(dir).scale(0.92));
        mob.hasImpulse = true;
        mob.faceCombatPosition(currentTarget.getEyePosition(), 10.0F, 85.0F);
        return BTStatus.RUNNING;
    }
}
