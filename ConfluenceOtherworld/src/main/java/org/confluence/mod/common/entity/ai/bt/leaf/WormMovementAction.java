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

/// 为可穿墙蠕虫提供不依赖原版地面导航网格的三维移动。
///
/// 钻地型仅在方块或液体内主动转向，出土后按惯性下落；飞行型可持续三维追踪。
/// 无目标时按各行为族选择游走落点，体节只跟随本体轨迹，不参与寻路。
public final class WormMovementAction extends BTNode {
    // 无有效目标时每 30 tick 重新选择游走方向，避免逐 tick 随机导致折线抖动。
    private static final int WANDER_RESELECT_TICKS = 30;
    // 速度为 0.4 时将九十度目标每 tick 收敛约五度，转弯半径足以容纳两格长体节。
    // 权重过大会让头部先折向而身体尚未传递到该弯点，形成体节互相穿插。
    private static final double TURN_WEIGHT = 0.08;
    // 钻地蠕虫离开方块或液体后沿惯性下落，不能继续在空中获得追踪升力。
    private static final double AIR_GRAVITY = 0.08;
    private static final double MAX_FALL_SPEED = 0.8;

    private final PathfinderMob worm;
    private final Profile profile;
    private Vec3 wanderTarget;
    private int wanderTicks;
    private Vec3 recoveryTarget;
    private int recoveryTicks;

    public WormMovementAction(PathfinderMob worm, Profile profile) {
        this.worm = worm;
        this.profile = profile;
    }

    @Override
    public void start() {
        wanderTarget = null;
        wanderTicks = 0;
        recoveryTarget = null;
        recoveryTicks = 0;
    }

    @Override
    public BTStatus execute() {
        // 必须先于离土下落判断；穿墙蠕虫穿过底层后，否则会永远落在此分支外的重力逻辑中。
        double floor = worm.level().getMinBuildHeight() + 2.0;
        double bottomMargin = 12.0;
        if (worm.getY() < floor + bottomMargin) {
            Vec3 velocity = worm.getDeltaMovement();
            double upward = Math.max(0.15, (floor + bottomMargin - worm.getY()) * 0.08);
            worm.setDeltaMovement(velocity.x * 0.95, Math.min(profile.attackSpeed(), upward), velocity.z * 0.95);
            wanderTarget = null;
            return BTStatus.RUNNING;
        }
        if (!profile.canFly() && !canBurrow()) {
            Vec3 velocity = worm.getDeltaMovement();
            velocity = new Vec3(velocity.x * 0.98, Math.max(-MAX_FALL_SPEED, velocity.y - AIR_GRAVITY), velocity.z * 0.98);
            worm.setDeltaMovement(velocity);
            wanderTarget = null;
            return BTStatus.RUNNING;
        }
        // 在转弯所需的距离之前选取区域内落点；保持该落点直到抵达，避免边界来回切换。
        Vec3 forward = worm.getDeltaMovement().lengthSqr() > 1.0E-6 ? worm.getDeltaMovement().normalize() : worm.getLookAngle();
        if (recoveryTarget != null && (--recoveryTicks <= 0
                || worm.distanceToSqr(recoveryTarget) < 4.0 || !insideRegion(recoveryTarget)))
            recoveryTarget = null;
        if (recoveryTarget == null && (!insideRegion(worm.position())
                || !insideRegion(worm.position().add(forward.scale(8.0))))) {
            recoveryTarget = findNearbyActivityPosition();
            recoveryTicks = 60;
        }
        if (recoveryTarget != null) {
            steerTowards(recoveryTarget, profile.wanderSpeed());
            return BTStatus.RUNNING;
        }
        LivingEntity target = worm.getTarget();
        if (target != null && target.isAlive() && worm.canAttack(target)) {
            steerTowards(target.getBoundingBox().getCenter(), profile.attackSpeed());
            wanderTarget = null;
            wanderTicks = 0;
            return BTStatus.RUNNING;
        }

        if (wanderTarget == null || --wanderTicks <= 0 || worm.distanceToSqr(wanderTarget) < 4.0) {
            wanderTarget = chooseWanderTarget();
            wanderTicks = WANDER_RESELECT_TICKS;
        }
        steerTowards(wanderTarget, profile.wanderSpeed());
        return BTStatus.RUNNING;
    }

