package org.confluence.mod.client.summoner.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 水滴拖尾配置（圆锥 + 头部半球）。
 * <p>
 * 适用于需要圆润头部的拖尾效果。圆锥主体复用 {@link ConeTrailConfig}，
 * 本类仅追加头部半球。使用 10 参数 addVertex 快速路径。
 * </p>
 *
 * @param <T> 实体类型
 */
public class DropletTrailConfig<T extends AttachmentEntity> extends ConeTrailConfig<T> {

    @Override
    public void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, PathNode visualNode, RenderType renderType) {
        RenderSetup<T> setup = beginRender(entity, poseStack, bufferSource, partialTick, visualNode, renderType);
        if (setup == null) {
            return;
        }
        // 圆锥主体（复用父类）
        renderConeBody(setup);
        // 头部半球
        renderHeadHemisphere(setup);
        flushVertices(setup.consumer);
    }

    /**
     * 渲染头部半球：在首个平滑节点处画半圆封顶。
     */
    protected void renderHeadHemisphere(RenderSetup<T> setup) {
        VertexConsumer consumer = setup.consumer;
        Matrix4f matrix = setup.matrix;
        T entity = setup.entity;

        float[] cosArr = getCosArray(resolution);
        float[] sinArr = getSinArray(resolution);

        InterpolatedNode headNode = setup.smoothNodes.get(0);
        float headFade = fadeOut.getFade(0);
        float headRadius = maxRadius * (minRadiusRatio + (1 - minRadiusRatio) * headFade);
        int headColor = colorFunction.getColor(entity, 0, setup.partialTick);
        int headARGB = packColor(headColor, headFade * (200f / 255f));

        float hrx = (float)(headNode.pos().x - setup.renderPos.x);
        float hry = (float)(headNode.pos().y - setup.renderPos.y);
        float hrz = (float)(headNode.pos().z - setup.renderPos.z);
        int hemisphereSegments = Math.max(2, resolution / 2);

        Vector3f v1 = new Vector3f(), v2 = new Vector3f(), v3 = new Vector3f(), v4 = new Vector3f();

        for (int lat = 0; lat < hemisphereSegments; lat++) {
            float latAngle1 = (float) (Math.PI / 2 * lat / hemisphereSegments);
            float latAngle2 = (float) (Math.PI / 2 * (lat + 1) / hemisphereSegments);
            float r1 = (float) Math.cos(latAngle1) * headRadius;
            float r2 = (float) Math.cos(latAngle2) * headRadius;
            float h1 = (float) Math.sin(latAngle1) * headRadius;
            float h2 = (float) Math.sin(latAngle2) * headRadius;

            for (int lon = 0; lon < resolution; lon++) {
                float cos1 = cosArr[lon], sin1 = sinArr[lon];
                float cos2 = cosArr[lon + 1], sin2 = sinArr[lon + 1];

                v1.set(cos1 * r1, sin1 * r1, h1).rotate(headNode.rot());
                v2.set(cos2 * r1, sin2 * r1, h1).rotate(headNode.rot());
                v3.set(cos2 * r2, sin2 * r2, h2).rotate(headNode.rot());
                v4.set(cos1 * r2, sin1 * r2, h2).rotate(headNode.rot());

                emitQuad(consumer, matrix,
                        hrx + v1.x, hry + v1.y, hrz + v1.z, headARGB,
                        hrx + v2.x, hry + v2.y, hrz + v2.z, headARGB,
                        hrx + v3.x, hry + v3.y, hrz + v3.z, headARGB,
                        hrx + v4.x, hry + v4.y, hrz + v4.z, headARGB);
            }
        }
    }
}
