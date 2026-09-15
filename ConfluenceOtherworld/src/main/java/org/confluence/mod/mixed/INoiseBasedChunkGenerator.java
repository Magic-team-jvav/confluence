package org.confluence.mod.mixed;

import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.Nullable;

/// 给 `NoiseBasedChunkGenerator` **实例**挂一份「已经按维度拼好的地表规则」。
///
/// ## 为什么需要它
///
/// 原版 `SurfaceSystem#buildSurface`（8 参）的调用点在
/// `NoiseBasedChunkGenerator#buildSurface(ChunkAccess, WorldGenerationContext, RandomState,
/// StructureManager, BiomeManager, Registry, Blender)` 里，那个方法没有 `WorldGenRegion`，
/// 拿不到维度；而带 `WorldGenRegion` 的 4 参重载只是转发，根本不碰规则源。
/// 所以由 Confluence Biome Injector 在 `ServerAboutToStartEvent` 里按 `LevelStem` 的维度类型
/// 把规则算好，写到生成器实例上，注入点只负责把它替换进去。
///
/// 状态是**每实例**的：同一个 `NoiseGeneratorSettings` 被多个维度复用时不会互相污染，
/// 也不存在 TerraBlender 那种「可变 regionType + surfaceRule() 懒记忆化」的时序陷阱。
public interface INoiseBasedChunkGenerator {
    void confluence$setSurfaceRules(@Nullable SurfaceRules.RuleSource rules);

    static INoiseBasedChunkGenerator of(NoiseBasedChunkGenerator generator) {
        return (INoiseBasedChunkGenerator) generator;
    }
}
