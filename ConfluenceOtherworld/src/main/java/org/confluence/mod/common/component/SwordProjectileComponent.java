package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.api.ITrackType;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/// 剑气武器的弹幕参数。
///
/// @param damageFactor 伤害倍率
/// @param baseSpeed    基础速度
/// @param acceleration 加速度
/// @param existTicks   存在时间
/// @param gravity      重力
/// @param cooldown     冷却时间
/// @param soundEvent   发射音效
/// @param projType     弹幕实体类型
/// @param trackType    追踪类型
/// @param generation   生成位置策略
/// @param appearance      客户端表现
/// @param particleEffects 粒子效果
public record SwordProjectileComponent(
        float damageFactor,
        float baseSpeed,
        float acceleration,
        int existTicks,
        float gravity,
        int cooldown,
        ResourceLocation soundEvent,
        ResourceLocation projType,
        Optional<ITrackType> trackType,
        IGeneration generation,
        SwordProjectileAppearance appearance,
        List<SwordProjectileParticleEffect> particleEffects
) {
    public static final Codec<SwordProjectileComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("damageFactor").forGetter(SwordProjectileComponent::damageFactor),
            Codec.FLOAT.fieldOf("baseSpeed").forGetter(SwordProjectileComponent::baseSpeed),
            Codec.FLOAT.fieldOf("acceleration").forGetter(SwordProjectileComponent::acceleration),
            Codec.INT.fieldOf("existTicks").forGetter(SwordProjectileComponent::existTicks),
            Codec.FLOAT.fieldOf("gravity").forGetter(SwordProjectileComponent::gravity),
            Codec.INT.fieldOf("cooldown").forGetter(SwordProjectileComponent::cooldown),
            ResourceLocation.CODEC.fieldOf("soundEvent").forGetter(SwordProjectileComponent::soundEvent),
            ResourceLocation.CODEC.fieldOf("projType").forGetter(SwordProjectileComponent::projType),
            ITrackType.TYPED_CODEC.optionalFieldOf("trackType").forGetter(SwordProjectileComponent::trackType),
            IGeneration.TYPED_CODEC.fieldOf("generation").forGetter(SwordProjectileComponent::generation),
            SwordProjectileAppearance.CODEC.fieldOf("appearance").forGetter(SwordProjectileComponent::appearance),
            SwordProjectileParticleEffect.CODEC.listOf().optionalFieldOf("particleEffects", List.of()).forGetter(SwordProjectileComponent::particleEffects)
    ).apply(instance, SwordProjectileComponent::new));

    public static final PortStreamCodec<ByteBuf, SwordProjectileComponent> STREAM_CODEC =
            PortByteBufCodecs.fromCodec(CODEC);

    public SwordProjectileComponent {
        damageFactor = requireNonNegative(damageFactor, "damageFactor");
        baseSpeed = requirePositive(baseSpeed, "baseSpeed");
        acceleration = requireNonNegative(acceleration, "acceleration");
        gravity = requireFinite(gravity, "gravity");
        if (existTicks < 1) throw new IllegalArgumentException("existTicks must be positive");
        if (cooldown < 0) throw new IllegalArgumentException("cooldown must be non-negative");
        soundEvent = Objects.requireNonNull(soundEvent, "soundEvent");
        projType = Objects.requireNonNull(projType, "projType");
        trackType = Objects.requireNonNull(trackType, "trackType");
        generation = Objects.requireNonNull(generation, "generation");
        appearance = Objects.requireNonNull(appearance, "appearance");
        particleEffects = List.copyOf(particleEffects);
    }

    public SwordProjectileComponent(float damageFactor, float baseSpeed, float acceleration, int existTicks, float gravity, int cooldown, ResourceLocation soundEvent, ResourceLocation projType, Optional<ITrackType> trackType, IGeneration generation, SwordProjectileAppearance appearance) {
        this(damageFactor, baseSpeed, acceleration, existTicks, gravity, cooldown, soundEvent, projType,
                trackType, generation, appearance, List.of());
    }

    public SoundEvent getSoundEvent() {
        return BuiltInRegistries.SOUND_EVENT.getOptional(soundEvent).orElseThrow(() -> new IllegalStateException("Unknown sword projectile sound: " + soundEvent));
    }

    public int getCooldownTicks(LivingEntity living) {
        AttributeInstance attackSpeed = living.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed == null) return cooldown;
        return Math.max(cooldown - (int) (attackSpeed.getValue() / 3.0), 0);
    }

    private static float requirePositive(float value, String name) {
        if (requireFinite(value, name) <= 0.0F)
            throw new IllegalArgumentException(name + " must be positive");
        return value;
    }

    private static float requireNonNegative(float value, String name) {
        if (requireFinite(value, name) < 0.0F)
            throw new IllegalArgumentException(name + " must be non-negative");
        return value;
    }

    private static float requireFinite(float value, String name) {
        if (!Float.isFinite(value)) throw new IllegalArgumentException(name + " must be finite");
        return value;
    }
}
