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

    public static void apply(LivingEntity self, Entity attacker, DamageSource damageSource, float amount) {
        if (attacker != null && self.hasEffect(ModEffects.THORNS) && !damageSource.is(DamageTypes.THORNS)) {
            attacker.hurt(attacker.damageSources().thorns(self), amount);
        }
    }
}
