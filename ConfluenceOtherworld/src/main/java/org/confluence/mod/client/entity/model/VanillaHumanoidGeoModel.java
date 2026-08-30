package org.confluence.mod.client.entity.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BowItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

/// 给普通双足怪物复用原版人形模型的基础肢体动画。
/// 这个模型只负责通用的头部、手臂和腿部旋转，不处理僵尸攻击、骷髅拉弓或哥布林专属动作。
/// 只有名称以约定前缀开头的骨骼会接收原版旋转，附加装饰骨骼保持资源里写好的姿态。
public final class VanillaHumanoidGeoModel<T extends Mob & GeoEntity> extends GeoNormalModel<T> {
    private static final String HEAD = "Vhead";
    private static final String LEFT_ARM = "Vleft_arm";
    private static final String RIGHT_ARM = "Vright_arm";
    private static final String LEFT_LEG = "Vleft_leg";
    private static final String RIGHT_LEG = "Vright_leg";

    private final HumanoidModel<T> vanillaModel;
    private final @Nullable ResourceLocation explicitModel;
    private final @Nullable ResourceLocation explicitTexture;
    private final boolean armorBones;
    private final boolean walks;

    public VanillaHumanoidGeoModel(EntityRendererProvider.Context context, ResourceLocation path) {
        super(path, false);
        vanillaModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER));
        explicitModel = null;
        explicitTexture = null;
        armorBones = false;
        walks = true;
    }

    private VanillaHumanoidGeoModel(EntityRendererProvider.Context context, ResourceLocation model,
                                    ResourceLocation texture, boolean walks) {
        super(model, false);
        vanillaModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER));
        explicitModel = model;
        explicitTexture = texture;
        armorBones = true;
        this.walks = walks;
    }

    public static <T extends Mob & GeoEntity> VanillaHumanoidGeoModel<T> armor(
            EntityRendererProvider.Context context, ResourceLocation model, ResourceLocation texture, boolean walks) {
        return new VanillaHumanoidGeoModel<>(context, model, texture, walks);
    }

    @Override
    public ResourceLocation getModelResource(T entity) {
        return explicitModel == null ? super.getModelResource(entity) : explicitModel;
    }

    @Override
    public ResourceLocation getTextureResource(T entity) {
        return explicitTexture == null ? super.getTextureResource(entity) : explicitTexture;
    }

    @Override
    public @Nullable ResourceLocation getAnimationResource(T entity) {
        return explicitModel == null ? super.getAnimationResource(entity) : null;
    }

    @Override
    public void setCustomAnimations(T entity, long instanceId, AnimationState<T> state) {
        float partialTick = state.getPartialTick();
        prepareModel(entity, partialTick);
        EntityModelData look = state.getData(DataTickets.ENTITY_MODEL_DATA);
        float limbSwing = walks ? state.getLimbSwing() : 0.0F;
        float limbSwingAmount = walks ? state.getLimbSwingAmount() : 0.0F;
        vanillaModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        vanillaModel.setupAnim(entity, limbSwing, limbSwingAmount, entity.tickCount + partialTick,
                look.netHeadYaw(), look.headPitch());
        copyRegisteredBones();
    }

    private void prepareModel(T entity, float partialTick) {
        // 不行走的悬浮盔甲模型（当前仅幻灵）对应 1.21 隐藏基础人形模型后
        // 只显示静态盔甲层的效果，不能把接触伤害的 swing 状态映射成挥臂。
        vanillaModel.attackTime = walks ? entity.getAttackAnim(partialTick) : 0.0F;
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
        if (armorBones) {
            if (name.startsWith("Head")) return vanillaModel.head;
            if (name.startsWith("Body") || name.equals("Belt")) return vanillaModel.body;
            if (name.startsWith("LeftArm")) return vanillaModel.leftArm;
            if (name.startsWith("RightArm")) return vanillaModel.rightArm;
            if (name.startsWith("LeftLeg") || name.equals("LeftBoot")) return vanillaModel.leftLeg;
            if (name.startsWith("RightLeg") || name.equals("RightBoot"))
                return vanillaModel.rightLeg;
            return null;
        }
        if (name.startsWith(HEAD)) return vanillaModel.head;
        if (name.startsWith(LEFT_ARM)) return vanillaModel.leftArm;
        if (name.startsWith(RIGHT_ARM)) return vanillaModel.rightArm;
        if (name.startsWith(LEFT_LEG)) return vanillaModel.leftLeg;
        if (name.startsWith(RIGHT_LEG)) return vanillaModel.rightLeg;
        return null;
    }

    static <T extends Mob> void applyBowPose(HumanoidModel<T> model, T entity, float partialTick) {
        if (!entity.isUsingItem() || !entity.isHolding(stack -> stack.getItem() instanceof BowItem))
            return;
        float progress = Mth.clamp((entity.getTicksUsingItem() + partialTick) / 5.0F, 0.0F, 1.0F);
        float headPitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) * Mth.DEG_TO_RAD;
        float headYaw = Mth.lerp(partialTick, entity.yBodyRotO - entity.yHeadRotO,
                entity.yBodyRot - entity.yHeadRot) * Mth.DEG_TO_RAD;
        model.rightArm.xRot = Mth.lerp(progress, model.rightArm.xRot, 1.5F - headPitch);
        model.rightArm.yRot = Mth.lerp(progress, model.rightArm.yRot, headYaw);
        model.rightArm.zRot = 0.0F;
        model.leftArm.xRot = Mth.lerp(progress, model.leftArm.xRot, 1.3F - headPitch);
        model.leftArm.yRot = Mth.lerp(progress, model.leftArm.yRot, Math.max(headYaw - 0.5F, -1.4F));
        model.leftArm.zRot = 0.0F;
    }

}
