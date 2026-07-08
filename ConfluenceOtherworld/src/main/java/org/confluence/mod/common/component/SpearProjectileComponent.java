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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.terraentity.api.entity.IGeneration;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.registries.generation.variant.ForwardGeneration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * <h1>长矛弹射物组件</h1>
 * 存储长矛弹射物的所有数据驱动参数，使用 Builder 模式构建。
 * <p>
 * 纯 POJO，兼容 1.20.1 移植。
 */
public class SpearProjectileComponent implements DataComponentType<SpearProjectileComponent> {

    public final float damageFactor;
    public final float baseSpeed;
    public final float acceleration;
    public final int existTicks;
    public final float gravity;
    public final int cooldown;
    public final ResourceLocation soundEvent;
    public final ResourceLocation projType;
    public final Optional<ITrackType> trackType;
    public final IGeneration generation;
    public final Optional<Integer> pierceCount;
    public final Optional<EffectStrategyComponent> hitEffect;

    private SpearProjectileComponent(Builder b) {
        this(b.damageFactor, b.baseSpeed, b.acceleration, b.existTicks, b.gravity, b.cooldown,
                b.soundEvent, b.projType, b.trackType, b.generation, b.pierceCount, b.hitEffect);
    }

    /** CODEC 反序列化用全参数构造器 */
    private SpearProjectileComponent(
            float damageFactor, float baseSpeed, float acceleration, int existTicks,
            float gravity, int cooldown, ResourceLocation soundEvent, ResourceLocation projType,
            Optional<ITrackType> trackType, IGeneration generation,
            Optional<Integer> pierceCount, Optional<EffectStrategyComponent> hitEffect) {
        this.damageFactor = damageFactor;
        this.baseSpeed = baseSpeed;
        this.acceleration = acceleration;
        this.existTicks = existTicks;
        this.gravity = gravity;
        this.cooldown = cooldown;
        this.soundEvent = soundEvent;
        this.projType = projType;
        this.trackType = trackType;
        this.generation = generation;
        this.pierceCount = pierceCount;
        this.hitEffect = hitEffect;
    }

    // ── Codec 访问器（与 record 风格保持一致） ──

    public float damageFactor() { return damageFactor; }
    public float baseSpeed() { return baseSpeed; }
    public float acceleration() { return acceleration; }
    public int existTicks() { return existTicks; }
    public float gravity() { return gravity; }
    public int cooldown() { return cooldown; }
    public ResourceLocation soundEvent() { return soundEvent; }
    public ResourceLocation projType() { return projType; }
    public Optional<ITrackType> trackType() { return trackType; }
    public IGeneration generation() { return generation; }
    public Optional<Integer> pierceCount() { return pierceCount; }
    public Optional<EffectStrategyComponent> hitEffect() { return hitEffect; }

    // ── CODEC / STREAM_CODEC ──

