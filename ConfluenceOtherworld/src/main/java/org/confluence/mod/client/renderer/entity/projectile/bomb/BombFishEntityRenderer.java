package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.BombFishEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.BombFishEntity;

public class BombFishEntityRenderer extends BombEntityRenderer<BombFishEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/bomb_fish_entity.png");
    private final BombFishEntityModel model;

    public BombFishEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new BombFishEntityModel(pContext.bakeLayer(BombFishEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(BombFishEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<BombFishEntity> getModel(BombFishEntity pEntity) {
        return model;
    }
}
