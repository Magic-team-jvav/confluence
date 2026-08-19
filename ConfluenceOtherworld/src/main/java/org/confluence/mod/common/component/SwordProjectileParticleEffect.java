package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/// 剑气在指定时机生成的客户端粒子。
public record SwordProjectileParticleEffect(Event event, Optional<ParticleOptions> particle,
                                            Optional<ResourceLocation> emitter, int interval,
                                            int count, float spread, float velocityScale) {
    public static final Codec<SwordProjectileParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Event.CODEC.fieldOf("event").forGetter(SwordProjectileParticleEffect::event),
            ParticleTypes.CODEC.optionalFieldOf("particle").forGetter(SwordProjectileParticleEffect::particle),
            ResourceLocation.CODEC.optionalFieldOf("emitter").forGetter(SwordProjectileParticleEffect::emitter),
            Codec.INT.optionalFieldOf("interval", 1).forGetter(SwordProjectileParticleEffect::interval),
            Codec.INT.optionalFieldOf("count", 1).forGetter(SwordProjectileParticleEffect::count),
            Codec.FLOAT.optionalFieldOf("spread", 0.0F).forGetter(SwordProjectileParticleEffect::spread),
            Codec.FLOAT.optionalFieldOf("velocityScale", 0.0F).forGetter(SwordProjectileParticleEffect::velocityScale)
    ).apply(instance, SwordProjectileParticleEffect::new));

    public SwordProjectileParticleEffect {
        event = Objects.requireNonNull(event, "event");
        particle = Objects.requireNonNull(particle, "particle");
        emitter = Objects.requireNonNull(emitter, "emitter");
        if (particle.isPresent() == emitter.isPresent())
            throw new IllegalArgumentException("Exactly one particle source must be configured");
        if (emitter.isPresent() && event != Event.TRAIL)
            throw new IllegalArgumentException("Particle emitters only support trail events");
        if (interval < 1) throw new IllegalArgumentException("interval must be positive");
        if (count < 0) throw new IllegalArgumentException("count must be non-negative");
        if (!Float.isFinite(spread) || spread < 0.0F)
            throw new IllegalArgumentException("spread must be finite and non-negative");
        if (!Float.isFinite(velocityScale))
            throw new IllegalArgumentException("velocityScale must be finite");
    }

    public static SwordProjectileParticleEffect particle(Event event, ParticleOptions particle, int interval, int count, float spread, float velocityScale) {
        return new SwordProjectileParticleEffect(event, Optional.of(particle), Optional.empty(), interval, count, spread, velocityScale);
    }

    public static SwordProjectileParticleEffect emitter(ResourceLocation emitter) {
        return new SwordProjectileParticleEffect(Event.TRAIL, Optional.empty(), Optional.of(emitter), 1, 0, 0.0F, 0.0F);
    }

    public enum Event implements StringRepresentable {
        TRAIL,
        ENTITY_HIT,
        BLOCK_HIT;

        public static final Codec<Event> CODEC = StringRepresentable.fromEnum(Event::values);

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
