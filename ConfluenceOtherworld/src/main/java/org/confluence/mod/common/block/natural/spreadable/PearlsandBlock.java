package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import static org.confluence.mod.common.block.natural.spreadable.PearlstoneBlock.generateCrystal;

public class PearlsandBlock extends SpreadingSandBlock {
    public PearlsandBlock(int color, Properties properties) {
        super(Type.HALLOW, color, properties);
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!serverLevel.isAreaLoaded(blockPos, 3)) return;
        spread(blockState, serverLevel, blockPos, randomSource);
        generateCrystal(serverLevel, blockPos, randomSource);
    }
}
