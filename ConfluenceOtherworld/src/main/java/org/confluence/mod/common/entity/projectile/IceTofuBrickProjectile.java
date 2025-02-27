package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.common.init.ModEntities;

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
        super.onHitEntity(result);
        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }
}
