package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

public class ChargeAttackAction extends BTNode {
    protected final PathfinderMob mob;
    protected final double speed;
    protected int tick;
    protected static final int DURATION = 30;
    protected static final int WINDUP = 10;

    public ChargeAttackAction(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public void start() {
        tick = 0;
    }

    @Override
    public BTStatus execute() {
        tick++;
        LivingEntity target = mob.getTarget();
        if (target == null) return BTStatus.SUCCESS;

        if (tick < WINDUP) {
            Vec3 dir = target.position().subtract(mob.position()).normalize();
            mob.setDeltaMovement(dir.scale(speed * 0.02));
            return BTStatus.RUNNING;
        }

        if (tick >= DURATION) return BTStatus.SUCCESS;

        Vec3 dir = target.position().subtract(mob.position()).normalize().scale(speed * 0.08);
        mob.setDeltaMovement(mob.getDeltaMovement().add(dir).scale(0.95));

        if (mob.getBoundingBox().inflate(0.5).intersects(target.getBoundingBox())) {
            mob.doHurtTarget(target);
            return BTStatus.SUCCESS;
        }
        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        mob.setDeltaMovement(Vec3.ZERO);
    }
}
