package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class CrimsonStormEffect extends MobEffect {
    public CrimsonStormEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        // 行为由对应的 HillOfFlesh 实例结算，这里不能保存跨实体的 Boss 状态。
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }
}
