package org.confluence.mod.common.summon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/// 飞行召唤物的通用运行基类。
public abstract class FlyingSummon extends SummonInstance {
    private static final double MOMENTUM_DAMPING = 0.91;
    private final double width;
    private final double height;
    private Vec3 hoverDestination;
    private int hoverRepositionCooldown;

    protected FlyingSummon(ResourceLocation type, ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose, double width, double height) {
        super(type, owner, slotCost, stats, initialPose);
        this.width = width;
        this.height = height;
    }

    @Override
    protected int ownerRecoveryInterval() {
        return 1;
    }

    @Override
    protected boolean canRecoverAt(Vec3 candidatePosition) {
        AABB destination = AABB.ofSize(candidatePosition.add(0.0, height * 0.5, 0.0), width, height, width);
        return owner().level().noCollision(null, destination);
    }

    protected final void moveToward(Vec3 destination, double acceleration, double maximumSpeed) {
        applyForceToward(destination, null, acceleration, maximumSpeed, 20.0F, 20.0F);
    }

    /// 朝目标位置移动，同时允许视线继续朝向另一处位置。
    protected final void moveToward(Vec3 destination, Vec3 lookAtPosition, double acceleration, double maximumSpeed) {
        applyForceToward(destination, lookAtPosition, acceleration, maximumSpeed, 15.0F, 85.0F);
    }

    protected final void moveToward(Vec3 destination, Vec3 lookAtPosition, double acceleration, double maximumSpeed, float maximumYawChange, float maximumPitchChange) {
        applyForceToward(destination, lookAtPosition, acceleration, maximumSpeed, maximumYawChange, maximumPitchChange);
    }

    protected Vec3 idleVelocity() {
        return velocity().scale(MOMENTUM_DAMPING);
    }

    private void applyForceToward(Vec3 destination, Vec3 lookAtPosition, double acceleration, double maximumSpeed, float maximumYawChange, float maximumPitchChange) {
        Vec3 offset = destination.subtract(position());
        double distance = offset.length();
        Vec3 force = distance < 1.0E-6 ? Vec3.ZERO : offset.scale(acceleration / distance);
        Vec3 nextVelocity = velocity().scale(MOMENTUM_DAMPING).add(force);
        if (nextVelocity.lengthSqr() > maximumSpeed * maximumSpeed)
            nextVelocity = nextVelocity.normalize().scale(maximumSpeed);
        if (distance < 0.5) nextVelocity = velocity().scale(0.455);
        if (lookAtPosition == null)
            moveByFacing(nextVelocity, position().add(nextVelocity), maximumYawChange, maximumPitchChange);
        else moveByFacing(nextVelocity, lookAtPosition, maximumYawChange, maximumPitchChange);
    }

    protected final void moveBy(Vec3 movement) {
        float yaw = movement.horizontalDistanceSqr() < 1.0E-8 ? currentPose().yaw()
                : (float) Math.toDegrees(Math.atan2(-movement.x, movement.z));
        float pitch = movement.lengthSqr() < 1.0E-8 ? currentPose().pitch()
                : (float) Math.toDegrees(Math.asin(-movement.normalize().y));
        moveBy(movement, yaw, pitch);
    }

    /// 按指定朝向移动。
    protected final void moveBy(Vec3 movement, float yaw, float pitch) {
        advanceTo(new SummonPose(position().add(movement), yaw, pitch, currentPose().roll()));
    }

    private void moveByFacing(Vec3 movement, Vec3 lookAtPosition, float maximumYawChange, float maximumPitchChange) {
        Vec3 lookDirection = lookAtPosition.subtract(position());
        if (lookDirection.lengthSqr() < 1.0E-8) {
            moveBy(movement);
            return;
        }
        Vec3 normalized = lookDirection.normalize();
        float targetYaw = (float) Math.toDegrees(Math.atan2(-normalized.x, normalized.z));
        float targetPitch = (float) Math.toDegrees(Math.asin(-normalized.y));
        float yaw = currentPose().yaw() + Mth.clamp(Mth.wrapDegrees(targetYaw - currentPose().yaw()), -maximumYawChange, maximumYawChange);
        float pitch = currentPose().pitch() + Mth.clamp(Mth.wrapDegrees(targetPitch - currentPose().pitch()), -maximumPitchChange, maximumPitchChange);
        moveBy(movement, yaw, pitch);
    }

    /// 保持在目标斜上方悬停。
    protected final void hoverNear(Vec3 targetPosition, Vec3 lookAtPosition, double horizontalDistance, double height, double replaceDistance, double chaseAcceleration, double hoverAcceleration, double maximumSpeed) {
        Vec3 horizontal = position().subtract(targetPosition).multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-4) horizontal = new Vec3(0.0, 0.0, 1.0);
        Vec3 nearestPosition = targetPosition.add(horizontal.normalize().scale(horizontalDistance)).add(0.0, height, 0.0);
        if (position().distanceToSqr(nearestPosition) > replaceDistance * replaceDistance) {
            if (hoverDestination == null || --hoverRepositionCooldown <= 0) {
                hoverDestination = nearestPosition;
                hoverRepositionCooldown = 20;
            }
            moveToward(hoverDestination, lookAtPosition, chaseAcceleration, maximumSpeed);
        } else if (owner().getRandom().nextFloat() < 0.5F) {
            moveToward(nearestPosition, lookAtPosition, hoverAcceleration, maximumSpeed);
        } else {
            moveByFacing(velocity().scale(MOMENTUM_DAMPING), lookAtPosition, 15.0F, 85.0F);
        }
    }
}
