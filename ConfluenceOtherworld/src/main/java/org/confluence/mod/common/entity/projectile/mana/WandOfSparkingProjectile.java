package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class WandOfSparkingProjectile extends BaseManaStaffProjectileEntity {
    public WandOfSparkingProjectile(LivingEntity living) {
        super(living, Variant.SPARK);
    }

    @Override
    protected void afterHurtTarget(Entity target) {
        if (!level().isClientSide) {
            target.igniteForTicks(100);
        }
    }
}
