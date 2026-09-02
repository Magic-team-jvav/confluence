package org.confluence.mod.client.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.entity.model.BunnyGeoModel;
import org.confluence.mod.common.entity.animal.Bunny;

public class BunnyRenderer extends GeoNormalRenderer<Bunny> {

    public BunnyRenderer(EntityRendererProvider.Context context) {
        super(context, new BunnyGeoModel());
        this.shadowRadius = 0.3F;
    }

    @Override
    public ResourceLocation getTextureLocation(Bunny bunny) {
        return bunny.getBunnyVariant().texturePath();
    }
}
