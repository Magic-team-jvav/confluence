package org.confluence.mod.mixin.client;

import net.minecraft.world.item.ItemStack;
import org.confluence.mod.mixed.IClientItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public class ClientItemStackMixin implements IClientItemStack {
    @Unique
    private int confluence$groupId = -1;

    @Override
    public void confluence$setGroupId(int id) {
        this.confluence$groupId = id;
    }

    @Override
    public int confluence$getGroupId() {
        return confluence$groupId;
    }
}
