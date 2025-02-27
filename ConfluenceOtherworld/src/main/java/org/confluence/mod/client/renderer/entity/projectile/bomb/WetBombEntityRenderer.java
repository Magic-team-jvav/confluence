package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.WetBombEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.LiquidBombEntity;

public class WetBombEntityRenderer extends BombEntityRenderer<LiquidBombEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/wet_bomb_entity.png");
    private final WetBombEntityModel model;

    public WetBombEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new WetBombEntityModel(pContext.bakeLayer(WetBombEntityModel.LAYER_LOCATION));
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
