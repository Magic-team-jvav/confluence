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

public class HorizontalDirectionalWithForwardTwoPartBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<HorizontalDirectionalWithForwardTwoPartBlock> CODEC = simpleCodec(HorizontalDirectionalWithForwardTwoPartBlock::new);
    public static final EnumProperty<StateProperties.ForwardTwoPart> PART = StateProperties.FORWARD_TWO_PART;

    public HorizontalDirectionalWithForwardTwoPartBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, StateProperties.ForwardTwoPart.BASE).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalWithForwardTwoPartBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.FORWARD_TWO_PART, FACING);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            BlockPos relativePos = pos.relative(state.getValue(FACING).getOpposite());
            level.setBlockAndUpdate(relativePos, state.setValue(StateProperties.FORWARD_TWO_PART, StateProperties.ForwardTwoPart.FORWARD));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockState blockState = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        BlockPos relativePos = pContext.getClickedPos().relative(StateProperties.ForwardTwoPart.getConnectedDirection(blockState));
        return level.getBlockState(relativePos).canBeReplaced(pContext) && level.getWorldBorder().isWithinBounds(relativePos) ? blockState : null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moveByPiston) {
        super.onRemove(state, level, pos, newState, moveByPiston);
        level.destroyBlock(pos.relative(StateProperties.ForwardTwoPart.getConnectedDirection(state)), false);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
