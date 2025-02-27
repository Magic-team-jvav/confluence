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

import java.util.Map;

public abstract class HorizontalDirectionalWithHorizontalFourPartBlock extends HorizontalDirectionalBlock {
    protected HorizontalDirectionalWithHorizontalFourPartBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(StateProperties.HORIZONTAL_FOUR_PART, StateProperties.HorizontalFourPart.BASE).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.HORIZONTAL_FOUR_PART, FACING);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (!pLevel.isClientSide) {
            for (Map.Entry<StateProperties.HorizontalFourPart, BlockPos> entry : StateProperties.HorizontalFourPart.getRelatives(pState.getValue(StateProperties.HORIZONTAL_FOUR_PART), pState.getValue(FACING), pPos).entrySet()) {
                pLevel.setBlockAndUpdate(entry.getValue(), pState.setValue(StateProperties.HORIZONTAL_FOUR_PART, entry.getKey()));
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        Direction direction = pContext.getHorizontalDirection();
        Direction facing = direction.getOpposite();
        BlockState blockState = defaultBlockState().setValue(FACING, facing);
        BlockPos clickedPos = pContext.getClickedPos();
        BlockPos relativePos = clickedPos.relative(StateProperties.HorizontalFourPart.getConnectedDirection(blockState)).relative(direction);
        for (BlockPos blockPos : BlockPos.betweenClosed(clickedPos, relativePos)) {
            if (!canSurvive(blockState, level, blockPos)) return null;
            if (!level.getBlockState(blockPos).canBeReplaced(pContext)) return null;
            if (!level.getWorldBorder().isWithinBounds(blockPos)) return null;
        }
        return blockState;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        BlockState air = Blocks.AIR.defaultBlockState();
        for (BlockPos relative : StateProperties.HorizontalFourPart.getRelatives(pState.getValue(StateProperties.HORIZONTAL_FOUR_PART), pState.getValue(FACING), pPos).values()) {
            pLevel.setBlockAndUpdate(relative, air);
        }
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
