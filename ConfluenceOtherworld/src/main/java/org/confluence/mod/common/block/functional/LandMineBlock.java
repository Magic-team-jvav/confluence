package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.block.functional.network.INetworkEntity;

public class LandMineBlock extends AbstractMechanicalBlock {
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 6, 14);

    public LandMineBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        level.explode(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 3, false, Level.ExplosionInteraction.MOB);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.Entity entity) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getBlockEntity(pos) instanceof INetworkEntity network) {
            onExecute(state, serverLevel, pos, -1, network);
        }
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (pLevel instanceof ServerLevel serverLevel) {
            execute(pState, serverLevel, pPos, pLevel.hasNeighborSignal(pPos));
        }
    }
}
