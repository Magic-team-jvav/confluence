package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.LibDamageTypes;

public class TentacleSpikesEffect extends MobEffect {
    public TentacleSpikesEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF0000);
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        LibDamageTypes.hurtWithoutKnockback(pLivingEntity, pLivingEntity.level().damageSources().magic(), pAmplifier + 2.0F);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return pDuration % 35 == 0;
    }
}