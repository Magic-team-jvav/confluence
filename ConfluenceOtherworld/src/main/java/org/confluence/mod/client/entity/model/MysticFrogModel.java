package org.confluence.mod.client.entity.model;

import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.animal.MysticFrog;
import software.bernie.geckolib.core.animation.AnimationState;

public final class MysticFrogModel extends GeoNormalModel<MysticFrog> {
    private final FrogModel<MysticFrog> vanillaModel;

    public MysticFrogModel(EntityRendererProvider.Context context) {
        super(Confluence.asResource("animal/mystic_frog"), false);
        vanillaModel = new FrogModel<>(context.bakeLayer(ModelLayers.FROG));
    }

    @Override
    public ResourceLocation getAnimationResource(MysticFrog frog) {
        return null;
    }

    @Override
    public void setCustomAnimations(MysticFrog frog, long instanceId, AnimationState<MysticFrog> state) {
        vanillaModel.setupAnim(frog, state.getLimbSwing(), state.getLimbSwingAmount(), frog.tickCount + state.getPartialTick(), 0, 0);
        for (var bone : getAnimationProcessor().getRegisteredBones()) {
            vanillaModel.getAnyDescendantWithName(bone.getName()).ifPresent(part -> {
                var initial = part.getInitialPose();
                bone.setRotX(part.xRot - initial.xRot);
                bone.setRotY(part.yRot - initial.yRot);
                bone.setRotZ(part.zRot - initial.zRot);
                bone.setPosX(-(part.x - initial.x));
                bone.setPosY(-(part.y - initial.y));
                bone.setPosZ(part.z - initial.z);
                bone.setScaleX(part.xScale);
                bone.setScaleY(part.yScale);
                bone.setScaleZ(part.zScale);
                bone.setHidden(!part.visible);
            });
        }
    }
}
