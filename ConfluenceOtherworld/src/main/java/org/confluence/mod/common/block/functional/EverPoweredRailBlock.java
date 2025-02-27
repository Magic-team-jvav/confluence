package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class EverPoweredRailBlock extends PoweredRailBlock {
    public EverPoweredRailBlock(Properties properties) {
        super(properties, true);
    }

    @Override
    protected void registerDefaultState() {
        registerDefaultState(stateDefinition.any()
                .setValue(SHAPE, RailShape.NORTH_SOUTH)
                .setValue(POWERED, true)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void updateState(BlockState state, Level level, BlockPos pos, Block block) {}

    @Override
    public float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        return super.getRailMaxSpeed(state, level, pos, cart) * 2.0F;
    }
}
