package org.confluence.terraentity.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;

/**
 * 图片路径装饰器：在不改变其他功能的情况下，改变模型的贴图路径，对于 复用某个模型 但贴图不同的 不同id的 实体非常有用
 */
public class GeoModelTextureDecoration<T extends GeoEntity> extends GeoNormalModel<T> {

    ResourceLocation texture;
    public GeoModelTextureDecoration(GeoNormalModel<T> model, ResourceLocation basePath) {
        super(model);
        this.texture = this.buildFormattedTexturePath(basePath);
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return this.texture;
    }
}