package org.confluence.terra_guns.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_guns.impl.SimpleStreamCodec;
import org.jetbrains.annotations.Nullable;

/**
 * @param cooldown  使用时间
 * @param damage    子弹伤害
 * @param velocity  射弹速度
 * @param knockback 击退
 * @param critical  暴击
 * @param penetrate 穿透
 * @param rarity    稀有度
 */
public record GunPropertyComponent(int cooldown, float damage, float velocity, float knockback, float critical, int penetrate,
                                   ModRarity rarity) implements DataComponentType<GunPropertyComponent> {
    public static final Codec<GunPropertyComponent> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("cooldown").forGetter(GunPropertyComponent::cooldown),
            Codec.FLOAT.fieldOf("damage").forGetter(GunPropertyComponent::damage),
            Codec.FLOAT.fieldOf("critical").forGetter(GunPropertyComponent::critical),
            Codec.FLOAT.fieldOf("velocity").forGetter(GunPropertyComponent::velocity),
            Codec.FLOAT.fieldOf("knockback").forGetter(GunPropertyComponent::knockback),
            Codec.INT.fieldOf("penetrate").forGetter(GunPropertyComponent::penetrate),
            ModRarity.CODEC.fieldOf("rarity").forGetter(GunPropertyComponent::rarity)
    ).apply(ins, GunPropertyComponent::new));

    public static final StreamCodec<FriendlyByteBuf, GunPropertyComponent> STREAM_CODEC = new SimpleStreamCodec<>(CODEC);

    @Override
    public @Nullable Codec<GunPropertyComponent> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super FriendlyByteBuf, GunPropertyComponent> streamCodec() {
        return STREAM_CODEC;
    }

    public static Builder<GunPropertyComponent> fastBuilder(Builder<GunPropertyComponent> builder) {
        return builder.persistent(CODEC).networkSynchronized(STREAM_CODEC);
    }
}
