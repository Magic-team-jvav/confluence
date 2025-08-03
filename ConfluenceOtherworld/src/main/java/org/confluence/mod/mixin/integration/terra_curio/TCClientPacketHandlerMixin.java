package org.confluence.mod.mixin.integration.terra_curio;

import net.minecraft.world.entity.Entity;
import org.confluence.mod.integration.sodium.SodiumDynamicLightHelper;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TCClientPacketHandler.class, remap = false)
public abstract class TCClientPacketHandlerMixin {
    @Inject(method = "getLuminance", at = @At("RETURN"), cancellable = true)
    private static void extra(Entity entity, CallbackInfoReturnable<Integer> cir) {
        int luminance = SodiumDynamicLightHelper.getLuminance(entity, cir.getReturnValue());
        if (luminance != 0) cir.setReturnValue(luminance);
    }
}
