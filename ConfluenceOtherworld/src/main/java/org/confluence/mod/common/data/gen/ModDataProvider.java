package org.confluence.mod.common.data.gen;

import net.minecraft.Util;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.*;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformFloat;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.PalmLeaves;
import org.confluence.mod.common.block.natural.StepRevealingBlock;
import org.confluence.mod.common.enchantment.SummonItemEffect;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.worldgen.SecretFlagPlacement;
import org.confluence.mod.common.worldgen.carver.DemonicCaveCarver;
import org.confluence.mod.common.worldgen.carver.GlowingMushroomCaveCarver;
import org.confluence.mod.common.worldgen.feature.*;
import org.confluence.mod.common.worldgen.structure.DungeonStructure;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class ModDataProvider {
    public static final RegistrySetBuilder DATA_BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, ModDamageTypes::bootstrap) // todo
            .add(Registries.BIOME, Biomes::boostrap)
            .add(Registries.STRUCTURE, Structures::boostrap)
            .add(Registries.ENCHANTMENT, Enchantments::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, PlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifierz::bootstrap)
            .add(Registries.CONFIGURED_CARVER, ConfiguredWorldCarvers::bootstrap);

    private static class ConfiguredFeatures {
        private static final ResourceKey<ConfiguredFeature<?, ?>> AMBER_ORE = key("amber_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> AMETHYST_ORE = key("amethyst_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> ASH_HELLSTONE = key("ash_hellstone");
        private static final ResourceKey<ConfiguredFeature<?, ?>> COLD_CRYSTAL_ORE = key("cold_crystal_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CRIMTANE_ORE = key("crimtane_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_ADAMANTITE_ORE_STEP_0 = key("deepslate_adamantite_ore_step_0");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_ADAMANTITE_ORE_STEP_1 = key("deepslate_adamantite_ore_step_1");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_ADAMANTITE_ORE_STEP_2 = key("deepslate_adamantite_ore_step_2");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_COBALT_ORE_STEP_0 = key("deepslate_cobalt_ore_step_0");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_COBALT_ORE_STEP_1 = key("deepslate_cobalt_ore_step_1");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_COBALT_ORE_STEP_2 = key("deepslate_cobalt_ore_step_2");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_MYTHRIL_ORE_STEP_0 = key("deepslate_mythril_ore_step_0");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_MYTHRIL_ORE_STEP_1 = key("deepslate_mythril_ore_step_1");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_MYTHRIL_ORE_STEP_2 = key("deepslate_mythril_ore_step_2");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_ORICHALCUM_ORE_STEP_0 = key("deepslate_orichalcum_ore_step_0");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_ORICHALCUM_ORE_STEP_1 = key("deepslate_orichalcum_ore_step_1");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_ORICHALCUM_ORE_STEP_2 = key("deepslate_orichalcum_ore_step_2");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_PALLADIUM_ORE_STEP_0 = key("deepslate_palladium_ore_step_0");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_PALLADIUM_ORE_STEP_1 = key("deepslate_palladium_ore_step_1");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_PALLADIUM_ORE_STEP_2 = key("deepslate_palladium_ore_step_2");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_TITANIUM_ORE_STEP_0 = key("deepslate_titanium_ore_step_0");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_TITANIUM_ORE_STEP_1 = key("deepslate_titanium_ore_step_1");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEEPSLATE_TITANIUM_ORE_STEP_2 = key("deepslate_titanium_ore_step_2");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEMONITE_ORE = key("demonite_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> GELSTONE_ORE = key("gelstone_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> JADE_ORE = key("jade_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> LEAD_ORE = key("lead_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> PLATINUM_ORE = key("platinum_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> RUBY_ORE = key("ruby_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SAPPHIRE_ORE = key("sapphire_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_ORE = key("silver_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> TIN_ORE = key("tin_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> TOPAZ_ORE = key("topaz_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> TUNGSTEN_ORE = key("tungsten_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> MARINE_GRAVEL = key("marine_gravel");
        private static final ResourceKey<ConfiguredFeature<?, ?>> OPAL_ORE = key("opal_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> THIN_ICE_PATCH = key("thin_ice_patch");
        private static final ResourceKey<ConfiguredFeature<?, ?>> POWDER_SNOW_PATCH = key("powder_snow_patch");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CRIMSON_ALTAR = key("crimson_altar");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEMON_ALTAR = key("demon_altar");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DESERT_FOSSIL = key("desert_fossil");
        private static final ResourceKey<ConfiguredFeature<?, ?>> FALLING_SAND_TRAP = key("falling_sand_trap");

        private static ResourceKey<ConfiguredFeature<?, ?>> key(String path) {
            return Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, path);
        }

        private static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
            TagMatchTest stoneOreReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
            TagMatchTest deepslateOreReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
            ore(context, AMBER_ORE, 8, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.DESERT_FOSSIL_REPLACEMENT), OreBlocks.AMBER_ORE.get().defaultBlockState()));
            ore(context, AMETHYST_ORE, 8, OreConfiguration.target(stoneOreReplaceables, OreBlocks.AMETHYST_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_AMETHYST_ORE.get().defaultBlockState()));
            ore(context, ASH_HELLSTONE, 16, OreConfiguration.target(new BlockMatchTest(NatureBlocks.ASH_BLOCK.get()), OreBlocks.ASH_HELLSTONE.get().defaultBlockState()));
            ore(context, COLD_CRYSTAL_ORE, 8, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.COLD_CRYSTAL_ORE_REPLACEMENT), OreBlocks.COLD_CRYSTAL_ORE.get().defaultBlockState()));
            ore(context, CRIMTANE_ORE, 7, 1, OreConfiguration.target(stoneOreReplaceables, OreBlocks.CRIMTANE_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_CRIMTANE_ORE.get().defaultBlockState()));
            scatteredOre(context, DEEPSLATE_ADAMANTITE_ORE_STEP_0, 5, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            scatteredOre(context, DEEPSLATE_ADAMANTITE_ORE_STEP_1, 5, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            scatteredOre(context, DEEPSLATE_ADAMANTITE_ORE_STEP_2, 5, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            scatteredOre(context, DEEPSLATE_COBALT_ORE_STEP_0, 7, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            scatteredOre(context, DEEPSLATE_COBALT_ORE_STEP_1, 7, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            scatteredOre(context, DEEPSLATE_COBALT_ORE_STEP_2, 7, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            scatteredOre(context, DEEPSLATE_MYTHRIL_ORE_STEP_0, 6, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_MYTHRIL_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            scatteredOre(context, DEEPSLATE_MYTHRIL_ORE_STEP_1, 6, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_MYTHRIL_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            scatteredOre(context, DEEPSLATE_MYTHRIL_ORE_STEP_2, 6, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_MYTHRIL_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            scatteredOre(context, DEEPSLATE_ORICHALCUM_ORE_STEP_0, 6, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            scatteredOre(context, DEEPSLATE_ORICHALCUM_ORE_STEP_1, 6, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            scatteredOre(context, DEEPSLATE_ORICHALCUM_ORE_STEP_2, 6, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            scatteredOre(context, DEEPSLATE_PALLADIUM_ORE_STEP_0, 7, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_PALLADIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            scatteredOre(context, DEEPSLATE_PALLADIUM_ORE_STEP_1, 7, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_PALLADIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            scatteredOre(context, DEEPSLATE_PALLADIUM_ORE_STEP_2, 7, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_PALLADIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            scatteredOre(context, DEEPSLATE_TITANIUM_ORE_STEP_0, 5, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            scatteredOre(context, DEEPSLATE_TITANIUM_ORE_STEP_1, 5, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            scatteredOre(context, DEEPSLATE_TITANIUM_ORE_STEP_2, 5, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            ore(context, DEMONITE_ORE, 7, 1, OreConfiguration.target(stoneOreReplaceables, OreBlocks.DEMONITE_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_DEMONITE_ORE.get().defaultBlockState()));
            ore(context, GELSTONE_ORE, 8, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.GELSTONE_ORE_REPLACEMENT), OreBlocks.GELSTONE_ORE.get().defaultBlockState()));
            ore(context, JADE_ORE, 8, OreConfiguration.target(stoneOreReplaceables, OreBlocks.JADE_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_JADE_ORE.get().defaultBlockState()));
            ore(context, LEAD_ORE, 7, OreConfiguration.target(stoneOreReplaceables, OreBlocks.LEAD_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_LEAD_ORE.get().defaultBlockState()));
            ore(context, PLATINUM_ORE, 5, OreConfiguration.target(stoneOreReplaceables, OreBlocks.PLATINUM_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_PLATINUM_ORE.get().defaultBlockState()));
            ore(context, RUBY_ORE, 8, OreConfiguration.target(stoneOreReplaceables, OreBlocks.RUBY_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_RUBY_ORE.get().defaultBlockState()));
            ore(context, SAPPHIRE_ORE, 8, OreConfiguration.target(stoneOreReplaceables, OreBlocks.SAPPHIRE_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_SAPPHIRE_ORE.get().defaultBlockState()));
            ore(context, SILVER_ORE, 6, OreConfiguration.target(stoneOreReplaceables, OreBlocks.SILVER_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_SILVER_ORE.get().defaultBlockState()));
            ore(context, TIN_ORE, 10, OreConfiguration.target(stoneOreReplaceables, OreBlocks.TIN_ORE.get().defaultBlockState()));
            ore(context, TOPAZ_ORE, 8, OreConfiguration.target(stoneOreReplaceables, OreBlocks.TOPAZ_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_TOPAZ_ORE.get().defaultBlockState()));
            ore(context, TUNGSTEN_ORE, 5, OreConfiguration.target(stoneOreReplaceables, OreBlocks.TUNGSTEN_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_TUNGSTEN_ORE.get().defaultBlockState()));
            gemTree(context, ModFeatures.Configured.AMBER, NatureBlocks.AMBER_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.AMETHYST, NatureBlocks.AMETHYST_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.DIAMOND, NatureBlocks.DIAMOND_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.JADE, NatureBlocks.JADE_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.RUBY, NatureBlocks.RUBY_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.SAPPHIRE, NatureBlocks.SAPPHIRE_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.TOPAZ, NatureBlocks.TOPAZ_BRANCHES.get());
            baobabTree(context, ModFeatures.Configured.BAOBAB, NatureBlocks.BAOBAB_LOG_BLOCKS.getLog().get(), NatureBlocks.BAOBAB_LOG_BLOCKS.getWood().get(), NatureBlocks.BAOBAB_LOG_BLOCKS.getWood().get(), NatureBlocks.BAOBAB_LOG_BLOCKS.getLeaves().get(), NatureBlocks.BAOBAB_LOG_BLOCKS.getStrippedLog().get(), 8);
            baobabTree(context, ModFeatures.Configured.BAOBAB_AIR, NatureBlocks.BAOBAB_LOG_BLOCKS.getLog().get(), NatureBlocks.BAOBAB_LOG_BLOCKS.getWood().get(), NatureBlocks.BAOBAB_LOG_BLOCKS.getWood().get(), NatureBlocks.BAOBAB_LOG_BLOCKS.getLeaves().get(), Blocks.AIR, 8);
            baobabTree(context, ModFeatures.Configured.BAOBAB_WATER, NatureBlocks.BAOBAB_LOG_BLOCKS.getLog().get(), NatureBlocks.BAOBAB_LOG_BLOCKS.getWood().get(), NatureBlocks.BAOBAB_LOG_BLOCKS.getWood().get(), NatureBlocks.BAOBAB_LOG_BLOCKS.getLeaves().get(), Blocks.WATER, 8);
            register(context, ModFeatures.Configured.PALM, ModFeatures.PALM_TREE.get(), new PalmTreeFeature.Config(BlockStateProvider.simple(NatureBlocks.PALM_LOG_BLOCKS.getLog().get()), BlockStateProvider.simple(NatureBlocks.PALM_LOG_BLOCKS.getLeaves().get().defaultBlockState().setValue(PalmLeaves.TYPE, SlabType.BOTTOM)), BlockStateProvider.simple(NatureBlocks.PALM_LOG_BLOCKS.getLeaves().get().defaultBlockState().setValue(PalmLeaves.TYPE, SlabType.TOP)), BlockStateProvider.simple(NatureBlocks.PALM_LOG_BLOCKS.getLeaves().get().defaultBlockState().setValue(PalmLeaves.TYPE, SlabType.DOUBLE))));
            droopingVineTree(context, ModFeatures.Configured.CONFIGURED_CRIMSON_TREE_CHECKED_0, NatureBlocks.SHADOW_LOG_BLOCKS.getLog().get(), NatureBlocks.SHADOW_LOG_BLOCKS.getLeaves().get(), NatureBlocks.CRIMSON_DROOPING_VINE.get(), 5);
            droopingVineTree(context, ModFeatures.Configured.CONFIGURED_THE_CORRUPTION_TREE_CHECKED_2, NatureBlocks.EBONY_LOG_BLOCKS.getLog().get(), NatureBlocks.EBONY_LOG_BLOCKS.getLeaves().get(), NatureBlocks.CRIMSON_DROOPING_VINE.get(), 5);
            droopingVineTree(context, ModFeatures.Configured.CONFIGURED_YELLOW_WILLOW, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getLog().get(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getLeaves().get(), NatureBlocks.YELLOW_WILLOW_DROOPING_LEAVES.get(), 6);
            register(context, ModFeatures.Configured.GLOWING_MUSHROOM, ModFeatures.MUSHROOM_TREE.get(), new MushroomTreeFeature.Config(BlockStateProvider.simple(NatureBlocks.GLOWING_MUSHROOM_STEM_BLOCK.get()), BlockStateProvider.simple(NatureBlocks.GLOWING_MUSHROOM_PILEUS_BLOCK.get()), BlockStateProvider.simple(NatureBlocks.GLOWING_MUSHROOM_INDUSIUM_BLOCK.get()), 4, 1));
            ore(context, MARINE_GRAVEL, 33, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.MARINE_GRAVEL_REPLACEMENT), NatureBlocks.MARINE_GRAVEL.get().defaultBlockState()));
            ore(context, OPAL_ORE, 4, 0.5F, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.OPAL_ORE_REPLACEMENT), OreBlocks.OPAL_ORE.get().defaultBlockState()));
            register(context, THIN_ICE_PATCH, ModFeatures.COLUMN_PATCH.get(), new ColumnPatchFeature.Config(3, 4, 32, 32, 0.5F, BlockStateProvider.simple(NatureBlocks.THIN_ICE_BLOCK.get())));
            register(context, POWDER_SNOW_PATCH, ModFeatures.COLUMN_PATCH.get(), new ColumnPatchFeature.Config(0, 2, 10, 32, 0.3F, BlockStateProvider.simple(Blocks.POWDER_SNOW)));
            register(context, CRIMSON_ALTAR, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(FunctionalBlocks.CRIMSON_ALTAR.get())));
            register(context, DEMON_ALTAR, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(FunctionalBlocks.DEMON_ALTAR.get())));
            ore(context, DESERT_FOSSIL, 33, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.DESERT_FOSSIL_REPLACEMENT), NatureBlocks.DESERT_FOSSIL.get().defaultBlockState()));
            register(context, FALLING_SAND_TRAP, ModFeatures.FALLING_SAND_TRAP.get(), new FallingSandTrapFeature.Config(BlockStateProvider.simple(Blocks.SAND), 4, 4, 4, 16));
        }

        private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
            context.register(key, new ConfiguredFeature<>(feature, config));
        }

        private static void ore(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, int size, OreConfiguration.TargetBlockState... targets) {
            register(context, key, Feature.ORE, new OreConfiguration(Arrays.stream(targets).toList(), size));
        }

        private static void ore(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, int size, float discardChanceOnAirExposure, OreConfiguration.TargetBlockState... targets) {
            register(context, key, Feature.ORE, new OreConfiguration(Arrays.stream(targets).toList(), size, discardChanceOnAirExposure));
        }

        private static void scatteredOre(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, int size, OreConfiguration.TargetBlockState... targets) {
            register(context, key, Feature.SCATTERED_ORE, new OreConfiguration(Arrays.stream(targets).toList(), size));
        }

        private static void gemTree(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, Block branchesBlock) {
            register(context, key, ModFeatures.BRANCH_TREE.get(), new BranchTreeFeature.Config(BlockStateProvider.simple(NatureBlocks.STONY_LOG.get()), BlockStateProvider.simple(branchesBlock), 6, 2));
        }

        private static void baobabTree(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, Block trunk, Block branch, Block root, Block leaves, Block inner, int height) {
            register(context, key, ModFeatures.BAOBAB_TREE.get(), new BaobabTreeFeature.Config(BlockStateProvider.simple(trunk), BlockStateProvider.simple(branch), BlockStateProvider.simple(root), BlockStateProvider.simple(leaves), BlockStateProvider.simple(inner), height));
        }

        private static void droopingVineTree(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, Block trunk, Block leaves, Block drooping_leave, int height) {
            register(context, key, ModFeatures.DROOPING_VINE_TREE.get(), new DroopingVineTreeFeature.Config(BlockStateProvider.simple(trunk), BlockStateProvider.simple(leaves), BlockStateProvider.simple(drooping_leave), height));
        }
    }

    private static class PlacedFeatures {
        private static final ResourceKey<PlacedFeature> AMBER_ORE = key("amber_ore");
        private static final ResourceKey<PlacedFeature> AMETHYST_ORE = key("amethyst_ore");
        private static final ResourceKey<PlacedFeature> ASH_HELLSTONE = key("ash_hellstone");
        private static final ResourceKey<PlacedFeature> COLD_CRYSTAL_ORE = key("cold_crystal_ore");
        private static final ResourceKey<PlacedFeature> CRIMTANE_ORE = key("crimtane_ore");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_ADAMANTITE_ORE_STEP_0 = key("deepslate_adamantite_ore_step_0");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_ADAMANTITE_ORE_STEP_1 = key("deepslate_adamantite_ore_step_1");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_ADAMANTITE_ORE_STEP_2 = key("deepslate_adamantite_ore_step_2");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_COBALT_ORE_STEP_0 = key("deepslate_cobalt_ore_step_0");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_COBALT_ORE_STEP_1 = key("deepslate_cobalt_ore_step_1");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_COBALT_ORE_STEP_2 = key("deepslate_cobalt_ore_step_2");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_MYTHRIL_ORE_STEP_0 = key("deepslate_mythril_ore_step_0");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_MYTHRIL_ORE_STEP_1 = key("deepslate_mythril_ore_step_1");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_MYTHRIL_ORE_STEP_2 = key("deepslate_mythril_ore_step_2");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_ORICHALCUM_ORE_STEP_0 = key("deepslate_orichalcum_ore_step_0");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_ORICHALCUM_ORE_STEP_1 = key("deepslate_orichalcum_ore_step_1");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_ORICHALCUM_ORE_STEP_2 = key("deepslate_orichalcum_ore_step_2");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_PALLADIUM_ORE_STEP_0 = key("deepslate_palladium_ore_step_0");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_PALLADIUM_ORE_STEP_1 = key("deepslate_palladium_ore_step_1");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_PALLADIUM_ORE_STEP_2 = key("deepslate_palladium_ore_step_2");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_TITANIUM_ORE_STEP_0 = key("deepslate_titanium_ore_step_0");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_TITANIUM_ORE_STEP_1 = key("deepslate_titanium_ore_step_1");
        private static final ResourceKey<PlacedFeature> DEEPSLATE_TITANIUM_ORE_STEP_2 = key("deepslate_titanium_ore_step_2");
        private static final ResourceKey<PlacedFeature> DEMONITE_ORE = key("demonite_ore");
        private static final ResourceKey<PlacedFeature> GELSTONE_ORE = key("gelstone_ore");
        private static final ResourceKey<PlacedFeature> JADE_ORE = key("jade_ore");
        private static final ResourceKey<PlacedFeature> LEAD_ORE = key("lead_ore");
        private static final ResourceKey<PlacedFeature> PLATINUM_ORE = key("platinum_ore");
        private static final ResourceKey<PlacedFeature> RUBY_ORE = key("ruby_ore");
        private static final ResourceKey<PlacedFeature> SAPPHIRE_ORE = key("sapphire_ore");
        private static final ResourceKey<PlacedFeature> SILVER_ORE = key("silver_ore");
        private static final ResourceKey<PlacedFeature> TIN_ORE = key("tin_ore");
        private static final ResourceKey<PlacedFeature> TOPAZ_ORE = key("topaz_ore");
        private static final ResourceKey<PlacedFeature> TUNGSTEN_ORE = key("tungsten_ore");
        private static final ResourceKey<PlacedFeature> AMBER_TREE = key("amber_tree");
        private static final ResourceKey<PlacedFeature> AMETHYST_TREE = key("amethyst_tree");
        private static final ResourceKey<PlacedFeature> DIAMOND_TREE = key("diamond_tree");
        private static final ResourceKey<PlacedFeature> JADE_TREE = key("jade_tree");
        private static final ResourceKey<PlacedFeature> RUBY_TREE = key("ruby_tree");
        private static final ResourceKey<PlacedFeature> SAPPHIRE_TREE = key("sapphire_tree");
        private static final ResourceKey<PlacedFeature> TOPAZ_TREE = key("topaz_tree");
        private static final ResourceKey<PlacedFeature> MARINE_GRAVEL = key("marine_gravel");
        private static final ResourceKey<PlacedFeature> OPAL_ORE = key("opal_ore");
        private static final ResourceKey<PlacedFeature> THIN_ICE_PATCH = key("thin_ice_patch");
        private static final ResourceKey<PlacedFeature> POWDER_SNOW_PATCH = key("powder_snow_patch");
        private static final ResourceKey<PlacedFeature> CRIMSON_ALTAR_BIOME = key("crimson_altar_biome");
        private static final ResourceKey<PlacedFeature> CRIMSON_ALTAR_WORLD = key("crimson_altar_world");
        private static final ResourceKey<PlacedFeature> DEMON_ALTAR_BIOME = key("demon_altar_biome");
        private static final ResourceKey<PlacedFeature> DEMON_ALTAR_WORLD = key("demon_altar_world");
        private static final ResourceKey<PlacedFeature> DESERT_FOSSIL = key("desert_fossil");
        private static final ResourceKey<PlacedFeature> FALLING_SAND_TRAP = key("falling_sand_trap");

        private static ResourceKey<PlacedFeature> key(String path) {
            return Confluence.asResourceKey(Registries.PLACED_FEATURE, path);
        }

        private static void bootstrap(BootstrapContext<PlacedFeature> context) {
            HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
            CountPlacement count1 = CountPlacement.of(1);
            CountPlacement count2 = CountPlacement.of(2);
            CountPlacement count3 = CountPlacement.of(3);
            CountPlacement count4 = CountPlacement.of(4);
            CountPlacement count5 = CountPlacement.of(5);
            InSquarePlacement inSquare = InSquarePlacement.spread();
            BiomeFilter biome = BiomeFilter.biome();
            HeightRangePlacement bottomThroughTop = HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP);
            register(context, AMBER_ORE, configured.getOrThrow(ConfiguredFeatures.AMBER_ORE), count1, inSquare, biome, heightRangeTriangle(-52, 10));
            register(context, AMETHYST_ORE, configured.getOrThrow(ConfiguredFeatures.AMETHYST_ORE), count1, inSquare, biome, heightRangeTriangle(-52, 10));
            register(context, ASH_HELLSTONE, configured.getOrThrow(ConfiguredFeatures.ASH_HELLSTONE), count4, inSquare, biome, heightRangeTriangle(0, 128));
            register(context, COLD_CRYSTAL_ORE, configured.getOrThrow(ConfiguredFeatures.COLD_CRYSTAL_ORE), count1, inSquare, biome, heightRangeTriangle(-52, 160));
            register(context, CRIMTANE_ORE, configured.getOrThrow(ConfiguredFeatures.CRIMTANE_ORE), SecretFlagPlacement.of(IWorldOptions.THE_CRIMSON), count2, inSquare, biome, heightRangeTriangle(-50, 30));
            register(context, DEEPSLATE_ADAMANTITE_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_0), count3, inSquare, biome, heightRangeTriangle(-60, -30));
            register(context, DEEPSLATE_ADAMANTITE_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_1), count3, inSquare, biome, heightRangeTriangle(-60, -30));
            register(context, DEEPSLATE_ADAMANTITE_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_2), count3, inSquare, biome, heightRangeTriangle(-60, -30));
            register(context, DEEPSLATE_COBALT_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_COBALT_ORE_STEP_0), count5, inSquare, biome, heightRangeTriangle(-60, -10));
            register(context, DEEPSLATE_COBALT_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_COBALT_ORE_STEP_1), count5, inSquare, biome, heightRangeTriangle(-60, -10));
            register(context, DEEPSLATE_COBALT_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_COBALT_ORE_STEP_2), count5, inSquare, biome, heightRangeTriangle(-60, -10));
            register(context, DEEPSLATE_MYTHRIL_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_0), count4, inSquare, biome, heightRangeTriangle(-60, -20));
            register(context, DEEPSLATE_MYTHRIL_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_1), count4, inSquare, biome, heightRangeTriangle(-60, -20));
            register(context, DEEPSLATE_MYTHRIL_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_2), count4, inSquare, biome, heightRangeTriangle(-60, -20));
            register(context, DEEPSLATE_ORICHALCUM_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_0), count4, inSquare, biome, heightRangeTriangle(-60, -20));
            register(context, DEEPSLATE_ORICHALCUM_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_1), count4, inSquare, biome, heightRangeTriangle(-60, -20));
            register(context, DEEPSLATE_ORICHALCUM_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_2), count4, inSquare, biome, heightRangeTriangle(-60, -20));
            register(context, DEEPSLATE_PALLADIUM_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_0), count5, inSquare, biome, heightRangeTriangle(-60, -10));
            register(context, DEEPSLATE_PALLADIUM_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_1), count5, inSquare, biome, heightRangeTriangle(-60, -10));
            register(context, DEEPSLATE_PALLADIUM_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_2), count5, inSquare, biome, heightRangeTriangle(-60, -10));
            register(context, DEEPSLATE_TITANIUM_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_TITANIUM_ORE_STEP_0), count3, inSquare, biome, heightRangeTriangle(-60, -30));
            register(context, DEEPSLATE_TITANIUM_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_TITANIUM_ORE_STEP_1), count3, inSquare, biome, heightRangeTriangle(-60, -30));
            register(context, DEEPSLATE_TITANIUM_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_TITANIUM_ORE_STEP_2), count3, inSquare, biome, heightRangeTriangle(-60, -30));
            register(context, DEMONITE_ORE, configured.getOrThrow(ConfiguredFeatures.DEMONITE_ORE), SecretFlagPlacement.of(IWorldOptions.THE_CORRUPTION), count2, inSquare, biome, heightRangeTriangle(-50, 30));
            register(context, GELSTONE_ORE, configured.getOrThrow(ConfiguredFeatures.GELSTONE_ORE), count1, inSquare, biome, heightRangeTriangle(-52, 160));
            register(context, JADE_ORE, configured.getOrThrow(ConfiguredFeatures.JADE_ORE), count1, inSquare, biome, heightRangeTriangle(-52, 10));
            register(context, LEAD_ORE, configured.getOrThrow(ConfiguredFeatures.LEAD_ORE), CountPlacement.of(8), inSquare, biome, heightRangeTriangle(-24, 56));
            register(context, PLATINUM_ORE, configured.getOrThrow(ConfiguredFeatures.PLATINUM_ORE), SecretFlagPlacement.of(IWorldOptions.TC_MASK, true), count2, inSquare, biome, heightRangeTriangle(-48, 10));
            register(context, RUBY_ORE, configured.getOrThrow(ConfiguredFeatures.RUBY_ORE), count1, inSquare, biome, heightRangeTriangle(-52, 10));
            register(context, SAPPHIRE_ORE, configured.getOrThrow(ConfiguredFeatures.SAPPHIRE_ORE), count1, inSquare, biome, heightRangeTriangle(-52, 10));
            register(context, SILVER_ORE, configured.getOrThrow(ConfiguredFeatures.SILVER_ORE), CountPlacement.of(6), inSquare, biome, heightRangeTriangle(-34, 28));
            register(context, TIN_ORE, configured.getOrThrow(ConfiguredFeatures.TIN_ORE), CountPlacement.of(16), inSquare, biome, heightRangeTriangle(0, 128));
            register(context, TOPAZ_ORE, configured.getOrThrow(ConfiguredFeatures.TOPAZ_ORE), count1, inSquare, biome, heightRangeTriangle(-52, 10));
            register(context, TUNGSTEN_ORE, configured.getOrThrow(ConfiguredFeatures.TUNGSTEN_ORE), SecretFlagPlacement.of(IWorldOptions.TC_MASK, true), count4, inSquare, biome, heightRangeTriangle(-38, 20));
            gemTree(context, AMBER_TREE, configured.getOrThrow(ModFeatures.Configured.AMBER), NatureBlocks.AMBER_SAPLING.get());
            gemTree(context, AMETHYST_TREE, configured.getOrThrow(ModFeatures.Configured.AMETHYST), NatureBlocks.AMETHYST_SAPLING.get());
            gemTree(context, DIAMOND_TREE, configured.getOrThrow(ModFeatures.Configured.DIAMOND), NatureBlocks.DIAMOND_SAPLING.get());
            gemTree(context, JADE_TREE, configured.getOrThrow(ModFeatures.Configured.JADE), NatureBlocks.JADE_SAPLING.get());
            gemTree(context, RUBY_TREE, configured.getOrThrow(ModFeatures.Configured.RUBY), NatureBlocks.RUBY_SAPLING.get());
            gemTree(context, SAPPHIRE_TREE, configured.getOrThrow(ModFeatures.Configured.SAPPHIRE), NatureBlocks.SAPPHIRE_SAPLING.get());
            gemTree(context, TOPAZ_TREE, configured.getOrThrow(ModFeatures.Configured.TOPAZ), NatureBlocks.TOPAZ_SAPLING.get());
            register(context, MARINE_GRAVEL, configured.getOrThrow(ConfiguredFeatures.MARINE_GRAVEL), CountPlacement.of(10), inSquare, biome, bottomThroughTop);
            register(context, OPAL_ORE, configured.getOrThrow(ConfiguredFeatures.OPAL_ORE), CountPlacement.of(7), inSquare, biome, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.aboveBottom(50)));
            register(context, THIN_ICE_PATCH, configured.getOrThrow(ConfiguredFeatures.THIN_ICE_PATCH), RarityFilter.onAverageOnceEvery(2), inSquare, biome, heightRangeUniform(0, 40));
            register(context, POWDER_SNOW_PATCH, configured.getOrThrow(ConfiguredFeatures.POWDER_SNOW_PATCH), RarityFilter.onAverageOnceEvery(2), inSquare, biome, heightRangeUniform(0, 40));
            evilAltar(context, CRIMSON_ALTAR_BIOME, configured.getOrThrow(ConfiguredFeatures.CRIMSON_ALTAR), 12);
            evilAltar(context, CRIMSON_ALTAR_WORLD, configured.getOrThrow(ConfiguredFeatures.CRIMSON_ALTAR), 1);
            evilAltar(context, DEMON_ALTAR_BIOME, configured.getOrThrow(ConfiguredFeatures.DEMON_ALTAR), 12);
            evilAltar(context, DEMON_ALTAR_WORLD, configured.getOrThrow(ConfiguredFeatures.DEMON_ALTAR), 1);
            register(context, DESERT_FOSSIL, configured.getOrThrow(ConfiguredFeatures.DESERT_FOSSIL), CountPlacement.of(14), inSquare, biome, bottomThroughTop);
            register(context, FALLING_SAND_TRAP, configured.getOrThrow(ConfiguredFeatures.FALLING_SAND_TRAP), count1, inSquare, biome, heightRangeTriangle(-58, 58));
        }

        private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature, PlacementModifier... modifiers) {
            context.register(key, new PlacedFeature(feature, Arrays.stream(modifiers).toList()));
        }

        private static void gemTree(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature, Block saplingBlock) {
            register(context, key, feature, RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), BiomeFilter.biome(), HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(60)), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.matchesBlocks(Blocks.AIR), 12), RandomOffsetPlacement.vertical(ConstantInt.of(1)), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(saplingBlock.defaultBlockState(), Vec3i.ZERO)));
        }

        private static void evilAltar(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature, int count) {
            register(context, key, feature, SecretFlagPlacement.of(IWorldOptions.THE_CRIMSON), CountPlacement.of(count), InSquarePlacement.spread(), BiomeFilter.biome(), HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(256)), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.hasSturdyFace(new Vec3i(0, -1, 0), Direction.UP), BlockPredicate.matchesBlocks(Blocks.AIR), 12), SurfaceRelativeThresholdFilter.of(Heightmap.Types.WORLD_SURFACE_WG, -110, -20));
        }

        private static PlacementModifier heightRangeUniform(int min, int max) {
            return HeightRangePlacement.uniform(VerticalAnchor.absolute(min), VerticalAnchor.absolute(max));
        }

        private static PlacementModifier heightRangeTriangle(int min, int max) {
            return HeightRangePlacement.triangle(VerticalAnchor.absolute(min), VerticalAnchor.absolute(max));
        }
    }

    private static class ConfiguredWorldCarvers {
        private static final ResourceKey<ConfiguredWorldCarver<?>> DESERT_CAVE_CARVER = key("desert_cave_carver");
        private static final ResourceKey<ConfiguredWorldCarver<?>> DEMONIC_CAVE_CARVER = key("demonic_cave_carver");
        private static final ResourceKey<ConfiguredWorldCarver<?>> GLOWING_MUSHROOM_CAVE_CARVER = key("glowing_mushroom_cave_carver");
        private static final ResourceKey<ConfiguredWorldCarver<?>> JUNGLE_CAVE_CARVER = key("jungle_cave_carver");
        private static final ResourceKey<ConfiguredWorldCarver<?>> WAVY_CAVE_CARVER = key("wavy_cave_carver");

        private static ResourceKey<ConfiguredWorldCarver<?>> key(String path) {
            return Confluence.asResourceKey(Registries.CONFIGURED_CARVER, path);
        }

        private static void bootstrap(BootstrapContext<ConfiguredWorldCarver<?>> context) {
            HolderGetter<Block> block = context.lookup(Registries.BLOCK);
            VerticalAnchor aboveBottom8 = VerticalAnchor.aboveBottom(8);
            VerticalAnchor absolute80 = VerticalAnchor.absolute(80);
            HolderSet.Named<Block> replaceable = block.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES);
            context.register(DESERT_CAVE_CARVER, new ConfiguredWorldCarver<>(ModCarvers.DESERT_CAVE_CARVER.get(), new CarverConfiguration(1, UniformHeight.of(aboveBottom8, absolute80), ConstantFloat.of(8), aboveBottom8, CarverDebugSettings.DEFAULT, replaceable)));
            context.register(DEMONIC_CAVE_CARVER, new ConfiguredWorldCarver<>(ModCarvers.DEMONIC_CAVE_CARVER.get(), new DemonicCaveCarver.Config(new CarverConfiguration(0.2F, UniformHeight.of(VerticalAnchor.aboveBottom(40), absolute80), ConstantFloat.of(4), aboveBottom8, CarverDebugSettings.DEFAULT, replaceable), UniformFloat.of(24, 48))));
            context.register(GLOWING_MUSHROOM_CAVE_CARVER, new ConfiguredWorldCarver<>(ModCarvers.GLOWING_MUSHROOM_CAVE_CARVER.get(), new GlowingMushroomCaveCarver.Config(new CarverConfiguration(0.6F, UniformHeight.of(VerticalAnchor.absolute(-40), VerticalAnchor.absolute(-10)), ConstantFloat.of(6), aboveBottom8, CarverDebugSettings.DEFAULT, replaceable))));
            context.register(JUNGLE_CAVE_CARVER, new ConfiguredWorldCarver<>(ModCarvers.JUNGLE_CAVE_CARVER.get(), new CaveCarverConfiguration(0.5F, UniformHeight.of(aboveBottom8, VerticalAnchor.absolute(100)), UniformFloat.of(0.1F, 0.9F), aboveBottom8, CarverDebugSettings.DEFAULT, replaceable, UniformFloat.of(2.8F, 5.6F), UniformFloat.of(1.6F, 2.6F), UniformFloat.of(-1, -0.4F))));
            context.register(WAVY_CAVE_CARVER, new ConfiguredWorldCarver<>(ModCarvers.WAVY_CAVE_CARVER.get(), new CarverConfiguration(0.2F, UniformHeight.of(VerticalAnchor.absolute(-40), absolute80), ConstantFloat.of(4), aboveBottom8, CarverDebugSettings.DEFAULT, replaceable)));
        }
    }

    private static class BiomeModifierz {
        private static void bootstrap(BootstrapContext<BiomeModifier> context) {
            HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
            HolderSet<Biome> desert = biome.getOrThrow(Tags.Biomes.IS_DESERT);
            HolderSet<Biome> snowyIcy = new OrHolderSet<>(biome.getOrThrow(Tags.Biomes.IS_SNOWY), biome.getOrThrow(Tags.Biomes.IS_ICY));
            HolderSet<Biome> desertBadlands = new OrHolderSet<>(biome.getOrThrow(Tags.Biomes.IS_DESERT), biome.getOrThrow(Tags.Biomes.IS_BADLANDS));

            HolderGetter<PlacedFeature> placedFeature = context.lookup(Registries.PLACED_FEATURE);
            Function<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> factory = placedFeature::getOrThrow;
            addFeatures(context, "desert_uo", desert, HolderSet.direct(placedFeature.getOrThrow(PlacedFeatures.AMBER_ORE)), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "snowy_icy_uo", snowyIcy, HolderSet.direct(placedFeature.getOrThrow(PlacedFeatures.COLD_CRYSTAL_ORE)), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "swamp_uo", biome.getOrThrow(Tags.Biomes.IS_SWAMP), HolderSet.direct(placedFeature.getOrThrow(PlacedFeatures.GELSTONE_ORE)), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "overworld_uo", biome.getOrThrow(Tags.Biomes.IS_OVERWORLD), HolderSet.direct(factory,
                    PlacedFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_0, PlacedFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_1, PlacedFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_2,
                    PlacedFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_0, PlacedFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_1, PlacedFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_2,
                    PlacedFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_0, PlacedFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_1, PlacedFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_2,
                    PlacedFeatures.DEEPSLATE_COBALT_ORE_STEP_0, PlacedFeatures.DEEPSLATE_COBALT_ORE_STEP_1, PlacedFeatures.DEEPSLATE_COBALT_ORE_STEP_2,
                    PlacedFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_0, PlacedFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_1, PlacedFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_2,
                    PlacedFeatures.DEEPSLATE_TITANIUM_ORE_STEP_0, PlacedFeatures.DEEPSLATE_TITANIUM_ORE_STEP_1, PlacedFeatures.DEEPSLATE_TITANIUM_ORE_STEP_2,
                    PlacedFeatures.DEMONITE_ORE, PlacedFeatures.CRIMTANE_ORE, PlacedFeatures.PLATINUM_ORE, PlacedFeatures.TUNGSTEN_ORE, PlacedFeatures.SILVER_ORE, PlacedFeatures.LEAD_ORE, PlacedFeatures.TIN_ORE,
                    PlacedFeatures.RUBY_ORE, PlacedFeatures.TOPAZ_ORE, PlacedFeatures.AMETHYST_ORE, PlacedFeatures.JADE_ORE, PlacedFeatures.SAPPHIRE_ORE
            ), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "overworld_ud", biome.getOrThrow(Tags.Biomes.IS_OVERWORLD), HolderSet.direct(factory,
                    PlacedFeatures.AMBER_TREE, PlacedFeatures.AMETHYST_TREE, PlacedFeatures.DIAMOND_TREE, PlacedFeatures.JADE_TREE, PlacedFeatures.RUBY_TREE, PlacedFeatures.SAPPHIRE_TREE, PlacedFeatures.TOPAZ_TREE,
                    PlacedFeatures.CRIMSON_ALTAR_WORLD, PlacedFeatures.DEMON_ALTAR_WORLD
            ), GenerationStep.Decoration.UNDERGROUND_DECORATION);
            addFeatures(context, "beach_ocean_uo", new OrHolderSet<>(biome.getOrThrow(Tags.Biomes.IS_BEACH), biome.getOrThrow(Tags.Biomes.IS_OCEAN)), HolderSet.direct(factory,
                    PlacedFeatures.MARINE_GRAVEL, PlacedFeatures.OPAL_ORE
            ), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "snowy_icy_ud", snowyIcy, HolderSet.direct(factory,
                    PlacedFeatures.THIN_ICE_PATCH, PlacedFeatures.POWDER_SNOW_PATCH
            ), GenerationStep.Decoration.UNDERGROUND_DECORATION);
            addFeatures(context, "desert_badlands_uo", desertBadlands, HolderSet.direct(placedFeature.getOrThrow(PlacedFeatures.DESERT_FOSSIL)), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "desert_ud", desert, HolderSet.direct(placedFeature.getOrThrow(PlacedFeatures.FALLING_SAND_TRAP)), GenerationStep.Decoration.UNDERGROUND_DECORATION);

            HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarver = context.lookup(Registries.CONFIGURED_CARVER);
            addCarvers(context, "desert_air", desert, HolderSet.direct(configuredWorldCarver.getOrThrow(ConfiguredWorldCarvers.DESERT_CAVE_CARVER)), GenerationStep.Carving.AIR);
        }

        private static void addFeatures(BootstrapContext<BiomeModifier> context, String path, HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) {
            context.register(Confluence.asResourceKey(NeoForgeRegistries.Keys.BIOME_MODIFIERS, path), new BiomeModifiers.AddFeaturesBiomeModifier(biomes, features, step));
        }

        private static void addCarvers(BootstrapContext<BiomeModifier> context, String path, HolderSet<Biome> biomes, HolderSet<ConfiguredWorldCarver<?>> carvers, GenerationStep.Carving step) {
            context.register(Confluence.asResourceKey(NeoForgeRegistries.Keys.BIOME_MODIFIERS, path), new BiomeModifiers.AddCarversBiomeModifier(biomes, carvers, step));
        }
    }

    private static class Biomes {
        private static void boostrap(BootstrapContext<Biome> context) {
            HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarver = context.lookup(Registries.CONFIGURED_CARVER);
            HolderGetter<PlacedFeature> placedFeature = context.lookup(Registries.PLACED_FEATURE);

            context.register(ModBiomes.THE_CORRUPTION, new Biome.BiomeBuilder().hasPrecipitation(true).temperature(1).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-9030507).grassColorOverride(-9351806).skyColor(-10726554).fogColor(-10726554).waterColor(-12837542).waterFogColor(-11055776).build())
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
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, PlacedFeatures.DEMON_ALTAR_BIOME);
                    }).build())
                    .build());
            context.register(ModBiomes.THE_CORRUPTION_DESERT, new Biome.BiomeBuilder().temperature(1).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-10271373).grassColorOverride(-10207626).skyColor(-8161900).fogColor(-8161900).waterColor(-11061641).waterFogColor(-9083007).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 22, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_CORRUPTION_TUNDRA, new Biome.BiomeBuilder().temperature(0).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-9939839).grassColorOverride(-9415030).skyColor(-8948332).fogColor(-8948332).waterColor(-9876078).waterFogColor(-9869439).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 22, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))
                            .build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_CRIMSON, new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.5F).downfall(0.5F)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-2282195).grassColorOverride(-4436402).skyColor(-8827314).fogColor(-8827314).waterColor(-7069664).waterFogColor(-7451572).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_KEMERA.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))
                            .build())
                    .generationSettings(Util.make(new BiomeGenerationSettings.Builder(placedFeature, configuredWorldCarver), builder -> {
                        addDefaultGenerations(builder);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, PlacedFeatures.CRIMSON_ALTAR_BIOME);
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
            context.register(ModBiomes.THE_CRIMSON_DESERT, new Biome.BiomeBuilder().temperature(0).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-6669252).grassColorOverride(-7915464).skyColor(-6331292).fogColor(-6331292).waterColor(-5294281).waterFogColor(-5674390).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_KEMERA.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_CRIMSON_TUNDRA, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-6664389).grassColorOverride(-7915464).skyColor(-6327708).fogColor(-6327708).waterColor(-5286090).waterFogColor(-5671318).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_KEMERA.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_HALLOW, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-16711703).grassColorOverride(-3999757).fogColor(-3346188).waterColor(-1554953).waterFogColor(-3345167).skyColor(-3346188).build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_HALLOW_DESERT, new Biome.BiomeBuilder().temperature(0).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-11084592).grassColorOverride(-4005129).fogColor(-3347468).waterColor(-3347468).waterFogColor(-1554953).skyColor(-4592650).build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_HALLOW_TUNDRA, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-11084592).grassColorOverride(-4005129).fogColor(-3347468).waterColor(-3347468).waterFogColor(-1554953).skyColor(-4592650).build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.ASH_FOREST, new Biome.BiomeBuilder().hasPrecipitation(false).temperature(2).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(10387789).grassColorOverride(9470285).fogColor(-10541025).waterColor(-10541025).waterFogColor(4159204).skyColor(-4592650).build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(Util.make(new BiomeGenerationSettings.Builder(placedFeature, configuredWorldCarver), builder -> {
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.FIREBLOSSOM);
                        builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, PlacedFeatures.ASH_HELLSTONE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.ASH_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.ASH_TREE);
                    }).build())
                    .build()
            );
            context.register(ModBiomes.ASH_WASTELAND, new Biome.BiomeBuilder().hasPrecipitation(false).temperature(2).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(10387789).grassColorOverride(9470285).fogColor(-10541025).waterColor(-10541025).waterFogColor(4159204).skyColor(-4592650).build())
                    .mobSpawnSettings(MobSpawnSettings.EMPTY)
                    .generationSettings(Util.make(new BiomeGenerationSettings.Builder(placedFeature, configuredWorldCarver), builder -> {
                        builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, PlacedFeatures.ASH_HELLSTONE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModFeatures.Placed.FIREBLOSSOM);
                    }).build())
                    .build()
            );
            context.register(ModBiomes.GLOWING_MUSHROOM, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(12638463).waterColor(4159204).waterFogColor(329011).skyColor(8103167).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_BAT.get(), 60, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_SKELETON.get(), 60, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_ZOMBIE.get(), 45, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HAT_SPORE_ZOMBIE.get(), 15, 1, 2))
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

    private static class Enchantments {
        private static void bootstrap(BootstrapContext<Enchantment> context) {
            HolderGetter<Item> item = context.lookup(Registries.ITEM);
            HolderGetter<Enchantment> enchantment = context.lookup(Registries.ENCHANTMENT);
            AllOfCondition.Builder isMagic = LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity())
                    .and(DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(Tags.DamageTypes.IS_MAGIC))));
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
                            isMagic
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
            register(context, ModEnchantments.SPELL_DESPERATION, Enchantment.enchantment(
                            Enchantment.definition(
                                    item.getOrThrow(ModTags.Items.MANA_WEAPON),
                                    2,
                                    2,
                                    Enchantment.dynamicCost(25, 25),
                                    Enchantment.dynamicCost(75, 25),
                                    4,
                                    EquipmentSlotGroup.MAINHAND
                            ))
                    .exclusiveWith(enchantment.getOrThrow(ModTags.Enchantments.MAGIC_ATTACK_EXCLUSIVE))
                    .withEffect(ModEnchantments.EffectComponentTypes.LESS_MANA_MORE_ATTACK.get(), new AddValue(LevelBasedValue.perLevel(1)), isMagic)
            );
            register(context, ModEnchantments.MYSTIC_SURGE, Enchantment.enchantment(
                            Enchantment.definition(
                                    item.getOrThrow(ModTags.Items.MANA_WEAPON),
                                    2,
                                    2,
                                    Enchantment.dynamicCost(25, 25),
                                    Enchantment.dynamicCost(75, 25),
                                    4,
                                    EquipmentSlotGroup.MAINHAND
                            ))
                    .exclusiveWith(enchantment.getOrThrow(ModTags.Enchantments.MAGIC_ATTACK_EXCLUSIVE))
                    .withEffect(ModEnchantments.EffectComponentTypes.MORE_MANA_MORE_ATTACK.get(), new AddValue(LevelBasedValue.perLevel(1)), isMagic)
            );
        }

        private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
            context.register(key, builder.build(key.location()));
        }
    }

    public static class Structures {
        private static final TagKey<Biome> HAS_STRUCTURE_$_SHIMMER_LAKE = Confluence.asTagKey(Registries.BIOME, "has_structure/shimmer_lake");

        public static void boostrap(BootstrapContext<Structure> context) {
            HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
            context.register(ModStructures.DUNGEON_KEY, new DungeonStructure(new Structure.StructureSettings(biome.getOrThrow(HAS_STRUCTURE_$_SHIMMER_LAKE), Map.of(
                    MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create(
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.ANGER_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_ANGER_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHORT_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.DARK_CASTER.get(), 240, 2, 3),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.CURSED_SKULL.get(), 120, 3, 4),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.DUNGEON_SLIME.get(), 120, 1, 2)
                    ))
            ), GenerationStep.Decoration.TOP_LAYER_MODIFICATION, TerrainAdjustment.NONE)));
        }
    }
}
