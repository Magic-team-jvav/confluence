package org.confluence.mod.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AchievementOffset(float x, float y, boolean hideLink) {
    public static final Codec<AchievementOffset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(AchievementOffset::x),
            Codec.FLOAT.fieldOf("y").forGetter(AchievementOffset::y),
            Codec.BOOL.fieldOf("hide_link").forGetter(AchievementOffset::hideLink)
    ).apply(instance, AchievementOffset::new));

    public static final StreamCodec<ByteBuf, AchievementOffset> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, AchievementOffset::x,
            ByteBufCodecs.FLOAT, AchievementOffset::y,
            ByteBufCodecs.BOOL, AchievementOffset::hideLink,
            AchievementOffset::new
    );

    public AchievementOffset(float x, float y) {
        this(x, y, true);
    }
}
