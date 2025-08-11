package org.confluence.mod.mixin.integration.curios;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.network.server.CuriosServerPayloadHandler;

@Mixin(value = CuriosServerPayloadHandler.class, remap = false)
public abstract class CuriosServerPayloadHandlerMixin {
    @Inject(method = "lambda$handleDestroyPacket$11", at = @At("TAIL"))
    private static void destroyExtraInventory(IPayloadContext ctx, CallbackInfo ci) {
        ExtraInventory extraInventory = ExtraInventory.of(ctx.player());
        for (int i = 0; i < extraInventory.getContainerSize(); i++) {
            extraInventory.setItem(i, ItemStack.EMPTY);
        }
    }
}
