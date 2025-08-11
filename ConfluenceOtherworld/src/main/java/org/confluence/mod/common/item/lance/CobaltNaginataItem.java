package org.confluence.mod.common.item.lance;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModDamageTypes;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.loading.math.value.Constant;

public class CobaltNaginataItem extends AbstractLanceItem {
    public CobaltNaginataItem() {
        super(new Properties().attributes(entityInteractionRange(4)), ModRarity.BLUE,15, 5, createKeyframes(
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
        victim.hurt(damageSource, 13.7F + (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE));
        VectorUtils.knockBackA2B(entity, victim, 0.4, 0.1);
    }
}
