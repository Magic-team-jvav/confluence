package org.confluence.mod.client.model.block;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import software.bernie.geckolib.model.GeoModel;

public class AltarBlockModel extends GeoModel<AltarBlock.Entity> {
    public static final ResourceLocation[] MODELS = new ResourceLocation[]{
            Confluence.asResource("geo/block/demon_altar.geo.json"),
            Confluence.asResource("geo/block/crimson_altar.geo.json")
    };
    public static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            Confluence.asResource("textures/block/demon_altar.png"),
            Confluence.asResource("textures/block/crimson_altar.png")
    };
    public static final ResourceLocation[] ANIMATIONS = new ResourceLocation[]{
            Confluence.asResource("animations/block/demon_altar.animation.json"),
            Confluence.asResource("animations/block/crimson_altar.animation.json")
    };

    @Override
    public ResourceLocation getModelResource(AltarBlock.Entity animatable) {
        return MODELS[animatable.getVariant().getId()];
    }

    @Override
    public ResourceLocation getTextureResource(AltarBlock.Entity animatable) {
        return TEXTURES[animatable.getVariant().getId()];
    }

    @Override
    public ResourceLocation getAnimationResource(AltarBlock.Entity animatable) {
        return ANIMATIONS[animatable.getVariant().getId()];
    }
}
