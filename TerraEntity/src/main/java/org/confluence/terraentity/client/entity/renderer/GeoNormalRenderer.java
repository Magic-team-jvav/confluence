package org.confluence.terraentity.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.terraentity.client.entity.model.GeoNormalModel;
import org.confluence.terraentity.entity.monster.AbstractMonster;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GeoNormalRenderer<T extends AbstractMonster> extends GeoEntityRenderer<T> {
    boolean ifRotX = false;
    float scale;
    float offsetY;
    public GeoNormalRenderer(EntityRendererProvider.Context renderManager, String name, boolean ifRotX) {
        this(renderManager, name, ifRotX,1,0);
    }
    public GeoNormalRenderer(EntityRendererProvider.Context renderManager, String name, boolean ifRotX,float scale,float offsetY) {
        super(renderManager, new GeoNormalModel<>(name));
        this.ifRotX = ifRotX;
        this.scale=scale;
        this.offsetY=offsetY;
    }
    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        poseStack.scale(scale, scale, scale);
        poseStack.translate(0, offsetY, 0);
        if(ifRotX) {
            double rad = animatable.yBodyRot * Math.PI / 180;
            poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(rad), 0, (float) Math.sin(rad))).rotationDegrees(animatable.xRotO));
            poseStack.translate(0, 0.5, 0);
        }

    }

}
