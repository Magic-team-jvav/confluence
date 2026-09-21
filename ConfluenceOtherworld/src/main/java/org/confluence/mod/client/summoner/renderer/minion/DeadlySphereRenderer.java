package org.confluence.mod.client.summoner.renderer.minion;

import org.confluence.mod.Confluence;
import org.confluence.mod.client.summoner.AbstractAttachmentEntityGeoRenderer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.trail.ModelConfig;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.DeadlySphereMinion;

public class DeadlySphereRenderer extends AbstractAttachmentEntityGeoRenderer<DeadlySphereMinion> {

    public DeadlySphereRenderer() {
        super(sphere -> Confluence.asResource("entity/summon/deadly_sphere_" + switch (sphere.getForm()) {
            case 1 -> "flames";
            case 2 -> "blade";
            default -> "spikes";
        }));
    }

    @Override
    protected RenderContext<DeadlySphereMinion> createContext(DeadlySphereMinion sphere, float partialTick) {
        return RenderContext.<DeadlySphereMinion>builder()
                .model(new ModelConfig<DeadlySphereMinion>()
                        .rotationOffset(0, (sphere.getTickCount() + partialTick) * 4.5f, 90)
                        .alphaDistanceFactor(1.5F))
                .build();
    }
}
