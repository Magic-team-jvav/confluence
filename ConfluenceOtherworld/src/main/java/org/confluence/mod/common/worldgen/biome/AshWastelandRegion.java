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

/// 灰烬荒原的下界噪声区域。
///
/// 温湿度与灰烬森林共享较宽的取值范围，但改用第 2 和第 6 档侵蚀度及山峰向怪异度。
/// 这使荒原更容易形成与森林有边界的开阔地带，而不是依赖生物群系 JSON 的偶然选中。
public final class AshWastelandRegion extends Region {
    public AshWastelandRegion(ResourceLocation name, int weight) {
        super(name, RegionType.NETHER, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        new ParameterPointListBuilder()
                .temperature(Temperature.span(Temperature.NEUTRAL, Temperature.HOT))
                .humidity(Humidity.span(Humidity.ARID, Humidity.HUMID))
                .continentalness(Continentalness.INLAND)
                .erosion(Erosion.EROSION_2, Erosion.EROSION_6)
                .depth(Depth.SURFACE, Depth.FLOOR)
                .weirdness(Weirdness.PEAK_NORMAL, Weirdness.FULL_RANGE)
                .build()
                .forEach(point -> builder.add(point, ModBiomes.ASH_WASTELAND));
        builder.build().forEach(mapper);
    }
}
