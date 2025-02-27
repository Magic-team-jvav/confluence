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
public class TheCorruptionRegion extends Region {
    public TheCorruptionRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        new ParameterPointListBuilder()
            .temperature(Temperature.span(Temperature.WARM, Temperature.HOT))
            .humidity(Humidity.span(Humidity.ARID, Humidity.DRY))
            .continentalness(Continentalness.INLAND)
            .erosion(Erosion.EROSION_0, Erosion. EROSION_3)
            .depth(Depth.UNDERGROUND, Depth.SURFACE)
            .weirdness(Weirdness.FULL_RANGE, Weirdness.LOW_SLICE_VARIANT_ASCENDING)
            .build().forEach(point -> builder.add(point, ModBiomes.THE_CORRUPTION));
        builder.build().forEach(mapper);
    }
}