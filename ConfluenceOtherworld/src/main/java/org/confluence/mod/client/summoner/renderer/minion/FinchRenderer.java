package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.minion.FinchMinion;

public class FinchRenderer extends AbstractAttachmentEntityGeoRenderer<FinchMinion> {

    public FinchRenderer() {
        super(Confluence.asResource("entity/summon/finch_baby"));
    }

    @Override
    protected RenderContext<FinchMinion> createContext(FinchMinion finch, float partialTick) {
        return RenderContext.<FinchMinion>builder()
                .model(new ModelConfig<FinchMinion>()
                        .translateOffset(0, -0.1f, 0.05f)
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }
}
