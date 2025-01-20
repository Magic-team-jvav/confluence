package org.confluence.mod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class HorizontalDirectionalWithVerticalFourPartBlock extends HorizontalDirectionalBlock {
    public HorizontalDirectionalWithVerticalFourPartBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(StateProperties.VERTICAL_FOUR_PART, StateProperties.VerticalFourPart.BASE).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.VERTICAL_FOUR_PART, FACING);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (!pLevel.isClientSide) {
            BlockPos relativePos = pPos.relative(StateProperties.VerticalFourPart.getConnectedDirection(pState));
            pLevel.setBlockAndUpdate(relativePos, pState.setValue(StateProperties.VERTICAL_FOUR_PART, StateProperties.VerticalFourPart.RIGHT));
            pLevel.setBlockAndUpdate(pPos.above(), pState.setValue(StateProperties.VERTICAL_FOUR_PART, StateProperties.VerticalFourPart.UP));
            pLevel.setBlockAndUpdate(relativePos.above(), pState.setValue(StateProperties.VERTICAL_FOUR_PART, StateProperties.VerticalFourPart.RIGHT_UP));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockState blockState = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        BlockPos clickedPos = pContext.getClickedPos();
        BlockPos relativeUpPos = clickedPos.relative(StateProperties.VerticalFourPart.getConnectedDirection(blockState)).above();
        for (BlockPos blockPos : BlockPos.betweenClosed(clickedPos, relativeUpPos)) {
            if (!level.getBlockState(blockPos).canBeReplaced(pContext) || !level.getWorldBorder().isWithinBounds(blockPos)) {
                return null;
            }
        }
        return blockState;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        BlockState air = Blocks.AIR.defaultBlockState();
        for (BlockPos relative : StateProperties.VerticalFourPart.getRelatives(pState.getValue(StateProperties.VERTICAL_FOUR_PART), pState.getValue(FACING), pPos).values()) {
            pLevel.setBlockAndUpdate(relative, air);
        }
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
