package org.confluence.mod.common.item.paint;

import PortLib.extensions.net.minecraft.world.entity.player.Player.PortPlayerExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import com.google.common.collect.Iterables;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.data.saved.BrushData;
import org.mesdag.portlib.wrapper.world.item.PortItem;

public class PaintItem extends Item {
    public PaintItem(int rgb) {
        super(new PortItem.PortProperties().stacksTo(99).dyedColor(rgb, true));
    }

    public static int getRGB(ItemStack stack) {
        int dyedColor = PortItemStackExtension.getDyedColor(stack);
        if (dyedColor == -1) {
            return 0xFFFFFF;
        }
        return dyedColor;
    }

    public static int getARGB(ItemStack stack) {
        return getRGB(stack) | 0xFF000000;
    }

    public static void setRGB(ItemStack stack, int rgb) {
        PortItemStackExtension.setDyedColor(stack, rgb);
    }

    public static int useAndGetRGB(Player player) {
        Inventory inventory = player.getInventory();
        for (ItemStack stack : Iterables.concat(inventory.offhand, inventory.items)) {
            assert stack != null;
            if (!stack.isEmpty() && stack.getItem() instanceof PaintItem) {
                int rgb = getRGB(stack);
                if (!PortPlayerExtension.hasInfiniteMaterials(player)) {
                    stack.shrink(1);
                }
                return rgb;
            }
        }
        return BrushData.EMPTY_COLOR;
    }
}
