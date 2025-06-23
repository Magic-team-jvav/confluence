package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class JungleGrassBlock extends SpreadingGrassBlock{
    public JungleGrassBlock(Type type, Properties properties) {
        super(type, properties.mapColor(DyeColor.byId(0x59C93C)));
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!serverLevel.isAreaLoaded(blockPos, 3)) return;
        if (isFullBlock(serverLevel, blockPos.above())) {
            serverLevel.setBlockAndUpdate(blockPos, Blocks.MUD.defaultBlockState());
        } else {
            super.randomTick(blockState, serverLevel, blockPos, randomSource);
        }
    }
}
