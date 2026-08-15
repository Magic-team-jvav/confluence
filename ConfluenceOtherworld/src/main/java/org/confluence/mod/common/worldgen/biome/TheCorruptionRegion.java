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

import static terrablender.api.ParameterUtils.Continentalness;
import static terrablender.api.ParameterUtils.Depth;
import static terrablender.api.ParameterUtils.Erosion;
import static terrablender.api.ParameterUtils.Humidity;
import static terrablender.api.ParameterUtils.ParameterPointListBuilder;
import static terrablender.api.ParameterUtils.Temperature;
import static terrablender.api.ParameterUtils.Weirdness;

/**
 * 腐化之地的主世界噪声区域。
 *
 * <p>相比血腥之地，腐化之地集中在干燥的暖热内陆和较低侵蚀度范围。
 * 参数区间的区分使两种邪恶群系能共用原版多噪声生成器，又不会在每个区块上相互覆盖。</p>
 */
public final class TheCorruptionRegion extends Region {
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
                .erosion(Erosion.EROSION_0, Erosion.EROSION_3)
                .depth(Depth.UNDERGROUND, Depth.SURFACE)
                .weirdness(Weirdness.FULL_RANGE, Weirdness.LOW_SLICE_VARIANT_ASCENDING)
                .build()
                .forEach(point -> builder.add(point, ModBiomes.THE_CORRUPTION));
        builder.build().forEach(mapper);
    }
}
