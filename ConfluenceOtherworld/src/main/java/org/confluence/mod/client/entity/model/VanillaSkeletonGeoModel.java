package org.confluence.mod.client.entity.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

/// 给骷髅类 Geo 模型复用原版行走和头部动画，并按 1.21 侧的骨骼公式处理拉弓姿势。
/// 这里不直接使用 {@link HumanoidModel.ArmPose#BOW_AND_ARROW}，因为 Java 模型的完整手臂姿势写回
/// 当前 Geo 资源后容易出现手臂向后翻的问题；拉弓时只覆盖两条手臂的目标角度。
public final class VanillaSkeletonGeoModel<T extends Mob & GeoEntity> extends GeoNormalModel<T> {
    private static final String HEAD = "Vhead";
    private static final String LEFT_ARM = "Vleft_arm";
    private static final String RIGHT_ARM = "Vright_arm";
    private static final String LEFT_LEG = "Vleft_leg";
    private static final String RIGHT_LEG = "Vright_leg";

    private final HumanoidModel<T> vanillaModel;

    public VanillaSkeletonGeoModel(EntityRendererProvider.Context context, ResourceLocation path) {
        super(path, false);
        vanillaModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.SKELETON));
    }

    @Override
    public void setCustomAnimations(T entity, long instanceId, AnimationState<T> state) {
        float partialTick = state.getPartialTick();
        prepareModel(entity, partialTick);
        EntityModelData look = state.getData(DataTickets.ENTITY_MODEL_DATA);
        vanillaModel.prepareMobModel(entity, state.getLimbSwing(), state.getLimbSwingAmount(), partialTick);
        vanillaModel.setupAnim(entity, state.getLimbSwing(), state.getLimbSwingAmount(), entity.tickCount + partialTick, look.netHeadYaw(), look.headPitch());
        applyBowPose(entity, partialTick);
        copyRegisteredBones();
    }

    private void prepareModel(T entity, float partialTick) {
        vanillaModel.attackTime = entity.getAttackAnim(partialTick);
        vanillaModel.riding = entity.isPassenger();
        vanillaModel.young = entity.isBaby();
        vanillaModel.crouching = entity.isCrouching();
        vanillaModel.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        vanillaModel.rightArmPose = HumanoidModel.ArmPose.EMPTY;
    }

    private void applyBowPose(T entity, float partialTick) {
        if (!entity.isUsingItem() || !isHoldingBow(entity)) return;
        float progress = Mth.clamp((entity.getTicksUsingItem() + partialTick) / 5.0F, 0.0F, 1.0F);
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) * Mth.DEG_TO_RAD;
        float headOffset = Mth.lerp(partialTick, entity.yBodyRotO - entity.yHeadRotO, entity.yBodyRot - entity.yHeadRot) * Mth.DEG_TO_RAD;
        vanillaModel.rightArm.xRot = Mth.lerp(progress, 0.0F, 1.5F - pitch);
        vanillaModel.rightArm.yRot = Mth.lerp(progress, 0.0F, headOffset);
        vanillaModel.rightArm.zRot = 0.0F;
        vanillaModel.leftArm.xRot = Mth.lerp(progress, 0.0F, 1.3F - pitch);
        vanillaModel.leftArm.yRot = Mth.lerp(progress, 0.0F, Math.max(headOffset - 0.5F, -1.4F));
        vanillaModel.leftArm.zRot = 0.0F;
    }

    private void copyRegisteredBones() {
        for (CoreGeoBone bone : getAnimationProcessor().getRegisteredBones()) {
            ModelPart source = sourcePart(bone.getName());
            if (source != null) {
                bone.setRotX(source.xRot);
                bone.setRotY(source.yRot);
                bone.setRotZ(source.zRot);
            }
        }
    }

    private ModelPart sourcePart(String name) {
        if (name.startsWith(HEAD)) return vanillaModel.head;
        if (name.startsWith(LEFT_ARM)) return vanillaModel.leftArm;
        if (name.startsWith(RIGHT_ARM)) return vanillaModel.rightArm;
        if (name.startsWith(LEFT_LEG)) return vanillaModel.leftLeg;
        if (name.startsWith(RIGHT_LEG)) return vanillaModel.rightLeg;
        return null;
    }

    private static boolean isHoldingBow(Mob entity) {
        return entity.getMainHandItem().is(Items.BOW) || entity.getOffhandItem().is(Items.BOW);
    }
}
