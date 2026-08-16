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

/// 地下发光蘑菇群系的主世界噪声区域。
///
/// <p>深度被限定在地下区间，同时需要远内陆、中等湿度和较高侵蚀度。
/// 因此它是洞穴内的自然群系，而不会把地表大面积替换成蘑菇地形。</p>
public final class GlowingMushroomRegion extends Region {
    public GlowingMushroomRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();
        new ParameterPointListBuilder()
                .temperature(Climate.Parameter.point(-0.10F))
                .humidity(Climate.Parameter.span(0.3F, 0.4F))
                .continentalness(Continentalness.FAR_INLAND)
                .erosion(Erosion.EROSION_4, Erosion.EROSION_5)
                .depth(Climate.Parameter.span(0.6F, 0.9F))
                .weirdness(Weirdness.FULL_RANGE)
                .build()
                .forEach(point -> builder.add(point, ModBiomes.GLOWING_MUSHROOM));
        builder.build().forEach(mapper);
    }
}
