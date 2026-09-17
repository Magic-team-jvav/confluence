package org.confluence.mod.client.entity.model;

import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.AngryTumbler;
import software.bernie.geckolib.core.animation.AnimationState;

public final class AngryTumblerModel extends GeoNormalModel<AngryTumbler> {
    public AngryTumblerModel() {
        super(Confluence.asResource("angry_tumbler"), false);
    }

    @Override
    public void setCustomAnimations(AngryTumbler entity, long instanceId, AnimationState<AngryTumbler> state) {
        super.setCustomAnimations(entity, instanceId, state);
        getBone("all").ifPresent(bone -> {
            bone.setPivotY(8.0F);
            bone.setRotX(state.getLimbSwing() * 2.0F);
        });
    }
}
