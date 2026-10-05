package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public final class ContactHumanoidGeoModel<T extends Mob & GeoEntity> extends GeoNormalModel<T> {
    private final String rightArmName;
    private final String leftArmName;

    public ContactHumanoidGeoModel(ResourceLocation path, String rightArmName, String leftArmName) {
        super(path);
        this.rightArmName = rightArmName;
        this.leftArmName = leftArmName;
    }

    @Override
    public void setCustomAnimations(T entity, long instanceId, AnimationState<T> state) {
        super.setCustomAnimations(entity, instanceId, state);
        float attack = entity.getAttackAnim(state.getPartialTick());
        if (attack <= 0.0F) return;

        float strike = Mth.sin(Mth.sqrt(attack) * Mth.PI);
        GeoBone rightArm = getAnimationProcessor().getBone(rightArmName);
        if (rightArm != null) {
            rightArm.setRotX(rightArm.getRotX() - strike * 1.15F);
            rightArm.setRotY(rightArm.getRotY() + strike * 0.28F);
        }
        GeoBone leftArm = getAnimationProcessor().getBone(leftArmName);
        if (leftArm != null) {
            leftArm.setRotX(leftArm.getRotX() - strike * 0.55F);
            leftArm.setRotY(leftArm.getRotY() - strike * 0.18F);
        }
    }
}
