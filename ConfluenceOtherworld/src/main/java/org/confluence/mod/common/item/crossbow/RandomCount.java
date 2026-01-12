package org.confluence.mod.common.item.crossbow;

import net.minecraft.util.RandomSource;

public interface RandomCount {
    RandomCount DEFAULT = create(1);
    RandomCount DEFAULT_EMPTY = create(0);

    float getCount(RandomSource randomSource);

    /**
     * 创建一个范围随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 范围随机数
     */
    static RandomCount create(float min, float max) {
        return new RangeRandom(min, max);
    }

    /**
     * 创建一个数组随机数
     *
     * @param number 数组
     * @return 数组随机数
     */
    static RandomCount create(float[] number) {
        return new ArrayRandom(number);
    }

    /**
     * 创建一个不随机数
     *
     * @param number 随机数
     * @return 不随机数
     */
    static RandomCount create(float number) {
        return new NonRandom(number);
    }

    /**
     * 范围随机
     */
    class RangeRandom implements RandomCount {
        private final float min;
        private final float max;

        public RangeRandom(float min, float max) {
            assert min < max : "min must less than max";
            this.min = min;
            this.max = max;
        }

        @Override
        public float getCount(RandomSource randomSource) {
            return randomSource.nextFloat() * (max - min) + min;
        }
    }

    /**
     * 数组随机
     */
    class ArrayRandom implements RandomCount {
        private final float[] numbers;

        public ArrayRandom(float... numbers) {
            this.numbers = numbers;
        }

        @Override
        public float getCount(RandomSource randomSource) {
            return numbers[randomSource.nextInt(numbers.length)];
        }
    }

    /**
     * 不随机
     */
    final class NonRandom implements RandomCount {
        private final float count;

        public NonRandom(float count) {
            this.count = count;
        }

        @Override
        public float getCount(RandomSource randomSource) {
            return count;
        }
    }
}

