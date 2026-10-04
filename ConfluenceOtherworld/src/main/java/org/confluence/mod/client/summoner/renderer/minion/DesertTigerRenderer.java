package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.minion.DesertTigerMinion;

public class DesertTigerRenderer extends AbstractAttachmentEntityGeoRenderer<DesertTigerMinion> {

    public DesertTigerRenderer() {
        super(tiger -> Confluence.asResource("entity/summon/desert_tiger_tier" + (tiger.getSlotCost() >= 7 ? 3 : tiger.getSlotCost() >= 4 ? 2 : 1)));
    }

    @Override
    protected RenderContext<DesertTigerMinion> createContext(DesertTigerMinion tiger, float partialTick) {
        return RenderContext.<DesertTigerMinion>builder()
                .model(new ModelConfig<DesertTigerMinion>()
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }
}
