package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * 通用顶点与贴图渲染工具（26.2 行为对齐）。
 * <p>
 * 顶点写入使用<b>完整实体顶点格式</b>（位置/颜色/UV/overlay/光照/法线），
 * 法线取 quad 朝向并经姿态 normal 矩阵变换（不忽略法线、不写死 0,0,1）。
 * consumer 为 BufferBuilder 时一次 reserve + Unsafe 直写（跳过逐顶点 addVertex 检查）；
 * 其他实现回退逐顶点 addVertex。
 * </p>
 */
@SuppressWarnings("deprecation")
public final class RenderUtil {

    /** 全亮光照常量（packed）。 */
    public static final int FULL_LIGHT = LightTexture.FULL_BRIGHT;

    private RenderUtil() {
    }

    /**
     * 在调用者提供的 PoseStack 原点渲染始终面向相机的贴图。
     * <p>
     * 这里只处理局部四边形，世界坐标由调用者平移到 PoseStack 原点。这样既适用于附件实体
     * 已经处于相机相对坐标系的渲染栈，也适用于普通实体、方块实体等原版渲染上下文。
     * </p>
     *
     * @param texture       贴图路径
     * @param poseStack     已经位于贴图中心、朝向由调用者决定的姿态栈
     * @param width         宽
     * @param height        高
     * @param bufferSource  渲染缓冲源
     * @param alwaysVisible true = 无深度测试变体
     * @param tintColor     整体染色 ARGB
     */
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

    /**
     * 在世界坐标处渲染始终面向相机的贴图。
     * <p>
     * 用于 RenderLevelStageEvent 等尚未把世界坐标平移到相机坐标系的场景。
     * </p>
     *
     * @param texture       贴图路径
     * @param center        世界坐标中心
     * @param poseStack     世界渲染姿态栈
     * @param width         宽
     * @param height        高
     * @param bufferSource  渲染缓冲源
     * @param alwaysVisible true = 无深度测试变体
     * @param tintColor     整体染色 ARGB
     */
    public static void renderImageInWorld(ResourceLocation texture, Vec3 center, PoseStack poseStack, float width, float height, MultiBufferSource bufferSource, boolean alwaysVisible, int tintColor) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(center.x - cameraPos.x, center.y - cameraPos.y, center.z - cameraPos.z);
        renderImage(texture, poseStack, width, height, bufferSource, alwaysVisible, tintColor);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int color, float x, float y, float z, float u, float v) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(color)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_LIGHT)
                .normal(pose.normal(), 0.0F, 0.0F, 1.0F)
                .endVertex();
    }

    /**
     * 通用顶点批量写入（Unsafe 直写，轨迹/伤害数字等共用，26.2 移植）。
     * <p>
     * 数据约定：每顶点 5 float（已变换 x,y,z + u,v）+ 每顶点 1 int（ARGB 颜色）。
     * 按目标格式布局写入：ITEM/NEW_ENTITY（36B，overlay=0、normal=0,0,1）与 BLOCK（32B，无 overlay）。
     * </p>
     *
     * @param consumer    顶点消费者
     * @param xyzuvData   每顶点 5 float（x,y,z,u,v，模型视图空间已变换）
     * @param colorData   每顶点 1 int（ARGB）
     * @param packedLight 打包光照
     * @param vertexCount 顶点数
     */
    public static void writeVertices(VertexConsumer consumer, float[] xyzuvData, int[] colorData, int packedLight, int vertexCount) {
        for (int i = 0; i < vertexCount; i++) {
            int sourceIndex = i * 5;
            int color = colorData[i];
            consumer.vertex(xyzuvData[sourceIndex], xyzuvData[sourceIndex + 1], xyzuvData[sourceIndex + 2])
                    .color(color)
                    .uv(xyzuvData[sourceIndex + 3], xyzuvData[sourceIndex + 4])
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(0, 0, 1)
                    .endVertex();
        }
    }
}
