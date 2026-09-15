package org.confluence.mod.common.worldgen.biome.injector;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/// 连续区域分配器：把石英坐标映射成「这一列归哪个区域」。
///
/// ## 为什么不用 TerraBlender 那套分层网格
///
/// TerraBlender 移植了原版 `ZoomLayer` 的分层噪声栈，区域是方格状的，而且索引依赖
/// **所有模组**注册顺序的全局加权表 —— 装/卸任意一个别的群系模组都会改变世界布局。
/// 这里改用单个 {@link NormalNoise}：
///
/// - 区域索引只由本模组自己的区域表决定，第三方模组完全影响不到；
/// - 边界是平滑连续的，没有网格感；
/// - 无每区块缓存需求（每区块实际只调用约 16 次有效采样，相比 `Climate.Sampler` 的
///   6 个密度函数求值可以忽略）。
///
/// ## 权重语义
///
/// 噪声值先按**实测标准差**归一化成 `z`，再用 logistic 分布函数近似标准正态 CDF 映射到
/// `t ∈ (0,1)`，最后按权重把 `t` 切成若干等宽带。这样 `weight` 就近似等于**面积占比**，
/// 与噪声的实际振幅、倍频程个数无关（实测标准差让这一点自动成立）。
///
/// 归一化后是 `z` 的单调函数，所以分带边界可以直接反解成 `z` 上的阈值，
/// 查询时只需一次比较 + 顺序扫描，不需要 `exp`。
public final class BiomeRegionAllocator {
    /// 用于从世界种子派生出与群系噪声无关的独立随机源。
    private static final long SALT = 0x5EED_5EED_C0FF_EE01L;
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

    private final NormalNoise noise;
    private final double inputScale;
    private final double inverseSigma;
    /// `thresholds[i]` 是「第 i 个区域」与「第 i+1 个区域」在归一化 z 上的分界（i 从 0 起）。
    /// `z < thresholds[0]` 表示原版。
    private final double[] thresholds;
    private final int regionCount;

    /// @param seed              世界种子
    /// @param vanillaWeight     原版占的权重份额（>= 1）
    /// @param regionWeights     每个区域的权重份额（各自 >= 1），顺序与区域表一致
    /// @param regionSizeBlocks  区域的大致格数尺度
    public BiomeRegionAllocator(long seed, int vanillaWeight, int[] regionWeights, double regionSizeBlocks) {
        RandomSource random = RandomSource.create(seed ^ SALT);
        this.noise = NormalNoise.create(random, FIRST_OCTAVE, 1.0D, 0.5D);
        this.inputScale = BASE_PERIOD / Math.max(16.0D, regionSizeBlocks * 0.25D);
        this.inverseSigma = 1.0D / estimateSigma(random);
        this.regionCount = regionWeights.length;

        int[] weights = new int[regionCount];
        int total = Math.max(1, vanillaWeight);
        for (int i = 0; i < regionCount; i++) {
            weights[i] = Math.max(1, regionWeights[i]);
            total += weights[i];
        }

        this.thresholds = new double[regionCount];
        int accumulated = Math.max(1, vanillaWeight);
        for (int i = 0; i < regionCount; i++) {
            double t = (double) accumulated / (double) total;
            this.thresholds[i] = normalQuantile(t);
            accumulated += weights[i];
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

    /// 归一化 z 上的分带阈值，`thresholds[i]` 是「第 i 个区域」与「第 i+1 个区域」的分界。
    /// 仅供启动日志与调试使用。
    public double[] thresholds() {
        return this.thresholds.clone();
    }

    /// 实测噪声标准差，仅供诊断：区域场被压成一条平线时这个值会明显异常。
    public double sigma() {
        return 1.0D / this.inverseSigma;
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

    /// @return `0` 表示这一列归原版，`1..regionCount` 对应区域表里的第 `index-1` 个区域
    public int index(int quartX, int quartZ) {
        double z = this.noise.getValue(quartX * this.inputScale, 0.0D, quartZ * this.inputScale) * this.inverseSigma;
        int index = 0;
        while (index < this.regionCount && z >= this.thresholds[index]) {
            index++;
        }
        return index;
    }

    /// 在世界尺度上实测噪声标准差。这比套用理论振幅稳健：无论倍频程和振幅怎么选，
    /// 权重到面积占比的映射都自动成立。
    private double estimateSigma(RandomSource random) {
        double sum = 0.0D;
        double squareSum = 0.0D;
        for (int i = 0; i < SAMPLE_COUNT; i++) {
            double x = (random.nextDouble() * 2.0D - 1.0D) * SAMPLE_SPREAD;
            double z = (random.nextDouble() * 2.0D - 1.0D) * SAMPLE_SPREAD;
            double value = this.noise.getValue(x, 0.0D, z);
            sum += value;
            squareSum += value * value;
        }
        double mean = sum / SAMPLE_COUNT;
        double variance = Math.max(squareSum / SAMPLE_COUNT - mean * mean, 1.0E-6D);
        // 用理论上界兜底，避免极端情况下把区域切得过碎。
        return Math.max(Math.min(Math.sqrt(variance), this.noise.maxValue()), 1.0E-3D);
    }
}
