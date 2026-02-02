package org.confluence.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Predicate;

public final class ComputerUtils {

    private static final BlockPos[] DIRECTIONS = {
            new BlockPos( 1,  0,  0), new BlockPos(-1,  0,  0),
            new BlockPos( 0,  1,  0), new BlockPos( 0, -1,  0),
            new BlockPos( 0,  0,  1), new BlockPos( 0,  0, -1)
    };


    /**
     * DFS包含方块检测
     * @param center 中心方块坐标
     * @param radius 半径
     * @param contains 包含方块的判定条件
     * @return 如果空间是封闭的，则返回封闭空间；否则返回空列表
     */
    public static List<BlockPos> zoomDetection(Level world, BlockPos center, int radius, Predicate<BlockState> contains) {
        // 定义完整的边界范围
        int minX = center.getX() - radius;
        int maxX = center.getX() + radius;
        int minY = center.getY() - radius;
        int maxY = center.getY() + radius;
        int minZ = center.getZ() - radius;
        int maxZ = center.getZ() + radius;

        // 如果 center 方块不是空气，则直接返回空列表
        if (!world.getBlockState(center).isAir()) {
            return Collections.emptyList();
        }

        Set<BlockPos> visited = new HashSet<>();
        // 记录封闭空间
        List<BlockPos> closedSpace = new ArrayList<>();

        // DFS
        Stack<BlockPos> stack = new Stack<>();
        stack.push(center);
        visited.add(center);

        while (!stack.isEmpty()) {
            BlockPos currentPos = stack.pop();
            closedSpace.add(currentPos);

            // 检查邻居方块
            for (BlockPos dir : DIRECTIONS) {
                BlockPos neighborPos = currentPos.offset(dir);

                // 如果邻居方块在边界内
                if (
                        neighborPos.getX() >= minX && neighborPos.getX() <= maxX &&
                                neighborPos.getY() >= minY && neighborPos.getY() <= maxY &&
                                neighborPos.getZ() >= minZ && neighborPos.getZ() <= maxZ)
                {
                    // 如果邻居方块是空气且未被访问，加入栈
                    if (contains.test(world.getBlockState(neighborPos)) && !visited.contains(neighborPos)) {
                        stack.push(neighborPos);
                        visited.add(neighborPos);
                    }
                } else {
                    // 如果邻居方块在边界外，则当前空间不封闭
                    return Collections.emptyList();
                }
            }
        }

        return closedSpace;
    }


}
