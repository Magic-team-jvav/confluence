package org.confluence.mod.common.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.mixin.world.level.dimension.DimensionTypeAccessor;

import java.util.stream.Stream;

public class TheEndBiomeHolder {
    private static Holder<Biome> chorusForest;
    private static Holder<Biome> inverseForest;
    private static Holder<Biome> moonlightForest;
    private static Holder<Biome> chorusPlains;
    private static Holder<Biome> inversePlains;
    private static Holder<Biome> moonlightPlains;
    private static Holder<Biome> moonlitDrySea;
    private static Holder<Biome> darkMoonFlats;

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
        moonlitDrySea = biomes.getOrThrow(ModBiomes.MOONLIT_DRY_SEA);
        darkMoonFlats = biomes.getOrThrow(ModBiomes.DARK_MOON_FLATS);

        normalNoise = NormalNoise.create(RandomSource.create(seed), -5, 1.0, 1.0, 1.0, 1.0);

        initialized = true;
    }

    public static void close() {
        chorusForest = null;
        inverseForest = null;
        moonlightForest = null;
        chorusPlains = null;
        inversePlains = null;
        moonlightPlains = null;
        moonlitDrySea = null;
        darkMoonFlats = null;

        seed = 0;

        initialized = false;
    }

    public static Stream<Holder<Biome>> addConfluenceBiomes(Stream<Holder<Biome>> original) {
        if (initialized) {
            Stream<Holder<Biome>> myBiomes = Stream.of(
                    chorusForest,
                    inverseForest,
                    moonlightForest,
                    chorusPlains,
                    inversePlains,
                    moonlightPlains,
                    moonlitDrySea,
                    darkMoonFlats
            );
            return Stream.concat(original, myBiomes);
        }
        return original;
    }

    public static Holder<Biome> replaceBiome(int x, int y, int z, Climate.Sampler sampler, Holder<Biome> original) {
        if (!initialized) return original;
        int blockX = QuartPos.toBlock(x);
        int blockY = QuartPos.toBlock(y);
        int blockZ = QuartPos.toBlock(z);
        double erosion = sampler.erosion().compute(new DensityFunction.SinglePointContext(blockX, blockY, blockZ));
        if (erosion < -0.0625) return original;

        //TODO 等牢鏡調整

        // 以下參數都是數字越大密度越大越稀碎，數字越小密度越小，單一群係也約廣闊

        double biomeScale = 0.5;
        // 決定了我們的群係組的分佈密度，也就是紫頌和月光群係大類的密度，倒懸是跟著紫頌一起的不用管它
        double treeScale = 0.45;
        // 決定了紫頌森林和紫頌平原的分佈密度
        double humidityScale = 0.2;
        // 決定了月光系列四種群係的密度
        double heightScale = 0.25;
        // 原版判斷島嶼什麼的，這個別改XXX
        double trueNoise = rippleNoise(0.01, blockX, blockY, blockZ, 3000);
        // 第一個參數決定了mod群係和原版群係的分佈區域的密度，最後一個參數決定了末地中心周圍多少米內不生成我們的群係
        double heightNoise = blockY + normalNoise.getValue(x * heightScale, 0, z * heightScale) * 5;
        double biomeNoise = normalNoise.getValue(x * biomeScale, y * biomeScale, z * biomeScale);
        double treeNoise = normalNoise.getValue(x * treeScale, y * treeScale, z * treeScale);
        double humidityNoise = normalNoise.getValue(x * humidityScale, y * humidityScale, z * humidityScale);
        if (trueNoise > 0) {
            if (biomeNoise > 0) {
                if (heightNoise > 30) {
                    return treeNoise > 0 ? chorusForest : chorusPlains;
                }
                return treeNoise > 0 ? inverseForest : inversePlains;
            }
            if (humidityNoise > 0.3) return moonlightForest;
            else if (humidityNoise > 0) return moonlightPlains;
            else if (humidityNoise > -0.3) return darkMoonFlats;
            else return moonlitDrySea;
        }
        return original;
    }

    public static double rippleNoise(double scale, int x, int y, int z, int radius) {
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
