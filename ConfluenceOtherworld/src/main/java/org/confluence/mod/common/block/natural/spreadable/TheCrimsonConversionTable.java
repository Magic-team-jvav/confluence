package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class TheCrimsonConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source) {
        if (source.is(BlockTags.LOGS)) return NatureBlocks.SHADOW_LOG_BLOCKS.getLog().get();
        if (source.is(BlockTags.LEAVES)) return NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves().get();
        if (source.is(BlockTags.BASE_STONE_OVERWORLD)) return NatureBlocks.CRIMSTONE.get();
        if (source.is(ModTags.Blocks.CRIMSON_CONVERSION_DIRT)) return Blocks.DIRT;
        if (source.is(ModTags.Blocks.CRIMSON_CONVERSION_GRASS_BLOCK)) return NatureBlocks.CRIMSON_GRASS_BLOCK.get();
        if (source.is(ModTags.Blocks.CRIMSON_CONVERSION_JUNGLE_GRASS_BLOCK)) return NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK.get();
        if (source.is(ModTags.Blocks.CRIMSON_CONVERSION_SHORT_GRASS)) return NatureBlocks.CRIMSON_GRASS.get();
        if (source.is(ModTags.Blocks.CRIMSON_CONVERSION_PACKED_ICE)) return NatureBlocks.RED_PACKED_ICE.get();
        if (source.is(ModTags.Blocks.CRIMSON_CONVERSION_ICE)) return NatureBlocks.RED_ICE.get();
        if (source.is(ModTags.Blocks.CRIMSON_CONVERSION_SAND)) return NatureBlocks.CRIMSAND.get();
        if (source.is(ModTags.Blocks.CRIMSON_CONVERSION_SANDSTONE)) return NatureBlocks.CRIMSANDSTONE.get();
        if (source.is(ModTags.Blocks.CRIMSON_CONVERSION_HARDENED_SAND_BLOCK)) return NatureBlocks.HARDENED_CRIMSAND_BLOCK.get();
        if (source.is(ModTags.Blocks.CRIMSON_CONVERSION_MOIST_SAND_BLOCK)) return NatureBlocks.MOISTENED_CRIMSAND_BLOCK.get();
        return null;
    }
}
