package org.confluence.mod.common.block.conversion.natural;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.conversion.ConversionTable;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class MoistenConversionTable extends ConversionTable {
    public static final MoistenConversionTable INSTANCE = new MoistenConversionTable();

    @Override
    protected @Nullable Block getTarget(BlockState source, boolean hardmode) {
        if (source.is(Blocks.SAND)) {
            return NatureBlocks.MOISTENED_SAND_BLOCK.get();
        }
        if (source.is(Blocks.RED_SAND)) {
            return NatureBlocks.MOISTENED_RED_SAND_BLOCK.get();
        }
        if (source.is(NatureBlocks.EBONSAND.get())) {
            return NatureBlocks.MOISTENED_EBONSAND_BLOCK.get();
        }
        if (source.is(NatureBlocks.PEARLSAND.get())) {
            return NatureBlocks.MOISTENED_PEARLSAND_BLOCK.get();
        }
        if (source.is(NatureBlocks.CRIMSAND.get())) {
            return NatureBlocks.MOISTENED_CRIMSAND_BLOCK.get();
        }
        if (source.is(BlockTags.CONVERTABLE_TO_MUD)) {
            return Blocks.MUD;
        }
        return null;
    }
}
