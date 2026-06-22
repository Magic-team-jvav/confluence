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
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.registries.hit_effect.variant.PrefabEffect;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.confluence.terraentity.registries.hit_effect.variant.TimePossibilityAmplifierEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * <h1>连枷弹射物组</h1>
 * 存储连枷的所有数据驱动参数，通过 Codec 持久化到物品 DataComponent
 *
 * @param damageFactor 伤害系数基于玩家基础攻击力的倍率
 * @param spinRadius   挥舞半径SPIN 阶段绕肩部画圆的半径
 * @param spinSpeed    挥舞角速度每 tick 增加的角度，弧度
 * @param throwSpeed   投掷速度THROWN 阶段的初始速度
 * @param maxDistance  最大抛出距离超过此距离自RETRACT
 * @param retractSpeed 收回速度
 * @param gravity      重力加速度
 * // @param cooldown     冷却时间 tick
 * @param bounceFactor 反弹能量衰减系数，范围 0.3~0.9
 * @param maxBounces   最大反弹次数，耗尽后落地
 * @param soundEvent   音效 ResourceLocation
 * @param projType      弹射物实体类ResourceLocation
 * @param ballTexture   弹球纹理 ResourceLocation
 * @param modelLocation 弹球 Geo 模型路径（可选，为空时使用默认 flail.geo.json）
 * @param hitEffect     击中特效（可选）
 */
