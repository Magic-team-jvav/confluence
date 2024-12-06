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
import org.confluence.mod.common.block.StateProperties;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MechanicalFragileBlock extends AbstractMechanicalBlock implements ISimulatorBlock {
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
    public void neighborChanged(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Block pNeighborBlock, @NotNull BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide && pLevel.hasNeighborSignal(pPos)) {
            execute(pState, (ServerLevel) pLevel, pPos, true);
        }
    }

    @Override
    public void onExecute(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        pLevel.removeBlock(pPos, false);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return simulatorBlock.get().getShape(level, pos, context);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(@NotNull BlockState state) {
        return PushReaction.DESTROY;
    }
}
