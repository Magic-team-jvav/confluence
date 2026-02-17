package org.confluence.mod.common.block.natural.spreadable.extended;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.SpreadableMoistenedSandBlock;
import org.confluence.mod.common.init.block.NatureBlocks;

import static org.confluence.mod.common.block.natural.spreadable.extended.PearlstoneBlock.generateCrystal;

public class MoistenedPearlsandBlock extends SpreadableMoistenedSandBlock {
    public MoistenedPearlsandBlock(Properties properties) {
        super(Type.HALLOW, properties, NatureBlocks.PEARLSAND);
    }

    @Override
    public void spread(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource randomSource) {
        super.spread(blockState, level, blockPos, randomSource);
        generateCrystal(level, blockPos, randomSource);
    }
}
