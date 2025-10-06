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

//发光蘑菇群系设置（自然生成，参数设置）
public class GlowingMushroomRegion extends Region {
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
                .build().forEach(point -> builder.add(point, ModBiomes.GLOWING_MUSHROOM));

        // Add our points to the mapper
        builder.build().forEach(mapper);
    }
}