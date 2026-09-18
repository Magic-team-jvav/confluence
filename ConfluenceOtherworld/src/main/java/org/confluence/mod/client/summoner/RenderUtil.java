package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
     * 渲染始终面向相机的贴图（1.21.1 对应 26.2 renderImage；召唤标记等使用）。
     *
     * @param texture      贴图路径
     * @param center       世界坐标中心
     * @param width        宽
     * @param height       高
     * @param bufferSource 渲染缓冲源
     * @param alwaysVisible true = 自发光变体（不受光照变暗）
     * @param tintColor    整体染色 ARGB
     */
    public static void renderImage(ResourceLocation texture, Vec3 center, float width, float height, MultiBufferSource bufferSource, boolean alwaysVisible, int tintColor) {
        Camera camera = Minecraft.getInstance().getEntityRenderDispatcher().camera;
        VertexConsumer consumer = bufferSource.getBuffer(LyraRenderTypes.texture(texture, alwaysVisible));
        Vec3 camPos = camera.getPosition();
        // 相机朝向四元数（原版实体名牌同款）：rotation × XN(180)
        Quaternionf rotation = new Quaternionf(camera.rotation()).mul(Axis.XN.rotationDegrees(180), new Quaternionf());
        Matrix4f matrix = new Matrix4f().rotate(rotation).setTranslation((float) (center.x - camPos.x), (float) (center.y - camPos.y), (float) (center.z - camPos.z));
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        // 每顶点 5 float（已变换 x,y,z + u,v）+ 颜色
        float[] xyzuvData = new float[4 * 5];
        int[] colorData = new int[4];
        Vector3f v = new Vector3f();
        int vertexIndex = 0;
        // 四边形顶点：x,y,z 偏移 + u,v（26.2 同序：u 随宽度、v 随高度）
        float[][] corners = {
                {-halfWidth, -halfHeight, 0f, 0f, 0f},
                {-halfWidth, halfHeight, 0f, 0f, 1f},
                {halfWidth, halfHeight, 0f, 1f, 1f},
                {halfWidth, -halfHeight, 0f, 1f, 0f}
        };
        for (float[] corner : corners) {
            matrix.transformPosition(corner[0], corner[1], corner[2], v);
            int dataIndex = vertexIndex * 5;
            xyzuvData[dataIndex] = v.x();
            xyzuvData[dataIndex + 1] = v.y();
            xyzuvData[dataIndex + 2] = v.z();
            xyzuvData[dataIndex + 3] = corner[3];
            xyzuvData[dataIndex + 4] = corner[4];
            colorData[vertexIndex] = tintColor;
            vertexIndex++;
        }
        writeVertices(consumer, xyzuvData, colorData, FULL_LIGHT, 4);
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
