package org.confluence.mod.common.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HeartLanternEffect extends MobEffect {
    public HeartLanternEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xEE3333);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level().getGameTime() % 20 == 0) {
            livingEntity.heal(1F);
        }
        return true;
    }
}
