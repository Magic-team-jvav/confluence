package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.IronGolemMinion;

public class IronGolemRenderer extends AbstractAttachmentEntityGeoRenderer<IronGolemMinion> {

    public IronGolemRenderer() {
        super(Confluence.asResource("entity/summon/iron_golem_32"));
    }

    @Override
    protected RenderContext<IronGolemMinion> createContext(IronGolemMinion golem, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(golem, visualNode, partialTick, packedLight)
                .model(new ModelContext()
                        .rotationOffset(180, 0, 0));
    }
}
