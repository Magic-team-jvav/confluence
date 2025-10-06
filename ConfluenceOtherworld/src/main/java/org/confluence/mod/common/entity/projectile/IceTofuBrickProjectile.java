package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.mixed.LibLivingEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ModItems;

public class IceTofuBrickProjectile extends ThrowableItemProjectile {
    public IceTofuBrickProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public IceTofuBrickProjectile(double x, double y, double z, Level level) {
        super(ModEntities.ICE_TOFU_BRICK_PROJECTILE.get(), x, y, z, level);
    }

    public IceTofuBrickProjectile(LivingEntity shooter, Level level) {
        super(ModEntities.ICE_TOFU_BRICK_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        freezeTarget();
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        freezeTarget();
        super.onHitBlock(result);
    }

    private void freezeTarget() {
        DamageSource source = damageSources().mobProjectile(this, getOwner() instanceof LivingEntity living ? living : null);
        for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, new AABB(position().add(-1, -1, -1), position().add(1, 1, 1)), living -> !(living instanceof Player))) {
            living.hurt(source, 9);
            ((LibLivingEntity) living).confluence$setTickFreezeTime(100);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ICE_TOFU_BRICK.get();
    }
}
