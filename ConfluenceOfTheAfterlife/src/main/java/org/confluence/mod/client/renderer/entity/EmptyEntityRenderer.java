package org.confluence.mod.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EmptyEntityRenderer<E extends Entity> extends EntityRenderer<E> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");

    public EmptyEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull E entity) {
        return TEXTURE;
    }
}
