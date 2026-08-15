package org.confluence.mod.client.summon;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

/**
 * 星尘龙头部或体节的纯客户端视觉状态。
 */
final class ClientStardustDragonVisual implements GeoAnimatable {
    private final UUID id;
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private Part part;
    private double age;

    ClientStardustDragonVisual(UUID id, Part part) {
        this.id = id;
        this.part = part;
    }

    void update(Part part, double age) {
        this.part = part;
        this.age = age;
    }

    UUID id() {
        return id;
    }

    Part part() {
        return part;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public double getTick(Object object) {
        return age;
    }

    enum Part {
        HEAD, BODY, TAIL
    }
}
