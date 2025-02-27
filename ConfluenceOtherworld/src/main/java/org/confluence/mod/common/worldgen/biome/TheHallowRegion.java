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

//腐化群系设置（自然生成，参数设置）
public class TheHallowRegion extends Region {
    public TheHallowRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        // Overlap Vanilla's parameters with our own for our COLD_BLUE biome.
        // The parameters for this biome are chosen arbitrarily.
        new ParameterPointListBuilder()
            .temperature(Temperature.span(Temperature.ICY, Temperature.COOL))
            .humidity(Humidity.span(Humidity.DRY, Humidity.NEUTRAL))
            .continentalness(Continentalness.INLAND)
            .erosion(Erosion.EROSION_4, Erosion.EROSION_6)
            .depth(Depth.SURFACE, Depth.FLOOR)
            .weirdness(Weirdness.MID_SLICE_NORMAL_ASCENDING, Weirdness.FULL_RANGE)
            .build().forEach(point -> builder.add(point, ModBiomes.THE_HALLOW));

        // Add our points to the mapper
        builder.build().forEach(mapper);
    }
}