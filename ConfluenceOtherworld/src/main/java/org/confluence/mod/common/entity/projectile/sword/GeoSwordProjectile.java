package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.component.SwordProjectileAppearance;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 由剑气组件提供模型、纹理和动画的通用 Geo 弹幕。
public final class GeoSwordProjectile extends ForwardSwordProjectile implements GeoEntity {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public GeoSwordProjectile(EntityType<? extends GeoSwordProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0, state -> {
            if (getProjectileComponent() == null
                    || !(getProjectileComponent().appearance() instanceof SwordProjectileAppearance.Geo appearance))
                return PlayState.STOP;
            return appearance.animationClip().map(clip -> state.setAndContinue(RawAnimation.begin().thenLoop(clip))).orElse(PlayState.STOP);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
