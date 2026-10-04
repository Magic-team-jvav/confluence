package org.confluence.mod.common.summoner.register;

import net.minecraft.core.particles.ParticleType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.particle.GenericParticleOptions;
import org.confluence.mod.common.summoner.particle.ZenithParticleOptions;
import org.mesdag.portlib.registries.PortParticleTypeRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;

public final class SummonerParticleTypes {

    public static final PortParticleTypeRegistration PARTICLES = PortRegisterHandler.particleType(Confluence.MODID);

    public static final PortRegistryEntry<ParticleType<?>, ParticleType<GenericParticleOptions>> GENERIC =
            PARTICLES.register("generic", true, GenericParticleOptions.CODEC, GenericParticleOptions.STREAM_CODEC);

    /** 天顶剑粒子：水滴形头部 + 四棱锥拖尾 */
    public static final PortRegistryEntry<ParticleType<?>, ParticleType<ZenithParticleOptions>> ZENITH =
            PARTICLES.register("zenith", true, ZenithParticleOptions.CODEC, ZenithParticleOptions.STREAM_CODEC);

    private SummonerParticleTypes() {
    }

    public static void init() {
    }
}
