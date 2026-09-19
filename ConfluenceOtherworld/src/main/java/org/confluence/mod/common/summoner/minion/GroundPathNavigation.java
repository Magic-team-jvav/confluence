package org.confluence.mod.common.summoner.minion;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * 地面寻路，仿照原版 {@link net.minecraft.world.entity.ai.navigation.GroundPathNavigation}：
 * 节点串的检索、推进与重新检索都由本类负责，地面召唤物只需要设置目标并调用 tick。
 * <p>
 * 召唤物绝对无敌，因此不做火焰、岩浆、仙人掌与悬崖的危险规避，通行性只取决于方块碰撞形状。
 * </p>
 */
public class GroundPathNavigation {

    /** 单次检索允许展开的节点上限 */
    private static final int DEFAULT_MAX_PATH_LENGTH = 256;
    /** 相邻两次检索的最小间隔（tick） */
    private static final int RECOMPUTE_INTERVAL = 10;
    /** 判定抵达节点的距离 */
    private static final double ARRIVE_DISTANCE = 0.4;
    /** 目标偏离路线终点超过该距离才重新检索 */
    private static final double REPATH_THRESHOLD_SQR = 4.0;
    /** 目标偏离上次检索位置超过该距离才重新检索 */
    private static final double TARGET_MOVE_THRESHOLD_SQR = 4.0;
    /** 召唤物偏离上次检索位置超过该距离才重新检索 */
    private static final double SELF_MOVE_THRESHOLD_SQR = 1.0;
    /** 单步允许的高度差 */
    private static final int MAX_STEP = 1;
    /** 需要起跳的高度差 */
    private static final double STEP_UP_HEIGHT = 0.5;
    /** 距离上一格高的节点多近时起跳 */
    private static final double JUMP_TRIGGER_DISTANCE_SQR = 0.81;
    /** 落脚点的垂直搜索范围 */
    private static final int MAX_SEARCH = 3;
    /** 水平方向候选，前四个为直行，后四个为对角 */
    private static final BlockPos[] DIRECTIONS = {
            new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(-1, 0, -1)
    };

    protected final GroundMinion holder;
    protected List<PathNode> path = List.of();
    protected int index;
    protected int recomputeTimer;
    protected double speedModifier = 0.05;
    protected int maxPathLength = DEFAULT_MAX_PATH_LENGTH;
    protected Vec3 targetPos;
    protected Vec3 recomputeOrigin = Vec3.ZERO;
    protected @Nullable LivingEntity targetEntity;

    public GroundPathNavigation(GroundMinion holder) {
        this.holder = holder;
    }

    /**
     * 前往固定位置：已经有一条通往附近的路线时继续沿用，避免频繁换路导致抽搐。
     */
    public boolean moveTo(Vec3 pos, double speed) {
        return moveTo(pos, speed, null);
    }

    /**
     * 追踪目标实体：已经有一条通往附近的路线时继续沿用，重新检索时才读取它的最新位置。
     */
    public boolean moveTo(LivingEntity target, double speed) {
        return moveTo(target.position(), speed, target);
    }

    private boolean moveTo(Vec3 pos, double speed, @Nullable LivingEntity target) {
        speedModifier = speed;
        targetEntity = target;
        boolean result = true;
        if (targetPos == null || !isStablePath(pos)) {
            targetPos = pos;
            result = recomputePath();
        }
        return result;
    }

    /**
     * 当前路线是否还能沿用：目标相对上次检索几乎没动，或者路线还在推进且终点离新目标足够近。
     */
    protected boolean isStablePath(Vec3 pos) {
        return targetPos != null && (pos.distanceToSqr(targetPos) <= REPATH_THRESHOLD_SQR
                || (isInProgress() && pathEnd().distanceToSqr(pos) <= REPATH_THRESHOLD_SQR));
    }

    /**
     * 路线终点，路线为空时退回召唤物当前位置。
     */
    protected Vec3 pathEnd() {
        return path.isEmpty() ? holder.getPos() : path.get(path.size() - 1).pos();
    }

    /**
     * 用记录的目标重新检索节点串并保存，目标不可达时以最近的落脚点结尾。
     */
    public boolean recomputePath() {
        recomputeTimer = RECOMPUTE_INTERVAL;
        recomputeOrigin = holder.getPos();
        targetPos = targetEntity == null ? targetPos : targetEntity.position();
        path = targetPos == null ? List.of() : createPath(holder.getPos(), targetPos);
        index = 0;
        return !path.isEmpty();
    }

    /**
     * 推进节点串：节点串走完就按间隔重新检索，否则朝当前节点添加动量完成线性移动。
     */
    public void tick() {
        if (canUpdatePath()) {
            if (isDone()) {
                if (shouldRecomputePath()) {
                    recomputePath();
                }
            } else {
                followPath();
            }
        } else {
            stop();
        }
    }

