package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.WallOfFlesh;

/// 驱动饿鬼围绕 Boss 锚点摆动、追击并在椭圆边界外回收。
final class HungryMovementAction extends BTNode {
    // 饿鬼系绳追击合成后的最大移动速度，单位为方块/tick。
    private static final double MAX_SPEED = 0.35;
    private final TheHungry hungry;
    private Vec3 direction = Vec3.ZERO;
    private int switchTicks = 5;

    HungryMovementAction(TheHungry hungry) {
        this.hungry = hungry;
    }

    @Override
    public BTStatus execute() {
        BaseBoss owner = hungry.getMaster();
        boolean free = hungry.isFree();
        if (!free && owner == null) {
            hungry.setDeltaMovement(Vec3.ZERO);
            return BTStatus.RUNNING;
        }
        Vec3 anchor = hungry.getAnchor();
        if (!free) direction = owner.getForward().normalize();
        LivingEntity target = hungry.getTarget();
        boolean hasTarget = target != null && target.isAlive();
        boolean outsideRange = isOutOfRange(anchor);
        boolean targetInRange = hasTarget && !outsideRange;
        double frequencyMultiplier = hasTarget ? 2.0 : 1.0;
        Vec3 pursuit = hasTarget ? pursuitVelocity(target, anchor, free, targetInRange) : Vec3.ZERO;
        if (!hasTarget && !free) updateIdleDirection(owner);

        Vec3 forward;
        if (free) {
            forward = direction.scale(0.3125);
        } else {
            double sine = hungry.tickCount * 0.15 * frequencyMultiplier;
            double shakeMultiplier = targetInRange ? 0.3 : 1.0;
            Vec3 shake = new Vec3(Math.sin(sine * 0.7) * 0.0875, Math.sin(sine * 1.3) * 0.1125, Math.sin(sine) * 0.075).scale(shakeMultiplier);
            forward = direction.scale(0.25 * (targetInRange ? 1.5 : 1.0)).add(shake);
        }

        Vec3 returnVelocity;
        if (free) {
            returnVelocity = Vec3.ZERO;
        } else if (outsideRange) {
            Vec3 destination = anchor.add(direction.scale(hungry.minimumDistance()));
            Vec3 offset = destination.subtract(hungry.position());
            returnVelocity = offset.lengthSqr() > 1.0E-8 ? offset.normalize().scale(0.3) : Vec3.ZERO;
        } else {
            double offset = 2.5 * (2.25 + Math.sin(hungry.tickCount * 0.05 * frequencyMultiplier));
            Vec3 destination = anchor.add(direction.scale(hungry.minimumDistance() + offset));
            returnVelocity = destination.subtract(hungry.position()).scale(hasTarget ? 0.075 : 0.15);
        }

        Vec3 velocity = pursuit.add(forward).add(returnVelocity);
        if (velocity.lengthSqr() > MAX_SPEED * MAX_SPEED)
            velocity = velocity.normalize().scale(MAX_SPEED);
        hungry.setDeltaMovement(velocity);
        hungry.hasImpulse = true;
        updateRotation(velocity);
        return BTStatus.RUNNING;
    }

    private Vec3 pursuitVelocity(LivingEntity target, Vec3 anchor, boolean free, boolean targetInRange) {
        Vec3 targetPosition = BossMinionCoordinator.predict(target, 4.0D, 3.0D);
        hungry.getLookControl().setLookAt(target, 200.0F, 85.0F);
        hungry.lookAt(target, 200.0F, 85.0F);
        Vec3 towardTarget = targetPosition.subtract(hungry.position()).normalize();
        Vec3 towardAnchor = anchor.subtract(hungry.position()).normalize();
        Vec3 mixed = towardTarget.add(towardAnchor.scale(0.3)).normalize();
        if (!free) direction = direction.lerp(targetPosition.subtract(anchor).normalize(), 0.3);
        return mixed.scale(free ? 1.15 : 1.15 * (targetInRange ? 2.0 : 1.0));
    }

    private void updateIdleDirection(BaseBoss owner) {
        if (--switchTicks > 0 || !(owner instanceof WallOfFlesh)) return;
        switchTicks = hungry.getRandom().nextInt(20) + 10;
        Vec3 candidate = new Vec3(hungry.getRandom().nextDouble() - 0.5, hungry.getRandom().nextDouble() - 0.5, hungry.getRandom().nextDouble() - 0.5);
        if (candidate.lengthSqr() < 1.0E-8) return;
        candidate = candidate.normalize();
        BlockPos testPosition = BlockPos.containing(hungry.position().add(candidate.scale(5.0)));
        if (hungry.level().getBlockState(testPosition).isAir() && testPosition.getY() > hungry.level().getMinBuildHeight())
            direction = direction.lerp(candidate, 0.4);
    }

    private boolean isOutOfRange(Vec3 anchor) {
        Vec3 offset = hungry.position().subtract(anchor);
        double horizontal = Math.hypot(offset.x, offset.z);
        return horizontal / hungry.maximumDistance() + Math.abs(offset.y) / (hungry.maximumDistance() * 2.0) > 1.0;
    }

    private void updateRotation(Vec3 velocity) {
        if (velocity.lengthSqr() < 1.0E-8) return;
        double horizontal = Math.hypot(velocity.x, velocity.z);
        hungry.setYRot((float) (Math.atan2(velocity.z, velocity.x) * 180.0 / Math.PI) - 90.0F);
        hungry.setXRot((float) -(Math.atan2(velocity.y, horizontal) * 180.0 / Math.PI));
    }
}
