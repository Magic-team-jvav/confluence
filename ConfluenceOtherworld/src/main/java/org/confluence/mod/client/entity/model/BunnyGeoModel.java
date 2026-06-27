package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.animal.Bunny;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.HashMap;
import java.util.Map;

public class BunnyGeoModel extends DefaultedEntityGeoModel<Bunny> {
    private final Map<ResourceLocation, GeoNormalModel<Bunny>> cache = new HashMap<>();

    public BunnyGeoModel() {
        super(Bunny.Variant.NORMAL.modelPath(), true);
    }

    private GeoNormalModel<Bunny> modelFor(Bunny bunny) {
        ResourceLocation path = bunny.getVariant().modelPath();
        return cache.computeIfAbsent(path, p -> new GeoNormalModel<Bunny>(p, true).setHeadName("head"));
    }

    @Override
    public ResourceLocation getModelResource(Bunny bunny) {
        return bunny.getVariant().modelPath();
    }

    @Override
    public ResourceLocation getTextureResource(Bunny bunny) {
        return bunny.getVariant().texturePath();
    }

    @Override
    public void setCustomAnimations(Bunny bunny, long instanceId, AnimationState<Bunny> state) {
        modelFor(bunny).setCustomAnimations(bunny, instanceId, state);
    }
}
