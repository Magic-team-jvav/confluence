package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.mixed.SelfGetter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ILivingEntity extends SelfGetter<LivingEntity> {
    void confluence$setBreakEasyCrashBlock(boolean breaking);

    boolean confluence$isBreakEasyCrashBlock();

    Object2IntMap<Immunity> confluence$getImmunityTicks();

    void confluence$setExtraInvulnerableTicks(int ticks);

    int confluence$getExtraInvulnerableTicks();

    static ILivingEntity of(LivingEntity living) {
        return (ILivingEntity) living;
    }

    static boolean hasEffect(Map<MobEffect, MobEffectInstance> activeEffects, MobEffect effect) {
        MobEffectInstance instance = activeEffects.get(effect);
        return instance != null && IMobEffectInstance.of(instance).confluence$isEnabled();
    }

    static @Nullable MobEffectInstance getEffect(Map<MobEffect, MobEffectInstance> activeEffects, MobEffect effect) {
        MobEffectInstance instance = activeEffects.get(effect);
        return instance == null || !IMobEffectInstance.of(instance).confluence$isEnabled() ? null : instance;
    }
}
