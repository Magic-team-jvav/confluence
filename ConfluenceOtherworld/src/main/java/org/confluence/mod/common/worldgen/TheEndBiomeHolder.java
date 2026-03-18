package org.confluence.mod.common.worldgen;

import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.confluence.mod.common.init.ModBiomes;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

public class TheEndBiomeHolder {
    private static Holder<Biome> chorusForest;
    private static Holder<Biome> inverseForest;
    private static Holder<Biome> silverSoulForest;

    private static long seed;

    private static ImprovedNoise improvedNoise;

    private static boolean initialized = false;

    public static void open(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        seed = server.getWorldData().worldGenOptions().seed();
        HolderLookup.RegistryLookup<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME);

        chorusForest = biomes.getOrThrow(ModBiomes.CHORUS_FOREST);
        inverseForest = biomes.getOrThrow(ModBiomes.INVERSE_FOREST);
        silverSoulForest = biomes.getOrThrow(ModBiomes.MOONBLIGHT_FOREST);

        improvedNoise = new ImprovedNoise(RandomSource.create(seed));

        initialized = true;
    }

    public static void close() {
        chorusForest = null;
        inverseForest = null;

        seed = 0;

        initialized = false;
    }

    public static Stream<Holder<Biome>> addConfluenceBiomes(Stream<Holder<Biome>> original) {
        if (initialized) {
            return Stream.concat(original, Stream.of(chorusForest, inverseForest));
        }
        return original;
    }

    /// [terrablender.mixin.MixinTheEndBiomeSource#onGetNoiseBiome]
    public static void replaceBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        if (initialized) {
            int blockX = QuartPos.toBlock(x);
            int blockY = QuartPos.toBlock(y);
            int blockZ = QuartPos.toBlock(z);
            long sectionX = SectionPos.blockToSectionCoord(blockX);
            long sectionZ = SectionPos.blockToSectionCoord(blockZ);
            if (sectionX * sectionX + sectionZ * sectionZ > 4096L) {
                double biomeScale = 0.025;
                double heightScale = 0.25;
                double trueNoise = rippleNoise(blockX, blockY, blockZ, 3000);
                double heightNoise = blockY + improvedNoise.noise(x * heightScale, 0, z * heightScale) * 5;
                double biomeNoise = improvedNoise.noise(x * biomeScale, y * biomeScale, z * biomeScale);
                if (trueNoise > 0.75) {
                    if (heightNoise > 30) {
                        if (biomeNoise > 0) cir.setReturnValue(chorusForest);
                        else cir.setReturnValue(silverSoulForest);
                    } else {
                        cir.setReturnValue(inverseForest);
                    }
                }
            }
        }
    }

    public static double rippleNoise(int x, int y, int z, int radius) {
        double scale = 0.001;
        double scale1 = 0.005;
        double base = improvedNoise.noise(x * scale, y * scale, z * scale); // 降低频率
        return (((Math.sin(base * 20) / 2 + 0.5) * radiusSet(radius, x, z) * 2) + Math.sin(improvedNoise.noise(x * scale1, y * scale1, z * scale1) * 20) * 0.3); // 波纹映射
    }

    public static double radiusSet(int radius, int x, int z) {
        int transit = 100;
        float dis = Mth.sqrt(x * x + z * z);
        return (Mth.abs(Mth.abs(dis / transit - (float) radius / transit - 1) - 1) - Mth.sqrt(dis / transit - (float) radius / transit - 2) + 2) / 2;
    }
}
