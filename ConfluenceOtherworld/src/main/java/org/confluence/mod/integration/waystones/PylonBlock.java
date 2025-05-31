package org.confluence.mod.integration.waystones;

import net.blay09.mods.waystones.block.WaystoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

// todo
public class PylonBlock extends WaystoneBlock {
    public PylonBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {}
}
