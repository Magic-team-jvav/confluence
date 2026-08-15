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
 * 血腥之地的主世界噪声区域。
 *
 * <p>它覆盖偏暖到炎热、内陆、地表至地下的大范围湿度组合，但只接管第 4—5 档侵蚀度。
 * 这使血腥之地与腐化之地在参数空间中保持不同形状，而不是单纯随机替换任意原版群系。</p>
 */
public final class TheCrimsonRegion extends Region {
    public TheCrimsonRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        new ParameterPointListBuilder()
                .temperature(Temperature.span(Temperature.WARM, Temperature.HOT))
                .humidity(Humidity.span(Humidity.ARID, Humidity.HUMID))
                .continentalness(Continentalness.INLAND)
                .erosion(Erosion.EROSION_4, Erosion.EROSION_5)
                .depth(Depth.UNDERGROUND, Depth.SURFACE)
                .weirdness(Weirdness.MID_SLICE_NORMAL_ASCENDING, Weirdness.FULL_RANGE)
                .build()
                .forEach(point -> builder.add(point, ModBiomes.THE_CRIMSON));
        builder.build().forEach(mapper);
    }
}
