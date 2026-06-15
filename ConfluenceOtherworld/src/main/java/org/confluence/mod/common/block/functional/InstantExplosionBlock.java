package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.network.INetworkEntity;

public class InstantExplosionBlock extends AbstractMechanicalBlock {
    public InstantExplosionBlock() {
        super(Properties.copy(Blocks.TNT));
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
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15.0F, false, Level.ExplosionInteraction.BLOCK);
    }
}
