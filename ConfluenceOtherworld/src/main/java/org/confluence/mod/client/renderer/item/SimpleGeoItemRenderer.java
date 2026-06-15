package org.confluence.mod.client.renderer.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.List;

public class SimpleGeoItemRenderer<T extends Item & GeoAnimatable> implements IClientItemExtensions {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final @Nullable ResourceLocation animation;
    private final boolean isGun;
    private GeoItemRenderer<T> renderer;

    /**
     * 通用 GeoItem 渲染器
     */
    public SimpleGeoItemRenderer(ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
        this.isGun = false;
    }

    /**
     * 枪械专用渲染器（带 fire bones 隐藏逻辑）
     */
    public SimpleGeoItemRenderer(DefaultedItemGeoModel<T> gunItemModel) {
        this.model = gunItemModel.getModelResource(null);
        this.texture = gunItemModel.getTextureResource(null);
        this.animation = gunItemModel.getAnimationResource(null);
        this.isGun = true;
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (renderer == null) {
            GeoModel<T> geoModel = new GeoModel<>() {
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

                @Override
                public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
                    if (isGun) {
                        boolean firing = isFiring(animationState);
                        List<String> fireBones = List.of("Fire", "Fire1", "Fire2", "Fire3");
                        for (String boneName : fireBones) {
                            CoreGeoBone bone = getAnimationProcessor().getBone(boneName);
                            if (bone != null) {
                                bone.setHidden(!firing);
                            }
                        }
                    }
                    super.setCustomAnimations(animatable, instanceId, animationState);
                }

                private boolean isFiring(AnimationState<T> animationState) {
                    AnimationController<T> controller = animationState.getController();
                    AnimationController.State state = controller.getAnimationState();
                    if (state != AnimationController.State.STOPPED) {
                        AnimationProcessor.QueuedAnimation currentAnimation = controller.getCurrentAnimation();
                        if (currentAnimation != null && "fire".equals(currentAnimation.animation().name())) {
                            return true;
                        }
                    }
                    return false;
                }
            };

            this.renderer = new GeoItemRenderer<>(geoModel);
        }
        return renderer;
    }
}
