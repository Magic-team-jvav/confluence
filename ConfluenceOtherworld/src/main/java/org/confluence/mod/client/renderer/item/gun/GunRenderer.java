package org.confluence.mod.client.renderer.item.gun;

import net.minecraft.world.item.Item;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GunRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
    public GunRenderer(GeoModel<T> model) {
        super(model);
//        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
