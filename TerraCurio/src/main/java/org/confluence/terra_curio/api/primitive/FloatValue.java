package org.confluence.terra_curio.api.primitive;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;

public final class FloatValue implements PrimitiveValue<Float> {
    public static final Codec<FloatValue> CODEC = Codec.FLOAT.xmap(FloatValue::new, FloatValue::get);
    public static final CombineRule<Float, FloatValue> GET_SELF = CombineRule.register(PrimitiveValue.identity(), "float_get_self");
    public static final CombineRule<Float, FloatValue> GET_MAX = CombineRule.register(Math::max, "float_get_max");
    public static final CombineRule<Float, FloatValue> GET_MAX_WITHIN_0_TO_1 = CombineRule.register((a, b) -> Mth.clamp(Math.max(a, b), 0.0F, 1.0F), "float_get_max_within_0_to_1");
    public static final CombineRule<Float, FloatValue> GET_MAX_WITHIN_0_TO_100 = CombineRule.register((a, b) -> Mth.clamp(Math.max(a, b), 0.0F, 100.0F), "float_get_max_within_0_to_100");
    public static final CombineRule<Float, FloatValue> ADDITION = CombineRule.register(Float::sum, "float_addition");
    public static final CombineRule<Float, FloatValue> ADDITION_WITHIN_0_TO_1 = CombineRule.register((a, b) -> Mth.clamp(a + b, 0.0F, 1.0F), "float_addition_within_0_to_1");
    private final float value;

    public FloatValue(float value) {
        this.value = value;
    }

    @Override
    public Float get() {
        return value;
    }

    @Override
    public Codec<FloatValue> codec() {
        return CODEC;
    }
}
