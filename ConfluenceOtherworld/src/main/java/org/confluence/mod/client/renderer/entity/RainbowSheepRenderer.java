package org.confluence.mod.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.model.entity.RainbowSheepModel;
import org.confluence.mod.common.entity.RainbowSheep;

public class RainbowSheepRenderer extends MobRenderer<RainbowSheep, RainbowSheepModel> {
    private static final ResourceLocation SHEEP_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sheep/sheep.png");

    public RainbowSheepRenderer(EntityRendererProvider.Context context) {
        super(context, new RainbowSheepModel(context.bakeLayer(RainbowSheepModel.LAYER_LOCATION)), 0.7F);
        this.addLayer(new RainbowSheepFurLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(RainbowSheep entity) {
        return SHEEP_LOCATION;
    }
}
