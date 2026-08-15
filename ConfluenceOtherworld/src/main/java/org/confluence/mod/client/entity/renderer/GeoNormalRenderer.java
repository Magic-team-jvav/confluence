package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GeoNormalRenderer<T extends Entity & GeoEntity> extends GeoEntityRenderer<T> {
    protected final boolean rotateAlongPitch;
    protected final float modelScale;
    protected final float modelOffsetY;
    protected float motionAnimThreshold = 0.01F;

    public GeoNormalRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        this(context, path, false, 1.0F, 0.0F);
    }

    public GeoNormalRenderer(
            EntityRendererProvider.Context context,
            ResourceLocation path,
            boolean rotateAlongPitch,
            float modelScale,
            float modelOffsetY) {
        this(context, new GeoNormalModel<>(path), rotateAlongPitch, modelScale, modelOffsetY);
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        this(context, model, false, 1.0F, 0.0F);
    }

    public GeoNormalRenderer(
            EntityRendererProvider.Context context,
            GeoModel<T> model,
            boolean rotateAlongPitch,
            float modelScale,
            float modelOffsetY) {
        super(context, model);
        this.rotateAlongPitch = rotateAlongPitch;
        this.modelScale = modelScale;
        this.modelOffsetY = modelOffsetY;
        this.shadowRadius = 0.25F;
    }

    /**
     * 应用通用 Geo 渲染参数。
     *
     * <p>这里仅处理模型缩放、垂直偏移和沿俯仰方向旋转。它们只影响客户端显示，
     * 不参与服务端实体碰撞箱、移动路径或伤害判定。</p>
     */
    @Override
    public void preRender(
            PoseStack poseStack,
            T animatable,
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
        poseStack.scale(modelScale, modelScale, modelScale);
        poseStack.translate(0.0F, modelOffsetY, 0.0F);
        if (rotateAlongPitch) {
            double yaw = Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot())
                    * Mth.DEG_TO_RAD;
            Vector3f axis = new Vector3f((float) Math.cos(yaw), 0.0F, (float) Math.sin(yaw));
            poseStack.mulPose(Axis.of(axis).rotationDegrees(
                    Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
        }
        adjustPose(poseStack, animatable, model, partialTick);
        super.preRender(
                poseStack, animatable, model, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected void adjustPose(
            PoseStack poseStack,
            T animatable,
            BakedGeoModel model,
            float partialTick) {}

    @Override
    public float getMotionAnimThreshold(T animatable) {
        return motionAnimThreshold;
    }

    @Override
    public GeoNormalRenderer<T> withScale(float scale) {
        super.withScale(scale);
        return this;
    }

    public GeoNormalRenderer<T> setMotionAnimThreshold(float threshold) {
        this.motionAnimThreshold = threshold;
        return this;
    }

    public GeoNormalRenderer<T> setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        return this;
    }
}
