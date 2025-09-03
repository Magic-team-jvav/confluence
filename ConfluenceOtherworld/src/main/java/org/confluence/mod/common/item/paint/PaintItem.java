package org.confluence.mod.common.item.paint;

import com.google.common.collect.Iterables;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.confluence.mod.common.data.saved.BrushData;

public class PaintItem extends Item {
    public PaintItem(int rgb) {
        super(new Properties().stacksTo(99).component(DataComponents.DYED_COLOR, new DyedItemColor(rgb, true)));
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

    public static int useAndGetRGB(Player player) {
        Inventory inventory = player.getInventory();
        for (ItemStack itemStack : Iterables.concat(inventory.offhand, inventory.items)) {
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof PaintItem) {
                int rgb = getRGB(itemStack);
                if (!player.hasInfiniteMaterials()) {
                    itemStack.shrink(1);
                }
                return rgb;
            }
        }
        return BrushData.EMPTY_COLOR;
    }
}
