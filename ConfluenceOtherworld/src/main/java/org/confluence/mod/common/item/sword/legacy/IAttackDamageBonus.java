package org.confluence.mod.common.item.sword.legacy;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public interface IAttackDamageBonus {
    float get(Entity target, float damage, DamageSource damageSource);
}
