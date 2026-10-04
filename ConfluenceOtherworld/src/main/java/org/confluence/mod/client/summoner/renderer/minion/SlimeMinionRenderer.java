package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.minion.SlimeMinion;

public class SlimeMinionRenderer extends AbstractAttachmentEntityGeoRenderer<SlimeMinion> {

    public SlimeMinionRenderer() {
        super(Confluence.asResource("entity/summon/slime_baby"));
    }

    @Override
    protected RenderContext<SlimeMinion> createContext(SlimeMinion slime, float partialTick) {
        return RenderContext.<SlimeMinion>builder()
                .model(new ModelConfig<SlimeMinion>()
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }
}
