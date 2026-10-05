package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.api.animation.first_person.CameraAnimation;
import org.confluence.mod.common.item.gun.BaseGun;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.List;

public class SimpleGeoItemRenderer<T extends Item & GeoAnimatable> implements GeoRenderProvider, IClientItemExtensions {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final @Nullable ResourceLocation animation;
    private final boolean gunRenderer;
    private GeoItemRenderer<T> renderer;
    private GunRenderer<T> activeGunRenderer;

    public SimpleGeoItemRenderer(ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
        this.gunRenderer = false;
    }

    public SimpleGeoItemRenderer(DefaultedItemGeoModel<T> gunItemModel) {
        this.model = gunItemModel.getModelResource(null);
        this.texture = gunItemModel.getTextureResource(null);
        this.animation = gunItemModel.getAnimationResource(null);
        this.gunRenderer = true;
    }

    ///
    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemStack, float partialTick, float equippedProgress, float swingProgress) {
        if (!gunRenderer) return false;
        removeVanillaViewBobbing(poseStack, player, partialTick);
        return true;
    }

    private static void removeVanillaViewBobbing(PoseStack poseStack, LocalPlayer player, float partialTick) {
        float xBob = Mth.lerp(partialTick, player.xBobO, player.xBob);
        float yBob = Mth.lerp(partialTick, player.yBobO, player.yBob);
        float xRotation = player.getViewXRot(partialTick) - xBob;
        float yRotation = player.getViewYRot(partialTick) - yBob;
        poseStack.mulPose(Axis.XP.rotationDegrees(-xRotation * 0.1F));
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRotation * 0.1F));
    }

    @Override
    public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
        return createRenderer();
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return createRenderer();
    }

    private BlockEntityWithoutLevelRenderer createRenderer() {
        if (renderer == null) {
            GeoModel<T> geoModel = createModel();
            if (gunRenderer) {
                activeGunRenderer = new GunRenderer<>(geoModel);
                renderer = activeGunRenderer;
            } else {
                renderer = new GeoItemRenderer<>(geoModel);
            }
        }
        return renderer;
    }

    private GeoModel<T> createModel() {
        return new GeoModel<>() {
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
                super.setCustomAnimations(animatable, instanceId, animationState);
                if (!gunRenderer) {
                    return;
                }

                boolean firstPerson = isFirstPersonPerspective();
                if (!firstPerson) {
                    resetToStaticPose();
                }

                boolean firing = firstPerson && isFiring(animatable, instanceId, animationState);
                setHidden(List.of("Fire", "Fire1", "Fire2", "Fire3"), !firing);
                setHidden(List.of("Shell", "shell", "Shell1", "shell1"), !firing);
                setHidden(List.of("lefthand_pos", "righthand_pos"), true);

                if (firstPerson && animatable instanceof BaseGun baseGun && baseGun.isCameraAnimationPlaying(instanceId)) {
                    CameraAnimation.capture(getAnimationProcessor().getBone("camera"));
                }
            }

            private boolean isFirstPersonPerspective() {
                if (activeGunRenderer == null) {
                    return false;
                }
                ItemDisplayContext perspective = activeGunRenderer.getRenderPerspective();
                return perspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                        || perspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            }

            private void setHidden(List<String> boneNames, boolean hidden) {
                for (String boneName : boneNames) {
                    GeoBone bone = getAnimationProcessor().getBone(boneName);
                    if (bone != null) {
                        bone.setHidden(hidden);
                    }
                }
            }

            private void resetToStaticPose() {
                for (GeoBone bone : getAnimationProcessor().getRegisteredBones()) {
                    BoneSnapshot snapshot = bone.getInitialSnapshot();
                    if (snapshot == null) {
                        continue;
                    }

                    bone.setPosX(snapshot.getOffsetX());
                    bone.setPosY(snapshot.getOffsetY());
                    bone.setPosZ(snapshot.getOffsetZ());
                    bone.setRotX(snapshot.getRotX());
                    bone.setRotY(snapshot.getRotY());
                    bone.setRotZ(snapshot.getRotZ());
                    bone.setScaleX(snapshot.getScaleX());
                    bone.setScaleY(snapshot.getScaleY());
                    bone.setScaleZ(snapshot.getScaleZ());
                    bone.resetStateChanges();
                }
            }

            private boolean isFiring(T animatable, long instanceId, AnimationState<T> animationState) {
                if (animatable instanceof BaseGun baseGun && baseGun.isShootAnimationPlaying(instanceId)) {
                    return true;
                }
                AnimationController<T> controller = animationState.getController();
                if (controller.getAnimationState() == AnimationController.State.STOPPED) {
                    return false;
                }
                AnimationProcessor.QueuedAnimation currentAnimation = controller.getCurrentAnimation();
                if (currentAnimation == null) {
                    return false;
                }
                String animationName = currentAnimation.animation().name();
                if (animatable instanceof BaseGun baseGun) {
                    return baseGun.isShootAnimationName(animationName);
                }
                return "fire".equals(animationName) || "shoot".equals(animationName);
            }
        };
    }
}
