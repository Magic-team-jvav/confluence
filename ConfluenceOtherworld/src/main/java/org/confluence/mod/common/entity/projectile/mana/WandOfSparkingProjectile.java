package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

public class WandOfSparkingProjectile extends BaseManaStaffProjectileEntity {
    public WandOfSparkingProjectile(LivingEntity living) {
        super(living, Variant.SPARK);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (!level().isClientSide) {
            entityHitResult.getEntity().igniteForTicks(100);
        }
        super.onHitEntity(entityHitResult);
    }
}
