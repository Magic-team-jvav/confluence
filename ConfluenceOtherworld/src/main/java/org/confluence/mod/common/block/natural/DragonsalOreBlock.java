package org.confluence.mod.common.block.natural;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import org.confluence.mod.common.init.block.ModBlocks;

public class DragonsalOreBlock extends Block {
    public DragonsalOreBlock() {
        super(Properties.of()
                .randomTicks()
                .mapColor(MapColor.COLOR_PURPLE)
                .requiresCorrectToolForDrops()
                .strength(40.0F, ModBlocks.getObsidianBasedExplosionResistance(100))
                .sound(SoundType.STONE));
    }

}
