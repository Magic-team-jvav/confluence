package org.confluence.mod.client.renderer.entity.projectile.bomb;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.bomb.BaseDynamiteEntityModel;
import org.confluence.mod.client.model.entity.bomb.BouncyDynamiteEntityModel;
import org.confluence.mod.common.entity.projectile.bomb.BaseDynamiteEntity;
import org.confluence.mod.common.entity.projectile.bomb.BouncyBombEntity;
import org.confluence.mod.common.entity.projectile.bomb.BouncyDynamiteEntity;

public class BouncyDynamiteEntityRenderer extends DynamiteEntityRenderer<BaseDynamiteEntity> {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/bomb/bouncy_dynamite_entity.png");
    private final BaseDynamiteEntityModel model;

    public BouncyDynamiteEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new BaseDynamiteEntityModel(pContext.bakeLayer(BaseDynamiteEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(BaseDynamiteEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public EntityModel<BaseDynamiteEntity> getModel(BaseDynamiteEntity pEntity) {
        return model;
    }
}
