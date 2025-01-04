package org.confluence.terraentity.client.boss.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import org.confluence.terraentity.client.boss.model.GeoBossModel;
import org.confluence.terraentity.entity.boss.EaterOfWorldSegment;
import software.bernie.geckolib.model.GeoModel;

public class EaterOfWorldSegmentRenderer extends GeoBossRenderer<EaterOfWorldSegment, GeoBossModel<EaterOfWorldSegment>> {
    static GeoBossModel<EaterOfWorldSegment> tailModel = new GeoBossModel<>("eater_of_world_tail");
    public EaterOfWorldSegmentRenderer(EntityRendererProvider.Context renderManager,float scale) {
        super(renderManager, new GeoBossModel<>("eater_of_world_segment"), scale);
    }

    public GeoModel<EaterOfWorldSegment> getGeoModel() {
        if(animatable.ifTail)
            return tailModel;
        return this.model;
    }
}