package org.confluence.mod.client.model.item;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.sword.Phaseblade;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class PhasebladeModel extends GeoModel<Phaseblade> {
    public static final ResourceLocation barModel = Confluence.asResource("geo/item/phaseblade_bar.geo.json");
    public static final ResourceLocation model = Confluence.asResource("geo/item/phaseblade.geo.json");
    public final ResourceLocation texture;
    private static final ResourceLocation animation = Confluence.asResource("animations/item/phaseblade.animation.json");

    public PhasebladeModel(String color) {
        this.texture = Confluence.asResource("textures/item/phaseblade/" + color + "_phaseblade.png");
    }

    @Override
    public ResourceLocation getModelResource(Phaseblade animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(Phaseblade animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(Phaseblade animatable) {
        return animation;
    }
}
