package org.confluence.mod.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.client.model.block.WeatherVaneBlockModel;
import org.confluence.mod.common.block.functional.WeatherVaneBlock;
import org.joml.Quaternionf;

public class WeatherVaneBlockRenderer implements BlockEntityRenderer<WeatherVaneBlock.BEntity> {
    private final WeatherVaneBlockModel model;

    public WeatherVaneBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new WeatherVaneBlockModel(context.bakeLayer(WeatherVaneBlockModel.LAYER_LOCATION));
    }

    @Override
    public void render(WeatherVaneBlock.BEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.0F, 0.5F);
        Quaternionf rotation = Axis.YP.rotation(Mth.lerp(partialTick, entity.rotationO, entity.rotation)).rotateX(Mth.HALF_PI);
        poseStack.mulPose(rotation.rotateZ(Mth.lerp(partialTick, entity.shakeO, entity.shake)));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        model.render(poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
