package org.confluence.mod.common.init;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.confluence.lib.common.worldgen.biome.BlockCounts;
import org.confluence.lib.common.worldgen.biome.DynamicBiomeUtils;
import org.confluence.lib.common.worldgen.biome.SectionContext;
import org.jetbrains.annotations.Nullable;

/// Confluence 的动态群系规则与优先级。
///
/// 计数机制与传播逻辑在 magiclib（{@link DynamicBiomeUtils}），这里只注册 Confluence 自己的语义：
/// 哪些方块算腐化/猩红/神圣、阈值多少、哪个群系优先。
public final class ModDynamicBiomes {
    /// 判定阈值：4096 个方块里有多少个才算这个群系
    public static final int BIOME_THRESHOLD = 256;
    /// 退出低于进入阈值，替换现有覆盖还需超过竞争差值，避免边界反复切换。
    public static final int EXIT_THRESHOLD = 204;
    public static final int REPLACEMENT_ADVANTAGE = 64;

    private ModDynamicBiomes() {}

    /// 强制触发本类的静态初始化，完成规则与优先级注册。
    ///
    /// 必须在**任何世界加载之前**调用（与 {@link ModBlockCounters#init()} 一起）。
    public static void init() {}

    static {
        ModBlockCounters.init(); // 计数器先注册：计数数组长度在第一个 section 创建时固化

        // 可蔓延（邪恶 / 神圣）群系：区块净化时回滚到备份的纯净群系
        DynamicBiomeUtils.registerSpreadable(biome -> biome.is(ModTags.Biomes.SPREADABLE));

        // 优先级，数字小的优先
        DynamicBiomeUtils.registerPriority(ModBiomes.THE_HALLOW_TUNDRA, 100);
        DynamicBiomeUtils.registerPriority(ModBiomes.THE_CORRUPTION_TUNDRA, 200);
        DynamicBiomeUtils.registerPriority(ModBiomes.THE_CRIMSON_TUNDRA, 300);
        DynamicBiomeUtils.registerPriority(ModBiomes.THE_HALLOW_DESERT, 400);
        DynamicBiomeUtils.registerPriority(ModBiomes.THE_CORRUPTION_DESERT, 500);
        DynamicBiomeUtils.registerPriority(ModBiomes.THE_CRIMSON_DESERT, 600);
        DynamicBiomeUtils.registerPriority(ModBiomes.THE_HALLOW, 700);
        DynamicBiomeUtils.registerPriority(ModBiomes.THE_CORRUPTION, 800);
        DynamicBiomeUtils.registerPriority(ModBiomes.THE_CRIMSON, 900);
        DynamicBiomeUtils.registerPriority(ModBiomes.GLOWING_MUSHROOM, 1000);

        DynamicBiomeUtils.registerRule(ModDynamicBiomes::judge);

        DynamicBiomeUtils.registerCounter(ModBlockCounters.CORRUPT, true);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.CRIMSON, true);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.HALLOW, true);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.GLOWING_MUSHROOM, true);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.CORRUPT_SAND, false);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.CORRUPT_ICE, false);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.CRIMSON_SAND, false);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.CRIMSON_ICE, false);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.HALLOW_SAND, false);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.HALLOW_ICE, false);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.SUNFLOWER, false);
        DynamicBiomeUtils.registerCounter(ModBlockCounters.WATER, false);

        /// 天然感染区域的净化底图；后续动态感染使用生成时保存的逐点底图。
        DynamicBiomeUtils.registerPureBiome(ModBiomes.THE_HALLOW_DESERT, Biomes.DESERT);
        DynamicBiomeUtils.registerPureBiome(ModBiomes.THE_CORRUPTION_DESERT, Biomes.DESERT);
        DynamicBiomeUtils.registerPureBiome(ModBiomes.THE_CRIMSON_DESERT, Biomes.DESERT);
        DynamicBiomeUtils.registerPureBiome(ModBiomes.THE_HALLOW_TUNDRA, Biomes.SNOWY_PLAINS);
        DynamicBiomeUtils.registerPureBiome(ModBiomes.THE_CORRUPTION_TUNDRA, Biomes.SNOWY_PLAINS);
        DynamicBiomeUtils.registerPureBiome(ModBiomes.THE_CRIMSON_TUNDRA, Biomes.SNOWY_PLAINS);
    }

    /// Confluence 的判定规则。返回 null 表示这个 section 保持原版群系。
    private static @Nullable Holder<Biome> judge(SectionContext context) {
        BlockCounts counts = context.counts();
        int sunflower = ModBlockCounters.SUNFLOWER.get(counts) * 64;
        int crimson = Math.max(0, ModBlockCounters.CRIMSON.get(counts) - sunflower);
        int corrupt = Math.max(0, ModBlockCounters.CORRUPT.get(counts) - sunflower);
        int hallow = ModBlockCounters.HALLOW.get(counts);
        int water = ModBlockCounters.WATER.get(counts);

        // (假设)同时存在400个猩红块和400个腐化块的时候，只要400个神圣块就能完全抵消，邪恶不会相加
        int evil = Math.max(crimson, corrupt);
        crimson -= hallow;
        corrupt -= hallow;
        hallow -= evil;

        int mushroom = ModBlockCounters.GLOWING_MUSHROOM.get(counts);
        Holder<Biome> current = context.currentBiome();
        Family previous = current.is(ModTags.Biomes.THE_CORRUPTION) ? Family.CORRUPTION
                : current.is(ModTags.Biomes.THE_CRIMSON) ? Family.CRIMSON
                : current.is(ModTags.Biomes.THE_HALLOW) ? Family.HALLOW
                : current.is(ModBiomes.GLOWING_MUSHROOM) ? Family.MUSHROOM : Family.NONE;
        Family winner = Family.NONE;
        int strongest = BIOME_THRESHOLD - 1;
        /// 相同强度使用固定次序；已有覆盖在退出阈值以上时享有替换差值。
        if (hallow > strongest) {
            winner = Family.HALLOW;
            strongest = hallow;
        }
        if (corrupt > strongest) {
            winner = Family.CORRUPTION;
            strongest = corrupt;
        }
        if (crimson > strongest) {
            winner = Family.CRIMSON;
            strongest = crimson;
        }
        if (mushroom > strongest) {
            winner = Family.MUSHROOM;
            strongest = mushroom;
        }
        int previousStrength = switch (previous) {
            case CORRUPTION -> corrupt;
            case CRIMSON -> crimson;
            case HALLOW -> hallow;
            case MUSHROOM -> mushroom;
            case NONE -> -1;
        };
        if (previous != Family.NONE && previousStrength >= EXIT_THRESHOLD
                && (winner == Family.NONE || strongest < previousStrength + REPLACEMENT_ADVANTAGE))
            winner = previous;
        if (winner == Family.NONE) return null;

        HolderLookup.RegistryLookup<Biome> lookup = context.lookup();
        if (winner == Family.MUSHROOM) return lookup.getOrThrow(ModBiomes.GLOWING_MUSHROOM);
        ResourceKey<Biome> normal;
        ResourceKey<Biome> desert;
        ResourceKey<Biome> snow;
        int sand;
        int ice;
        switch (winner) {
            case CORRUPTION -> {
                normal = ModBiomes.THE_CORRUPTION;
                desert = ModBiomes.THE_CORRUPTION_DESERT;
                snow = ModBiomes.THE_CORRUPTION_TUNDRA;
                sand = ModBlockCounters.CORRUPT_SAND.get(counts);
                ice = ModBlockCounters.CORRUPT_ICE.get(counts);
            }
            case CRIMSON -> {
                normal = ModBiomes.THE_CRIMSON;
                desert = ModBiomes.THE_CRIMSON_DESERT;
                snow = ModBiomes.THE_CRIMSON_TUNDRA;
                sand = ModBlockCounters.CRIMSON_SAND.get(counts);
                ice = ModBlockCounters.CRIMSON_ICE.get(counts);
            }
            default -> {
                normal = ModBiomes.THE_HALLOW;
                desert = ModBiomes.THE_HALLOW_DESERT;
                snow = ModBiomes.THE_HALLOW_TUNDRA;
                sand = ModBlockCounters.HALLOW_SAND.get(counts);
                ice = ModBlockCounters.HALLOW_ICE.get(counts);
            }
        }
        if (context.originalBiomeIs(biome -> biome.is(Biomes.DESERT))
                || sand - water >= (current.is(desert) ? EXIT_THRESHOLD : BIOME_THRESHOLD))
            return lookup.getOrThrow(desert);
        if (context.originalBiomeIs(biome -> biome.is(Biomes.SNOWY_PLAINS) || biome.is(Biomes.ICE_SPIKES) || biome.is(BiomeTags.SPAWNS_SNOW_FOXES))
                || ice >= (current.is(snow) ? EXIT_THRESHOLD : BIOME_THRESHOLD))
            return lookup.getOrThrow(snow);
        return lookup.getOrThrow(normal);
    }

    private enum Family {NONE, CORRUPTION, CRIMSON, HALLOW, MUSHROOM}
}
