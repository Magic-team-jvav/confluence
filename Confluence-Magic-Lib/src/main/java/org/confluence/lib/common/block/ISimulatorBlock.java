package org.confluence.lib.common.block;

import net.minecraft.world.level.block.state.BlockState;

public interface ISimulatorBlock {
    BlockState getSimulatedBlock(boolean isClient);
}
