package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.LavaBombEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.LiquidBombEntity;

public class LavaBombEntityRenderer extends BombEntityRenderer<LiquidBombEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/lava_bomb_entity.png");
    private final LavaBombEntityModel model;

    public LavaBombEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new LavaBombEntityModel(pContext.bakeLayer(LavaBombEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(LiquidBombEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<LiquidBombEntity> getModel(LiquidBombEntity pEntity) {
        return model;
    }
}
