package org.confluence.mod.common.entity.projectile.flail;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/// 花之力发射的直线花瓣。
public final class FlowerPowerPetalProjectile extends FlailAuxiliaryProjectile {
    public FlowerPowerPetalProjectile(EntityType<? extends FlowerPowerPetalProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        hurtTarget(hit.getEntity(), 0.15F, 0.08F);
        discard();
    }
}
