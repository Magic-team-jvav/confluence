package org.confluence.mod.client.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.entity.model.GeoNormalModel;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GeoNormalRenderer<T extends Entity & GeoEntity> extends GeoEntityRenderer<T> {
    protected float motionAnimThreshold = 0.01F;

    public GeoNormalRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        this(context, new GeoNormalModel<>(path));
    }

    public GeoNormalRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        this.shadowRadius = 0.25F;
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
