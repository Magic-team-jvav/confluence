package org.confluence.mod.common.worldgen.biome.injector;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.stream.Stream;

/// 基于 {@link BiomeRegionTable} 的处理器，可选地再挂一个后处理（主世界的
/// 「不是蜜蜂」密种与出生点保护）。
public final class RegionBiomeHandler implements BiomeSourceHandler {
    /// 在区域结果之后、最终返回之前做的额外替换。
    public interface PostProcessor {
        Holder<Biome> apply(int x, int y, int z, Holder<Biome> biome);
    }

    private final BiomeRegionTable table;
    @Nullable
    private final PostProcessor postProcessor;

    public RegionBiomeHandler(BiomeRegionTable table, @Nullable PostProcessor postProcessor) {
        this.table = table;
        this.postProcessor = postProcessor;
    }

    public BiomeRegionTable table() {
        return table;
    }

    @Override
    public Holder<Biome> resolve(int x, int y, int z, Climate.Sampler sampler, Supplier<Holder<Biome>> original) {
        Holder<Biome> biome = table.resolve(x, y, z, sampler);
        if (biome == null) {
            biome = original.get();
        } else {
            InjectionProbe.biomeApplied();
        }
        return postProcessor == null ? biome : postProcessor.apply(x, y, z, biome);
    }

    @Override
    public Stream<Holder<Biome>> extraBiomes() {
        return table.biomeStream();
    }
}
