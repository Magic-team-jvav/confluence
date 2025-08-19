package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.api.entity.IGeneration;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.registries.generation.variant.AboveFallenGeneration;
import org.confluence.terraentity.registries.generation.variant.ForwardGeneration;
import org.confluence.terraentity.registries.hit_effect.variant.TimePossibilityAmplifierEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * <h1>弹幕组件</h1>
 * @param damageFactor 伤害系数
 * @param baseSpeed 基础速度
 * @param acceleration 加速度
 * @param existTicks 存在时间
 * @param gravity 重力
 * @param cooldown 冷却时间
 * @param soundEvent 音效
 * @param trackType 追踪类型
 * @param generation 生成器
 * @param hitEffect 击中特效
 */
public record SwordProjectileComponent (
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
        Optional<EffectStrategyComponent> hitEffect
) implements DataComponentType<SwordProjectileComponent> {

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
            EffectStrategyComponent.CODEC.optionalFieldOf("hitEffect").forGetter(SwordProjectileComponent::hitEffect)
    ).apply(instance, SwordProjectileComponent::new));

    public static final Supplier<SwordProjectileComponent> ICE_PROJ =
            ()->new SwordProjectileComponent(2,0.6f,0.9f,40, 0, 15,
                    ModSoundEvents.FROZEN_ARROW.getId(), ModEntities.ICE_BLADE_SWORD_PROJECTILE.getId(),
                    Optional.empty(), ForwardGeneration.of(0,0),
                    Optional.empty()   );

    public static final Supplier<SwordProjectileComponent> STAR_FURY_PROJ =
            ()->new SwordProjectileComponent(1.5f,1.5f,0.9f,100, 0, 15,
                    ModSoundEvents.STAR.getId(),ModEntities.STAR_FURY_PROJECTILE.getId(),
                    Optional.empty(), new AboveFallenGeneration(30,30,10,1,20,5),
                    Optional.empty() );

    public static final Supplier<SwordProjectileComponent> ENCHANTED_SWORD_PROJ =
            ()->new SwordProjectileComponent(1.2f,0.8f,0.9f,40, 0, 10,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),ModEntities.ENCHANTED_SWORD_PROJECTILE.getId(),
                    Optional.empty(), ForwardGeneration.of(0,0),
                    Optional.empty() );

    public static final Supplier<SwordProjectileComponent> GRASS_PROJ =
            ()->new SwordProjectileComponent(1,0.8f,0.9f,20, 0, 10,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.GRASS_PROJECTILE.getId(),
                    Optional.empty(), ForwardGeneration.of(0,20),
                    Optional.of(EffectStrategyComponent.of(TimePossibilityAmplifierEffect.of("grass_effect", MobEffects.POISON, 100, 1, 0.5f))));

    public static final Supplier<SwordProjectileComponent> NIGHT_PROJ =
            ()->new SwordProjectileComponent(1,0.8f,0.9f,20, 0, 10,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.NIGHTS_EDGE_PROJECTILE.getId(),
                    Optional.empty(), ForwardGeneration.of(0,20),
                    Optional.empty());




    public SoundEvent getSoundEvent() {
        return BuiltInRegistries.SOUND_EVENT.get(soundEvent);
    }

    public static final StreamCodec<ByteBuf, SwordProjectileComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    @Override
    public @Nullable Codec<SwordProjectileComponent> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, SwordProjectileComponent> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof SwordProjectileComponent other) {
            return damageFactor == other.damageFactor &&
                    baseSpeed == other.baseSpeed &&
                    acceleration == other.acceleration &&
                    existTicks == other.existTicks&&
                    gravity == other.gravity &&
                    cooldown == other.cooldown &&
                    soundEvent.equals(other.soundEvent) &&
                    trackType.equals(other.trackType) &&
                    generation.equals(other.generation) &&
                    projType.equals(other.projType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(damageFactor);
        result = 31 * result + Float.hashCode(baseSpeed);
        result = 31 * result + Float.hashCode(acceleration);
        result = 31 * result + existTicks;
        result = 31 * result + Float.hashCode(gravity);
        result = 31 * result + cooldown;
        result = 31 * result + soundEvent.hashCode();
        result = 31 * result + projType.hashCode();
        result = 31 * result + trackType.hashCode();
        result = 31 * result + generation.hashCode();
        result = 31 * result + hitEffect.hashCode();
        return result;
    }

    public float getVelocity(LivingEntity living) {
        float velocity = baseSpeed();
        AttributeInstance attributeInstance = living.getAttribute(TCAttributes.getRangedVelocity());
        if (attributeInstance != null) return velocity * (float) attributeInstance.getValue();
        return velocity;
    }

    public int getAttackSpeed(LivingEntity living){
        int cooldown = cooldown();
        AttributeInstance attributeInstance = living.getAttribute(Attributes.ATTACK_SPEED);
        if (attributeInstance != null) return Math.max(cooldown - (int) (attributeInstance.getValue() / 3.0), 0);
        return cooldown;
    }
}
