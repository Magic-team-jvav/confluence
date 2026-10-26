package org.confluence.mod.mixin.integration.iris;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.confluence.mod.client.summoner.AttachmentEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {"net.irisshaders.iris.shadows.ShadowRenderer", "net.coderbot.iris.shadows.ShadowRenderer"}, remap = false)
public class ShadowRendererMixin {

    @Inject(method = "renderShadows", at = @At("HEAD"), remap = false)
    private void confluence$beginShadows(CallbackInfo ci) {
        AttachmentEntityRenderDispatcher.setShadowPass(true);
    }

    @Inject(method = "renderShadows", at = @At("RETURN"), remap = false)
    private void confluence$endShadows(CallbackInfo ci) {
        AttachmentEntityRenderDispatcher.setShadowPass(false);
    }

    @Inject(method = "renderEntities", at = @At("RETURN"), remap = false)
    private void confluence$renderSummonerShadows(@Coerce Object levelRenderer, EntityRenderDispatcher dispatcher, MultiBufferSource.BufferSource buffers, PoseStack poseStack, float partialTick, Frustum frustum, double cameraX, double cameraY, double cameraZ, CallbackInfoReturnable<Integer> cir) {
        AttachmentEntityRenderDispatcher.renderShadows(Minecraft.getInstance().level, poseStack, buffers, partialTick, cameraX, cameraY, cameraZ);
    }
}
