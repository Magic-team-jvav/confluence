package org.confluence.mod.common.worldgen.biome.injector;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/// 连续区域分配器：把石英坐标映射成「这一列归哪个区域」。
///
/// ## 为什么不用 TerraBlender 那套分层网格
///
/// TerraBlender 移植了原版 `ZoomLayer` 的分层噪声栈，区域是方格状的，而且索引依赖
/// **所有模组**注册顺序的全局加权表 —— 装/卸任意一个别的群系模组都会改变世界布局。
/// 这里改成每个区域各自一条 {@link NormalNoise}：
///
/// - 区域索引只由本模组自己的区域表决定，第三方模组完全影响不到；
/// - 边界是平滑连续的，没有网格感；
/// - 无每区块缓存需求（每区块实际只调用约 16 次有效采样，相比 `Climate.Sampler` 的
///   6 个密度函数求值可以忽略）。
///
/// ## 为什么不是「一条噪声 + 多条阈值」
///
/// 曾经的写法是三个区域共用**一条**噪声 `z`，按三条**递增**阈值切成带：
/// `带1 = {t₀ ≤ z < t₁}`、`带2 = {t₁ ≤ z < t₂}`、`带3 = {z ≥ t₂}`。
/// 因为 `t₀ < t₁ < t₂`，水平集必然层层嵌套
/// `{z ≥ t₂} ⊆ {z ≥ t₁} ⊆ {z ≥ t₀}` —— 每个噪声峰值处一定长成
/// 「第三个区域的核心 → 第二个区域的环 → 第一个区域的环」的同心结构，
/// 俯视图上就是环状条带，而且最后一个区域永远被前两个包在里面。
/// **这不是参数没调好**：改权重、改区域尺度都改变不了「嵌套」这个拓扑。
///
/// 现在给每个区域一条**独立**的 {@link NormalNoise}（种子由
/// `seed ^ SALT ^ (i+1) × REGION_SALT_STEP` 派生），各自归一化后与**自己的入选门槛**
/// 比较，只在多个区域同时达标的少数格点上取归一化值最大者仲裁。
/// 各区域的达标集合之间没有包含关系，足迹因此是互不嵌套的独立斑块。
///
/// ## 权重语义
///
/// 每条噪声先按**实测标准差**归一化成 `z`，门槛取 `Φ⁻¹(1 − wᵢ / 总权重)`，
/// 于是 `P(zᵢ ≥ 门槛) = wᵢ / 总权重`：`weight` 仍然近似等于**面积占比**，
/// 与噪声的实际振幅、倍频程个数无关（实测标准差让这一点自动成立）。
/// 归一化后是 `z` 的单调函数，所以门槛能直接反解成 `z` 上的一个数，
/// 查询时只需一次比较，不需要 `exp`。
///
/// 注意：各区域独立判定，因此**允许**极少数格点上两个区域同时达标（由 argmax 仲裁），
/// 原版占比因此是 `∏(1 − wᵢ/总权重)` 而不是 `vanillaWeight / 总权重`。
public final class BiomeRegionAllocator {
    /// 用于从世界种子派生出与群系噪声无关的独立随机源。
    private static final long SALT = 0x5EED_5EED_C0FF_EE01L;
    /// 从世界种子派生**每个区域**的独立噪声种子用的黄金比常数。
    /// `seed ^ SALT ^ ((i+1) * REGION_SALT_STEP)`，`i` 是区域在区域表里的下标。
    private static final long REGION_SALT_STEP = 0x9E37_79B9_7F4A_7C15L;
    /// 抽样自检用的固定随机源种子，保证每次启动测出的占比一致、可对比。
    private static final long SAMPLE_SEED = 0x5EED_5EED_5EED_0001L;
    /// `NormalNoise` 的最低频倍频程：`-6` 对应噪声输入空间里 64 单位一个周期。
    private static final int FIRST_OCTAVE = -6;
    private static final double BASE_PERIOD = 64.0D;
    /// 归一化采样点数与采样跨度（以噪声输入为单位）。
    private static final int SAMPLE_COUNT = 1024;
    private static final double SAMPLE_SPREAD = 2.0E6D;

