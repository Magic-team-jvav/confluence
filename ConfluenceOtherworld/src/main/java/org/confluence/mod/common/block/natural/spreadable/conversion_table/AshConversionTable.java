package org.confluence.mod.common.block.natural.spreadable.conversion_table;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class AshConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source) {
        return source.is(NatureBlocks.ASH_BLOCK) ? NatureBlocks.ASH_GRASS_BLOCK.get() : null;
    }
}
