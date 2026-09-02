package org.confluence.mod.client.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.CritterGeoModel;
import org.confluence.mod.common.entity.animal.CritterVisual;

public class CritterRenderer<T extends Entity & CritterVisual> extends GeoNormalRenderer<T> {

    public CritterRenderer(EntityRendererProvider.Context context) {
        super(context, new CritterGeoModel<>(Confluence.asResource("geo/animal/dummy")));
        this.shadowRadius = 0.3F;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity.getTexturePath();
    }
}
