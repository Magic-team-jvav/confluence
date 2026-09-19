package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.minion.IronGolemMinion;

public class IronGolemRenderer extends AbstractAttachmentEntityGeoRenderer<IronGolemMinion> {

    public IronGolemRenderer() {
        super(Confluence.asResource("entity/summon/iron_golem_32"));
    }

    @Override
    protected RenderContext<IronGolemMinion> createContext(IronGolemMinion golem, float partialTick) {
        return RenderContext.<IronGolemMinion>builder()
                .model(new ModelConfig<IronGolemMinion>()
                        .rotationOffset(180, 0, 0))
                .build();
    }
}
