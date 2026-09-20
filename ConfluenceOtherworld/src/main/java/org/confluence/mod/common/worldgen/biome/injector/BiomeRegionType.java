package org.confluence.mod.common.worldgen.biome.injector;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.confluence.mod.util.OverworldUtils;
import org.jetbrains.annotations.Nullable;

/// 区域类别。决定使用哪一张区域表和哪一组地表规则。
///
/// 只覆盖「用多噪声参数空间描述群系分布」的维度；末地群系不使用气候参数，走
/// {@link BiomeSourceHandler} 的自定义实现。
///
/// ## 关于 `vanillaWeight`
///
/// 每个区域的入选门槛取 `Φ⁻¹(1 − wᵢ / 总权重)`，其中
/// `总权重 = vanillaWeight + 所有区域权重之和`。于是某个区域分到的**名义**列占比 =
/// `区域权重 / 总权重`，**只有相对比例有意义**。
///
/// 实测占比会比名义值略低约 5~6%（归一化 sigma 的估计误差 + 高斯尾部模型不精确），
/// 所以这里的取值是**按实测标定过的**：主世界 25 对应 ~9.9% 的列，
/// 下界 17 对应 ~10.2% 的列。
///
/// 因为单个区域权重的最小值是 1，想让新群系占得更少**只能把 `vanillaWeight` 调大**。
/// 换句话讲，`vanillaWeight` 就是「全世界有多少份留给原版」这个旋钮。
///
/// ## 关于 `regionSizeBlocks`
///
/// 这是区域场的**特征尺度**，也就是「一个区域大概横跨多少格」——
/// 它是底层 `NormalNoise` 的最低频倍频程周期（`firstOctave = -6` ⇒ 噪声输入空间 64 单位一个周期），
/// 换算到方块就是 `regionSizeBlocks`。
///
/// **它和占比完全解耦**：占比只取决于入选门槛（也就是权重），跟噪声频率无关。
/// 所以「数量合适但范围太大」只需要调小这个值，不用动权重。
/// 调小会让区域变成更多、更小的碎片；调大则变成更少、更整片的大区域。
public enum BiomeRegionType {
    /// 主世界。
    ///
    /// 权重 `25 : 1 : 1 : 1` ⇒ 实测约 9.9% 的列是新群系，三个区域各约 3.3%：
    /// 邪恶群系（腐化+猩红，两个区域在本世界重映射成同一个群系）合计约 6.6%，
    /// 发光蘑菇地约 3.3%。乘地表陆地占比后邪恶群系约占 **4.7% 的地表**，
    /// 蘑菇地约占 **2.3% 的地下体积**。
    ///
    /// （这里曾经是 27。改成每个区域一条独立噪声之后，同一门槛的实测占比会略低，
    /// 25 就是把「每区域 3.3% / 合计 ~10%」拉回原水平的标定值。）
    ///
    /// 区域尺度 512 格：单个区域大致横跨 200~400 格，与泰拉瑞亚的腐化带（约 300 格宽）
    /// 和发光蘑菇地（约 150 格）同量级。全图范围内大概每 2000 格能碰到一处。
    OVERWORLD(OverworldUtils.dimension(), 25, 512.0D),
    /// 下界。
    ///
    /// 权重 `17 : 1 : 1` ⇒ 实测约 10.2% 的列是新群系，灰烬森林与灰烬荒原各约 5.1%。
    /// 再乘温湿度闸后两种灰烬地形合计约占 **6% 的下界**。
    ///
    /// （这里曾经是 18，原因同主世界：独立噪声方案下同一门槛的实测占比会略低。）
    ///
    /// 区域尺度 256 格（下界只有 128 格高，而且本来就是个紧凑维度）。
    NETHER(OverworldUtils.underworld(), 17, 256.0D);

    private final ResourceKey<Level> dimension;
    private final int vanillaWeight;
    private final double regionSizeBlocks;

    BiomeRegionType(ResourceKey<Level> dimension, int vanillaWeight, double regionSizeBlocks) {
        this.dimension = dimension;
        this.vanillaWeight = vanillaWeight;
        this.regionSizeBlocks = regionSizeBlocks;
    }

    public ResourceKey<Level> dimension() {
        return dimension;
    }

    /// 原版「区域」占的权重份额。只有相对比例有意义：某个区域分到的列占比 =
    /// `区域权重 / (vanillaWeight + 所有区域权重之和)`。区域权重最小值是 1，
    /// 所以想让新群系占得更少，调大这个值。
    public int vanillaWeight() {
        return vanillaWeight;
    }

    /// 区域的特征尺度（格数）。等价于底层噪声的最低频周期，见类文档。
    /// **只影响区域的大小与碎片化程度，不影响占比。**
    public double regionSizeBlocks() {
        return regionSizeBlocks;
    }

    @Nullable
    public static BiomeRegionType byDimension(ResourceKey<Level> dimension) {
        for (BiomeRegionType type : values()) {
            if (type.dimension.equals(dimension)) return type;
        }
        return null;
    }

    @Nullable
    public static BiomeRegionType byDimensionType(Holder<DimensionType> type) {
        if (type.is(BuiltinDimensionTypes.OVERWORLD)) return OVERWORLD;
        if (type.is(BuiltinDimensionTypes.NETHER)) return NETHER;
        return null;
    }
}
