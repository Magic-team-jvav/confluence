package org.confluence.mod.common.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.particle.DamageIndicatorOptions;
import org.confluence.mod.common.particle.WholeItemParticleOptions;
import org.mesdag.portlib.registries.PortParticleTypeRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistryEntry;

public final class ModParticleTypes {
    public static void init() {}

    public static final PortParticleTypeRegistration PARTICLES = PortRegisterHandler.particleType(Confluence.MODID);

    public static final PortRegistryEntry<ParticleType<?>, ParticleType<DamageIndicatorOptions>> DAMAGE_INDICATOR = PARTICLES.register("damage_indicator", true, DamageIndicatorOptions.CODEC, DamageIndicatorOptions.STREAM_CODEC);
    public static final PortRegistryEntry<ParticleType<?>, ParticleType<WholeItemParticleOptions>> WHOLE_ITEM = PARTICLES.register("whole_item", true, WholeItemParticleOptions.CODEC, WholeItemParticleOptions.STREAM_CODEC);
    public static final PortRegistryEntry<ParticleType<?>, SimpleParticleType> LEAVES = PARTICLES.register("leaves", true);
    public static final PortRegistryEntry<ParticleType<?>, SimpleParticleType> RED_SAND = PARTICLES.register("red_sand", true);
    public static final PortRegistryEntry<ParticleType<?>, SimpleParticleType> SAND = PARTICLES.register("sand", true);
    public static final PortRegistryEntry<ParticleType<?>, SimpleParticleType> SNOW = PARTICLES.register("snow", true);
    public static final PortRegistryEntry<ParticleType<?>, SimpleParticleType> YELLOW_WILLOW = PARTICLES.register("yellow_willow", true);
    public static final PortRegistryEntry<ParticleType<?>, SimpleParticleType> LIGHT_BANE = PARTICLES.register("lights_bane", true);
    public static final PortRegistryEntry<ParticleType<?>, SimpleParticleType> LIGHT_BANE_DUST = PARTICLES.register("lights_bane_dust", true);
    public static final PortRegistryEntry<ParticleType<?>, SimpleParticleType> LIGHT_BANE_FADE = PARTICLES.register("lights_bane_fade", true);
    public static final PortRegistryEntry<ParticleType<?>, SimpleParticleType> ECTO_MIST = PARTICLES.register("ecto_mist", true);
    public static final PortRegistryEntry<ParticleType<?>, SimpleParticleType> SPORE_CLOUD = PARTICLES.register("spore_cloud", true);
}
