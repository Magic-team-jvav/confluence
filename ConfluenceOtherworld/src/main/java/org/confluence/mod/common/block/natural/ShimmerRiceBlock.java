package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import org.confluence.mod.common.block.common.SimpleShimmerImmersedBlock;
import org.confluence.mod.common.init.ModFluids;
import org.jetbrains.annotations.Nullable;

public class ShimmerRiceBlock extends Block implements SimpleShimmerImmersedBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty SHIMMER_IMMERSED = SimpleShimmerImmersedBlock.SHIMMER_IMMERSED;

    public ShimmerRiceBlock(Properties properties) {
        super(properties.lightLevel(state -> state.getValue(SHIMMER_IMMERSED) ? 10 : 0));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(SHIMMER_IMMERSED, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        boolean isShimmer = fluidState.getType() == ModFluids.SHIMMER.fluid().get();
        return this.defaultBlockState().setValue(SHIMMER_IMMERSED, isShimmer);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP) && state.getValue(SHIMMER_IMMERSED);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        DoubleBlockHalf half = state.getValue(HALF);
        if (half == DoubleBlockHalf.LOWER && !state.getValue(SHIMMER_IMMERSED)) return;
        if (age < 7) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 3);
        } else if (half == DoubleBlockHalf.LOWER && age == 7) {
            BlockPos above = pos.above();
            if (level.isEmptyBlock(above)) {
                level.setBlock(above, this.defaultBlockState()
                        .setValue(HALF, DoubleBlockHalf.UPPER)
                        .setValue(AGE, 0)
                        .setValue(SHIMMER_IMMERSED, false), 3);
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(SHIMMER_IMMERSED)) {
            level.scheduleTick(pos, ModFluids.SHIMMER.fluid().get(), ModFluids.SHIMMER.fluid().get().getTickDelay(level));
        }
        if (!state.canSurvive(level, pos)) {
            return state.getValue(SHIMMER_IMMERSED) ? ModFluids.SHIMMER.fluid().get().getSource(false).createLegacyBlock() : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(SHIMMER_IMMERSED) ?
                ModFluids.SHIMMER.fluid().get().getSource(false) :
                super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, HALF, SHIMMER_IMMERSED);
    }
}
