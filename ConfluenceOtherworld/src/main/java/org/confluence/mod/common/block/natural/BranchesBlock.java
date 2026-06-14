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
        return this.updateAllProperties(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockState newState = this.updateAllProperties(this.defaultBlockState(), level, currentPos);
        if (newState.getValue(DISTANCE) == DECAY_DISTANCE && !newState.canSurvive(level, currentPos)) {
            level.scheduleTick(currentPos, this, 1);
        }
        return newState;
    }

    private BlockState updateAllProperties(BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState result = state;
        result = result.trySetValue(BlockStateProperties.UP, canConnect(level.getBlockState(pos.above())));
        boolean haveDown = canConnect(level.getBlockState(pos.below()));
        result = result.trySetValue(BlockStateProperties.DOWN, haveDown);
        for (Direction direction : LibUtils.HORIZONTAL) {
            BlockPos nextPos = pos.relative(direction);
            if (canConnect(level.getBlockState(nextPos)) && (!haveDown || !canConnect(level.getBlockState(nextPos.below())))) {
                result = result.trySetValue(PROPERTY_BY_DIRECTION.get(direction), true);
            }
        }
        int minDistance = DECAY_DISTANCE;
        for (Direction dir : LibUtils.DIRECTIONS) {
            BlockState neighbor = level.getBlockState(pos.relative(dir));
            int d = getDistanceAt(neighbor, dir);
            if (d < minDistance) minDistance = d;
        }

        return result.setValue(DISTANCE, Math.min(minDistance + 1, DECAY_DISTANCE));
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
        BlockState below = level.getBlockState(pos.below());
        if (below.is(this) || below.is(supporting)) return true;
        int currentDist = state.getValue(DISTANCE);
        if (currentDist < DECAY_DISTANCE) return true;
        for (Direction dir : LibUtils.HORIZONTAL) {
            if (level.getBlockState(pos.relative(dir)).is(attachable)) return true;
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
