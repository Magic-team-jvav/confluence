package org.confluence.mod.common.item.fishing;

import com.google.common.collect.Iterables;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.BaitItems;
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
            // TODO 猪鲨实现后为松露虫接入专用召唤流程，暂不作为普通鱼饵提供 666 点渔力。
            if (!itemStack.isEmpty() && !itemStack.is(BaitItems.TRUFFLE_WORM.get()) && itemStack.getItem() instanceof IBait) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }
}
