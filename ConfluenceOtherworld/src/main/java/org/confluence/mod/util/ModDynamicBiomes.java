package org.confluence.mod.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.confluence.lib.common.worldgen.biome.BlockCounts;
import org.confluence.lib.common.worldgen.biome.DynamicBiomeUtils;
import org.confluence.lib.common.worldgen.biome.SectionContext;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;

/// Confluence 的动态群系规则与优先级。
///
/// 计数机制与传播逻辑在 magiclib（{@link DynamicBiomeUtils}），这里只注册 Confluence 自己的语义：
/// 哪些方块算腐化/猩红/神圣、阈值多少、哪个群系优先。
public final class ModDynamicBiomes {
    /// 判定阈值：4096 个方块里有多少个才算这个群系
    public static final int BIOME_THRESHOLD = 256;

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

        HolderLookup.RegistryLookup<Biome> lookup = context.lookup();
        if (corrupt >= BIOME_THRESHOLD && corrupt >= crimson) {
            if (ModBlockCounters.CORRUPT_SAND.get(counts) - water >= BIOME_THRESHOLD || context.originalBiomeIs(biome -> biome.is(Biomes.DESERT))) {
                return lookup.getOrThrow(ModBiomes.THE_CORRUPTION_DESERT);
            } else if (ModBlockCounters.CORRUPT_ICE.get(counts) >= BIOME_THRESHOLD || context.originalBiomeIs(biome -> biome.is(BiomeTags.SPAWNS_SNOW_FOXES))) {
                return lookup.getOrThrow(ModBiomes.THE_CORRUPTION_TUNDRA);
            }
            return lookup.getOrThrow(ModBiomes.THE_CORRUPTION);
        } else if (crimson >= BIOME_THRESHOLD) {
            if (ModBlockCounters.CRIMSON_SAND.get(counts) - water >= BIOME_THRESHOLD || context.originalBiomeIs(biome -> biome.is(Biomes.DESERT))) {
                return lookup.getOrThrow(ModBiomes.THE_CRIMSON_DESERT);
            } else if (ModBlockCounters.CRIMSON_ICE.get(counts) >= BIOME_THRESHOLD || context.originalBiomeIs(biome -> biome.is(BiomeTags.SPAWNS_SNOW_FOXES))) {
                return lookup.getOrThrow(ModBiomes.THE_CRIMSON_TUNDRA);
            }
            return lookup.getOrThrow(ModBiomes.THE_CRIMSON);
        } else if (hallow >= BIOME_THRESHOLD) {
            if (ModBlockCounters.HALLOW_SAND.get(counts) - water >= BIOME_THRESHOLD || context.originalBiomeIs(biome -> biome.is(Biomes.DESERT))) {
                return lookup.getOrThrow(ModBiomes.THE_HALLOW_DESERT);
            } else if (ModBlockCounters.HALLOW_ICE.get(counts) >= BIOME_THRESHOLD || context.originalBiomeIs(biome -> biome.is(BiomeTags.SPAWNS_SNOW_FOXES))) {
                return lookup.getOrThrow(ModBiomes.THE_HALLOW_TUNDRA);
            }
            return lookup.getOrThrow(ModBiomes.THE_HALLOW);
        } else if (ModBlockCounters.GLOWING_MUSHROOM.get(counts) >= BIOME_THRESHOLD) {
            return lookup.getOrThrow(ModBiomes.GLOWING_MUSHROOM);
        }
        return null;
    }

    // 迷你生物群系标记见 ModMiniBiomes（同样基于这套计数器）
}
