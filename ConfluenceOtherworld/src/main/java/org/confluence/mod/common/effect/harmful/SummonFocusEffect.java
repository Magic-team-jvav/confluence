package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

public class SummonFocusEffect extends PortMobEffect {
    public SummonFocusEffect() {
        super(MobEffectCategory.HARMFUL, 0xABAB11);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {}

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
