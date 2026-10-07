package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class RenderUtil {

    public static final int FULL_LIGHT = LightTexture.FULL_BRIGHT;

    private RenderUtil() {
    }

    public static void renderImage(ResourceLocation texture, PoseStack poseStack, float width, float height, MultiBufferSource bufferSource, boolean alwaysVisible, int tintColor) {
        VertexConsumer consumer = bufferSource.getBuffer(LyraRenderTypes.texture(texture, alwaysVisible));
        float halfWidth = width * 0.5F;
        float halfHeight = height * 0.5F;
        poseStack.pushPose();
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        PoseStack.Pose pose = poseStack.last();
        vertex(consumer, pose, tintColor, -halfWidth, -halfHeight, 0.0F, 0.0F, 1.0F);
        vertex(consumer, pose, tintColor, halfWidth, -halfHeight, 0.0F, 1.0F, 1.0F);
        vertex(consumer, pose, tintColor, halfWidth, halfHeight, 0.0F, 1.0F, 0.0F);
        vertex(consumer, pose, tintColor, -halfWidth, halfHeight, 0.0F, 0.0F, 0.0F);
        poseStack.popPose();
    }

    public static void renderImage(ResourceLocation texture, Vec3 center, float width, float height, MultiBufferSource bufferSource, boolean alwaysVisible, int tintColor) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        VertexConsumer consumer = bufferSource.getBuffer(LyraRenderTypes.texture(texture, alwaysVisible));
        Vec3 cameraPos = camera.getPosition();
        Quaternionf rotation = new Quaternionf(camera.rotation()).mul(Axis.XN.rotationDegrees(180.0F), new Quaternionf());
        Matrix4f matrix = new Matrix4f().rotate(rotation).setTranslation(
                (float) (center.x - cameraPos.x), (float) (center.y - cameraPos.y), (float) (center.z - cameraPos.z));
        float halfWidth = width * 0.5F;
        float halfHeight = height * 0.5F;
        worldVertex(consumer, matrix, tintColor, -halfWidth, -halfHeight, 0.0F, 0.0F);
        worldVertex(consumer, matrix, tintColor, -halfWidth, halfHeight, 0.0F, 1.0F);
        worldVertex(consumer, matrix, tintColor, halfWidth, halfHeight, 1.0F, 1.0F);
        worldVertex(consumer, matrix, tintColor, halfWidth, -halfHeight, 1.0F, 0.0F);
    }

    private static void worldVertex(VertexConsumer consumer, Matrix4f matrix, int tintColor, float x, float y, float u, float v) {
        Vector3f position = matrix.transformPosition(x, y, 0.0F, new Vector3f());
        consumer.addVertex(position.x, position.y, position.z)
                .setColor(tintColor)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_LIGHT)
                .setNormal(0.0F, 0.0F, 1.0F);
    }

    public static void renderImageInWorld(ResourceLocation texture, Vec3 center, PoseStack poseStack, float width, float height, MultiBufferSource bufferSource, boolean alwaysVisible, int tintColor) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(center.x - cameraPos.x, center.y - cameraPos.y, center.z - cameraPos.z);
        renderImage(texture, poseStack, width, height, bufferSource, alwaysVisible, tintColor);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int color, float x, float y, float z, float u, float v) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_LIGHT)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }
}
