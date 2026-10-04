package org.confluence.mod.common.effect.neutral;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;

public class SparkleSlimeEffect extends MobEffect {
    private static final DustParticleOptions PINK = new DustParticleOptions(new Vector3f(1.0F, 0.3F, 0.8F), 0.8F);
    private static final DustParticleOptions PURPLE = new DustParticleOptions(new Vector3f(0.6F, 0.2F, 0.9F), 0.8F);

    public SparkleSlimeEffect() {
        super(MobEffectCategory.NEUTRAL, 0xAF52D6);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (living.level().isClientSide) {
            living.level().addParticle(living.getRandom().nextBoolean() ? PINK : PURPLE,
                    living.getRandomX(0.5), living.getRandomY(), living.getRandomZ(0.5), 0.0, -0.04, 0.0);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 2 == 0;
    }
}
