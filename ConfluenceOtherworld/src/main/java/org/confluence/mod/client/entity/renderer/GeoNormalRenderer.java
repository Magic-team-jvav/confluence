package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GeoNormalRenderer<T extends Entity & GeoEntity> extends GeoEntityRenderer<T> {
    protected float scale = 1.0F;
    protected float offsetY;
    protected float motionAnimThreshold = 0.01F;

    public GeoNormalRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        this(context, path, 1.0F, 0.0F);
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, ResourceLocation path, float scale, float offsetY) {
        this(context, new GeoNormalModel<>(path), scale, offsetY);
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, GeoModel<T> model, float scale, float offsetY) {
        super(context, model);
        this.scale = scale;
        this.offsetY = offsetY;
        this.shadowRadius = 0.25F;
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0, offsetY, 0);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public float getMotionAnimThreshold(T animatable) {
        return motionAnimThreshold;
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
