package org.confluence.mod.mixin.item;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.confluence.mod.common.attachment.ExtraInventory.*;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Shadow
    protected abstract boolean hasRemainingSpaceForItem(ItemStack destination, ItemStack origin);

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamaged()Z"), cancellable = true)
    private void add2Extra(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ModTags.Items.COINS)) {
            if (confluence$insert2Extra(COINS_START, SIZE_COINS, stack)) {
                cir.setReturnValue(true);
            }
        } else if (stack.is(ModTags.Items.AMMO)) {
            if (confluence$insert2Extra(AMMO_START, SIZE_AMMO, stack)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private boolean confluence$insert2Extra(int head, int size, ItemStack stack) {
        ExtraInventory extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
        int i;
        do {
            i = stack.getCount();
            stack.setCount(confluence$addResource2Extra(head, size, extraInventory, stack));
        } while (!stack.isEmpty() && stack.getCount() < i);

        if (stack.getCount() == i && player.hasInfiniteMaterials()) {
            stack.setCount(0);
            return true;
        } else {
            return stack.getCount() < i;
        }
    }

    @Unique
    private int confluence$addResource2Extra(int head, int size, ExtraInventory extraInventory, ItemStack stack) {
        int index = 0;
        ItemStack found = null;
        for (; index < size; index++) {
            ItemStack itemStack = extraInventory.getItem(head + index);
            if (itemStack.isEmpty() || hasRemainingSpaceForItem(itemStack, stack)) {
                found = itemStack;
                break;
            }
        }
        if (found == null) {
            return stack.getCount();
        }
        int i = stack.getCount();
        ItemStack itemStack = extraInventory.getItem(head + index);
        if (itemStack.isEmpty()) {
            itemStack = stack.copyWithCount(0);
            extraInventory.setItem(head + index, itemStack);
        }
        int j = extraInventory.getMaxStackSize(itemStack) - itemStack.getCount();
        int k = Math.min(i, j);
        if (k != 0) {
            i -= k;
            itemStack.grow(k);
            itemStack.setPopTime(5);
        }
        return i;
    }
}
