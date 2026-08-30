package org.confluence.mod.mixin.client.renderer.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.common.data.saved.BrushData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {
    /// @see org.confluence.mod.mixin.integration.sodium.BlockRendererMixin
    @WrapOperation(method = "putQuadData", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;[FFFFF[IIZ)V"))
    private void putColor(VertexConsumer instance, PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue, float alpha, int[] lightmap, int packedOverlay, boolean readAlpha, Operation<Void> original, @Local(argsOnly = true) BlockPos pPos) {
        int color = LocalBrushData.getColor(pPos, quad.getDirection());
        if (color == BrushData.EMPTY_COLOR || color == BrushData.ECHO_COLOR) {
            original.call(instance, pose, quad, brightness, red, green, blue, alpha, lightmap, packedOverlay, readAlpha);
        } else if (color == BrushData.ILLUMINANT_COLOR) {
            original.call(instance, pose, quad, brightness, red, green, blue, alpha, LibRenderUtils.FULL_BRIGHT, packedOverlay, readAlpha);
        } else if (color == BrushData.NEGATIVE_COLOR) {
            if (red != 1.0F || green != 1.0F || blue != 1.0F) {
                original.call(instance, pose, quad, brightness, 1.0F - red, 1.0F - green, 1.0F - blue, alpha, lightmap, packedOverlay, readAlpha);
            } else {
                original.call(instance, pose, quad, brightness, red, green, blue, alpha, lightmap, packedOverlay, readAlpha);
            }
        } else {
            float r = (float) (color >> 16 & 255) * LibMathUtils.INV_255;
            float g = (float) (color >> 8 & 255) * LibMathUtils.INV_255;
            float b = (float) (color & 255) * LibMathUtils.INV_255;
            original.call(instance, pose, quad, brightness, r, g, b, alpha, lightmap, packedOverlay, readAlpha);
        }
    }
}
