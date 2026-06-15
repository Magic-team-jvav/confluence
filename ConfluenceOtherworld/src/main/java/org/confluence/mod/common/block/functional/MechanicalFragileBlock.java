package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.ILibSimulatorBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MechanicalFragileBlock extends AbstractMechanicalBlock implements ILibSimulatorBlock {
    private final Supplier<BlockState> simulatorBlock;

    public MechanicalFragileBlock(Properties pProperties, Supplier<BlockState> simulatorBlock) {
        super(pProperties);
        this.simulatorBlock = simulatorBlock;
        registerDefaultState(stateDefinition.any().setValue(StateProperties.IS_SUPPORTING, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.IS_SUPPORTING);
    }

    @Override
    public BlockState getSimulatedBlock(boolean isClient) {
        return simulatorBlock.get();
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide && pLevel.hasNeighborSignal(pPos)) {
            execute(pState, (ServerLevel) pLevel, pPos, true);
        }
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        level.removeBlock(pos, false);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return simulatorBlock.get().getShape(level, pos, context);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}
