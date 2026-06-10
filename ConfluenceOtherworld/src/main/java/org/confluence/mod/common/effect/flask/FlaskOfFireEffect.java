package org.confluence.mod.common.effect.flask;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FlaskOfFireEffect extends FlaskEffect {
    public FlaskOfFireEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xBBAA00);
    }

    @Override
    public void doMeleeAttack(LivingEntity attacker, LivingEntity victim, int amplifier, DamageSource damageSource, float amount) {
        victim.setRemainingFireTicks(120);
    }
}
