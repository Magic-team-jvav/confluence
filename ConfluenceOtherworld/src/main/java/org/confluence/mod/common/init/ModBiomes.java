package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.biome.SurfaceRuleData;
import org.confluence.mod.common.worldgen.biome.injector.ConfluenceBiomeInjector;
import org.confluence.mod.common.worldgen.biome.injector.SurfaceRuleRegistry;

public final class ModBiomes {
    public static final ResourceKey<Biome> THE_CORRUPTION = register("the_corruption");
    public static final ResourceKey<Biome> THE_CORRUPTION_DESERT = register("the_corruption_desert");
    public static final ResourceKey<Biome> THE_CORRUPTION_TUNDRA = register("the_corruption_tundra");
    public static final ResourceKey<Biome> THE_CRIMSON = register("the_crimson");
    public static final ResourceKey<Biome> THE_CRIMSON_DESERT = register("the_crimson_desert");
    public static final ResourceKey<Biome> THE_CRIMSON_TUNDRA = register("the_crimson_tundra");
    public static final ResourceKey<Biome> THE_HALLOW = register("the_hallow");
    public static final ResourceKey<Biome> THE_HALLOW_DESERT = register("the_hallow_desert");
    public static final ResourceKey<Biome> THE_HALLOW_TUNDRA = register("the_hallow_tundra");
    public static final ResourceKey<Biome> ASH_FOREST = register("ash_forest");
    public static final ResourceKey<Biome> ASH_WASTELAND = register("ash_wasteland");
    public static final ResourceKey<Biome> GLOWING_MUSHROOM = register("glowing_mushroom");
    public static final ResourceKey<Biome> CHORUS_FOREST = register("chorus_forest");
    public static final ResourceKey<Biome> CHORUS_PLAINS = register("chorus_plains");
    public static final ResourceKey<Biome> INVERSE_FOREST = register("inverse_forest");
    public static final ResourceKey<Biome> INVERSE_PLAINS = register("inverse_plains");
    public static final ResourceKey<Biome> MOONBLIGHT_FOREST = register("moonblight_forest");
    public static final ResourceKey<Biome> MOONBLIGHT_PLAINS = register("moonblight_plains");
    public static final ResourceKey<Biome> MOONLIT_DRY_SEA = register("moonlit_dry_sea");
    public static final ResourceKey<Biome> DARK_MOON_FLATS = register("dark_moon_flats");

    private static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registries.BIOME, Confluence.asResource(name));
    }

    /// 登记群系区域与地表规则。在 `FMLCommonSetupEvent` 里调用。
    ///
    /// 区域本身只在这里登记；真正的「按世界种子建表、按 LevelStem 挂到 BiomeSource 上」
    /// 在 `ServerAboutToStartEvent`（{@link ConfluenceBiomeInjector#install}）里做。
    ///
    /// 地表规则的 {@link SurfaceRuleRegistry.Stage#PREPEND} 等价于原 TerraBlender 的
    /// `BEFORE_BEDROCK`：在 `sequence` 里排在真正的原版规则之前，因此我们的规则优先。
    /// 这里不再需要 TerraBlender 那份复制出来的原版地表规则副本。
    ///
    /// 两条主世界规则用**不同的 owner**。去重键是 `(owner, stage, priority)`，
    /// 但分开命名能让启动日志一眼看出「两条都在」，也更不容易踩到「后一条删掉前一条」。
    public static final String SURFACE_OWNER_BIOMES = "confluence:biomes";
    public static final String SURFACE_OWNER_VANILLA = "confluence:vanilla_overrides";

    public static void registerRegionAndSurface() {
        ConfluenceBiomeInjector.bootstrap();

        // 本模组群系自己的地表规则。全部由 SurfaceRules#isBiome 门控，只会作用在对应群系上。
        ConfluenceBiomeInjector.addSurfaceRules(SurfaceRuleRegistry.Category.OVERWORLD, SURFACE_OWNER_BIOMES, SurfaceRuleRegistry.Stage.PREPEND, 10, SurfaceRuleData.makeConfluenceOverWorldRules());
        ConfluenceBiomeInjector.addSurfaceRules(SurfaceRuleRegistry.Category.NETHER, SURFACE_OWNER_BIOMES, SurfaceRuleRegistry.Stage.PREPEND, 10, SurfaceRuleData.makeConfluenceNetherRules());
        ConfluenceBiomeInjector.addSurfaceRules(SurfaceRuleRegistry.Category.END, SURFACE_OWNER_BIOMES, SurfaceRuleRegistry.Stage.PREPEND, 10, SurfaceRuleData.makeConfluenceEndRules());

        // 改写原版群系的地表（海底、丛林、沙漠、恶地、雪原）。
        // 同样前置：原实现在 TerraBlender 里是 BEFORE_BEDROCK + priority 0，语义一致。
        ConfluenceBiomeInjector.addSurfaceRules(SurfaceRuleRegistry.Category.OVERWORLD, SURFACE_OWNER_VANILLA, SurfaceRuleRegistry.Stage.PREPEND, 0, SurfaceRuleData.makeMinecraftOverWorldRules());
    }
}
