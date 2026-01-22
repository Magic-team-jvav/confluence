package org.confluence.mod.common.block.natural.spreadable.conversion_table;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.confluence.mod.common.block.natural.CattailBlock;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class GlowingMushroomConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source, boolean hardmode) {
        Block block = source.getBlock();
        if (block == Blocks.TALL_GRASS) {
            return source.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER ? NatureBlocks.GLOWING_MUSHROOM.get() : Blocks.AIR;
        }
        if (block == Blocks.MUD) return NatureBlocks.MUSHROOM_GRASS_BLOCK.get();
        if (block instanceof CattailBlock && block != NatureBlocks.GLOWING_MUSHROOM_CATTAIL_BLOCK.get())
            return NatureBlocks.GLOWING_MUSHROOM_CATTAIL_BLOCK.get();
        return null;
    }
}
