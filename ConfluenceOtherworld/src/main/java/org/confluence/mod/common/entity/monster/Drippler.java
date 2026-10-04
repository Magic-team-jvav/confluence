package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.entity.ai.bt.leaf.DemonEyeLeaveAction;
import org.confluence.mod.common.entity.ai.bt.leaf.DirectFloatingPursuitAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 缓慢贴近地面悬浮追击玩家的滴滴怪。
 */
public final class Drippler extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");

    public Drippler(EntityType<? extends Drippler> type, Level level) {
        super(type, level);
        setDiscardFriction(true);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new ConditionalSwitchNode(
                        () -> level().isDay(),
                        new DemonEyeLeaveAction(Drippler.this),
                        new DirectFloatingPursuitAction(Drippler.this, 2.0)
                );
            }
        };
    }

    @Override
    protected boolean canTargetPlayer(LivingEntity target) {
        return level().isNight();
    }

    @Override
    public void tick() {
        if (!level().isClientSide && level().isDay()) setTarget(null);
        super.tick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(FLY)));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.DRIPPLER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.DRIPPLER_DEATH.get();
    }
}
