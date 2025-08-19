package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.terraentity.init.TEEffects;

public class WandOfFrostingProjectile extends BaseManaStaffProjectileEntity {
    public WandOfFrostingProjectile(LivingEntity living) {
        super(living, Variant.FROST);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (!level().isClientSide && entityHitResult.getEntity() instanceof LivingEntity living) {
            int duration = living.getRandom().nextFloat() < 2.0F / 3.0F ? 40 : 60;
            living.addEffect(new MobEffectInstance(TEEffects.FROST_BURN, duration));
        }
        super.onHitEntity(entityHitResult);
    }
}
