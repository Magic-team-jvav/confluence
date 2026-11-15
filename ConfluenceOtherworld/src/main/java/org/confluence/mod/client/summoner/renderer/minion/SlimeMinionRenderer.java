package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.SlimeMinion;

public class SlimeMinionRenderer extends AbstractAttachmentEntityGeoRenderer<SlimeMinion> {

    public SlimeMinionRenderer() {
        super(Confluence.asResource("entity/summon/slime_baby"));
    }

    @Override
    protected RenderContext<SlimeMinion> createContext(SlimeMinion slime, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(slime, visualNode, partialTick, packedLight)
                .model(new ModelContext()
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F));
    }
}
