package org.confluence.mod.client.summoner.renderer.minion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.model.LyraModelRenderer;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.FinchMinion;

public class FinchRenderer extends AbstractAttachmentEntityRenderer<FinchMinion> {

    @Override
    protected RenderContext<FinchMinion> createContext(FinchMinion finch, float partialTick) {
        return RenderContext.<FinchMinion>builder()
                .model(new ModelConfig<FinchMinion>()
                        .translateOffset(0, -0.1f, 0)
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }

    @Override
    protected void render(FinchMinion finch, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode, RenderContext<FinchMinion> context, float partialTick, int packedLight, float alpha) {
        float ageTicks = finch.idleBlend == 1 ? 3f : finch.getTickCount() + partialTick;
        LyraModelRenderer.geo(Confluence.asResource("finch_baby"))
                .animation("move.fly", ageTicks)
                .light(packedLight)
                .alpha(alpha)
                .render(poseStack, bufferSource);
    }
}
