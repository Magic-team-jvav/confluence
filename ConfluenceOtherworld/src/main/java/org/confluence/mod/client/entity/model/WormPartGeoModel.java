package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.monster.WormSegment;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

// 实体类型对应固定的资源，节段角色只决定使用身体还是尾部。
public class WormPartGeoModel<T extends GeoEntity & WormSegment> extends GeoNormalModel<T> {
    private final ResourceLocation bodyModel;
    private final ResourceLocation bodyTexture;
    private final ResourceLocation tailModel;
    private final ResourceLocation tailTexture;

    public WormPartGeoModel(ResourceLocation bodyModel, ResourceLocation bodyTexture, ResourceLocation tailModel, ResourceLocation tailTexture) {
        super(bodyModel, false);
        this.bodyModel = bodyModel;
        this.bodyTexture = bodyTexture;
        this.tailModel = tailModel;
        this.tailTexture = tailTexture;
    }

    @Override
    public ResourceLocation getModelResource(T segment) {
        return segment.isTail() ? tailModel : bodyModel;
    }

    @Override
    public ResourceLocation getTextureResource(T segment) {
        return segment.isTail() ? tailTexture : bodyTexture;
    }

    @Override
    public @Nullable ResourceLocation getAnimationResource(T segment) {
        return null;
    }
}
