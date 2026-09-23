package org.confluence.mod.common.entity.yoyo;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class ChikCrystalProjectile extends BaseYoyoProjectile {
    private int immunityTicks;

    public ChikCrystalProjectile(EntityType<? extends ChikCrystalProjectile> type, Level level) {super(type, level);}

    public ChikCrystalProjectile configureImmunity(int ticks) {
        immunityTicks = ticks;
        return this;
    }

    @Override
    protected ParticleOptions particle() {return ParticleTypes.END_ROD;}

    @Override
    protected boolean ignoresExcludedTarget() {return tickCount <= immunityTicks;}

    @Override
    public Type confluence$getImmunityType() {return Type.STATIC;}

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {return immunityTicks;}
}
