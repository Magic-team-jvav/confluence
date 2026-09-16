package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ThrownRockProjectile extends StraightMonsterProjectile implements ItemSupplier {
    public static final double GRAVITY = 0.05;

    public ThrownRockProjectile(EntityType<? extends ThrownRockProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return velocity.add(0.0, -GRAVITY, 0.0);
    }

    @Override
    public ItemStack getItem() {
        return Items.STONE.getDefaultInstance();
    }
}
