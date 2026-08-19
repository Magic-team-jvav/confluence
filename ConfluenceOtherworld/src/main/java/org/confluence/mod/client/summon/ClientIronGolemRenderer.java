package org.confluence.mod.client.summon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Objects;

/// 直接使用原版铁傀儡模型部件绘制同步状态，不创建客户端实体。
final class ClientIronGolemRenderer {
    private static final ResourceLocation TEXTURE = Objects.requireNonNull(ResourceLocation.tryParse("minecraft:textures/entity/iron_golem/iron_golem.png"), "Iron golem texture location must be valid");
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    ClientIronGolemRenderer(EntityModelSet models) {
        root = models.bakeLayer(ModelLayers.IRON_GOLEM);
        head = root.getChild("head");
        rightArm = root.getChild("right_arm");
        leftArm = root.getChild("left_arm");
        rightLeg = root.getChild("right_leg");
        leftLeg = root.getChild("left_leg");
    }

    void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, float yaw, float pitch, float walkPosition, float walkSpeed, int attackTicks, float partialTick) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        if (walkSpeed >= 0.01F) {
            float phase = walkPosition + 6.0F;
            float sway = (Math.abs(phase % 13.0F - 6.5F) - 3.25F) / 3.25F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(6.5F * sway));
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        setupAnimation(pitch, walkPosition, Math.min(walkSpeed, 1.0F), attackTicks, partialTick);
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        root.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private void setupAnimation(float pitch, float walkPosition, float walkSpeed, int attackTicks, float partialTick) {
        root.resetPose();
        head.yRot = 0.0F;
        head.xRot = pitch * Mth.DEG_TO_RAD;
        rightLeg.xRot = -1.5F * Mth.triangleWave(walkPosition, 13.0F) * walkSpeed;
        leftLeg.xRot = 1.5F * Mth.triangleWave(walkPosition, 13.0F) * walkSpeed;
        rightLeg.yRot = 0.0F;
        leftLeg.yRot = 0.0F;
        if (attackTicks > 0) {
            float attackRotation = -2.0F + 1.5F * Mth.triangleWave(attackTicks - partialTick, 10.0F);
            rightArm.xRot = attackRotation;
            leftArm.xRot = attackRotation;
        } else {
            rightArm.xRot = (-0.2F + 1.5F * Mth.triangleWave(walkPosition, 13.0F)) * walkSpeed;
            leftArm.xRot = (-0.2F - 1.5F * Mth.triangleWave(walkPosition, 13.0F)) * walkSpeed;
        }
    }
}
