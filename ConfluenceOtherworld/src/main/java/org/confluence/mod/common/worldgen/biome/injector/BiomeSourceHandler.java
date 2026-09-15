package org.confluence.mod.common.worldgen.biome.injector;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.function.Supplier;
import java.util.stream.Stream;

/// 挂在单个 `BiomeSource` 实例上的群系注入处理器。
///
/// 注入点一律是**环绕式**的：只有确实被接管的坐标才不调用原实现，其余全部
/// `original.get()` 透传，因此与其它同样修改 `getNoiseBiome` 的模组可以叠加。
public interface BiomeSourceHandler {
    /// @param original 惰性求值的原实现结果；只有需要时才调用，避免无谓开销
    Holder<Biome> resolve(int x, int y, int z, Climate.Sampler sampler, Supplier<Holder<Biome>> original);

    /// 需要额外并入 `possibleBiomes()` 的群系。
    ///
    /// 原版 `ChunkGenerator` 会用它做地物步骤排序（`FeatureSorter.buildFeaturesPerStep`），
    /// 以及结构集筛选（`ChunkGeneratorStructureState`），漏掉会导致地物不生成。
    default Stream<Holder<Biome>> extraBiomes() {
        return Stream.empty();
    }
}
