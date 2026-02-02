package org.confluence.terraentity.data.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;


public record SingleBooleanComponent(boolean value) implements DataComponentType<SingleBooleanComponent> {

    public static final SingleBooleanComponent TRUE = new SingleBooleanComponent(true);
    public static final SingleBooleanComponent FALSE = new SingleBooleanComponent(false);

    public static final Codec<SingleBooleanComponent> CODEC = Codec.BOOL.xmap(SingleBooleanComponent::new, SingleBooleanComponent::value);

    public static final StreamCodec<ByteBuf, SingleBooleanComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SingleBooleanComponent::value,
            SingleBooleanComponent::new
    );

    @Override
    public @Nullable Codec<SingleBooleanComponent> codec() {return CODEC;}

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SingleBooleanComponent> streamCodec() {return STREAM_CODEC;}

    @Override
    public boolean equals(Object object) {
        return object instanceof SingleBooleanComponent(boolean value1) && value1 == value;
    }

}
