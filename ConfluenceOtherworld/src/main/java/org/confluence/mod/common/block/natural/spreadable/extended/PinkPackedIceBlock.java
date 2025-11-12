package org.confluence.mod.common.block.natural.spreadable.extended;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.spreadable.SpreadingIceBlock;

import static org.confluence.mod.common.block.natural.spreadable.extended.PearlstoneBlock.generateCrystal;

public class PinkPackedIceBlock extends SpreadingIceBlock {
    public PinkPackedIceBlock(Properties properties) {
        super(Type.HALLOW, properties);
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (serverLevel.isAreaLoaded(blockPos, 3)) {
            generateCrystal(serverLevel, blockPos, randomSource);
        }
    }
}
