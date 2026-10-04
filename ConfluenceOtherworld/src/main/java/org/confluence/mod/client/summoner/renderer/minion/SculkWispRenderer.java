package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.SculkWispMinion;

public class SculkWispRenderer extends AbstractAttachmentEntityGeoRenderer<SculkWispMinion> {

    public SculkWispRenderer() {
        super(Confluence.asResource("entity/summon/sculk_wisp"));
    }

    @Override
    protected RenderContext<SculkWispMinion> createContext(SculkWispMinion wisp, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(wisp, visualNode, partialTick, packedLight)
                .model(new ModelContext()
                        .translateOffset(0, -0.245f, 0)
                        .rotationOffset(-180, 0, 0)
                        .alphaDistanceFactor(1.5F));
    }
}
