package com.xiaohunao.enemybanner;

import com.mojang.logging.LogUtils;
import com.xiaohunao.enemybanner.banner.BannerManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class EnemyBannerEffect extends MobEffect {
    public static final Logger LOGGER = LogUtils.getLogger();

    protected EnemyBannerEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        return BannerManager.isInBannerRange(livingEntity);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 60 == 0;
    }

    @Override
    public void onEffectStarted(@NotNull LivingEntity livingEntity, int amplifier) {
        super.onEffectStarted(livingEntity, amplifier);
    }

    @Override
    public void onMobHurt(@NotNull LivingEntity livingEntity, int amplifier, @NotNull DamageSource damageSource, float amount) {
        super.onMobHurt(livingEntity, amplifier, damageSource, amount);
    }
}
