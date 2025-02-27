package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.DirtBombEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.BaseDirtBombEntity;

public class DirtBombEntityRenderer extends BombEntityRenderer<BaseDirtBombEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/dirt_bomb_entity.png");
    private final DirtBombEntityModel model;

    public DirtBombEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new DirtBombEntityModel(pContext.bakeLayer(DirtBombEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(BaseDirtBombEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<BaseDirtBombEntity> getModel(BaseDirtBombEntity pEntity) {
        return model;
    }
}
