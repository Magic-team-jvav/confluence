package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseWormMonster;
import org.confluence.mod.util.OverworldUtils;

/// 区域内选点，沿连续弧线转向；离土后的下落与世界底部保护不再争夺同一高度带。
public final class WormMovementAction extends BTNode {
    private static final int WANDER_RESELECT_TICKS = 60;
    private static final double AIR_GRAVITY = 0.08;
    private static final double MAX_FALL_SPEED = 0.8;
    private static final double TURN_ANGLE = 0.10;
    private final PathfinderMob worm;
    private final Profile profile;
    private Vec3 wanderTarget;
    private int wanderTicks;
    private Vec3 heading;
    private Vec3 lastRegionPosition;

    public WormMovementAction(PathfinderMob worm, Profile profile) {
        this.worm = worm;
        this.profile = profile;
    }

    @Override
    public void start() {
        wanderTarget = null;
        wanderTicks = 0;
        heading = worm.getLookAngle();
        lastRegionPosition = null;
    }

    @Override
    public BTStatus execute() {
        Vec3 position = worm.position();
        Vec3 velocity = worm.getDeltaMovement();
        boolean inside = insideRegion(position);
        if (inside) lastRegionPosition = position;

        if (!profile.canFly() && !canBurrow()) {
            double falling = velocity.y - AIR_GRAVITY;
            if (falling < -MAX_FALL_SPEED) falling = -MAX_FALL_SPEED;
            Vec3 fall = new Vec3(velocity.x * 0.98, falling, velocity.z * 0.98);
            Vec3 next = position.add(fall);
            if (insideWorld(next)) {
                worm.setDeltaMovement(fall);
                heading = fall.normalize();
                wanderTarget = null;
                return BTStatus.RUNNING;
            }
        }

        LivingEntity target = worm.getTarget();
        double speed = profile.wanderSpeed();
        Vec3 destination;
        if (!inside) {
            destination = lastRegionPosition != null && insideRegion(lastRegionPosition)
                    ? lastRegionPosition : findNearbyTarget();
        } else if (target != null && target.isAlive() && worm.canAttack(target)
                && insideRegion(target.getBoundingBox().getCenter())) {
            destination = target.getBoundingBox().getCenter();
            speed = profile.attackSpeed();
            wanderTarget = null;
        } else {
            if (target != null) worm.setTarget(null);
            if (wanderTarget == null || --wanderTicks <= 0 || !insideRegion(wanderTarget)
                    || position.distanceToSqr(wanderTarget) < 4) {
                wanderTarget = chooseWanderTarget();
                wanderTicks = WANDER_RESELECT_TICKS;
            }
            destination = wanderTarget;
        }

        /// 人工生成或群系变化可能让附近完全没有适生区域；仍保持游走，不把区域限制变成冻结。
        if (destination == null) {
            if (wanderTarget == null || --wanderTicks <= 0
                    || !insideWorld(wanderTarget) || position.distanceToSqr(wanderTarget) < 4) {
                wanderTarget = randomWanderTarget();
                wanderTicks = WANDER_RESELECT_TICKS;
            }
            destination = wanderTarget;
        }
        Vec3 offset = destination.subtract(position);
        if (offset.lengthSqr() < 1.0E-6) {
            wanderTarget = null;
            return BTStatus.RUNNING;
        }
        Vec3 desired = offset.normalize();
        if (heading == null) heading = worm.getLookAngle();
        /// 活动区只改变目的地，不否决转弯经过的位置，也不截断速度。
        if (inside && !insideRegion(position.add(heading.scale(speed * 12)))) {
            Vec3 inward = findNearbyTarget();
            if (inward != null) desired = inward.subtract(position).normalize();
        }
        heading = turnTowards(heading, desired);
        Vec3 movement = heading.scale(speed);
        /// 世界边缘是物理保护，不参与群系边界的判断。
        if (!insideWorld(position.add(movement))) {
            if (position.y + movement.y < minimumY() && movement.y < 0)
                movement = new Vec3(movement.x, -movement.y, movement.z);
            else if (position.y + movement.y + worm.getBbHeight() >= worm.level().getMaxBuildHeight() && movement.y > 0)
                movement = new Vec3(movement.x, -movement.y, movement.z);
            if (!loaded(position.add(movement))) movement = movement.scale(-1);
            heading = movement.normalize();
            if (!loaded(position.add(movement))) movement = Vec3.ZERO;
        }
        worm.setDeltaMovement(movement);
        return BTStatus.RUNNING;
    }

