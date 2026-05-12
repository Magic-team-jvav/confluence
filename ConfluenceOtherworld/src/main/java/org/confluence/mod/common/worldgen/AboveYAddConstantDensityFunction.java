package org.confluence.mod.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.Objects;

public class AboveYAddConstantDensityFunction implements DensityFunction {
    public static final MapCodec<AboveYAddConstantDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(AboveYAddConstantDensityFunction::input),
            Codec.INT.fieldOf("y").forGetter(AboveYAddConstantDensityFunction::y),
            Codec.DOUBLE.fieldOf("constant").forGetter(AboveYAddConstantDensityFunction::constant)
    ).apply(instance, AboveYAddConstantDensityFunction::new));
    public static final KeyDispatchDataCodec<AboveYAddConstantDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
    private final DensityFunction input;
    private final int y;
    private final double constant;

    private long lastPos2D = ChunkPos.INVALID_CHUNK_POS;
    private boolean lastValue;

    public AboveYAddConstantDensityFunction(DensityFunction input, int y, double constant) {
        this.input = input;
        this.y = y;
        this.constant = constant;
    }

    @Override
    public double compute(FunctionContext context) {
        double v = input.compute(context);
        if (v > 0 || context.blockY() < y || cache(context.blockX(), context.blockZ())) {
            return v;
        }
        return v + constant;
    }

    private boolean cache(int x, int z) {
        long pos = ChunkPos.asLong(x, z);
        if (lastPos2D != pos) {
            this.lastPos2D = pos;
            this.lastValue = input.compute(new SinglePointContext(x, y - 1, z)) < 0;
        }
        return lastValue;
    }

    @Override
    public void fillArray(double[] array, ContextProvider contextProvider) {
        for (int i = 0; i < array.length; i++) {
            FunctionContext context = contextProvider.forIndex(i);
            double v = input.compute(context);
            if (v > 0 || context.blockY() < y || input.compute(new SinglePointContext(context.blockX(), y - 1, context.blockZ())) < 0) {
                array[i] = v;
            } else {
                array[i] = v + constant;
            }
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

    public DensityFunction input() {
        return input;
    }

    public int y() {
        return y;
    }

    public double constant() {
        return constant;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AboveYAddConstantDensityFunction) obj;
        return Objects.equals(this.input, that.input) &&
                this.y == that.y &&
                Double.doubleToLongBits(this.constant) == Double.doubleToLongBits(that.constant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, y, constant);
    }

    @Override
    public String toString() {
        return "AboveYAddConstantDensityFunction[" +
                "input=" + input + ", " +
                "y=" + y + ", " +
                "constant=" + constant + ']';
    }
}
