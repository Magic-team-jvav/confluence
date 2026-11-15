package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;

/// 从中距离向目标发起一次有前摇的跃击。
///
/// 节点只负责参考侧的一次性起跳；离地后立即交还调度，由普通近战目标处理命中。
public final class JumpAttackAction extends BTNode {
    private final BaseMonster mob;
    private final double maximumDistance;
    private final double speedMultiplier;
    private final int cooldownTicks;
    private final int windupTicks;
    private int lastLaunchTick = Integer.MIN_VALUE / 2;
    private int elapsedTicks;

    public JumpAttackAction(BaseMonster mob, double maximumDistance, double speedMultiplier, int cooldownTicks, int windupTicks) {
        if (maximumDistance <= 0.0) {
            throw new IllegalArgumentException("Jump maximum distance must be positive");
        }
        if (speedMultiplier <= 0.0) {
            throw new IllegalArgumentException("Jump speed multiplier must be positive");
        }
        if (cooldownTicks < 0 || windupTicks < 0) {
            throw new IllegalArgumentException("Jump cooldown and windup must be non-negative");
        }
        this.mob = mob;
        this.maximumDistance = maximumDistance;
        this.speedMultiplier = speedMultiplier;
        this.cooldownTicks = cooldownTicks;
        this.windupTicks = windupTicks;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && canLaunch(target);
    }

    @Override
    public void start() {
        elapsedTicks = 0;
        mob.getNavigation().stop();
        mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0.0);
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
        mob.faceCombatPosition(target.getEyePosition(), 30.0F, 30.0F);
        if (elapsedTicks > windupTicks) {
            launchAt(target);
            return BTStatus.SUCCESS;
        }
        return BTStatus.RUNNING;
    }

    private boolean canLaunch(LivingEntity target) {
        if (!mob.onGround() || mob.tickCount - lastLaunchTick < cooldownTicks) {
            return false;
        }
        double distanceSqr = mob.distanceToSqr(target);
        Vec3 horizontal = target.position().subtract(mob.position()).multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-8) return false;
        double contactDistance = Math.max(1.5, (mob.getBbWidth() + target.getBbWidth()) * 0.5 + 0.75);
        return distanceSqr > contactDistance * contactDistance && distanceSqr <= maximumDistance * maximumDistance;
    }

    private void launchAt(LivingEntity target) {
        Vec3 horizontal = target.position().subtract(mob.position()).multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-8) {
            return;
        }
        double speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED)
                * speedMultiplier;
        Vec3 impulse = horizontal.normalize().scale(speed);
        mob.faceCombatDirection(impulse, 180.0F, 180.0F);
        mob.getJumpControl().jump();
        mob.addDeltaMovement(new Vec3(impulse.x, 0.0, impulse.z));
        mob.hasImpulse = true;
        mob.setAggressive(true);
        lastLaunchTick = mob.tickCount;
    }

}
