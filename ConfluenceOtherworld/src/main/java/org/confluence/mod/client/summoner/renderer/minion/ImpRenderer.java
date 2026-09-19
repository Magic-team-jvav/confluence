package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.minion.ImpMinion;

public class ImpRenderer extends AbstractAttachmentEntityGeoRenderer<ImpMinion> {

    public ImpRenderer() {
        super(Confluence.asResource("entity/summon/summon_imp"));
    }

    @Override
    protected RenderContext<ImpMinion> createContext(ImpMinion imp, float partialTick) {
        return RenderContext.<ImpMinion>builder()
                .model(new ModelConfig<ImpMinion>()
                        .scale(0.8F)
                        .translateOffset(0, -1, 0)
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F))
                .build();
    }
}
