package org.confluence.mod.common.entity.yoyo;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class CascadeFireProjectile extends BaseYoyoProjectile {
    private int fireChanceDenominator;
    private int minFireSeconds;
    private int maxFireSeconds;

    public CascadeFireProjectile(EntityType<? extends CascadeFireProjectile> type, Level level) {super(type, level);}

    public CascadeFireProjectile configureIgnition(int chanceDenominator, int minimumSeconds, int maximumSeconds) {
        fireChanceDenominator = chanceDenominator;
        minFireSeconds = minimumSeconds;
        maxFireSeconds = maximumSeconds;
        return this;
    }

    @Override
    protected ParticleOptions particle() {return ParticleTypes.FLAME;}

    @Override
    protected boolean requiresLineOfSight() {return true;}

    @Override
    protected void onHit(Entity recipient) {
        if (fireChanceDenominator > 0 && random.nextInt(fireChanceDenominator) == 0)
            recipient.setSecondsOnFire(minFireSeconds + random.nextInt(maxFireSeconds - minFireSeconds + 1));
    }
}
