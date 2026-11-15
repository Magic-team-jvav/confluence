package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.VampireFrogMinion;

public class VampireFrogRenderer extends AbstractAttachmentEntityGeoRenderer<VampireFrogMinion> {

    public VampireFrogRenderer() {
        super(Confluence.asResource("entity/summon/vampire_frog"));
    }

    @Override
    protected RenderContext<VampireFrogMinion> createContext(VampireFrogMinion frog, PathNode visualNode, float partialTick, int packedLight) {
        return new RenderContext<>(frog, visualNode, partialTick, packedLight)
                .model(new ModelContext()
                        .rotationOffset(180, 0, 0)
                        .alphaDistanceFactor(1.5F));
    }
}
