package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.minion.BloodBatMinion;

public class BloodBatRenderer extends AbstractAttachmentEntityGeoRenderer<BloodBatMinion> {

    public BloodBatRenderer() {
        super(Confluence.asResource("entity/summon/vampire_bat"));
    }

    @Override
    protected RenderContext<BloodBatMinion> createContext(BloodBatMinion bat, float partialTick) {
        return RenderContext.<BloodBatMinion>builder()
                .model(new ModelConfig<BloodBatMinion>()
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }
}
