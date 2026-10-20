package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.init.ModEffects;

public class WandOfFrostingProjectile extends BaseManaStaffProjectileEntity {
    public WandOfFrostingProjectile(LivingEntity living) {
        super(living, Variant.FROST);
    }

    @Override
    protected void afterHurtTarget(Entity target) {
        LivingEntity living = LibEntityUtils.effectTarget(target);
        if (!level().isClientSide && living != null) {
            int duration = living.getRandom1211().nextFloat() < 2.0F / 3.0F ? 40 : 60;
            living.addEffect(new MobEffectInstance(ModEffects.FROST_BURN.get(), duration));
        }
    }
}
