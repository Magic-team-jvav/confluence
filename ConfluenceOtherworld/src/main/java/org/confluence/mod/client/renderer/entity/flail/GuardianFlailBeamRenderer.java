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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.handler.ClientBeamCache;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;

/**
 * <h1>守卫连枷光束渲染</h1>
 */
public final class GuardianFlailBeamRenderer {

    private static final ResourceLocation BEAM_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/guardian_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(BEAM_TEXTURE);

    private GuardianFlailBeamRenderer() {}

    /**
     * 渲染连枷的所有守卫者激光束
     */
    public static void renderGuardianBeams(BaseFlailEntity entity, PoseStack poseStack,
                                           MultiBufferSource bufferSource, float partialTick) {
        ClientBeamCache.BeamEntry entry = ClientBeamCache.get(entity.getId());
        if (entry == null || entry.targetIds.length == 0) return;

        Level level = entity.level();
        Vec3 flailEye = new Vec3(0, 0.25, 0);
        // 原版攻击进度暖机渐变：暗紫→亮金
        float warmup = Mth.clamp((System.currentTimeMillis() - entry.startTimeMs) / 1000.0F, 0.0F, 1.0F);
        float f8 = warmup * warmup;
        int r, g, b;
        if (entry.elder) {
            r = (int) (f8 * 128);
            g = (int) (f8 * 128);
            b = (int) (128 + f8 * 127);
        } else {
            r = 64 + (int) (f8 * 191.0F);
            g = 32 + (int) (f8 * 191.0F);
            b = 128 - (int) (f8 * 64.0F);
        }
        int outerColor = 0xFF000000 | (r << 16) | (g << 8) | b;

        for (int targetId : entry.targetIds) {
            Entity target = level.getEntity(targetId);
            if (target instanceof LivingEntity living) {
                Vec3 targetCenter = living.getBoundingBox().getCenter();
                Vec3 beamVec = targetCenter.subtract(entity.getPosition(partialTick));
                renderSingleBeam(poseStack, bufferSource, flailEye, beamVec, outerColor,
                        entity.tickCount + partialTick);
            }
        }
    }

    private static void renderSingleBeam(PoseStack poseStack, MultiBufferSource bufferSource,
                                         Vec3 beamSource, Vec3 beamVec, int color, float time) {
        double length = beamVec.length();
        if (length < 0.01) return;

        int r = FastColor.ARGB32.red(color);
        int g = FastColor.ARGB32.green(color);
        int b = FastColor.ARGB32.blue(color);

        poseStack.pushPose();
        poseStack.translate(beamSource.x, beamSource.y, beamSource.z);

        Vec3 n = beamVec.normalize();
        float f5 = (float) Math.acos(n.y);
        float f6 = (float) Math.atan2(n.z, n.x);
        poseStack.mulPose(Axis.YP.rotationDegrees((((float) Math.PI / 2F) - f6) * (180F / (float) Math.PI)));
        poseStack.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float) Math.PI)));

        float f4 = (float) (length + 1.0);
        float f7 = time * 0.05F * -1.5F;
        float f2 = time * 0.5F % 1.0F;
        float f29 = -1.0F + f2;
        float f30 = f4 * 2.5F + f29;

        PoseStack.Pose pose = poseStack.last();
        VertexConsumer vc = bufferSource.getBuffer(BEAM_RENDER_TYPE);

        float f19 = Mth.cos(f7 + (float) Math.PI) * 0.2F;
        float f20 = Mth.sin(f7 + (float) Math.PI) * 0.2F;
        float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
        float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
        float f23 = Mth.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f24 = Mth.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f25 = Mth.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f26 = Mth.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;

        vertex(vc, pose, f19, f4, f20, r, g, b, 0.4999F, f30);
        vertex(vc, pose, f19, 0.0F, f20, r, g, b, 0.4999F, f29);
        vertex(vc, pose, f21, 0.0F, f22, r, g, b, 0.0F, f29);
        vertex(vc, pose, f21, f4, f22, r, g, b, 0.0F, f30);

        vertex(vc, pose, f23, f4, f24, r, g, b, 0.4999F, f30);
        vertex(vc, pose, f23, 0.0F, f24, r, g, b, 0.4999F, f29);
        vertex(vc, pose, f25, 0.0F, f26, r, g, b, 0.0F, f29);
        vertex(vc, pose, f25, f4, f26, r, g, b, 0.0F, f30);

        float f31 = ((int) time) % 2 == 0 ? 0.5F : 0.0F;

        float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
        float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
        float f13 = Mth.cos(f7 + ((float) Math.PI / 4F)) * 0.282F;
        float f14 = Mth.sin(f7 + ((float) Math.PI / 4F)) * 0.282F;
        float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
        float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
        float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
        float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;

        vertex(vc, pose, f11, f4, f12, r, g, b, 0.5F, f31 + 0.5F);
        vertex(vc, pose, f13, f4, f14, r, g, b, 1.0F, f31 + 0.5F);
        vertex(vc, pose, f17, f4, f18, r, g, b, 1.0F, f31);
        vertex(vc, pose, f15, f4, f16, r, g, b, 0.5F, f31);

        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose,
                                float x, float y, float z,
                                int red, int green, int blue,
                                float u, float v) {
        consumer.addVertex(pose, x, y, z)
                .setColor(red, green, blue, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
