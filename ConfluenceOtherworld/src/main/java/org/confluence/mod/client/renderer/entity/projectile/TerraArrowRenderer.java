package org.confluence.mod.client.renderer.entity.projectile;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;

public class TerraArrowRenderer extends ArrowRenderer<BaseArrowEntity> {
    public TerraArrowRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(BaseArrowEntity baseArrowEntity) {
        ResourceLocation path = baseArrowEntity.getTexturePath();
        if (path != null) return path;
        return ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");
    }
}
