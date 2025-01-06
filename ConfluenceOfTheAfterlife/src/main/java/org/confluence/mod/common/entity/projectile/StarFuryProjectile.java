package org.confluence.mod.common.entity.projectile;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class StarFuryProjectile extends SwordProjectile {
    public int hitCount = 2;//可穿透两个目标

    public StarFuryProjectile(EntityType<? extends SwordProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if(!this.level().isClientSide()) {

            Entity entity1 = pResult.getEntity();
            Entity entity = this.getOwner();
            entity1.hurt(this.damageSources().mobProjectile(this, entity instanceof LivingEntity livingentity ? livingentity : null), getBaseDamage());
            if(--hitCount == 0)
                discard();
        }
    }
    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if(!this.level().isClientSide())
            discard();
    }

}
