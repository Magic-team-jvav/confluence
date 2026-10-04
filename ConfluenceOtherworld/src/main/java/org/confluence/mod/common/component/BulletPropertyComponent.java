package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibStreamCodecUtils;

/// @param damage             子弹伤害
/// @param velocity           射弹速度
/// @param velocityMultiplier 总射弹速度乘数
/// @param knockback          击退
/// @param penetrate          穿透
/// @param rarity             稀有度
/// @param infinity           无限使用
public record BulletPropertyComponent(
        float damage,
        float velocity,
        float velocityMultiplier,
        float knockback,
        int penetrate,
        ModRarity rarity,
        boolean infinity) {
    public static final Codec<BulletPropertyComponent> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.FLOAT.fieldOf("damage").forGetter(BulletPropertyComponent::damage),
            Codec.FLOAT.fieldOf("velocity").forGetter(BulletPropertyComponent::velocity),
            Codec.FLOAT.fieldOf("velocityMultiplier").forGetter(BulletPropertyComponent::velocityMultiplier),
            Codec.FLOAT.fieldOf("knockback").forGetter(BulletPropertyComponent::knockback),
            Codec.INT.fieldOf("penetrate").forGetter(BulletPropertyComponent::penetrate),
            ModRarity.CODEC.fieldOf("rarity").forGetter(BulletPropertyComponent::rarity),
            Codec.BOOL.fieldOf("infinity").forGetter(BulletPropertyComponent::infinity)
    ).apply(ins, BulletPropertyComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BulletPropertyComponent> STREAM_CODEC = LibStreamCodecUtils.composite(
            ByteBufCodecs.FLOAT, BulletPropertyComponent::damage,
            ByteBufCodecs.FLOAT, BulletPropertyComponent::velocity,
            ByteBufCodecs.FLOAT, BulletPropertyComponent::velocityMultiplier,
            ByteBufCodecs.FLOAT, BulletPropertyComponent::knockback,
            ByteBufCodecs.VAR_INT, BulletPropertyComponent::penetrate,
            ModRarity.STREAM_CODEC, BulletPropertyComponent::rarity,
            ByteBufCodecs.BOOL, BulletPropertyComponent::infinity,
            BulletPropertyComponent::new
    );
    public static final BulletPropertyComponent EMPTY = new BulletPropertyComponent(0, 0, 1, 0, 0, ModRarity.WHITE, false);
}
