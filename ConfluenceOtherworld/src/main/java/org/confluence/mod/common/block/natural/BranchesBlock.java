package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.confluence.lib.util.LibUtils;

public class BranchesBlock extends PipeBlock {
    public static final MapCodec<BranchesBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.codec(Registries.BLOCK).fieldOf("attachable").forGetter(block -> block.attachable),
            TagKey.codec(Registries.BLOCK).fieldOf("supporting").forGetter(block -> block.supporting)
    ).apply(instance, BranchesBlock::new));

    public static final int DECAY_DISTANCE = 12;
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 1, DECAY_DISTANCE);

    protected final TagKey<Block> attachable;
    protected final TagKey<Block> supporting;

    public BranchesBlock(TagKey<Block> attachable, TagKey<Block> supporting) {
        super(0.1875F, Properties.of()
                .instabreak()
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion());
        this.attachable = attachable;
        this.supporting = supporting;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DISTANCE, DECAY_DISTANCE)
                .setValue(NORTH, false).setValue(EAST, false)
                .setValue(SOUTH, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateAllProperties(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockState newState = updateAllProperties(this.defaultBlockState(), level, currentPos);
        if (newState.getValue(DISTANCE) == DECAY_DISTANCE && !newState.canSurvive(level, currentPos)) {
            level.scheduleTick(currentPos, this, 1);
        }
        return newState;
    }

    private BlockState updateAllProperties(BlockState state, LevelAccessor level, BlockPos pos) {
        // 缓存垂直方向状态，避免重复查询
        BlockState aboveState = level.getBlockState(pos.above());
        BlockState belowState = level.getBlockState(pos.below());

        boolean haveDown = canConnect(belowState);
        BlockState result = state
                .trySetValue(BlockStateProperties.UP, canConnect(aboveState))
                .trySetValue(BlockStateProperties.DOWN, haveDown);

        // 处理水平方向连接
        for (Direction direction : LibUtils.HORIZONTAL) {
            BlockPos nextPos = pos.relative(direction);
            BlockState nextState = level.getBlockState(nextPos);
            if (canConnect(nextState)) {
                // 避免在条件不满足时查询 nextPos.below()
                if (!haveDown || !canConnect(level.getBlockState(nextPos.below()))) {
                    result = result.trySetValue(PROPERTY_BY_DIRECTION.get(direction), true);
                }
            }
        }

        // 计算最小距离
        int minDistance = calculateMinDistance(level, pos);
        return result.setValue(DISTANCE, Math.min(minDistance + 1, DECAY_DISTANCE));
    }

    /**
     * 计算周围方块的最小距离值
     */
    private int calculateMinDistance(LevelAccessor level, BlockPos pos) {
        int minDistance = DECAY_DISTANCE;
        for (Direction dir : LibUtils.DIRECTIONS) {
            BlockState neighbor = level.getBlockState(pos.relative(dir));
            int distance = getDistanceAt(neighbor, dir);
            if (distance < minDistance) {
                minDistance = distance;
                // 提前退出：距离为0是最小值，无需继续
                if (minDistance == 0) break;
            }
        }
        return minDistance;
    }

    private boolean canConnect(BlockState blockState) {
        return blockState.is(this) || blockState.is(attachable);
    }

    private int getDistanceAt(BlockState neighbor, Direction direction) {
        if (neighbor.is(attachable) || (direction == Direction.DOWN && neighbor.is(supporting))) {
            return 0;
        }
        return neighbor.hasProperty(DISTANCE) ? neighbor.getValue(DISTANCE) : DECAY_DISTANCE;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(DISTANCE) == DECAY_DISTANCE && !state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // 优先检查距离属性
        if (state.getValue(DISTANCE) < DECAY_DISTANCE) {
            return true;
        }

        // 检查下方支撑
        BlockState below = level.getBlockState(pos.below());
        if (below.is(this) || below.is(supporting)) {
            return true;
        }

        // 检查水平方向附着点
        for (Direction dir : LibUtils.HORIZONTAL) {
            if (level.getBlockState(pos.relative(dir)).is(attachable)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return CODEC;
    }
}
