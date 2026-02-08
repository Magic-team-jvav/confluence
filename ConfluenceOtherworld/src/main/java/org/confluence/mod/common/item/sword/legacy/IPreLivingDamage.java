package org.confluence.mod.common.item.sword.legacy;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IPreLivingDamage {
    float apply(ItemStack stack, DamageSource damageSource, @Nullable Entity attacker, LivingEntity victim, float amount);
}
