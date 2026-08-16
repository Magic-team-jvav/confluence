package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

/// 资源路径由调用方显式提供的 GeckoLib 模型。
///
/// <p>普通 {@link GeoNormalModel} 会按约定从一个基础路径推导模型、纹理和动画；部分迁移实体
/// 不遵循该目录约定，或者多个实体需要共享同一份资源，此类用于保留这些明确映射。动画允许为
/// {@code null}，适用于完全由代码姿态驱动或静态显示的模型。</p>
public class ExplicitGeoModel<T extends GeoEntity> extends GeoNormalModel<T> {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final @Nullable ResourceLocation animation;

    public ExplicitGeoModel(ResourceLocation model, ResourceLocation texture,
                            @Nullable ResourceLocation animation) {
        super(model, false);
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return texture;
    }

    @Override
    public @Nullable ResourceLocation getAnimationResource(T animatable) {
        return animation;
    }
}
