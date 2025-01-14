package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.BouncyGrenadeEntityModel;
import org.confluence.mod.client.model.entity.bomb.StickyGrenadeEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.BouncyGrenadeEntity;
import org.confluence.mod.common.entity.projectile.bomb.StickyGrenadeEntity;

public class BouncyGrenadeEntityRenderer extends GrenadeEntityRenderer<BouncyGrenadeEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/bouncy_grenade_entity.png");
    private final BouncyGrenadeEntityModel model;

    public BouncyGrenadeEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new BouncyGrenadeEntityModel(pContext.bakeLayer(BouncyGrenadeEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(BouncyGrenadeEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<BouncyGrenadeEntity> getModel(BouncyGrenadeEntity pEntity) {
        return model;
    }
}
