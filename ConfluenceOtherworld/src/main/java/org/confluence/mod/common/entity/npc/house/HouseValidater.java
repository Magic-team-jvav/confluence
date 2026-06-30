package org.confluence.mod.common.entity.npc.house;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * 3D BFS 房屋检测器。
 * 从候选起点扩散，遇空气/房屋构成方块继续，遇墙停止。
 */
public final class HouseValidater {
    private static final int MAX_RADIUS = 15;
    private static final int MIN_VOLUME = 16;
    private static final int MAX_VOLUME = 1500;
    private static final int MIN_LIGHT = 10;

    public enum ResultType {
        FOUND,
        TOO_SMALL,
        TOO_LARGE,
        NO_LIGHT,
        NO_CHAIR,
        NO_TABLE
    }

    public record Result(ResultType type, @Nullable BlockPos min, @Nullable BlockPos max) {
        public static Result error(ResultType type) {
            return new Result(type, null, null);
        }

        public boolean isValid() {
            return type == ResultType.FOUND && min != null && max != null;
        }
    }

    /**
     * 从 start 位置 BFS flood fill，检测是否是合法 NPC 房屋。
     */
    public static Result scan(Level level, BlockPos start) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);

        boolean hasLight = false;
        boolean hasChair = false;
        boolean hasTable = false;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();

            // 更新边界
            minX = Math.min(minX, pos.getX()); maxX = Math.max(maxX, pos.getX());
            minY = Math.min(minY, pos.getY()); maxY = Math.max(maxY, pos.getY());
            minZ = Math.min(minZ, pos.getZ()); maxZ = Math.max(maxZ, pos.getZ());

            BlockState state = level.getBlockState(pos);

            if (!hasLight && level.getLightEmission(pos) >= MIN_LIGHT) hasLight = true;
            if (!hasChair && state.is(ModTags.Blocks.NPC_HOUSE_CHAIR)) hasChair = true;
            if (!hasTable && state.is(ModTags.Blocks.NPC_HOUSE_TABLE)) hasTable = true;

            for (BlockPos neighbor : neighbors(pos)) {
                if (visited.contains(neighbor)) continue;
                int dx = Math.abs(neighbor.getX() - start.getX());
                int dy = Math.abs(neighbor.getY() - start.getY());
                int dz = Math.abs(neighbor.getZ() - start.getZ());
                if (dx > MAX_RADIUS || dy > MAX_RADIUS || dz > MAX_RADIUS) continue;

                BlockState nbState = level.getBlockState(neighbor);
                if (nbState.isAir() || nbState.is(ModTags.Blocks.NPC_HOUSE_CONSTITUTE)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        int volume = visited.size();
        int xSpan = maxX - minX;
        int zSpan = maxZ - minZ;

        if (volume < MIN_VOLUME || xSpan < 3 || zSpan < 3) return Result.error(ResultType.TOO_SMALL);
        if (volume > MAX_VOLUME) return Result.error(ResultType.TOO_LARGE);
        if (!hasLight) return Result.error(ResultType.NO_LIGHT);
        if (!hasChair) return Result.error(ResultType.NO_CHAIR);
        if (!hasTable) return Result.error(ResultType.NO_TABLE);

        return new Result(ResultType.FOUND,
                new BlockPos(minX, minY, minZ),
                new BlockPos(maxX, maxY, maxZ));
    }

    private static BlockPos[] neighbors(BlockPos pos) {
        return new BlockPos[]{
                pos.above(), pos.below(),
                pos.north(), pos.south(),
                pos.east(), pos.west()
        };
    }
}
