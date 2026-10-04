package org.confluence.mod.client.model.block;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.GardenGnomeBlock;
import software.bernie.geckolib.model.GeoModel;

public class GardenGnomeBlockModel extends GeoModel<GardenGnomeBlock.BEntity> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/gnome.geo.json");
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/gnome.png");
    private static final ResourceLocation ANIMATION = Confluence.asResource("animations/entity/gnome.animation.json");

    @Override
    public ResourceLocation getModelResource(GardenGnomeBlock.BEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GardenGnomeBlock.BEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GardenGnomeBlock.BEntity animatable) {
        return ANIMATION;
    }
}
