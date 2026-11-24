package org.confluence.mod.common.item.fishing;

import com.google.common.collect.Iterables;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IBait {
    float getBaitBonus();

    static @Nullable IBait of(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof IBait bait) {
            return bait;
        }
        return null;
    }

    static ItemStack getFirstBait(Inventory inventory) {
        for (ItemStack itemStack : Iterables.concat(inventory.offhand, inventory.items)) {
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof IBait) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }
}
