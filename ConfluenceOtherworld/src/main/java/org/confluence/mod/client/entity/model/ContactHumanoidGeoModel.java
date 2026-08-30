package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

/// 为只有移动资源、没有 attack.strike 的接触型人形敌怪补充短促挥击。
///
/// 原资源动画仍负责身体、腿和附加部件；这里仅在实体已有 swing 进度时叠加双臂旋转，
/// 因而不会改变行为、命中时机或空闲姿势。
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
        CoreGeoBone rightArm = getAnimationProcessor().getBone(rightArmName);
        if (rightArm != null) {
            CoreGeoBone arm = rightArm;
            arm.setRotX(arm.getRotX() - strike * 1.15F);
            arm.setRotY(arm.getRotY() + strike * 0.28F);
        }
        CoreGeoBone leftArm = getAnimationProcessor().getBone(leftArmName);
        if (leftArm != null) {
            CoreGeoBone arm = leftArm;
            arm.setRotX(arm.getRotX() - strike * 0.55F);
            arm.setRotY(arm.getRotY() - strike * 0.18F);
        }
    }
}
