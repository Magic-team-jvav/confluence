package org.confluence.mod.client.summoner.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.RenderUtil;
import org.confluence.mod.client.summoner.model.LyraModelRenderer;
import org.confluence.mod.client.summoner.model.ZenithSwordModels;
import org.confluence.mod.client.summoner.trail.RibbonTrailContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.projectile.Zenith;
import org.confluence.mod.common.summoner.util.EasingCurve;

import java.util.ArrayList;
import java.util.List;

/// 天顶剑模型、弧形拖尾及生命周期淡出。
public class ZenithRenderer extends AbstractAttachmentEntityRenderer<Zenith> {

    @Override
    protected RenderContext<Zenith> createContext(Zenith zenith, PathNode visualNode, float partialTick, int packedLight) {
        if (zenith.getTickCount() >= 1) {
            context = super.createContext(zenith, visualNode, partialTick, packedLight);
            if (zenith.alpha > 0.3) {
                context = context.trail(new ZenithTrailContext()
                        .downOffset(-1.32575f)
                        .tipAlphaBoost((entity, progress) -> (1.0F - progress) * 20.0F * zenith.alpha).timer(1)
                        .colorRGB(zenith.renderType.getColor()));
            }
            return context.model(new ModelContext()
                    .scale(2)
                    .translateOffset(-1f, -1f, -0.5f)
                    .rotationOffset(0, 90, 45));
        } else {
            return null;
        }
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        Zenith zenith = context.entity;
        PathNode visualNode = context.visualNode;
        LyraModelRenderer.json(ZenithSwordModels.modelId(zenith.renderType))
                .color(context.color.argbInt())
                .light(RenderUtil.FULL_LIGHT)
                .render(poseStack, bufferSource);
        float light = zenith.alpha * 0.5f;
        if (zenith.getTickCount() < 2) {
            light *= context.partialTick;
        }
        DynamicLightDispatcher.INSTANCE.addLightSource(visualNode.pos(), light);
        if (context.trail != null && zenith.alpha > 0.75) {
            float alpha = 0;
            float progress = zenith.getProgress(context.partialTick);
            if (progress >= 0.3 && progress <= 0.5) {
                alpha = (progress - 0.3f) / 0.2f;
            }
            if (progress >= 0.5 && progress <= 0.7) {
                alpha = (0.7f - progress) / 0.2f;
            }
            alpha = EasingCurve.EASE_IN_OUT_QUAD.apply(alpha);
            if (alpha > 0) {
                Vec3 pos = visualNode.pos();
                int color = zenith.renderType.getColorARBG(alpha);
                RenderUtil.renderImage(Confluence.asResource("textures/zenith.png"), pos, 4 * alpha, alpha, bufferSource, false, color);
            }
        }
    }

    @Override
    protected float getAlphaModify() {
        float alpha = 1;
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            alpha = super.getAlphaModify();
        } else {
            float tick = context.entity.getTickCount() + context.partialTick;
            if (tick < 4) {
                alpha = Math.max(0.21f, Math.min(1f, tick / 4f));
            }
            if (tick > 8) {
                alpha = Math.max(0.102F, Math.min(1f, (12 - tick) / 4f));
            }
        }
        if (context.entity.alpha < alpha) {
            alpha = context.entity.alpha;
        }
        if (context.entity.getTickCount() < 2) {
            if (context.partialTick < alpha) {
                alpha = context.partialTick;
            }
        }
        return alpha;
    }

    public static class ZenithTrailContext extends RibbonTrailContext<Zenith> {

        @Override
        protected List<InterpolatedNode> buildSmoothNodes(Zenith entity, PathNode visualNode, float partialTick) {
            List<InterpolatedNode> result = new ArrayList<>();
            float tick = entity.getTickCount() + partialTick;
            int count = 60;
            if (tick <= 4) {
                count = (int) (tick * 15);
            }
            if (tick >= 7) {
                count = (int) ((11 - tick) * 15);
            }
            if (count > 0) {
                for (int i = 0; i < count && tick > 0; i++) {
                    PathNode node = entity.getRenderNodeFromTickCount(tick);
                    result.add(new InterpolatedNode(node.pos(), node.toQuaternion()));
                    tick -= 0.05f;
                }
            }
            return result;
        }
    }
}
