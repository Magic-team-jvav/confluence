package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.state.BoneSnapshot;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// 枪械专用 GeoItem 渲染器。
///
/// <p>枪械资源以 1.21 TerraGuns 为权威：第一人称根节点、GUI、展示框和地面姿态都使用同一套
/// Geo 模型。1.20 侧只在 Forge 与 GeckoLib 4.3 的渲染入口上做适配，不改变枪械数值和行为。</p>
public class GunRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
    private static final float FIRST_PERSON_X = 0.54F;
    private static final float FIRST_PERSON_Y = -0.10F;
    private static final float FIRST_PERSON_Z = -0.24F;

    private Matrix4f queuedLeftArmPose;
    private Matrix3f queuedLeftArmNormal;
    private Matrix4f queuedRightArmPose;
    private Matrix3f queuedRightArmNormal;
    private Matrix4f lastLeftArmLocalPose;
    private Matrix3f lastLeftArmLocalNormal;
    private Matrix4f lastRightArmLocalPose;
    private Matrix3f lastRightArmLocalNormal;
    private ItemDisplayContext lastRenderedPerspective;
    private final Map<String, BonePose> lastModelPose = new HashMap<>();
    private final Map<String, BonePose> modelPoseBeforeOverride = new HashMap<>();
    private boolean modelPosePrepared;
    private boolean modelPoseOverridden;
    private Matrix4f firstPersonBasePose;
    private Matrix3f firstPersonBaseNormal;
    private MultiBufferSource firstPersonBufferSource;
    private int firstPersonArmLight;

    public GunRenderer(GeoModel<T> model) {
        super(model);
    }

    public ItemDisplayContext getRenderPerspective() {
        return renderPerspective;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        boolean firstPersonRoot = isFirstPersonPerspective() && bone.getParent() == null;
        GeoBone displayPosition = !isFirstPersonPerspective() && bone.getParent() == null
                ? findDisplayPositionBone()
                : null;
        boolean displayRoot = displayPosition != null;

        if (bone.getParent() == null) {
            if (lastRenderedPerspective != null && lastRenderedPerspective != renderPerspective) {
                clearLastArmPoses();
            }
            lastRenderedPerspective = renderPerspective;
        }

        if (firstPersonRoot) {
            prepareModelPoseForFrame();
            poseStack.pushPose();
            poseStack.translate(0.0F, -0.01F, 0.0F);

            float side = renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1.0F : 1.0F;
            poseStack.translate(side * FIRST_PERSON_X, FIRST_PERSON_Y, FIRST_PERSON_Z);
            TaczFirstPersonTransform.applyIdleViewInverse(poseStack, findModelBone("idle_view"));

            if (firstPersonBasePose == null) {
                firstPersonBasePose = new Matrix4f(poseStack.last().pose());
                firstPersonBaseNormal = new Matrix3f(poseStack.last().normal());
                firstPersonBufferSource = bufferSource;
                firstPersonArmLight = packedLight;
            }
        } else if (displayRoot) {
            poseStack.pushPose();
            TaczFirstPersonTransform.applyPositioningInverse(poseStack, displayPosition);
        }

        if (isFirstPersonPerspective()) {
            HumanoidArm arm = switch (bone.getName()) {
                case "lefthand_pos" -> HumanoidArm.LEFT;
                case "righthand_pos" -> HumanoidArm.RIGHT;
                default -> null;
            };
            if (arm != null) {
                queuePlayerArm(poseStack, bone, arm);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        if (firstPersonRoot || displayRoot) {
            poseStack.popPose();
        }
    }

    @Override
    public void doPostRenderCleanup() {
        if (isFirstPersonPerspective() && firstPersonBasePose != null && firstPersonBufferSource != null) {
            queueMissingModelHands();
            rememberArmPose(HumanoidArm.LEFT, queuedLeftArmPose, queuedLeftArmNormal);
            rememberArmPose(HumanoidArm.RIGHT, queuedRightArmPose, queuedRightArmNormal);
            renderQueuedArm(firstPersonBufferSource, firstPersonArmLight,
                    queuedLeftArmPose, queuedLeftArmNormal, HumanoidArm.LEFT);
            renderQueuedArm(firstPersonBufferSource, firstPersonArmLight,
                    queuedRightArmPose, queuedRightArmNormal, HumanoidArm.RIGHT);
        }

        finishModelPoseFrame();
        clearQueuedArms();
        firstPersonBasePose = null;
        firstPersonBaseNormal = null;
        firstPersonBufferSource = null;
        super.doPostRenderCleanup();
    }

    private boolean isFirstPersonPerspective() {
        return renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
    }

    private GeoBone findDisplayPositionBone() {
        String boneName = switch (renderPerspective) {
            case GROUND -> "ground";
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> "thirdperson_hand";
            case FIXED, GUI -> "fixed";
            default -> null;
        };
        return boneName == null ? null : findModelBone(boneName);
    }

    private GeoBone findModelBone(String name) {
        CoreGeoBone bone = getGeoModel().getAnimationProcessor().getBone(name);
        return bone instanceof GeoBone geoBone ? geoBone : getGeoModel().getBone(name).orElse(null);
    }

    private void queuePlayerArm(PoseStack poseStack, GeoBone handPosition, HumanoidArm arm) {
        if (isInitialHandPose(handPosition) && hasLastArmPose(arm)) {
            reuseLastArmPose(arm);
            return;
        }

        PoseStack handPose = new PoseStack();
        handPose.last().pose().set(poseStack.last().pose());
        handPose.last().normal().set(poseStack.last().normal());
        RenderUtils.translateMatrixToBone(handPose, handPosition);
        RenderUtils.translateAndRotateMatrixForBone(handPose, handPosition);
        RenderUtils.scaleMatrixForBone(handPose, handPosition);

        setQueuedArmPose(arm, handPose);
    }

    private void prepareModelPoseForFrame() {
        if (modelPosePrepared) {
            return;
        }
        modelPosePrepared = true;
        modelPoseOverridden = false;

        if (!isTransientModelResetPose() || lastModelPose.isEmpty()) {
            return;
        }

        modelPoseBeforeOverride.clear();
        for (GeoBone bone : registeredGeoBones()) {
            BonePose cachedPose = lastModelPose.get(bone.getName());
            if (cachedPose == null) {
                continue;
            }
            modelPoseBeforeOverride.put(bone.getName(), BonePose.capture(bone));
            cachedPose.applyTo(bone);
        }
        modelPoseOverridden = !modelPoseBeforeOverride.isEmpty();
    }

    private boolean isTransientModelResetPose() {
        boolean foundMarker = false;
        boolean allHandsAreInitial = true;
        for (String markerName : List.of("lefthand_pos", "righthand_pos")) {
            GeoBone marker = findModelBone(markerName);
            if (marker == null) {
                continue;
            }
            foundMarker = true;
            allHandsAreInitial &= isInitialHandPose(marker);
        }
        return foundMarker && allHandsAreInitial;
    }

    private void finishModelPoseFrame() {
        if (!modelPosePrepared) {
            return;
        }

        if (modelPoseOverridden) {
            for (GeoBone bone : registeredGeoBones()) {
                BonePose originalPose = modelPoseBeforeOverride.get(bone.getName());
                if (originalPose != null) {
                    originalPose.applyTo(bone);
                }
                bone.resetStateChanges();
            }
            modelPoseBeforeOverride.clear();
        } else if (firstPersonBasePose != null) {
            lastModelPose.clear();
            for (GeoBone bone : registeredGeoBones()) {
                lastModelPose.put(bone.getName(), BonePose.capture(bone));
            }
        }

        modelPosePrepared = false;
        modelPoseOverridden = false;
    }

    private boolean isInitialHandPose(GeoBone marker) {
        GeoBone hand = marker.getParent();
        BoneSnapshot initial = hand == null ? null : hand.getInitialSnapshot();
        if (initial == null) {
            return false;
        }

        return isClose(hand.getPosX(), initial.getOffsetX())
                && isClose(hand.getPosY(), initial.getOffsetY())
                && isClose(hand.getPosZ(), initial.getOffsetZ())
                && isClose(hand.getRotX(), initial.getRotX())
                && isClose(hand.getRotY(), initial.getRotY())
                && isClose(hand.getRotZ(), initial.getRotZ())
                && isClose(hand.getScaleX(), initial.getScaleX())
                && isClose(hand.getScaleY(), initial.getScaleY())
                && isClose(hand.getScaleZ(), initial.getScaleZ());
    }

    private boolean hasLastArmPose(HumanoidArm arm) {
        return arm == HumanoidArm.RIGHT
                ? lastRightArmLocalPose != null && lastRightArmLocalNormal != null
                : lastLeftArmLocalPose != null && lastLeftArmLocalNormal != null;
    }

    private static boolean isClose(float actual, float expected) {
        return Math.abs(actual - expected) < 0.0001F;
    }

    /// 手臂定位骨骼通常是隐藏标记。若 GeckoLib 当前遍历路径没有回调隐藏骨骼，
    /// 就从动画后的骨骼层级手动重建一次定位矩阵。
    ///
    /// <p>普通枪没有手臂定位骨骼。为了避免第一人称只剩枪械模型，这里为缺失定位的侧手提供固定兜底姿态；
    /// 带定位骨骼的新枪仍完全以模型标记为准。</p>
    private void queueMissingModelHands() {
        if (queuedLeftArmPose == null || queuedLeftArmNormal == null) {
            if (lastLeftArmLocalPose != null && lastLeftArmLocalNormal != null) {
                reuseLastArmPose(HumanoidArm.LEFT);
            } else {
                GeoBone marker = findModelBone("lefthand_pos");
                if (marker != null) {
                    queuePlayerArmFromModel(marker, HumanoidArm.LEFT);
                }
            }
        }
        if (queuedRightArmPose == null || queuedRightArmNormal == null) {
            if (lastRightArmLocalPose != null && lastRightArmLocalNormal != null) {
                reuseLastArmPose(HumanoidArm.RIGHT);
            } else {
                GeoBone marker = findModelBone("righthand_pos");
                if (marker != null) {
                    queuePlayerArmFromModel(marker, HumanoidArm.RIGHT);
                }
            }
        }
    }

    private void queuePlayerArmFromModel(GeoBone marker, HumanoidArm arm) {
        PoseStack handPose = new PoseStack();
        handPose.last().pose().set(firstPersonBasePose);
        handPose.last().normal().set(firstPersonBaseNormal);

        List<GeoBone> parents = new ArrayList<>();
        for (GeoBone parent = marker.getParent(); parent != null; parent = parent.getParent()) {
            parents.add(parent);
        }
        for (int index = parents.size() - 1; index >= 0; index--) {
            RenderUtils.prepMatrixForBone(handPose, parents.get(index));
        }
        RenderUtils.translateMatrixToBone(handPose, marker);
        RenderUtils.translateAndRotateMatrixForBone(handPose, marker);
        RenderUtils.scaleMatrixForBone(handPose, marker);

        setQueuedArmPose(arm, handPose);
    }

    private void setQueuedArmPose(HumanoidArm arm, PoseStack handPose) {
        if (arm == HumanoidArm.RIGHT) {
            queuedRightArmPose = new Matrix4f(handPose.last().pose());
            queuedRightArmNormal = new Matrix3f(handPose.last().normal());
        } else {
            queuedLeftArmPose = new Matrix4f(handPose.last().pose());
            queuedLeftArmNormal = new Matrix3f(handPose.last().normal());
        }
    }

    private void reuseLastArmPose(HumanoidArm arm) {
        if (firstPersonBasePose == null || firstPersonBaseNormal == null) {
            return;
        }

        if (arm == HumanoidArm.RIGHT) {
            queuedRightArmPose = new Matrix4f(firstPersonBasePose).mul(lastRightArmLocalPose);
            queuedRightArmNormal = new Matrix3f(firstPersonBaseNormal).mul(lastRightArmLocalNormal);
        } else {
            queuedLeftArmPose = new Matrix4f(firstPersonBasePose).mul(lastLeftArmLocalPose);
            queuedLeftArmNormal = new Matrix3f(firstPersonBaseNormal).mul(lastLeftArmLocalNormal);
        }
    }

    private void rememberArmPose(HumanoidArm arm, Matrix4f pose, Matrix3f normal) {
        if (pose == null || normal == null || firstPersonBasePose == null || firstPersonBaseNormal == null) {
            return;
        }

        Matrix4f basePoseInverse = new Matrix4f(firstPersonBasePose).invert();
        Matrix3f baseNormalInverse = new Matrix3f(firstPersonBaseNormal).invert();
        if (arm == HumanoidArm.RIGHT) {
            lastRightArmLocalPose = basePoseInverse.mul(new Matrix4f(pose));
            lastRightArmLocalNormal = baseNormalInverse.mul(new Matrix3f(normal));
        } else {
            lastLeftArmLocalPose = basePoseInverse.mul(new Matrix4f(pose));
            lastLeftArmLocalNormal = baseNormalInverse.mul(new Matrix3f(normal));
        }
    }

    private void renderQueuedArm(MultiBufferSource bufferSource, int packedLight,
                                 Matrix4f pose, Matrix3f normal, HumanoidArm arm) {
        if (pose == null || normal == null) {
            return;
        }
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (!(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player)
                instanceof PlayerRenderer renderer)) {
            return;
        }

        PoseStack armPose = new PoseStack();
        armPose.last().pose().set(pose);
        armPose.last().normal().set(normal);
        renderPlayerArmDirect(renderer, player, armPose, bufferSource, packedLight, arm);
    }

    /// 只渲染当前枪械需要的第一人称手臂，避免再次经过原版手臂事件造成取消或重复渲染。
    @SuppressWarnings("unchecked")
    private static void renderPlayerArmDirect(PlayerRenderer renderer, AbstractClientPlayer player,
                                              PoseStack poseStack, MultiBufferSource bufferSource,
                                              int packedLight, HumanoidArm arm) {
        PlayerModel<AbstractClientPlayer> model = renderer.getModel();
        model.setAllVisible(false);
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        ModelPart armPart;
        ModelPart sleevePart;
        if (arm == HumanoidArm.RIGHT) {
            armPart = model.rightArm;
            sleevePart = model.rightSleeve;
        } else {
            armPart = model.leftArm;
            sleevePart = model.leftSleeve;
        }
        armPart.visible = true;
        sleevePart.visible = true;
        armPart.xRot = 0.0F;
        sleevePart.xRot = 0.0F;

        ResourceLocation skinTexture = player.getSkinTextureLocation();
        armPart.render(poseStack, bufferSource.getBuffer(RenderType.entitySolid(skinTexture)),
                packedLight, OverlayTexture.NO_OVERLAY);
        sleevePart.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(skinTexture)),
                packedLight, OverlayTexture.NO_OVERLAY);
    }

    private void clearQueuedArms() {
        queuedLeftArmPose = null;
        queuedLeftArmNormal = null;
        queuedRightArmPose = null;
        queuedRightArmNormal = null;
    }

    private List<GeoBone> registeredGeoBones() {
        List<GeoBone> bones = new ArrayList<>();
        for (CoreGeoBone bone : getGeoModel().getAnimationProcessor().getRegisteredBones()) {
            if (bone instanceof GeoBone geoBone) {
                bones.add(geoBone);
            }
        }
        return bones;
    }

    private void clearLastArmPoses() {
        lastLeftArmLocalPose = null;
        lastLeftArmLocalNormal = null;
        lastRightArmLocalPose = null;
        lastRightArmLocalNormal = null;
        lastModelPose.clear();
        modelPoseBeforeOverride.clear();
    }

    private record BonePose(float posX, float posY, float posZ,
                            float rotX, float rotY, float rotZ,
                            float scaleX, float scaleY, float scaleZ) {
        private static BonePose capture(GeoBone bone) {
            return new BonePose(bone.getPosX(), bone.getPosY(), bone.getPosZ(),
                    bone.getRotX(), bone.getRotY(), bone.getRotZ(),
                    bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
        }

        private void applyTo(GeoBone bone) {
            bone.setPosX(posX);
            bone.setPosY(posY);
            bone.setPosZ(posZ);
            bone.setRotX(rotX);
            bone.setRotY(rotY);
            bone.setRotZ(rotZ);
            bone.setScaleX(scaleX);
            bone.setScaleY(scaleY);
            bone.setScaleZ(scaleZ);
        }
    }
}
