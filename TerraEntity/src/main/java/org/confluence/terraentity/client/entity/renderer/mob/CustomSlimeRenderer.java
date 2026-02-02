package org.confluence.terraentity.client.entity.renderer.mob;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;
import org.confluence.terraentity.TerraEntity;
import org.jetbrains.annotations.NotNull;

public class CustomSlimeRenderer extends SlimeRenderer {
    private final ResourceLocation texture;

    public CustomSlimeRenderer(EntityRendererProvider.Context context, String path) {
        super(context);
        this.texture = TerraEntity.space("textures/entity/slime/slime_" + path + ".png");
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Slime slime) {
        return texture;
    }
}
