package org.confluence.mod.mixed;

import net.minecraft.world.item.ItemStack;

public interface IClientItemStack {
    void confluence$setGroupId(int id);

    int confluence$getGroupId();

    static IClientItemStack of(ItemStack stack) {
        return (IClientItemStack) stack;
    }
}
