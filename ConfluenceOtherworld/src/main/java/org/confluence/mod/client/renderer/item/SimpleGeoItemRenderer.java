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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.mod.client.animation.GunCameraAnimation;
import org.confluence.mod.common.item.gun.BaseGun;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.state.BoneSnapshot;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.List;

public class SimpleGeoItemRenderer<T extends Item & GeoAnimatable> implements IClientItemExtensions {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final @Nullable ResourceLocation animation;
    private final boolean gunRenderer;
    private BlockEntityWithoutLevelRenderer renderer;
    private GunRenderer<T> activeGunRenderer;

    /**
     * 通用 Geo 物品渲染器。
     */
    public SimpleGeoItemRenderer(ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
        this.gunRenderer = false;
    }

    /** 枪械专用渲染器，对齐 1.21 TerraGuns 的注册路径。 */
    public SimpleGeoItemRenderer(DefaultedItemGeoModel<T> gunItemModel) {
        this.model = gunItemModel.getModelResource(null);
        this.texture = gunItemModel.getTextureResource(null);
        this.animation = gunItemModel.getAnimationResource(null);
        this.gunRenderer = true;
    }

    @Override
    public boolean applyForgeHandTransform(
            PoseStack poseStack,
            LocalPlayer player,
            HumanoidArm arm,
            ItemStack itemStack,
            float partialTick,
            float equippedProgress,
            float swingProgress
    ) {
        if (!gunRenderer || !(itemStack.getItem() instanceof BaseGun)) {
            return false;
        }

        // 枪械第一人称姿态由 GunRenderer 统一接管。Forge 调用这里时原版视角摆动
        // 已经进入矩阵栈，因此只撤销视角摆动；模型位置、相机和手臂渲染继续交给枪械渲染器。
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
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
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

                if (firstPerson && animatable instanceof BaseGun baseGun
                        && baseGun.isCameraAnimationPlaying(instanceId)) {
                    GunCameraAnimation.capture(getAnimationProcessor().getBone("camera"));
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
                    CoreGeoBone bone = getAnimationProcessor().getBone(boneName);
                    if (bone != null) {
                        bone.setHidden(hidden);
                    }
                }
            }

            private void resetToStaticPose() {
                for (CoreGeoBone coreBone : getAnimationProcessor().getRegisteredBones()) {
                    if (!(coreBone instanceof GeoBone bone)) {
                        continue;
                    }
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
