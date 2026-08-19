package org.confluence.mod.common.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/// 为非实体地面召唤物计算短距离方块路径。
///
/// <p>寻路只处理当前召唤物需要的轻量场景：水平移动、一格上台阶和一格下落。</p>
final class GroundPathfinder {
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private GroundPathfinder() {
    }

    static List<Vec3> find(ServerLevel level, Vec3 startPosition, Vec3 destination, double width, double height) {
        BlockPos start = BlockPos.containing(startPosition);
        BlockPos goal = BlockPos.containing(destination);
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::score));
        Map<BlockPos, Double> costs = new HashMap<>();
        Map<BlockPos, BlockPos> parents = new HashMap<>();
        Set<BlockPos> closed = new HashSet<>();
        costs.put(start, 0.0);
        open.add(new Node(start, heuristic(start, goal)));
        BlockPos nearest = start;
        double nearestDistance = heuristic(start, goal);
        int visited = 0;
        while (!open.isEmpty() && visited++ < 512) {
            BlockPos current = open.poll().position();
            if (!closed.add(current)) {
                continue;
            }
            double currentDistance = heuristic(current, goal);
            if (currentDistance < nearestDistance) {
                nearest = current;
                nearestDistance = currentDistance;
            }
            if (current.distManhattan(goal) <= 1) {
                nearest = current;
                break;
            }
            for (int[] direction : DIRECTIONS) {
                BlockPos neighbor = findWalkableNeighbor(level, current, direction[0], direction[1], width, height);
                if (neighbor == null || closed.contains(neighbor) || neighbor.distManhattan(start) > 48) {
                    continue;
                }
                double cost = costs.get(current) + 1.0 + Math.abs(neighbor.getY() - current.getY()) * 0.5;
                if (cost >= costs.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    continue;
                }
                costs.put(neighbor, cost);
                parents.put(neighbor, current);
                open.add(new Node(neighbor, cost + heuristic(neighbor, goal)));
            }
        }
        List<Vec3> reversed = new ArrayList<>();
        for (BlockPos node = nearest; !node.equals(start); node = parents.get(node)) {
            if (node == null) {
                return List.of();
            }
            reversed.add(Vec3.atBottomCenterOf(node));
        }
        Collections.reverse(reversed);
        return reversed;
    }

    private static BlockPos findWalkableNeighbor(ServerLevel level, BlockPos current, int offsetX, int offsetZ, double width, double height) {
        for (int offsetY : new int[]{1, 0, -1}) {
            BlockPos candidate = current.offset(offsetX, offsetY, offsetZ);
            if (walkable(level, candidate, width, height)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean walkable(ServerLevel level, BlockPos position, double width, double height) {
        Vec3 bottomCenter = Vec3.atBottomCenterOf(position);
        AABB body = AABB.ofSize(bottomCenter.add(0.0, height * 0.5, 0.0), width, height, width).deflate(1.0E-4);
        AABB support = new AABB(body.minX, body.minY - 0.08, body.minZ, body.maxX, body.minY, body.maxZ);
        return !level.getBlockCollisions(null, body).iterator().hasNext()
                && level.getBlockCollisions(null, support).iterator().hasNext();
    }

    private static double heuristic(BlockPos from, BlockPos to) {
        return Math.abs(from.getX() - to.getX()) + Math.abs(from.getZ() - to.getZ())
                + Math.abs(from.getY() - to.getY()) * 1.5;
    }

    private record Node(BlockPos position, double score) {
    }
}
