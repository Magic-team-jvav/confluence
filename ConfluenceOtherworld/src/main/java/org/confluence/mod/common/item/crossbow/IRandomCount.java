package org.confluence.mod.common.item.crossbow;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.IntStream;

public interface IRandomCount {
    IRandomCount DEFAULT = create(1);
    IRandomCount DEFAULT_EMPTY = create(0);

    float getCount(@NotNull RandomSource randomSource);

    float getMaxCount();

    float getMinCount();

    default boolean isEmpty() {
        return getMinCount() == 0 && getMaxCount() == 0;
    }

    default String getString() {
        return "";
    }

    static String getString(IRandomCount randomCount) {
        switch (randomCount) {
            case ArrayRandom random -> {
                StringBuilder sb = new StringBuilder();
                for (float number : random.getNumbers()) {
                    sb.append(number).append(", ");
                }
                return sb.toString();
            }
            case NonRandom nonRandom -> {
                return String.valueOf(nonRandom.getCount());
            }
            case RangeRandom random -> {
                return random.getMinCount() + "~" + random.getMaxCount();
            }
            case null -> {
                return "";
            }
            default -> {
                return randomCount.getString();
            }
        }
    }

    static boolean is(IRandomCount randomCount, float i) {
        switch (randomCount) {
            case ArrayRandom random -> {
                if (random.getNumbers().length > 1) {
                    return false;
                }
                return random.getNumbers()[0] == i;
            }
            case NonRandom nonRandom -> {
                return nonRandom.getCount() == i;
            }
            case RangeRandom random -> {
                return random.getMinCount() == i && random.getMaxCount() == i;
            }
            case null, default -> {
                return false;
            }
        }
    }

    /**
     * 创建一个范围随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 范围随机数
     */
    static IRandomCount create(float min, float max) {
        return new RangeRandom(min, max);
    }

    /**
     * 创建一个数组随机数
     *
     * @param number 数组
     * @return 数组随机数
     */
    static IRandomCount create(float[] number) {
        return new ArrayRandom(number);
    }

    /**
     * 创建一个不随机数
     *
     * @param number 随机数
     * @return 不随机数
     */
    static IRandomCount create(float number) {
        return new NonRandom(number);
    }

    abstract class RandomCount implements IRandomCount {


        @Override
        public String toString() {
            return "RandomCount{" + "min=" + getMinCount() + ", max=" + getMaxCount() + '}';
        }
    }

    /**
     * 范围随机
     */
    class RangeRandom extends RandomCount {
        private final float min;
        private final float max;

        public RangeRandom(float min, float max) {
            assert min < max : "min must less than max";
            this.min = min;
            this.max = max;
        }

        @Override
        public float getCount(@NotNull RandomSource randomSource) {
            return randomSource.nextFloat() * (max - min) + min;
        }

        @Override
        public float getMaxCount() {
            return max;
        }

        @Override
        public float getMinCount() {
            return min;
        }
    }

    /**
     * 数组随机
     */
    class ArrayRandom extends RandomCount {
        private final float[] numbers;

        public ArrayRandom(float... numbers) {
            this.numbers = numbers;
        }

        @Override
        public float getCount(@NotNull RandomSource randomSource) {
            return numbers[randomSource.nextInt(numbers.length)];
        }

        @Override
        public float getMaxCount() {
            return (float) Arrays.stream(getDoubleArray()).max().orElse(0);
        }

        @Override
        public float getMinCount() {
            return (float) Arrays.stream(getDoubleArray()).min().orElse(0);
        }

        private double[] getDoubleArray() {
            return IntStream.range(0, numbers.length).mapToDouble(i -> numbers[i]).toArray();
        }

        public float[] getNumbers() {
            return numbers;
        }

        @Override
        public String toString() {
            return "RandomCount{" + "numbers=" + Arrays.toString(numbers) + '}';
        }
    }

    /**
     * 不随机
     */
    final class NonRandom extends RandomCount {
        private final float count;

        public NonRandom(float count) {
            this.count = count;
        }

        @Override
        public float getCount(@NotNull RandomSource randomSource) {
            return count;
        }

        public float getCount() {
            return count;
        }

        @Override
        public float getMaxCount() {
            return count;
        }

        @Override
        public float getMinCount() {
            return count;
        }

        @Override
        public String toString() {
            return "RandomCount{" + "count=" + count + '}';
        }
    }
}

