package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/// 玩家手部与受控实体之间的通用绳线绘制工具。
///
/// <p>调用方只提供实体、玩家和颜色；第一人称近裁剪面、第三人称身体旋转、蹲伏偏移、
/// 实体插值及绳线下垂均在这里统一处理。</p>
public final class TetherRenderHelper {
    private TetherRenderHelper() {
    }

    public static void renderMainHandString(
            EntityRenderDispatcher dispatcher,
            Entity entity,
            Player player,
            float entityYOffset,
            int color,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers
    ) {
        Vec3 hand = mainHandPosition(dispatcher, player, partialTick);
        Vec3 tether = entity.getPosition(partialTick)
                .add(0.0, entityYOffset, 0.0);
        float x = (float) (hand.x - tether.x);
        float y = (float) (hand.y - tether.y);
        float z = (float) (hand.z - tether.z);
        VertexConsumer consumer = buffers.getBuffer(RenderType.lineStrip());
        PoseStack.Pose pose = poseStack.last();
        for (int segment = 0; segment <= 16; segment++) {
            vertex(
                    x,
                    y,
                    z,
                    consumer,
                    pose,
                    segment / 16.0F,
                    (segment + 1) / 16.0F,
                    color);
        }
    }

    private static Vec3 mainHandPosition(
            EntityRenderDispatcher dispatcher,
            Player player,
            float partialTick
    ) {
        int arm = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        if (dispatcher.options.getCameraType().isFirstPerson()
                && player == Minecraft.getInstance().player) {
            double scale = 960.0 / dispatcher.options.fov().get();
            return player.getEyePosition(partialTick).add(
                    dispatcher.camera
                            .getNearPlane()
                            .getPointOnPlane(arm * 0.525F, -0.5F)
                            .scale(scale));
        }
        float yaw = Mth.lerp(
                partialTick, player.yBodyRotO, player.yBodyRot)
                * Mth.DEG_TO_RAD;
        double sin = Mth.sin(yaw);
        double cos = Mth.cos(yaw);
        float crouch = player.isCrouching() ? -0.1875F : 0.0F;
        return player.getEyePosition(partialTick).add(
                -cos * arm * 0.35 - sin * 0.25,
                crouch - 0.65,
                -sin * arm * 0.35 + cos * 0.25);
    }

    private static void vertex(
            float x,
            float y,
            float z,
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float fraction,
            float nextFraction,
            int color
    ) {
        float px = x * fraction;
        float lowerArc = y * (fraction * fraction + fraction) * 0.5F
                + 0.25F;
        float upperArc = y * (-(fraction - 1.0F) * (fraction - 1.0F) + 1.0F)
                + 0.25F;
        float py = Mth.lerp(
                (Mth.clamp(y, -5.0F, 5.0F) + 5.0F) * 0.1F,
                upperArc,
                lowerArc);
        py += (fraction - 1.0F) * fraction * 2.0F;
        float pz = z * fraction;
        float nx = x * nextFraction - px;
        float ny = y * (nextFraction * nextFraction + nextFraction)
                * 0.5F + 0.25F - py;
        float nz = z * nextFraction - pz;
        float length = Mth.sqrt(nx * nx + ny * ny + nz * nz);
        if (length < 1.0E-5F) {
            return;
        }
        consumer.vertex(pose.pose(), px, py, pz)
                .color(color)
                .normal(
                        pose.normal(),
                        nx / length,
                        ny / length,
                        nz / length)
                .endVertex();
    }
}
