package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import org.confluence.mod.common.entity.PartHitTarget;
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

    public GeoNormalRenderer(EntityRendererProvider.Context context, ResourceLocation path, boolean rotateAlongPitch, float modelScale, float modelOffsetY) {
        this(context, new GeoNormalModel<>(path), rotateAlongPitch, modelScale, modelOffsetY);
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        this(context, model, false, 1.0F, 0.0F);
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, GeoModel<T> model, boolean rotateAlongPitch, float modelScale, float modelOffsetY) {
        super(context, model);
        this.rotateAlongPitch = rotateAlongPitch;
        this.modelScale = modelScale;
        this.modelOffsetY = modelOffsetY;
        this.shadowRadius = 0.25F;
    }

    /// 应用通用 Geo 渲染参数。
    ///
    /// 这里仅处理模型缩放、垂直偏移和沿俯仰方向旋转。它们只影响客户端显示，
    /// 不参与服务端实体碰撞箱、移动路径或伤害判定。
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
        float effectiveScale = getEffectiveModelScale(animatable) * getEncounterScale(animatable);
        poseStack.scale(effectiveScale, effectiveScale, effectiveScale);
        poseStack.translate(0.0F, modelOffsetY, 0.0F);
        if (rotateAlongPitch) {
            // 实体渲染原点位于碰撞箱脚底。直接在此处俯仰会把长模型绕脚底甩出链条，
            // 接近 90° 时尤其明显；先移到实际碰撞箱中心，旋转后再移回。
            float pivotY = animatable.getBbHeight() * 0.5F / effectiveScale - modelOffsetY;
            poseStack.translate(0.0F, pivotY, 0.0F);
            double yaw = Mth.rotLerp(partialTick, animatable.yRotO, animatable.getYRot())
                    * Mth.DEG_TO_RAD;
            Vector3f axis = new Vector3f((float) Math.cos(yaw), 0.0F, (float) Math.sin(yaw));
            poseStack.mulPose(Axis.of(axis).rotationDegrees(Mth.rotLerp(partialTick, animatable.xRotO, animatable.getXRot())));
            poseStack.translate(0.0F, -pivotY, 0.0F);
        }
        adjustPose(poseStack, animatable, model, partialTick);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected void adjustPose(PoseStack poseStack, T animatable, BakedGeoModel model, float partialTick) {}

    /// 返回当前实体实际使用的模型缩放。共用渲染器可按资源家族覆盖，旋转中心必须读取同一值。
    protected float getEffectiveModelScale(T animatable) {
        return modelScale;
    }

    /// 非生物部件没有自己的 scale 属性，渲染时继承遭遇主体的同步倍率。
    private float getEncounterScale(T animatable) {
        if (animatable instanceof LivingEntity || !(animatable instanceof PartHitTarget part))
            return 1.0F;
        Entity owner = part.encounterOwner();
        return owner instanceof LivingEntity living ? living.getScale() : 1.0F;
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        if (!(animatable instanceof LivingEntity)) {
            rotationYaw = Mth.rotLerp(partialTick, animatable.yRotO, animatable.getYRot());
        }
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

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
