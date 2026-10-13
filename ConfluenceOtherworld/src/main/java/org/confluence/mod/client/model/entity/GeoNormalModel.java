package org.confluence.mod.client.model.entity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import javax.annotation.Nullable;

public class GeoNormalModel<T extends GeoEntity> extends DefaultedEntityGeoModel<T> {
    protected CoreGeoBone head;
    private boolean headPitchEnabled = true;
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
            this.head = getHead();
            if (this.head != null) {
                EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
                if (entityData == null) return;
                if (this.head instanceof GeoBone bone) {
                    var initial = bone.getInitialSnapshot();
                    this.head.setRotX(initial.getRotX() + (headPitchEnabled ? entityData.headPitch() * 0.017453292F : 0.0F));
                    this.head.setRotY(initial.getRotY() + entityData.netHeadYaw() * 0.017453292F);
                }
            }
        }
    }

    protected @Nullable CoreGeoBone getHead() {
        CoreGeoBone bone = getAnimationProcessor().getBone(getHeadName());
        return bone == null && "Head".equals(getHeadName())
                ? getAnimationProcessor().getBone("head") : bone;
    }

    protected @Nullable String getHeadName() {
        return headBone;
    }

    // 整体模型承担俯仰时，头部仅应用相对偏航。
    public void setHeadPitchEnabled(boolean enabled) {
        headPitchEnabled = enabled;
    }

    public GeoNormalModel<T> setHeadName(String headName) {
        this.headBone = headName;
        return this;
    }
}