    private void steerTowards(Vec3 destination, double speed) {
        destination = new Vec3(destination.x,
                Mth.clamp(destination.y, worm.level().getMinBuildHeight() + 16.0, worm.level().getMaxBuildHeight() - 4.0), destination.z);
        Vec3 desired = destination.subtract(worm.position());
        if (desired.lengthSqr() < 1.0E-6) {
            return;
        }
        desired = desired.normalize();
        Vec3 current = worm.getDeltaMovement();
        if (current.lengthSqr() < 1.0E-6) {
            current = worm.getLookAngle();
        }
        current = current.normalize();
        // 正反向量直接混合会永远保持原方向，必须先选取稳定的转弯侧向。
        if (current.dot(desired) < -0.999) {
            Vec3 axis = Math.abs(current.y) < 0.9 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
            desired = current.cross(axis).normalize();
        }
        Vec3 direction = current.scale(1.0 - TURN_WEIGHT).add(desired.scale(TURN_WEIGHT)).normalize();
        worm.setDeltaMovement(direction.scale(speed));
        // 这里只决定速度；头部朝向在位移结束后确定，避免视线控制器覆盖及二次转向滞后。
    }

    private boolean canBurrow() {
        var bounds = worm.getBoundingBox();
        for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(bounds.minX), Mth.floor(bounds.minY), Mth.floor(bounds.minZ),
                Mth.floor(bounds.maxX), Mth.floor(bounds.maxY), Mth.floor(bounds.maxZ))) {
            // 检查已有区块，不能因实体移动探测同步加载新区块。
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
            if (insideRegion(candidate)) {
                return candidate;
            }
        }
        Vec3 nearby = findNearbyActivityPosition();
        // 完全找不到活动区时保留前进方向，不能给静止实体返回自身坐标。
        return nearby == null ? worm.position().add(worm.getLookAngle().scale(8.0)) : nearby;
    }

    private boolean insideRegion(Vec3 position) {
        BlockPos pos = BlockPos.containing(position);
        return worm.level().hasChunkAt(pos) && (!(worm instanceof BaseWormMonster baseWorm)
                || baseWorm.isInsideActivityRegion(pos));
    }

    /// 随机候选全部失败时搜索附近有效空间，也供越界实体返回活动区。
    private Vec3 findNearbyActivityPosition() {
        Vec3 best = null;
        double bestScore = -Double.MAX_VALUE;
        Vec3 forward = worm.getDeltaMovement().lengthSqr() > 1.0E-6
                ? worm.getDeltaMovement().normalize() : worm.getLookAngle();
        for (int radius = 4; radius <= 24; radius += 4) {
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        Vec3 candidate = worm.position().add(x * radius, y * radius, z * radius);
                        if (candidate.y < worm.level().getMinBuildHeight() + 16.0 || candidate.y >= worm.level().getMaxBuildHeight() - 4.0)
                            continue;
                        if (!insideRegion(candidate)) continue;
                        int clearance = 0;
                        for (var face : net.minecraft.core.Direction.values()) {
                            if (insideRegion(candidate.add(Vec3.atLowerCornerOf(face.getNormal()).scale(4.0))))
                                clearance++;
                        }
                        Vec3 offset = candidate.subtract(worm.position());
                        double score = clearance * 10.0 + forward.dot(offset.normalize()) * 4.0 - offset.length() * 0.1;
                        if (score > bestScore) {
                            bestScore = score;
                            best = candidate;
                        }
                    }
                }
            }
        }
        return best;
    }

    private Vec3 randomWanderTarget() {
        Vec3 forward = worm.getLookAngle().normalize().scale(10.0);
        double angle = worm.getRandom1211().nextDouble() * Mth.TWO_PI;
        double radius = 8.0 + worm.getRandom1211().nextDouble() * 12.0;
        double x = worm.getX() + forward.x + Math.cos(angle) * radius;
        double z = worm.getZ() + forward.z + Math.sin(angle) * radius;
        double baseY = switch (profile.wanderHeightMode()) {
            case BELOW_TERRAIN -> worm.level().hasChunk(Mth.floor(x) >> 4, Mth.floor(z) >> 4)
                    ? Math.min(worm.getY(), worm.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Mth.floor(x), Mth.floor(z)) + profile.wanderHeightOffset())
                    : worm.getY();
            case FIXED_FROM_MIN_HEIGHT ->
                    worm.level().getMinBuildHeight() + profile.wanderHeightBoundary() + profile.wanderHeightOffset();
            case FIXED -> profile.wanderHeightBoundary() + profile.wanderHeightOffset();
            case TERRAIN -> worm.level().hasChunk(Mth.floor(x) >> 4, Mth.floor(z) >> 4)
                    ? worm.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z))
                    + profile.wanderHeightOffset()
                    : worm.getY();
        };
        double y = Mth.clamp(baseY + worm.getRandom1211().nextInt(9) - 3.0, worm.level().getMinBuildHeight() + 16.0, worm.level().getMaxBuildHeight() - 4.0);
        return new Vec3(x, y, z);
    }

    /// 不同蠕虫族只声明移动边界，公共节点统一处理平滑转向和三维速度。
    /// @param attackSpeed        攻击时移动速度（方块/秒）
    /// @param wanderSpeed        漫游时移动速度（方块/秒）
    /// @param wanderHeightBoundary 漫游高度边界（与高度模式配合使用）
    /// @param wanderHeightOffset 漫游高度偏移（如负值表示在地形下方）
    /// @param wanderHeightMode   漫游高度模式（FIXED/ABOVE_TERRAIN/BELOW_TERRAIN/FIXED_FROM_MIN_HEIGHT）
    /// @param canFly             是否可飞行（true 时不受重力影响）
    public record Profile(double attackSpeed, double wanderSpeed, double wanderHeightBoundary,
                          double wanderHeightOffset, WanderHeightMode wanderHeightMode,
                          boolean canFly) {
        public Profile {
            if (attackSpeed <= 0.0 || wanderSpeed <= 0.0 || wanderHeightMode == null) {
                throw new IllegalArgumentException("Worm movement speeds must be positive");
            }
        }

        // 地下蠕虫：在地形下方 10 格处漫游，不可飞行
        public static Profile underground() {
            return new Profile(0.4, 0.34, 0.0, -10.0, WanderHeightMode.BELOW_TERRAIN, false);
        }

        // 腐化蠕虫：在地形下方 6 格处漫游，不可飞行
        public static Profile corruption() {
            return new Profile(0.4, 0.34, 0.0, -6.0, WanderHeightMode.BELOW_TERRAIN, false);
        }

        // 飞行蠕虫：在太空高度上方 10 格处按固定高度漫游，可飞行
        public static Profile flying() {
            return new Profile(0.4, 0.34, OverworldUtils.getSpaceY() + 10.0, 0.0, WanderHeightMode.FIXED, true);
        }

        // 骨蛇：以世界最低高度为基准，在固定 32 格高度漫游，不可飞行
        public static Profile boneSerpent() {
            return new Profile(0.4, 0.34, 32.0, 0.0, WanderHeightMode.FIXED_FROM_MIN_HEIGHT, false);
        }
    }

    /// 地下与飞行型限制游走中心高度，地表型使用已加载地形的实际高度。
    public enum WanderHeightMode {
        BELOW_TERRAIN,
        FIXED_FROM_MIN_HEIGHT,
        FIXED,
        TERRAIN
    }
}
