package org.confluence.mod.client.summoner.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 丝带拖尾配置。
 * <p>
 * 适用于剑状、刀锋、扁平物体、有明显方向性的实体。
 * 使用 10 参数 addVertex 快速路径 + matrix.transformPosition 预变换。
 * </p>
 *
 * @param <T> 实体类型
 */
public class RibbonTrailConfig<T extends AttachmentEntity> extends TrailConfig<T, RibbonTrailConfig<T>> {

    public float upOffset = 0f;
    private float downOffset = 0f;

    /** 尖端透明度增强函数 */
    public RenderContext.AlphaBoostFunction<T> tipAlphaBoost = (entity, progress) -> (1 - progress) * 20;

    /** 尖端亮度增强函数 */
    public RenderContext.BrightnessBoostFunction<T> tipBrightnessBoost = (entity, progress) -> (1 - (progress * 0.5f));

    public RibbonTrailConfig<T> upOffset(float upOffset) {
        this.upOffset = upOffset;
        return this;
    }

    public RibbonTrailConfig<T> downOffset(float downOffset) {
        this.downOffset = downOffset;
        return this;
    }

    public RibbonTrailConfig<T> tipAlphaBoost(RenderContext.AlphaBoostFunction<T> function) {
        this.tipAlphaBoost = function;
        return this;
    }

    public RibbonTrailConfig<T> tipBrightnessBoost(RenderContext.BrightnessBoostFunction<T> function) {
        this.tipBrightnessBoost = function;
        return this;
    }

    @Override
    public void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, PathNode visualNode, RenderType renderType) {
        RenderSetup<T> setup = beginRender(entity, poseStack, bufferSource, partialTick, visualNode, renderType);
        if (setup == null) {
            return;
        }

        VertexConsumer consumer = setup.consumer;
        Matrix4f matrix = setup.matrix;
        Vec3 renderPos = setup.renderPos;

        int nodeCount = setup.nodeCount();

        // 复用向量
        Vector3f currTip = new Vector3f(), currBase = new Vector3f();
        Vector3f prevTip = new Vector3f(), prevBase = new Vector3f();

        for (int i = 0; i < nodeCount - 1; i++) {
            InterpolatedNode curr = setup.smoothNodes.get(i);
            InterpolatedNode prev = setup.smoothNodes.get(i + 1);

            float currProgress = (float) i / (nodeCount - 1);
            float prevProgress = (float) (i + 1) / (nodeCount - 1);

            // 顶/底偏移向量（按节点朝向旋转）
            currTip.set(0, 0, upOffset).rotate(curr.rot());
            currBase.set(0, 0, downOffset).rotate(curr.rot());
            prevTip.set(0, 0, upOffset).rotate(prev.rot());
            prevBase.set(0, 0, downOffset).rotate(prev.rot());

            // 颜色与亮度
            int currColorRGB = colorFunction.getColor(entity, currProgress, setup.partialTick);
            int prevColorRGB = colorFunction.getColor(entity, prevProgress, setup.partialTick);
            float currBright = tipBrightnessBoost.getBoost(entity, currProgress);
            float prevBright = tipBrightnessBoost.getBoost(entity, prevProgress);
            float currAlphaBoost = tipAlphaBoost.getBoost(entity, currProgress);
            float prevAlphaBoost = tipAlphaBoost.getBoost(entity, prevProgress);

            // 尖端 alpha = scale * 0.1 * boost；基部 alpha = max(0, 1-progress*2.5) * 0.04 * boost
            float currScale = Math.max(0f, 1f - currProgress);
            float prevScale = Math.max(0f, 1f - prevProgress);
            int currTipColor = packColor(currColorRGB, currScale * 0.1f * currAlphaBoost, currBright);
            int currBaseColor = packColor(currColorRGB, Math.max(0f, 1f - currProgress * 2.5f) * 0.04f * currAlphaBoost, currBright);
            int prevTipColor = packColor(prevColorRGB, prevScale * 0.1f * prevAlphaBoost, prevBright);
            int prevBaseColor = packColor(prevColorRGB, Math.max(0f, 1f - prevProgress * 2.5f) * 0.04f * prevAlphaBoost, prevBright);

            // 本地坐标 = 节点相对位置 + 偏移，直接传给 emitQuad 内部变换
            float crx = (float)(curr.pos().x - renderPos.x);
            float cry = (float)(curr.pos().y - renderPos.y);
            float crz = (float)(curr.pos().z - renderPos.z);
            float prx = (float)(prev.pos().x - renderPos.x);
            float pry = (float)(prev.pos().y - renderPos.y);
            float prz = (float)(prev.pos().z - renderPos.z);

            emitQuad(consumer, matrix,
                    crx + currTip.x, cry + currTip.y, crz + currTip.z, currTipColor,
                    crx + currBase.x, cry + currBase.y, crz + currBase.z, currBaseColor,
                    prx + prevBase.x, pry + prevBase.y, prz + prevBase.z, prevBaseColor,
                    prx + prevTip.x, pry + prevTip.y, prz + prevTip.z, prevTipColor);
        }
        flushVertices(setup.consumer);
    }
}
