package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.DryBombEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.DryBombEntity;

public class DryBombEntityRenderer extends BombEntityRenderer<DryBombEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/dry_bomb_entity.png");
    private final DryBombEntityModel model;

    public DryBombEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new DryBombEntityModel(pContext.bakeLayer(DryBombEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(DryBombEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<DryBombEntity> getModel(DryBombEntity pEntity) {
        return model;
    }
}