    public static final Codec<SpearProjectileComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("damageFactor").forGetter(SpearProjectileComponent::damageFactor),
            Codec.FLOAT.fieldOf("baseSpeed").forGetter(SpearProjectileComponent::baseSpeed),
            Codec.FLOAT.fieldOf("acceleration").forGetter(SpearProjectileComponent::acceleration),
            Codec.INT.fieldOf("existTicks").forGetter(SpearProjectileComponent::existTicks),
            Codec.FLOAT.fieldOf("gravity").forGetter(SpearProjectileComponent::gravity),
            Codec.INT.fieldOf("cooldown").forGetter(SpearProjectileComponent::cooldown),
            ResourceLocation.CODEC.fieldOf("soundEvent").forGetter(SpearProjectileComponent::soundEvent),
            ResourceLocation.CODEC.fieldOf("projType").forGetter(SpearProjectileComponent::projType),
            ITrackType.TYPED_CODEC.optionalFieldOf("trackType").forGetter(SpearProjectileComponent::trackType),
            IGeneration.TYPED_CODEC.fieldOf("generation").forGetter(SpearProjectileComponent::generation),
            Codec.INT.optionalFieldOf("pierceCount").forGetter(SpearProjectileComponent::pierceCount),
            EffectStrategyComponent.CODEC.optionalFieldOf("hitEffect").forGetter(SpearProjectileComponent::hitEffect)
    ).apply(instance, SpearProjectileComponent::new));

    public static final StreamCodec<ByteBuf, SpearProjectileComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    @Override
    public @Nullable Codec<SpearProjectileComponent> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, SpearProjectileComponent> streamCodec() {
        return STREAM_CODEC;
    }

    // ── 预定义长矛弹射物 ──

    /** 风暴长矛 — 直线加速弹射物 */
    public static final SpearProjectileComponent STORM_SPEAR_PROJ = new Builder()
            .damageFactor(1.5f).baseSpeed(0.1f).acceleration(1.0f)
            .existTicks(40).gravity(0.0f).cooldown(15)
            .soundEvent(ModSoundEvents.FROZEN_ARROW.getId())
            .projType(ModEntities.STORM_SPEAR_SHOT_PROJECTILE.getId())
            .generation(ForwardGeneration.of(0, 0))
            .build();

    /** 直线标准弹射物 */
    public static final SpearProjectileComponent ORICHALCUM_HALBERD_PROJ = new Builder()
            .damageFactor(1.2f).baseSpeed(1.2f).acceleration(0.95f)
            .existTicks(20).gravity(0.0f).cooldown(12)
            .soundEvent(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(Confluence.asResource("orichalcum_halberd_projectile"))
            .generation(ForwardGeneration.of(0, 0))
            .build();

    /** 蘑菇孢子 - 自旋悬浮弹射物 */
    public static final SpearProjectileComponent MUSHROOM_SPEAR_PROJ = new Builder()
            .damageFactor(1.0f).baseSpeed(0.0f).acceleration(0.95f)
            .existTicks(20).gravity(0.0f).cooldown(12)
            .soundEvent(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(Confluence.asResource("mushroom_projectile"))
            .generation(ForwardGeneration.of(0, 0))
            .build();

    /** 北极 — 弧形雪花弹射物 */
    public static final SpearProjectileComponent NORTH_POLE_PROJ = new Builder()
            .damageFactor(1.0f).baseSpeed(1.0f).acceleration(0.99f)
            .existTicks(120).gravity(0.03f).cooldown(18)
            .soundEvent(ModSoundEvents.FROZEN_ARROW.getId())
            .projType(Confluence.asResource("north_pole_projectile"))
            .generation(ForwardGeneration.of(0, 0))
            .pierceCount(3)
            .build();

    /**
     * 叶绿长戟 — 孢子云弹射物
     * 注意：该弹射物的生命管理使用速度控制。
     */
    public static final SpearProjectileComponent SPORE_CLOUD_PROJ = new Builder()
            .damageFactor(0.8f).baseSpeed(1.2f).acceleration(1.0f)
            .existTicks(200).gravity(0.0f).cooldown(20)
            .soundEvent(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(Confluence.asResource("spore_cloud_projectile"))
            .generation(ForwardGeneration.of(0, (float) 1.5))
            .pierceCount(Integer.MAX_VALUE)
            .build();

    /** 恶魂长戟 — 恶魂弹射物，水平飞行，无限穿透穿墙 */
    public static final SpearProjectileComponent GHASTLY_PROJECTILE = new Builder()
            .damageFactor(0.9f).baseSpeed(0.5f).acceleration(1.0f)
            .existTicks(10).gravity(0.0f).cooldown(15)
            .soundEvent(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.GHASTLY_PROJECTILE.getId())
            .generation(ForwardGeneration.of(0, 0))
            .pierceCount(Integer.MAX_VALUE)
            .build();

    // ── 工具方法 ──

    public SoundEvent getSoundEvent() {
        return BuiltInRegistries.SOUND_EVENT.get(soundEvent);
    }

    /**
     * 计算实际速度（受远程速度属性影响）
     */
    public float getVelocity(LivingEntity living) {
        float velocity = baseSpeed;
        AttributeInstance attributeInstance = living.getAttribute(LibAttributes.getRangedVelocity());
        if (attributeInstance != null) return velocity * (float) attributeInstance.getValue();
        return velocity;
    }

    /**
     * 计算实际冷却（受攻击速度属性影响）
     */
    public int getAttackSpeed(LivingEntity living) {
        int cooldown = this.cooldown;
        AttributeInstance attributeInstance = living.getAttribute(Attributes.ATTACK_SPEED);
        if (attributeInstance != null)
            return Math.max(cooldown - (int) (attributeInstance.getValue() / 3.0), 0);
        return cooldown;
    }

    // ── Builder ──

    public static class Builder {
        float damageFactor;
        float baseSpeed;
        float acceleration = 1.0f;
        int existTicks = 40;
        float gravity;
        int cooldown;
        ResourceLocation soundEvent;
        ResourceLocation projType;
        Optional<ITrackType> trackType = Optional.empty();
        IGeneration generation = ForwardGeneration.of(0, 0);
        Optional<Integer> pierceCount = Optional.empty();
        Optional<EffectStrategyComponent> hitEffect = Optional.empty();

        public Builder damageFactor(float v) { this.damageFactor = v; return this; }
        public Builder baseSpeed(float v) { this.baseSpeed = v; return this; }
        public Builder acceleration(float v) { this.acceleration = v; return this; }
        public Builder existTicks(int v) { this.existTicks = v; return this; }
        public Builder gravity(float v) { this.gravity = v; return this; }
        public Builder cooldown(int v) { this.cooldown = v; return this; }
        public Builder soundEvent(ResourceLocation v) { this.soundEvent = v; return this; }
        public Builder projType(ResourceLocation v) { this.projType = v; return this; }
        public Builder trackType(ITrackType v) { this.trackType = Optional.ofNullable(v); return this; }
        public Builder generation(IGeneration v) { this.generation = v; return this; }
        public Builder pierceCount(int v) { this.pierceCount = Optional.of(v); return this; }
        public Builder hitEffect(EffectStrategyComponent v) { this.hitEffect = Optional.ofNullable(v); return this; }

        public SpearProjectileComponent build() {
            return new SpearProjectileComponent(this);
        }
    }
}
