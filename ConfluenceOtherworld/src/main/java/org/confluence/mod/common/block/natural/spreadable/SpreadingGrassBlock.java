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
            Type spreadType = getSpreadType();
            if (random.nextInt(50) == 0 && level.getBlockState(above).isAir()) {
                if (random.nextInt(100) < 1) {
                    tryGrowFungi(spreadType, level, above);
                } else {
                    tryGrowThorn(spreadType, level, above);
                }
            }
            super.randomTick(state, level, pos, random);
        }
    }

    private static void tryGrowFungi(Type spreadType, ServerLevel level, BlockPos above) {
        if (spreadType == Type.JUNGLE) {
            if (above.getY() >= -60 && above.getY() <= 0) {
                level.setBlockAndUpdate(above, NatureBlocks.JUNGLE_SPORE.get().defaultBlockState());
            }
        } else if (spreadType == Type.CORRUPT) {
            level.setBlockAndUpdate(above, NatureBlocks.VILE_MUSHROOM.get().defaultBlockState());
        } else if (spreadType == Type.CRIMSON) {
            level.setBlockAndUpdate(above, NatureBlocks.VICIOUS_MUSHROOM.get().defaultBlockState());
        }
    }

    private static void tryGrowThorn(Type spreadType, ServerLevel level, BlockPos above) {
        ThornBlock thorn = null;
        if (spreadType == Type.CRIMSON) {
            thorn = NatureBlocks.CRIMSON_THORN.get();
        } else if (spreadType == Type.CORRUPT) {
            thorn = NatureBlocks.CORRUPTION_THORN.get();
        }
        if (thorn == null) return;
        if (!level.getBlockState(above.east()).isAir()
                || !level.getBlockState(above.west()).isAir()
                || !level.getBlockState(above.south()).isAir()
                || !level.getBlockState(above.north()).isAir()) return;
        level.setBlockAndUpdate(above, thorn.getStateForPlacement(level, above));
    }
}
