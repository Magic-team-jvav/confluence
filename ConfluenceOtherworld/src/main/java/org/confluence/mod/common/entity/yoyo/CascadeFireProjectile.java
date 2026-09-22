package org.confluence.mod.common.entity.yoyo;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class CascadeFireProjectile extends BaseYoyoProjectile {
    public CascadeFireProjectile(EntityType<? extends CascadeFireProjectile> type, Level level) {super(type, level);}

    @Override
    protected ParticleOptions particle() {return ParticleTypes.FLAME;}

    @Override
    protected float damageMultiplier() {return 2;}

    @Override
    protected double targetRange() {return 13;}

    @Override
    protected boolean requiresLineOfSight() {return true;}

    @Override
    protected void onHit(Entity recipient) {
        if (random.nextInt(3) == 0) recipient.setSecondsOnFire(1 + random.nextInt(4));
    }
}
