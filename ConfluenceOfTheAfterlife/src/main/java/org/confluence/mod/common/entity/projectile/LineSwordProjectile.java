package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LineSwordProjectile extends SwordProjectile {

    public LineSwordProjectile(EntityType<LineSwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setDeltaMovement(vec3.scale(0.93));
        setPos(offX, offY, offZ);
        if (tickCount >= SwordProjectile.TIME_EXISTENCE) discard();
    }
}
