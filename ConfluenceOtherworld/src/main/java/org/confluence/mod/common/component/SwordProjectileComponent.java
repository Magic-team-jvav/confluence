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
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.api.ITrackType;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.util.generation.variant.AboveFallenGeneration;
import org.confluence.mod.util.generation.variant.ForwardGeneration;
import org.confluence.mod.util.generation.variant.StillGeneration;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 剑气武器的轻量、可序列化动作配置。
 *
 * <p>该组件只声明数值、实体类型、运动参数、追踪算法和生成布局，不在组件层执行资源消耗、
 * 属性解析、世界生成、冷却写入或音效播放。真正的发射事务由 {@code BaseSwordItem} 交给
 * MagicLib 统一处理，因此具体剑只需要提供一份配置即可。</p>
 *
 * @param damageFactor  发射瞬间近战攻击伤害的倍率
 * @param baseKnockback 进入 MagicLib 击退结算前的基础击退
 * @param baseSpeed     基础飞行速度；近战剑气不会读取远程弹速
 * @param acceleration  每 tick 作用于下一 tick 速度的倍率
 * @param existTicks    最大存在时间
 * @param gravity       每 tick 重力
 * @param cooldown      基础物品冷却 tick
 * @param soundEvent    成功生成整批弹幕后播放的音效键
 * @param projType      必须创建 {@code SwordProjectile} 的实体类型键
 * @param trackType     可选追踪算法
 * @param generation    不得直接写入世界的纯生成布局
 */
