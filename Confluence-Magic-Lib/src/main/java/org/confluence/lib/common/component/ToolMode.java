package org.confluence.lib.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ToolMode(int mode) implements DataComponentType<ToolMode> {
    public static final Codec<ToolMode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("mode").forGetter(ToolMode::mode)
    ).apply(instance, ToolMode::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolMode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ToolMode::mode,
            ToolMode::new
    );

    @Override
    public Codec<ToolMode> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ToolMode> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ToolMode(int mode1) && mode == mode1);
    }

    @Override
    public int hashCode() {
        return mode;
    }
}
