package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// 带逐 tick 速度倍率的直线剑气。
public class ForwardSwordProjectile extends SwordProjectile {
    public ForwardSwordProjectile(EntityType<? extends ForwardSwordProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) {
            return;
        }
        Vec3 movement = getDeltaMovement();
        float acceleration = getProjectileComponent() == null ? 0.8F : getProjectileComponent().acceleration();
        setDeltaMovement(movement.scale(acceleration));
        setPos(getX() + movement.x, getY() + movement.y, getZ() + movement.z);
    }
}
