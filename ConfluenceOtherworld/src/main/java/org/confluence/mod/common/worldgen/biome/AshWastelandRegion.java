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

/// 灰烬荒原的下界噪声区域。
///
/// 温湿度与灰烬森林共享较宽的取值范围，但改用较低到较高的侵蚀度区间及山峰向怪异度。
/// 由于下界的 erosion 恒为 0（见 {@link AshForestRegion}），两种灰烬地形的实际分布
/// 主要由区域分配器的权重决定，参数盒子只负责划出「允许出现」的温湿度范围。
///
/// 旧的声明是第 2 档与第 6 档侵蚀度两个**互不相邻**的区间，两者都不包含 0，
/// 在包含判定下永远无法命中，等同于该群系不会生成。这里改用它们的跨越区间，让区域重新生效。
public final class AshWastelandRegion implements BiomeRegion {
    public static final ResourceLocation ID = Confluence.asResource("ash_wasteland");

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
        return Set.of(ModBiomes.ASH_WASTELAND);
    }

    @Override
    public void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {
        add(consumer,
                Temperature.span(Temperature.NEUTRAL, Temperature.HOT),
                Humidity.span(Humidity.ARID, Humidity.HUMID),
                Continentalness.INLAND,
                Erosion.span(Erosion.EROSION_2, Erosion.EROSION_6),
                Depth.NETHER_RANGE,
                Weirdness.FULL_RANGE,
                ModBiomes.ASH_WASTELAND);
    }
}
