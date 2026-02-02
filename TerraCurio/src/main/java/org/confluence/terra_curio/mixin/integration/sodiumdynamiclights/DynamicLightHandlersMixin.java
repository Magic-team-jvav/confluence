package org.confluence.terra_curio.mixin.integration.sodiumdynamiclights;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "dev.lambdaurora.lambdynlights.api.DynamicLightHandlers", remap = false)
public abstract class DynamicLightHandlersMixin {
    @ModifyReturnValue(method = "getLuminanceFrom(Lnet/minecraft/world/entity/Entity;)I", at = @At("RETURN"))
    private static <T extends Entity> int getAccessoryLuminance(int original, @Local(argsOnly = true) T entity) {
        return Math.max(TCClientPacketHandler.getLuminance(entity), original);
    }
}
