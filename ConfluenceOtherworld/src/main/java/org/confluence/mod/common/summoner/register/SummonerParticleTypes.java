package org.confluence.mod.common.summoner.register;

import net.minecraft.core.particles.ParticleType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.particle.GenericParticleOptions;
import org.mesdag.portlib.registries.PortParticleTypeRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;

public final class SummonerParticleTypes {

    public static final PortParticleTypeRegistration PARTICLES = PortRegisterHandler.particleType(Confluence.MODID);

    public static final PortRegistryEntry<ParticleType<?>, ParticleType<GenericParticleOptions>> GENERIC =
            PARTICLES.register("generic", true, GenericParticleOptions.CODEC, GenericParticleOptions.STREAM_CODEC);

    private SummonerParticleTypes() {
    }

    public static void init() {
    }
}
