package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.minion.HornetMinion;

public class HornetRenderer extends AbstractAttachmentEntityGeoRenderer<HornetMinion> {

    public HornetRenderer() {
        super(Confluence.asResource("entity/summon/hornet_baby"));
    }

    @Override
    protected RenderContext<HornetMinion> createContext(HornetMinion hornet, float partialTick) {
        return RenderContext.<HornetMinion>builder()
                .model(new ModelConfig<HornetMinion>()
                        .scale(0.6F)
                        .translateOffset(0, 0, 0)
                        .rotationOffset(180, -35, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }
}
