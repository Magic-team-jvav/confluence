package org.confluence.mod.common.entity.monster.skeleton;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;

public class MeleeSkeleton extends RangeSkeleton {
    public MeleeSkeleton(EntityType<? extends AbstractSkeleton> entityType, Level level, AttributeBuilder builder) {
        super(entityType, level, builder);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 8;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {}

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypeTags.IS_DROWNING)) {
            return false;
        }
        return super.hurt(pSource, pAmount); // confluence mixin here
    }

    @Override
    public int getCurrentSwingDuration() {
        return 10;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.swing(InteractionHand.MAIN_HAND, true);
        return super.doHurtTarget(entity);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.TR_SKELETON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.TR_ZOMBIE_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk/Idle", 5, state -> {
            state.setControllerSpeed(2f);
            return state.setAndContinue(state.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
        }));
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        if (effectInstance.is(() -> MobEffects.POISON)) return false;
        return super.addEffect(effectInstance, entity);
    }
}
