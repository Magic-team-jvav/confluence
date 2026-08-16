package org.confluence.mod.common.summon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/// 带实体体积的逻辑召唤物基类。
///
/// <p>召唤物本身不进入世界实体列表，但仍然需要按 Minecraft 的方块碰撞、重力和落地规则移动。
/// 地面召唤物、史莱姆一类有真实体积感的召唤物都应该从这里继承。</p>
public abstract class PhysicalSummon extends SummonInstance {
    private final double width;
    private final double height;
    private boolean onGround;
    private java.util.List<Vec3> groundPath = java.util.List.of();
    private int groundPathIndex;
    private int repathCooldown;
    private net.minecraft.core.BlockPos lastGroundDestination;

    protected PhysicalSummon(ResourceLocation type, ServerPlayer owner, int slotCost, SummonStats stats,
                             SummonPose initialPose, double width, double height) {
        super(type, owner, slotCost, stats, initialPose);
        if (!Double.isFinite(width) || !Double.isFinite(height) || width <= 0.0 || height <= 0.0) {
            throw new IllegalArgumentException("Physical summon dimensions must be finite and positive");
        }
        this.width = width;
        this.height = height;
    }

    /// 按实体碰撞规则推进一次。
    ///
    /// <p>这里直接复用原版碰撞计算，但不会把召唤物注册进世界实体列表。</p>
    protected final Vec3 moveWithCollision(Vec3 requestedMovement) {
        AABB box = collisionBox();
        Vec3 movement = Entity.collideBoundingBox(null, requestedMovement, box, owner().level(),
                owner().level().getEntityCollisions(null, box.expandTowards(requestedMovement)));
        onGround = requestedMovement.y < 0.0 && movement.y != requestedMovement.y;
        Vec3 nextPosition = position().add(movement);
        float yaw = horizontalYaw(movement, currentPose().yaw());
        setPath("physical_move", java.util.List.of(new SummonPose(nextPosition, yaw, currentPose().pitch(), currentPose().roll())));
        return movement;
    }

    /// 无碰撞移动入口。
    ///
    /// <p>仅供原行为明确要求穿行或飞回主人时使用，例如史莱姆距离主人过远后的飞回阶段。</p>
    protected final Vec3 moveWithoutCollision(Vec3 movement) {
        onGround = false;
        Vec3 nextPosition = position().add(movement);
        float yaw = horizontalYaw(movement, currentPose().yaw());
        setPath("physical_no_collision_move", java.util.List.of(new SummonPose(nextPosition, yaw,
                currentPose().pitch(), currentPose().roll())));
        return movement;
    }

    protected final AABB collisionBox() {
        return AABB.ofSize(position().add(0.0, height * 0.5, 0.0), width, height, width);
    }

    @Override
    protected boolean canRecoverAt(Vec3 candidatePosition) {
        Vec3 movement = candidatePosition.subtract(position());
        AABB destination = collisionBox().move(movement);
        if (!owner().level().noCollision(null, destination)) {
            return false;
        }
        var floorPosition = net.minecraft.core.BlockPos.containing(candidatePosition).below();
        var floorState = owner().level().getBlockState(floorPosition);
        return !(floorState.getBlock() instanceof LeavesBlock)
                && floorState.isCollisionShapeFullBlock(owner().level(), floorPosition);
    }

    /// 沿短距离方块路径行走。
    ///
    /// <p>路径会定期刷新；目标跨方块移动时也会尽快重新寻路，避免地面召唤物卡在旧路径上。</p>
    protected final Vec3 navigateGround(Vec3 destination, double speed, double jumpStrength) {
        var destinationBlock = net.minecraft.core.BlockPos.containing(destination);
        if (lastGroundDestination == null || !lastGroundDestination.closerThan(destinationBlock, 2.0) || repathCooldown-- <= 0) {
            groundPath = GroundPathfinder.find(owner().serverLevel(), position(), destination, width, height);
            groundPathIndex = 0;
            repathCooldown = 10;
            lastGroundDestination = destinationBlock;
        }
        while (groundPathIndex < groundPath.size() && position().distanceToSqr(groundPath.get(groundPathIndex)) < 0.36) {
            groundPathIndex++;
        }
        Vec3 waypoint = groundPathIndex < groundPath.size() ? groundPath.get(groundPathIndex) : destination;
        Vec3 horizontal = new Vec3(waypoint.x - position().x, 0.0, waypoint.z - position().z);
        Vec3 horizontalMovement = horizontal.lengthSqr() < 1.0E-6 ? Vec3.ZERO : horizontal.normalize().scale(speed);
        double vertical = velocity().y - 0.08;
        if (onGround && waypoint.y > position().y + 0.35) vertical = jumpStrength;
        return moveWithCollision(new Vec3(horizontalMovement.x, vertical, horizontalMovement.z));
    }

    protected static float horizontalYaw(Vec3 movement, float fallback) {
        return movement.horizontalDistanceSqr() < 1.0E-8 ? fallback
                : (float) Math.toDegrees(Math.atan2(-movement.x, movement.z));
    }

    protected final boolean onGround() {return onGround;}

    protected final double width() {return width;}

    protected final double height() {return height;}
}
