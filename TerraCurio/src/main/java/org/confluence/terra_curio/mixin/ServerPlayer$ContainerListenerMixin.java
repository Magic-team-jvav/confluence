package org.confluence.terra_curio.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.network.s2c.InfoCurioCheckPacketS2C;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.level.ServerPlayer$2")
public abstract class ServerPlayer$ContainerListenerMixin {
    @Shadow
    @Final
    ServerPlayer this$0;

    @Inject(method = "slotChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/InventoryChangeTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;)V"))
    private void inventoryChange(AbstractContainerMenu p_143466_, int p_143467_, ItemStack p_143468_, CallbackInfo ci) {
        InfoCurioCheckPacketS2C.sendToClient(this$0, this$0.getInventory());
    }
}
