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
 * 丝带拖尾上下文：相邻平滑节点间连成四边形带，尖端上下偏移决定丝带宽度。
 * <p>
 * 适用于剑状、刀锋、扁平物体、有明显方向性的实体。
 * </p>
 * <p>
 * 形态对齐 Lyra 1.21.1.13（{@code first.lyra.client.render.trail.RibbonTrailContext}）。
 * </p>
 *
 * @param <T> 实体类型
 */
public class RibbonTrailContext<T extends AttachmentEntity> extends TrailContext<T> {

    /** 尖端方向的偏移距离 */
    public float upOffset = 0F;

    /** 反方向的偏移距离 */
    public float downOffset = 0F;

    /** 尖端透明度增强函数 */
    public RenderContext.AlphaBoostFunction<T> tipAlphaBoost = (entity, progress) -> (1 - progress) * 20;

    /** 尖端亮度增强函数 */
    public RenderContext.BrightnessBoostFunction<T> tipBrightnessBoost = (entity, progress) -> (1 - (progress * 0.5F));

    public RibbonTrailContext<T> upOffset(float upOffset) {
        this.upOffset = upOffset;
        return this;
    }

    public RibbonTrailContext<T> downOffset(float downOffset) {
        this.downOffset = downOffset;
        return this;
    }

    public RibbonTrailContext<T> tipAlphaBoost(RenderContext.AlphaBoostFunction<T> function) {
        this.tipAlphaBoost = function;
        return this;
    }

    public RibbonTrailContext<T> tipBrightnessBoost(RenderContext.BrightnessBoostFunction<T> function) {
        this.tipBrightnessBoost = function;
        return this;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context) {
        List<InterpolatedNode> nodes = buildSmoothNodes(context.entity, context.visualNode, context.partialTick);
        int nodeCount = nodes.size();
        if (nodeCount <= 1) {
            return;
        }
        // 与旧实现（TrailConfig）一致的提交方式：先把「已做矩阵变换」的顶点收集到 float[]/int[]，
        // 再交给 RenderUtil.writeVertices 按 36 字节的 NEW_ENTITY 布局批量落盘。
        // 注意：不要改用 VertexConsumer#addVertex(pose, …) 链式写法 —— 那条链只写 6 个元素，
        // 而 NEW_ENTITY 有 7 个（Position/Color/UV0/UV1/UV2/Normal/Padding），
        // 元素数不匹配会让 endVertex() 抛异常或写出错位数据。
        VertexConsumer consumer = bufferSource.getBuffer(LyraRenderTypes.getTrail());
        Matrix4f matrix = new Matrix4f(poseStack.last().pose());
        Vec3 renderPos = context.visualNode.pos();
        int quadCount = nodeCount - 1;
        int vertexCount = quadCount * 4;
        float[] xyzuv = new float[vertexCount * 5];
        int[] colors = new int[vertexCount];
        Vector3f currTip = new Vector3f(), currBase = new Vector3f(), prevTip = new Vector3f(), prevBase = new Vector3f();
        int v = 0;
        for (int i = 0; i < quadCount; i++) {
            InterpolatedNode curr = nodes.get(i);
            InterpolatedNode prev = nodes.get(i + 1);
            float currProgress = (float) i / quadCount;
            float prevProgress = (float) (i + 1) / quadCount;

            currTip.set(0, 0, upOffset).rotate(curr.rot());
            currBase.set(0, 0, downOffset).rotate(curr.rot());
            prevTip.set(0, 0, upOffset).rotate(prev.rot());
            prevBase.set(0, 0, downOffset).rotate(prev.rot());

            int currColorRGB = colorFunction.getColor(context.entity, currProgress, context.partialTick);
            int prevColorRGB = colorFunction.getColor(context.entity, prevProgress, context.partialTick);
            float currBright = tipBrightnessBoost.getBoost(context.entity, currProgress);
            float prevBright = tipBrightnessBoost.getBoost(context.entity, prevProgress);
            float currAlphaBoost = tipAlphaBoost.getBoost(context.entity, currProgress);
            float prevAlphaBoost = tipAlphaBoost.getBoost(context.entity, prevProgress);
            int currTipColor = packColor(currColorRGB, Math.max(0F, 1F - currProgress) * 0.1F * currAlphaBoost, currBright);
            int currBaseColor = packColor(currColorRGB, Math.max(0F, 1F - currProgress * 2.5F) * 0.04F * currAlphaBoost, currBright);
            int prevTipColor = packColor(prevColorRGB, Math.max(0F, 1F - prevProgress) * 0.1F * prevAlphaBoost, prevBright);
            int prevBaseColor = packColor(prevColorRGB, Math.max(0F, 1F - prevProgress * 2.5F) * 0.04F * prevAlphaBoost, prevBright);

            float crx = (float) (curr.pos().x - renderPos.x), cry = (float) (curr.pos().y - renderPos.y), crz = (float) (curr.pos().z - renderPos.z);
            float prx = (float) (prev.pos().x - renderPos.x), pry = (float) (prev.pos().y - renderPos.y), prz = (float) (prev.pos().z - renderPos.z);

            v = putVertex(xyzuv, colors, v, matrix, crx + currTip.x, cry + currTip.y, crz + currTip.z, 0F, 0F, currTipColor);
            v = putVertex(xyzuv, colors, v, matrix, crx + currBase.x, cry + currBase.y, crz + currBase.z, 1F, 0F, currBaseColor);
            v = putVertex(xyzuv, colors, v, matrix, prx + prevBase.x, pry + prevBase.y, prz + prevBase.z, 1F, 1F, prevBaseColor);
            v = putVertex(xyzuv, colors, v, matrix, prx + prevTip.x, pry + prevTip.y, prz + prevTip.z, 0F, 1F, prevTipColor);
        }
        RenderUtil.writeVertices(consumer, xyzuv, colors, LightTexture.FULL_BRIGHT, vertexCount);
    }
}
