package org.confluence.mod.client.model.block;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.crafting.SkyMillBlock;
import software.bernie.geckolib.model.GeoModel;

public class SkyMillBlockModel extends GeoModel<SkyMillBlock.Entity> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/sky_mill.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/sky_mill.png");
    public static final ResourceLocation ANIMATION = Confluence.asResource("animations/block/sky_mill.animation.json");

    @Override
    public ResourceLocation getModelResource(SkyMillBlock.Entity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SkyMillBlock.Entity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(SkyMillBlock.Entity animatable) {
        return ANIMATION;
    }

}
