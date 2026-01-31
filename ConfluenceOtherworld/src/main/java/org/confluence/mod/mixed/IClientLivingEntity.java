package org.confluence.mod.mixed;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface IClientLivingEntity {
    boolean confluence$deadO(boolean... dead);

    Vec3 confluence$deathMotion(Vec3... motion);

    static IClientLivingEntity of(LivingEntity living) {
        return (IClientLivingEntity) living;
    }
}
