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
 * @param damage 子弹伤害
 * @param velocity 射弹速度
 * @param velocityMultiplier 总射弹速度乘数
 * @param knockback 击退
 * @param penetrate 穿透
 * @param rarity 稀有度
 * @param infinity 无限使用
 */
public record BulletPropertyComponent(float damage, float velocity, float velocityMultiplier, float knockback, int penetrate, ModRarity rarity, boolean infinity) implements DataComponentType<BulletPropertyComponent>{
    public static final Codec<BulletPropertyComponent> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.FLOAT.fieldOf("damage").forGetter(BulletPropertyComponent::damage),
            Codec.FLOAT.fieldOf("velocity").forGetter(BulletPropertyComponent::velocity),
            Codec.FLOAT.fieldOf("velocityMultiplier").forGetter(BulletPropertyComponent::velocityMultiplier),
            Codec.FLOAT.fieldOf("knockback").forGetter(BulletPropertyComponent::knockback),
            Codec.INT.fieldOf("penetrate").forGetter(BulletPropertyComponent::penetrate),
            ModRarity.CODEC.fieldOf("rarity").forGetter(BulletPropertyComponent::rarity),
            Codec.BOOL.fieldOf("infinity").forGetter(BulletPropertyComponent::infinity)
    ).apply(ins, BulletPropertyComponent::new));

    public static final StreamCodec<FriendlyByteBuf, BulletPropertyComponent> STREAM_CODEC = new SimpleStreamCodec<>(CODEC);
    public static final BulletPropertyComponent EMPTY = new BulletPropertyComponent(0, 0, 1, 0, 0, ModRarity.WHITE, false);

    @Override
    public @Nullable Codec<BulletPropertyComponent> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super FriendlyByteBuf, BulletPropertyComponent> streamCodec() {
        return STREAM_CODEC;
    }

    public static DataComponentType.Builder<BulletPropertyComponent> fastBuilder(DataComponentType.Builder<BulletPropertyComponent> builder){
        return builder.persistent(CODEC).networkSynchronized(STREAM_CODEC);
    }
}
