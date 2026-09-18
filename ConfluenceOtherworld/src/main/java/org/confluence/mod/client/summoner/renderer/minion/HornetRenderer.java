package org.confluence.mod.client.summoner.renderer.minion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.model.LyraModelRenderer;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.HornetMinion;

public class HornetRenderer extends AbstractAttachmentEntityRenderer<HornetMinion> {

    @Override
    protected RenderContext<HornetMinion> createContext(HornetMinion hornet, float partialTick) {
        return RenderContext.<HornetMinion>builder()
                .model(new ModelConfig<HornetMinion>()
                        .scale(0.6f)
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }

    @Override
    protected void render(HornetMinion hornet, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode, RenderContext<HornetMinion> context, float partialTick, int packedLight, float alpha) {
        if (hornet.cooldown > 0) {
            float progress = 1 - Mth.lerp(partialTick, hornet.lastCooldown, hornet.cooldown) / hornet.maxCooldown;
            LyraModelRenderer.geo(Confluence.asResource("hornet_baby"))
                    .animationProgress("attack.cast", progress)
                    .translate(-0.5F, -0.21F, -0.5F)
                    .light(packedLight)
                    .alpha(alpha)
                    .render(poseStack, bufferSource);
        } else {
            LyraModelRenderer.geo(Confluence.asResource("hornet_baby"))
                    .animation("misc.idle", hornet.getTickCount() + partialTick)
                    .translate(-0.5F, -0.21F, -0.5F)
                    .light(packedLight)
                    .alpha(alpha)
                    .render(poseStack, bufferSource);
        }
    }
}
