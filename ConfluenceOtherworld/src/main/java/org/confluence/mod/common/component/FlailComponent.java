package org.confluence.mod.common.component;

import PortLib.extensions.net.minecraft.resources.ResourceLocation.PortResourceLocationExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.function.Supplier;

/// 链锤物品与实体共同使用的同步参数。
///
/// <p>球体纹理和链条纹理必须分别保存：球体纹理贴在实体模型上，链条纹理只用于连接玩家手部
/// 与球体的分段四边形。将两者混在同一字段会让球体引用不存在的链条文件，也无法为不同链锤
/// 正确选择链条外观。</p>
///
/// @param damageFactor 伤害系数，基于玩家对应攻击属性计算
/// @param spinRadius   旋转阶段绕玩家手部运动的半径
/// @param spinSpeed    每 tick 增加的旋转弧度
/// @param throwSpeed   投出时的初始速度
/// @param maxDistance  自动进入收回阶段的最大距离
/// @param retractSpeed 收回速度
/// @param gravity      停留阶段的重力加速度
/// @param cooldown     投出后的物品冷却 tick
/// @param bounceFactor 碰撞后的速度保留比例
/// @param maxBounces   最大反弹次数
/// @param soundEvent   使用音效
/// @param projType     链锤实体类型
/// @param ballTexture  球体模型纹理
/// @param chainTexture 链条分段纹理
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
        ResourceLocation ballTexture,
        ResourceLocation chainTexture
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
            ResourceLocation.CODEC.fieldOf("ballTexture").forGetter(FlailComponent::ballTexture),
            ResourceLocation.CODEC.fieldOf("chainTexture").forGetter(FlailComponent::chainTexture)
    ).apply(instance, FlailComponent::new));
    public static final PortStreamCodec<ByteBuf, FlailComponent> STREAM_CODEC = LibStreamCodecUtils.composite(
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
            PortResourceLocationExtension.streamCodec(), FlailComponent::ballTexture,
            PortResourceLocationExtension.streamCodec(), FlailComponent::chainTexture,
            FlailComponent::new
    );

    /// 致伤球 Ball O' Hurt 预制数据
    public static final Supplier<FlailComponent> MACE = preset(
            "mace", 11.0F, 1.2F, 1.2F, 1.2F, 8.0F, 1.0F, 0.05F, false);

    /// 火焰链锤；点燃效果由物品子类处理。
    public static final Supplier<FlailComponent> FLAMING_MACE = preset(
            "flaming_mace", 11.0F, 1.2F, 1.2F, 1.2F, 8.0F, 1.0F, 0.05F, false);

    /// 风锚。
    public static final Supplier<FlailComponent> WIND_ANCHOR = preset(
            "wind_anchor", 13.0F, 1.2F, 0.9F, 1.0F, 10.0F, 0.9F, 0.05F, true);

    /// 守卫者链锤；光束行为由专用实体持有。
    public static final Supplier<FlailComponent> GUARDIAN_FLAIL = preset(
            "guardian_flail", 15.0F, 1.3F, 1.3F, 1.3F, 11.0F, 1.2F, 0.04F,
            true, ModEntities.GUARDIAN_FLAIL_ENTITY.getId());

    /// 远古守卫者链锤；专用实体最多同时维护三条光束。
    public static final Supplier<FlailComponent> ANCIENT_GUARDIAN_FLAIL = preset(
            "ancient_guardian_flail", 15.0F, 1.3F, 1.3F, 1.3F, 14.0F, 1.2F,
            0.04F, true, ModEntities.ANCIENT_GUARDIAN_FLAIL_ENTITY.getId());

    /// 致伤球。
    public static final Supplier<FlailComponent> BALL_O_HURT = preset(
            "ball_o_hurt", 17.0F, 1.2F, 1.5F, 1.3F, 11.0F, 1.0F, 0.2F, true);

    /// 血肉之球。
    public static final Supplier<FlailComponent> THE_MEATBALL = preset(
            "the_meatball", 19.0F, 1.2F, 1.5F, 1.3F, 13.0F, 1.0F, 0.2F, true);

    /// 蓝月。
    public static final Supplier<FlailComponent> BLUE_MOON = preset(
            "blue_moon", 29.0F, 1.2F, 1.5F, 1.3F, 20.0F, 1.0F, 0.2F, true);

    /// 阳炎之怒；点燃效果由物品子类处理。
    public static final Supplier<FlailComponent> SUNFURY = preset(
            "sunfury", 34.0F, 1.2F, 1.5F, 1.3F, 23.0F, 1.0F, 0.2F, true);

    /// 太极连枷；困惑效果由物品子类处理。
    public static final Supplier<FlailComponent> DAO_OF_POW = preset(
            "dao_of_pow", 52.0F, 1.2F, 1.5F, 1.3F, 26.0F, 1.0F, 0.2F, true);

    /// 花之力；花瓣发射周期由专用实体负责。
    public static final Supplier<FlailComponent> FLOWER_POWER = preset(
            "flower_power", 67.0F, 1.2F, 1.5F, 1.3F, 26.0F, 1.0F, 0.2F,
            true, ModEntities.FLOWER_POWER_FLAIL.getId());

    /// 滴滴怪致残者；收回时的血肉弹由专用实体负责。
    public static final Supplier<FlailComponent> DRIPPLER_CRIPPLER = preset(
            "drippler_crippler", 55.0F, 1.2F, 1.5F, 1.3F, 20.0F, 1.0F,
            0.2F, true, ModEntities.DRIPPLER_CRIPPLER_FLAIL.getId());

    /// 猪鲨链球；气泡发射由专用实体负责。
    public static final Supplier<FlailComponent> FLAIRON = preset(
            "flairon", 67.0F, 1.2F, 1.8F, 1.8F, 25.0F, 1.5F, 0.2F,
            true, ModEntities.FLAIRON_FLAIL.getId());

    /// 链刃；实体创建后直接投出。
    public static final Supplier<FlailComponent> CHAIN_KNIFE = preset(
            "chain_knife", 6.0F, 1.2F, 1.2F, 1.3F, 10.0F, 1.0F, 0.0F,
            true, ModEntities.CHAIN_KNIFE_FLAIL.getId());

    /// 锚；实体创建后直接投出并受重力影响。
    public static final Supplier<FlailComponent> ANCHOR = preset(
            "anchor", 35.0F, 1.2F, 1.2F, 1.3F, 100.0F, 1.0F, 0.05F,
            true, ModEntities.ANCHOR_FLAIL.getId());

    private static Supplier<FlailComponent> preset(
            String id,
            float damageFactor,
            float spinRadius,
            float spinSpeed,
            float throwSpeed,
            float maxDistance,
            float retractSpeed,
            float gravity,
            boolean customChain
    ) {
        return preset(
                id,
                damageFactor,
                spinRadius,
                spinSpeed,
                throwSpeed,
                maxDistance,
                retractSpeed,
                gravity,
                customChain,
                ModEntities.FLAIL_ENTITY.getId());
    }

    private static Supplier<FlailComponent> preset(
            String id,
            float damageFactor,
            float spinRadius,
            float spinSpeed,
            float throwSpeed,
            float maxDistance,
            float retractSpeed,
            float gravity,
            boolean customChain,
            ResourceLocation entityType
    ) {
        ResourceLocation ballTexture =
                Confluence.asResource("textures/entity/flail/" + id + ".png");
        ResourceLocation chainTexture = customChain
                ? Confluence.asResource(
                "textures/block/chain/" + id + ".png")
                : ResourceLocation.withDefaultNamespace(
                "textures/block/chain.png");
        return () -> new FlailComponent(
                damageFactor,
                spinRadius,
                spinSpeed,
                throwSpeed,
                maxDistance,
                retractSpeed,
                gravity,
                20,
                0.3F,
                3,
                ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId(),
                entityType,
                ballTexture,
                chainTexture);
    }

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
                    ballTexture.equals(other.ballTexture) &&
                    chainTexture.equals(other.chainTexture);
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
        result = 31 * result + ballTexture.hashCode();
        result = 31 * result + chainTexture.hashCode();
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
