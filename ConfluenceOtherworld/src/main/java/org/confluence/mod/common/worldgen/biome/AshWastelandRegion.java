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

// 灰烬森林群系设置（自然生成，参数设置）
public class AshWastelandRegion extends Region {
    public AshWastelandRegion(ResourceLocation name, int weight) {
        super(name, RegionType.NETHER, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        // Overlap Vanilla's parameters with our own for our COLD_BLUE biome.
        // The parameters for this biome are chosen arbitrarily.
        new ParameterPointListBuilder()
            .temperature(Temperature.span(Temperature.NEUTRAL, Temperature.HOT))
            .humidity(Humidity.span(Humidity.ARID, Humidity.HUMID))
            .continentalness(Continentalness.INLAND)
            .erosion(Erosion.EROSION_2, Erosion.EROSION_6)
            .depth(Depth.SURFACE, Depth.FLOOR)
            .weirdness(Weirdness.PEAK_NORMAL, Weirdness.FULL_RANGE)
            .build().forEach(point -> builder.add(point, ModBiomes.ASH_WASTELAND));
        // Add our points to the mapper
        builder.build().forEach(mapper);
    }
}
