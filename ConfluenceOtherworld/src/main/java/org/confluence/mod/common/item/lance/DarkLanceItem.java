package org.confluence.mod.common.item.lance;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEffects;
import software.bernie.geckolib.animation.EasingType;

public class DarkLanceItem extends AbstractLanceItem {
    public DarkLanceItem() {
        super(new Properties().attributes(entityInteractionRange(4)), ModRarity.ORANGE,9, 3, createKeyframes(
                K.of(0, 0, EasingType.LINEAR),
                K.of(0.08, 6, EasingType.EASE_OUT_BACK),
                K.of(0.25, -16, EasingType.EASE_IN_EXPO),
                K.of(0.42, 0, EasingType.LINEAR)
        ));
    }

    @Override
    protected DamageSource getDamageSource(ServerLevel level, LivingEntity owner) {
        return ModDamageTypes.of(level, DamageTypes.STING, owner);
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, Entity entity, LivingEntity living, Entity victim) {
        victim.hurt(damageSource, 8.3F + (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (victim instanceof LivingEntity living1) {
            living1.addEffect(new MobEffectInstance(ModEffects.SHADOWFLAME, 300));
        }
        VectorUtils.knockBackA2B(entity, victim, 0.5, 0.1);
    }
}
