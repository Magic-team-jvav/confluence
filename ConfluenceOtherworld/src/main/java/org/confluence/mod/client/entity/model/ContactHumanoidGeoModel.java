package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

/// 为只有移动资源、没有 attack.strike 的接触型人形敌怪补充短促挥击（1.20 同名文件）。
///
/// 原资源动画仍负责身体、腿和附加部件；这里仅在实体已有 swing 进度时叠加双臂旋转，
/// 因而不会改变行为、命中时机或空闲姿势。
///
/// 1.20 → 1.21.1 与同族模型相同的 geckolib 包名迁移：
/// `core.animatable.model.CoreGeoBone` → `cache.object.GeoBone`、`core.animation.AnimationState` → `animation.AnimationState`。
/// 渲染器侧还用到 geckolib 自带的 `withAltAnimations`（见 `DefaultedEntityGeoModel`，1.21 jar 已核）与
/// 本仓库 `GeoNormalRenderer#withCutout`（1.21 `:77`）。
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
