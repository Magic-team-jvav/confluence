package org.confluence.mod.common.item.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpyglassItem;
import net.minecraftforge.common.ToolAction;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;


public class BinocularsItem extends SpyglassItem {
    public BinocularsItem() {
        super(new Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.LIGHT_RED));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction itemAbility) {
        return false;
    }

}
