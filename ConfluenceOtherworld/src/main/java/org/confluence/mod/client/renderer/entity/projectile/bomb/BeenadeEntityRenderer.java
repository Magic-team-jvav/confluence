package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.BeenadeEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.BeenadeEntity;

public class BeenadeEntityRenderer extends BombEntityRenderer<BeenadeEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/beenade_entity.png");
    private final BeenadeEntityModel model;

    public BeenadeEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new BeenadeEntityModel(pContext.bakeLayer(BeenadeEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(BeenadeEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<BeenadeEntity> getModel(BeenadeEntity pEntity) {
        return model;
    }
}
