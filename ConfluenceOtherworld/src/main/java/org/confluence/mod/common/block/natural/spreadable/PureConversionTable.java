package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class PureConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source) {
        if (source.is(BlockTags.LOGS)) return Blocks.OAK_LOG;
        if (source.is(BlockTags.LEAVES)) return Blocks.OAK_LEAVES;
        if (source.is(BlockTags.BASE_STONE_OVERWORLD)) return Blocks.STONE;
        if (source.is(ModTags.Blocks.PURE_CONVERSION_GRASS_BLOCK)) return Blocks.GRASS_BLOCK;
        if (source.is(ModTags.Blocks.PURE_CONVERSION_JUNGLE_GRASS_BLOCK)) return NatureBlocks.JUNGLE_GRASS_BLOCK.get();
        if (source.is(ModTags.Blocks.PURE_CONVERSION_SHORT_GRASS)) return Blocks.SHORT_GRASS;
        if (source.is(ModTags.Blocks.PURE_CONVERSION_PACKED_ICE)) return Blocks.PACKED_ICE;
        if (source.is(ModTags.Blocks.PURE_CONVERSION_ICE)) return Blocks.ICE;
        if (source.is(ModTags.Blocks.PURE_CONVERSION_SAND)) return Blocks.SAND;
        if (source.is(ModTags.Blocks.PURE_CONVERSION_SANDSTONE)) return Blocks.SANDSTONE;
        if (source.is(ModTags.Blocks.PURE_CONVERSION_HARDENED_SAND_BLOCK)) return NatureBlocks.HARDENED_SAND_BLOCK.get();
        if (source.is(ModTags.Blocks.PURE_CONVERSION_MOIST_SAND_BLOCK)) return NatureBlocks.MOISTENED_SAND_BLOCK.get();
        return null;
    }
}
