package org.confluence.mod.common.item.common;

import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;

public class BaseDyeItem extends CustomRarityItem {
    public BaseDyeItem(Properties properties, ModRarity rarity, int rgb) {
        super(properties.dyedColor(rgb, true), rarity);
    }

    public BaseDyeItem(ModRarity rarity, int rgb) {
        this(new Properties(), rarity, rgb);
    }

    public static int getRGB(ItemStack stack) {
        int dyedColor = stack.getDyedColor();
        if (dyedColor == -1) {
            return 0xFFFFFF;
        }
        return dyedColor;
    }

    public static int getARGB(ItemStack stack) {
        return getRGB(stack) | 0xFF000000;
    }

    public static void setRGB(ItemStack stack, int rgb) {
        stack.setDyedColor(rgb);
    }
}
