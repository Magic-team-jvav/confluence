package org.confluence.mod.mixin.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import org.confluence.mod.common.init.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemUtils.class)
public abstract class ItemUtilsMixin {
    @Inject(method = "createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private static void denyEmpty(ItemStack emptyStack, Player player, ItemStack filledStack, boolean preventDuplicates, CallbackInfoReturnable<ItemStack> cir) {
        if (emptyStack.is(ModTags.Items.BOTTOMLESS)) {
            cir.setReturnValue(emptyStack);
        }
    }
}
