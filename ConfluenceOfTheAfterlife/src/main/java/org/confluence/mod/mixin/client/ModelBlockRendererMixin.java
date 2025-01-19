package org.confluence.mod.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import org.confluence.mod.client.textures.LocalBrushData;
import org.confluence.mod.common.data.saved.BrushData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {
    @WrapOperation(method = "putQuadData", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFFF[IIZ)V"))
    private void putColor(VertexConsumer instance, PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue, float alpha, int[] lightmap, int packedOverlay, boolean readAlpha, Operation<Void> original, @Local(argsOnly = true) BlockPos pPos) {
        int color = LocalBrushData.getColor(pPos, quad.getDirection());
        if (color == BrushData.EMPTY_COLOR || color == BrushData.NEGATIVE_COLOR) {
            original.call(instance, pose, quad, brightness, red, green, blue, alpha, lightmap, packedOverlay, readAlpha);
        } else if (color == BrushData.ILLUMINANT_COLOR) {
            original.call(instance, pose, quad, brightness, red, green, blue, alpha, new int[]{0xF000F0, 0xF000F0, 0xF000F0, 0xF000F0}, packedOverlay, readAlpha);
        } else {
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            original.call(instance, pose, quad, brightness, r, g, b, alpha, lightmap, packedOverlay, readAlpha);
        }
    }
}
