package org.confluence.mod.common.block.natural.spreadable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class GlowingMushroomConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source) {
        return source.is(Blocks.MUD) ? NatureBlocks.MUSHROOM_GRASS_BLOCK.get() : null;
    }
}
