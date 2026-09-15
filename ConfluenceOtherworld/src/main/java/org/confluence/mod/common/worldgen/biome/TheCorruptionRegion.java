package org.confluence.mod.common.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.worldgen.biome.injector.BiomeRegion;

import java.util.Set;
import java.util.function.Consumer;

import static org.confluence.mod.common.worldgen.biome.injector.ParameterBuilder.*;

/// 腐化之地的主世界噪声区域。
///
/// ## 为什么盒子开得这么宽
///
/// 一个世界只会存在腐化或猩红中的一种（`MultiNoiseBiomeSourceMixin#confluence$getBiomePair`
/// 按世界种子二选一，再把另一个区域的群系键整体改写过来），所以**参数盒子不需要用区分两种邪恶群系**，
/// 那件事交给区域分配器（噪声场）做就够了 —— 两个区域各占一条噪声带。
///
/// 盒子只需要回答「哪些地表可以被邪恶群系整片接管」，答案就是**全部陆地**：
///
/// - 温度 / 湿度 / 侵蚀度 / 怪异度：全区间。泰拉里的腐化本来就覆盖沙漠、雪原、丛林等各种地表。
/// - 大陆度：{@link Continentalness#LAND}（海岸到远内陆），只排除海洋与蘑菇岛。
/// - 深度：{@link ParameterBuilder#COLUMN}，整列接管（含天空，把最底部留给原版深暗之域）。
///
/// ## 占比
///
/// 盒子开到全陆地之后，**占多少完全由区域带宽决定**：主世界是
/// `原版 27 : 腐化 1 : 猩红 1 : 蘑菇 1`，两个邪恶区域又都会重映射成同一个群系，
/// 所以每个邪恶群系实际占 `2/30 ≈ 6.7%` 的列，乘陆地占比后约 **4.7% 的地表**。
/// 嫌多/嫌少就调 `BiomeRegionType#vanillaWeight()`（只有相对比例有意义）。
///
/// 曾经用的是 `温度[0.2, 1.0] + 大陆度[-0.11, 0.55] + 侵蚀度[-1.0, 0.05]`，
/// 四条轴相乘后只剩约 5% 的地表满足，再乘区域带宽，实际占比不到 1%，
/// `/locate biome` 在玩家周围 6400 格内基本必然找不到。诊断日志里
/// `owns column ... but no parameter box covers T=-3748 ... C=-1309 ...` 就是这一现象。
public final class TheCorruptionRegion implements BiomeRegion {
    public static final ResourceLocation ID = Confluence.asResource("the_corruption");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public int weight() {
        return 1;
    }

    @Override
    public Set<ResourceKey<Biome>> biomes() {
        return Set.of(ModBiomes.THE_CORRUPTION);
    }

    @Override
    public void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {
        addColumn(consumer,
                Temperature.FULL_RANGE,
                Humidity.FULL_RANGE,
                Continentalness.LAND,
                Erosion.FULL_RANGE,
                Weirdness.FULL_RANGE,
                ModBiomes.THE_CORRUPTION);
    }
}
