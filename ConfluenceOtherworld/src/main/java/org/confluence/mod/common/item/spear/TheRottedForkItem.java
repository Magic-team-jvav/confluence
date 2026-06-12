package org.confluence.mod.common.item.spear;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibMathUtils;
import software.bernie.geckolib.animation.EasingType;

public class TheRottedForkItem extends AbstractSpearItem {
    public TheRottedForkItem() {
        super(new Properties().attributes(attributes(6, 8.5F)), ModRarity.BLUE, 15, 5, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.25, 6, EasingType.EASE_OUT_BACK),
                K.of(0.5, -16, EasingType.EASE_IN_EXPO),
                K.of(0.75, 0, EasingType.LINEAR)
        ));
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim) {
        hurtVictim(damageSource, owner, victim);
        LibEntityUtils.knockBackA2B(owner, victim, 0.25, 0.1);
    }
}
