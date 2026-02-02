package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.terraentity.client.animation.api.context.AnimatorContext;
import org.confluence.terraentity.client.animation.multi_bone.animator.SkeletronAnimator;
import org.confluence.terraentity.client.boss.model.SkeletronHandModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.animation.BoneStates;
import org.confluence.terraentity.entity.animation.MultiBone;
import org.confluence.terraentity.entity.animation.MultiBoneStateMachine;
import org.confluence.terraentity.entity.boss.SkeletronHand;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

public class SkeletronHandRenderer extends GeoNormalRenderer<SkeletronHand> {
    private GeoBone hand;
    SkeletronAnimator<SkeletronHand> animator;

    public SkeletronHandRenderer(EntityRendererProvider.Context renderManager, SkeletronHandModel model) {
        super(renderManager, model, true, 1, 0);
    }

    @Override
    public void preRender(PoseStack poseStack, SkeletronHand animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        model.getBone("bone2").ifPresent(bone -> bone.setHidden(true)); // 头
        model.getBone("bone3").ifPresent(geoBone -> geoBone.setHidden(false));

//        model.getBone("hand").ifPresent(hand -> {
//            this.hand = hand;
//            hand.setScaleZ(animatable.handSide == SkeletronHand.HandSide.LEFT ? -1 : 1);
//        });
//
//        model.getBone("arm2").ifPresent(wholeArm->{ // 小臂
//            if (animatable.owner == null) {
//                wholeArm.setHidden(true);
//                return;
//            }
//            wholeArm.setHidden(false);
////            float yRot = Mth.lerp(partialTick, animatable.owner.yHeadRotO, animatable.owner.yHeadRot) * Mth.DEG_TO_RAD;
//            Vec3 selfPos = animatable.position();
//            Vec3 rootPos = animatable.getRootPos();
//            double a = Math.min(rootPos.distanceTo(selfPos), 10.4 * scale);
//            double b = 5.2 * scale;
//            double c = 5.2 * scale;
//            double radC = Math.acos((a * a + b * b - c * c) / (2 * a * b)); // 余弦定理
//            double armRot = selfPos.y < rootPos.y - 2 ? radC : -radC;
//            wholeArm.setRotZ((float) armRot);
//            hand.setRotZ((float) armRot);
//            double radA = Math.PI - Math.acos((b * b + c * c - a * a) / (2 * b * c));
////            wholeArm.getChildBones().getFirst().setHidden(false); // 大臂
//            wholeArm.getChildBones().getFirst().setRotZ((float) (selfPos.y < rootPos.y - 1 ? -radA : radA));
//        });
//        poseStack.translate(-11.4f, -0.6f, 0);
//        poseStack.translate(0, 0.5f, 0);

        if(animator == null){
            MultiBone bone = new MultiBone();
            bone.hand = model.getBone("hand").get();
            bone.arm1 = model.getBone("arm2").get();
            bone.arm2 = model.getBone("arm2").get().getChildBones().getFirst();
            animator = new SkeletronAnimator<>(bone);

        }else{
            AnimatorContext context = new AnimatorContext(0);
            handleBone(animatable.stateMachine, animatable, animator, partialTick,  context);
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

    }

    @Override
    public void postRender(PoseStack poseStack, SkeletronHand animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if(animator != null){
            animator.getBone().hand.setRotY(0);
        }
    }

    private void handleBone(MultiBoneStateMachine<BoneStates> stateMachine, SkeletronHand animatable,
                            SkeletronAnimator<SkeletronHand> animator, float partialTick, AnimatorContext context) {
        animator.updateState(stateMachine, animatable, partialTick, context);
        stateMachine.apply(partialTick, animator.getBone());
    }

    @Override
    protected void applyRotations(SkeletronHand animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
//        poseStack.translate(-11.4f, -0.6f, 0);
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
//        poseStack.translate(11.4f, -1.1f, 0);

    }
}
