package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.confluence.mod.common.block.common.ModBlockStateProperties;
import org.confluence.mod.common.block.common.SimpleShimmerImmersedBlock;
import org.confluence.mod.common.init.ModFluids;

public class ShimmerCoralTubeBlock extends PipeBlock implements SimpleShimmerImmersedBlock {
    public static final MapCodec<ShimmerCoralTubeBlock> CODEC = simpleCodec(ShimmerCoralTubeBlock::new);
    public static final BooleanProperty SHIMMER_IMMERSED = ModBlockStateProperties.SHIMMER_IMMERSED;

    public ShimmerCoralTubeBlock(Properties properties) {
        super(0.1875F, properties.lightLevel(state -> state.getValue(SHIMMER_IMMERSED) ? 10 : 0));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(SHIMMER_IMMERSED, false));
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return CODEC;
    }

    private boolean shouldConnectTo(BlockState state, Direction face, BlockGetter level, BlockPos pos) {
        return state.is(this) || state.isFaceSturdy(level, pos, face.getOpposite());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = level.getFluidState(pos);
        boolean isShimmer = fluidState.getType() == ModFluids.SHIMMER.fluid().get();
        BlockState state = this.defaultBlockState().setValue(SHIMMER_IMMERSED, isShimmer);
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction),
                    this.shouldConnectTo(neighborState, direction, level, neighborPos));
        }

        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(SHIMMER_IMMERSED)) {
            level.scheduleTick(currentPos, ModFluids.SHIMMER.fluid().get(), ModFluids.SHIMMER.fluid().get().getTickDelay(level));
        }
        return state.setValue(PROPERTY_BY_DIRECTION.get(facing), this.shouldConnectTo(facingState, facing, level, facingPos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, SHIMMER_IMMERSED);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(SHIMMER_IMMERSED) ? ModFluids.SHIMMER.fluid().get().getSource(false) : super.getFluidState(state);
    }
}
