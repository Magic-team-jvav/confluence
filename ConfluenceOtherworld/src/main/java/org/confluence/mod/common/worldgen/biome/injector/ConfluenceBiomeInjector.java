package org.confluence.mod.common.worldgen.biome.injector;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.BannedBiomeNoiseBasedChunkGenerator;
import org.confluence.mod.common.worldgen.TheEndBiomeHolder;
import org.confluence.mod.common.worldgen.biome.*;
import org.confluence.mod.mixed.IMultiNoiseBiomeSource;
import org.confluence.mod.mixed.INoiseBasedChunkGenerator;
import org.confluence.mod.util.OverworldUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/// Confluence Biome Injector —— 自研群系添加器的公共入口，取代 TerraBlender。
///
/// 用法：
///
/// 1. {@link #bootstrap()} 在 `FMLCommonSetupEvent` 里登记区域；
/// 2. {@link #install(MinecraftServer)} 在 `ServerAboutToStartEvent` 里按 `LevelStem`
///    建表并挂到对应的 `BiomeSource` 实例上；
/// 3. 地表规则用 {@link #addSurfaceRules} 登记，在 `NoiseBasedChunkGenerator#buildSurface`
///    与 `#applyCarvers` 处按维度类别拼到真正的原版规则源上。
///
/// 与 TerraBlender 的对照见各子类文档；关键差异是不共享全局索引、不复制原版规则、
/// 不使用占位群系、不往共享的数据包注册表对象上挂可变状态。
public final class ConfluenceBiomeInjector {
    private static final List<BiomeRegion> OVERWORLD_REGIONS = new ArrayList<>();
    private static final List<BiomeRegion> NETHER_REGIONS = new ArrayList<>();
    private static boolean bootstrapped;

    private ConfluenceBiomeInjector() {}

    /// 登记本模组的区域。区域顺序即区域表顺序，也决定同权重时的索引，必须保持稳定。
    public static synchronized void bootstrap() {
        if (bootstrapped) return;
        bootstrapped = true;

        // 邪恶群系：两个区域始终登记，索引与布局完全一致；
        // 具体出现哪一个由 install() 按世界旗标做群系键重映射决定。
        OVERWORLD_REGIONS.add(new TheCorruptionRegion());
        OVERWORLD_REGIONS.add(new TheCrimsonRegion());
        OVERWORLD_REGIONS.add(new GlowingMushroomRegion());

        NETHER_REGIONS.add(new AshForestRegion());
        NETHER_REGIONS.add(new AshWastelandRegion());

        Confluence.LOGGER.info("Registered {} overworld biome regions and {} nether biome regions", OVERWORLD_REGIONS.size(), NETHER_REGIONS.size());
    }

    public static List<BiomeRegion> regionsOf(BiomeRegionType type) {
        bootstrap();
        return switch (type) {
            case OVERWORLD -> OVERWORLD_REGIONS;
            case NETHER -> NETHER_REGIONS;
        };
    }

    /// 在 `ServerAboutToStartEvent` 里调用。
    ///
    /// 时序依据：Forge 在 `DedicatedServer#initServer` / `IntegratedServer#initServer` 里
    /// 于 `loadLevel()` **之前**触发该事件，而 `possibleBiomes()` 的首次求值发生在
    /// `loadLevel() -> createLevels()` 之后，因此这里改 `collectPossibleBiomes` 一定生效。
    public static void install(MinecraftServer server) {
        bootstrap();
        RegistryAccess access = server.registryAccess();
        Registry<Biome> biomeRegistry = access.registryOrThrow(Registries.BIOME);
        long seed = server.getWorldData().worldGenOptions().seed();
        // 「不是蜜蜂」世界里不做邪恶群系互换，保留腐化与猩红同时存在 ——
        // 这是替换 TerraBlender 之前的既有行为（原 OverworldUtils#replaceBiome 只在 else 分支里取互换对）。
        boolean allowEvilRemap = !ModSecretSeeds.NOT_THE_BEES.match(server);
        Map<BiomeSource, BiomeSourceHandler> handlers = new Reference2ObjectOpenHashMap<>();

        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> stemEntry : access.registryOrThrow(Registries.LEVEL_STEM).entrySet()) {
            LevelStem stem = stemEntry.getValue();
            Holder<DimensionType> dimensionType = stem.type();

            // 地表规则：按维度类别把本模组规则拼到真正的原版规则源上，再挂到生成器实例。
            // 注入点（NoiseBasedChunkGeneratorMixin）只做替换，不需要自己判断维度。
            if (stem.generator() instanceof NoiseBasedChunkGenerator noiseGenerator) {
                SurfaceRuleRegistry.Category category = SurfaceRuleRegistry.categoryOf(dimensionType);
                if (category != null) {
                    SurfaceRules.RuleSource vanilla = noiseGenerator.generatorSettings().value().surfaceRule();
                    INoiseBasedChunkGenerator.of(noiseGenerator).confluence$setSurfaceRules(SurfaceRuleRegistry.compose(category, vanilla));
                }
            }

            BiomeSource source = stem.generator().getBiomeSource();
            if (source instanceof MultiNoiseBiomeSource multiNoise) {
                BiomeRegionType type = BiomeRegionType.byDimensionType(dimensionType);
                if (type == null) {
                    continue;
                }
                handlers.put(source, createRegionHandler(type, seed, biomeRegistry, multiNoise, stem.generator(), allowEvilRemap));
            } else if (source instanceof TheEndBiomeSource) {
                handlers.put(source, TheEndBiomeHolder.handler());
            }
        }

