package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.ModelContext;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.MiniDestroyerMinion;
import org.confluence.mod.common.summoner.minion.MomentumMinion;

public class MiniDestroyerRenderer<T extends MomentumMinion> extends AbstractAttachmentEntityGeoRenderer<T> {
    public MiniDestroyerRenderer() {
        super(entity -> {
            if (entity instanceof MiniDestroyerMinion destroyer) {
                String part = destroyer.isHead() ? "head" : destroyer.getOrder() == destroyer.getSameSize() - 1 ? "tail" : "body";
                return Confluence.asResource("entity/summon/mini_destroyer_" + part);
            }
            return Confluence.asResource("entity/summon/mini_probe");
        });
    }

    @Override
    protected RenderContext<T> createContext(T entity, PathNode visualNode, float partialTick, int packedLight) {
        return super.createContext(entity, visualNode, partialTick, packedLight)
                .model(new ModelContext().rotationOffset(90, 0, 0));
    }
}
