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

public class HorizontalDirectionalWithVerticalTwoPartBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<HorizontalDirectionalWithVerticalTwoPartBlock> CODEC = simpleCodec(HorizontalDirectionalWithVerticalTwoPartBlock::new);
    public static final EnumProperty<StateProperties.VerticalTwoPart> PART = StateProperties.VERTICAL_TWO_PART;

    public HorizontalDirectionalWithVerticalTwoPartBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, StateProperties.VerticalTwoPart.BASE));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalWithVerticalTwoPartBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            BlockPos relativePos = pos.relative(StateProperties.VerticalTwoPart.getConnectedDirection(state));
            level.setBlockAndUpdate(relativePos, state.setValue(PART, StateProperties.VerticalTwoPart.getAnotherPart(state.getValue(PART))));
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockState blockState = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        BlockPos relativePos = context.getClickedPos().relative(StateProperties.VerticalTwoPart.getConnectedDirection(blockState));
        return level.getBlockState(relativePos).canBeReplaced(context) ? blockState : null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        level.destroyBlock(pos.relative(StateProperties.VerticalTwoPart.getConnectedDirection(state)), false);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
