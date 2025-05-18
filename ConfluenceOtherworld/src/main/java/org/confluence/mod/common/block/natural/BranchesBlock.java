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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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

    private final TagKey<Block> attachable;
    private final TagKey<Block> supporting;

    public BranchesBlock(TagKey<Block> attachable, TagKey<Block> supporting) {
        super(0.1875F, Properties.of().instabreak().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).randomTicks());
        registerDefaultState(stateDefinition.any()
                .setValue(DISTANCE, DECAY_DISTANCE)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
        this.attachable = attachable;
        this.supporting = supporting;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState dest = defaultBlockState();
        BlockState belowState = level.getBlockState(pos.below());
        boolean belowIsThis = belowState.is(this);
        for (Direction direction : LibUtils.DIRECTIONS) {
            BlockPos relative = pos.relative(direction);
            BlockState relativeState = level.getBlockState(relative);
            boolean shouldConnect = relativeState.is(this) || relativeState.is(attachable) || (direction == Direction.DOWN && relativeState.is(supporting));
            BlockState relativeBelowState = level.getBlockState(relative.below());
            shouldConnect &= !relativeBelowState.is(this) || !relativeBelowState.getValue(PROPERTY_BY_DIRECTION.get(direction.getOpposite()));
            BooleanProperty dirProp = PROPERTY_BY_DIRECTION.get(direction);
            if (belowIsThis) shouldConnect &= belowState.getValue(dirProp);
            dest = dest.trySetValue(dirProp, shouldConnect);
        }
        return updateDistance(dest, level, pos);
    }

    private BlockState updateDistance(BlockState state, LevelAccessor level, BlockPos pos) {
        int i = DECAY_DISTANCE;
        int value = state.getValue(DISTANCE);
        for (Direction direction : LibUtils.DIRECTIONS) {
            if (direction == Direction.UP) continue;
            int distanceAt = getDistanceAt(level.getBlockState(pos.relative(direction)), direction);
            if (distanceAt < value) {
                i = Math.min(i, distanceAt + 1);
                if (i == 1) break;
            }
        }
        return state.setValue(DISTANCE, i);
    }

    private int getDistanceAt(BlockState neighbor, Direction direction) {
        if (neighbor.is(attachable) || (direction == Direction.DOWN && neighbor.is(supporting))) {
            return 0;
        } else {
            return neighbor.hasProperty(DISTANCE) ? neighbor.getValue(DISTANCE) : DECAY_DISTANCE;
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockState newState = updateDistance(state, level, currentPos);
        if (!newState.canSurvive(level, currentPos)) {
            level.scheduleTick(currentPos, this, 1);
            return super.updateShape(newState, facing, facingState, level, currentPos, facingPos);
        } else if (facing.getAxis().isVertical() || !level.getBlockState(facingPos.below()).is(this)) {
            boolean flag = facingState.is(this) || facingState.is(attachable) || (facing == Direction.DOWN && facingState.is(supporting));
            return newState.setValue(PROPERTY_BY_DIRECTION.get(facing), flag);
        }
        return newState;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(DISTANCE) == DECAY_DISTANCE && !state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.is(this) || stateBelow.is(supporting)) {
            return true;
        }
        int value = state.getValue(DISTANCE);
        if (value == 1) return true;
        for (Direction direction : LibUtils.HORIZONTAL) {
            BlockState stateAtSide = level.getBlockState(pos.relative(direction));
            int distanceAt = getDistanceAt(stateAtSide, direction);
            if (distanceAt < value || stateAtSide.is(attachable)) {
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
    protected MapCodec<BranchesBlock> codec() {
        return CODEC;
    }
}
