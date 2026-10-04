package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class NimbusRain extends StraightMonsterProjectile {
    public NimbusRain(EntityType<? extends NimbusRain> type, Level level) {
        super(type, level);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return new Vec3(0.0, Math.max(-1.5, velocity.y - 0.05), 0.0);
    }
}
