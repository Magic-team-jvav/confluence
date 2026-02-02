package org.confluence.terraentity.client.entity.renderer.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.terraentity.client.entity.model.DemonEyeModel;
import org.confluence.terraentity.entity.monster.demoneye.DemonEye;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DemonEyeRenderer extends GeoEntityRenderer<DemonEye> {
    public DemonEyeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DemonEyeModel());
    }

    @Override
    public void preRender(PoseStack poseStack, DemonEye animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.scale(1.55f, 1.55f, 1.55f);
        double rad = animatable.yBodyRot * Math.PI / 180;
        poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(rad), 0, (float) Math.sin(rad))).rotationDegrees(-animatable.xRotO));
    }

    @Override
    protected float getDeathMaxRotation(DemonEye animatable){
        return 0;
    }
}
