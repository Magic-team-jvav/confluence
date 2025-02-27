package org.confluence.mod.client.renderer.entity.projectile;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.BaseArrowEntity;

public class TerraArrowRenderer extends ArrowRenderer<BaseArrowEntity> {


    public TerraArrowRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(BaseArrowEntity baseArrowEntity) {
        //默认为黑色
        return Confluence.asResource(baseArrowEntity.texturePath);
    }

}
