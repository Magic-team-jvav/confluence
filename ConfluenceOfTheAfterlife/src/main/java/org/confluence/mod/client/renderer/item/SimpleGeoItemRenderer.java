package org.confluence.mod.client.renderer.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SimpleGeoItemRenderer<T extends Item & GeoAnimatable> implements IClientItemExtensions {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final @Nullable ResourceLocation animation;
    private GeoItemRenderer<T> renderer;

    public SimpleGeoItemRenderer(ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }

    @SuppressWarnings("removal")
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (renderer == null) {
            this.renderer = new GeoItemRenderer<>(new GeoModel<>() {
                @Override
                public ResourceLocation getModelResource(T animatable) {
                    return model;
                }

                @Override
                public ResourceLocation getTextureResource(T animatable) {
                    return texture;
                }

                @Override
                public ResourceLocation getAnimationResource(T animatable) {
                    return animation;
                }
            });
        }
        return renderer;
    }
}
