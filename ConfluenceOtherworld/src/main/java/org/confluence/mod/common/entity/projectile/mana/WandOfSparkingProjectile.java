package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.confluence.lib.util.LibEntityUtils;

public class WandOfSparkingProjectile extends BaseManaStaffProjectileEntity {
    public WandOfSparkingProjectile(LivingEntity living) {
        super(living, Variant.SPARK);
    }

    @Override
    protected void afterHurtTarget(Entity target) {
        LivingEntity living = LibEntityUtils.effectTarget(target);
        if (!level().isClientSide && living != null) {
            living.igniteForTicks(100);
        }
    }
}
