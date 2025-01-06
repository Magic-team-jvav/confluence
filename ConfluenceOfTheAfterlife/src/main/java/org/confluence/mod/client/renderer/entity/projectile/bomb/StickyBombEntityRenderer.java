package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.StickyBombEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.StickyBombEntity;

public class StickyBombEntityRenderer extends BombEntityRenderer<StickyBombEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/sticky_bomb_entity.png");
    private final StickyBombEntityModel model;

    public StickyBombEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new StickyBombEntityModel(pContext.bakeLayer(StickyBombEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(StickyBombEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<StickyBombEntity> getModel(StickyBombEntity pEntity) {
        return model;
    }
}
