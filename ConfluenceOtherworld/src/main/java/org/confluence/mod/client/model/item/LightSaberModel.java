package org.confluence.mod.client.model.item;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.sword.LightSaber;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class LightSaberModel extends GeoModel<LightSaber> {
    public static final ResourceLocation barModel = Confluence.asResource("geo/item/light_saber_bar.geo.json");
    public static final ResourceLocation model = Confluence.asResource("geo/item/light_saber.geo.json");
    public final ResourceLocation texture;
    private static final ResourceLocation animation = Confluence.asResource("animations/item/light_saber.animation.json");

    public LightSaberModel(String color) {
        this.texture = Confluence.asResource("textures/item/light_saber/" + color + "_light_saber.png");
    }

    @Override
    public ResourceLocation getModelResource(LightSaber animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(LightSaber animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LightSaber animatable) {
        return animation;
    }
}
