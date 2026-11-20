package org.confluence.mod.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.model.entity.BunnyGeoModel;
import org.confluence.mod.common.entity.animal.CritterVisual;

public class BunnyRenderer<T extends Entity & CritterVisual> extends GeoNormalRenderer<T> {

    public BunnyRenderer(EntityRendererProvider.Context context) {
        super(context, new BunnyGeoModel<>());
        this.shadowRadius = 0.3F;
    }

    @Override
    public ResourceLocation getTextureLocation(T bunny) {
        return bunny.getTexturePath();
    }
}
