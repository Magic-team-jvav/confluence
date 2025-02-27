package org.confluence.mod.common.block.functional;

import net.minecraft.world.level.block.state.BlockState;

public interface ISimulatorBlock {
    BlockState getSimulatedBlock(boolean isClient);
}
