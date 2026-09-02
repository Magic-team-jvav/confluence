package org.confluence.mod.common.entity.animal;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;

public interface CritterVisual extends GeoEntity {
    ResourceLocation getModelPath();

    ResourceLocation getTexturePath();
}
