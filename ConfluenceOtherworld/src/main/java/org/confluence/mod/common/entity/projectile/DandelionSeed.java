package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class DandelionSeed extends StraightMonsterProjectile implements GeoEntity {
    public static final double GRAVITY = 0.004;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DandelionSeed(EntityType<? extends DandelionSeed> type, Level level) {
        super(type, level);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return velocity.add(0.0, -GRAVITY, 0.0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