    /// Acklam 反正态近似（相对误差 < 1.15e-9）的系数。
    ///
    /// 之前用 `1/(1+exp(-1.702 z))` 近似正态 CDF，虽然只需一次 `exp`，
    /// 但 logistic 的尾巴比正态厚，反解出来的分带阈值会系统性偏大 ——
    /// 实测下来最靠后的那条带只能拿到标称值的约 70%。
    /// 既然「权重 = 面积占比」是这个分配器对外的核心契约，就值得换成正态分位数。
    private static final double[] A = {
            -3.969683028665376E+01, 2.209460984245205E+02, -2.759285104469687E+02,
            1.383577518672690E+02, -3.066479806614716E+01, 2.506628277459239E+00
    };
    private static final double[] B = {
            -5.447609879822406E+01, 1.615858368580409E+02, -1.556989798598866E+02,
            6.680131188771972E+01, -1.328068155288572E+01
    };
    private static final double[] C = {
            -7.784894002430293E-03, -3.223964580411365E-01, -2.400758277161838E+00,
            -2.549732539343734E+00, 4.374664141464968E+00, 2.938163982698783E+00
    };
    private static final double[] D = {
            7.784695709041462E-03, 3.224671290700398E-01, 2.445134137142996E+00,
            3.754408661907416E+00
    };
    private static final double P_LOW = 0.02425D;
    private static final double P_HIGH = 1.0D - P_LOW;

    /// 每个区域一条**独立**的 {@link NormalNoise}，下标与区域表顺序一致。
    private final NormalNoise[] noises;
    private final double inputScale;
    /// 每条噪声各自的 `1 / 实测标准差`。
    private final double[] inverseSigma;
    /// `thresholds[i]` 是第 i 个区域的**入选门槛**（归一化 z 上的一个数）：
    /// 归一化值 ≥ 门槛 ⇒ 该区域在这一列出现。取值为 `Φ⁻¹(1 − wᵢ/总权重)`。
    private final double[] thresholds;
    private final int regionCount;

    /// @param seed              世界种子
    /// @param vanillaWeight     原版占的权重份额（>= 1）
    /// @param regionWeights     每个区域的权重份额（各自 >= 1），顺序与区域表一致
    /// @param regionSizeBlocks  区域的大致格数尺度
    public BiomeRegionAllocator(long seed, int vanillaWeight, int[] regionWeights, double regionSizeBlocks) {
        this.inputScale = BASE_PERIOD / Math.max(16.0D, regionSizeBlocks * 0.25D);
        this.regionCount = regionWeights.length;

        int[] weights = new int[regionCount];
        int total = Math.max(1, vanillaWeight);
        for (int i = 0; i < regionCount; i++) {
            weights[i] = Math.max(1, regionWeights[i]);
            total += weights[i];
        }

        this.noises = new NormalNoise[regionCount];
        this.inverseSigma = new double[regionCount];
        this.thresholds = new double[regionCount];
        for (int i = 0; i < regionCount; i++) {
            // 每个区域一条独立噪声：种子按黄金比错开，保证互不相关。
            RandomSource random = RandomSource.create(seed ^ SALT ^ (REGION_SALT_STEP * (i + 1)));
            this.noises[i] = NormalNoise.create(random, FIRST_OCTAVE, 1.0D, 0.5D);
            this.inverseSigma[i] = 1.0D / estimateSigma(random, this.noises[i]);
            // 反解「大于等于该门槛」的概率 = 该区域的名义占比。
            this.thresholds[i] = normalQuantile(1.0D - (double) weights[i] / (double) total);
        }
    }

    /// 标准正态分布的分位数（反 CDF）。`p` 必须落在 (0, 1) 内。
    private static double normalQuantile(double p) {
        if (p < P_LOW) {
            double q = Math.sqrt(-2.0D * Math.log(p));
            return (((((C[0] * q + C[1]) * q + C[2]) * q + C[3]) * q + C[4]) * q + C[5])
                    / ((((D[0] * q + D[1]) * q + D[2]) * q + D[3]) * q + 1.0D);
        }
        if (p <= P_HIGH) {
            double q = p - 0.5D;
            double r = q * q;
            return (((((A[0] * r + A[1]) * r + A[2]) * r + A[3]) * r + A[4]) * r + A[5]) * q
                    / (((((B[0] * r + B[1]) * r + B[2]) * r + B[3]) * r + B[4]) * r + 1.0D);
        }
        double q = Math.sqrt(-2.0D * Math.log(1.0D - p));
        return -(((((C[0] * q + C[1]) * q + C[2]) * q + C[3]) * q + C[4]) * q + C[5])
                / ((((D[0] * q + D[1]) * q + D[2]) * q + D[3]) * q + 1.0D);
    }

