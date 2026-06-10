package org.confluence.mod.common.item.spear;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibMathUtils;
import software.bernie.geckolib.animation.EasingType;

public class OrichalcumHalberdItem extends AbstractSpearItem {
    public OrichalcumHalberdItem() {
        super(new Properties().attributes(attributes(6, 23F)), ModRarity.LIGHT_RED, 10, 3, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.17, 6, EasingType.EASE_OUT_BACK),
                K.of(0.33, -16, EasingType.EASE_IN_EXPO),
                K.of(0.5, 0, EasingType.LINEAR)
        ));
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim) {
        hurtVictim(damageSource, owner, victim);
        LibMathUtils.knockBackA2B(owner, victim, 0.25, 0.1);
    }
}
