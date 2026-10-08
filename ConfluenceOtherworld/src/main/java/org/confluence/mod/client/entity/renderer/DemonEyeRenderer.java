package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.client.entity.model.DemonEyeGeoModel;
import org.confluence.mod.common.entity.monster.DemonEye;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class DemonEyeRenderer extends GeoNormalRenderer<DemonEye> {

    public DemonEyeRenderer(EntityRendererProvider.Context context) {
        super(context, new DemonEyeGeoModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            DemonEye eye,
            BakedGeoModel model,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            int colour) {
        // 1.21 侧恶魔眼模型本身按 1.55 倍显示；1.20 额外保留变种体型差异。
        float scale = 1.55F * eye.getVariant().scale();
        poseStack.scale(scale, scale, scale);
        super.preRender(poseStack, eye, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    protected void applyRotations(DemonEye eye, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        super.applyRotations(eye, poseStack, ageInTicks, rotationYaw, partialTick, scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(-Mth.rotLerp(partialTick, eye.xRotO, eye.getXRot())));
    }

    @Override
    protected float getDeathMaxRotation(DemonEye eye) {
        return 0.0F;
    }
}
