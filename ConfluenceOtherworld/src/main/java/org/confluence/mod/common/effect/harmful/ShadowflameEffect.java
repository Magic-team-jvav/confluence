package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.EffectCure;
import org.confluence.lib.util.LibUtils;

import java.util.Set;

public class ShadowflameEffect extends MobEffect {
    public ShadowflameEffect() {
        super(MobEffectCategory.HARMFUL, 0x990099);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.add(LibUtils.DENY_HEAL);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        livingEntity.hurt(livingEntity.damageSources().inFire(), 3);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
