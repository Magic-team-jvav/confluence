package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.StickyGrenadeEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.StickyGrenadeEntity;

public class StickyGrenadeEntityRenderer extends BombEntityRenderer<StickyGrenadeEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/sticky_grenade_entity.png");
    private final StickyGrenadeEntityModel model;

    public StickyGrenadeEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new StickyGrenadeEntityModel(pContext.bakeLayer(StickyGrenadeEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(StickyGrenadeEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<StickyGrenadeEntity> getModel(StickyGrenadeEntity pEntity) {
        return model;
    }
}
