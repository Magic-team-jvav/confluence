package org.confluence.mod.common.entity.yoyo;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class KrakenWaveProjectile extends BaseYoyoProjectile {
    public KrakenWaveProjectile(EntityType<? extends KrakenWaveProjectile> type, Level level) {super(type, level);}

    @Override
    protected ParticleOptions particle() {return ParticleTypes.SPLASH;}

    @Override
    protected boolean excludesInitialTarget() {return false;}

    @Override
    protected boolean pierces() {return true;}

    @Override
    public Type confluence$getImmunityType() {return Type.STATIC;}
}
