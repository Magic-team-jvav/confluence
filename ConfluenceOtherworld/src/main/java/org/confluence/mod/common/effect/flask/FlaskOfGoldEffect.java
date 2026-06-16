package org.confluence.mod.common.effect.flask;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModEffects;

public class FlaskOfGoldEffect extends FlaskEffect {
    public FlaskOfGoldEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFAA00);
    }

    @Override
    public void doMeleeAttack(LivingEntity attacker, LivingEntity victim, int amplifier, DamageSource damageSource, float amount) {
        victim.addEffect(new MobEffectInstance(ModEffects.MIDAS.get(), 40));
    }
}
