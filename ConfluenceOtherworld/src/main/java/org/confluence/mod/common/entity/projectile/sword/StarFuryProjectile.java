package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class StarFuryProjectile extends SwordProjectile {
    public StarFuryProjectile(EntityType<? extends SwordProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        remainingHits = 2;
        survivesBlockHit = true;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        clearKnockback();
    }

    @Override
    public void tick() {
        super.tick();
        if (isInWater() && !isRemoved()) setDeltaMovement(getDeltaMovement().scale(1.25));
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 5;
    }
}
