package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.mesdag.portlib.component.PortDataComponentType;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 子弹物品栈持久化的基础射击数值。
///
/// @param damage             子弹提供的基础伤害
/// @param velocity           子弹提供的基础弹速
/// @param velocityMultiplier 最终弹速乘数
/// @param knockback          子弹提供的基础击退
/// @param penetrate          子弹提供的穿透次数，-1 表示无限穿透
/// @param rarity             子弹稀有度
/// @param infinity           是否不消耗该弹药
public record BulletPropertyComponent(
        float damage,
        float velocity,
        float velocityMultiplier,
        float knockback,
        int penetrate,
        ModRarity rarity,
        boolean infinity
) {
    public static final Codec<BulletPropertyComponent> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.FLOAT.fieldOf("damage").forGetter(BulletPropertyComponent::damage),
            Codec.FLOAT.fieldOf("velocity").forGetter(BulletPropertyComponent::velocity),
            Codec.FLOAT.fieldOf("velocityMultiplier").forGetter(BulletPropertyComponent::velocityMultiplier),
            Codec.FLOAT.fieldOf("knockback").forGetter(BulletPropertyComponent::knockback),
            Codec.INT.fieldOf("penetrate").forGetter(BulletPropertyComponent::penetrate),
            ModRarity.CODEC.fieldOf("rarity").forGetter(BulletPropertyComponent::rarity),
            Codec.BOOL.fieldOf("infinity").forGetter(BulletPropertyComponent::infinity)
    ).apply(ins, BulletPropertyComponent::new));
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, BulletPropertyComponent> STREAM_CODEC = LibStreamCodecUtils.composite(
            PortByteBufCodecs.FLOAT, BulletPropertyComponent::damage,
            PortByteBufCodecs.FLOAT, BulletPropertyComponent::velocity,
            PortByteBufCodecs.FLOAT, BulletPropertyComponent::velocityMultiplier,
            PortByteBufCodecs.FLOAT, BulletPropertyComponent::knockback,
            PortByteBufCodecs.VAR_INT, BulletPropertyComponent::penetrate,
            ModRarity.STREAM_CODEC, BulletPropertyComponent::rarity,
            PortByteBufCodecs.BOOL, BulletPropertyComponent::infinity,
            BulletPropertyComponent::new
    );
    public static final BulletPropertyComponent EMPTY = new BulletPropertyComponent(0, 0, 1, 0, 0, ModRarity.WHITE, false);

    public static void fastBuilder(PortDataComponentType.Builder<BulletPropertyComponent> builder) {
        builder.persistent(CODEC).networkSynchronized(STREAM_CODEC);
    }
}
