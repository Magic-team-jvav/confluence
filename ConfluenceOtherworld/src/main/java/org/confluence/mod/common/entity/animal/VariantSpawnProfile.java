package org.confluence.mod.common.entity.animal;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 每种小动物独立声明的自然生成变体权重表。
 *
 * <p>这里只描述同一实体类型内部的外观概率，不承载实体类型、继承关系、属性、行为、
 * 导航、模型或纹理。选择结果只能由实体自己的生成初始化方法写入。</p>
 */
final class VariantSpawnProfile<T> {
    private final List<Entry<T>> entries;
    private final int totalWeight;

    private VariantSpawnProfile(List<Entry<T>> entries, int totalWeight) {
        this.entries = entries;
        this.totalWeight = totalWeight;
    }

    static <T> Builder<T> builder() {
        return new Builder<>();
    }

    T select(RandomSource random) {
        int cursor = random.nextInt(totalWeight);
        for (Entry<T> entry : entries) {
            cursor -= entry.weight;
            if (cursor < 0) return entry.variant;
        }
        return entries.get(entries.size() - 1).variant;
    }

    static final class Builder<T> {
        private final List<Entry<T>> entries = new ArrayList<>();
        private int totalWeight;

        Builder<T> add(T variant, int weight) {
            if (weight <= 0)
                throw new IllegalArgumentException("Variant spawn weight must be positive");
            entries.add(new Entry<>(variant, weight));
            totalWeight = Math.addExact(totalWeight, weight);
            return this;
        }

        VariantSpawnProfile<T> build() {
            if (entries.isEmpty())
                throw new IllegalStateException("Variant spawn profile cannot be empty");
            return new VariantSpawnProfile<>(entries, totalWeight);
        }
    }

    private record Entry<T>(T variant, int weight) {}
}
