package org.confluence.mod.common.item.sword;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/// 剑气在指定时机生成的客户端粒子。
public record SwordProjectileParticleEffect(Event event, ResourceLocation emitter) {
    public static final Codec<SwordProjectileParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Event.CODEC.fieldOf("event").forGetter(SwordProjectileParticleEffect::event),
            ResourceLocation.CODEC.fieldOf("emitter").forGetter(SwordProjectileParticleEffect::emitter)
    ).apply(instance, SwordProjectileParticleEffect::new));

    public static SwordProjectileParticleEffect emitter(Event event, ResourceLocation emitter) {
        return new SwordProjectileParticleEffect(event, emitter);
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
