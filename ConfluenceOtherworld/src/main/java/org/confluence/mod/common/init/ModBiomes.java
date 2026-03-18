package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.biome.*;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

import static org.confluence.mod.Confluence.MODID;

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

    private static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registries.BIOME, Confluence.asResource(name));
    }

    public static void registerRegionAndSurface() {
        Regions.register(new TheCrimsonRegion(Confluence.asResource("the_crimson"), 1));
        Regions.register(new TheCorruptionRegion(Confluence.asResource("the_corruption"), 1));
        Regions.register(new GlowingMushroomRegion(Confluence.asResource("glowing_mushroom"), 2));
        Regions.register(new AshForestRegion(Confluence.asResource("ash_forest"), 1));
        Regions.register(new AshWastelandRegion(Confluence.asResource("ash_wasteland"), 1));
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MODID, SurfaceRuleData.makeConfluenceOverWorldRules());
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, MODID, SurfaceRuleData.makeConfluenceNetherRules());
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.END, MODID, SurfaceRuleData.makeConfluenceEndRules());
        SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 0, SurfaceRuleData.makeMinecraftOverWorldRules());
    }
}
