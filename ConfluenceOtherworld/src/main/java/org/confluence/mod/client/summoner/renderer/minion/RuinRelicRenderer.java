package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.minion.RuinRelicMinion;

public class RuinRelicRenderer extends AbstractAttachmentEntityGeoRenderer<RuinRelicMinion> {

    public RuinRelicRenderer() {
        super(Confluence.asResource("entity/summon/ruin_relic"));
    }

    @Override
    protected RenderContext<RuinRelicMinion> createContext(RuinRelicMinion relic, float partialTick) {
        return RenderContext.<RuinRelicMinion>builder()
                .model(new ModelConfig<RuinRelicMinion>()
                        .translateOffset(0, -0.35f, 0.015f)
                        .rotationOffset(90, 0, 0)
                        .scale((float) (0.95f + Math.sin((relic.getTickCount() + partialTick) * 0.1) * 0.1f))
                        .alphaDistanceFactor(1.5F))
                .build();
    }
}
