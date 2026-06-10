package org.confluence.mod.common.block.natural;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import org.confluence.mod.common.init.block.ModBlocks;

public class LunartearOreBlock extends Block {
    public LunartearOreBlock() {
        super(Properties.of()
                .randomTicks()
                .mapColor(MapColor.COLOR_CYAN)
                .requiresCorrectToolForDrops()
                .strength(30.0F, ModBlocks.getObsidianBasedExplosionResistance(100))
                .sound(SoundType.STONE));
    }

}
