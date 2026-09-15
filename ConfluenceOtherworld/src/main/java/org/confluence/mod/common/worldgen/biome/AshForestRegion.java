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

/// 灰烬森林的下界噪声区域。
///
/// 区域覆盖中性至炎热、干燥至湿润的内陆参数，主要落在第 3—5 档侵蚀度。
/// 它与灰烬荒原的侵蚀度取值不同，以保证两种灰烬地形能在下界稳定分布。
///
/// ## 下界的气候不变量
///
/// `NoiseRouterData#nether` 的 continentalness / erosion / depth / weirdness 路由全部是
/// `DensityFunctions.zero()`，即恒为 0。所以下界真正能区分的只有 temperature 与 humidity，
/// 其余轴必须声明成**包含 0 的区间**才可能命中；两者之间的实际分布由区域分配器（噪声场）决定。
public final class AshForestRegion implements BiomeRegion {
    public static final ResourceLocation ID = Confluence.asResource("ash_forest");

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
        return Set.of(ModBiomes.ASH_FOREST);
    }

    @Override
    public void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {
        add(consumer,
                Temperature.span(Temperature.NEUTRAL, Temperature.HOT),
                Humidity.span(Humidity.ARID, Humidity.HUMID),
                Continentalness.INLAND,
                Erosion.span(Erosion.EROSION_3, Erosion.EROSION_5),
                Depth.NETHER_RANGE,
                Weirdness.FULL_RANGE,
                ModBiomes.ASH_FOREST);
    }
}
