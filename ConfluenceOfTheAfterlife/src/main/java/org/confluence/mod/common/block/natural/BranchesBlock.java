package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class BranchesBlock extends PipeBlock {
    public static final MapCodec<BranchesBlock> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("ground").forGetter(branchesBlock -> branchesBlock.ground)).
            apply(builder, BranchesBlock::new)
    );
    private final Block ground;

    public BranchesBlock(Block GroundBlocks) {
        super(0.3125f, Properties.of().instabreak().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).randomTicks());
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(EAST, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(UP, false)
            .setValue(DOWN, false));
        this.ground = GroundBlocks;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockGetter level = context.getLevel();
        Direction placedOn = context.getClickedFace().getOpposite();
        BlockState state = this.defaultBlockState().setValue(PROPERTY_BY_DIRECTION.get(placedOn), true);
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.relative(direction);
            BlockState adjacentState = level.getBlockState(adjacentPos);
            if (adjacentState.is(this)) {
                boolean adjacentDown = adjacentState.getValue(DOWN);
                if (direction == Direction.UP || direction == Direction.DOWN || !state.getValue(DOWN) || !adjacentDown) {
                    state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
                }
            } else if (adjacentState.is(ground) && (direction == Direction.UP || direction == Direction.DOWN)) {
                state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
            }
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (!state.canSurvive(level, currentPos)) {
            level.scheduleTick(currentPos, this, 1);
            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        } else {
            boolean isConnected = facingState.is(this) || facingState.is(ground);
            if (facing == Direction.UP || facing == Direction.DOWN || !state.getValue(DOWN) || (facingState.is(this) && !facingState.getValue(DOWN))) {
                state = state.setValue(PROPERTY_BY_DIRECTION.get(facing), isConnected);
            } else {
                state = state.setValue(PROPERTY_BY_DIRECTION.get(facing), false);
            }
            return state;
        }
    }

    @Override
    public void tick(BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
            dropResources(state, level, pos);
        }
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState stateBelow = pLevel.getBlockState(pPos.below());
        if (stateBelow.is(this) || stateBelow.is(ground)) {
            return true;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos posAtSide = pPos.relative(direction);
            BlockState stateAtSide = pLevel.getBlockState(posAtSide);
            if (stateAtSide.is(this) || stateAtSide.is(ground)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    protected MapCodec<BranchesBlock> codec() {
        return CODEC;
    }
}
