package org.confluence.mod.client.entity.model;

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
 * 给普通双足怪物复用原版人形模型的基础肢体动画。
 * 这个模型只负责通用的头部、手臂和腿部旋转，不处理僵尸攻击、骷髅拉弓或哥布林专属动作。
 * 只有名称以约定前缀开头的骨骼会接收原版旋转，附加装饰骨骼保持资源里写好的姿态。
 */
public final class VanillaHumanoidGeoModel<T extends Mob & GeoEntity> extends GeoNormalModel<T> {
    private static final String HEAD = "Vhead";
    private static final String LEFT_ARM = "Vleft_arm";
    private static final String RIGHT_ARM = "Vright_arm";
    private static final String LEFT_LEG = "Vleft_leg";
    private static final String RIGHT_LEG = "Vright_leg";

    private final HumanoidModel<T> vanillaModel;

    public VanillaHumanoidGeoModel(EntityRendererProvider.Context context, ResourceLocation path) {
        super(path, false);
        vanillaModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER));
    }

    @Override
    public void setCustomAnimations(T entity, long instanceId, AnimationState<T> state) {
        float partialTick = state.getPartialTick();
        prepareModel(entity, partialTick);
        EntityModelData look = state.getData(DataTickets.ENTITY_MODEL_DATA);
        vanillaModel.prepareMobModel(entity, state.getLimbSwing(), state.getLimbSwingAmount(), partialTick);
        vanillaModel.setupAnim(entity, state.getLimbSwing(), state.getLimbSwingAmount(), entity.tickCount + partialTick, look.netHeadYaw(), look.headPitch());
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
