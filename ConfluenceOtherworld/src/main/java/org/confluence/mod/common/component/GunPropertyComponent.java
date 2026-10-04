package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibStreamCodecUtils;

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
        ModRarity rarity) {
    public static final Codec<GunPropertyComponent> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("cooldown").forGetter(GunPropertyComponent::cooldown),
            Codec.FLOAT.fieldOf("damage").forGetter(GunPropertyComponent::damage),
            Codec.FLOAT.fieldOf("velocity").forGetter(GunPropertyComponent::velocity),
            Codec.FLOAT.fieldOf("knockback").forGetter(GunPropertyComponent::knockback),
            Codec.FLOAT.fieldOf("critical").forGetter(GunPropertyComponent::critical),
            Codec.INT.fieldOf("penetrate").forGetter(GunPropertyComponent::penetrate),
            ModRarity.CODEC.fieldOf("rarity").forGetter(GunPropertyComponent::rarity)
    ).apply(ins, GunPropertyComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, GunPropertyComponent> STREAM_CODEC = LibStreamCodecUtils.composite(
            ByteBufCodecs.VAR_INT, GunPropertyComponent::cooldown,
            ByteBufCodecs.FLOAT, GunPropertyComponent::damage,
            ByteBufCodecs.FLOAT, GunPropertyComponent::velocity,
            ByteBufCodecs.FLOAT, GunPropertyComponent::knockback,
            ByteBufCodecs.FLOAT, GunPropertyComponent::critical,
            ByteBufCodecs.VAR_INT, GunPropertyComponent::penetrate,
            ModRarity.STREAM_CODEC, GunPropertyComponent::rarity,
            GunPropertyComponent::new
    );
}
