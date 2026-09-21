package org.confluence.mod.client.summoner.renderer.minion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.SanguineBatMinion;

public class SanguineBatRenderer extends AbstractAttachmentEntityGeoRenderer<SanguineBatMinion> {

    public SanguineBatRenderer() {
        super(Confluence.asResource("entity/summon/sanguine_bat"));
    }

    @Override
    protected RenderContext<SanguineBatMinion> createContext(SanguineBatMinion bat, float partialTick) {
        return RenderContext.<SanguineBatMinion>builder()
                .model(new ModelConfig<SanguineBatMinion>()
                        .scale(0.5f)
                        .rotationOffset(180, 0, 0)
                        .translateOffset(0, -0.5f, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }

    @Override
    protected void render(SanguineBatMinion entity, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode, RenderContext<SanguineBatMinion> context, float partialTick, int packedLight, float alpha) {
        super.render(entity, poseStack, bufferSource, visualNode, context, partialTick, LightTexture.FULL_BRIGHT, alpha);
    }
}
