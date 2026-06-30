package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.animal.BaseCritter;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CritterGeoModel<T extends BaseCritter> extends DefaultedEntityGeoModel<T> {
    private final ResourceLocation defaultModel;

    public CritterGeoModel(ResourceLocation defaultModel) {
        super(defaultModel, true);
        this.defaultModel = defaultModel;
    }

    @Override
    public ResourceLocation getModelResource(T entity) {
        ResourceLocation path = entity.getModelPath();
        return path.equals(defaultModel) ? path : path.withSuffix(".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T entity) {
        return entity.getTexturePath();
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> state) {
        super.setCustomAnimations(animatable, instanceId, state);
        EntityModelData data = state.getData(DataTickets.ENTITY_MODEL_DATA);
        if (data != null && getBone("head").isPresent()) {
            getBone("head").get().setRotX(data.headPitch() * ((float) Math.PI / 180F));
            getBone("head").get().setRotY(data.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
