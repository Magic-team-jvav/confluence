package org.confluence.mod.common.init;

import net.minecraft.Util;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.biome.*;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
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

    private static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registries.BIOME, Confluence.asResource(name));
    }

    public static void registerRegionAndSurface() {
        Regions.register(new TheCrimsonRegion(Confluence.asResource("the_crimson"), 1));
        Regions.register(new TheCorruptionRegion(Confluence.asResource("the_corruption"), 1));
        Regions.register(new GlowingMushroomRegion(Confluence.asResource("glowing_mushroom"), 1));
        Regions.register(new AshForestRegion(Confluence.asResource("ash_forest"), 1));
        Regions.register(new AshWastelandRegion(Confluence.asResource("ash_wasteland"), 1));
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MODID, SurfaceRuleData.makeConfluenceOverWorldRules());
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, MODID, SurfaceRuleData.makeConfluenceNetherRules());
        SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.BEFORE_BEDROCK, 0, SurfaceRuleData.makeMinecraftOverWorldRules());
    }

    public static void boostrap(BootstrapContext<Biome> context) {
        HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarver = context.lookup(Registries.CONFIGURED_CARVER);
        HolderGetter<PlacedFeature> placedFeature = context.lookup(Registries.PLACED_FEATURE);

        context.register(THE_CORRUPTION, new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(1)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .foliageColorOverride(-9030507)
                        .grassColorOverride(-9351806)
                        .skyColor(-10726554)
                        .fogColor(-10726554)
                        .waterColor(-12837542)
                        .waterFogColor(-11055776)
                        .build())
                .mobSpawnSettings(new MobSpawnSettings.Builder()
                        .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 22, 1, 1))
                        .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))
                        .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))
                        .build())
                .generationSettings(Util.make(new BiomeGenerationSettings.Builder(placedFeature, configuredWorldCarver), builder -> {
                    addDefaultGenerations(builder);
                    builder.addCarver(GenerationStep.Carving.AIR, ModCarvers.CONFIGURED_DEMONIC_CAVE_CARVER);
                    builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.PLACED_TREES_CORRUPTION);
                }).build())
                .build());
        context.register(THE_CORRUPTION_DESERT, new Biome.BiomeBuilder()
                .temperature(0)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        );
        context.register(THE_CORRUPTION_TUNDRA, new Biome.BiomeBuilder()
                .temperature(0)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        );
        context.register(THE_CRIMSON, new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.5F)
                .downfall(0.5F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .foliageColorOverride(-2282195)
                        .grassColorOverride(-4436402)
                        .skyColor(-8827314)
                        .fogColor(-8827314)
                        .waterColor(-7069664)
                        .waterFogColor(-7451572)
                        .build())
                .mobSpawnSettings(new MobSpawnSettings.Builder()
                        .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))
                        .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))
                        .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_KEMERA.get(), 60, 1, 1))
                        .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))
                        .build())
                .generationSettings(Util.make(new BiomeGenerationSettings.Builder(placedFeature, configuredWorldCarver), builder -> {
                    addDefaultGenerations(builder);
                    builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.PLACED_TREES_TR_CRIMSON);
                }).build())
                .build()
        );
        context.register(THE_CRIMSON_DESERT, new Biome.BiomeBuilder()
                .temperature(0)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        );
        context.register(THE_CRIMSON_TUNDRA, new Biome.BiomeBuilder()
                .temperature(0)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        );
        context.register(THE_HALLOW, new Biome.BiomeBuilder()
                .temperature(0)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        );
        context.register(THE_HALLOW_DESERT, new Biome.BiomeBuilder()
                .temperature(0)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        );
        context.register(THE_HALLOW_TUNDRA, new Biome.BiomeBuilder()
                .temperature(0)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        );
        context.register(ASH_FOREST, new Biome.BiomeBuilder()
                .temperature(0)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        );
        context.register(ASH_WASTELAND, new Biome.BiomeBuilder()
                .temperature(0)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        );
        context.register(GLOWING_MUSHROOM, new Biome.BiomeBuilder()
                .temperature(0)
                .downfall(0)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0)
                        .waterColor(0)
                        .waterFogColor(0)
                        .skyColor(0)
                        .build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY)
                .generationSettings(BiomeGenerationSettings.EMPTY)
                .build()
        );
    }

    private static void addDefaultGenerations(BiomeGenerationSettings.Builder builder) {
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND);
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
        builder.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultOres(builder);
        BiomeDefaultFeatures.addDefaultSoftDisks(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
    }
}
