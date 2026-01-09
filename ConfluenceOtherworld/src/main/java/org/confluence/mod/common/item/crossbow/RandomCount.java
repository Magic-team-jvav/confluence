package org.confluence.mod.common.item.crossbow;

import net.minecraft.util.RandomSource;

public interface RandomCount {
    RandomCount DEFAULT = createNonRandom(1);

    int getCount(RandomSource randomSource);

    /**
     * 创建一个范围随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 范围随机数
     */
    static RandomCount createRangeRandom(int min, int max) {
        return new RangeRandom(min, max);
    }

    /**
     * 创建一个数组随机数
     *
     * @param number 数组
     * @return 数组随机数
     */
    static RandomCount createArrayRandom(int... number) {
        return new ArrayRandom(number);
    }

    /**
     * 创建一个不随机数
     *
     * @param number 随机数
     * @return 不随机数
     */
    static RandomCount createNonRandom(int number) {
        return new NonRandom(number);
    }

    /**
     * 范围随机
     */
    class RangeRandom implements RandomCount {
        private final int min;
        private final int max;

        public RangeRandom(int min, int max) {
            assert min < max : "min must less than max";
            this.min = min;
            this.max = max;
        }

        @Override
        public int getCount(RandomSource randomSource) {
            return randomSource.nextIntBetweenInclusive(min, max);
        }
    }

    /**
     * 数组随机
     */
    class ArrayRandom implements RandomCount {
        private final int[] numbers;

        public ArrayRandom(int... numbers) {
            this.numbers = numbers;
        }

        @Override
        public int getCount(RandomSource randomSource) {
            return numbers[randomSource.nextInt(numbers.length)];
        }
    }

    /**
     * 不随机
     */
    final class NonRandom implements RandomCount {
        private final int count;

        public NonRandom(int count) {
            this.count = count;
        }

        @Override
        public int getCount(RandomSource randomSource) {
            return count;
        }
    }
}

