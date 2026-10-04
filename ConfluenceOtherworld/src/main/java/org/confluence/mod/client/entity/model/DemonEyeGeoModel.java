package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.monster.DemonEye;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DemonEyeGeoModel extends DefaultedEntityGeoModel<DemonEye> {

    public DemonEyeGeoModel() {
        super(DemonEye.Variant.NORMAL.modelPath(), false);
    }

    @Override
    public ResourceLocation getModelResource(DemonEye eye) {
        return eye.getVariant().modelPath();
    }

    @Override
    public ResourceLocation getTextureResource(DemonEye eye) {
        return eye.getVariant().texturePath();
    }

    @Override
    public ResourceLocation getAnimationResource(DemonEye eye) {
        return eye.getVariant().animationPath();
    }
}
