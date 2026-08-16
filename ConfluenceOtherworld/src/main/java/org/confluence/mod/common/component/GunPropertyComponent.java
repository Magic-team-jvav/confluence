package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.mesdag.portlib.component.PortDataComponentType;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 枪械物品栈持久化的基础射击数值。
///
/// @param cooldown  使用间隔，单位为 tick
/// @param damage    枪械提供的基础伤害
/// @param velocity  枪械提供的基础弹速
/// @param knockback 枪械提供的基础击退
/// @param critical  基础暴击率，使用 0 到 1 的小数表示
/// @param penetrate 枪械额外提供的穿透次数，-1 表示无限穿透
/// @param rarity    枪械稀有度
public record GunPropertyComponent(
        int cooldown,
        float damage,
        float velocity,
        float knockback,
        float critical,
        int penetrate,
        ModRarity rarity
) {
    public static final Codec<GunPropertyComponent> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("cooldown").forGetter(GunPropertyComponent::cooldown),
            Codec.FLOAT.fieldOf("damage").forGetter(GunPropertyComponent::damage),
            Codec.FLOAT.fieldOf("velocity").forGetter(GunPropertyComponent::velocity),
            Codec.FLOAT.fieldOf("knockback").forGetter(GunPropertyComponent::knockback),
            Codec.FLOAT.fieldOf("critical").forGetter(GunPropertyComponent::critical),
            Codec.INT.fieldOf("penetrate").forGetter(GunPropertyComponent::penetrate),
            ModRarity.CODEC.fieldOf("rarity").forGetter(GunPropertyComponent::rarity)
    ).apply(ins, GunPropertyComponent::new));
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, GunPropertyComponent> STREAM_CODEC = LibStreamCodecUtils.composite(
            PortByteBufCodecs.VAR_INT, GunPropertyComponent::cooldown,
            PortByteBufCodecs.FLOAT, GunPropertyComponent::damage,
            PortByteBufCodecs.FLOAT, GunPropertyComponent::velocity,
            PortByteBufCodecs.FLOAT, GunPropertyComponent::knockback,
            PortByteBufCodecs.FLOAT, GunPropertyComponent::critical,
            PortByteBufCodecs.VAR_INT, GunPropertyComponent::penetrate,
            ModRarity.STREAM_CODEC, GunPropertyComponent::rarity,
            GunPropertyComponent::new
    );

    public static void fastBuilder(PortDataComponentType.Builder<GunPropertyComponent> builder) {
        builder.persistent(CODEC).networkSynchronized(STREAM_CODEC);
    }
}
