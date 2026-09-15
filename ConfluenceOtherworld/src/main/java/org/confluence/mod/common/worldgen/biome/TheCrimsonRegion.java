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

/// 血腥之地的主世界噪声区域。
///
/// 与 {@link TheCorruptionRegion} 使用**完全相同**的参数盒子，理由见那边的文档：
/// 一个世界只会出现两种邪恶群系之一，盒子不承担区分职责，分布由区域分配器的噪声带决定。
/// 两者盒子一致也保证了「腐化世界」与「猩红世界」的地形骨架完全一致，
/// 只差群系本身（贴图、音效、刷怪、地物）。
public final class TheCrimsonRegion implements BiomeRegion {
    public static final ResourceLocation ID = Confluence.asResource("the_crimson");

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
        return Set.of(ModBiomes.THE_CRIMSON);
    }

    @Override
    public void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {
        addColumn(consumer,
                Temperature.FULL_RANGE,
                Humidity.FULL_RANGE,
                Continentalness.LAND,
                Erosion.FULL_RANGE,
                Weirdness.FULL_RANGE,
                ModBiomes.THE_CRIMSON);
    }
}
