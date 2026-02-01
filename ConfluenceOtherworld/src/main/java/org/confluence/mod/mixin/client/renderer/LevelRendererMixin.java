package org.confluence.mod.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.util.ClientUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 1))
    private void renderBoulderSunAndDiscardMoon(int shaderTexture, ResourceLocation textureId, Operation<Void> original) {
        ClientUtils.renderBoulderSun(minecraft);
        if (ClientGameEventSystem.moonTexture == null) {
            original.call(shaderTexture, textureId);
        } else {
            original.call(shaderTexture, ClientGameEventSystem.moonTexture);
        }
    }
}
