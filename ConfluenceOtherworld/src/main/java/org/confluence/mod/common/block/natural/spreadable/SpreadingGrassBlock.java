package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.ThornBlock;
import org.confluence.mod.common.init.block.NatureBlocks;

public class SpreadingGrassBlock extends SpreadingBlock {
    public SpreadingGrassBlock(Type type, Properties properties) {
        super(type, properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 3)) return;
        BlockPos above = pos.above();
        if (isFullBlock(level, above)) {
            level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
        } else {
            ThornBlock thorn = switch (getSpreadType()) {
                case CRIMSON -> NatureBlocks.CRIMSON_THORN.get();
                case CORRUPT -> NatureBlocks.CORRUPTION_THORN.get();
                default -> null;
            };
            if (thorn != null && random.nextInt(50) == 0
                    && level.getBlockState(above).isAir()
                    && level.getBlockState(above.east()).isAir()
                    && level.getBlockState(above.west()).isAir()
                    && level.getBlockState(above.south()).isAir()
                    && level.getBlockState(above.north()).isAir()
            ) {
                level.setBlockAndUpdate(above, thorn.getStateForPlacement(level, above));
            }
            super.randomTick(state, level, pos, random);
        }
    }
}
