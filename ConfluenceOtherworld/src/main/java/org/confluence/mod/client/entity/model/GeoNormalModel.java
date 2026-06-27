package org.confluence.mod.client.entity.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class GeoNormalModel<T extends GeoEntity> extends DefaultedEntityGeoModel<T> {
    protected GeoBone head;
    protected final ResourceLocation path;

    public GeoNormalModel(ResourceLocation path) {
        this(path, true);
    }

    public GeoNormalModel(ResourceLocation path, boolean turnsHead) {
        super(path, turnsHead ? "Head" : null);
        this.path = path;
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        if (this.headBone != null) {
            if (this.head == null) {
                this.head = getHead();
            }
            if (this.head != null) {
                EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
                this.head.setRotX(entityData.headPitch() * 0.017453292F);
                this.head.setRotY(entityData.netHeadYaw() * 0.017453292F);
            }
        }
    }

    protected @Nullable GeoBone getHead() {
        return getAnimationProcessor().getBone(getHeadName());
    }

    protected @Nullable String getHeadName() {
        return headBone;
    }

    public GeoNormalModel<T> setHeadName(String headName) {
        this.headBone = headName;
        return this;
    }
}
