package org.confluence.mod.client.renderer.item.gun;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.List;

public class SimpleGeoItemRenderer<T extends Item & GeoAnimatable> implements IClientItemExtensions {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;
    private GunRenderer<T> renderer;

    public SimpleGeoItemRenderer(ResourceLocation model, ResourceLocation texture, ResourceLocation animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }

    public SimpleGeoItemRenderer(DefaultedItemGeoModel<T> gunItemModel) {
        this.model = gunItemModel.getModelResource(null);
        this.texture = gunItemModel.getTextureResource(null);
        this.animation = gunItemModel.getAnimationResource(null);
    }

    @Override
    public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (renderer == null) {
            this.renderer = new GunRenderer<>(new GeoModel<>() {
                @Override
                public ResourceLocation getModelResource(T animatable) {
                    return model;
                }

                @Override
                public ResourceLocation getTextureResource(T animatable) {
                    return texture;
                }

                @Override
                public ResourceLocation getAnimationResource(T animatable) {
                    return animation;
                }

                @Override
                public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
                    boolean isFiring = this.isFiring(animationState);

                    List<String> fireBones = List.of("Fire", "Fire1", "Fire2", "Fire3");
                    for (String boneName : fireBones) {
                        CoreGeoBone bone = getAnimationProcessor().getBone(boneName);
                        if (bone != null) {
                            bone.setHidden(!isFiring);
                        }
                    }

                    super.setCustomAnimations(animatable, instanceId, animationState);
                }

                private boolean isFiring(AnimationState<T> animationState) {
                    AnimationController<T> controller = animationState.getController();
                    AnimationController.State state = controller.getAnimationState();

                    boolean isFiring = false;
                    if (state != AnimationController.State.STOPPED) {
                        AnimationProcessor.QueuedAnimation currentAnimation = controller.getCurrentAnimation();
                        if (currentAnimation != null && "fire".equals(currentAnimation.animation().name())) {
                            isFiring = true;
                        }
                    }
                    return isFiring;
                }
            });
        }
        return renderer;
    }
}
