package org.confluence.terraentity.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.monster.demoneye.DemonEye;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class DemonEyeModel extends GeoModel<DemonEye> {
    private static final ResourceLocation MODEL = TerraEntity.space("geo/entity/demon_eye.geo.json");
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            TerraEntity.space("textures/entity/demon_eye/normal.png"),
            TerraEntity.space("textures/entity/demon_eye/cataract.png"),
            TerraEntity.space("textures/entity/demon_eye/sleepy.png"),
            TerraEntity.space("textures/entity/demon_eye/dilated.png"),
            TerraEntity.space("textures/entity/demon_eye/green.png"),
            TerraEntity.space("textures/entity/demon_eye/purple.png"),
            TerraEntity.space("textures/entity/demon_eye/owl.png"),
            TerraEntity.space("textures/entity/demon_eye/spaceship.png")
    };
    private static final ResourceLocation ANIMATION = TerraEntity.space("animations/entity/demon_eye.animation.json");

    @Override
    public ResourceLocation getModelResource(DemonEye animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DemonEye animatable) {
        return TEXTURES[animatable.getVariant().getTextureIndex()];
    }

    @Override
    public ResourceLocation getAnimationResource(DemonEye animatable) {
        return ANIMATION;
    }
}
