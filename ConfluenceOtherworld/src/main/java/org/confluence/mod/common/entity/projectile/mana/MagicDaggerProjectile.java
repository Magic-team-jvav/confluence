package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.entity.projectile.ThrowableDropSelfProjectile;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;

public class MagicDaggerProjectile extends ThrowableDropSelfProjectile {
    public MagicDaggerProjectile(EntityType<? extends ThrowableDropSelfProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MagicDaggerProjectile(LivingEntity living) {
        super(ModEntities.MAGIC_DAGGER_PROJECTILE.get(), living.level());
    }

    @Override
    public DamageSource getDamageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity.hurt(getDamageSource(), getCalculatedDamage())) {
            hitSet.add(entity.getUUID());
            this.damage -= deltaDamage;
            VectorUtils.knockBackA2B(this, entity, 0.5, 0.2);
            if (penetrate >= 2) {
                discard();
            } else {
                ++this.penetrate;
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockState blockstate = level().getBlockState(result.getBlockPos());
        blockstate.onProjectileHit(level(), blockstate, result, this);
        discard();
    }
}
