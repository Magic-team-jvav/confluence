package org.confluence.mod.common.item.paint;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.confluence.mod.common.data.saved.BrushData;

public class PaintItem extends Item {
    public PaintItem(int color) {
        super(new Properties().stacksTo(99).component(DataComponents.DYED_COLOR, new DyedItemColor(color, true)));
    }

    public int getColor(ItemStack stack) {
        DyedItemColor dyeditemcolor = stack.get(DataComponents.DYED_COLOR);
        return dyeditemcolor != null ? dyeditemcolor.rgb() : 0xFFFFFF;
    }

    public static int getColor(Player player) {
        Inventory inventory = player.getInventory();
        ItemStack stack = inventory.offhand.getFirst();
        if (!stack.isEmpty() && stack.getItem() instanceof PaintItem paintItem) {
            int color = paintItem.getColor(stack);
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
            return color;
        }
        for (ItemStack itemStack : inventory.items) {
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof PaintItem paintItem) {
                int color = paintItem.getColor(itemStack);
                if (!player.hasInfiniteMaterials()) {
                    itemStack.shrink(1);
                }
                return color;
            }
        }
        return BrushData.EMPTY_COLOR;
    }
}
