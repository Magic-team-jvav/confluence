package org.confluence.mod.client.entity.model;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BowItem;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.util.RenderUtils;

/// 给骷髅类 Geo 模型复用原版行走、头部与近战动画，并保留 1.21 侧的拉弓姿势。
/// 所有姿势先在 ModelPart 坐标系内计算，最后统一转换 X、Y 旋转方向，避免攻击状态切换时反向。
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
        VanillaHumanoidGeoModel.applyBowPose(vanillaModel, entity, partialTick);
        if ((entity.isAggressive() || vanillaModel.attackTime > 0) && !(entity.getMainHandItem().getItem() instanceof BowItem)) {
            // 原版 SkeletonModel 的近战姿势；不要求近战实体实现远程攻击接口。
            float swing = Mth.sin(vanillaModel.attackTime * Mth.PI);
            float recovery = Mth.sin((1 - (1 - vanillaModel.attackTime) * (1 - vanillaModel.attackTime)) * Mth.PI);
            vanillaModel.rightArm.zRot = 0;
            vanillaModel.leftArm.zRot = 0;
            vanillaModel.rightArm.yRot = -(0.1F - swing * 0.6F);
            vanillaModel.leftArm.yRot = 0.1F - swing * 0.6F;
            vanillaModel.rightArm.xRot = -Mth.HALF_PI - swing * 1.2F + recovery * 0.4F;
            vanillaModel.leftArm.xRot = vanillaModel.rightArm.xRot;
            AnimationUtils.bobArms(vanillaModel.rightArm, vanillaModel.leftArm, entity.tickCount + partialTick);
        }
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

    private void copyRegisteredBones() {
        for (CoreGeoBone bone : getAnimationProcessor().getRegisteredBones()) {
            ModelPart source = sourcePart(bone.getName());
            if (source != null) {
                RenderUtils.matchModelPartRot(source, bone);
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
}
