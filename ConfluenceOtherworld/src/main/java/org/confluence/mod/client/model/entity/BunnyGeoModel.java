package org.confluence.mod.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.entity.animal.Bunny;
import org.confluence.mod.common.entity.animal.CritterVisual;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BunnyGeoModel<T extends Entity & CritterVisual> extends DefaultedEntityGeoModel<T> {
    public BunnyGeoModel() {
        super(Bunny.Variant.NORMAL.modelPath(), true);
    }

    @Override
    public ResourceLocation getModelResource(T bunny) {
        return CritterGeoModel.resolveModelResource(bunny.getModelPath());
    }

    @Override
    public ResourceLocation getTextureResource(T bunny) {
        return bunny.getTexturePath();
    }

    @Override
    public ResourceLocation getAnimationResource(T bunny) {
        return CritterGeoModel.resolveAnimationResource(bunny.getModelPath());
    }

    @Override
    public void setCustomAnimations(T bunny, long instanceId, AnimationState<T> state) {
        EntityModelData data = state.getData(DataTickets.ENTITY_MODEL_DATA);
        if (data == null) return;
        getBone("head").ifPresent(head -> {
            var initial = head.getInitialSnapshot();
            head.setRotX(initial.getRotX() + data.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(initial.getRotY() + data.netHeadYaw() * ((float) Math.PI / 180F));
        });
    }
}
