package org.confluence.mod.common.block.conversion.machine;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.conversion.ConversionTable;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class MushroomMachineConversionTable extends ConversionTable {
    @Override
    protected @Nullable Block getTarget(BlockState source, boolean hardmode) {
        if (source.is(NatureBlocks.JUNGLE_THORN.get())) {
            return Blocks.AIR;
        }
        if (source.is(NatureBlocks.JUNGLE_GRASS_BLOCK.get())) {
            return NatureBlocks.MUSHROOM_GRASS_BLOCK.get();
        }
        if (source.is(NatureBlocks.JUNGLE_CATTAIL_BLOCK.get())) {
            return NatureBlocks.GLOWING_MUSHROOM_CATTAIL_BLOCK.get();
        }
        if (source.is(NatureBlocks.CORRUPTION_THORN.get())
                || source.is(NatureBlocks.CRIMSON_THORN.get())
                || source.is(NatureBlocks.PLANTERA_THORN.get())) {
            return Blocks.AIR;
        }
        return null;
    }
}
