package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.function.Function;

/// 共用同一套几何模型与动画、仅按实体同步状态切换纹理的模型。
///
/// <p>变种判定由实体负责同步，模型只负责选择纹理。这样服务端行为、存档数据和客户端外观
/// 之间不会出现第二套变种状态，也不需要为每一种双色生物单独编写渲染器。</p>
public final class VariantTextureGeoModel<T extends GeoEntity> extends ExplicitGeoModel<T> {
    private final Function<T, ResourceLocation> textureSelector;

    public VariantTextureGeoModel(ResourceLocation model, ResourceLocation animation, Function<T, ResourceLocation> textureSelector) {
        super(model, null, animation);
        this.textureSelector = textureSelector;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        ResourceLocation texture = textureSelector.apply(animatable);
        if (texture == null) {
            throw new IllegalStateException("Variant texture selector returned null");
        }
        return texture;
    }
}
