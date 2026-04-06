package org.confluence.mod.common.block.natural.spreadable.conversion_table;

import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class MoistenConversionTable extends ConversionTable {
    public static final MoistenConversionTable INSTANCE = new MoistenConversionTable();

    @Override
    protected @Nullable Block getTarget(BlockState source, boolean hardmode) {
        Block block = source.getBlock();
        if (block == Blocks.SAND) {
            return NatureBlocks.MOISTENED_SAND_BLOCK.get();
        } else if (block == Blocks.RED_SAND) {
            return NatureBlocks.MOISTENED_RED_SAND_BLOCK.get();
        } else if (block == NatureBlocks.EBONSAND.get()) {
            return NatureBlocks.MOISTENED_EBONSAND_BLOCK.get();
        } else if (block == NatureBlocks.PEARLSAND.get()) {
            return NatureBlocks.MOISTENED_PEARLSAND_BLOCK.get();
        } else if (block == NatureBlocks.CRIMSAND.get()) {
            return NatureBlocks.MOISTENED_CRIMSAND_BLOCK.get();
        }

        Holder.Reference<Block> holder = block.builtInRegistryHolder();
        if (holder.is(BlockTags.CONVERTABLE_TO_MUD)) {
            return Blocks.MUD;
        }
        return null;
    }
}
