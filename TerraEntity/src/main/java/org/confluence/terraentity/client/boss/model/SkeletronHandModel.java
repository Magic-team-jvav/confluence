package org.confluence.terraentity.client.boss.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.boss.SkeletronHand;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class SkeletronHandModel extends GeoModel<SkeletronHand> {
    @Override
    public ResourceLocation getModelResource(SkeletronHand animatable) {
        return TerraEntity.space("geo/entity/boss/skeletron.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SkeletronHand animatable) {
        return TerraEntity.space("textures/entity/boss/skeletron.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SkeletronHand animatable) {
        return null;
    }

    @Override
    public @Nullable Animation getAnimation(SkeletronHand animatable, String name) {
        return null;
    }
}
