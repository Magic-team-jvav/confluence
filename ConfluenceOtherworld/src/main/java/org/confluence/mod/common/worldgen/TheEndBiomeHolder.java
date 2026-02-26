package org.confluence.mod.common.worldgen;

import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.confluence.mod.common.init.ModBiomes;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

public class TheEndBiomeHolder {
    private static Holder<Biome> chorusForest;
    private static Holder<Biome> inverseForest;

    private static DensityFunction noise;

    private static boolean initialized = false;

    public static void open(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        HolderLookup.RegistryLookup<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME);

        chorusForest = biomes.getOrThrow(ModBiomes.CHORUS_FOREST);
        inverseForest = biomes.getOrThrow(ModBiomes.INVERSE_FOREST);

        noise = DensityFunctions.cache2d(DensityFunctions.endIslands(0L));

        initialized = true;
    }

    public static void close() {
        chorusForest = null;
        inverseForest = null;

        noise = null;

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
                double heightNoise = noise.compute(new DensityFunction.SinglePointContext(blockX, blockY, blockZ));
                if (heightNoise > 0.25) { // highlands

                } else if (heightNoise >= -0.0625) { // midlands

                } else if (heightNoise < -0.21875) { // islands

                } else { // barrens

                }
            }
        }
    }
}
