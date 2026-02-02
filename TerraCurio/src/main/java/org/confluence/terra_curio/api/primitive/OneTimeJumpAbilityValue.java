package org.confluence.terra_curio.api.primitive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Tuple;

import java.util.List;

public record OneTimeJumpAbilityValue(Tuple<Float, Integer> value) implements PrimitiveValue<Tuple<Float, Integer>> {
    public static final Codec<OneTimeJumpAbilityValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("jump_speed").forGetter(v -> v.value.getA()),
            ExtraCodecs.POSITIVE_INT.fieldOf("jump_ticks").forGetter(v -> v.value.getB())
    ).apply(instance, (speed, ticks) -> new OneTimeJumpAbilityValue(new Tuple<>(speed, ticks))));
    public static final CombineRule<Tuple<Float, Integer>, OneTimeJumpAbilityValue> COMBINE_RULE = CombineRule.register((a, b) -> new Tuple<>(Math.max(a.getA(), b.getA()), Math.max(a.getB(), b.getB())), "one_time_jump_ability");

    @Override
    public Tuple<Float, Integer> get() {
        return value;
    }

    @Override
    public Codec<OneTimeJumpAbilityValue> codec() {
        return CODEC;
    }

    @Override
    public List<String> getDescription() {
        return List.of(
                "{",
                "    jump_speed=" + value.getA(),
                "    jump_ticks=" + value.getB(),
                "}"
        );
    }
}
