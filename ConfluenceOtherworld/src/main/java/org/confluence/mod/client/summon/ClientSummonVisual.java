package org.confluence.mod.client.summon;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.summon.SummonAnimation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

/// 普通召唤物的客户端动画状态。
final class ClientSummonVisual implements GeoAnimatable {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
    private static final RawAnimation CAST = RawAnimation.begin().thenLoop("attack.cast");
    private final UUID id;
    private final ResourceLocation type;
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private SummonAnimation animation = SummonAnimation.NONE;
    private boolean moving;
    private double age;

    ClientSummonVisual(UUID id, ResourceLocation type) {
        this.id = id;
        this.type = type;
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
        controllers.add(new AnimationController<>(this, "summon_visual", transitionTicks(), state -> state.setAndContinue(selectedAnimation())));
    }

    private int transitionTicks() {
        return type.getPath().equals("slime_baby") ? 0 : 4;
    }

    private RawAnimation selectedAnimation() {
        String path = type.getPath();
        if (path.equals("finch_baby")) return FLY;
        if (path.equals("slime_baby"))
            return animation == SummonAnimation.FLY ? FLY : moving ? WALK : IDLE;
        if (animation == SummonAnimation.MELEE_ATTACK && (path.equals("hornet_baby") || path.equals("sculk_wisp") || path.equals("summon_imp")))
            return CAST;
        if (path.equals("hornet_baby")) return IDLE;
        return moving ? WALK : IDLE;
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
