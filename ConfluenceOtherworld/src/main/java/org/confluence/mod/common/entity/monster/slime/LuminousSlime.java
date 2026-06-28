package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 夜明史莱姆 —— 持续发光并产生残影粒子。
 */
public class LuminousSlime extends BaseSlime {

    public LuminousSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFFFFFF, false, 60);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(36.4f, 30, 180.0f, 60);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && tickCount % 3 == 0) {
            level().addParticle(ParticleTypes.END_ROD,
                    getX() + random.nextGaussian() * 0.3,
                    getY() + getBbHeight() * 0.5,
                    getZ() + random.nextGaussian() * 0.3,
                    0, 0.02, 0);
        }
    }

}
