package org.confluence.terraentity.client.boss.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.terraentity.client.boss.model.GeoBossModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.boss.EaterOfWorldsSegment;
import software.bernie.geckolib.model.GeoModel;

public class EaterOfWorldSegmentRenderer extends GeoNormalRenderer<EaterOfWorldsSegment> {
    static GeoBossModel<EaterOfWorldsSegment> tailModel = new GeoBossModel<>("eater_of_worlds_tail");
    public EaterOfWorldSegmentRenderer(EntityRendererProvider.Context renderManager,float scale,float yOffset) {
        super(renderManager, new GeoBossModel<>("eater_of_worlds_segment"),true, scale,yOffset);
    }

    public GeoModel<EaterOfWorldsSegment> getGeoModel() {
        if(animatable!=null && animatable.ifTail)
            return tailModel;
        return this.model;
    }
}