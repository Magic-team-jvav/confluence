package org.confluence.mod.common.item.sword.legacy;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.consumer.Consumer4;

public class InventoryTickStrategy {
    /**雨伞 缓降*/
    public static final Consumer4<ItemStack, Level, Entity, Boolean> UMBRELLA_TICK = (stack, level, entity, selected) -> {
        if (!level.isClientSide && selected && entity instanceof LivingEntity living && !living.swinging) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2, 2, false, false, false));
        }
    };
}
