package org.confluence.mod.common.entity.animal;

import net.minecraft.util.RandomSource;

/// 小动物变体选择的边界安全工具。
///
/// 同步或存档中的非法序号统一回退到调用方指定的默认变体；自然生成则在普通变体间等概率
/// 选择，并用固定的四百分之一概率替换为金色稀有变体。集中处理可以防止每个小动物类各自实现
/// 不同的越界和稀有度语义。
final class CritterVariantUtil {
    static final int GOLD_RARITY = 400;

    private CritterVariantUtil() {}

    /// 按序号读取变体，越界时返回安全回退值。
    static <T> T byId(T[] variants, int id, T fallback) {
        return id >= 0 && id < variants.length ? variants[id] : fallback;
    }

    /// 在非空普通变体数组中等概率选择一个值。
    static <T> T uniform(RandomSource random, T[] variants) {
        return variants[random.nextInt(variants.length)];
    }

    /// 先判定金色稀有变体，未命中时再从普通变体中等概率选择。
    static <T> T withRareVariant(RandomSource random, T[] commonVariants, T rareVariant) {
        return random.nextInt(GOLD_RARITY) == 0
                ? rareVariant
                : uniform(random, commonVariants);
    }
}
