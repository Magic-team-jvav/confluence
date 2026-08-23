package org.confluence.mod.common.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.confluence.mod.common.init.ModBiomes;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

import static terrablender.api.ParameterUtils.*;

/// 灰烬森林的下界噪声区域。
///
/// 区域覆盖中性至炎热、干燥至湿润的内陆参数，主要落在第 3 和第 5 档侵蚀度。
/// 它与灰烬荒原的侵蚀度、怪异度取值不同，以保证两种灰烬地形能在下界稳定分布。
public final class AshForestRegion extends Region {
    public AshForestRegion(ResourceLocation name, int weight) {
        super(name, RegionType.NETHER, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        new ParameterPointListBuilder()
                .temperature(Temperature.span(Temperature.NEUTRAL, Temperature.HOT))
                .humidity(Humidity.span(Humidity.ARID, Humidity.HUMID))
                .continentalness(Continentalness.INLAND)
                .erosion(Erosion.EROSION_3, Erosion.EROSION_5)
                .depth(Depth.SURFACE, Depth.FLOOR)
                .weirdness(Weirdness.MID_SLICE_NORMAL_ASCENDING, Weirdness.FULL_RANGE)
                .build()
                .forEach(point -> builder.add(point, ModBiomes.ASH_FOREST));
        builder.build().forEach(mapper);
    }
}