    /**
     * 朝当前节点移动：抵达后切换到下一个节点，否则添加动量并转向移动方向。
     */
    protected void followPath() {
        PathNode node = getCurrentNode();
        assert node != null;
        Vec3 offset = node.pos().subtract(holder.getPos());
        if (offset.lengthSqr() < ARRIVE_DISTANCE * ARRIVE_DISTANCE || passedNode(offset)) {
            advance();
        } else {
            if (shouldJump(offset)) {
                holder.jump(offset);
            }
            holder.addVelocity(offset.normalize().scale(speedModifier));
            holder.setDesiredRotation(PathNode.yawFromDirection(offset), 0, holder.getRoll());
        }
    }

    /**
     * 当前节点比脚下一格还高、且已经走到起跳距离内时准备起跳，用于登上一格高的方块。
     */
    protected boolean shouldJump(Vec3 offset) {
        return offset.y > 1 && offset.horizontalDistanceSqr() < 4;
    }

    /**
     * 召唤物已经越过节点时也算抵达，避免掉头修正造成来回摆动。
     */
    protected boolean passedNode(Vec3 offset) {
        Vec3 velocity = holder.getVelocity();
        return velocity.lengthSqr() > 1.0E-4 && velocity.dot(offset) < 0.0 && offset.lengthSqr() < 1.0;
    }

    /**
     * 节点串走完后判断是否重新检索：目标或召唤物相对上次检索移动明显时才继续。
     */
    protected boolean shouldRecomputePath() {
        boolean result = false;
        if (targetPos != null) {
            if (recomputeTimer > 0) {
                recomputeTimer--;
            } else {
                Vec3 currentTarget = targetEntity == null ? targetPos : targetEntity.position();
                result = currentTarget.distanceToSqr(targetPos) > TARGET_MOVE_THRESHOLD_SQR || holder.getPos().distanceToSqr(recomputeOrigin) > SELF_MOVE_THRESHOLD_SQR;
            }
        }
        return result;
    }

    /**
     * 检索起点到离目标最近落脚点的节点串，节点上限由 {@link #getMaxPathLength()} 决定。
     */
    protected List<PathNode> createPath(Vec3 start, Vec3 target) {
        BlockPos from = standable(BlockPos.containing(start));
        BlockPos to = standable(BlockPos.containing(target));
        Map<BlockPos, Double> costs = new HashMap<>();
        Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
        Set<BlockPos> visited = new HashSet<>();
        PriorityQueue<Step> open = new PriorityQueue<>(Comparator.comparingDouble(Step::score)
                .thenComparingInt(step -> step.pos().getX())
                .thenComparingInt(step -> step.pos().getY())
                .thenComparingInt(step -> step.pos().getZ()));
        costs.put(from, 0.0);
        open.add(new Step(from, distance(from, to)));
        BlockPos closest = from;
        boolean reached = false;
        while (!reached && !open.isEmpty() && visited.size() < getMaxPathLength()) {
            Step current = open.poll();
            if (visited.add(current.pos())) {
                if (distance(current.pos(), to) < distance(closest, to)) {
                    closest = current.pos();
                }
                reached = current.pos().equals(to);
                if (!reached) {
                    search(current.pos(), to, costs, cameFrom, open);
                }
            }
        }
        return trace(cameFrom, closest);
    }

    /**
     * 检索当前节点的全部可行走邻居，代价更低的邻居写入回溯表并放进候选队列。
     */
    private void search(BlockPos current, BlockPos target, Map<BlockPos, Double> costs, Map<BlockPos, BlockPos> cameFrom, PriorityQueue<Step> open) {
        for (BlockPos direction : DIRECTIONS) {
            BlockPos next = neighbour(current, direction);
            if (next != null && canCutCorner(current, next)) {
                double cost = costs.get(current) + Math.sqrt(current.distSqr(next));
                if (cost < costs.getOrDefault(next, Double.MAX_VALUE)) {
                    costs.put(next, cost);
                    cameFrom.put(next, current);
                    open.add(new Step(next, cost + distance(next, target)));
                }
            }
        }
    }

    /**
     * 在给定方向上寻找高度变化最小且可站立的落脚点，找不到时返回 null。
     */
    protected @Nullable BlockPos neighbour(BlockPos current, BlockPos direction) {
        BlockPos result = null;
        int best = MAX_STEP + 1;
        for (int dy = MAX_STEP; dy >= -MAX_STEP; dy--) {
            BlockPos candidate = current.offset(direction.getX(), dy, direction.getZ());
            if (isStableDestination(candidate) && Math.abs(dy) < best) {
                result = candidate;
                best = Math.abs(dy);
            }
        }
        return result;
    }

