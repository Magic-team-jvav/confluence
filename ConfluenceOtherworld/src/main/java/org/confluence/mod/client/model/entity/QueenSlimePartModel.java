package org.confluence.mod.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

// 女皇仆从和凝胶共用目录结构，具体资源由注册时指定。
public class QueenSlimePartModel<T extends GeoEntity> extends GeoModel<T> {
    private final String name;

    public QueenSlimePartModel(String name) {
        this.name = name;
    }

    @Override
    public ResourceLocation getModelResource(T entity) {
        return Confluence.asResource("geo/entity/boss/" + name + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T entity) {
        return Confluence.asResource("textures/entity/queen_slime/" + name + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T entity) {
        return Confluence.asResource("animations/entity/boss/" + name + ".animation.json");
    }
}
