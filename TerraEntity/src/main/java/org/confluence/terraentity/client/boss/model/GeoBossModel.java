package org.confluence.terraentity.client.boss.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.EntityType;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Supplier;

public class GeoBossModel<T extends AbstractTerraBossBase> extends GeoModel<T> {
    private final ResourceLocation MODEL ;
    private final ResourceLocation TEXTURES ;
    private final ResourceLocation ANIMATION ;

    public GeoBossModel(Supplier<EntityType<T>> entityType) {
        this(BuiltInRegistries.ENTITY_TYPE.getKey(entityType.get()).getPath());
    }
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
