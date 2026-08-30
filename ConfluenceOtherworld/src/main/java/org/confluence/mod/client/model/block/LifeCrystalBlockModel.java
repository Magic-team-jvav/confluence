package org.confluence.mod.client.model.block;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class LifeCrystalBlockModel<T extends GeoBlockEntity> extends GeoModel<T> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/life_crystal_block.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/life_crystal_block.png");
    public static final ResourceLocation ENTITY_MODEL = Confluence.asResource("geo/block/life_crystal_entity.geo.json");
    public static final ResourceLocation ENTITY_TEXTURE = Confluence.asResource("textures/entity/life_crystal_entity.png");
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return null;
    }
}
