package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.ISimulatorBlock;
import org.confluence.lib.common.block.StateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class FragileBlock extends Block implements ISimulatorBlock {
    private final Supplier<BlockState> simulatorBlock;

    public FragileBlock(Properties pProperties, Supplier<BlockState> simulatorBlock) {
        super(pProperties);
        this.simulatorBlock = simulatorBlock;
        registerDefaultState(stateDefinition.any().setValue(StateProperties.IS_SUPPORTING, true).setValue(BlockStateProperties.FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.IS_SUPPORTING, BlockStateProperties.FACING);
    }

    @Override
    public BlockState getSimulatedBlock(boolean isClient) {
        return simulatorBlock.get();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockState blockState = level.getBlockState(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
        BlockState state = defaultBlockState();
        if (blockState.hasProperty(StateProperties.IS_SUPPORTING)) {
            state = state.setValue(StateProperties.IS_SUPPORTING, false);
        }
        return state.setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide() && !state.getValue(StateProperties.IS_SUPPORTING)) {
            BlockState blockState = level.getBlockState(pos.relative(state.getValue(BlockStateProperties.FACING)));
            if (!blockState.hasProperty(StateProperties.IS_SUPPORTING)) {
                level.destroyBlock(pos, false);
            }
        }
        return state;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return simulatorBlock.get().getShape(level, pos, context);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}
