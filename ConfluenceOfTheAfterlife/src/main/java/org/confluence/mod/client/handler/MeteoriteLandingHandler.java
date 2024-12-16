package org.confluence.mod.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.network.s2c.MeteoriteLocationPacketS2C;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class MeteoriteLandingHandler {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/painting/magic_harp.png");
    private static final float RADIUS = 5.0F;
    private static Vec3 location;
    private static int tickUntilLanding = -1;

    public static void handleMeteorite(MeteoriteLocationPacketS2C packet) {
        location = packet.location().getCenter();
        tickUntilLanding = packet.tickUntilLanding();
    }

    public static void render(RenderLevelStageEvent event) {
        if (location == null) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.level().dimension() != Level.OVERWORLD) return;
        Vector3f vector = player.position().subtract(location).toVector3f();
        if (vector.length() < event.getLevelRenderer().getLastViewDistance()) return;
        Tesselator tesselator = Tesselator.getInstance();
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(event.getModelViewMatrix());

        vector = vector.normalize();
        float rot = Mth.HALF_PI - (float) Math.atan2(vector.z, vector.x);
        poseStack.mulPose(Axis.YP.rotation(rot).rotateX(-Mth.HALF_PI));

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        Matrix4f matrix4f = poseStack.last().pose();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, -RADIUS, 100, -RADIUS).setUv(0.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, RADIUS, 100, -RADIUS).setUv(1.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, RADIUS, 100, RADIUS).setUv(1.0F, 0.0F);
        bufferBuilder.addVertex(matrix4f, -RADIUS, 100, RADIUS).setUv(0.0F, 0.0F);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }
}
