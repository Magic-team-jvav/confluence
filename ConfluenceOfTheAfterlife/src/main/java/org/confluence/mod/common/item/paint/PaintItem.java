package org.confluence.mod.common.item.paint;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PaintItem extends Item {
    public final int color;

    public PaintItem(int color) {
        super(new Properties().stacksTo(99));
        this.color = color;
    }

    public static int getColor(Player player) {
        Inventory inventory = player.getInventory();
        if (!inventory.offhand.isEmpty() && inventory.offhand.getFirst().getItem() instanceof PaintItem paintItem) {
            if (!player.getAbilities().instabuild) {
                inventory.offhand.getFirst().shrink(1);
            }
            return paintItem.color;
        }
        for (ItemStack itemStack : inventory.items) {
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof PaintItem paintItem) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                return paintItem.color;
            }
        }
        return -1;
    }
}
