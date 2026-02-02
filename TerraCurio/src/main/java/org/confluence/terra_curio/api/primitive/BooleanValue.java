package org.confluence.terra_curio.api.primitive;

import com.mojang.serialization.Codec;

public record BooleanValue(boolean bool) implements PrimitiveValue<Boolean> {
    public static final Codec<BooleanValue> CODEC = Codec.BOOL.xmap(BooleanValue::new, BooleanValue::get);
    public static final CombineRule<Boolean, BooleanValue> OR = CombineRule.register((a, b) -> a || b, "boolean_or");

    @Override
    public Boolean get() {
        return bool;
    }

    @Override
    public Codec<BooleanValue> codec() {
        return CODEC;
    }
}
