package org.confluence.mod.client.model.block;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class VoidVaultModel<T extends GeoAnimatable> extends GeoModel<T> {
    public static final ResourceLocation MODEL = Confluence.asResource("geo/block/void_vault.geo.json");
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/block/void_vault.png");
    public static final ResourceLocation ANIMATION = Confluence.asResource("animations/block/void_vault.animation.json");

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
        return ANIMATION;
    }
}
