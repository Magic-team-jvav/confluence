package org.confluence.mod.common.item.common;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;

public class BottomlessBucketItem extends BucketItem {
    public BottomlessBucketItem(FlowingFluid flowingFluid, ModRarity rarity) {
        super(flowingFluid, new Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return itemStack.copy();
    }
}
