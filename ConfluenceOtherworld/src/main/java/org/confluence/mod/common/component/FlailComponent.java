package org.confluence.mod.common.component;

import PortLib.extensions.net.minecraft.resources.ResourceLocation.PortResourceLocationExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Optional;
import java.util.function.Supplier;

/// # 连枷弹射物组
/// 存储连枷的所有数据驱动参数，通过 Codec 持久化到物品 DataComponent
///
/// @param damageFactor 伤害系数基于玩家基础攻击力的倍率
/// @param spinRadius   挥舞半径SPIN 阶段绕肩部画圆的半径
/// @param spinSpeed    挥舞角速度每 tick 增加的角度，弧度
/// @param throwSpeed   投掷速度THROWN 阶段的初始速度
/// @param maxDistance  最大抛出距离超过此距离自RETRACT
/// @param retractSpeed 收回速度
/// @param gravity      重力加速度
/// @param cooldown     冷却时间 tick
/// @param bounceFactor 反弹能量衰减系数，范围 0.3~0.9
/// @param maxBounces   最大反弹次数，耗尽后落地
/// @param soundEvent   音效 ResourceLocation
/// @param projType     弹射物实体类ResourceLocation
/// @param chainTexture 链条纹理 ResourceLocation
/// @param hitEffect    击中特效（可选）
public record FlailComponent(
        float damageFactor,
        float spinRadius,
        float spinSpeed,
        float throwSpeed,
        float maxDistance,
        float retractSpeed,
        float gravity,
        int cooldown,
        float bounceFactor,
        int maxBounces,
        ResourceLocation soundEvent,
        ResourceLocation projType,
        ResourceLocation chainTexture,
        Optional<EffectStrategyComponent> hitEffect
) {

    public static final Codec<FlailComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("damageFactor").forGetter(FlailComponent::damageFactor),
            Codec.FLOAT.fieldOf("spinRadius").forGetter(FlailComponent::spinRadius),
            Codec.FLOAT.fieldOf("spinSpeed").forGetter(FlailComponent::spinSpeed),
            Codec.FLOAT.fieldOf("throwSpeed").forGetter(FlailComponent::throwSpeed),
            Codec.FLOAT.fieldOf("maxDistance").forGetter(FlailComponent::maxDistance),
            Codec.FLOAT.fieldOf("retractSpeed").forGetter(FlailComponent::retractSpeed),
            Codec.FLOAT.fieldOf("gravity").forGetter(FlailComponent::gravity),
            Codec.INT.fieldOf("cooldown").forGetter(FlailComponent::cooldown),
            Codec.FLOAT.optionalFieldOf("bounceFactor", 0.3F).forGetter(FlailComponent::bounceFactor),
            Codec.INT.optionalFieldOf("maxBounces", 3).forGetter(FlailComponent::maxBounces),
            ResourceLocation.CODEC.fieldOf("soundEvent").forGetter(FlailComponent::soundEvent),
            ResourceLocation.CODEC.fieldOf("projType").forGetter(FlailComponent::projType),
            ResourceLocation.CODEC.fieldOf("chainTexture").forGetter(FlailComponent::chainTexture),
            EffectStrategyComponent.CODEC.optionalFieldOf("hitEffect").forGetter(FlailComponent::hitEffect)
    ).apply(instance, FlailComponent::new));
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, FlailComponent> STREAM_CODEC = LibStreamCodecUtils.composite(
            PortByteBufCodecs.FLOAT, FlailComponent::damageFactor,
            PortByteBufCodecs.FLOAT, FlailComponent::spinRadius,
            PortByteBufCodecs.FLOAT, FlailComponent::spinSpeed,
            PortByteBufCodecs.FLOAT, FlailComponent::throwSpeed,
            PortByteBufCodecs.FLOAT, FlailComponent::maxDistance,
            PortByteBufCodecs.FLOAT, FlailComponent::retractSpeed,
            PortByteBufCodecs.FLOAT, FlailComponent::gravity,
            PortByteBufCodecs.VAR_INT, FlailComponent::cooldown,
            PortByteBufCodecs.FLOAT, FlailComponent::bounceFactor,
            PortByteBufCodecs.VAR_INT, FlailComponent::maxBounces,
            PortResourceLocationExtension.streamCodec(), FlailComponent::soundEvent,
            PortResourceLocationExtension.streamCodec(), FlailComponent::projType,
            PortResourceLocationExtension.streamCodec(), FlailComponent::chainTexture,
            PortByteBufCodecs.optional(EffectStrategyComponent.STREAM_CODEC), FlailComponent::hitEffect,
            FlailComponent::new
    );

    /// 致伤球 Ball O' Hurt 预制数据
    public static final Supplier<FlailComponent> BALL_O_HURT =
            () -> new FlailComponent(
                    26.0f,
                    1.2f,
                    1.5f,
                    1.5f,
                    8.0f,
                    1.0f,
                    0.2f,
                    20,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/ball_o_hurt_chain.png"),
                    Optional.empty()
            );

    /// 链球 MACE 预制参数
    public static final Supplier<FlailComponent> MACE =
            () -> new FlailComponent(
                    18.0f,
                    1.2f,
                    0.12f,
                    0.12f,
                    6.0f,
                    0.18f,
                    0.05f,
                    20,
                    0.3f,
                    3,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                    ModEntities.FLAIL_ENTITY.getId(),
                    Confluence.asResource("textures/entity/mace_chain.png"),
                    Optional.empty()
            );

    public SoundEvent getSoundEvent() {
        return BuiltInRegistries.SOUND_EVENT.get(soundEvent);
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
                    cooldown == other.cooldown &&
                    bounceFactor == other.bounceFactor &&
                    maxBounces == other.maxBounces &&
                    soundEvent.equals(other.soundEvent) &&
                    projType.equals(other.projType) &&
                    chainTexture.equals(other.chainTexture) &&
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
        result = 31 * result + cooldown;
        result = 31 * result + Float.hashCode(bounceFactor);
        result = 31 * result + maxBounces;
        result = 31 * result + soundEvent.hashCode();
        result = 31 * result + projType.hashCode();
        result = 31 * result + chainTexture.hashCode();
        result = 31 * result + hitEffect.hashCode();
        return result;
    }

    /// 获取修正后的投掷速度（受远程速度属性影响）
    public float getVelocity(LivingEntity living) {
        float velocity = throwSpeed;
        AttributeInstance instance = living.getAttribute(LibAttributes.getRangedVelocity().value());
        if (instance != null) return velocity * (float) instance.getValue();
        return velocity;
    }

    /// 获取修正后的冷却时间（受攻击速度属性影响）
    public int getCooldown(LivingEntity living) {
        AttributeInstance instance = living.getAttribute(Attributes.ATTACK_SPEED);
        if (instance != null) return Math.max(cooldown - (int) (instance.getValue() / 3.0), 0);
        return cooldown;
    }

    /// 获取修正后的挥舞速度（受近战速度属性影响）
    public float getSpinSpeed(LivingEntity living) {
        AttributeInstance instance = living.getAttribute(Attributes.ATTACK_SPEED);
        if (instance != null) return spinSpeed * (float) instance.getValue() / 4.0f;
        return spinSpeed;
    }
}
