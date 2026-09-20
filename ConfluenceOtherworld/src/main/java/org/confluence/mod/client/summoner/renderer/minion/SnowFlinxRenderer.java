package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.minion.SnowFlinxMinion;

public class SnowFlinxRenderer extends AbstractAttachmentEntityGeoRenderer<SnowFlinxMinion> {

    public SnowFlinxRenderer() {
        super(Confluence.asResource("entity/summon/summon_snow_flinx"));
    }

    @Override
    protected RenderContext<SnowFlinxMinion> createContext(SnowFlinxMinion flinx, float partialTick) {
        return RenderContext.<SnowFlinxMinion>builder()
                .model(new ModelConfig<SnowFlinxMinion>()
                        .rotationOffset(90, 0, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }
}
