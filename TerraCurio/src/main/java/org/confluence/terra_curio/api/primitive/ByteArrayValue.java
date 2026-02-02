package org.confluence.terra_curio.api.primitive;

import com.mojang.serialization.Codec;

import java.util.Arrays;

public record ByteArrayValue(Byte[] value) implements PrimitiveValue<Byte[]> {
    public static final Codec<ByteArrayValue> CODEC = Codec.BYTE.listOf().xmap(
            bytes -> new ByteArrayValue(bytes.toArray(new Byte[0])),
            value -> Arrays.stream(value.value).toList()
    );
    public static final CombineRule<Byte[], ByteArrayValue> GET_SELF = CombineRule.register((a, b) -> a, "byte_array_get_self");

    @Override
    public Byte[] get() {
        return value;
    }

    @Override
    public Codec<ByteArrayValue> codec() {
        return CODEC;
    }
}
