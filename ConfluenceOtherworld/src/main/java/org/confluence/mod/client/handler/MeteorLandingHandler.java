package org.confluence.mod.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.network.s2c.MeteoriteLocationPacketS2C;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public final class MeteorLandingHandler {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/environment/meteor.png");
    private static final float RADIUS = 5.0F;
    private static @Nullable GlobalPos globalPos = null;
    private static @Nullable Vec3 location = null;
    private static int tickUntilLanding = 0;

    private static int totalLandingTick = 0;
    private static float alpha = 0.0F;
    private static float pitch = -Mth.HALF_PI;
    private static float pitchO = -Mth.HALF_PI;
    private static float yaw = Mth.HALF_PI;
    private static float yawO = Mth.HALF_PI;
    private static @Nullable Vec3 vector = null;
    private static double distance = 0.0;
    private static float v0 = 0;
    private static float v1 = 0;

    public static void handle(Minecraft minecraft, @Nullable Player player) {
        if (player == null) {
            globalPos = null;
            location = null;
            tickUntilLanding = 0;
            return;
        }
        if (minecraft.isPaused()) return;
        if (tickUntilLanding > 0) {
            pitchO = pitch;
            yawO = yaw;
            calculate(player);
            tickUntilLanding--;
            v0 = 0.25F * ((tickUntilLanding >> 1) % 4);
            v1 = v0 + 0.25F;
        } else {
            location = null;
        }
    }

    public static void handlePacket(MeteoriteLocationPacketS2C packet, Player player) {
        globalPos = GlobalPos.of(Level.OVERWORLD, packet.location());
        if (packet.tickUntilLanding() <= 0) {
            location = null;
            tickUntilLanding = 0;
            return;
        }
        location = packet.location().getCenter();
        tickUntilLanding = packet.tickUntilLanding();

        totalLandingTick = tickUntilLanding;
        calculate(player);
        pitchO = pitch;
        yawO = yaw;
    }

    private static void calculate(Player player) {
        vector = player.position().subtract(location);
        distance = vector.length() * 0.0625; // 1.0 / 16.0
        Vec3 vec = vector.normalize();
        alpha = 1.0F - (float) tickUntilLanding / (float) totalLandingTick; // 0.0 -> 1.0
        float ratio = Mth.lerp(alpha, 0.3F, 1.0F); // 0.3 -> 1.0
        pitch = -Mth.HALF_PI * ratio;
        yaw = Mth.HALF_PI * ratio - (float) Math.atan2(vec.z, vec.x);
    }

    public static void render(RenderLevelStageEvent event) {
        if (location == null || vector == null) return;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || player.level().dimension() != Level.OVERWORLD) return;
        if (distance < event.getLevelRenderer().getLastViewDistance()) return;
        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        Tesselator tesselator = Tesselator.getInstance();
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(event.getModelViewMatrix());
        poseStack.mulPose(Axis.YP
                .rotation(Mth.lerp(partialTick, yawO, yaw))
                .rotateX(Mth.lerp(partialTick, pitchO, pitch)));

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        Matrix4f matrix4f = poseStack.last().pose().rotate(LibClientUtils.ANGLE_45);
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.addVertex(matrix4f, -RADIUS, 100, -RADIUS).setUv(0.0F, v1).setColor(1.0F, 1.0F, 1.0F, alpha);
        bufferBuilder.addVertex(matrix4f, RADIUS, 100, -RADIUS).setUv(1.0F, v1).setColor(1.0F, 1.0F, 1.0F, alpha);
        bufferBuilder.addVertex(matrix4f, RADIUS, 100, RADIUS).setUv(1.0F, v0).setColor(1.0F, 1.0F, 1.0F, alpha);
        bufferBuilder.addVertex(matrix4f, -RADIUS, 100, RADIUS).setUv(0.0F, v0).setColor(1.0F, 1.0F, 1.0F, alpha);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    public static @Nullable GlobalPos getGlobalPos() {
        return globalPos;
    }
}
