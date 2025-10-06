package org.confluence.mod.common.effect.beneficial;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModEffects;

public class ThornsEffect extends MobEffect {   //荆棘 给予伤害来源反伤
    public ThornsEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FF00);
    }

    @Override
    public void onMobHurt(LivingEntity self, int amplifier, DamageSource damageSource, float amount) {
        Entity attacker = damageSource.getEntity();
        if (attacker != null && self != attacker && self.hasEffect(ModEffects.THORNS) && !damageSource.is(DamageTypes.THORNS)) {
            attacker.hurt(attacker.damageSources().thorns(self), amount);
        }
    }
}
