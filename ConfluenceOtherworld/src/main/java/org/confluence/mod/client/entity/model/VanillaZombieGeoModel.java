package org.confluence.mod.client.entity.model;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

/**
 * 给僵尸类 Geo 模型复用原版僵尸的行走、转头和前伸手臂动作。
 * 这个实现和普通人形族分开，避免把僵尸特有的攻击姿态施加到其他双足怪物。
 */
public class VanillaZombieGeoModel<T extends Mob & GeoEntity> extends GeoNormalModel<T> {
    private static final String HEAD = "Vhead";
    private static final String LEFT_ARM = "Vleft_arm";
    private static final String RIGHT_ARM = "Vright_arm";
    private static final String LEFT_LEG = "Vleft_leg";
    private static final String RIGHT_LEG = "Vright_leg";

    private final HumanoidModel<T> vanillaModel;

    public VanillaZombieGeoModel(EntityRendererProvider.Context context, ResourceLocation path) {
        super(path, false);
        vanillaModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE));
    }

    @Override
    public void setCustomAnimations(T entity, long instanceId, AnimationState<T> state) {
        float partialTick = state.getPartialTick();
        float age = entity.tickCount + partialTick;
        prepareModel(entity, partialTick);
        EntityModelData look = state.getData(DataTickets.ENTITY_MODEL_DATA);
        vanillaModel.prepareMobModel(entity, state.getLimbSwing(), state.getLimbSwingAmount(), partialTick);
        vanillaModel.setupAnim(entity, state.getLimbSwing(), state.getLimbSwingAmount(), age, look.netHeadYaw(), look.headPitch());
        AnimationUtils.animateZombieArms(vanillaModel.leftArm, vanillaModel.rightArm, entity.isAggressive() || entity.isSprinting(), vanillaModel.attackTime, age);
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
}
