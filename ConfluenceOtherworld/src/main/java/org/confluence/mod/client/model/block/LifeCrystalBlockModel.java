package org.confluence.mod.client.model.block;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LifeCrystalBlock;
import software.bernie.geckolib.model.GeoModel;

public class LifeCrystalBlockModel extends GeoModel<LifeCrystalBlock.BEntity> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/life_crystal_block.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/life_crystal_block.png");

    @Override
    public ResourceLocation getModelResource(LifeCrystalBlock.BEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(LifeCrystalBlock.BEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(LifeCrystalBlock.BEntity animatable) {
        return null;
    }
}