        BiomeSourceInjector.setHandlers(handlers);
    }

    public static void uninstall() {
        BiomeSourceInjector.uninstall();
    }

    private static BiomeSourceHandler createRegionHandler(BiomeRegionType type, long seed, Registry<Biome> biomeRegistry, MultiNoiseBiomeSource source, ChunkGenerator generator, boolean allowEvilRemap) {
        ResourceKey<Biome> from = null;
        ResourceKey<Biome> to = null;

        if (generator instanceof BannedBiomeNoiseBasedChunkGenerator banned) {
            // 自建世界预设里显式声明了「禁止哪个、换成哪个」，直接用它，
            // 不需要再靠 BannedBiomeMultiNoiseBiomeSource 在查询时换。
            from = banned.bannedBiome;
            to = banned.targetBiome;
        } else if (type == BiomeRegionType.OVERWORLD && allowEvilRemap) {
            Pair<Holder<Biome>, Holder<Biome>> pair = IMultiNoiseBiomeSource.of(source).confluence$getBiomePair();
            if (pair != null && pair.getFirst() != null && pair.getSecond() != null) {
                from = pair.getFirst().unwrapKey().orElse(null);
                to = pair.getSecond().unwrapKey().orElse(null);
            }
        }

        UnaryOperator<ResourceKey<Biome>> remap = UnaryOperator.identity();
        if (from != null && to != null) {
            ResourceKey<Biome> unwanted = from;
            ResourceKey<Biome> wanted = to;
            remap = key -> key.equals(unwanted) ? wanted : key;
            // 一个世界只会存在两种邪恶群系中的一种（醉酒世界与「不是蜜蜂」例外），
            // 所以「被换掉」的那个用 /locate biome 一定找不到 —— 这是设计如此，不是 bug。
        }

        RegionBiomeHandler.PostProcessor post = type == BiomeRegionType.OVERWORLD ? new OverworldPostProcessor(source) : null;
        return new RegionBiomeHandler(BiomeRegionTable.build(type, seed, biomeRegistry, regionsOf(type), remap), post);
    }

    // ── 地表规则 ────────────────────────────────────────────────────────────

    public static void addSurfaceRules(SurfaceRuleRegistry.Category category, SurfaceRuleRegistry.Stage stage, int priority, SurfaceRules.RuleSource source) {
        addSurfaceRules(category, Confluence.MODID, stage, priority, source);
    }

    public static void addSurfaceRules(SurfaceRuleRegistry.Category category, String owner, SurfaceRuleRegistry.Stage stage, int priority, SurfaceRules.RuleSource source) {
        SurfaceRuleRegistry.add(category, owner, stage, priority, source);
    }

    public static void removeSurfaceRules(SurfaceRuleRegistry.Category category, String owner) {
        SurfaceRuleRegistry.remove(category, owner);
    }

    /// 主世界的收尾替换：「不是蜜蜂」密种把非特定群系换成丛林群系，以及出生点附近的
    /// 邪恶群系保护。原先这些逻辑散在 `OverworldUtils#replaceBiome` 与 TB 的注入里。
    private static final class OverworldPostProcessor implements RegionBiomeHandler.PostProcessor {
        private final MultiNoiseBiomeSource source;
        private volatile List<Holder<Biome>> jungle;

        OverworldPostProcessor(MultiNoiseBiomeSource source) {
            this.source = source;
        }

        @Override
        public Holder<Biome> apply(int x, int y, int z, Holder<Biome> biome) {
            return OverworldUtils.postProcess(x, y, z, biome, this::jungle);
        }

        private List<Holder<Biome>> jungle() {
            List<Holder<Biome>> cached = this.jungle;
            if (cached == null) {
                cached = source.possibleBiomes().stream().filter(OverworldUtils::isJungle).toList();
                this.jungle = cached;
            }
            return cached;
        }
    }
}
