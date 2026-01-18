package org.confluence.mod.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.util.ClientUtils;
import org.joml.Matrix4f;
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
        if (ClientGameEventSystem.moonTexture != null) {
            textureId = ClientGameEventSystem.NO_MOON_TEXTURE;
        }
        original.call(shaderTexture, textureId);
    }

    @ModifyExpressionValue(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"))
    private float renderNeoMoon(float original, @Local(ordinal = 2) Matrix4f matrix4f1, @Local Tesselator tesselator) {
        ResourceLocation moonTexture = ClientGameEventSystem.moonTexture;
        if (moonTexture != null) {
            RenderSystem.setShaderTexture(0, moonTexture);
            BufferBuilder bufferbuilder1 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder1.addVertex(matrix4f1, -20, -100.0F, 20).setUv(0.0F, 0.0F);
            bufferbuilder1.addVertex(matrix4f1, 20, -100.0F, 20).setUv(1.0F, 0.0F);
            bufferbuilder1.addVertex(matrix4f1, 20, -100.0F, -20).setUv(1.0F, 1.0F);
            bufferbuilder1.addVertex(matrix4f1, -20, -100.0F, -20).setUv(0.0F, 1.0F);
            BufferUploader.drawWithShader(bufferbuilder1.buildOrThrow());
        }
        return original;
    }
}
