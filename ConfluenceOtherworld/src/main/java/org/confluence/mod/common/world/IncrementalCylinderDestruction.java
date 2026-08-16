package org.confluence.mod.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.Queue;

/// 将大型圆柱清场拆分到多个服务端 tick 执行。
///
/// <p>任务按半径逐圈扩张，每次只处理固定数量的方块。底层铺设下界岩，其余高度
/// 清为空气；带有 {@link BlockTags#FEATURES_CANNOT_REPLACE} 标签的结构核心不会被
/// 改写。该类不保存世界引用之外的实体状态，调用方只需持久化当前半径。</p>
public final class IncrementalCylinderDestruction {
    private static final int BLOCK_BUDGET_PER_TICK = 4096;

    private final Level level;
    private final int centerX;
    private final int centerZ;
    private final int minY;
    private final int maxY;
    private final int maximumRadius;
    private final Queue<Column> pendingColumns = new ArrayDeque<>();

    private int currentRadius;
    private Column currentColumn;
    private int currentY;

    public IncrementalCylinderDestruction(
            Level level,
            int centerX,
            int centerZ,
            int minY,
            int maxY,
            int startRadius,
            int maximumRadius) {
        if (minY > maxY) {
            throw new IllegalArgumentException(
                    "Cylinder minimum Y must not exceed maximum Y");
        }
        if (startRadius < 0 || maximumRadius < startRadius) {
            throw new IllegalArgumentException(
                    "Cylinder radius range is invalid");
        }
        this.level = level;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.minY = minY;
        this.maxY = maxY;
        this.currentRadius = startRadius;
        this.maximumRadius = maximumRadius;
    }

    /// 推进一次清场任务。
    ///
    /// @return 已完成最大半径时为 {@code true}
    public boolean tick() {
        int budget = BLOCK_BUDGET_PER_TICK;
        while (budget-- > 0) {
            if (currentColumn == null && !prepareNextColumn()) {
                return true;
            }
            processCurrentBlock();
        }
        return false;
    }

    public int getCurrentRadius() {
        return currentRadius;
    }

    private boolean prepareNextColumn() {
        while (pendingColumns.isEmpty()) {
            if (currentRadius >= maximumRadius) {
                return false;
            }
            currentRadius++;
            enqueueShell(currentRadius);
        }
        currentColumn = pendingColumns.remove();
        currentY = minY;
        return true;
    }

    private void enqueueShell(int radius) {
        int innerSquared = (radius - 1) * (radius - 1);
        int outerSquared = radius * radius;
        for (int offsetX = -radius; offsetX <= radius; offsetX++) {
            for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                int distanceSquared =
                        offsetX * offsetX + offsetZ * offsetZ;
                if (distanceSquared > innerSquared
                        && distanceSquared <= outerSquared) {
                    pendingColumns.add(new Column(
                            centerX + offsetX, centerZ + offsetZ));
                }
            }
        }
    }

    private void processCurrentBlock() {
        BlockPos pos = new BlockPos(
                currentColumn.x(), currentY, currentColumn.z());
        BlockState state = level.getBlockState(pos);
        if (!state.is(BlockTags.FEATURES_CANNOT_REPLACE)) {
            if (currentY == minY) {
                if (!state.is(Blocks.NETHERRACK)) {
                    level.setBlock(
                            pos, Blocks.NETHERRACK.defaultBlockState(), 3);
                }
            } else if (!state.isAir()) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        currentY++;
        if (currentY > maxY) {
            currentColumn = null;
        }
    }

    private record Column(int x, int z) {}
}
