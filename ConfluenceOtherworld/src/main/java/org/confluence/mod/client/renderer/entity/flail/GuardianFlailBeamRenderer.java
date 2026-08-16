package org.confluence.mod.client.renderer.entity.flail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.flail.GuardianFlailEntity;

/// 使用原版守卫者光束纹理渲染链锤与同步目标之间的实体光束。
public final class GuardianFlailBeamRenderer {
    private static final ResourceLocation BEAM_TEXTURE =
            ResourceLocation.withDefaultNamespace(
                    "textures/entity/guardian_beam.png");
    private static final RenderType BEAM_RENDER_TYPE =
            RenderType.entityCutoutNoCull(BEAM_TEXTURE);

    private GuardianFlailBeamRenderer() {
    }

    public static void render(
            GuardianFlailEntity entity,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            float partialTick
    ) {
        float warmup = entity.getAttackProgress(partialTick);
        float intensity = warmup * warmup;
        int red;
        int green;
        int blue;
        if (entity.isElder()) {
            red = (int) (intensity * 128.0F);
            green = (int) (intensity * 128.0F);
            blue = (int) (128.0F + intensity * 127.0F);
        } else {
            red = 64 + (int) (intensity * 191.0F);
            green = 32 + (int) (intensity * 191.0F);
            blue = 128 - (int) (intensity * 64.0F);
        }
        int color = 0xFF000000 | red << 16 | green << 8 | blue;

        Vec3 renderPosition = entity.getPosition(partialTick);
        for (LivingEntity target : entity.getBeamTargets()) {
            Vec3 beam = target.getBoundingBox().getCenter()
                    .subtract(renderPosition);
            renderSingleBeam(
                    poseStack,
                    bufferSource,
                    new Vec3(0.0, 0.25, 0.0),
                    beam,
                    color,
                    entity.tickCount + partialTick);
        }
    }

    private static void renderSingleBeam(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            Vec3 source,
            Vec3 beam,
            int color,
            float time
    ) {
        double length = beam.length();
        if (length < 0.01) {
            return;
        }

        int red = FastColor.ARGB32.red(color);
        int green = FastColor.ARGB32.green(color);
        int blue = FastColor.ARGB32.blue(color);
        poseStack.pushPose();
        poseStack.translate(source.x, source.y, source.z);

        Vec3 direction = beam.normalize();
        float pitch = (float) Math.acos(direction.y);
        float yaw = (float) Math.atan2(direction.z, direction.x);
        poseStack.mulPose(Axis.YP.rotationDegrees(
                ((float) Math.PI / 2.0F - yaw) * Mth.RAD_TO_DEG));
        poseStack.mulPose(Axis.XP.rotationDegrees(
                pitch * Mth.RAD_TO_DEG));

        float end = (float) length + 1.0F;
        float rotation = time * -0.075F;
        float startV = -1.0F + time * 0.5F % 1.0F;
        float endV = end * 2.5F + startV;
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer =
                bufferSource.getBuffer(BEAM_RENDER_TYPE);

        float firstX = Mth.cos(rotation + (float) Math.PI) * 0.2F;
        float firstZ = Mth.sin(rotation + (float) Math.PI) * 0.2F;
        float secondX = Mth.cos(rotation) * 0.2F;
        float secondZ = Mth.sin(rotation) * 0.2F;
        float thirdX = Mth.cos(rotation + Mth.HALF_PI) * 0.2F;
        float thirdZ = Mth.sin(rotation + Mth.HALF_PI) * 0.2F;
        float fourthX = Mth.cos(rotation + Mth.HALF_PI * 3.0F) * 0.2F;
        float fourthZ = Mth.sin(rotation + Mth.HALF_PI * 3.0F) * 0.2F;

        vertex(consumer, pose, firstX, end, firstZ,
                red, green, blue, 0.4999F, endV);
        vertex(consumer, pose, firstX, 0.0F, firstZ,
                red, green, blue, 0.4999F, startV);
        vertex(consumer, pose, secondX, 0.0F, secondZ,
                red, green, blue, 0.0F, startV);
        vertex(consumer, pose, secondX, end, secondZ,
                red, green, blue, 0.0F, endV);

        vertex(consumer, pose, thirdX, end, thirdZ,
                red, green, blue, 0.4999F, endV);
        vertex(consumer, pose, thirdX, 0.0F, thirdZ,
                red, green, blue, 0.4999F, startV);
        vertex(consumer, pose, fourthX, 0.0F, fourthZ,
                red, green, blue, 0.0F, startV);
        vertex(consumer, pose, fourthX, end, fourthZ,
                red, green, blue, 0.0F, endV);
        // 封住光束末端，避免从目标方向观察时看到中空截面。
        float endFrameV = ((int) time & 1) == 0 ? 0.5F : 0.0F;
        float endFirstX = Mth.cos(rotation + 2.3561945F) * 0.282F;
        float endFirstZ = Mth.sin(rotation + 2.3561945F) * 0.282F;
        float endSecondX = Mth.cos(rotation + (float) Math.PI / 4.0F) * 0.282F;
        float endSecondZ = Mth.sin(rotation + (float) Math.PI / 4.0F) * 0.282F;
        float endThirdX = Mth.cos(rotation + 3.926991F) * 0.282F;
        float endThirdZ = Mth.sin(rotation + 3.926991F) * 0.282F;
        float endFourthX = Mth.cos(rotation + 5.4977875F) * 0.282F;
        float endFourthZ = Mth.sin(rotation + 5.4977875F) * 0.282F;

        vertex(consumer, pose, endFirstX, end, endFirstZ,
                red, green, blue, 0.5F, endFrameV + 0.5F);
        vertex(consumer, pose, endSecondX, end, endSecondZ,
                red, green, blue, 1.0F, endFrameV + 0.5F);
        vertex(consumer, pose, endFourthX, end, endFourthZ,
                red, green, blue, 1.0F, endFrameV);
        vertex(consumer, pose, endThirdX, end, endThirdZ,
                red, green, blue, 0.5F, endFrameV);
        poseStack.popPose();
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            int red,
            int green,
            int blue,
            float u,
            float v
    ) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(red, green, blue, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
