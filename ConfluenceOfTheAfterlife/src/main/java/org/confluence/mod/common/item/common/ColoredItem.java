package org.confluence.mod.common.item.common;

import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.component.NbtComponent;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terra_curio.util.TCUtils;

public class ColoredItem extends CustomRarityItem {
    public ColoredItem(ModRarity rarity) {
        super(rarity);
    }

    public static void setColor(ItemStack itemStack, int rgb) {
        TCUtils.updateItemStackNbt(itemStack, tag -> tag.putInt("color", rgb));
    }

    public static int getColor(ItemStack itemStack) {
        NbtComponent nbtComponent = itemStack.get(TCDataComponentTypes.NBT);
        if (nbtComponent == null) {
            return 0xFF66CCFF;
        }
        return nbtComponent.nbt().getInt("color");
    }
}
