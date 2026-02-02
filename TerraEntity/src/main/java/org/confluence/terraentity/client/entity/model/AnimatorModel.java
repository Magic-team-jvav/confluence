package org.confluence.terraentity.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/**
 * 用于硬编码骨骼，自动对Geo动画和硬编码动画之间进行插值
 */
public abstract class AnimatorModel<T extends GeoEntity> extends GeoNormalModel<T>{

    public AnimatorModel(ResourceLocation path) {
        super(path);
    }

    public AnimatorModel(ResourceLocation path, boolean turnsHead) {
        super(path, turnsHead);
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        float partialTick = animationState.getPartialTick();
        customAnimations(animatable, instanceId, animationState, partialTick);
    }

    /**
     * 初始化骨骼动画控制器，获取需要的GeoBone骨骼并初始化Animator
     */
    public abstract void initBoneAnimators(T animatable, BakedGeoModel model);

    public abstract void customAnimations(T animatable, long instanceId, AnimationState<T> animationState,  float partialTick);


}
