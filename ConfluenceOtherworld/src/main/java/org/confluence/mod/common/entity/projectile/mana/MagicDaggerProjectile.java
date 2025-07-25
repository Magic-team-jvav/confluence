package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.entity.projectile.ThrowableDropSelfProjectile;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;

public class MagicDaggerProjectile extends ThrowableDropSelfProjectile {
    public MagicDaggerProjectile(EntityType<? extends ThrowableDropSelfProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MagicDaggerProjectile(LivingEntity living) {
        super(ModEntities.MAGIC_DAGGER_PROJECTILE.get(), living.level());
    }

    @Override
    protected DamageSource getDamageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockState blockstate = level().getBlockState(result.getBlockPos());
        blockstate.onProjectileHit(level(), blockstate, result, this);
        discard();
    }
}
