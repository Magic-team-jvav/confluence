package org.confluence.lib.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

public class HorizontalDirectionalWithHorizontalTwoPartBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<HorizontalDirectionalWithHorizontalTwoPartBlock> CODEC = simpleCodec(HorizontalDirectionalWithHorizontalTwoPartBlock::new);
    public static final EnumProperty<StateProperties.HorizontalTwoPart> PART = StateProperties.HORIZONTAL_TWO_PART;

    public HorizontalDirectionalWithHorizontalTwoPartBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, StateProperties.HorizontalTwoPart.BASE).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalWithHorizontalTwoPartBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, FACING);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (!pLevel.isClientSide) {
            BlockPos relativePos = pPos.relative(StateProperties.HorizontalTwoPart.getConnectedDirection(pState));
            pLevel.setBlockAndUpdate(relativePos, pState.setValue(PART, StateProperties.HorizontalTwoPart.RIGHT));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockState blockState = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        BlockPos relativePos = pContext.getClickedPos().relative(StateProperties.HorizontalTwoPart.getConnectedDirection(blockState));
        return level.getBlockState(relativePos).canBeReplaced(pContext) && level.getWorldBorder().isWithinBounds(relativePos) ? blockState : null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moveByPiston) {
        super.onRemove(state, level, pos, newState, moveByPiston);
        level.destroyBlock(pos.relative(StateProperties.HorizontalTwoPart.getConnectedDirection(state)), false);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
