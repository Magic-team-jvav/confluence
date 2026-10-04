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

/**
 * 天顶剑飞剑渲染器：按剑型渲染对应 JSON 模型 + 丝带拖尾 + 挥砍爆发贴图。
 * <p>
 * 行为对齐源实现 {@code first.summoner.client.attachmentEntityRenderer.projectile.ZenithRenderer}：
 * 出生 tick 不渲染；拖尾长度随 tick 先伸后收（{@code <=6} 用 tickCount，{@code >8} 用 {@code 13-tickCount}）；
 * 仅当 {@code alpha > 0.5} 时挂拖尾；tick 4~6 之间在剑尖绘制一次爆发贴图。
 * </p>
 */
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
        float alpha = tickAlpha(zenith, partialTick);
        RenderContext<Zenith> context = new RenderContext<>(zenith, visualNode, partialTick, packedLight);
        if (zenith.alpha > 0.5) {
            // upOffset 返回 RibbonTrailContext，基类链式方法返回 TrailContext，故子类方法需先调用
            context = context.trail(new RibbonTrailContext<Zenith>()
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
        DynamicLightDispatcher.INSTANCE.addLightSource(context.visualNode.pos(), 0.5f);
        LyraModelRenderer.json(Confluence.asResource("projectile/zenith/" + context.entity.renderType.textureName()))
                .color(context.color.argbInt())
                .light(RenderUtil.FULL_LIGHT)
                .render(poseStack, bufferSource);
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        if (context.trail == null) {
            return;
        }
        Zenith zenith = context.entity;
        float tick = zenith.getTickCount() + context.partialTick;
        float alpha = 0;
        if (tick >= 4 && tick <= 5) {
            alpha = tick - 4F;
        }
        if (tick >= 5 && tick <= 6) {
            alpha = 6 - tick;
        }
        if (alpha <= 0) {
            return;
        }
        PathNode renderNode = zenith.getRenderNode(context.partialTick);
        Vec3 pos = renderNode.pos().add(zenith.getLookAngle().scale(1.15));
        RenderUtil.renderImage(BURST_TEXTURE, pos, 3 * alpha, 0.75F * alpha, bufferSource, false, zenith.renderType.getColorARBG(alpha));
    }

    @Override
    protected float getAlphaModify() {
        Zenith zenith = context.entity;
        float alpha = Minecraft.getInstance().options.getCameraType().isFirstPerson() ? super.getAlphaModify() : tickAlpha(zenith, context.partialTick);
        return Math.min(alpha, zenith.alpha);
    }

    private static float tickAlpha(Zenith zenith, float partialTick) {
        float tick = zenith.getTickCount() + partialTick;
        if (tick < 4) {
            return Math.max(0.21F, Math.min(1F, tick / 4F));
        }
        if (tick > 8) {
            return Math.max(0.102F, Math.min(1F, (12 - tick) / 4F));
        }
        return 1F;
    }
}
