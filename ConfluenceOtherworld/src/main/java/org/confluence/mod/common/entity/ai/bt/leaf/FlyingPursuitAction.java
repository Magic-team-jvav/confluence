package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 让使用飞行导航的敌怪持续追逐目标。
public final class FlyingPursuitAction extends BTNode {
    private static final int REPATH_INTERVAL = 10;
    private final PathfinderMob mob;
    private final double navigationSpeed;
    private int repathTicks;

    public FlyingPursuitAction(PathfinderMob mob, double navigationSpeed) {
        if (!Double.isFinite(navigationSpeed) || navigationSpeed <= 0.0)
            throw new IllegalArgumentException("Flying pursuit speed must be finite and positive");
        this.mob = mob;
        this.navigationSpeed = navigationSpeed;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            return BTStatus.FAILURE;
        }

        double distanceSqr = mob.distanceToSqr(target);
        Vec3 targetPosition = target.getEyePosition();
        Vec3 direction = targetPosition.subtract(mob.getEyePosition());
        mob.getLookControl().setLookAt(target, 30.0F, 85.0F);
        mob.lookAt(target, 30.0F, 85.0F);
        Vec3 movement = mob.getDeltaMovement();
        if (distanceSqr > 9.0 && angleBetween(movement, direction) > 0.6) {
            mob.setDeltaMovement(movement.scale(0.95));
        }
        if (mob.getNavigation().isDone() || --repathTicks <= 0) {
            mob.getNavigation().moveTo(targetPosition.x, targetPosition.y, targetPosition.z, navigationSpeed);
            repathTicks = REPATH_INTERVAL;
        }

        return BTStatus.RUNNING;
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        repathTicks = 0;
    }

    private static double angleBetween(Vec3 first, Vec3 second) {
        double product = first.length() * second.length();
        if (product < 1.0E-6) return 0.0;
        return Math.acos(Mth.clamp(first.dot(second) / product, -1.0, 1.0));
    }
}
