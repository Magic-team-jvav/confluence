package org.confluence.mod.common.summon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;

/**
 * 飞行召唤物的通用运行基类。
 *
 * <p>这类召唤物不会注册为世界实体，服务端只维护逻辑位置、速度和朝向，
 * 再把表现状态同步给客户端渲染层。</p>
 */
public abstract class FlyingSummon extends SummonInstance {
    protected FlyingSummon(ResourceLocation type, ServerPlayer owner, int slotCost, ProjectileCombatSnapshot snapshot, SummonPose initialPose) {
        super(type, owner, slotCost, snapshot, initialPose);
    }

    protected final void moveToward(Vec3 destination, double acceleration, double maximumSpeed) {
        moveToward(destination, null, acceleration, maximumSpeed);
    }

    /**
     * 朝目标位置移动，同时允许视线继续朝向另一处位置。
     *
     * <p>远程召唤物常需要“飞向悬停点，但看向攻击目标”，所以移动方向和朝向不能强绑定。</p>
     */
    protected final void moveToward(Vec3 destination, Vec3 lookAtPosition, double acceleration, double maximumSpeed) {
        Vec3 offset = destination.subtract(position());
        Vec3 nextVelocity = velocity().scale(0.88);
        if (offset.lengthSqr() > 1.0E-6)
            nextVelocity = nextVelocity.add(offset.normalize().scale(acceleration));
        if (nextVelocity.length() > maximumSpeed)
            nextVelocity = nextVelocity.normalize().scale(maximumSpeed);
        if (lookAtPosition == null) moveBy(nextVelocity);
        else moveByFacing(nextVelocity, lookAtPosition);
    }

    protected final void moveBy(Vec3 movement) {
        float yaw = movement.horizontalDistanceSqr() < 1.0E-8 ? currentPose().yaw()
                : (float) Math.toDegrees(Math.atan2(-movement.x, movement.z));
        float pitch = movement.lengthSqr() < 1.0E-8 ? currentPose().pitch()
                : (float) Math.toDegrees(Math.asin(-movement.normalize().y));
        moveBy(movement, yaw, pitch);
    }

    /**
     * 按指定朝向移动。
     *
     * <p>用于朝向与实际飞行方向不完全一致的行为，例如施法前摇、悬停射击和绕行。</p>
     */
    protected final void moveBy(Vec3 movement, float yaw, float pitch) {
        setPath("flying_move", java.util.List.of(new SummonPose(position().add(movement), yaw, pitch, currentPose().roll())));
    }

    private void moveByFacing(Vec3 movement, Vec3 lookAtPosition) {
        Vec3 lookDirection = lookAtPosition.subtract(position());
        if (lookDirection.lengthSqr() < 1.0E-8) {
            moveBy(movement);
            return;
        }
        Vec3 normalized = lookDirection.normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-normalized.x, normalized.z));
        float pitch = (float) Math.toDegrees(Math.asin(-normalized.y));
        moveBy(movement, yaw, pitch);
    }

    protected final void followOwner(double distance, double acceleration, double maximumSpeed) {
        followOwner(distance, acceleration, maximumSpeed, 1.8);
    }

    /**
     * 按指定悬停高度跟随所有者。
     *
     * <p>1.21 侧部分飞行召唤物拥有独立的跟随高度，这里保留这个差异点。</p>
     */
    protected final void followOwner(double distance, double acceleration, double maximumSpeed, double height) {
        Vec3 destination = owner().position().add(0.0, height, 0.0);
        if (position().distanceToSqr(owner().position()) >= distance * distance)
            moveToward(destination, acceleration, maximumSpeed);
        else moveBy(velocity().scale(0.8));
    }

    /**
     * 保持在目标斜上方悬停。
     *
     * <p>距离较远时主动追近，到达悬停点后只施加轻微推力，避免黄蜂、小鬼一类远程召唤物
     * 在目标附近来回硬切。</p>
     */
    protected final void hoverNear(Vec3 targetPosition, Vec3 lookAtPosition, double horizontalDistance, double height,
                                   double replaceDistance, double chaseAcceleration, double hoverAcceleration,
                                   double maximumSpeed) {
        Vec3 horizontal = position().subtract(targetPosition).multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-4) horizontal = new Vec3(0.0, 0.0, 1.0);
        Vec3 destination = targetPosition.add(horizontal.normalize().scale(horizontalDistance)).add(0.0, height, 0.0);
        if (position().distanceToSqr(destination) > replaceDistance * replaceDistance) {
            moveToward(destination, lookAtPosition, chaseAcceleration, maximumSpeed);
        } else if (owner().getRandom().nextFloat() < 0.5F) {
            moveToward(destination, lookAtPosition, hoverAcceleration, maximumSpeed);
        } else {
            moveByFacing(velocity().scale(0.9), lookAtPosition);
        }
    }
}
