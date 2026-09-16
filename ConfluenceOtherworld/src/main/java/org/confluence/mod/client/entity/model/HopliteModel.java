package org.confluence.mod.client.entity.model;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.Hoplite;
import software.bernie.geckolib.core.animation.AnimationState;

public final class HopliteModel extends VanillaHumanoidGeoModel<Hoplite> {
    public HopliteModel(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("geo/entity/hoplite.geo.json"), Confluence.asResource("textures/entity/hoplite.png"), true);
    }

    @Override
    public void setCustomAnimations(Hoplite entity, long instanceId, AnimationState<Hoplite> state) {
        super.setCustomAnimations(entity, instanceId, state);
        if (entity.isThrowing()) getBone("RightArm").ifPresent(arm -> {
            arm.setRotX(-Mth.PI);
            arm.setRotY(0.0F);
            arm.setRotZ(-0.15F);
        });
    }
}
