package org.confluence.mod.common.item.common;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;

public class BaseDyeItem extends CustomRarityItem {
    public BaseDyeItem(Properties properties, ModRarity rarity, int rgb) {
        super(properties.component(DataComponents.DYED_COLOR, new DyedItemColor(rgb, true)), rarity);
    }

    public BaseDyeItem(ModRarity rarity, int rgb) {
        this(new Properties(), rarity, rgb);
    }

    public static int getRGB(ItemStack stack) {
        DyedItemColor dyeditemcolor = stack.get(DataComponents.DYED_COLOR);
        return dyeditemcolor != null ? dyeditemcolor.rgb() : 0xFFFFFF;
    }

    public static int getARGB(ItemStack stack) {
        return FastColor.ARGB32.opaque(getRGB(stack));
    }

    public static void setRGB(ItemStack stack, int rgb) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(rgb, true));
    }
}
