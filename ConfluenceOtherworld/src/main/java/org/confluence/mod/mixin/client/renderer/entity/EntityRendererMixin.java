package org.confluence.mod.mixin.client.renderer.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.summoner.DynamicLightDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @ModifyReturnValue(method = "getPackedLightCoords", at = @At("RETURN"))
    private int getPackedLightCoords(int original, Entity entity, float partialTicks) {
        return DynamicLightDispatcher.getDynamicLight(entity.getLightProbePosition(partialTicks), original);
    }
}