public record FlailComponent(
        float damageFactor,
        float spinRadius,
        float spinSpeed,
        float throwSpeed,
        float maxDistance,
        float retractSpeed,
        float gravity,
        float bounceFactor,
        int maxBounces,
        ResourceLocation soundEvent,
        ResourceLocation projType,
        ResourceLocation ballTexture,
        Optional<ResourceLocation> modelLocation,
        Optional<EffectStrategyComponent> hitEffect
) implements DataComponentType<FlailComponent> {
    public static final Codec<FlailComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("damageFactor").forGetter(FlailComponent::damageFactor),
            Codec.FLOAT.fieldOf("spinRadius").forGetter(FlailComponent::spinRadius),
            Codec.FLOAT.fieldOf("spinSpeed").forGetter(FlailComponent::spinSpeed),
            Codec.FLOAT.fieldOf("throwSpeed").forGetter(FlailComponent::throwSpeed),
            Codec.FLOAT.fieldOf("maxDistance").forGetter(FlailComponent::maxDistance),
            Codec.FLOAT.fieldOf("retractSpeed").forGetter(FlailComponent::retractSpeed),
            Codec.FLOAT.fieldOf("gravity").forGetter(FlailComponent::gravity),
            Codec.FLOAT.optionalFieldOf("bounceFactor", 0.3F).forGetter(FlailComponent::bounceFactor),
            Codec.INT.optionalFieldOf("maxBounces", 3).forGetter(FlailComponent::maxBounces),
            ResourceLocation.CODEC.fieldOf("soundEvent").forGetter(FlailComponent::soundEvent),
            ResourceLocation.CODEC.fieldOf("projType").forGetter(FlailComponent::projType),
            ResourceLocation.CODEC.fieldOf("ballTexture").forGetter(FlailComponent::ballTexture),
            ResourceLocation.CODEC.optionalFieldOf("modelLocation").forGetter(FlailComponent::modelLocation),
            EffectStrategyComponent.CODEC.optionalFieldOf("hitEffect").forGetter(FlailComponent::hitEffect)
    ).apply(instance, FlailComponent::new));

    /** 致伤球 Ball O' Hurt 预制数据 */
    public static final Supplier<FlailComponent> BALL_O_HURT =
            () -> new FlailComponent(
                    17.0f,
                    1.2f,
                    1.5f,
                    1.3f,
                    11.0f,
                    1.0f,
                    0.2f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/ball_o_hurt.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/ball_o_hurt.geo.json")),
                    Optional.empty()
            );

    /** 链球 MACE 预制参数（测试用）*/
    public static final Supplier<FlailComponent> MACE_TEST =
            () -> new FlailComponent(
                    11.0f,
                    1.2f,
                    0.1f,
                    0.3f,
                    8.0f,
                    0.3f,
                    0.05f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/mace.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/mace.geo.json")),
                    Optional.empty()
            );

    /** 链锤*/
    public static final Supplier<FlailComponent> MACE =
            () -> new FlailComponent(
                    11.0f,
                    1.2f,
                    1.2f,
                    1.2f,
                    8.0f,
                    1.0f,
                    0.05f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/mace.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/mace.geo.json")),
                    Optional.empty()
            );

    // 火焰链锤 — 1/6 几率着火
    public static final Supplier<FlailComponent> FLAMING_MACE =
            () -> new FlailComponent(
                    11.0f,
                    1.2f,
                    1.2f,
                    1.2f,
                    8.0f,
                    1.0f,
                    0.05f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/flaming_mace.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/flaming_mace.geo.json")),
                    Optional.of(new EffectStrategyComponent(List.of(
                            new PrefabEffect("flaming_mace_fire",
                                    ModEffectStrategies.FIRE_3S_1_6)
                    )))
            );

    // 风锚
    public static final Supplier<FlailComponent> WIND_ANCHOR =
            () -> new FlailComponent(
                    13.0f,
                    1.2f,
                    0.9f,
                    1.0f,
                    10.0f,
                    0.9f,
                    0.05f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/wind_anchor.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/wind_anchor.geo.json")),
                    Optional.empty()
            );
    // todo 掷出击中敌人会产生只持续3秒的HURTNADO_PROJECTILE

    // 守卫链球
    public static final Supplier<FlailComponent> GUARDIAN_FLAIL =
            () -> new FlailComponent(
                    15.0f,
                    1.3f,
                    1.3f,
                    1.3f,
                    11.0f,
                    1.2f,
                    0.04f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/guardian_flail.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/guardian_flail.geo.json")),
                    Optional.empty()
            );
    // todo 链球停留在地面时会对周围的一个敌人发射守卫者激光

    // 守卫链球
    public static final Supplier<FlailComponent> ANCIENT_GUARDIAN_FLAIL =
            () -> new FlailComponent(
                    15.0f,
                    1.3f,
                    1.3f,
                    1.3f,
                    14.0f,
                    1.2f,
                    0.04f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/ancient_guardian_flail.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/ancient_guardian_flail.geo.json")),
                    Optional.empty()
            );
    // todo 链球停留在地面时会对周围的三个敌人发射守卫者激光


    // 血肉之球
    public static final Supplier<FlailComponent> THE_MEATBALL =
            () -> new FlailComponent(
                    19.0f,
                    1.2f,
                    1.5f,
                    1.3f,
                    13.0f,
                    1.0f,
                    0.2f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/the_meatball.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/the_meatball.geo.json")),
                    Optional.empty()
            );
    // 蓝月
    public static final Supplier<FlailComponent> BLUE_MOON =
            () -> new FlailComponent(
                    29.0f,
                    1.2f,
                    1.5f,
                    1.3f,
                    20.0f,
                    1.0f,
                    0.2f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/blue_moon.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/blue_moon.geo.json")),
                    Optional.empty()
            );

    // 阳炎之怒 — 1/4 几率着火
    public static final Supplier<FlailComponent> SUNFURY =
            () -> new FlailComponent(
                    34.0f,
                    1.2f,
                    1.5f,
                    1.3f,
                    23.0f,
                    1.0f,
                    0.2f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/sunfury.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/sunfury.geo.json")),
                    Optional.of(new EffectStrategyComponent(List.of(
                            new PrefabEffect("sunfury_fire",
                                    ModEffectStrategies.FIRE_3S_1_4)
                    )))
            );

    // 太极连枷 — 4/5 几率困惑
    public static final Supplier<FlailComponent> DAO_OF_POW =
            () -> new FlailComponent(
                    52.0f,
                    1.2f,
                    1.5f,
                    1.3f,
                    26.0f,
                    1.0f,
                    0.2f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/dao_of_pow.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/dao_of_pow.geo.json")),
                    Optional.of(new EffectStrategyComponent(List.of(
                            TimePossibilityAmplifierEffect.of("dao_confused",
                                    org.confluence.terra_curio.common.init.TCEffects.CONFUSED.getDelegate(),
                                    40, 0, 0, 0.8f)
                    )))
            );

    //花之力
    public static final Supplier<FlailComponent> FLOWER_POWER =
            () -> new FlailComponent(
                    67.0f,
                    1.2f,
                    1.5f,
                    1.3f,
                    26.0f,
                    1.0f,
                    0.2f,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/flail/flower_power.png"),
                    Optional.of(Confluence.asResource("geo/entity/flail/flower_power.geo.json")),
                    Optional.empty()
            );

    public SoundEvent getSoundEvent() {
        return BuiltInRegistries.SOUND_EVENT.get(soundEvent);
    }

    public static final StreamCodec<ByteBuf, FlailComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    @Override
    public @Nullable Codec<FlailComponent> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, FlailComponent> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof FlailComponent other) {
            return damageFactor == other.damageFactor &&
                    spinRadius == other.spinRadius &&
                    spinSpeed == other.spinSpeed &&
                    throwSpeed == other.throwSpeed &&
                    maxDistance == other.maxDistance &&
                    retractSpeed == other.retractSpeed &&
                    gravity == other.gravity &&
                    bounceFactor == other.bounceFactor &&
                    maxBounces == other.maxBounces &&
                    soundEvent.equals(other.soundEvent) &&
                    projType.equals(other.projType) &&
                    ballTexture.equals(other.ballTexture) &&
                    hitEffect.equals(other.hitEffect);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(damageFactor);
        result = 31 * result + Float.hashCode(spinRadius);
        result = 31 * result + Float.hashCode(spinSpeed);
        result = 31 * result + Float.hashCode(throwSpeed);
        result = 31 * result + Float.hashCode(maxDistance);
        result = 31 * result + Float.hashCode(retractSpeed);
        result = 31 * result + Float.hashCode(gravity);
        result = 31 * result + Float.hashCode(bounceFactor);
        result = 31 * result + maxBounces;
        result = 31 * result + soundEvent.hashCode();
        result = 31 * result + projType.hashCode();
        result = 31 * result + ballTexture.hashCode();
        result = 31 * result + hitEffect.hashCode();
        return result;
    }

    /** 获取修正后的投掷速度（受远程速度属性影响） */
    public float getVelocity(LivingEntity living) {
        float velocity = throwSpeed;
        AttributeInstance instance = living.getAttribute(LibAttributes.getRangedVelocity());
        if (instance != null) return velocity * (float) instance.getValue();
        return velocity;
    }

    /** 获取修正后的挥舞速度（受近战速度属性影响） */
    public float getSpinSpeed(LivingEntity living) {
        AttributeInstance instance = living.getAttribute(Attributes.ATTACK_SPEED);
        if (instance != null) return spinSpeed * (float) instance.getValue() / 4.0f;
        return spinSpeed;
    }
}
