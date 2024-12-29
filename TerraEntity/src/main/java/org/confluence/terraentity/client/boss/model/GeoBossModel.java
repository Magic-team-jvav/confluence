package org.confluence.terraentity.client.boss.model;

import net.minecraft.resources.ResourceLocation;

import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import software.bernie.geckolib.model.GeoModel;

public class GeoBossModel<T extends AbstractTerraBossBase> extends GeoModel<T> {
    private final ResourceLocation MODEL ;
    private final ResourceLocation TEXTURES ;
    private final ResourceLocation ANIMATION ;

    public GeoBossModel(String entityName) {
        MODEL = TerraEntity.space("geo/entity/boss/" + entityName + ".geo.json");
        TEXTURES = TerraEntity.space("textures/entity/boss/" + entityName + ".png");
        ANIMATION = TerraEntity.space("animations/entity/boss/" + entityName + ".animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return TEXTURES;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return ANIMATION;
    }
}
