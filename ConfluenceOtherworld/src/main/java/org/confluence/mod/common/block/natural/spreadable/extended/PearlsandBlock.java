package org.confluence.mod.common.block.natural.spreadable.extended;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.spreadable.SpreadingSandBlock;

import static org.confluence.mod.common.block.natural.spreadable.extended.PearlstoneBlock.generateCrystal;

public class PearlsandBlock extends SpreadingSandBlock {
    public PearlsandBlock(int color, Properties properties) {
        super(Type.HALLOW, color, properties);
    }

    @Override
    public void spread(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        super.spread(blockState, serverLevel, blockPos, randomSource);
        generateCrystal(serverLevel, blockPos, randomSource);
    }
}
