package org.confluence.mod.client.summon;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.summon.SummonAnimation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

/// 普通召唤物的客户端动画状态。
final class ClientSummonVisual implements GeoAnimatable {
    private final ClientSummonModels.Animations animations;
    private final UUID id;
    private final ResourceLocation type;
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private SummonAnimation animation = SummonAnimation.NONE;
    private boolean moving;
    private double age;

    ClientSummonVisual(UUID id, ResourceLocation type, ClientSummonModels.Animations animations) {
        this.id = id;
        this.type = type;
        this.animations = animations;
    }

    UUID id() {
        return id;
    }

    ResourceLocation type() {
        return type;
    }

    void update(SummonAnimation animation, boolean moving, double age) {
        this.animation = animation;
        this.moving = moving;
        this.age = age;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "summon_visual", animations.transitionTicks(),
                state -> state.setAndContinue(animations.select(animation, moving))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public double getTick(Object object) {
        return age;
    }
}
