package org.confluence.mod.common.entity.projectile.sword;


import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.mixed.Immunity;

public class StarFuryProjectile extends SwordProjectile implements Immunity {

    public StarFuryProjectile(EntityType<? extends SwordProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        hitCount = 2;
        canPenalize = true;
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
    }

    @Override
    public Type confluence$getImmunityType(){
        return Type.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource){
        return 5;
    }
}
