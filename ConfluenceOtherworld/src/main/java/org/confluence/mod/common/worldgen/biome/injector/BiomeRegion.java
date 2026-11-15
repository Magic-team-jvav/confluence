package org.confluence.mod.common.worldgen.biome.injector;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.Set;
import java.util.function.Consumer;

/// 一块由自身气候参数盒子定义的群系区域。
///
/// 与原版 / TerraBlender 的区别：区域不再依赖「最近邻」和占位群系来兜底。
/// 判断流程是「先由 {@link BiomeRegionAllocator} 决定这一列归哪个区域，再用
/// {@link BiomeRegionTable#find} 做参数包含判定」，包含不到就整体回落原版。
/// 因此每个区域必须**完整声明它想接管的气候范围**，声明不到的地方就是原版。
public interface BiomeRegion {
    /// 区域的稳定标识，用于日志和调试；也用于构造地表规则的命名空间。
    ResourceLocation id();

    /// 区域在噪声值域上分到的相对份额。份额只影响面积占比，不影响区域内的参数分布。
    default int weight() {
        return 1;
    }

    /// 该区域可能产出的群系键。用于 `possibleBiomes` 与「按世界旗标禁用某个邪恶群系」的筛选。
    Set<ResourceKey<Biome>> biomes();

    /// 声明参数点。同一个区域可以声明多个盒子，命中顺序即声明顺序（先声明先命中）。
    void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer);
}
