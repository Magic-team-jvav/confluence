package org.confluence.mod.common.data.gen;

import net.minecraft.Util;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.enchantment.SummonItemEffect;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.Arrays;

public class ModDataProvider {
    public static final RegistrySetBuilder DATA_BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, ModDamageTypes::bootstrap)
            .add(Registries.BIOME, Biomes::boostrap)
            .add(Registries.STRUCTURE, ModStructures::boostrap)
            .add(Registries.ENCHANTMENT, Enchantments::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, PlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifierz::bootstrap);

    public static class ConfiguredFeatures {
        private static final ResourceKey<ConfiguredFeature<?, ?>> AMBER_ORE = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "amber_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> AMETHYST_ORE = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "amethyst_ore");

        public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
            ore(context, AMBER_ORE, 8, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.DESERT_FOSSIL_REPLACEMENT), OreBlocks.AMBER_ORE.get().defaultBlockState()));
            ore(context, AMETHYST_ORE, 8,
                    OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), OreBlocks.AMETHYST_ORE.get().defaultBlockState()),
                    OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), OreBlocks.DEEPSLATE_AMETHYST_ORE.get().defaultBlockState())
            );
        }

        private static void ore(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, int size, OreConfiguration.TargetBlockState... targets) {
            context.register(key, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(Arrays.stream(targets).toList(), size)));
        }
    }

    public static class PlacedFeatures {
        private static final ResourceKey<PlacedFeature> AMBER_ORE = Confluence.asResourceKey(Registries.PLACED_FEATURE, "amber_ore");
        private static final ResourceKey<PlacedFeature> AMETHYST_ORE = Confluence.asResourceKey(Registries.PLACED_FEATURE, "amethyst_ore");

        public static void bootstrap(BootstrapContext<PlacedFeature> context) {
            HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
            register(context, AMBER_ORE, configured.getOrThrow(ConfiguredFeatures.AMBER_ORE),
                    CountPlacement.of(1),
                    InSquarePlacement.spread(),
                    HeightRangePlacement.triangle(
                            VerticalAnchor.absolute(-52),
                            VerticalAnchor.absolute(10)
                    ),
                    BiomeFilter.biome()
            );
            register(context, AMETHYST_ORE, configured.getOrThrow(ConfiguredFeatures.AMETHYST_ORE),
                    CountPlacement.of(1),
                    InSquarePlacement.spread(),
                    HeightRangePlacement.triangle(
                            VerticalAnchor.absolute(-52),
                            VerticalAnchor.absolute(10)
                    ),
                    BiomeFilter.biome()
            );
        }

        private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature, PlacementModifier... modifiers) {
            context.register(key, new PlacedFeature(feature, Arrays.stream(modifiers).toList()));
        }
    }

    public static class BiomeModifierz {
        public static void bootstrap(BootstrapContext<BiomeModifier> context) {
            HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
            HolderGetter<PlacedFeature> placedFeature = context.lookup(Registries.PLACED_FEATURE);
            addFeatures(context, "desert", biome.getOrThrow(Tags.Biomes.IS_DESERT), HolderSet.direct(placedFeature.getOrThrow(PlacedFeatures.AMBER_ORE)), GenerationStep.Decoration.UNDERGROUND_ORES);
        }

        private static void addFeatures(BootstrapContext<BiomeModifier> context, String path, HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) {
            context.register(Confluence.asResourceKey(NeoForgeRegistries.Keys.BIOME_MODIFIERS, path), new BiomeModifiers.AddFeaturesBiomeModifier(biomes, features, step));
        }
    }

    public static class Biomes {
        public static void boostrap(BootstrapContext<Biome> context) {
            HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarver = context.lookup(Registries.CONFIGURED_CARVER);
            HolderGetter<PlacedFeature> placedFeature = context.lookup(Registries.PLACED_FEATURE);

            context.register(ModBiomes.THE_CORRUPTION, new Biome.BiomeBuilder()
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
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.TREES_CORRUPTION);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.CORRUPT_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.VILE_MUSHROOM);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.THE_CORRUPTION_TREE_CHECKED_0);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.THE_CORRUPTION_TREE_CHECKED_1);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.THE_CORRUPTION_TREE_CHECKED_2);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.CORRUPT_DROOPING_VINE);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ModFeatures.Placed.CORRUPTION_POT);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ModFeatures.Placed.DEMON_ALTAR);
                    }).build())
                    .build());
            context.register(ModBiomes.THE_CORRUPTION_DESERT, new Biome.BiomeBuilder()
                    .temperature(1)
                    .downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder()
                            .foliageColorOverride(-10271373)
                            .grassColorOverride(-10207626)
                            .skyColor(-8161900)
                            .fogColor(-8161900)
                            .waterColor(-11061641)
                            .waterFogColor(-9083007)
                            .build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 22, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_CORRUPTION_TUNDRA, new Biome.BiomeBuilder()
                    .temperature(0)
                    .downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder()
                            .foliageColorOverride(-9939839)
                            .grassColorOverride(-9415030)
                            .skyColor(-8948332)
                            .fogColor(-8948332)
                            .waterColor(-9876078)
                            .waterFogColor(-9869439)
                            .build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 22, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))
                            .build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_CRIMSON, new Biome.BiomeBuilder()
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
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ModFeatures.Placed.CRIMSON_ALTAR);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ModFeatures.Placed.CRIMSON_POT);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.TREES_CRIMSON);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.CRIMSON_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.CRIMSON_TREE_CHECKED_0);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.CRIMSON_TREE_CHECKED_1);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.CRIMSON_DROOPING_VINE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.VICIOUS_MUSHROOM);
                    }).build())
                    .build()
            );
            context.register(ModBiomes.THE_CRIMSON_DESERT, new Biome.BiomeBuilder()
                    .temperature(0)
                    .downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder()
                            .foliageColorOverride(-6669252)
                            .grassColorOverride(-7915464)
                            .skyColor(-6331292)
                            .fogColor(-6331292)
                            .waterColor(-5294281)
                            .waterFogColor(-5674390)
                            .build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_KEMERA.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_CRIMSON_TUNDRA, new Biome.BiomeBuilder()
                    .temperature(0.5f)
                    .downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder()
                            .foliageColorOverride(-6664389)
                            .grassColorOverride(-7915464)
                            .skyColor(-6327708)
                            .fogColor(-6327708)
                            .waterColor(-5286090)
                            .waterFogColor(-5671318)
                            .build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_KEMERA.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_HALLOW, new Biome.BiomeBuilder()
                    .temperature(0.5f)
                    .downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder()
                            .foliageColorOverride(-16711703)
                            .grassColorOverride(-3999757)
                            .fogColor(-3346188)
                            .waterColor(-1554953)
                            .waterFogColor(-3345167)
                            .skyColor(-3346188)
                            .build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_HALLOW_DESERT, new Biome.BiomeBuilder()
                    .temperature(0)
                    .downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder()
                            .foliageColorOverride(-11084592)
                            .grassColorOverride(-4005129)
                            .fogColor(-3347468)
                            .waterColor(-3347468)
                            .waterFogColor(-1554953)
                            .skyColor(-4592650)
                            .build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_HALLOW_TUNDRA, new Biome.BiomeBuilder()
                    .temperature(0.5f)
                    .downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder()
                            .foliageColorOverride(-11084592)
                            .grassColorOverride(-4005129)
                            .fogColor(-3347468)
                            .waterColor(-3347468)
                            .waterFogColor(-1554953)
                            .skyColor(-4592650)
                            .build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.ASH_FOREST, new Biome.BiomeBuilder()
                    .hasPrecipitation(false)
                    .temperature(2)
                    .downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder()
                            .foliageColorOverride(10387789)
                            .grassColorOverride(9470285)
                            .fogColor(-10541025)
                            .waterColor(-10541025)
                            .waterFogColor(4159204)
                            .skyColor(-4592650)
                            .build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(Util.make(new BiomeGenerationSettings.Builder(placedFeature, configuredWorldCarver), builder -> {
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.FIREBLOSSOM);
                        builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, ModFeatures.Placed.ASH_HELLSTONE_GENERATES);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.ASH_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.ASH_TREE);
                    }).build())
                    .build()
            );
            context.register(ModBiomes.ASH_WASTELAND, new Biome.BiomeBuilder()
                    .hasPrecipitation(false)
                    .temperature(2)
                    .downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder()
                            .foliageColorOverride(10387789)
                            .grassColorOverride(9470285)
                            .fogColor(-10541025)
                            .waterColor(-10541025)
                            .waterFogColor(4159204)
                            .skyColor(-4592650)
                            .build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(Util.make(new BiomeGenerationSettings.Builder(placedFeature, configuredWorldCarver), builder -> {
                        builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, ModFeatures.Placed.ASH_HELLSTONE_GENERATES);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.FIREBLOSSOM);
                    }).build())
                    .build()
            );
            context.register(ModBiomes.GLOWING_MUSHROOM, new Biome.BiomeBuilder()
                    .temperature(0.5f)
                    .downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder()
                            .fogColor(12638463)
                            .waterColor(4159204)
                            .waterFogColor(329011)
                            .skyColor(8103167)
                            .build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_BAT.get(), 30, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_SKELETON.get(), 30, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_ZOMBIE.get(), 30, 1, 2))
                            .build())
                    .generationSettings(Util.make(new BiomeGenerationSettings.Builder(placedFeature, configuredWorldCarver), builder -> {
                        addDefaultGenerations(builder);
                        builder.addCarver(GenerationStep.Carving.AIR, ModCarvers.CONFIGURED_GLOWING_MUSHROOM_CAVE_CARVER);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.GLOWING_MUSHROOM);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.GLOWING_MUSHROOM_LIFE_CRYSTAL);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.GLOWING_MUSHROOM_TREE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.GLOWING_MUSHROOM_VINE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.GLOWING_MUSHROOM_CATTAILS);
                    }).build())
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

    public static class Enchantments {
        public static void bootstrap(BootstrapContext<Enchantment> context) {
            HolderGetter<Item> item = context.lookup(Registries.ITEM);
            HolderGetter<Enchantment> enchantment = context.lookup(Registries.ENCHANTMENT);
            register(context, ModEnchantments.MANA_REGENERATION, Enchantment.enchantment(
                            Enchantment.definition(
                                    new OrHolderSet<>(item.getOrThrow(Tags.Items.ARMORS), item.getOrThrow(ModTags.Items.MANA_WEAPON)),
                                    2,
                                    3,
                                    Enchantment.dynamicCost(25, 25),
                                    Enchantment.dynamicCost(75, 25),
                                    4,
                                    EquipmentSlotGroup.ARMOR, EquipmentSlotGroup.MAINHAND
                            ))
                    .exclusiveWith(enchantment.getOrThrow(ModTags.Enchantments.MANA_IO_EXCLUSIVE))
                    .withEffect(ModEnchantments.EffectComponentTypes.MANA_REGENERATION.get(), new AddValue(LevelBasedValue.perLevel(0.1F)))
            );
            register(context, ModEnchantments.EFFICIENT_MAGIC, Enchantment.enchantment(
                            Enchantment.definition(
                                    item.getOrThrow(ModTags.Items.MANA_WEAPON),
                                    2,
                                    1,
                                    Enchantment.dynamicCost(25, 25),
                                    Enchantment.dynamicCost(75, 25),
                                    4,
                                    EquipmentSlotGroup.MAINHAND
                            ))
                    .exclusiveWith(enchantment.getOrThrow(ModTags.Enchantments.MANA_IO_EXCLUSIVE))
                    .withEffect(ModEnchantments.EffectComponentTypes.EFFICIENT_MAGIC.get())
            );
            register(context, ModEnchantments.MANA_MENDING, Enchantment.enchantment(
                            Enchantment.definition(
                                    item.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                                    2,
                                    3,
                                    Enchantment.dynamicCost(25, 25),
                                    Enchantment.dynamicCost(75, 25),
                                    4,
                                    EquipmentSlotGroup.ANY
                            ))
                    .exclusiveWith(enchantment.getOrThrow(ModTags.Enchantments.MENDING_EXCLUSIVE))
                    .withEffect(ModEnchantments.EffectComponentTypes.MANA_MENDING.get(), new AddValue(LevelBasedValue.perLevel(-2)))
            );
            register(context, ModEnchantments.CELESTIAL_ABSORPTION, Enchantment.enchantment(
                            Enchantment.definition(
                                    item.getOrThrow(ModTags.Items.MANA_WEAPON),
                                    2,
                                    2,
                                    Enchantment.dynamicCost(25, 25),
                                    Enchantment.dynamicCost(75, 25),
                                    4,
                                    EquipmentSlotGroup.MAINHAND
                            ))
                    .exclusiveWith(enchantment.getOrThrow(ModTags.Enchantments.MANA_AFFECTIVE_EXCLUSIVE))
                    .withEffect(
                            ModEnchantments.EffectComponentTypes.ATTACK_DROPS_MANA.get(),
                            EnchantmentTarget.ATTACKER,
                            EnchantmentTarget.VICTIM,
                            new SummonItemEffect(item.getOrThrow(ModItems.STAR.getKey()), LevelBasedValue.perLevel(0.1F)),
                            LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity())
                                    .and(DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(Tags.DamageTypes.IS_MAGIC))))
                    )
            );
            register(context, ModEnchantments.SOOTHED_MANA, Enchantment.enchantment(
                            Enchantment.definition(
                                    item.getOrThrow(ModTags.Items.MANA_WEAPON),
                                    2,
                                    2,
                                    Enchantment.dynamicCost(25, 25),
                                    Enchantment.dynamicCost(75, 25),
                                    4,
                                    EquipmentSlotGroup.MAINHAND
                            ))
                    .exclusiveWith(enchantment.getOrThrow(ModTags.Enchantments.MANA_AFFECTIVE_EXCLUSIVE))
                    .withEffect(ModEnchantments.EffectComponentTypes.MANA_SICKNESS_DURATION_REDUCE.get(), new AddValue(LevelBasedValue.perLevel(-0.1F)))
            );
            register(context, ModEnchantments.ARCANE_PROTECTION, Enchantment.enchantment(
                            Enchantment.definition(
                                    item.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                    2,
                                    4,
                                    Enchantment.dynamicCost(25, 25),
                                    Enchantment.dynamicCost(75, 25),
                                    4,
                                    EquipmentSlotGroup.HEAD, EquipmentSlotGroup.CHEST, EquipmentSlotGroup.LEGS, EquipmentSlotGroup.FEET
                            ))
                    .exclusiveWith(enchantment.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
                    .withEffect(
                            ModEnchantments.EffectComponentTypes.MANA_PROTECTION.get(),
                            new AddValue(LevelBasedValue.perLevel(0.05F)),
                            DamageSourceCondition.hasDamageSource(
                                    DamageSourcePredicate.Builder.damageType().tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))
                            )
                    )
            );
        }

        private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
            context.register(key, builder.build(key.location()));
        }
    }
}
