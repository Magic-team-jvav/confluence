package org.confluence.lib.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;

@javax.annotation.ParametersAreNonnullByDefault
public class MultiplyExplosionDamageCalculator extends ExplosionDamageCalculator {
    private final float multiplier;

    public MultiplyExplosionDamageCalculator(float multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public float getEntityDamageAmount(Explosion explosion, Entity entity) {
        return super.getEntityDamageAmount(explosion, entity) * multiplier;
    }
}
