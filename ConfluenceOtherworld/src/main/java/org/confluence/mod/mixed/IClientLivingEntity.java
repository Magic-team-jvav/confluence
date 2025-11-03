package org.confluence.mod.mixed;

import net.minecraft.world.entity.LivingEntity;

public interface IClientLivingEntity {
    boolean confluence$deadO(boolean... dead);

    static IClientLivingEntity of(LivingEntity living) {
        return (IClientLivingEntity) living;
    }
}
