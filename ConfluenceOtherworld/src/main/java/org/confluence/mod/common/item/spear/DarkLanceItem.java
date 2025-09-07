package org.confluence.mod.common.item.spear;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEffects;
import software.bernie.geckolib.animation.EasingType;

public class DarkLanceItem extends AbstractSpearItem {
    public DarkLanceItem() {
        super(new Properties().attributes(attributes(6, 8.3F)), ModRarity.ORANGE, 10, 3, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.17, 6, EasingType.EASE_OUT_BACK),
                K.of(0.33, -16, EasingType.EASE_IN_EXPO),
                K.of(0.5, 0, EasingType.LINEAR)
        ));
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim) {
        hurtVictim(damageSource, owner, victim);
        if (victim instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.SHADOWFLAME, 300));
        }
        VectorUtils.knockBackA2B(owner, victim, 0.5, 0.1);
    }
}
