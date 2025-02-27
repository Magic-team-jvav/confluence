package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.BaseDynamiteEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.BaseDynamiteEntity;

public class BaseDynamiteEntityRenderer extends BombEntityRenderer<BaseDynamiteEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/dynamite_entity.png");
    private final BaseDynamiteEntityModel model;

    public BaseDynamiteEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new BaseDynamiteEntityModel(pContext.bakeLayer(BaseDynamiteEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(BaseDynamiteEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<BaseDynamiteEntity> getModel(BaseDynamiteEntity pEntity) {
        return model;
    }
}
