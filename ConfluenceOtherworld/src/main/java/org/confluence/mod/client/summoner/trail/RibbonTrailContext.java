package org.confluence.mod.client.summoner.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.summoner.LyraRenderTypes;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

/// 按轨迹上下偏移提交带状拖尾，并保留尖端颜色与亮度变化。
public class RibbonTrailContext<T extends AttachmentEntity> extends TrailContext<T> {

    public float upOffset = 0F;

    public float downOffset = 0F;

    public RenderContext.AlphaBoostFunction<T> tipAlphaBoost = (entity, progress) -> (1 - progress) * 20;

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
        if (nodeCount > 1) {
            VertexConsumer buffer = bufferSource.getBuffer(LyraRenderTypes.getTrail());
            Matrix4f matrix = new Matrix4f(poseStack.last().pose());
            Vec3 renderPos = context.visualNode.pos();
            Vector3f currTip = new Vector3f(), currBase = new Vector3f(), prevTip = new Vector3f(), prevBase = new Vector3f();
            for (int i = 0; i < nodeCount - 1; i++) {
                InterpolatedNode curr = nodes.get(i);
                InterpolatedNode prev = nodes.get(i + 1);
                float currProgress = (float) i / (nodeCount - 1);
                float prevProgress = (float) (i + 1) / (nodeCount - 1);

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

                Vector3f v1 = matrix.transformPosition(crx + currTip.x, cry + currTip.y, crz + currTip.z, new Vector3f());
                Vector3f v2 = matrix.transformPosition(crx + currBase.x, cry + currBase.y, crz + currBase.z, new Vector3f());
                Vector3f v3 = matrix.transformPosition(prx + prevBase.x, pry + prevBase.y, prz + prevBase.z, new Vector3f());
                Vector3f v4 = matrix.transformPosition(prx + prevTip.x, pry + prevTip.y, prz + prevTip.z, new Vector3f());

                Vector3f normal = new Vector3f(v3).sub(v1).cross(new Vector3f(v2).sub(v1));
                if (normal.lengthSquared() > 1.0E-6F) {
                    normal.normalize();
                } else {
                    normal.set(0, 1, 0);
                }
                Vector3f direction = matrix.transformDirection(normal, new Vector3f());

                buffer.addVertex(v1.x, v1.y, v1.z).setColor(currTipColor).setUv(0F, 0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(direction.x, direction.y, direction.z);
                buffer.addVertex(v2.x, v2.y, v2.z).setColor(currBaseColor).setUv(1F, 0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(direction.x, direction.y, direction.z);
                buffer.addVertex(v3.x, v3.y, v3.z).setColor(prevBaseColor).setUv(1F, 1F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(direction.x, direction.y, direction.z);
                buffer.addVertex(v4.x, v4.y, v4.z).setColor(prevTipColor).setUv(0F, 1F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(direction.x, direction.y, direction.z);
            }
        }
    }
}
