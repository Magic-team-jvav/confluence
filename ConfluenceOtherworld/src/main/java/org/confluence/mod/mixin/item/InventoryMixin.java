package org.confluence.mod.mixin.item;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

import static org.confluence.mod.common.attachment.ExtraInventory.*;
import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Shadow
    protected abstract boolean hasRemainingSpaceForItem(ItemStack destination, ItemStack origin);

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void add2Extra(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ModTags.Items.COINS)) {
            ExtraInventory extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
            if (confluence$insert2Extra(COINS_START, SIZE_COINS, extraInventory, stack, extraInventory1 -> {
                for (int i = 0; i < SIZE_COINS; i++) {
                    ItemStack coins = extraInventory.getCoins(i);
                    int count = coins.getCount();
                    if (count >= UPGRADES_COUNT) {
                        Item coin = coins.getItem();
                        ItemStack itemStack = null;
                        if (coin == ModItems.COPPER_COIN.get()) {
                            itemStack = ModItems.SILVER_COIN.get().getDefaultInstance();
                        } else if (coin == ModItems.SILVER_COIN.get()) {
                            itemStack = ModItems.GOLDEN_COIN.get().getDefaultInstance();
                        } else if (coin == ModItems.GOLDEN_COIN.get()) {
                            itemStack = ModItems.PLATINUM_COIN.get().getDefaultInstance();
                        }
                        if (itemStack != null) {
                            coins.setCount(count % UPGRADES_COUNT);
                            extraInventory1.setItem(COINS_START + i, coins);
                            itemStack.setCount(count / UPGRADES_COUNT);
                            confluence$insert2Extra(COINS_START, SIZE_COINS, extraInventory1, itemStack, extraInventory2 -> {});
                        }
                    }
                }
            })) {
                PlayerUtils.sortCoins(player);
                cir.setReturnValue(true);
            }
        } else if (stack.is(ModTags.Items.AMMO)) {
            if (stack.is(MaterialItems.FALLING_STAR.get())) return;
            ExtraInventory extraInventory = player.getData(ModAttachmentTypes.EXTRA_INVENTORY);
            if (confluence$insert2Extra(AMMO_START, SIZE_AMMO, extraInventory, stack, extraInventory2 -> {})) {
                cir.setReturnValue(true);
            }
        } else if (!stack.isEmpty() && PrefixUtils.canInit(stack)) {
            PrefixUtils.initPrefix(player.getRandom(), stack);
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"))
    private void initPrefix(int index, ItemStack stack, CallbackInfo ci) {
        if (PrefixUtils.canInit(stack)) {
            PrefixUtils.initPrefix(player.getRandom(), stack);
        }
    }

    @Unique
    private boolean confluence$insert2Extra(int head, int size, ExtraInventory extraInventory, ItemStack stack, Consumer<ExtraInventory> consumer) {
        int i;
        do {
            i = stack.getCount();
            stack.setCount(confluence$addResource2Extra(head, size, extraInventory, stack));
            consumer.accept(extraInventory);
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
        boolean found = false;
        for (; index < size; index++) {
            ItemStack itemStack = extraInventory.getItem(head + index);
            if (itemStack.isEmpty() || hasRemainingSpaceForItem(itemStack, stack)) {
                found = true;
                break;
            }
        }
        if (!found) {
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
