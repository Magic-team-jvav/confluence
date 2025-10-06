package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.entity.SpearEntity;

public class SpearTrapBlock extends AbstractDispenserMechanicalBlock {
    public SpearTrapBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected boolean behaviour(BlockState pState, ServerLevel pLevel, BlockPos pPos, int pColor, INetworkEntity pEntity) {
        Direction direction = pState.getValue(FACING);
        double x = pPos.getX() + 0.5 + direction.getStepX();
        double y = pPos.getY() + direction.getStepY();
        double z = pPos.getZ() + 0.5 + direction.getStepZ();
        SpearEntity spear = new SpearEntity(pLevel, direction);
        spear.trapPos = pPos;
        spear.setPos(x, y, z);
        pLevel.addFreshEntity(spear);
        return false;
    }

    @Override
    public int delay() {
        return 30;
    }
}
