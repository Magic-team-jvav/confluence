package org.confluence.mod.common.item.accessory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Tuple;
import org.confluence.terra_curio.api.primitive.CombineRule;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;

public record PickupRangeAbilityValue(Tuple<Float, Integer> value) implements PrimitiveValue<Tuple<Float, Integer>> {
    public static final Codec<PickupRangeAbilityValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("range").forGetter(v -> v.value.getA()),
            ExtraCodecs.POSITIVE_INT.fieldOf("combine_id").forGetter(v -> v.value.getB())
    ).apply(instance, (speed, ticks) -> new PickupRangeAbilityValue(new Tuple<>(speed, ticks))));
    public static final CombineRule<Tuple<Float, Integer>, PickupRangeAbilityValue> COMBINE_RULE = CombineRule.register((a, b) -> {
        if (a.getB().equals(b.getB())) return a; // a与b 的 combine_id 相同时，不合并
        return new Tuple<>(a.getA() + b.getA(), a.getB());
    }, "pickup_range_ability");

    @Override
    public Tuple<Float, Integer> get() {
        return value;
    }

    @Override
    public Codec<PickupRangeAbilityValue> codec() {
        return CODEC;
    }
}
