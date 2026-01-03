package org.confluence.mod.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.AxeItems;
import org.confluence.mod.network.s2c.LucyTheAxeDialogPacketS2C;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @WrapOperation(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack checkLucyTheAxe(AbstractContainerMenu instance, Player player, int slotId, Operation<ItemStack> original) {
        ItemStack stack = original.call(instance, player, slotId);
        if (player instanceof ServerPlayer serverPlayer && stack.is(AxeItems.LUCY_THE_AXE)) {
            Slot slot = slots.get(slotId);
            LucyTheAxeDialogPacketS2C.inventory(serverPlayer, slot, !slot.hasItem());
        }
        return stack;
    }
}
