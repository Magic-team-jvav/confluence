package org.confluence.mod.common.item.common;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import org.confluence.terra_curio.common.component.ModRarity;

public class DemonConch extends MagicConch {
    public DemonConch() {
        super(new Properties().stacksTo(1), ModRarity.LIGHT_RED);
    }

    @Override
    protected boolean checkAvailable(UseOnContext pContext) {
        return pContext.getLevel().getBlockState(pContext.getClickedPos()).is(Blocks.NETHER_PORTAL);
    }
}
