package org.confluence.mod.common.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HeartLanternEffect extends MobEffect {
    public HeartLanternEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xEE3333);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        int interval = 20 >> amplifier;
        return interval <= 1 || tickCount % interval == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
            livingEntity.heal(0.2F);
        }
        return true;
    }
}
