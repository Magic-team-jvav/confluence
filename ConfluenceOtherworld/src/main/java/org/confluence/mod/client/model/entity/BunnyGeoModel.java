package org.confluence.mod.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.animal.Bunny;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BunnyGeoModel extends DefaultedEntityGeoModel<Bunny> {
    public BunnyGeoModel() {
        super(Bunny.Variant.NORMAL.modelPath(), true);
    }

    @Override
    public ResourceLocation getModelResource(Bunny bunny) {
        return CritterGeoModel.resolveModelResource(bunny.getBunnyVariant().modelPath());
    }

    @Override
    public ResourceLocation getTextureResource(Bunny bunny) {
        return bunny.getBunnyVariant().texturePath();
    }

    @Override
    public ResourceLocation getAnimationResource(Bunny bunny) {
        return CritterGeoModel.resolveAnimationResource(bunny.getBunnyVariant().modelPath());
    }

    @Override
    public void setCustomAnimations(Bunny bunny, long instanceId, AnimationState<Bunny> state) {
        EntityModelData data = state.getData(DataTickets.ENTITY_MODEL_DATA);
        if (data == null) return;
        getBone("head").ifPresent(head -> {
            var initial = head.getInitialSnapshot();
            head.setRotX(initial.getRotX() + data.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(initial.getRotY() + data.netHeadYaw() * ((float) Math.PI / 180F));
        });
    }
}
