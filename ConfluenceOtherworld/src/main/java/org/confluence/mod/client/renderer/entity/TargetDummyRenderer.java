package org.confluence.mod.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.model.entity.TargetDummyModel;
import org.confluence.mod.common.entity.TargetDummyEntity;

public class TargetDummyRenderer extends LivingEntityRenderer<TargetDummyEntity, TargetDummyModel<TargetDummyEntity>> {
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/target_dummy.png");

    public TargetDummyRenderer(EntityRendererProvider.Context context) {
        super(context, new TargetDummyModel<>(context.bakeLayer(TargetDummyModel.LAYER_LOCATION)), 0);
    }

    @Override
    public ResourceLocation getTextureLocation(TargetDummyEntity entity) {
        return TEXTURE;
    }
}
