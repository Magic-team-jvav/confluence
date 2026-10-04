package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.ThornBlock;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.util.OverworldUtils;

public class SpreadingGrassBlock extends SpreadingBlock {
    private static final int MUSHROOM_GROWTH_CHANCE = 5000;
    private static final int SPORE_GROWTH_CHANCE = 5000;
    private static final int THORN_GROWTH_CHANCE = 50;

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
            if (level.getBlockState(above).isAir()) tryGrowPlant(level, above, random);
            super.randomTick(state, level, pos, random);
        }
    }

    private void tryGrowPlant(ServerLevel level, BlockPos above, RandomSource random) {
        Type spreadType = getSpreadType();
        if (spreadType == Type.JUNGLE) {
            if (above.getY() <= OverworldUtils.getUndergroundY() && random.nextInt(SPORE_GROWTH_CHANCE) == 0) {
                level.setBlockAndUpdate(above, NatureBlocks.JUNGLE_SPORE.get().defaultBlockState());
            }
            return;
        }
        BlockState mushroom;
        ThornBlock thorn;
        if (spreadType == Type.CRIMSON) {
            mushroom = NatureBlocks.VICIOUS_MUSHROOM.get().defaultBlockState();
            thorn = NatureBlocks.CRIMSON_THORN.get();
        } else if (spreadType == Type.CORRUPT) {
            mushroom = NatureBlocks.VILE_MUSHROOM.get().defaultBlockState();
            thorn = NatureBlocks.CORRUPTION_THORN.get();
        } else return;

        /// 两种植物独立抽签；同一位置只能放一个，蘑菇长出后不再尝试荆棘。
        if (random.nextInt(MUSHROOM_GROWTH_CHANCE) == 0) {
            level.setBlockAndUpdate(above, mushroom);
            return;
        }
        if (random.nextInt(THORN_GROWTH_CHANCE) != 0) return;
        if (!level.getBlockState(above.east()).isAir()
                || !level.getBlockState(above.west()).isAir()
                || !level.getBlockState(above.south()).isAir()
                || !level.getBlockState(above.north()).isAir()) return;
        level.setBlockAndUpdate(above, thorn.getStateForPlacement(level, above));
    }
}
