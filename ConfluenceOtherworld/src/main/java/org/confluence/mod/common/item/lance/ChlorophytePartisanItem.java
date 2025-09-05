package org.confluence.mod.common.item.lance;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.VectorUtils;
import software.bernie.geckolib.animation.EasingType;

// todo攻击时能向前发射孢子云射弹
public class ChlorophytePartisanItem extends AbstractLanceItem {
    public ChlorophytePartisanItem() {
        super(new Properties().attributes(attributes(6, 20.5F)), ModRarity.LIME, 10, 3, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.17, 6, EasingType.EASE_OUT_BACK),
                K.of(0.33, -16, EasingType.EASE_IN_EXPO),
                K.of(0.5, 0, EasingType.LINEAR)
        ));
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, LivingEntity owner, Entity victim) {
        hurtVictim(damageSource, owner, victim);
        VectorUtils.knockBackA2B(owner, victim, 0.62, 0.2);
    }
}
