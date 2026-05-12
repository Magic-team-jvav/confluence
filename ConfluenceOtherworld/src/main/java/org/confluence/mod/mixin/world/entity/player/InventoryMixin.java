package org.confluence.mod.mixin.world.entity.player;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.common.CoinItem;
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

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_AMMO;
import static org.confluence.mod.common.attachment.ExtraInventory.SIZE_COINS;
import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Shadow
    protected abstract boolean hasRemainingSpaceForItem(ItemStack destination, ItemStack origin);

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void add2Extra(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) ItemStack stack) {
        if (stack.is(ModTags.Items.COINS)) {
            ExtraInventory extraInventory = ExtraInventory.of(player);
            if (confluence$insert2Extra(SIZE_COINS, extraInventory, stack, extraInventory1 -> {
                for (int i = 0; i < SIZE_COINS; i++) {
                    ItemStack coins = extraInventory.getCoins(i);
                    int count = coins.getCount();
                    if (count >= UPGRADES_COUNT && coins.getItem() instanceof CoinItem coinItem && coinItem.upgrade != null) {
                        ItemStack itemStack = coinItem.upgrade.get().getDefaultInstance();
                        coins.setCount(count % UPGRADES_COUNT);
                        extraInventory1.setCoins(i, coins);
                        itemStack.setCount(count / UPGRADES_COUNT);
                        confluence$insert2Extra(SIZE_COINS, extraInventory1, itemStack, extraInventory2 -> {}, extraInventory1::getCoins, extraInventory1::setCoins);
                    }
                }
            }, extraInventory::getCoins, extraInventory::setCoins)) {
                PlayerUtils.sortCoins(player);
                cir.setReturnValue(true);
            }
        } else if (stack.is(ModTags.Items.AMMO)) {
            if (CommonConfigs.ammoSlotsItemBlackList.stream().anyMatch(stack.getItemHolder()::is) ||
                    CommonConfigs.ammoSlotsTagBlackList.stream().anyMatch(stack::is)) return;
            ExtraInventory extraInventory = ExtraInventory.of(player);
            if (confluence$insert2Extra(SIZE_AMMO, extraInventory, stack, extraInventory2 -> {}, extraInventory::getAmmo, extraInventory::setAmmo)) {
                cir.setReturnValue(true);
            }
        } else if (!stack.isEmpty() && PrefixUtils.canInit(stack)) {
            PrefixUtils.initPrefix(player.getRandom(), stack);
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"))
    private void initPrefix(int index, ItemStack stack, CallbackInfo ci) {
        if (!stack.isEmpty() && PrefixUtils.canInit(stack)) {
            PrefixUtils.initPrefix(player.getRandom(), stack);
        }
    }

    @Inject(method = "clearOrCountMatchingItems", at = @At("RETURN"), cancellable = true)
    private void withExtra(Predicate<ItemStack> stackPredicate, int maxCount, Container inventory, CallbackInfoReturnable<Integer> cir, @Local boolean simulate) {
        int count = cir.getReturnValue();
        count += ContainerHelper.clearOrCountMatchingItems(ExtraInventory.of(player), stackPredicate, maxCount - count, simulate);
        cir.setReturnValue(count);
    }

    @Unique
    private boolean confluence$insert2Extra(int size, ExtraInventory extraInventory, ItemStack stack, Consumer<ExtraInventory> consumer, IntFunction<ItemStack> getter, BiConsumer<Integer, ItemStack> setter) {
        int i;
        do {
            i = stack.getCount();
            stack.setCount(confluence$addResource2Extra(size, stack, getter, setter));
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
    private int confluence$addResource2Extra(int size, ItemStack stack, IntFunction<ItemStack> getter, BiConsumer<Integer, ItemStack> setter) {
        int index = 0;
        boolean found = false;
        for (; index < size; index++) {
            ItemStack itemStack = getter.apply(index);
            if (itemStack.isEmpty() || hasRemainingSpaceForItem(itemStack, stack)) {
                found = true;
                break;
            }
        }
        if (!found) {
            return stack.getCount();
        }
        int i = stack.getCount();
        ItemStack itemStack = getter.apply(index);
        if (itemStack.isEmpty()) {
            itemStack = stack.copyWithCount(0);
            setter.accept(index, itemStack);
        }
        int j = stack.getMaxStackSize() - itemStack.getCount();
        int k = Math.min(i, j);
        if (k != 0) {
            i -= k;
            itemStack.grow(k);
            itemStack.setPopTime(5);
        }
        return i;
    }
}
