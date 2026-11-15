package org.confluence.mod.common.worldgen.biome.injector;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import java.util.function.Supplier;

/// 挂在单个 `BiomeSource` 实例上的群系注入处理器。
///
/// 注入点一律是**环绕式**的：只有确实被接管的坐标才不调用原实现，其余全部
/// `original.get()` 透传，因此与其它同样修改 `getNoiseBiome` 的模组可以叠加。
public interface BiomeSourceHandler {
    /// @param original 惰性求值的原实现结果；只有需要时才调用，避免无谓开销
    Holder<Biome> resolve(int x, int y, int z, Climate.Sampler sampler, Supplier<Holder<Biome>> original);

    /// 需要额外并入 `possibleBiomes()` 的群系。
    ///
    /// 原版 `ChunkGenerator` 会用它做地物步骤排序（`FeatureSorter.buildFeaturesPerStep`）、
    /// 建面时的群系筛选（`ChunkGenerator#applyBiomeDecoration` 里那句 `retainAll`），
    /// 以及结构集筛选（`ChunkGeneratorStructureState`），漏掉会导致地物不生成、
    /// `/locate biome` 找不到。
    ///
    /// 返回 `List` 而不是 `Stream`：这个方法是 `BiomeSource#possibleBiomes` 的读路径上的，
    /// 按区块被调用，实现方应当返回一个**稳定的、建好就不再变**的列表，避免每次分配。
    default List<Holder<Biome>> extraBiomes() {
        return List.of();
    }
}
