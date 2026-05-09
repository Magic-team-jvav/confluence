package org.confluence.mod.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record AboveYAddConstantDensityFunction(
        DensityFunction input,
        int y,
        double constant
) implements DensityFunction {
    public static final MapCodec<AboveYAddConstantDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(AboveYAddConstantDensityFunction::input),
            Codec.INT.fieldOf("y").forGetter(AboveYAddConstantDensityFunction::y),
            Codec.DOUBLE.fieldOf("constant").forGetter(AboveYAddConstantDensityFunction::constant)
    ).apply(instance, AboveYAddConstantDensityFunction::new));
    public static final KeyDispatchDataCodec<AboveYAddConstantDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

    @Override
    public double compute(FunctionContext context) {
        double v = input.compute(context);
        if (v > 0 || input.compute(new SinglePointContext(context.blockX(), y - 1, context.blockZ())) < 0) {
            return v;
        }
        return v + constant;
    }

    @Override
    public void fillArray(double[] array, ContextProvider contextProvider) {
        for (int i = 0; i < array.length; i++) {
            FunctionContext context = contextProvider.forIndex(i);
            array[i] += compute(context);
        }
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(new AboveYAddConstantDensityFunction(input.mapAll(visitor), y, constant));
    }

    @Override
    public double minValue() {
        return input.minValue();
    }

    @Override
    public double maxValue() {
        return input.maxValue();
    }

    @Override
    public KeyDispatchDataCodec<AboveYAddConstantDensityFunction> codec() {
        return CODEC;
    }
}
