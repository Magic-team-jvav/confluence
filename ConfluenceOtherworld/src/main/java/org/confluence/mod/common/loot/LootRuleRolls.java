package org.confluence.mod.common.loot;

import java.util.function.DoubleSupplier;

// 子规则百分比抽取，与战利品序列化分离。
public final class LootRuleRolls {
    private LootRuleRolls() {}

    // 将零幸运值概率与原版有效权重倍率换算为实际概率；倍率为零表示条目不满足条件或不可选。
    // 互斥组共用剩余空概率，独立组分别保留各自的空概率，避免改变原版 quality 的效果。
    public static double[] applyLuck(double[] probabilities, double[] multipliers, boolean exclusive) {
        if (probabilities.length != multipliers.length)
            throw new IllegalArgumentException("Probability and multiplier lengths differ");
        double[] adjusted = new double[probabilities.length];
        double empty = 1;
        double total = 0;
        for (int i = 0; i < probabilities.length; i++) {
            double p = probabilities[i];
            double multiplier = multipliers[i];
            if (!Double.isFinite(p) || p < 0 || p > 1 || !Double.isFinite(multiplier) || multiplier < 0) {
                throw new IllegalArgumentException("Invalid probability or luck multiplier");
            }
            double weight = p * multiplier;
            empty -= p;
            total += weight;
            adjusted[i] = exclusive ? weight : normalizeChance(weight, 1 - p + weight);
        }
        if (exclusive) {
            total += Math.max(0, empty);
            for (int i = 0; i < adjusted.length; i++)
                adjusted[i] = normalizeChance(adjusted[i], total);
        }
        return adjusted;
    }

    // 全部候选不可用时返回零，浮点误差不得使概率超过一。
    private static double normalizeChance(double weight, double total) {
        return total > 0 ? Math.min(1, weight / total) : 0;
    }

    // probabilities 使用 0～1 的概率，返回值与输入逐项对应；random 应提供 [0, 1) 的随机数。
    // exclusive 开启时至多成功一项；guarantee 表示本次整组全失败后重抽，不累计击杀次数。
    // 通过条件概率直接得到重抽结果；所有概率为零时返回全失败，避免无限重试。
    public static boolean[] roll(double[] probabilities, boolean exclusive, boolean guarantee, DoubleSupplier random) {
        boolean[] selected = new boolean[probabilities.length];
        double[] suffixSuccess = new double[probabilities.length + 1];
        double sum = 0;
        for (int i = probabilities.length - 1; i >= 0; i--) {
            double p = probabilities[i];
            if (!Double.isFinite(p) || p < 0 || p > 1) {
                throw new IllegalArgumentException("Invalid child probability: " + p);
            }
            sum += p;
            suffixSuccess[i] = p + (1 - p) * suffixSuccess[i + 1];
        }
        if (exclusive) {
            if (sum > 1.000000001)
                throw new IllegalArgumentException("Exclusive percentages exceed 100");
            double roll = random.getAsDouble() * (guarantee ? sum : 1);
            for (int i = 0; i < probabilities.length; i++) {
                roll -= probabilities[i];
                if (roll < 0) {
                    selected[i] = true;
                    break;
                }
            }
        } else {
            boolean succeeded = false;
            for (int i = 0; i < probabilities.length; i++) {
                double p = probabilities[i];
                // 按至少成功一项的条件概率抽取，等价于全失败后重掷整组。
                // 避免极低概率或全零概率导致无限重试。
                if (guarantee && !succeeded && suffixSuccess[i] > 0) p /= suffixSuccess[i];
                selected[i] = p > 0 && random.getAsDouble() < p;
                succeeded |= selected[i];
            }
        }
        return selected;
    }
}
