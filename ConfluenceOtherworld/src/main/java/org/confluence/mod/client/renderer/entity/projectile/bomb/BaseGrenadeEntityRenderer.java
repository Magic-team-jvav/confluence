package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.BaseGrenadeEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.BaseGrenadeEntity;

public class BaseGrenadeEntityRenderer extends BombEntityRenderer<BaseGrenadeEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/grenade_entity.png");
    private final BaseGrenadeEntityModel model;

    public BaseGrenadeEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new BaseGrenadeEntityModel(pContext.bakeLayer(BaseGrenadeEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(BaseGrenadeEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<BaseGrenadeEntity> getModel(BaseGrenadeEntity pEntity) {
        return model;
    }
}
