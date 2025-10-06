package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModDataMaps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ValueComponent(int value) implements DataComponentType<ValueComponent> {
    public static final Codec<ValueComponent> CODEC = Codec.INT.xmap(ValueComponent::new, ValueComponent::value);
    public static final StreamCodec<ByteBuf, ValueComponent> STREAM_CODEC = ByteBufCodecs.INT.map(ValueComponent::new, ValueComponent::value);

    @Override
    public @Nullable Codec<ValueComponent> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ValueComponent> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ValueComponent(int value1) && value1 == value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    public static int getValue(ItemStack itemStack, int defaultValue, boolean prototype) {
        DataComponentType<ValueComponent> type = ModDataComponentTypes.VALUE.get();
        ValueComponent value = prototype ? itemStack.getPrototype().get(type) : itemStack.get(type);
        if (value == null) {
            value = itemStack.getItemHolder().getData(ModDataMaps.VALUE);
            return (value == null ? defaultValue : value.value()) * itemStack.getCount();
        }
        return value.value() * itemStack.getCount();
    }

    public static int getValue(ItemStack itemStack, int defaultValue) {
        return getValue(itemStack, defaultValue, false);
    }
}
