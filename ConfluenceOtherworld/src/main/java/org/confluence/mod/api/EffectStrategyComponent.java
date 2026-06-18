package org.confluence.mod.api;

import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface EffectStrategyComponent {
    void applyAll(LivingEntity owner, LivingEntity target);
}
