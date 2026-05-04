package org.confluence.mod.mixin.client.renderer;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0))
    private ResourceLocation renderSun(ResourceLocation textureId) {
        return ClientPacketHandler.sunTexture == null ? textureId : ClientPacketHandler.sunTexture;
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 1))
    private ResourceLocation renderMoon(ResourceLocation textureId) {
        return ClientGameEventSystem.moonTexture == null ? textureId : ClientGameEventSystem.moonTexture;
    }
}
