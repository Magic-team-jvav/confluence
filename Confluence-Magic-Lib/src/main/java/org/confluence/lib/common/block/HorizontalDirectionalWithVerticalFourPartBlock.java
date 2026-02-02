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

public class HorizontalDirectionalWithVerticalFourPartBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<HorizontalDirectionalWithVerticalFourPartBlock> CODEC = simpleCodec(HorizontalDirectionalWithVerticalFourPartBlock::new);
    public static final EnumProperty<StateProperties.VerticalFourPart> PART = StateProperties.VERTICAL_FOUR_PART;

    public HorizontalDirectionalWithVerticalFourPartBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, StateProperties.VerticalFourPart.BASE).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalWithVerticalFourPartBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, FACING);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            BlockPos relativePos = pos.relative(StateProperties.VerticalFourPart.getConnectedDirection(state));
            level.setBlockAndUpdate(relativePos, state.setValue(PART, StateProperties.VerticalFourPart.RIGHT));
            level.setBlockAndUpdate(pos.above(), state.setValue(PART, StateProperties.VerticalFourPart.UP));
            level.setBlockAndUpdate(relativePos.above(), state.setValue(PART, StateProperties.VerticalFourPart.RIGHT_UP));
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockState blockState = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        BlockPos clickedPos = context.getClickedPos();
        BlockPos relativeUpPos = clickedPos.relative(StateProperties.VerticalFourPart.getConnectedDirection(blockState)).above();
        for (BlockPos blockPos : BlockPos.betweenClosed(clickedPos, relativeUpPos)) {
            if (!canSurvive(blockState, level, blockPos)) return null;
            if (!level.getBlockState(blockPos).canBeReplaced(context)) return null;
            if (!level.getWorldBorder().isWithinBounds(blockPos)) return null;
        }
        return blockState;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        for (BlockPos relative : StateProperties.VerticalFourPart.getRelatives(state.getValue(PART), state.getValue(FACING), pos).values()) {
            level.destroyBlock(relative, false);
        }
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
