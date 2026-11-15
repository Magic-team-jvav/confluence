package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.VexTotemMinion;

public class VexTotemRenderer extends AbstractAttachmentEntityGeoRenderer<VexTotemMinion> {

    public VexTotemRenderer() {
        super(Confluence.asResource("entity/summon/vex_totem"));
    }

    @Override
    protected RenderContext<VexTotemMinion> createContext(VexTotemMinion minion, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(minion, visualNode, partialTick, packedLight)
                .model(new ModelContext()
                        .scale(0.6F)
                        .translateOffset(0F, -1.3F, 0F)
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F));
    }
}
