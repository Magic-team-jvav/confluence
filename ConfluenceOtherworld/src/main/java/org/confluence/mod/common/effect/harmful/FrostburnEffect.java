package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibUtils;
import org.mesdag.portlib.wrapper.common.PortEffectCure;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

import java.util.Set;

public class FrostburnEffect extends PortMobEffect {
    public FrostburnEffect() {
        super(MobEffectCategory.HARMFUL, 0xBBFFFF);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        living.hurt(LibDamageTypes.of(living.level(), LibDamageTypes.FROST_BURN),
                2.0F * (amplifier + 1));
        int tick = living.getTicksFrozen();
        living.setTicksFrozen(Math.min(200 * (amplifier + 1), tick + 100 * (amplifier + 1)));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void fillEffectCures(Set<PortEffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.add(LibUtils.DENY_HEAL);
    }
}
