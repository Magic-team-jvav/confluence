package org.confluence.lib.common.item;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.component.NbtComponent;
import org.confluence.lib.util.LibUtils;

public class ColoredItem extends CustomRarityItem {
    public ColoredItem(Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (ItemStack.isSameItem(stack, other)) {
            setRGBA(other, getRGBA(stack));
        }
        return false;
    }

    public static void setRGBA(ItemStack itemStack, int rgba) {
        LibUtils.updateItemStackNbt(itemStack, tag -> tag.putInt("color", rgba));
    }

    public static int getRGBA(ItemStack itemStack) {
        NbtComponent nbtComponent = itemStack.get(ConfluenceMagicLib.NBT);
        if (nbtComponent == null) {
            return 0xFF66CCFF;
        }
        return nbtComponent.nbt().getInt("color");
    }

    public static void merge(ItemStack carried, ItemStack onSlot) {
        if (onSlot.getItem() instanceof ColoredItem && ItemStack.isSameItem(onSlot, carried)) {
            setRGBA(carried, getRGBA(onSlot));
        }
    }
}
