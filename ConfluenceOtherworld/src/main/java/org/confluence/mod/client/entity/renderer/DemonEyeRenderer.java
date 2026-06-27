package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.client.entity.model.DemonEyeGeoModel;
import org.confluence.mod.common.entity.monster.DemonEye;

public class DemonEyeRenderer extends GeoNormalRenderer<DemonEye> {

    public DemonEyeRenderer(EntityRendererProvider.Context context) {
        super(context, new DemonEyeGeoModel(), 1.0F, 0.0F);
        this.shadowRadius = 0.5F;
    }

    @Override
    public void preRender(PoseStack poseStack, DemonEye eye, software.bernie.geckolib.cache.object.BakedGeoModel model, net.minecraft.client.renderer.MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = eye.getVariant().scale();
        poseStack.scale(scale, scale, scale);
        double rad = Mth.lerp(partialTick, eye.yBodyRotO, eye.yBodyRot) * Math.PI / 180;
        poseStack.mulPose(Axis.YP.rotationDegrees(-(float) Math.toDegrees(rad)));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, eye.xRotO, eye.getXRot())));
        super.preRender(poseStack, eye, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
