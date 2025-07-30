package org.confluence.mod.common.item.lance;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.loading.math.value.Constant;

public class SpearItem extends AbstractLanceItem {
    public SpearItem() {
        super(new Properties(), ModRarity.WHITE, 12, ObjectArrayList.of(
                new Keyframe<>(0.0, new Constant(0.0), new Constant(0.0), EasingType.LINEAR),
                new Keyframe<>(5.0, new Constant(0.0), new Constant(6.0), EasingType.EASE_OUT_BACK),
                new Keyframe<>(5.0, new Constant(6.0), new Constant(-16.0), EasingType.EASE_IN_EXPO),
                new Keyframe<>(5.0, new Constant(-16.0), new Constant(0.0), EasingType.LINEAR)
        ));
    }

    @Override
    protected DamageSource getDamageSource(ServerLevel level, LivingEntity owner) {
        return ModDamageTypes.of(level, DamageTypes.STING, owner);
    }

    @Override
    protected void onHitEntity(DamageSource damageSource, Entity entity, LivingEntity living, Entity victim) {
        victim.hurt(damageSource, 6.2F + (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE));
        VectorUtils.knockBackA2B(entity, victim, 0.5, 0.1);
    }
}
