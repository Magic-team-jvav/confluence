package org.confluence.mod.common.entity.yoyo;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class CthulhuEyeProjectile extends BaseYoyoProjectile implements GeoEntity {
    private static final RawAnimation DASH = RawAnimation.begin().thenLoop("type_2_run");
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 0, state -> state.setAndContinue(DASH)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return animationCache;}

    public CthulhuEyeProjectile(EntityType<? extends CthulhuEyeProjectile> type, Level level) {super(type, level);}

    @Override
    protected ParticleOptions particle() {return ParticleTypes.GLOW;}

    @Override
    protected float damageMultiplier() {return 2;}

    @Override
    protected double speed() {return 1.2;}

    @Override
    protected double targetRange() {return 5;}

    @Override
    protected boolean fallsBackToExcludedTarget() {return true;}

    @Override
    protected boolean pierces() {return true;}

    @Override
    public float getCriticalChance() {return 0;}
}
