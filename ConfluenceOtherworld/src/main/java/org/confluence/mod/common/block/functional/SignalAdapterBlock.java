package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.block.functional.network.INetworkEntity;

import javax.annotation.Nullable;

import static org.confluence.lib.common.block.StateProperties.REVERSE;
import static org.confluence.lib.common.block.StateProperties.SIGNAL;

public class SignalAdapterBlock extends AbstractMechanicalBlock {
    public SignalAdapterBlock() {
        super(Properties.copy(Blocks.REDSTONE_BLOCK));
        registerDefaultState(stateDefinition.any()
                .setValue(SIGNAL, false) // 同时代表了signal和power(强度15)
                .setValue(REVERSE, false)); // false代表signal->power;true代表power->signal
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(SIGNAL, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SIGNAL, REVERSE);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide && pState.getValue(REVERSE)) { // power->signal
            execute(pState, (ServerLevel) pLevel, pPos, pLevel.hasNeighborSignal(pPos));
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (skipInteraction(player.getMainHandItem())) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide && player.isCrouching()) {
            level.setBlockAndUpdate(pos, state.cycle(REVERSE));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        return !pState.getValue(REVERSE) && pState.getValue(SIGNAL) ? 15 : 0;
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return !pState.getValue(REVERSE) && pState.getValue(SIGNAL);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @org.jetbrains.annotations.Nullable Direction direction) {
        return true;
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        if (!state.getValue(SIGNAL)) {
            level.setBlockAndUpdate(pos, state.setValue(SIGNAL, true));
        }
    }

    @Override
    public void onUnExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        if (state.getValue(SIGNAL)) {
            level.setBlockAndUpdate(pos, state.setValue(SIGNAL, false));
        }
    }
}
