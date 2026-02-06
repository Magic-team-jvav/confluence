package org.confluence.mod.common.item.sword.legacy;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IPostHurtEnemy {
    void accept(ItemStack stack, LivingEntity target, LivingEntity attacker);
}
