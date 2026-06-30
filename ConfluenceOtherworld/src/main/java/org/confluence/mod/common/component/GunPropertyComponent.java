package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.mesdag.portlib.component.PortDataComponentType;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// @param cooldown  使用时间
/// @param damage    子弹伤害
/// @param velocity  射弹速度
/// @param knockback 击退
/// @param critical  暴击
/// @param penetrate 穿透
/// @param rarity    稀有度
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
            Codec.FLOAT.fieldOf("critical").forGetter(GunPropertyComponent::critical),
            Codec.FLOAT.fieldOf("velocity").forGetter(GunPropertyComponent::velocity),
            Codec.FLOAT.fieldOf("knockback").forGetter(GunPropertyComponent::knockback),
            Codec.INT.fieldOf("penetrate").forGetter(GunPropertyComponent::penetrate),
            ModRarity.CODEC.fieldOf("rarity").forGetter(GunPropertyComponent::rarity)
    ).apply(ins, GunPropertyComponent::new));
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, GunPropertyComponent> STREAM_CODEC = LibStreamCodecUtils.composite(
            PortByteBufCodecs.VAR_INT, GunPropertyComponent::cooldown,
            PortByteBufCodecs.FLOAT, GunPropertyComponent::damage,
            PortByteBufCodecs.FLOAT, GunPropertyComponent::critical,
            PortByteBufCodecs.FLOAT, GunPropertyComponent::velocity,
            PortByteBufCodecs.FLOAT, GunPropertyComponent::knockback,
            PortByteBufCodecs.VAR_INT, GunPropertyComponent::penetrate,
            ModRarity.STREAM_CODEC, GunPropertyComponent::rarity,
            GunPropertyComponent::new
    );

    public static void fastBuilder(PortDataComponentType.Builder<GunPropertyComponent> builder) {
        builder.persistent(CODEC).networkSynchronized(STREAM_CODEC);
    }
}