    /// 只控制连续转向，不处理活动区或裁剪位移。
    private static Vec3 turnTowards(Vec3 current, Vec3 desired) {
        double dot = current.dot(desired);
        if (dot >= Math.cos(TURN_ANGLE)) return desired;
        Vec3 tangent = desired.subtract(current.scale(dot));
        if (tangent.lengthSqr() < 1.0E-10) {
            Vec3 axis = Math.abs(current.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
            tangent = current.cross(axis);
        }
        return current.scale(Math.cos(TURN_ANGLE)).add(tangent.normalize().scale(Math.sin(TURN_ANGLE))).normalize();
    }

    private double minimumY() {
        return worm.level().getMinBuildHeight() + 0.5;
    }

    private boolean loaded(Vec3 position) {
        return worm.level().hasChunk(Mth.floor(position.x) >> 4, Mth.floor(position.z) >> 4);
    }

    private boolean insideRegion(Vec3 position) {
        return insideWorld(position) && (!(worm instanceof BaseWormMonster baseWorm)
                || baseWorm.isInsideActivityRegion(BlockPos.containing(position)));
    }

    private boolean insideWorld(Vec3 position) {
        return position.y >= minimumY() && position.y + worm.getBbHeight() < worm.level().getMaxBuildHeight()
                && loaded(position);
    }

    private boolean canBurrow() {
        var bounds = worm.getBoundingBox();
        for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(bounds.minX), Mth.floor(bounds.minY), Mth.floor(bounds.minZ),
                Mth.floor(bounds.maxX), Mth.floor(bounds.maxY), Mth.floor(bounds.maxZ))) {
            if (!worm.level().hasChunkAt(pos)) continue;
            var state = worm.level().getBlockState(pos);
            if (!state.getFluidState().isEmpty() || !state.getCollisionShape(worm.level(), pos).isEmpty())
                return true;
        }
        return false;
    }

    private Vec3 chooseWanderTarget() {
        for (int attempt = 0; attempt < 8; attempt++) {
            Vec3 candidate = randomWanderTarget();
            if (insideRegion(candidate)) return candidate;
        }
        return findNearbyTarget();
    }

    /// 同时搜索当前高度和其他高度；薄地形里仍有水平游走候选，不要求十六格底部余量。
    private Vec3 findNearbyTarget() {
        Vec3 best = null;
        double bestScore = -Double.MAX_VALUE;
        Vec3 forward = heading == null ? worm.getLookAngle() : heading;
        for (int distance = 4; distance <= 24; distance += 4) {
            for (int vertical = -1; vertical <= 1; vertical++) {
                for (int index = 0; index < 12; index++) {
                    double angle = index * Math.PI / 6;
                    Vec3 candidate = worm.position().add(Math.cos(angle) * distance, vertical * distance, Math.sin(angle) * distance);
                    if (!insideRegion(candidate)) continue;
                    Vec3 offset = candidate.subtract(worm.position());
                    double score = forward.dot(offset.normalize()) * 4 - offset.length() * 0.05;
                    if (score > bestScore) {
                        bestScore = score;
                        best = candidate;
                    }
                }
            }
        }
        return best;
    }

    private Vec3 randomWanderTarget() {
        Vec3 forward = heading == null ? worm.getLookAngle() : heading;
        double angle = worm.getRandom1211().nextDouble() * Mth.TWO_PI;
        double radius = 8 + worm.getRandom1211().nextDouble() * 12;
        double x = worm.getX() + forward.x * 10 + Math.cos(angle) * radius;
        double z = worm.getZ() + forward.z * 10 + Math.sin(angle) * radius;
        double baseY = switch (profile.wanderHeightMode()) {
            case BELOW_TERRAIN ->
                    loaded(new Vec3(x, worm.getY(), z)) ? Math.min(worm.getY(), worm.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z)) + profile.wanderHeightOffset()) : worm.getY();
            case FIXED_FROM_MIN_HEIGHT ->
                    worm.level().getMinBuildHeight() + profile.wanderHeightBoundary() + profile.wanderHeightOffset();
            case FIXED -> profile.wanderHeightBoundary() + profile.wanderHeightOffset();
            case TERRAIN ->
                    loaded(new Vec3(x, worm.getY(), z)) ? worm.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z)) + profile.wanderHeightOffset() : worm.getY();
        };
        double y = baseY + worm.getRandom1211().nextInt(9) - 3;
        if (y < minimumY()) y = minimumY();
        double ceiling = worm.level().getMaxBuildHeight() - worm.getBbHeight() - 0.5;
        if (y > ceiling) y = ceiling;
        return new Vec3(x, y, z);
    }

    /// 不同蠕虫族只声明移动边界，公共节点统一处理平滑转向和三维速度。
    public record Profile(double attackSpeed, double wanderSpeed, double wanderHeightBoundary,
                          double wanderHeightOffset, WanderHeightMode wanderHeightMode,
                          boolean canFly) {
        public Profile {
            if (attackSpeed <= 0.0 || wanderSpeed <= 0.0 || wanderHeightMode == null) {
                throw new IllegalArgumentException("Worm movement speeds must be positive");
            }
        }

        public static Profile underground() {
            return new Profile(0.4, 0.34, 0.0, -10.0, WanderHeightMode.BELOW_TERRAIN, false);
        }

        public static Profile corruption() {
            return new Profile(0.4, 0.34, 0.0, -2.0, WanderHeightMode.BELOW_TERRAIN, false);
        }

        public static Profile flying() {
            return new Profile(0.4, 0.34, OverworldUtils.getSpaceY() + 10.0, 0.0, WanderHeightMode.FIXED, true);
        }

        public static Profile boneSerpent() {
            return new Profile(0.4, 0.34, 32.0, 0.0, WanderHeightMode.FIXED_FROM_MIN_HEIGHT, false);
        }
    }

    public enum WanderHeightMode {
        BELOW_TERRAIN,
        FIXED_FROM_MIN_HEIGHT,
        FIXED,
        TERRAIN
    }
}