    /**
     * 对角移动时要求夹住这条边的两个方块可穿过，避免贴着墙角走。
     */
    protected boolean canCutCorner(BlockPos current, BlockPos next) {
        boolean result = true;
        if (next.getX() != current.getX() && next.getZ() != current.getZ()) {
            result = isPassable(level(), new BlockPos(next.getX(), next.getY(), current.getZ())) && isPassable(level(), new BlockPos(current.getX(), next.getY(), next.getZ()));
        }
        return result;
    }

    /**
     * 从离目标最近的落脚点回溯出整条节点串，起点自身已经走过所以不放进路线。
     */
    protected List<PathNode> trace(Map<BlockPos, BlockPos> cameFrom, BlockPos closest) {
        List<PathNode> result = new ArrayList<>();
        BlockPos current = closest;
        while (current != null) {
            result.add(new PathNode(Vec3.atBottomCenterOf(current), 0, 0, 0));
            current = cameFrom.get(current);
        }
        Collections.reverse(result);
        if (result.size() > 1) {
            result.remove(0);
        }
        return result;
    }

    /**
     * 把任意位置修正为附近的落脚点，供起点与目标点使用。
     */
    protected BlockPos standable(BlockPos pos) {
        BlockPos result = pos;
        if (!isStableDestination(pos)) {
            boolean found = false;
            for (int dy = 1; dy <= MAX_SEARCH && !found; dy++) {
                if (isStableDestination(pos.below(dy))) {
                    result = pos.below(dy);
                    found = true;
                } else if (isStableDestination(pos.above(dy))) {
                    result = pos.above(dy);
                    found = true;
                }
            }
        }
        return result;
    }

    /**
     * 在给定范围内寻找离 origin 最近的可站立位置，找不到时返回 origin 的落脚点修正结果。
     */
    public BlockPos nearestStableDestination(BlockPos origin, int range) {
        BlockPos result = standable(origin);
        boolean found = isStableDestination(result);
        for (int radius = 1; radius <= range && !found; radius++) {
            for (int dx = -radius; dx <= radius && !found; dx++) {
                for (int dz = -radius; dz <= radius && !found; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) == radius) {
                        BlockPos candidate = standable(origin.offset(dx, 0, dz));
                        if (isStableDestination(candidate)) {
                            result = candidate;
                            found = true;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 是否允许继续推进路径，召唤物绝对无敌，因此只要求它还存活。
     */
    protected boolean canUpdatePath() {
        return holder.isAlive();
    }

    /**
     * 该格是否为稳定落脚点：从脚部到召唤物身高所占的格子都可穿过，下方有支撑。
     */
    public boolean isStableDestination(BlockPos pos) {
        Level level = level();
        int height = Mth.ceil(holder.getBlockCollisionBox().getYsize());
        boolean passable = isPassable(level, pos);
        for (int dy = 1; dy < height && passable; dy++) {
            passable = isPassable(level, pos.above(dy));
        }
        return passable && isSolid(level, pos.below());
    }

    /**
     * 方块是否可以作为支撑面。
     */
    private static boolean isSolid(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && !state.getCollisionShape(level, pos).isEmpty();
    }

    /**
     * 方块是否可以让实体穿过，召唤物无敌所以液体一律视作可穿过。
     */
    private static boolean isPassable(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.getCollisionShape(level, pos).isEmpty();
    }

    private static double distance(BlockPos from, BlockPos to) {
        return Math.sqrt(from.distSqr(to));
    }

    private Level level() {
        return holder.getLevel();
    }

    /**
     * 节点串是否已经消费完。
     */
    public boolean isDone() {
        return index >= path.size();
    }

    /**
     * 节点串是否还在推进。
     */
    public boolean isInProgress() {
        return !isDone();
    }

    /**
     * 获取当前消费到的节点，消费完时返回 null。
     */
    public @Nullable PathNode getCurrentNode() {
        return isDone() ? null : path.get(index);
    }

    /**
     * 前进到下一个节点。
     */
    public void advance() {
        index++;
    }

    /**
     * 停止移动并清除当前目标。
     */
    public void stop() {
        path = List.of();
        index = 0;
        targetPos = null;
        targetEntity = null;
    }

    public List<PathNode> getPath() {
        return path;
    }

    public double getSpeedModifier() {
        return speedModifier;
    }

    public void setSpeedModifier(double speedModifier) {
        this.speedModifier = speedModifier;
    }

    public int getMaxPathLength() {
        return maxPathLength;
    }

    public void setMaxPathLength(int maxPathLength) {
        this.maxPathLength = maxPathLength;
    }

    private record Step(BlockPos pos, double score) {
    }
}
