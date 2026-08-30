package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 从中距离向目标发起一次有前摇的跃击。
///
/// 节点只负责参考侧的一次性起跳；离地后立即交还调度，由普通近战目标处理命中。
public final class JumpAttackAction extends BTNode {
    private static final double MINIMUM_DISTANCE = 4.0;
    private final PathfinderMob mob;
    private final double speedMultiplier;
    private final double maximumDistance;
    private final int cooldownTicks;
    private final int windupTicks;
    private int lastLaunchTick = Integer.MIN_VALUE / 2;
    private int elapsedTicks;

    public JumpAttackAction(PathfinderMob mob, double speedMultiplier, double maximumDistance, int cooldownTicks, int windupTicks) {
        if (!Double.isFinite(speedMultiplier) || speedMultiplier <= 0.0) {
            throw new IllegalArgumentException("Jump speed multiplier must be finite and positive");
        }
        if (!Double.isFinite(maximumDistance) || maximumDistance <= MINIMUM_DISTANCE) {
            throw new IllegalArgumentException("Jump maximum distance must be finite and greater than four");
        }
        if (cooldownTicks < 0 || windupTicks < 0) {
            throw new IllegalArgumentException("Jump cooldown and windup must be non-negative");
        }
        this.mob = mob;
        this.speedMultiplier = speedMultiplier;
        this.maximumDistance = maximumDistance;
        this.cooldownTicks = cooldownTicks;
        this.windupTicks = windupTicks;
    }

    @Override
    public void start() {
        elapsedTicks = 0;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            return BTStatus.FAILURE;
        }
        if (!canLaunch(target)) {
            return BTStatus.FAILURE;
        }

        elapsedTicks++;
        mob.getNavigation().stop();
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (elapsedTicks > windupTicks) {
            launchAt(target);
            return BTStatus.SUCCESS;
        }
        return BTStatus.RUNNING;
    }

    private boolean canLaunch(LivingEntity target) {
        if (!mob.onGround() || mob.tickCount - lastLaunchTick <= cooldownTicks) {
            return false;
        }
        double distanceSqr = mob.distanceToSqr(target);
        return distanceSqr > MINIMUM_DISTANCE * MINIMUM_DISTANCE
                && distanceSqr < maximumDistance * maximumDistance;
    }

    private void launchAt(LivingEntity target) {
        Vec3 horizontal = target.position().subtract(mob.position()).multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-8) {
            return;
        }
        double speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED)
                * speedMultiplier;
        Vec3 impulse = horizontal.normalize().scale(speed);
        mob.getJumpControl().jump();
        mob.addDeltaMovement(new Vec3(impulse.x, 0.0, impulse.z));
        mob.hasImpulse = true;
        mob.setAggressive(true);
        lastLaunchTick = mob.tickCount;
    }

}
