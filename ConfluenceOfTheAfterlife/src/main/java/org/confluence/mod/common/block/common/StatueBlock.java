package org.confluence.mod.common.block.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.block.StateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <a href="https://terraria.wiki.gg/zh/wiki/%E9%9B%95%E5%83%8F">雕像Wiki页面</a>
 */
public class StatueBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<StatueBlock> CODEC = simpleCodec(StatueBlock::new);
    private static final VoxelShape LOWER_SHAPE_Z = Shapes.or(
            box(0, 0, 0, 16, 3, 16),
            box(1, 3, 1, 15, 7, 15),
            box(0, 7, 0, 16, 10, 16),
            box(1, 10, 6, 15, 16, 10)
    );
    private static final VoxelShape LOWER_SHAPE_X = Shapes.or(
            box(0, 0, 0, 16, 3, 16),
            box(1, 3, 1, 15, 7, 15),
            box(0, 7, 0, 16, 10, 16),
            box(6, 10, 1, 10, 16, 15)
    );
    private static final VoxelShape UPPER_SHAPE_Z = box(1, 0, 6, 15, 14, 10);
    private static final VoxelShape UPPER_SHAPE_X = box(6, 0, 1, 10, 14, 15);

    public StatueBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(StateProperties.VERTICAL_TWO_PART, StateProperties.VerticalTwoPart.BASE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, StateProperties.VERTICAL_TWO_PART);
    }

    @Override
    protected @NotNull MapCodec<StatueBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        boolean base = pState.getValue(StateProperties.VERTICAL_TWO_PART).isBase();
        return switch (pState.getValue(FACING)) {
            case NORTH, SOUTH -> base ? LOWER_SHAPE_Z : UPPER_SHAPE_Z;
            default -> base ? LOWER_SHAPE_X : UPPER_SHAPE_X;
        };
    }

    @Override
    public void setPlacedBy(Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, @NotNull ItemStack pStack) {
        if (!pLevel.isClientSide) {
            BlockPos relativePos = pPos.relative(StateProperties.VerticalTwoPart.getConnectedDirection(pState));
            pLevel.setBlockAndUpdate(relativePos, defaultBlockState().setValue(StateProperties.VERTICAL_TWO_PART, StateProperties.VerticalTwoPart.UP).setValue(FACING, pState.getValue(FACING)));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockState blockState = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        BlockPos relativePos = pContext.getClickedPos().relative(StateProperties.VerticalTwoPart.getConnectedDirection(blockState));
        return level.getBlockState(relativePos).canBeReplaced(pContext) ? blockState : null;
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        pLevel.setBlockAndUpdate(pPos.relative(StateProperties.VerticalTwoPart.getConnectedDirection(pState)), Blocks.AIR.defaultBlockState());
    }
}
