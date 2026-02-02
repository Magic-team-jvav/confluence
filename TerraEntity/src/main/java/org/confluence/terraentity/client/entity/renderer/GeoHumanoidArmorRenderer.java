package org.confluence.terraentity.client.entity.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GeoHumanoidArmorRenderer<T extends Item & GeoItem> extends GeoArmorRenderer<T> {

    public GeoHumanoidArmorRenderer(ResourceLocation path) {
        super(new DefaultedItemGeoModel<>(path));
    }


    @Nullable
    public GeoBone getHeadBone(GeoModel<T> model) {
        return model.getBone("Head").orElse(null);
    }

    @Nullable
    public GeoBone getBodyBone(GeoModel<T> model) {
        return model.getBone("Body").orElse(null);
    }

    @Nullable
    public GeoBone getRightArmBone(GeoModel<T> model) {
        return model.getBone("RightArm").orElse(null);
    }

    @Nullable
    public GeoBone getLeftArmBone(GeoModel<T> model) {
        return model.getBone("LeftArm").orElse(null);
    }

    @Nullable
    public GeoBone getRightLegBone(GeoModel<T> model) {
        return model.getBone("RightLeg").orElse(null);
    }

    @Nullable
    public GeoBone getLeftLegBone(GeoModel<T> model) {
        return model.getBone("LeftLeg").orElse(null);
    }

    @Nullable
    public GeoBone getRightBootBone(GeoModel<T> model) {
        return model.getBone("RightBoot").orElse(null);
    }

    @Nullable
    public GeoBone getLeftBootBone(GeoModel<T> model) {
        return model.getBone("LeftBoot").orElse(null);
    }

}
