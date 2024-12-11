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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;

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
        super(0.3125f, Properties.of().instabreak().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).randomTicks());
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
        BlockState state = defaultBlockState();
        BlockState blockstate = level.getBlockState(pos.below());
        BlockState blockstate1 = level.getBlockState(pos.above());
        BlockState blockstate2 = level.getBlockState(pos.north());
        BlockState blockstate3 = level.getBlockState(pos.east());
        BlockState blockstate4 = level.getBlockState(pos.south());
        BlockState blockstate5 = level.getBlockState(pos.west());
        return updateDistance(state.trySetValue(DOWN, blockstate.is(this) || blockstate.is(attachable) || blockstate.is(supporting))
                .trySetValue(UP, blockstate1.is(this) || blockstate1.is(attachable))
                .trySetValue(NORTH, blockstate2.is(this) || blockstate2.is(attachable))
                .trySetValue(EAST, blockstate3.is(this) || blockstate3.is(attachable))
                .trySetValue(SOUTH, blockstate4.is(this) || blockstate4.is(attachable))
                .trySetValue(WEST, blockstate5.is(this) || blockstate5.is(attachable)), level, pos);
    }

    private BlockState updateDistance(BlockState state, LevelAccessor level, BlockPos pos) {
        int i = DECAY_DISTANCE;
        int value = state.getValue(DISTANCE);
        for (Direction direction : ModUtils.DIRECTIONS) {
            if (direction == Direction.UP) continue;
            int distanceAt = getDistanceAt(level.getBlockState(pos.relative(direction)));
            if (distanceAt < value) {
                i = Math.min(i, distanceAt + 1);
                if (i == 1) break;
            }
        }
        return state.setValue(DISTANCE, i);
    }

    private int getDistanceAt(BlockState neighbor) {
        if (neighbor.is(attachable) || neighbor.is(supporting)) {
            return 0;
        } else {
            return neighbor.hasProperty(DISTANCE) ? neighbor.getValue(DISTANCE) : DECAY_DISTANCE;
        }
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        BlockState newState = updateDistance(state, level, currentPos);
        if (!newState.canSurvive(level, currentPos)) {
            level.scheduleTick(currentPos, this, 1);
            return super.updateShape(newState, facing, facingState, level, currentPos, facingPos);
        } else {
            boolean flag = facingState.is(this) || facingState.is(attachable) || facing == Direction.DOWN && facingState.is(supporting);
            return newState.setValue(PROPERTY_BY_DIRECTION.get(facing), flag);
        }
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.getValue(DISTANCE) == DECAY_DISTANCE && !state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.is(this) || stateBelow.is(supporting)) {
            return true;
        }
        int value = state.getValue(DISTANCE);
        for (Direction direction : ModUtils.HORIZONTAL) {
            BlockPos posAtSide = pos.relative(direction);
            BlockState stateAtSide = level.getBlockState(posAtSide);
            int distanceAt = getDistanceAt(stateAtSide);
            if (distanceAt < value || stateAtSide.is(supporting)) {
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
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected @NotNull MapCodec<BranchesBlock> codec() {
        return CODEC;
    }
}
