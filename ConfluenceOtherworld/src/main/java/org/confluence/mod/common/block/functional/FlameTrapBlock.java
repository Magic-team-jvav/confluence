package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.entity.FlameCloudEntity;

public class FlameTrapBlock extends AbstractDispenserMechanicalBlock {
    public FlameTrapBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected boolean behaviour(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        Direction direction = pState.getValue(FACING);
        double x = pPos.getX() + 0.5 + 2.5 * direction.getStepX();
        double y = pPos.getY() + 0.5 + 2.5 * direction.getStepY();
        double z = pPos.getZ() + 0.5 + 2.5 * direction.getStepZ();
        FlameCloudEntity entity = new FlameCloudEntity(pLevel, x, y, z);
       return pLevel.addFreshEntity(entity);
    }

    @Override
    protected int delay() {
        return 37;
    }
}
