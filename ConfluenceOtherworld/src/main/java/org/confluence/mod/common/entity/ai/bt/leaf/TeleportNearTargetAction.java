package org.confluence.mod.common.entity.ai.bt.leaf;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;


/// 将地面生物传送到目标附近的安全落点。
///
/// 候选点以目标为中心生成，并强制保持至少四格距离。节点只接受脚下可站立、没有熔岩且
/// 完整碰撞箱不与方块重叠的位置，也不会为寻找落点加载新区块。
public class TeleportNearTargetAction extends BTNode {
    private static final int MINIMUM_DISTANCE = 4;
    private final PathfinderMob mob;
    private final int horizontalRange;
    private final int verticalRange;
    private final int attempts;
    private boolean done;

    public TeleportNearTargetAction(PathfinderMob mob, int horizontalRange, int verticalRange, int attempts) {
        if (horizontalRange <= 0 || verticalRange < 0 || attempts <= 0) {
            throw new IllegalArgumentException("Teleport range and attempts must be positive");
        }
        this.mob = mob;
        this.horizontalRange = horizontalRange;
        this.verticalRange = verticalRange;
        this.attempts = attempts;
    }

    @Override
    public void start() {
        done = false;
    }

    @Override
    public BTStatus execute() {
        if (done) return BTStatus.SUCCESS;

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return BTStatus.FAILURE;

        for (int attempt = 0; attempt < attempts; attempt++) {
            int xOffset = mob.getRandom().nextIntBetweenInclusive(-horizontalRange, horizontalRange);
            int zOffset = mob.getRandom().nextIntBetweenInclusive(-horizontalRange, horizontalRange);
            int horizontalDistanceSquared = xOffset * xOffset + zOffset * zOffset;
            if (horizontalDistanceSquared < MINIMUM_DISTANCE * MINIMUM_DISTANCE || horizontalDistanceSquared > horizontalRange * horizontalRange) {
                continue;
            }
            BlockPos origin = target.blockPosition().offset(
                    xOffset,
                    mob.getRandom().nextIntBetweenInclusive(-verticalRange, verticalRange),
                    zOffset);
            Vec3 candidate = findStandingPosition(origin);
            if (candidate == null) continue;

            mob.getNavigation().stop();
            mob.teleportTo(candidate.x, candidate.y, candidate.z);
            done = true;
            return BTStatus.SUCCESS;
        }
        return BTStatus.FAILURE;
    }

    @Nullable
    private Vec3 findStandingPosition(BlockPos origin) {
        BlockPos.MutableBlockPos cursor = origin.above(verticalRange).mutable();
        for (int offset = 0; offset <= verticalRange * 2; ++offset) {
            BlockPos feet = cursor.immutable();
            if (mob.level().hasChunkAt(feet)
                    && mob.level().getBlockState(feet.below()).isFaceSturdy(mob.level(), feet.below(), Direction.UP)
                    && !mob.level().getFluidState(feet).is(FluidTags.LAVA)) {
                Vec3 destination = Vec3.atBottomCenterOf(feet);
                AABB destinationBox = mob.getBoundingBox().move(destination.x - mob.getX(), destination.y - mob.getY(), destination.z - mob.getZ());
                if (mob.level().noCollision(mob, destinationBox)) {
                    return destination;
                }
            }
            cursor.move(Direction.DOWN);
        }
        return null;
    }
}
