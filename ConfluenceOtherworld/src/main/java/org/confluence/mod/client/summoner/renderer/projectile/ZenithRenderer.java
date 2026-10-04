package org.confluence.mod.client.summoner.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.RenderUtil;
import org.confluence.mod.client.summoner.model.LyraModelRenderer;
import org.confluence.mod.client.summoner.trail.RibbonTrailContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.projectile.Zenith;

public class ZenithRenderer extends AbstractAttachmentEntityRenderer<Zenith> {

    private static final ResourceLocation BURST_TEXTURE = Confluence.asResource("textures/zenith.png");

    @Override
    protected RenderContext<Zenith> createContext(Zenith zenith, PathNode visualNode, float partialTick, int packedLight) {
        if (zenith.getTickCount() < 1) {
            return null;
        }
        int historyLength = 6;
        if (zenith.getTickCount() <= 6) {
            historyLength = zenith.getTickCount();
        } else if (zenith.getTickCount() > 8) {
            historyLength = 13 - zenith.getTickCount();
        }
        float tick = zenith.getTickCount() + partialTick;
        float alpha = 1;
        if (tick < 4) {
            alpha = Math.max(0.21F, Math.min(1F, tick / 4F));
        }
        if (tick > 8) {
            alpha = Math.max(0.102F, Math.min(1F, (12 - tick) / 4F));
        }
        if (zenith.alpha < alpha) {
            alpha = zenith.alpha;
        }
        RenderContext<Zenith> context = new RenderContext<>(zenith, visualNode, partialTick, packedLight);
        if (zenith.alpha > 0.5) {
            context.trail(new RibbonTrailContext<Zenith>()
                                  .upOffset(1.32575F)
                                  .timer(12)
                                  .colorRGB(zenith.renderType.getColorARBG(alpha))
                                  .segmentsPerNode(6)
                                  .historyLength(historyLength));
        }
        return context.model(new ModelContext()
                                     .scale(2)
                                     .translateOffset(-0.5F, -0.5F, -0.5F)
                                     .rotationOffset(0, 90, 45));
    }

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        Zenith zenith = context.entity;
        DynamicLightDispatcher.INSTANCE.addLightSource(context.visualNode.pos(), 8);
        LyraModelRenderer.json(Confluence.asResource("projectile/zenith/" + zenith.renderType.textureName()))
                .color(context.color.argbInt())
                .light(RenderUtil.FULL_LIGHT)
                .render(poseStack, bufferSource);
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        if (context.trail != null) {
            Zenith zenith = context.entity;
            float tick = zenith.getTickCount() + context.partialTick;
            float alpha = 0;
            if (tick >= 4 && tick <= 5) {
                alpha = tick - 4F;
            }
            if (tick >= 5 && tick <= 6) {
                alpha = 6 - tick;
            }
            if (alpha > 0) {
                PathNode renderNode = zenith.getRenderNode(context.partialTick);
                Vec3 pos = renderNode.pos().add(zenith.getLookAngle().scale(1.15));
                RenderUtil.renderImage(BURST_TEXTURE, pos, 3 * alpha, 0.75F * alpha, bufferSource, false,
                        zenith.renderType.getColorARBG(alpha));
            }
        }
    }

    @Override
    protected float getAlphaModify() {
        Zenith zenith = context.entity;
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            return Math.min(super.getAlphaModify(), zenith.alpha);
        }
        float tick = zenith.getTickCount() + context.partialTick;
        float alpha = 1;
        if (tick < 4) {
            alpha = Math.max(0.21F, Math.min(1F, tick / 4F));
        }
        if (tick > 8) {
            alpha = Math.max(0.102F, Math.min(1F, (12 - tick) / 4F));
        }
        return Math.min(alpha, zenith.alpha);
    }
}
