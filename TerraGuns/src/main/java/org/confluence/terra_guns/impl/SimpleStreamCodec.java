package org.confluence.terra_guns.impl;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record SimpleStreamCodec<B extends FriendlyByteBuf, C extends DataComponentType<?>>(Codec<C> codec) implements StreamCodec<B, C> {
    @Override
    public void encode(B buffer, @NotNull C value) {
        buffer.writeJsonWithCodec(codec, value);
    }

    @Override
    public @NotNull C decode(B buffer) {
        return buffer.readJsonWithCodec(codec);
    }
}
