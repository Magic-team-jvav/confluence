package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SoulArtifactCooldown(int remainingTicks, int totalTicks) {
    public static final Codec<SoulArtifactCooldown> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("remainingTicks").forGetter(SoulArtifactCooldown::remainingTicks),
                    Codec.INT.fieldOf("totalTicks").forGetter(SoulArtifactCooldown::totalTicks))
            .apply(instance, SoulArtifactCooldown::new));
    public static final StreamCodec<ByteBuf, SoulArtifactCooldown> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SoulArtifactCooldown::remainingTicks,
            ByteBufCodecs.INT, SoulArtifactCooldown::totalTicks,
            SoulArtifactCooldown::new);
}
