package org.confluence.mod.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.TargetDummyEntity;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class TargetDummyModel extends GeoModel<TargetDummyEntity> {
    private static final ResourceLocation MODEL = Confluence.asResource("geo/entity/target_dummy.geo.json");
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/entity/target_dummy.png");
    private static final ResourceLocation ANIMATION = Confluence.asResource("animations/entity/target_dummy.animation.json");

    @Override
    public ResourceLocation getModelResource(TargetDummyEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TargetDummyEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TargetDummyEntity animatable) {
        return ANIMATION;
    }
}
