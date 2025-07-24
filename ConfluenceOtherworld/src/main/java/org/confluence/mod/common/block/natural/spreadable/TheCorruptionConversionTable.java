package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class TheCorruptionConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source) {
        if (source.is(BlockTags.LOGS)) return NatureBlocks.EBONY_LOG_BLOCKS.getLog().get();
        if (source.is(BlockTags.LEAVES)) return NatureBlocks.EBONY_LOG_BLOCKS.getLeaves().get();
        if (source.is(BlockTags.BASE_STONE_OVERWORLD)) return NatureBlocks.EBONSTONE.get();
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_DIRT)) return Blocks.DIRT;
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_GRASS_BLOCK)) return NatureBlocks.CORRUPT_GRASS_BLOCK.get();
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_JUNGLE_GRASS_BLOCK)) return NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get();
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_SHORT_GRASS)) return NatureBlocks.CORRUPT_GRASS.get();
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_PACKED_ICE)) return NatureBlocks.PURPLE_PACKED_ICE.get();
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_ICE)) return NatureBlocks.PURPLE_ICE.get();
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_SAND)) return NatureBlocks.EBONSAND.get();
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_SANDSTONE)) return NatureBlocks.EBONSANDSTONE.get();
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_HARDENED_SAND_BLOCK)) return NatureBlocks.HARDENED_EBONSAND_BLOCK.get();
        if (source.is(ModTags.Blocks.CORRUPTION_CONVERSION_MOIST_SAND_BLOCK)) return NatureBlocks.MOISTENED_EBONSAND_BLOCK.get();
        return null;
    }
}
