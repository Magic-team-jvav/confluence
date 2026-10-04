package org.confluence.mod.client.summoner.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.summoner.LyraRenderTypes;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.RenderUtil;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

/**
 * 圆锥拖尾上下文：相邻平滑节点间画截面壳面，头粗尾细。
 * <p>
 * 适用于球状、圆形、能量弹、魔法球等实体。
 * </p>
 * <p>
 * 形态对齐 Lyra 1.21.1.13（{@code first.lyra.client.render.trail.ConeTrailContext}）。
 * </p>
 *
 * @param <T> 实体类型
 */
public class ConeTrailContext<T extends AttachmentEntity> extends TrailContext<T> {

    /** 拖尾头部最大半径 */
    public float maxRadius = 0.2F;

    /** 最小半径比例，控制尾端不会完全缩成一点 */
    public float minRadiusRatio = 0.0F;

    /** 圆锥截面正多边形边数 */
    public int resolution = 6;

    public ConeTrailContext<T> maxRadius(float radius) {
        this.maxRadius = radius;
        return this;
    }

    public ConeTrailContext<T> minRadiusRatio(float ratio) {
        this.minRadiusRatio = ratio;
        return this;
    }

    public ConeTrailContext<T> resolution(int resolution) {
        this.resolution = resolution;
        return this;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context) {
        renderCone(poseStack, bufferSource, context, buildSmoothNodes(context.entity, context.visualNode, context.partialTick));
    }

    /**
     * 渲染圆锥主体：相邻平滑节点间画 {@link #resolution} 边截面壳面。
     * <p>
     * 半径 = maxRadius × (minRadiusRatio + (1 - minRadiusRatio) × fadeOut(progress))，
     * 头粗尾细；颜色与透明度由 {@link #colorFunction} / {@link #fadeOut} 决定。
     * 子类可传入自己构建的平滑节点以复用主体渲染。
     * </p>
     */
    protected void renderCone(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context, List<InterpolatedNode> nodes) {
        int nodeCount = nodes.size();
        if (nodeCount > 1) {
            int quadCount = (nodeCount - 1) * resolution;
            int vertexCount = quadCount * 4;
            VertexConsumer buffer = bufferSource.getBuffer(LyraRenderTypes.getTrail());
            Matrix4f matrix = new Matrix4f(poseStack.last().pose());
            Vec3 renderPos = context.visualNode.pos();
            float[] cosArr = getCosArray(resolution);
            float[] sinArr = getSinArray(resolution);
            Vector3f currV1 = new Vector3f(), currV2 = new Vector3f(), prevV1 = new Vector3f(), prevV2 = new Vector3f();
            // 与 RibbonTrailContext 相同的提交方式：收集后交给 RenderUtil.writeVertices，
            // 避免 VertexConsumer#addVertex(pose, …) 链与 NEW_ENTITY 的 7 个元素不匹配。
            float[] xyzuv = new float[vertexCount * 5];
            int[] colors = new int[vertexCount];
            int v = 0;

            for (int i = 0; i < nodeCount - 1; i++) {
                InterpolatedNode curr = nodes.get(i);
                InterpolatedNode prev = nodes.get(i + 1);
                float currProgress = (float) i / (nodeCount - 1);
                float prevProgress = (float) (i + 1) / (nodeCount - 1);

                float currFade = fadeOut.getFade(currProgress);
                float prevFade = fadeOut.getFade(prevProgress);
                float currRadius = maxRadius * (minRadiusRatio + (1 - minRadiusRatio) * currFade);
                float prevRadius = maxRadius * (minRadiusRatio + (1 - minRadiusRatio) * prevFade);

                int currARGB = packColor(colorFunction.getColor(context.entity, currProgress, context.partialTick), currFade * (200F / 255F));
                int prevARGB = packColor(colorFunction.getColor(context.entity, prevProgress, context.partialTick), prevFade * (200F / 255F));

                float crx = (float) (curr.pos().x - renderPos.x), cry = (float) (curr.pos().y - renderPos.y), crz = (float) (curr.pos().z - renderPos.z);
                float prx = (float) (prev.pos().x - renderPos.x), pry = (float) (prev.pos().y - renderPos.y), prz = (float) (prev.pos().z - renderPos.z);

                for (int j = 0; j < resolution; j++) {
                    float cos1 = cosArr[j], sin1 = sinArr[j], cos2 = cosArr[j + 1], sin2 = sinArr[j + 1];
                    currV1.set(cos1 * currRadius, sin1 * currRadius, 0).rotate(curr.rot());
                    currV2.set(cos2 * currRadius, sin2 * currRadius, 0).rotate(curr.rot());
                    prevV1.set(cos1 * prevRadius, sin1 * prevRadius, 0).rotate(prev.rot());
                    prevV2.set(cos2 * prevRadius, sin2 * prevRadius, 0).rotate(prev.rot());

                    v = putVertex(xyzuv, colors, v, matrix, crx + currV1.x, cry + currV1.y, crz + currV1.z, 0F, 0F, currARGB);
                    v = putVertex(xyzuv, colors, v, matrix, crx + currV2.x, cry + currV2.y, crz + currV2.z, 1F, 0F, currARGB);
                    v = putVertex(xyzuv, colors, v, matrix, prx + prevV2.x, pry + prevV2.y, prz + prevV2.z, 1F, 1F, prevARGB);
                    v = putVertex(xyzuv, colors, v, matrix, prx + prevV1.x, pry + prevV1.y, prz + prevV1.z, 0F, 1F, prevARGB);
                }
            }
            RenderUtil.writeVertices(buffer, xyzuv, colors, LightTexture.FULL_BRIGHT, v);
        }
    }
}