public record SwordProjectileComponent(
        float damageFactor,
        float baseKnockback,
        float baseSpeed,
        float acceleration,
        int existTicks,
        float gravity,
        int cooldown,
        ResourceLocation soundEvent,
        ResourceLocation projType,
        Optional<ITrackType> trackType,
        IGeneration generation
) {
    /** 1.20.1 只接受本轮新字段，不提供旧组件字段迁移。 */
    public static final Codec<SwordProjectileComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("damage_factor").forGetter(SwordProjectileComponent::damageFactor),
            Codec.FLOAT.fieldOf("base_knockback").forGetter(SwordProjectileComponent::baseKnockback),
            Codec.FLOAT.fieldOf("base_speed").forGetter(SwordProjectileComponent::baseSpeed),
            Codec.FLOAT.fieldOf("acceleration").forGetter(SwordProjectileComponent::acceleration),
            Codec.INT.fieldOf("exist_ticks").forGetter(SwordProjectileComponent::existTicks),
            Codec.FLOAT.fieldOf("gravity").forGetter(SwordProjectileComponent::gravity),
            Codec.INT.fieldOf("cooldown").forGetter(SwordProjectileComponent::cooldown),
            ResourceLocation.CODEC.fieldOf("sound_event").forGetter(SwordProjectileComponent::soundEvent),
            ResourceLocation.CODEC.fieldOf("projectile_type").forGetter(SwordProjectileComponent::projType),
            ITrackType.TYPED_CODEC.optionalFieldOf("track_type").forGetter(SwordProjectileComponent::trackType),
            IGeneration.TYPED_CODEC.fieldOf("generation").forGetter(SwordProjectileComponent::generation)
    ).apply(instance, SwordProjectileComponent::new));

    public static final PortStreamCodec<ByteBuf, SwordProjectileComponent> STREAM_CODEC =
            PortByteBufCodecs.fromCodec(CODEC);

    public static final Supplier<SwordProjectileComponent> ICE_PROJ =
            () -> new SwordProjectileComponent(1.0F, 0.0F, 0.6F, 0.9F, 40, 0.0F, 15,
                    ModSoundEvents.FROZEN_ARROW.getId(), ModEntities.ICE_BLADE_SWORD.getId(),
                    Optional.empty(), ForwardGeneration.of(0.0F, 0.0F));

    public static final Supplier<SwordProjectileComponent> STAR_FURY_PROJ =
            () -> new SwordProjectileComponent(1.5F, 0.0F, 1.5F, 0.9F, 100, 0.0F, 15,
                    ModSoundEvents.STAR.getId(), ModEntities.STAR_FURY.getId(),
                    Optional.empty(), new AboveFallenGeneration(30.0F, 30.0F, 10.0F, 1.0F, 20.0F, 5.0F));

    public static final Supplier<SwordProjectileComponent> ENCHANTED_SWORD_PROJ =
            () -> new SwordProjectileComponent(1.0F, 0.0F, 0.8F, 0.9F, 40, 0.0F, 10,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.ENCHANTED_SWORD.getId(),
                    Optional.empty(), ForwardGeneration.of(0.0F, 0.0F));

    public static final Supplier<SwordProjectileComponent> GRASS_PROJ =
            () -> new SwordProjectileComponent(0.25F, 0.0F, 0.8F, 0.9F, 20, 0.0F, 10,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.GRASS.getId(),
                    Optional.empty(), ForwardGeneration.of(0.0F, 20.0F));

    public static final Supplier<SwordProjectileComponent> NIGHT_PROJ =
            () -> new SwordProjectileComponent(1.0F, 0.0F, 0.8F, 0.9F, 20, 0.0F, 10,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.NIGHTS_EDGE.getId(),
                    Optional.empty(), ForwardGeneration.of(0.0F, 20.0F));

    public static final Supplier<SwordProjectileComponent> LIGHTS_BANE_PROJ =
            () -> new SwordProjectileComponent(1.0F, 0.0F, 0.8F, 0.9F, 12, 0.0F, 20,
                    ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(), ModEntities.LIGHTS_BANE.getId(),
                    Optional.empty(), StillGeneration.of(Vec3.ZERO));

    public SwordProjectileComponent {
        damageFactor = requireNonNegative(damageFactor, "Sword projectile damage factor");
        baseKnockback = requireNonNegative(baseKnockback, "Sword projectile base knockback");
        baseSpeed = requirePositive(baseSpeed, "Sword projectile base speed");
        acceleration = requireNonNegative(acceleration, "Sword projectile acceleration");
        gravity = requireFinite(gravity, "Sword projectile gravity");
        if (existTicks < 1) {
            throw new IllegalArgumentException("Sword projectile lifetime must be positive");
        }
        if (cooldown < 0) {
            throw new IllegalArgumentException("Sword projectile cooldown must be non-negative");
        }
        soundEvent = Objects.requireNonNull(soundEvent, "Sword projectile sound id must not be null");
        projType = Objects.requireNonNull(projType, "Sword projectile entity type id must not be null");
        trackType = Objects.requireNonNull(trackType, "Sword projectile track type must not be null");
        trackType.ifPresent(value -> Objects.requireNonNull(value, "Sword projectile track value must not be null"));
        generation = Objects.requireNonNull(generation, "Sword projectile generation must not be null");
    }

    /** 严格解析成功表现音效；不存在的注册键属于开发者配置错误。 */
    public SoundEvent getSoundEvent() {
        return BuiltInRegistries.SOUND_EVENT.getOptional(soundEvent).orElseThrow(() ->
                new IllegalStateException("Unknown sword projectile sound id: " + soundEvent));
    }

    /** 按现有剑攻速规则计算本次服务端冷却，但不在组件中修改玩家状态。 */
    public int getAttackSpeed(LivingEntity living) {
        Objects.requireNonNull(living, "Sword projectile owner must not be null");
        AttributeInstance attackSpeed = living.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed == null) {
            return cooldown;
        }
        return Math.max(cooldown - (int) (attackSpeed.getValue() / 3.0), 0);
    }

    private static float requireFinite(float value, String fieldName) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException(fieldName + " must be finite");
        }
        return value;
    }

    private static float requireNonNegative(float value, String fieldName) {
        if (requireFinite(value, fieldName) < 0.0F) {
            throw new IllegalArgumentException(fieldName + " must be non-negative");
        }
        return value;
    }

    private static float requirePositive(float value, String fieldName) {
        if (requireFinite(value, fieldName) <= 0.0F) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
        return value;
    }
}
