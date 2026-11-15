package org.confluence.mod.common.entity.projectile.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DeveloperArrowEntity extends BaseArrowEntity {
    public DeveloperArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public DeveloperArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(entityType, owner, pickupItemStack, firedFromWeapon);
    }

    @Override
    protected void init() {
        super.init();
        setNoGravity(true);
    }

    @Override
    public double getBaseDamage() {
        return 9999;
    }

    @Override
    protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
        target.setRemainingFireTicks(target.getRemainingFireTicks() + 200 - tickCount);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, inaccuracy);
        setDeltaMovement(getDeltaMovement().scale(2));
    }

    @Override
    protected int getPenetrationCount() {
        return 9999;
    }
}
