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
import java.util.function.Supplier;

/**
 * <h1>长矛弹射物组件</h1>
 *
 * @param damageFactor 伤害系数
 * @param baseSpeed    基础速度
 * @param acceleration 加速度（每tick乘数）
 * @param existTicks   存在时间
 * @param gravity      重力加速度
 * @param cooldown     冷却时间
 * @param soundEvent   发射音效
 * @param projType     弹射物实体类型ID
 * @param trackType    追踪类型
 * @param generation   生成位置策略
 * @param pierceCount  穿透次数
 * @param hitEffect    击中特效
 */
public record SpearProjectileComponent(
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
        Optional<Integer> pierceCount,
        Optional<EffectStrategyComponent> hitEffect
) implements DataComponentType<SpearProjectileComponent> {

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

    /**
     * 风暴长矛 — 直线加速弹射物
     */
    public static final Supplier<SpearProjectileComponent> STORM_SPEAR_PROJ =
            () -> new SpearProjectileComponent(1.5f, 0.1f, 1.0f, 40, 0.0f, 15,
                    ModSoundEvents.FROZEN_ARROW.getId(),
                    ModEntities.STORM_SPEAR_SHOT_PROJECTILE.getId(),
                    Optional.empty(), ForwardGeneration.of(0, 0),
                    Optional.empty(), Optional.empty());

    /**
     * 直线标准弹射物
     */
    public static final Supplier<SpearProjectileComponent> ORICHALCUM_HALBERD_PROJ =
            () -> new SpearProjectileComponent(1.2f, 1.2f, 0.95f, 20, 0.0f, 12,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    Confluence.asResource("orichalcum_halberd_projectile"),
                    Optional.empty(), ForwardGeneration.of(0, 0),
                    Optional.empty(), Optional.empty());
    /**
     * 蘑菇孢子 - 自旋悬浮弹射物
     */
    public static final Supplier<SpearProjectileComponent> MUSHROOM_SPEAR_PROJ =
            () -> new SpearProjectileComponent(1.0f, 0.0f, 0.95f, 20, 0.0f, 12,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    Confluence.asResource("mushroom_projectile"),
                    Optional.empty(), ForwardGeneration.of(0, 0),
                    Optional.empty(), Optional.empty());

    /**
     * 北极 — 弧形雪花弹射物
     */
    public static final Supplier<SpearProjectileComponent> NORTH_POLE_PROJ =
            () -> new SpearProjectileComponent(1.0f, 1.0f, 0.99f, 120, 0.03f, 18,
                    ModSoundEvents.FROZEN_ARROW.getId(),
                    Confluence.asResource("north_pole_projectile"),
                    Optional.empty(), ForwardGeneration.of(0, 0),
                    Optional.of(3), Optional.empty());

    /**
     * 叶绿长戟 — 孢子云弹射物
     */
    public static final Supplier<SpearProjectileComponent> SPORE_CLOUD_PROJ =
            () -> new SpearProjectileComponent(0.8f, 1.2f, 1.0f, 200, 0.0f, 20,//注意，该弹射物的生命管理使用速度控制。
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    Confluence.asResource("spore_cloud_projectile"),
                    Optional.empty(), ForwardGeneration.of(0, (float) 1.5),
                    Optional.of(Integer.MAX_VALUE), Optional.empty());

    /**
     * 恶魂长戟 — 恶魂弹射物，水平飞行，无限穿透穿墙
     */
    public static final Supplier<SpearProjectileComponent> GHASTLY_PROJECTILE =
            () -> new SpearProjectileComponent(0.9f, 0.5f, 1.0f, 10, 0.0f, 15,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.GHASTLY_PROJECTILE.getId(),
                    Optional.empty(), ForwardGeneration.of(0, 0),
                    Optional.of(Integer.MAX_VALUE), Optional.empty());

    public SoundEvent getSoundEvent() {
        return BuiltInRegistries.SOUND_EVENT.get(soundEvent);
    }

    public static final StreamCodec<ByteBuf, SpearProjectileComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    @Override
    public @Nullable Codec<SpearProjectileComponent> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, SpearProjectileComponent> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof SpearProjectileComponent other) {
            return Float.compare(damageFactor, other.damageFactor) == 0 &&
                    Float.compare(baseSpeed, other.baseSpeed) == 0 &&
                    Float.compare(acceleration, other.acceleration) == 0 &&
                    existTicks == other.existTicks &&
                    Float.compare(gravity, other.gravity) == 0 &&
                    cooldown == other.cooldown &&
                    soundEvent.equals(other.soundEvent) &&
                    projType.equals(other.projType) &&
                    trackType.equals(other.trackType) &&
                    generation.equals(other.generation) &&
                    pierceCount.equals(other.pierceCount) &&
                    hitEffect.equals(other.hitEffect);
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
        result = 31 * result + pierceCount.hashCode();
        result = 31 * result + hitEffect.hashCode();
        return result;
    }

    /**
     * 计算实际速度（受远程速度属性影响）
     */
    public float getVelocity(LivingEntity living) {
        float velocity = baseSpeed();
        AttributeInstance attributeInstance = living.getAttribute(LibAttributes.getRangedVelocity());
        if (attributeInstance != null) return velocity * (float) attributeInstance.getValue();
        return velocity;
    }

    /**
     * 计算实际冷却（受攻击速度属性影响）
     */
    public int getAttackSpeed(LivingEntity living) {
        int cooldown = cooldown();
        AttributeInstance attributeInstance = living.getAttribute(Attributes.ATTACK_SPEED);
        if (attributeInstance != null)
            return Math.max(cooldown - (int) (attributeInstance.getValue() / 3.0), 0);
        return cooldown;
    }
}