    public int regionCount() {
        return regionCount;
    }

    /// 每个区域的入选门槛（归一化 z）。`zᵢ ≥ thresholds[i]` ⇒ 第 i 个区域在这一列出现。
    /// 仅供启动日志与调试使用。
    public double[] thresholds() {
        return this.thresholds.clone();
    }

    /// 第 0 个区域实测噪声标准差，仅供诊断：区域场被压成一条平线时这个值会明显异常。
    /// 每个区域各有一条噪声，需要全部时用 {@link #sigmas()}。
    public double sigma() {
        return 1.0D / this.inverseSigma[0];
    }

    /// 每个区域各自的实测噪声标准差。
    public double[] sigmas() {
        double[] out = new double[this.regionCount];
        for (int i = 0; i < this.regionCount; i++) {
            out[i] = 1.0D / this.inverseSigma[i];
        }
        return out;
    }

    /// 在真实种子上抽样测出的各带占比，`shares[0]` 是原版，`shares[i]` 是第 i 个区域。
    ///
    /// 这是判断「区域分配器是否按预期工作」最直接的自检：它不依赖任何世界生成，
    /// 启动时跑一次就能确认权重到面积的映射在这个种子上真的成立。
    /// 采样范围覆盖到世界边境（±840 万石英 ≈ ±3355 万格），统计上等价于全图。
    public double[] measureShares(int samples) {
        long[] counts = new long[this.regionCount + 1];
        RandomSource random = RandomSource.create(SAMPLE_SEED);
        for (int i = 0; i < samples; i++) {
            int quartX = random.nextInt(1 << 24) - (1 << 23);
            int quartZ = random.nextInt(1 << 24) - (1 << 23);
            counts[index(quartX, quartZ)]++;
        }
        double[] shares = new double[counts.length];
        for (int i = 0; i < counts.length; i++) {
            shares[i] = counts[i] / (double) samples;
        }
        return shares;
    }

    /// @return `0` 表示这一列归原版，`1..regionCount` 对应区域表里的第 `index-1` 个区域。
    ///
    /// 与旧实现的关键区别：旧实现是在**一条**噪声上顺序扫描分带，所以结果必然是嵌套的
    /// 同心环；这里每个区域各自与自己的门槛比较，达标集合之间没有包含关系，
    /// 只在多个区域同时达标（很罕见）时取归一化值最大者仲裁。
    public int index(int quartX, int quartZ) {
        int best = 0;
        double bestZ = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < this.regionCount; i++) {
            double z = this.noises[i].getValue(quartX * this.inputScale, 0.0D, quartZ * this.inputScale)
                    * this.inverseSigma[i];
            if (z < this.thresholds[i]) continue;
            if (z > bestZ) {
                bestZ = z;
                best = i + 1;
            }
        }
        return best;
    }

    /// 在世界尺度上实测某条噪声的标准差。这比套用理论振幅稳健：无论倍频程和振幅怎么选，
    /// 权重到面积占比的映射都自动成立。
    private static double estimateSigma(RandomSource random, NormalNoise noise) {
        double sum = 0.0D;
        double squareSum = 0.0D;
        for (int i = 0; i < SAMPLE_COUNT; i++) {
            double x = (random.nextDouble() * 2.0D - 1.0D) * SAMPLE_SPREAD;
            double z = (random.nextDouble() * 2.0D - 1.0D) * SAMPLE_SPREAD;
            double value = noise.getValue(x, 0.0D, z);
            sum += value;
            squareSum += value * value;
        }
        double mean = sum / SAMPLE_COUNT;
        double variance = Math.max(squareSum / SAMPLE_COUNT - mean * mean, 1.0E-6D);
        // 用理论上界兜底，避免极端情况下把区域切得过碎。
        return Math.max(Math.min(Math.sqrt(variance), noise.maxValue()), 1.0E-3D);
    }
}
