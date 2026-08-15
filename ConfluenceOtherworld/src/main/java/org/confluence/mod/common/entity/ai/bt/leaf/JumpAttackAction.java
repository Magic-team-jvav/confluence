package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/**
 * 从中距离向目标发起一次有前摇的跃击。
 *
 * <p>节点只有在生物落地、冷却结束且目标位于有效距离内时才接管行为树；条件不满足会立即
 * 失败，使后续普通追击分支继续运行。起跳后节点持续到落地或超时，并在飞行过程中至多结算
 * 一次近战命中，避免每 tick 重复伤害。</p>
 */
public final class JumpAttackAction extends BTNode {
    private static final double MINIMUM_DISTANCE = 4.0;
    private static final int MAXIMUM_AIR_TICKS = 40;
    private final PathfinderMob mob;
    private final double speedMultiplier;
    private final double maximumDistance;
    private final int cooldownTicks;
    private final int windupTicks;
    private int lastLaunchTick = Integer.MIN_VALUE / 2;
    private int elapsedTicks;
    private boolean launched;
    private boolean dealtDamage;

    public JumpAttackAction(
            PathfinderMob mob,
            double speedMultiplier,
            double maximumDistance,
            int cooldownTicks,
            int windupTicks) {
        if (!Double.isFinite(speedMultiplier) || speedMultiplier <= 0.0) {
            throw new IllegalArgumentException(
                    "Jump speed multiplier must be finite and positive");
        }
        if (!Double.isFinite(maximumDistance)
                || maximumDistance <= MINIMUM_DISTANCE) {
            throw new IllegalArgumentException(
                    "Jump maximum distance must be finite and greater than four");
        }
        if (cooldownTicks < 0 || windupTicks < 0) {
            throw new IllegalArgumentException(
                    "Jump cooldown and windup must be non-negative");
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
        launched = false;
        dealtDamage = false;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            return BTStatus.FAILURE;
        }
        if (!launched && !canLaunch(target)) {
            return BTStatus.FAILURE;
        }

        elapsedTicks++;
        mob.getNavigation().stop();
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (!launched && elapsedTicks > windupTicks) {
            launchAt(target);
        }
        if (!launched) {
            return BTStatus.RUNNING;
        }

        tryDealContactDamage(target);
        if ((mob.onGround() && elapsedTicks > windupTicks + 1)
                || elapsedTicks > windupTicks + MAXIMUM_AIR_TICKS) {
            mob.setAggressive(false);
            return BTStatus.SUCCESS;
        }
        return BTStatus.RUNNING;
    }

    private boolean canLaunch(LivingEntity target) {
        if (!mob.onGround()
                || mob.tickCount - lastLaunchTick < cooldownTicks) {
            return false;
        }
        double distanceSqr = mob.distanceToSqr(target);
        return distanceSqr > MINIMUM_DISTANCE * MINIMUM_DISTANCE
                && distanceSqr < maximumDistance * maximumDistance;
    }

    private void launchAt(LivingEntity target) {
        Vec3 horizontal = target.position().subtract(mob.position())
                .multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-8) {
            return;
        }
        double speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED)
                * speedMultiplier;
        Vec3 impulse = horizontal.normalize().scale(speed);
        mob.setDeltaMovement(impulse.x, 0.42, impulse.z);
        mob.hasImpulse = true;
        mob.setAggressive(true);
        launched = true;
        lastLaunchTick = mob.tickCount;
    }

    private void tryDealContactDamage(LivingEntity target) {
        if (dealtDamage) {
            return;
        }
        double reach = mob.getBbWidth() * 0.5
                + target.getBbWidth() * 0.5 + 0.75;
        if (mob.distanceToSqr(target) <= reach * reach) {
            dealtDamage = mob.doHurtTarget(target);
        }
    }
}
