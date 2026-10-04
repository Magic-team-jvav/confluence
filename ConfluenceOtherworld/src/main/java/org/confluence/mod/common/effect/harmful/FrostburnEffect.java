package org.confluence.mod.common.effect.harmful;

import java.util.Set;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibUtils;


public class FrostburnEffect extends MobEffect {
    public FrostburnEffect() {
        super(MobEffectCategory.HARMFUL, 0xBBFFFF);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        living.hurt(LibDamageTypes.of(living.level(), LibDamageTypes.FROST_BURN),
                2.0F * (amplifier + 1));
        int tick = living.getTicksFrozen();
        living.setTicksFrozen(Math.min(200 * (amplifier + 1), tick + 100 * (amplifier + 1)));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        super.fillEffectCures(cures, effectInstance);
        cures.add(LibUtils.DENY_HEAL);
    }
}
