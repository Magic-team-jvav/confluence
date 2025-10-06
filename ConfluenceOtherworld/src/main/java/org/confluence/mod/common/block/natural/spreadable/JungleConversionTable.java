package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.confluence.mod.common.block.natural.CattailsBodyBlock;
import org.confluence.mod.common.block.natural.CattailsHeadBlock;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class JungleConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source) {
        Block block = source.getBlock();
        if (block == Blocks.TALL_GRASS) {
            return source.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER ? Blocks.SHORT_GRASS : Blocks.AIR;
        }
        if (block == Blocks.MUD) return NatureBlocks.JUNGLE_GRASS_BLOCK.get();
        if (block instanceof CattailsHeadBlock) return NatureBlocks.JUNGLE_CATTAILS_HEAD.get();
        if (block instanceof CattailsBodyBlock) return NatureBlocks.JUNGLE_CATTAILS_BODY.get();
        return null;
    }
}
