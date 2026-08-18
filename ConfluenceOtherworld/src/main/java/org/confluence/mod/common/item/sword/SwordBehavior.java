package org.confluence.mod.common.item.sword;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/// 剑的独立运行时行为。
public interface SwordBehavior {
    default void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {}

    default void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity victim, DamageSource source) {}

    default float modifyDamage(ItemStack stack, DamageSource source, @Nullable Entity attacker, LivingEntity victim, float amount) {
        return amount;
    }

    default void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {}
}
