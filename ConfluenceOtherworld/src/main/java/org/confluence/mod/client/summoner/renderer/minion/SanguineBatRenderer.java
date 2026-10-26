package org.confluence.mod.client.summoner.renderer.minion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.SanguineBatMinion;
import org.confluence.mod.integration.opallight.OpalLightIntegration;

public class SanguineBatRenderer extends AbstractAttachmentEntityGeoRenderer<SanguineBatMinion> {

    public SanguineBatRenderer() {
        super(Confluence.asResource("entity/summon/sanguine_bat"));
    }

    @Override
    protected RenderContext<SanguineBatMinion> createContext(SanguineBatMinion bat, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(bat, visualNode, partialTick, packedLight)
                .packedLight(LightTexture.FULL_BRIGHT)
                .model(new ModelContext()
                        .scale(0.5f)
                        .rotationOffset(180, 0, 0)
                        .translateOffset(0, -0.5f, 0)
                        .alphaDistanceFactor(1.5F));
    }
    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        if (!OpalLightIntegration.handlesAttachment(context.entity.getType().location())) {
            DynamicLightDispatcher.INSTANCE.addLightSource(context.visualNode.pos(), 0.5f);
        }
    }
}
