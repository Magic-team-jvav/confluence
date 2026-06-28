package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 金色史莱姆 —— 高血量、快速跳跃、掉落金币，稀有。
 */
public class GoldenSlime extends BaseSlime {

    public GoldenSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFFD700, false, -40);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(5.0f, 2, 97.0f, -40);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && tickCount % 11 == 0) {
            level().addParticle(ParticleTypes.HAPPY_VILLAGER,
                    getX() + random.nextGaussian() * 0.3,
                    getY() + getBbHeight() * 0.5,
                    getZ() + random.nextGaussian() * 0.3,
                    0, 0.05, 0);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result && !level().isClientSide && level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 3; i++) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        getX() + random.nextGaussian() * 0.5,
                        getY() + getBbHeight() * 0.5,
                        getZ() + random.nextGaussian() * 0.5,
                        3, 0.3, 0.3, 0.3, 0.1);
            }
        }
        return result;
    }
}
