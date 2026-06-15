package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalFourPartBlock;
import org.confluence.mod.common.init.ModFluids;
import org.jetbrains.annotations.Nullable;

public class BlinkingRoyalShimmerlilyBlock extends HorizontalDirectionalWithHorizontalFourPartBlock {
    protected static final VoxelShape A_SHAPE = box(3, -1, 0, 16, 0, 13); // 南
    protected static final VoxelShape B_SHAPE = box(3, -1, 3, 16, 0, 16); // 西
    protected static final VoxelShape C_SHAPE = box(0, -1, 3, 13, 0, 16); // 北
    protected static final VoxelShape D_SHAPE = box(0, -1, 0, 13, 0, 13); // 东
    protected static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{A_SHAPE, B_SHAPE, C_SHAPE, D_SHAPE};
    protected static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{D_SHAPE, A_SHAPE, B_SHAPE, C_SHAPE};
    protected static final VoxelShape[] CORNER_SHAPES = new VoxelShape[]{C_SHAPE, D_SHAPE, A_SHAPE, B_SHAPE};
    protected static final VoxelShape[] FRONT_SHAPES = new VoxelShape[]{B_SHAPE, C_SHAPE, D_SHAPE, A_SHAPE};

    public BlinkingRoyalShimmerlilyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue();
        return switch (state.getValue(PART)) {
            case BASE -> BASE_SHAPES[index];
            case RIGHT -> RIGHT_SHAPES[index];
            case FRONT -> FRONT_SHAPES[index];
            case CORNER -> CORNER_SHAPES[index];
        };
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getFluidState().getType().getFluidType() == ModFluids.SHIMMER.type().get();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide() && direction == Direction.DOWN) {
            level.scheduleTick(pos, this, 2);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }
}
