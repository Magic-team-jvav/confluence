package org.confluence.mod.client.model.block;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.crafting.ExtractinatorBlock;
import software.bernie.geckolib.model.GeoModel;

public class ExtractinatorBlockModel extends GeoModel<ExtractinatorBlock.Entity> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/extractinator.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/extractinator.png");
    public static final ResourceLocation ANIMATION = Confluence.asResource("animations/block/extractinator.animation.json");

    @Override
    public ResourceLocation getModelResource(ExtractinatorBlock.Entity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ExtractinatorBlock.Entity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ExtractinatorBlock.Entity animatable) {
        return ANIMATION;
    }
}
