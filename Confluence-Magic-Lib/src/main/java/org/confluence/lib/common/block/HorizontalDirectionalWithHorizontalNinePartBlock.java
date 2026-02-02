package org.confluence.lib.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

public class HorizontalDirectionalWithHorizontalNinePartBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<HorizontalDirectionalWithHorizontalNinePartBlock> CODEC = simpleCodec(HorizontalDirectionalWithHorizontalNinePartBlock::new);
    public static final EnumProperty<StateProperties.HorizontalNinePart> PART = StateProperties.HORIZONTAL_NINE_PART;
    public HorizontalDirectionalWithHorizontalNinePartBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.getStateDefinition().any().setValue(PART, StateProperties.HorizontalNinePart.CENTER).setValue(FACING, Direction.NORTH));
    }
    @Override
    public MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, FACING);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            StateProperties.HorizontalNinePart.getAllExcept(facing, pos, state.getValue(PART)).forEach((part, posNew) -> level.setBlockAndUpdate(posNew, state.setValue(PART, part).setValue(FACING, facing)));
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();

        for (BlockPos probableCenter : StateProperties.HorizontalNinePart.getAllExcept(facing, pos, null).values()) {
            if (check(probableCenter, facing, level, context)) {
                StateProperties.HorizontalNinePart role = StateProperties.HorizontalNinePart.infer(probableCenter, pos, facing);
                if (role == null) return null;
                else return defaultBlockState().setValue(PART, role).setValue(FACING, facing);
            }
        }
        return null;
    }

    private boolean check(BlockPos probableCenter, Direction facing, Level level, BlockPlaceContext context) {
        return StateProperties.HorizontalNinePart.getAllExcept(facing, probableCenter, null).entrySet().stream()
                .allMatch(entry -> {
                    if (!canSurvive(defaultBlockState().setValue(PART, entry.getKey()).setValue(FACING, facing), level, entry.getValue())) return false;
                    if (!level.getBlockState(entry.getValue()).canBeReplaced(context)) return false;
                    return level.getWorldBorder().isWithinBounds(entry.getValue());
                });
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (newState.getBlock() instanceof HorizontalDirectionalWithHorizontalNinePartBlock) return;
        StateProperties.HorizontalNinePart partBase = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        StateProperties.HorizontalNinePart.getAllExcept(facing, partBase.toCenter(pos, facing, false), partBase).forEach((part, posO) -> level.destroyBlock(posO, false));
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(PART).equals(StateProperties.HorizontalNinePart.CENTER)) {
            BlockPos suspendingPos = pos.above();
            return level.getBlockState(suspendingPos).isFaceSturdy(level, suspendingPos, Direction.DOWN);
        }
        else return super.canSurvive(state, level, pos);
    }
}
