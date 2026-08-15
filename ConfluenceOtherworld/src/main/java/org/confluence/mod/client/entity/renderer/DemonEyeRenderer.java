package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.client.entity.model.DemonEyeGeoModel;
import org.confluence.mod.common.entity.monster.DemonEye;
import org.joml.Vector3f;
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
            float red,
            float green,
            float blue,
            float alpha) {
        // 1.21 侧恶魔眼模型本身按 1.55 倍显示；1.20 额外保留变种体型差异。
        float scale = 1.55F * eye.getVariant().scale();
        poseStack.scale(scale, scale, scale);
        // 沿当前朝向的侧轴俯仰，避免飞行和受击时被渲染成横躺在地面上。
        double yaw = Mth.lerp(partialTick, eye.yRotO, eye.getYRot()) * Mth.DEG_TO_RAD;
        Vector3f pitchAxis = new Vector3f((float) Math.cos(yaw), 0.0F, (float) Math.sin(yaw));
        poseStack.mulPose(Axis.of(pitchAxis).rotationDegrees(-Mth.lerp(partialTick, eye.xRotO, eye.getXRot())));
        super.preRender(poseStack, eye, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected float getDeathMaxRotation(DemonEye eye) {
        return 0.0F;
    }
}
