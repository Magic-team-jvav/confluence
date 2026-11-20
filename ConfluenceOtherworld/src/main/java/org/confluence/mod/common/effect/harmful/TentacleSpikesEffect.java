package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

public class TentacleSpikesEffect extends PortMobEffect {
    public TentacleSpikesEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF0000);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        pLivingEntity.hurt(pLivingEntity.level().damageSources().magic(), pAmplifier + 2.0F);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return pDuration % 35 == 0;
    }
}
