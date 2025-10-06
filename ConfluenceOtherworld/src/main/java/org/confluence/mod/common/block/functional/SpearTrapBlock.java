package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.entity.SpearEntity;

public class SpearTrapBlock extends AbstractDispenserMechanicalBlock {
    public SpearTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean behaviour(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity entity) {
        Direction direction = state.getValue(FACING);
        double x = pos.getX() + 0.5 + direction.getStepX();
        double y = pos.getY() + direction.getStepY();
        double z = pos.getZ() + 0.5 + direction.getStepZ();
        SpearEntity spear = new SpearEntity(level, direction);
        spear.trapPos = pos;
        spear.setPos(x, y, z);
        level.addFreshEntity(spear);
        return false;
    }

    @Override
    public int delay() {
        return 30;
    }
}
