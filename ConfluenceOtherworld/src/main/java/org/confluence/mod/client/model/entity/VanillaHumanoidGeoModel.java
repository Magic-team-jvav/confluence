package org.confluence.mod.client.model.entity;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BowItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.util.RenderUtils;

/// 给普通双足怪物复用原版人形模型的基础肢体动画。
/// 只有名称以约定前缀开头的骨骼会接收原版旋转，附加装饰骨骼保持资源里写好的姿态。
public class VanillaHumanoidGeoModel<T extends Mob & GeoEntity> extends GeoNormalModel<T> {
    private static final String HEAD = "Vhead";
    private static final String LEFT_ARM = "Vleft_arm";
    private static final String RIGHT_ARM = "Vright_arm";
    private static final String LEFT_LEG = "Vleft_leg";
    private static final String RIGHT_LEG = "Vright_leg";

    private final HumanoidModel<T> vanillaModel;
    private final @Nullable ResourceLocation explicitModel;
    private final @Nullable ResourceLocation explicitTexture;

    protected enum Posture {HUMANOID, ZOMBIE, SKELETON}

    private enum Bones {PREFIXED, ARMOR, STANDALONE}

    private final Bones bones;
    private final Posture posture;
    private final boolean walks;

    public VanillaHumanoidGeoModel(EntityRendererProvider.Context context, ResourceLocation path) {
        this(context, path, Posture.HUMANOID);
    }

    protected VanillaHumanoidGeoModel(EntityRendererProvider.Context context, ResourceLocation path, Posture posture) {
        this(context, path, null, Bones.PREFIXED, posture, true);
    }

    protected VanillaHumanoidGeoModel(EntityRendererProvider.Context context, ResourceLocation model, ResourceLocation texture, boolean walks) {
        this(context, model, texture, Bones.ARMOR, Posture.HUMANOID, walks);
    }

    private VanillaHumanoidGeoModel(EntityRendererProvider.Context context, ResourceLocation path, @Nullable ResourceLocation texture,
                                    Bones bones, Posture posture, boolean walks) {
        super(path, false);
        vanillaModel = new HumanoidModel<>(context.bakeLayer(posture == Posture.SKELETON ? ModelLayers.SKELETON : ModelLayers.PLAYER));
        explicitModel = texture == null ? null : path;
        explicitTexture = texture;
        this.bones = bones;
        this.posture = posture;
        this.walks = walks;
    }

    public static <T extends Mob & GeoEntity> VanillaHumanoidGeoModel<T> armor(EntityRendererProvider.Context context, ResourceLocation model, ResourceLocation texture, boolean walks) {
        return new VanillaHumanoidGeoModel<>(context, model, texture, walks);
    }

    public static <T extends Mob & GeoEntity> VanillaHumanoidGeoModel<T> standalone(EntityRendererProvider.Context context, ResourceLocation model, ResourceLocation texture) {
        return new VanillaHumanoidGeoModel<>(context, model, texture, Bones.STANDALONE, Posture.HUMANOID, true);
    }

    public static <T extends Mob & GeoEntity> VanillaHumanoidGeoModel<T> skeleton(EntityRendererProvider.Context context, ResourceLocation id) {
        return new VanillaHumanoidGeoModel<>(context, id, Posture.SKELETON);
    }

    public static <T extends Mob & GeoEntity> VanillaHumanoidGeoModel<T> zombie(EntityRendererProvider.Context context, ResourceLocation id) {
        return new VanillaHumanoidGeoModel<>(context, id.withPrefix("geo/entity/").withSuffix(".geo.json"),
                id.withPrefix("textures/entity/").withSuffix(".png"), Bones.STANDALONE, Posture.ZOMBIE, true);
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
        if (walks && posture != Posture.ZOMBIE) prepareBowPose(vanillaModel, entity);
        vanillaModel.setupAnim(entity, limbSwing, limbSwingAmount, entity.tickCount + partialTick,
                look.netHeadYaw(), look.headPitch());
        if (posture == Posture.ZOMBIE) {
            AnimationUtils.animateZombieArms(vanillaModel.leftArm, vanillaModel.rightArm, entity.isAggressive() || entity.isSprinting(), vanillaModel.attackTime, entity.tickCount + partialTick);
        }
        if (posture == Posture.SKELETON && (entity.isAggressive() || vanillaModel.attackTime > 0) && !(entity.getUseItem().getItem() instanceof BowItem) && !(entity.getMainHandItem().getItem() instanceof BowItem)) {
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
                RenderUtils.matchModelPartRot(source, bone);
            }
        }
    }

    private ModelPart sourcePart(String name) {
        if (bones == Bones.STANDALONE) {
            return switch (name) {
                case "Head", "Vhead" -> vanillaModel.head;
                case "Body", "Body3" -> vanillaModel.body;
                case "LeftArm", "Vleft_arm" -> vanillaModel.leftArm;
                case "RightArm", "Vright_arm" -> vanillaModel.rightArm;
                case "LeftLeg", "Vleft_leg" -> vanillaModel.leftLeg;
                case "RightLeg", "RightBoot", "Vright_leg" -> vanillaModel.rightLeg;
                default -> null;
            };
        }
        if (bones == Bones.ARMOR) {
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

    private static <T extends Mob> void prepareBowPose(HumanoidModel<T> model, T entity) {
        if (!(entity.getUseItem().getItem() instanceof BowItem)) return;
        HumanoidArm arm = entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
        if (arm == HumanoidArm.RIGHT) model.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
        else model.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
    }

}
