package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.BouncyBombEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.BouncyBombEntity;

public class BouncyBombEntityRenderer extends BombEntityRenderer<BouncyBombEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/bouncy_bomb_entity.png");
    private final BouncyBombEntityModel model;

    public BouncyBombEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new BouncyBombEntityModel(pContext.bakeLayer(BouncyBombEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(BouncyBombEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<BouncyBombEntity> getModel(BouncyBombEntity pEntity) {
        return model;
    }
}
