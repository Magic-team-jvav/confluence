package org.confluence.mod.common.worldgen;

import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.mixin.accessor.DimensionTypeAccessor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.api.EndBiomeRegistry;
import terrablender.core.TerraBlender;
import terrablender.worldgen.noise.LayeredNoiseUtil;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class TheEndBiomeHolder {
    private static Holder<Biome> chorusForest;
    private static Holder<Biome> inverseForest;
    private static Holder<Biome> moonlightForest;
    private static Holder<Biome> chorusPlains;
    private static Holder<Biome> inversePlains;
    private static Holder<Biome> moonlightPlains;

    private static long seed;

    private static NormalNoise normalNoise;

    private static boolean initialized = false;

    public static void open(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        seed = server.getWorldData().worldGenOptions().seed();
        HolderLookup.RegistryLookup<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME);

        chorusForest = biomes.getOrThrow(ModBiomes.CHORUS_FOREST);
        inverseForest = biomes.getOrThrow(ModBiomes.INVERSE_FOREST);
        moonlightForest = biomes.getOrThrow(ModBiomes.MOONBLIGHT_FOREST);
        chorusPlains = biomes.getOrThrow(ModBiomes.CHORUS_PLAINS);
        inversePlains = biomes.getOrThrow(ModBiomes.INVERSE_PLAINS);
        moonlightPlains = biomes.getOrThrow(ModBiomes.MOONBLIGHT_PLAINS);

        normalNoise = NormalNoise.create(RandomSource.create(seed), -5, 1.0, 1.0, 1.0, 1.0);

        fixTerraBlender(server);

        initialized = true;
    }

    private static void fixTerraBlender(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        LevelStem levelStem = registryAccess.holderOrThrow(LevelStem.END).value();
        if (levelStem.generator().getBiomeSource() instanceof TheEndBiomeSource biomeSource) {
            try {
                Field islandsArea = biomeSource.getClass().getDeclaredField("islandsArea");
                islandsArea.setAccessible(true);
                islandsArea.set(biomeSource, LayeredNoiseUtil.biomeArea(registryAccess, seed, TerraBlender.CONFIG.endIslandBiomeSize, EndBiomeRegistry.getIslandBiomes()));
            } catch (Exception ignored) {}
        }
    }

    public static void close() {
        chorusForest = null;
        inverseForest = null;
        moonlightForest = null;
        chorusPlains = null;
        inversePlains = null;
        moonlightPlains = null;

        seed = 0;

        initialized = false;
    }

    public static void addConfluenceBiomes(CallbackInfoReturnable<Stream<Holder<Biome>>> cir) {
        if (initialized) {
            Stream<Holder<Biome>> stream = cir.getReturnValue();
            if (stream == null) return;
            Stream<Holder<Biome>> myBiomes = Stream.of(chorusForest, inverseForest, moonlightForest, chorusPlains, inversePlains, moonlightPlains);
            cir.setReturnValue(Stream.concat(cir.getReturnValue(), myBiomes));
        }
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
                double heightNoise = sampler.erosion().compute(new DensityFunction.SinglePointContext(blockX, blockY, blockZ));
                if (heightNoise < -0.0625) {
                    return;
                }

                double biomeScale = 0.5;
                double treeScale = 0.45;
                double heightScale = 0.25;
                double trueNoise = rippleNoise(blockX, blockY, blockZ, 3000);
                heightNoise = blockY + normalNoise.getValue(x * heightScale, 0, z * heightScale) * 5;
                double biomeNoise = normalNoise.getValue(x * biomeScale, y * biomeScale, z * biomeScale);
                double treeNoise = normalNoise.getValue(x * treeScale, y * treeScale, z * treeScale);
                if (trueNoise > 0) {
                    if (heightNoise > 30) {
                        if (biomeNoise > 0) {
                            cir.setReturnValue((treeNoise > 0) ? chorusForest : chorusPlains);
                        } else {
                            cir.setReturnValue((treeNoise > 0) ? moonlightForest : moonlightPlains);
                        }
                    } else {
                        cir.setReturnValue((treeNoise > 0) ? inverseForest : inversePlains);
                    }
                }
            }
        }
    }

    public static double rippleNoise(int x, int y, int z, int radius) {
        double scale = 0.01;
        double base = normalNoise.getValue(x * scale, y * scale, z * scale); // 降低频率
        return (Math.sin(base * 20) * radiusSet(radius, x, z)); // 波纹映射
    }

    public static double radiusSet(int radius, int x, int z) {
        int transit = 100;
        float dis = Mth.sqrt(x * x + z * z);
        return (Mth.abs(Mth.abs(dis / transit - (float) radius / transit - 1) - 1) - Mth.sqrt(dis / transit - (float) radius / transit - 2) + 2) / 2;
    }

    public static void modifyDimensionType(DimensionType type) {
        DimensionTypeAccessor accessor = (DimensionTypeAccessor) (Record) type;
        if (accessor.getMinY() > -64) {
            accessor.setMinY(-64);
        }
        if (accessor.getHeight() < 384) {
            accessor.setHeight(384);
        }
        if (accessor.getLogicalHeight() < 384) {
            accessor.setLogicalHeight(384);
        }
    }
}
