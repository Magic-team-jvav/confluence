package org.confluence.terraentity.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;

import java.util.function.Supplier;

public final class TEParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, TerraEntity.MODID);

    public static final Supplier<SimpleParticleType> ITEM_GEL = PARTICLES.register("item_gel", () -> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> LEAVES = register("leaves", true);
    public static final Supplier<SimpleParticleType> SPIT = register("spit", true);
    public static final Supplier<SimpleParticleType> SPIT_GLOW = register("spit_glow", true);
    public static final Supplier<SimpleParticleType> FIRE_BOUND = register("fire_bound", true);

    private static Supplier<SimpleParticleType> register(String id, boolean overrideLimiter) {
        return PARTICLES.register(id, () -> new SimpleParticleType(overrideLimiter));
    }
}
