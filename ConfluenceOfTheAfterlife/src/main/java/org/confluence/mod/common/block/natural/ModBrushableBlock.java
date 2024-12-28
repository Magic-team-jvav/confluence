package org.confluence.mod.common.block.natural;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBrushableBlock extends BrushableBlock {

    public ModBrushableBlock(Block turnsInto, SoundEvent brushSound, SoundEvent brushCompletedSound, BlockBehaviour.Properties properties) {
        super(turnsInto, brushSound, brushCompletedSound, properties);
    }
}
