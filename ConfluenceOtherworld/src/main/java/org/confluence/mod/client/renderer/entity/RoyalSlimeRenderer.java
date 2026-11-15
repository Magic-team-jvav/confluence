package org.confluence.mod.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

/// 身体、纹理和动画根据同步生命值切换到同一阶段。
public class RoyalSlimeRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    public RoyalSlimeRenderer(EntityRendererProvider.Context context, String name, float modelScale) {
        super(context, new GeoModel<>() {
            private String body(T queen) {
                return name;
            }

            @Override
            public ResourceLocation getModelResource(T queen) {
                return Confluence.asResource("geo/entity/boss/" + body(queen) + ".geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(T queen) {
                return Confluence.asResource("textures/entity/queen_slime/" + body(queen) + ".png");
            }

            @Override
            public ResourceLocation getAnimationResource(T queen) {
                return Confluence.asResource("animations/entity/boss/" + body(queen) + ".animation.json");
            }
        }, false, modelScale, 0.0F);
    }
}
