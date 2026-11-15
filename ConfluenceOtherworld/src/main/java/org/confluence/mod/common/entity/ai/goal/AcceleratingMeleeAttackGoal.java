package org.confluence.mod.common.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/// 在寻路速度上逐步加速，不改写实体属性或绕过地形碰撞。
public final class AcceleratingMeleeAttackGoal extends MeleeAttackGoal {
    private final double initialSpeed;
    private final double maximumSpeed;
    private final int accelerationTicks;
    private int progress;

    public AcceleratingMeleeAttackGoal(PathfinderMob mob, double initialSpeed, double maximumSpeed, int accelerationTicks) {
        super(mob, initialSpeed, true);
        this.initialSpeed = initialSpeed;
        this.maximumSpeed = maximumSpeed;
        this.accelerationTicks = accelerationTicks;
    }

    @Override
    public void start() {
        progress = 0;
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
        var target = mob.getTarget();
        if (target == null || mob.horizontalCollision || mob.hurtTime > 0
                || target.position().subtract(mob.position()).multiply(1.0, 0.0, 1.0).dot(mob.getDeltaMovement()) < 0.0) {
            progress = Math.max(0, progress - 4);
        } else if (mob.onGround() && !mob.getNavigation().isDone()) {
            progress = Math.min(accelerationTicks, progress + 1);
        }
        mob.getNavigation().setSpeedModifier(initialSpeed + (maximumSpeed - initialSpeed) * progress / accelerationTicks);
    }

    @Override
    public void stop() {
        super.stop();
        progress = 0;
    }
}
