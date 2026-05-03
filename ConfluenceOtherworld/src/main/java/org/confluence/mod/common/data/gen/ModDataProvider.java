package org.confluence.mod.common.data.gen;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.*;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.*;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.natural.PalmLeaves;
import org.confluence.mod.common.block.natural.RemainsBlock;
import org.confluence.mod.common.block.natural.StepRevealingBlock;
import org.confluence.mod.common.data.saved.MeteoriteTracker;
import org.confluence.mod.common.enchantment.SummonItemEffect;
import org.confluence.mod.common.init.*;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.worldgen.BannedBiomeNoiseBasedChunkGenerator;
import org.confluence.mod.common.worldgen.SecretFlagPlacement;
import org.confluence.mod.common.worldgen.carver.DemonicCaveCarver;
import org.confluence.mod.common.worldgen.feature.*;
import org.confluence.mod.common.worldgen.structure.*;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class ModDataProvider {
    public static final RegistrySetBuilder DATA_BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, ModDamageTypes::bootstrap)
            .add(Registries.BIOME, Biomes::boostrap)
            .add(Registries.PROCESSOR_LIST, ProcessorListz::bootstrap)
            .add(Registries.TEMPLATE_POOL, TemplatePools::bootstrap)
            .add(Registries.STRUCTURE, Structures::boostrap)
            .add(Registries.STRUCTURE_SET, StructureSets::bootstrap)
            .add(Registries.ENCHANTMENT, Enchantments::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, PlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifierz::bootstrap)
            .add(Registries.CONFIGURED_CARVER, ConfiguredWorldCarvers::bootstrap)
            .add(Registries.WORLD_PRESET, WorldPresetz::bootstrap);

    private static <T> HolderLookup.RegistryLookup<T> registryLookup(ResourceKey<Registry<T>> key, HolderGetter<T> holderGetter) {
        return new HolderLookup.RegistryLookup<>() {
            private final Map<ResourceKey<T>, Optional<Holder.Reference<T>>> holders = new IdentityHashMap<>();
            private final Map<TagKey<T>, Optional<HolderSet.Named<T>>> tags = new IdentityHashMap<>();

            @Override
            public ResourceKey<? extends Registry<? extends T>> key() {
                return key;
            }

            @Override
            public Lifecycle registryLifecycle() {
                return Lifecycle.experimental();
            }

            @Override
            public Stream<Holder.Reference<T>> listElements() {
                return holders.values().stream().filter(Optional::isPresent).map(Optional::get);
            }

            @Override
            public Stream<ResourceKey<T>> listElementIds() {
                return holders.keySet().stream();
            }

            @Override
            public Stream<HolderSet.Named<T>> listTags() {
                return tags.values().stream().filter(Optional::isPresent).map(Optional::get);
            }

            @Override
            public Stream<TagKey<T>> listTagIds() {
                return tags.keySet().stream();
            }

            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> resourceKey) {
                return holders.computeIfAbsent(resourceKey, holderGetter::get);
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> tagKey) {
                return tags.computeIfAbsent(tagKey, holderGetter::get);
            }
        };
    }

    public static class WorldPresetz {
        private static void bootstrap(BootstrapContext<WorldPreset> context) {
            HolderGetter<DimensionType> dimensionType = context.lookup(Registries.DIMENSION_TYPE);
            HolderGetter<MultiNoiseBiomeSourceParameterList> multiNoiseBiomeSourceParameterList = context.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
            HolderGetter<NoiseGeneratorSettings> noiseGeneratorSettings = context.lookup(Registries.NOISE_SETTINGS);
            Holder<NoiseGeneratorSettings> overworldNoiseGeneratorSettings = noiseGeneratorSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
            MultiNoiseBiomeSource overworldMultiNoiseBiomeSource = MultiNoiseBiomeSource.createFromPreset(multiNoiseBiomeSourceParameterList.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD));
            LevelStem nether = new LevelStem(dimensionType.getOrThrow(BuiltinDimensionTypes.NETHER), new NoiseBasedChunkGenerator(
                    MultiNoiseBiomeSource.createFromPreset(multiNoiseBiomeSourceParameterList.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER)),
                    noiseGeneratorSettings.getOrThrow(NoiseGeneratorSettings.NETHER)
            ));
            LevelStem end = new LevelStem(dimensionType.getOrThrow(BuiltinDimensionTypes.END), new NoiseBasedChunkGenerator(
                    TheEndBiomeSource.create(context.lookup(Registries.BIOME)),
                    noiseGeneratorSettings.getOrThrow(NoiseGeneratorSettings.END)
            ));

            context.register(Confluence.asResourceKey(Registries.WORLD_PRESET, "the_corruption"), new WorldPreset(Map.of(
                    LevelStem.OVERWORLD, new LevelStem(dimensionType.getOrThrow(BuiltinDimensionTypes.OVERWORLD), new BannedBiomeNoiseBasedChunkGenerator(
                            overworldMultiNoiseBiomeSource,
                            overworldNoiseGeneratorSettings,
                            ModBiomes.THE_CRIMSON,
                            ModBiomes.THE_CORRUPTION
                    )),
                    LevelStem.NETHER, nether,
                    LevelStem.END, end
            )));
            context.register(Confluence.asResourceKey(Registries.WORLD_PRESET, "the_crimson"), new WorldPreset(Map.of(
                    LevelStem.OVERWORLD, new LevelStem(dimensionType.getOrThrow(BuiltinDimensionTypes.OVERWORLD), new BannedBiomeNoiseBasedChunkGenerator(
                            overworldMultiNoiseBiomeSource,
                            overworldNoiseGeneratorSettings,
                            ModBiomes.THE_CORRUPTION,
                            ModBiomes.THE_CRIMSON
                    )),
                    LevelStem.NETHER, nether,
                    LevelStem.END, end
            )));
        }
    }

    private static class ConfiguredFeatures {
        private static final ResourceKey<ConfiguredFeature<?, ?>> AMBER_ORE = key("amber_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> AMETHYST_ORE = key("amethyst_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> ASH_HELLSTONE = key("ash_hellstone");
        private static final ResourceKey<ConfiguredFeature<?, ?>> NETHERRACK_HELLSTONE = key("netherrack_hellstone");
        private static final ResourceKey<ConfiguredFeature<?, ?>> COLD_CRYSTAL_ORE = key("cold_crystal_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CRIMTANE_ORE = key("crimtane_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CHLOROPHYTE_ORE = key("chlorophyte_ore");
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
        private static final ResourceKey<ConfiguredFeature<?, ?>> CAVE_CHESTS = key("cave_chests"); // 洞穴金箱
        private static final ResourceKey<ConfiguredFeature<?, ?>> UNDERGROUND_CHESTS = key("underground_chests"); // 地下木箱
        private static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_DROOPING_VINE = key("forest_drooping_vine");
        private static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_CATTAILS = key("forest_cattails");
        private static final ResourceKey<ConfiguredFeature<?, ?>> LIFE_MUSHROOM = key("life_mushroom");
        private static final ResourceKey<ConfiguredFeature<?, ?>> BLINKROOT = key("blinkroot");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DAYBLOOM = key("daybloom");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEATHWEED = key("deathweed");
        private static final ResourceKey<ConfiguredFeature<?, ?>> WATERLEAF = key("waterleaf");
        private static final ResourceKey<ConfiguredFeature<?, ?>> MOONGLOW = key("moonglow");
        private static final ResourceKey<ConfiguredFeature<?, ?>> FIREBLOSSOM = key("fireblossom");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SHIVERTHORN = key("shiverthorn");
        private static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_POT = key("forest_pot");
        private static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_POT = key("jungle_pot");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CORRUPTION_POT = key("corruption_pot");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CRIMSON_POT = key("crimson_pot");
        private static final ResourceKey<ConfiguredFeature<?, ?>> UNDERGROUND_DESERT_POT = key("underground_desert_pot");
        private static final ResourceKey<ConfiguredFeature<?, ?>> TUNDRA_POT = key("tundra_pot");

        private static final ResourceKey<ConfiguredFeature<?, ?>> CORRUPT_DROOPING_VINE = key("corrupt_drooping_vine");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CORRUPT_CATTAILS = key("corrupt_cattails");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CORRUPT_GRASS = key("corrupt_grass");
        private static final ResourceKey<ConfiguredFeature<?, ?>> VILE_MUSHROOM = key("vile_mushroom");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CRIMSON_GRASS = key("crimson_grass");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CRIMSON_DROOPING_VINE = key("crimson_drooping_vine");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CRIMSON_CATTAILS = key("crimson_cattails");
        private static final ResourceKey<ConfiguredFeature<?, ?>> VICIOUS_MUSHROOM = key("vicious_mushroom");
        private static final ResourceKey<ConfiguredFeature<?, ?>> GLOWING_MUSHROOM = key("glowing_mushroom");
        private static final ResourceKey<ConfiguredFeature<?, ?>> GLOWING_MUSHROOM_VINE = key("glowing_mushroom_vine");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SILENT_DROOPING_VINE = key("silent_drooping_vine");
        private static final ResourceKey<ConfiguredFeature<?, ?>> GLOWING_MUSHROOM_CATTAILS = key("glowing_mushroom_cattails");
        private static final ResourceKey<ConfiguredFeature<?, ?>> ASH_GRASS = key("ash_grass");
        private static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_ROSE = key("jungle_rose");
        private static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_SPORE = key("jungle_spore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_DROOPING_VINE = key("jungle_drooping_vine");
        private static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_CATTAILS = key("jungle_cattails");
        private static final ResourceKey<ConfiguredFeature<?, ?>> UNDERGROUND_JUNGLE_GRASS = key("underground_jungle_grass");
        private static final ResourceKey<ConfiguredFeature<?, ?>> UNDERGROUND_JUNGLE_BUSH = key("underground_jungle_bush");
        private static final ResourceKey<ConfiguredFeature<?, ?>> UNDERGROUND_JUNGLE_TREE = key("underground_jungle_tree");
        private static final ResourceKey<ConfiguredFeature<?, ?>> NATURES_GIFT = key("natures_gift");
        private static final ResourceKey<ConfiguredFeature<?, ?>> GRAVITATION_TRAP = key("gravitation_trap");
        private static final ResourceKey<ConfiguredFeature<?, ?>> PNEUMATIC_TRAP = key("pneumatic_trap");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SCULK_TRAP = key("sculk_trap");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SHIMMER_TRAP = key("shimmer_trap");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SCULK_SENSOR_WITH_TNT = key("sculk_sensor_with_tnt");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DART_TRAP = key("dart_trap");
        private static final ResourceKey<ConfiguredFeature<?, ?>> BOULDER_TRAP = key("boulder_trap");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DEATH_CHEST_TRAP = key("death_chest_trap");
        private static final ResourceKey<ConfiguredFeature<?, ?>> LIFE_CRYSTAL = key("life_crystal");
        private static final ResourceKey<ConfiguredFeature<?, ?>> WATER_CHESTS = key("water_chests");
        private static final ResourceKey<ConfiguredFeature<?, ?>> ROLLING_CACTUS = key("rolling_cactus");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SILT_BLOCK = key("silt_block");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SLUSH = key("slush");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CAVE_SANDSTONE_CHESTS = key("cave_sandstone_chests");
        private static final ResourceKey<ConfiguredFeature<?, ?>> UNDERGROUND_SANDSTONE_CHESTS = key("underground_sandstone_chests");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SURFACE_CHESTS = key("surface_chests");
        private static final ResourceKey<ConfiguredFeature<?, ?>> CAVE_FROZEN_CHESTS = key("cave_frozen_chests");
        private static final ResourceKey<ConfiguredFeature<?, ?>> UNDERGROUND_FROZEN_CHESTS = key("underground_frozen_chests");
        private static final ResourceKey<ConfiguredFeature<?, ?>> GEMSTONE_CAVE = key("gemstone_cave");
        private static final ResourceKey<ConfiguredFeature<?, ?>> ADDITIONAL_ANCIENT_DEBRIS = key("additional_ancient_debris");
        private static final ResourceKey<ConfiguredFeature<?, ?>> PLATINUM_VEIN_WITH_DETONATOR = key("platinum_vein_with_detonator");
        private static final ResourceKey<ConfiguredFeature<?, ?>> GOLD_VEIN_WITH_DETONATOR = key("gold_vein_with_detonator");
        private static final ResourceKey<ConfiguredFeature<?, ?>> VOID_GRASS = key("void_grass");
        private static final ResourceKey<ConfiguredFeature<?, ?>> TALL_VOID_GRASS = key("tall_void_grass");
        private static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_GRASS = key("silver_grass");
        private static final ResourceKey<ConfiguredFeature<?, ?>> TALL_SILVER_GRASS = key("tall_silver_grass");
        private static final ResourceKey<ConfiguredFeature<?, ?>> VOID_VIOLET = key("void_violet");
        private static final ResourceKey<ConfiguredFeature<?, ?>> VOID_TREE = key("void_tree");
        private static final ResourceKey<ConfiguredFeature<?, ?>> END_BROKEN_STONE = key("end_broken_stone");
        private static final ResourceKey<ConfiguredFeature<?, ?>> VOID_HUGE_STONE = key("void_huge_stone");
        private static final ResourceKey<ConfiguredFeature<?, ?>> END_HUGE_STONE = key("end_huge_stone");
        private static final ResourceKey<ConfiguredFeature<?, ?>> LUNAR_CORAL_HUGE_STONE = key("lunar_coral_huge_stone");
        private static final ResourceKey<ConfiguredFeature<?, ?>> DRAGONSAL_ORE = key("dragonsal_ore");
        private static final ResourceKey<ConfiguredFeature<?, ?>> LUNAR_CORAL = key("lunar_coral");
        private static final ResourceKey<ConfiguredFeature<?, ?>> MOONGLOW_WILLOW_TREE = key("moonglow_willow_tree");
        private static final ResourceKey<ConfiguredFeature<?, ?>> WITHERED_SEA_SILK = key("withered_sea_silk");

        private static ResourceKey<ConfiguredFeature<?, ?>> key(String path) {
            return Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, path);
        }

        private static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
            TagMatchTest stoneOreReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
            TagMatchTest deepslateOreReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

            ore(context, AMBER_ORE, 8, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.DESERT_FOSSIL_REPLACEMENT), OreBlocks.AMBER_ORE.get().defaultBlockState()));
            ore(context, AMETHYST_ORE, 8, OreConfiguration.target(stoneOreReplaceables, OreBlocks.AMETHYST_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_AMETHYST_ORE.get().defaultBlockState()));
            ore(context, ASH_HELLSTONE, 16, OreConfiguration.target(new BlockMatchTest(NatureBlocks.ASH_BLOCK.get()), OreBlocks.ASH_HELLSTONE.get().defaultBlockState()));
            ore(context, NETHERRACK_HELLSTONE, 10, OreConfiguration.target(new BlockMatchTest(Blocks.NETHERRACK), OreBlocks.HELLSTONE.get().defaultBlockState()));
            ore(context, COLD_CRYSTAL_ORE, 8, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.COLD_CRYSTAL_ORE_REPLACEMENT), OreBlocks.COLD_CRYSTAL_ORE.get().defaultBlockState()));
            ore(context, CRIMTANE_ORE, 7, 1, OreConfiguration.target(stoneOreReplaceables, OreBlocks.CRIMTANE_ORE.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_CRIMTANE_ORE.get().defaultBlockState()));
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
            ore(context, MARINE_GRAVEL, 33, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.MARINE_GRAVEL_REPLACEMENT), NatureBlocks.MARINE_GRAVEL.get().defaultBlockState()));
            ore(context, OPAL_ORE, 4, 0.5F, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.OPAL_ORE_REPLACEMENT), OreBlocks.OPAL_ORE.get().defaultBlockState()));
            ore(context, DESERT_FOSSIL, 33, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.DESERT_FOSSIL_REPLACEMENT), NatureBlocks.DESERT_FOSSIL.get().defaultBlockState()));
            ore(context, SILT_BLOCK, 33, OreConfiguration.target(stoneOreReplaceables, NatureBlocks.SILT_BLOCK.get().defaultBlockState()), OreConfiguration.target(deepslateOreReplaceables, NatureBlocks.SILT_BLOCK.get().defaultBlockState()));
            ore(context, SLUSH, 33, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.SLUSH_REPLACEMENT), NatureBlocks.SLUSH.get().defaultBlockState()));
            ore(context, CHLOROPHYTE_ORE, 9, OreConfiguration.target(new TagMatchTest(ModTags.Blocks.GELSTONE_ORE_REPLACEMENT), OreBlocks.CHLOROPHYTE_ORE.get().defaultBlockState()));

            // 标记生成大小
            ore(context, DEEPSLATE_ADAMANTITE_ORE_STEP_0, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            ore(context, DEEPSLATE_ADAMANTITE_ORE_STEP_1, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            ore(context, DEEPSLATE_ADAMANTITE_ORE_STEP_2, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            ore(context, DEEPSLATE_COBALT_ORE_STEP_0, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            ore(context, DEEPSLATE_COBALT_ORE_STEP_1, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            ore(context, DEEPSLATE_COBALT_ORE_STEP_2, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            ore(context, DEEPSLATE_MYTHRIL_ORE_STEP_0, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_MYTHRIL_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            ore(context, DEEPSLATE_MYTHRIL_ORE_STEP_1, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_MYTHRIL_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            ore(context, DEEPSLATE_MYTHRIL_ORE_STEP_2, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_MYTHRIL_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            ore(context, DEEPSLATE_ORICHALCUM_ORE_STEP_0, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            ore(context, DEEPSLATE_ORICHALCUM_ORE_STEP_1, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            ore(context, DEEPSLATE_ORICHALCUM_ORE_STEP_2, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            ore(context, DEEPSLATE_PALLADIUM_ORE_STEP_0, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_PALLADIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            ore(context, DEEPSLATE_PALLADIUM_ORE_STEP_1, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_PALLADIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            ore(context, DEEPSLATE_PALLADIUM_ORE_STEP_2, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_PALLADIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));
            ore(context, DEEPSLATE_TITANIUM_ORE_STEP_0, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 0)));
            ore(context, DEEPSLATE_TITANIUM_ORE_STEP_1, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 1)));
            ore(context, DEEPSLATE_TITANIUM_ORE_STEP_2, 21, OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, 2)));

            gemTree(context, ModFeatures.Configured.AMBER_TREE, NatureBlocks.AMBER_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.AMETHYST_TREE, NatureBlocks.AMETHYST_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.DIAMOND_TREE, NatureBlocks.DIAMOND_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.JADE_TREE, NatureBlocks.JADE_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.RUBY_TREE, NatureBlocks.RUBY_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.SAPPHIRE_TREE, NatureBlocks.SAPPHIRE_BRANCHES.get());
            gemTree(context, ModFeatures.Configured.TOPAZ_TREE, NatureBlocks.TOPAZ_BRANCHES.get());

            simple(context, CRIMSON_ALTAR, FunctionalBlocks.CRIMSON_ALTAR.get());
            simple(context, DEMON_ALTAR, FunctionalBlocks.DEMON_ALTAR.get());
            simple(context, FOREST_POT, PotBlocks.FOREST_POT.get());
            simple(context, JUNGLE_POT, PotBlocks.JUNGLE_POT.get());
            simple(context, CORRUPTION_POT, PotBlocks.CORRUPTION_POT.get());
            simple(context, CRIMSON_POT, PotBlocks.CRIMSON_POT.get());
            simple(context, UNDERGROUND_DESERT_POT, PotBlocks.UNDERGROUND_DESERT_POT.get());
            simple(context, TUNDRA_POT, PotBlocks.TUNDRA_POT.get());
            simple(context, JUNGLE_SPORE, NatureBlocks.JUNGLE_SPORE.get());
            simple(context, GRAVITATION_TRAP, FunctionalBlocks.GRAVITATION_TRAP.value());
            simple(context, PNEUMATIC_TRAP, FunctionalBlocks.PNEUMATIC_TRAP.value());
            simple(context, SCULK_TRAP, FunctionalBlocks.SCULK_TRAP.value());
            simple(context, SHIMMER_TRAP, FunctionalBlocks.SHIMMER_TRAP.value());
            simple(context, LIFE_CRYSTAL, NatureBlocks.LIFE_CRYSTAL_BLOCK.get());
            simple(context, ROLLING_CACTUS, FunctionalBlocks.ROLLING_CACTUS_BOULDER.get());

            herb(context, LIFE_MUSHROOM, 12, NatureBlocks.LIFE_MUSHROOM.get());
            herb(context, BLINKROOT, 50, ModBlocks.BLINKROOT.get());
            herb(context, DAYBLOOM, 32, ModBlocks.DAYBLOOM.get());
            herb(context, DEATHWEED, 12, ModBlocks.DEATHWEED.get());
            herb(context, WATERLEAF, 32, ModBlocks.WATERLEAF.get());
            herb(context, MOONGLOW, 32, ModBlocks.MOONGLOW.get());
            herb(context, FIREBLOSSOM, 40, ModBlocks.FIREBLOSSOM.get());
            herb(context, SHIVERTHORN, 32, ModBlocks.SHIVERTHORN.get());
            herb(context, CORRUPT_GRASS, 45, NatureBlocks.CORRUPT_GRASS.get());
            herb(context, VILE_MUSHROOM, 12, NatureBlocks.VILE_MUSHROOM.get());
            herb(context, CRIMSON_GRASS, 28, NatureBlocks.CRIMSON_GRASS.get());
            herb(context, VOID_GRASS, 180, NatureBlocks.VOID_GRASS.get());
            herb(context, TALL_VOID_GRASS, 180, NatureBlocks.TALL_VOID_GRASS.get());
            herb(context, SILVER_GRASS, 180, NatureBlocks.SILVER_GRASS.get());
            herb(context, TALL_SILVER_GRASS, 180, NatureBlocks.TALL_SILVER_GRASS.get());
            herb(context, VOID_VIOLET, 180, NatureBlocks.VOID_VIOLET.get());
            herb(context, VICIOUS_MUSHROOM, 12, NatureBlocks.VICIOUS_MUSHROOM.get());
            herb(context, GLOWING_MUSHROOM, 180, NatureBlocks.GLOWING_MUSHROOM.get());
            herb(context, ASH_GRASS, 180, NatureBlocks.ASH_GRASS.get());
            herb(context, JUNGLE_ROSE, 16, NatureBlocks.JUNGLE_ROSE.get());
            herb(context, NATURES_GIFT, 4, NatureBlocks.NATURES_GIFT.get());

            droopingVineTree(context, ModFeatures.Configured.YELLOW_WILLOW_TREE, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.LOG.get(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.LEAVES.get(), NatureBlocks.YELLOW_WILLOW_DROOPING_LEAVES.get(), 6);

            register(context, CORRUPT_DROOPING_VINE, ModFeatures.BLOCK_POST.get(), new BlockPostFeature.Config(BlockStateProvider.simple(NatureBlocks.CORRUPT_DROOPING_VINE.get()), false, 1, 9, Direction.DOWN, false));
            register(context, ModFeatures.Configured.EBONY_TREE, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
                    new WeightedPlacedFeature(direct(
                            Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                                    BlockStateProvider.simple(NatureBlocks.EBONY_LOG_BLOCKS.LOG.get()),
                                    new StraightTrunkPlacer(6, 0, 0),
                                    BlockStateProvider.simple(Blocks.AIR),
                                    new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, 3),
                                    new TwoLayersFeatureSize(1, 0, 1)
                            ).ignoreVines().dirt(BlockStateProvider.simple(Blocks.DIRT)).build(),
                            BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.EBONY_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
                    ), 0.05F),
                    new WeightedPlacedFeature(direct(
                            Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                                    BlockStateProvider.simple(NatureBlocks.EBONY_LOG_BLOCKS.LOG.get()),
                                    new StraightTrunkPlacer(5, 2, 0),
                                    BlockStateProvider.simple(Blocks.AIR),
                                    new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, 3),
                                    new TwoLayersFeatureSize(1, 0, 1)
                            ).ignoreVines().dirt(BlockStateProvider.simple(NatureBlocks.CORRUPT_GRASS_BLOCK.get())).build(),
                            BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.EBONY_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
                    ), 0.05F)
            ), direct(
                    ModFeatures.DROOPING_VINE_TREE.get(), new DroopingVineTreeFeature.Config(BlockStateProvider.simple(NatureBlocks.EBONY_LOG_BLOCKS.LOG.get()), BlockStateProvider.simple(NatureBlocks.EBONY_LOG_BLOCKS.LEAVES.get()), BlockStateProvider.simple(NatureBlocks.CORRUPT_DROOPING_VINE.get()), 5),
                    BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.EBONY_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
            )));
            register(context, ModFeatures.Configured.SHADOW_TREE, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(Collections.singletonList(
                    new WeightedPlacedFeature(direct(
                            Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                                    BlockStateProvider.simple(NatureBlocks.SHADOW_LOG_BLOCKS.LOG.get()),
                                    new StraightTrunkPlacer(5, 2, 0),
                                    BlockStateProvider.simple(NatureBlocks.SHADOW_LOG_BLOCKS.LEAVES.get()),
                                    new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, 3),
                                    new TwoLayersFeatureSize(1, 0, 1)
                            ).ignoreVines().dirt(BlockStateProvider.simple(NatureBlocks.CRIMSON_GRASS_BLOCK.get())).build(),
                            BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.SHADOW_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
                    ), 0.5F)
            ), direct(
                    ModFeatures.DROOPING_VINE_TREE.get(), new DroopingVineTreeFeature.Config(BlockStateProvider.simple(NatureBlocks.SHADOW_LOG_BLOCKS.LOG.get()), BlockStateProvider.simple(NatureBlocks.SHADOW_LOG_BLOCKS.LEAVES.get()), BlockStateProvider.simple(NatureBlocks.CRIMSON_DROOPING_VINE.get()), 5),
                    BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.SHADOW_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
            )));
            register(context, CRIMSON_DROOPING_VINE, ModFeatures.BLOCK_POST.get(), new BlockPostFeature.Config(BlockStateProvider.simple(NatureBlocks.CRIMSON_DROOPING_VINE.get()), false, 1, 9, Direction.DOWN, false));
            register(context, ModFeatures.Configured.GLOWING_MUSHROOM_TREE, ModFeatures.MUSHROOM_TREE.get(), new MushroomTreeFeature.Config(
                    BlockStateProvider.simple(NatureBlocks.GLOWING_MUSHROOM_STEM_BLOCK.get()),
                    BlockStateProvider.simple(NatureBlocks.GLOWING_MUSHROOM_PILEUS_BLOCK.get()),
                    BlockStateProvider.simple(NatureBlocks.GLOWING_MUSHROOM_INDUSIUM_BLOCK.get()),
                    4,
                    1
            ));
            register(context, ModFeatures.Configured.LIFE_MUSHROOM_TREE, ModFeatures.MUSHROOM_TREE.get(), new MushroomTreeFeature.Config(
                    BlockStateProvider.simple(NatureBlocks.LIFE_MUSHROOM_STEM_BLOCK.get()),
                    BlockStateProvider.simple(NatureBlocks.LIFE_MUSHROOM_PILEUS_BLOCK.get()),
                    BlockStateProvider.simple(NatureBlocks.LIFE_MUSHROOM_INDUSIUM_BLOCK.get()),
                    4,
                    1
            ));
            register(context, ModFeatures.Configured.HUGE_LIFE_MUSHROOM_TREE, ModFeatures.HUGE_MUSHROOM_TREE.get(), new HugeMushroomTreeFeature.Config(
                    BlockStateProvider.simple(NatureBlocks.LIFE_MUSHROOM_STEM_BLOCK.get()),
                    BlockStateProvider.simple(NatureBlocks.LIFE_MUSHROOM_PILEUS_BLOCK.get()),
                    BlockStateProvider.simple(NatureBlocks.LIFE_MUSHROOM_STEM_BLOCK.get()),
                    BlockStateProvider.simple(NatureBlocks.HANGING_MYCELIUM.get()),
                    BlockStateProvider.simple(NatureBlocks.MYCELIAL_DIRT.get()),
                    BlockStateProvider.simple(Blocks.RED_MUSHROOM),
                    BlockStateProvider.simple(Blocks.BROWN_MUSHROOM),
                    BlockStateProvider.simple(Blocks.MYCELIUM)
            ));
            register(context, GLOWING_MUSHROOM_VINE, ModFeatures.BLOCK_POST.get(), new BlockPostFeature.Config(BlockStateProvider.simple(NatureBlocks.GLOWING_MUSHROOM_VINE.get()), false, 1, 9, Direction.DOWN, false));
            register(context, SILENT_DROOPING_VINE, ModFeatures.BLOCK_POST.get(), new BlockPostFeature.Config(BlockStateProvider.simple(NatureBlocks.SILENT_DROOPING_VINE.get()), false, 1, 15, Direction.DOWN, false));
            register(context, GLOWING_MUSHROOM_CATTAILS, ModFeatures.CATTAILS.get(), new CattailsFeature.Config(BlockStateProvider.simple(NatureBlocks.GLOWING_MUSHROOM_CATTAIL_BLOCK.get()), 0, 1.0F, 20));
            register(context, JUNGLE_CATTAILS, ModFeatures.CATTAILS.get(), new CattailsFeature.Config(BlockStateProvider.simple(NatureBlocks.JUNGLE_CATTAIL_BLOCK.get()), 2, 0.5F, 20));
            register(context, CORRUPT_CATTAILS, ModFeatures.CATTAILS.get(), new CattailsFeature.Config(BlockStateProvider.simple(NatureBlocks.EBONY_CATTAIL_BLOCK.get()), 2, 0.5F, 20));
            register(context, CRIMSON_CATTAILS, ModFeatures.CATTAILS.get(), new CattailsFeature.Config(BlockStateProvider.simple(NatureBlocks.CRIMSON_CATTAIL_BLOCK.get()), 2, 0.5F, 20));
            register(context, FOREST_CATTAILS, ModFeatures.CATTAILS.get(), new CattailsFeature.Config(BlockStateProvider.simple(NatureBlocks.CATTAIL_BLOCK.get()), 2, 0.5F, 20));
            register(context, ModFeatures.Configured.PINE_TREE, ModFeatures.PINE_TREE.get(), new PineTreeFeature.Config(
                            BlockStateProvider.simple(NatureBlocks.PINE_LOG_BLOCKS.LOG.get()),
                            BlockStateProvider.simple(NatureBlocks.PINE_DROOPING_VINE.get()),
                            BlockStateProvider.simple(NatureBlocks.PINE_LOG_BLOCKS.LEAVES.get()),
                            14,
                            10
                    )
            );
            register(context, MOONGLOW_WILLOW_TREE, ModFeatures.MOONGLOW_WILLOW_TREE.get(), new MoonglowWillowTreeFeature.Config(
                            BlockStateProvider.simple(NatureBlocks.MOONGLOW_WILLOW_LOG_BLOCKS.LOG.get()),
                            BlockStateProvider.simple(NatureBlocks.MOONGLOW_WILLOW_DROOPING_VINE.get()),
                            BlockStateProvider.simple(NatureBlocks.MOONGLOW_WILLOW_LOG_BLOCKS.LEAVES.get())
                    )
            );
            register(context, LUNAR_CORAL, ModFeatures.LUNAR_CORAL.get(), new LunarCoralFeature.Config(
                            BlockStateProvider.simple(NatureBlocks.LUNAR_CORAL_BLOCK.get()),
                            BlockStateProvider.simple(NatureBlocks.LUNAR_CORAL.get()),
                            BlockStateProvider.simple(NatureBlocks.LUNAR_CORAL_FAN.get())
                    )
            );
            register(context, WITHERED_SEA_SILK, ModFeatures.BLOCK_POST.get(), new BlockPostFeature.Config(
                            BlockStateProvider.simple(NatureBlocks.WITHERED_SEA_SILK.get()),
                            false,
                            1,
                            35,
                            Direction.UP,
                            false
                    )
            );
            register(context, ModFeatures.Configured.CHINESE_PINE_TREE, ModFeatures.CHINESE_STYLE_PINE_TREE.get(), new ChineseStylePineTreeFeature.Config(
                            BlockStateProvider.simple(NatureBlocks.PINE_LOG_BLOCKS.LOG.get()),
                            BlockStateProvider.simple(NatureBlocks.PINE_DROOPING_VINE.get()),
                            BlockStateProvider.simple(NatureBlocks.PINE_LOG_BLOCKS.LEAVES.get()),
                            7,
                            4
                    )
            );
            register(context, ModFeatures.Configured.BAOBAB_TREE, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
                    new WeightedPlacedFeature(direct(
                            ModFeatures.BAOBAB_TREE.get(), new BaobabTreeFeature.Config(
                                    BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.LOG.get()),
                                    BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.WOOD.get()),
                                    BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.WOOD.get()),
                                    BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.LEAVES.get()),
                                    BlockStateProvider.simple(Blocks.AIR),
                                    8
                            ),
                            BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.BAOBAB_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
                    ), 0.05F),
                    new WeightedPlacedFeature(direct(
                            ModFeatures.BAOBAB_TREE.get(), new BaobabTreeFeature.Config(
                                    BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.LOG.get()),
                                    BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.WOOD.get()),
                                    BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.WOOD.get()),
                                    BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.LEAVES.get()),
                                    BlockStateProvider.simple(Blocks.WATER),
                                    8
                            ),
                            BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.BAOBAB_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
                    ), 0.05F)
            ), direct(
                    ModFeatures.BAOBAB_TREE.get(), new BaobabTreeFeature.Config(
                            BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.LOG.get()),
                            BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.WOOD.get()),
                            BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.WOOD.get()),
                            BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.LEAVES.get()),
                            BlockStateProvider.simple(NatureBlocks.BAOBAB_LOG_BLOCKS.STRIPPED_LOG.get()),
                            8
                    ),
                    BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.BAOBAB_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
            )));
            register(context, VOID_TREE, ModFeatures.VOID_TREE.get(), new VoidTreeFeature.Config(BlockStateProvider.simple(NatureBlocks.VOID_LOG_BLOCKS.LOG.get().defaultBlockState()), BlockStateProvider.simple(NatureBlocks.VOID_TREE_ROOT_BLOCK.get().defaultBlockState()), BlockStateProvider.simple(NatureBlocks.VOID_LOG_BLOCKS.LEAVES.get().defaultBlockState())));
            register(context, END_BROKEN_STONE, ModFeatures.BROKEN_STONE.get(), new BrokenStoneFeature.Config(30, 50, 5, 5, BlockStateProvider.simple(NatureBlocks.VOID_WEAVE.get()), 0.2F, BrokenStoneFeature.ResidueType.ALL, ModTags.Blocks.END_BROKEN_STONE_CAN_MOVE));
            register(context, VOID_HUGE_STONE, ModFeatures.HUGE_STONE.get(), new HugeStoneFeature.Config(7, 8, BlockStateProvider.simple(NatureBlocks.GLOOM_OBSIDIAN.get()), BlockStateProvider.simple(NatureBlocks.VOID_WEAVE.get()), 2.0F));
            register(context, END_HUGE_STONE, ModFeatures.HUGE_STONE.get(), new HugeStoneFeature.Config(4, 4, BlockStateProvider.simple(Blocks.END_STONE), BlockStateProvider.simple(NatureBlocks.DEAD_LUNAR_CORAL_BLOCK.get()), 2.0F));
            register(context, LUNAR_CORAL_HUGE_STONE, ModFeatures.HUGE_STONE.get(), new HugeStoneFeature.Config(4, 4, BlockStateProvider.simple(NatureBlocks.DEAD_LUNAR_CORAL_BLOCK.get()), BlockStateProvider.simple(NatureBlocks.LUNAR_CORAL_BLOCK.get()), 2.0F));
            register(context, DRAGONSAL_ORE, ModFeatures.BILAYER_ORE.get(), new BilayerOreFeature.Config(1, 5, BlockStateProvider.simple(OreBlocks.DRAGONSAL_ORE.get()), BlockStateProvider.simple(OreBlocks.LUNARTEAR_ORE.get()), ModTags.Blocks.DRAGONSAL_ORE_REPLACE));
            register(context, THIN_ICE_PATCH, ModFeatures.COLUMN_PATCH.get(), new ColumnPatchFeature.Config(3, 4, 32, 32, 0.5F, BlockStateProvider.simple(NatureBlocks.THIN_ICE_BLOCK.get())));
            register(context, POWDER_SNOW_PATCH, ModFeatures.COLUMN_PATCH.get(), new ColumnPatchFeature.Config(0, 2, 10, 32, 0.3F, BlockStateProvider.simple(Blocks.POWDER_SNOW)));
            register(context, FALLING_SAND_TRAP, ModFeatures.FALLING_SAND_TRAP.get(), new FallingSandTrapFeature.Config(BlockStateProvider.simple(Blocks.SAND), 4, 4, 4, 16));
            register(context, CAVE_CHESTS, ModFeatures.SIMPLE_BLOCK_NBT.get(), new SimpleBlockNBTFeature.Config(new WeightedStateProvider(randomState(ChestBlocks.GOLDEN_CHEST.get().defaultBlockState().setValue(BaseChestBlock.UNLOCKED, true), ChestBlock.FACING)), tag -> tag.putString("LootTable", "confluence:chests/cave_chests")));
            register(context, UNDERGROUND_CHESTS, ModFeatures.SIMPLE_BLOCK_NBT.get(), new SimpleBlockNBTFeature.Config(new WeightedStateProvider(randomState(Blocks.CHEST.defaultBlockState(), ChestBlock.FACING)), tag -> tag.putString("LootTable", "confluence:chests/underground_chests")));
            register(context, FOREST_DROOPING_VINE, ModFeatures.BLOCK_POST.get(), new BlockPostFeature.Config(BlockStateProvider.simple(NatureBlocks.FOREST_DROOPING_VINE.get()), false, 1, 9, Direction.DOWN, false));
            register(context, ModFeatures.Configured.ASH_TREE, ModFeatures.BRANCH_TREE.get(), new BranchTreeFeature.Config(BlockStateProvider.simple(NatureBlocks.ASH_LOG_BLOCKS.LOG.get()), BlockStateProvider.simple(NatureBlocks.ASH_BRANCHES.get()), 7, 3));
            register(context, JUNGLE_DROOPING_VINE, ModFeatures.BLOCK_POST.get(), new BlockPostFeature.Config(BlockStateProvider.simple(NatureBlocks.JUNGLE_DROOPING_VINE.get()), false, 1, 9, Direction.DOWN, false));
            register(context, UNDERGROUND_JUNGLE_GRASS, Feature.RANDOM_PATCH, new RandomPatchConfiguration(32, 7, 3, direct(
                    Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.SHORT_GRASS.defaultBlockState(), 3).add(Blocks.FERN.defaultBlockState(), 1).build())),
                    BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.matchesBlocks(Blocks.AIR), BlockPredicate.not(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.PODZOL))))
            )));
            register(context, UNDERGROUND_JUNGLE_BUSH, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(Blocks.JUNGLE_LOG),
                    new StraightTrunkPlacer(1, 0, 0),
                    BlockStateProvider.simple(Blocks.OAK_LEAVES),
                    new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 2),
                    new TwoLayersFeatureSize(0, 0, 0)
            ).build());
            register(context, UNDERGROUND_JUNGLE_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(Blocks.JUNGLE_LOG),
                    new StraightTrunkPlacer(4, 8, 0),
                    BlockStateProvider.simple(Blocks.JUNGLE_LEAVES),
                    new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, 3),
                    new TwoLayersFeatureSize(1, 0, 1)
            ).decorators(List.of(
                    new CocoaDecorator(0.2F),
                    TrunkVineDecorator.INSTANCE,
                    new LeaveVineDecorator(0.25F))
            ).ignoreVines().build());
            register(context, SCULK_SENSOR_WITH_TNT, ModFeatures.SCULK_SENSOR_WITH_TNT.get(), new SculkSensorWithTNTFeature.Config(64));
            register(context, DART_TRAP, ModFeatures.DART_TRAP.get(), new DartTrapFeature.Config(24, 32));
            register(context, BOULDER_TRAP, ModFeatures.BOULDER_TRAP.get(), new BoulderTrapFeature.Config(FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState(), 64));
            register(context, DEATH_CHEST_TRAP, ModFeatures.DEATH_CHEST_TRAP.get(), new DeathChestTrapFeature.Config(24, FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState(), 5, 64, 3, 32, ModLootTables.CAVE_CHESTS));
            register(context, WATER_CHESTS, ModFeatures.SIMPLE_BLOCK_NBT.get(), new SimpleBlockNBTFeature.Config(BlockStateProvider.simple(ChestBlocks.WATER_CHEST.get().defaultBlockState().setValue(BaseChestBlock.UNLOCKED, true).setValue(ChestBlock.WATERLOGGED, true)), tag -> tag.putString("LootTable", "confluence:chests/water_chests")));
            register(context, CAVE_SANDSTONE_CHESTS, ModFeatures.SIMPLE_BLOCK_NBT.get(), new SimpleBlockNBTFeature.Config(BlockStateProvider.simple(ChestBlocks.SANDSTONE_CHEST.get().defaultBlockState().setValue(BaseChestBlock.UNLOCKED, true)), tag -> tag.putString("LootTable", "confluence:chests/cave_sandstone_chests")));
            register(context, UNDERGROUND_SANDSTONE_CHESTS, ModFeatures.SIMPLE_BLOCK_NBT.get(), new SimpleBlockNBTFeature.Config(BlockStateProvider.simple(ChestBlocks.SANDSTONE_CHEST.get().defaultBlockState().setValue(BaseChestBlock.UNLOCKED, true)), tag -> tag.putString("LootTable", "confluence:chests/sandstone_chests")));
            register(context, SURFACE_CHESTS, ModFeatures.SIMPLE_BLOCK_NBT.get(), new SimpleBlockNBTFeature.Config(BlockStateProvider.simple(Blocks.CHEST), tag -> tag.putString("LootTable", "confluence:chests/surface_chests")));
            register(context, CAVE_FROZEN_CHESTS, ModFeatures.SIMPLE_BLOCK_NBT.get(), new SimpleBlockNBTFeature.Config(BlockStateProvider.simple(ChestBlocks.FROZEN_CHEST.get().defaultBlockState().setValue(BaseChestBlock.UNLOCKED, true)), tag -> tag.putString("LootTable", "confluence:chests/cave_frozen_chests")));
            register(context, UNDERGROUND_FROZEN_CHESTS, ModFeatures.SIMPLE_BLOCK_NBT.get(), new SimpleBlockNBTFeature.Config(BlockStateProvider.simple(ChestBlocks.FROZEN_CHEST.get().defaultBlockState().setValue(BaseChestBlock.UNLOCKED, true)), tag -> tag.putString("LootTable", "confluence:chests/frozen_chests")));
            register(context, ModFeatures.Configured.PALM_TREE, ModFeatures.PALM_TREE.get(), new PalmTreeFeature.Config(
                    BlockStateProvider.simple(NatureBlocks.PALM_LOG_BLOCKS.LOG.get()),
                    BlockStateProvider.simple(NatureBlocks.PALM_LOG_BLOCKS.LEAVES.get().defaultBlockState().setValue(LeavesBlock.DISTANCE, 1).setValue(PalmLeaves.TYPE, SlabType.BOTTOM)),
                    BlockStateProvider.simple(NatureBlocks.PALM_LOG_BLOCKS.LEAVES.get().defaultBlockState().setValue(LeavesBlock.DISTANCE, 1).setValue(PalmLeaves.TYPE, SlabType.TOP)),
                    BlockStateProvider.simple(NatureBlocks.PALM_LOG_BLOCKS.LEAVES.get().defaultBlockState().setValue(LeavesBlock.DISTANCE, 1).setValue(PalmLeaves.TYPE, SlabType.DOUBLE))
            ));
            register(context, GEMSTONE_CAVE, ModFeatures.GEMSTONE_CAVE.get(), new GemstoneCaveFeature.Config(3));
            register(context, ModFeatures.Configured.PEARL_TREE, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
                    new WeightedPlacedFeature(direct(
                            Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                                    BlockStateProvider.simple(NatureBlocks.PEARL_LOG_BLOCKS.LOG.get()),
                                    new ForkingTrunkPlacer(5, 0, 0),
                                    BlockStateProvider.simple(NatureBlocks.PEARL_LOG_BLOCKS.LEAVES.get()),
                                    new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO),
                                    new TwoLayersFeatureSize(1, 0, 2)
                            ).ignoreVines().dirt(BlockStateProvider.simple(Blocks.DIRT)).build(),
                            BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.PEARL_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
                    ), 0.5F),
                    new WeightedPlacedFeature(direct(
                            Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                                    BlockStateProvider.simple(NatureBlocks.PEARL_LOG_BLOCKS.LOG.get()),
                                    new FancyTrunkPlacer(7, 0, 0),
                                    BlockStateProvider.simple(NatureBlocks.PEARL_LOG_BLOCKS.LEAVES.get()),
                                    new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                                    new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
                            ).ignoreVines().dirt(BlockStateProvider.simple(Blocks.DIRT)).build(),
                            BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.PEARL_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
                    ), 0.5F),
                    new WeightedPlacedFeature(direct(
                            Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                                    BlockStateProvider.simple(NatureBlocks.PEARL_LOG_BLOCKS.LOG.get()),
                                    new StraightTrunkPlacer(5, 2, 1),
                                    BlockStateProvider.simple(NatureBlocks.PEARL_LOG_BLOCKS.LEAVES.get()),
                                    new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)),
                                    new TwoLayersFeatureSize(2, 0, 2)
                            ).ignoreVines().dirt(BlockStateProvider.simple(Blocks.DIRT)).build(),
                            BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.PEARL_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
                    ), 0.5F),
                    new WeightedPlacedFeature(direct(
                            Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                                    BlockStateProvider.simple(NatureBlocks.PEARL_LOG_BLOCKS.LOG.get()),
                                    new StraightTrunkPlacer(5, 2, 0),
                                    BlockStateProvider.simple(NatureBlocks.PEARL_LOG_BLOCKS.LEAVES.get()),
                                    new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.ZERO, 3),
                                    new TwoLayersFeatureSize(1, 0, 1)
                            ).ignoreVines().dirt(BlockStateProvider.simple(NatureBlocks.HALLOW_GRASS_BLOCK.get())).build(),
                            BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.PEARL_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
                    ), 0.5F)
            ), direct(
                    Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                            BlockStateProvider.simple(NatureBlocks.PEARL_LOG_BLOCKS.LOG.get()),
                            new StraightTrunkPlacer(7, 0, 0),
                            BlockStateProvider.simple(NatureBlocks.PEARL_LOG_BLOCKS.LEAVES.get()),
                            new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 2),
                            new TwoLayersFeatureSize(0, 0, 0)
                    ).ignoreVines().dirt(BlockStateProvider.simple(Blocks.DIRT)).build(),
                    BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.PEARL_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO))
            )));
            register(context, LivingMahoganyTreeStructure.LIVING_IVY_CHESTS, ModFeatures.SIMPLE_BLOCK_NBT.get(), new SimpleBlockNBTFeature.Config(BlockStateProvider.simple(ChestBlocks.IVY_CHEST.get()), tag -> tag.putString("LootTable", "confluence:chests/ivy_chests")));
            register(context, MarbleCaveStructure.MARBLE_CAVE_POT, ModFeatures.GROUND_BLOCK.get(), new GroundBlockFeature.Config(BlockStateProvider.simple(PotBlocks.MARBLE_CAVE_POT.get()), 20));
            register(context, MeteoriteTracker.METEORITE, ModFeatures.METEORITE.get(), new MeteoriteFeature.Config(7, 0.4F, 0.1F, 0.15F));
            register(context, ADDITIONAL_ANCIENT_DEBRIS, Feature.SCATTERED_ORE, new OreConfiguration(Collections.singletonList(OreConfiguration.target(new TagMatchTest(BlockTags.BASE_STONE_NETHER), Blocks.ANCIENT_DEBRIS.defaultBlockState())), 3, 1));
            register(context, DungeonStructure.DUNGEON_LOST_PAPER, ModFeatures.GROUND_BLOCK_NBT.get(), new GroundBlockNBTFeature.Config(BlockStateProvider.simple(DecorativeBlocks.LOST_PAPER_BLOCK.get()), 3, tag -> tag.putString("LootTable", "confluence:lost_paper/dungeon")));
            register(context, DungeonStructure.DUNGEON_POT, ModFeatures.GROUND_BLOCK.get(), new GroundBlockFeature.Config(BlockStateProvider.simple(PotBlocks.DUNGEON_POT.get()), 3));
            register(context, DungeonStructure.DUNGEON_REMAINS, ModFeatures.GROUND_BLOCK.get(), new GroundBlockFeature.Config(BlockStateProvider.simple(DecorativeBlocks.REMAINS_BLOCK.get().defaultBlockState().setValue(RemainsBlock.FACING, Direction.UP).setValue(RemainsBlock.IS_FACE_STURDY, true)), 3));
            register(context, MineTunnelsStructure.RAIL_SUPPORT, ModFeatures.RAIL_SUPPORT.get(), new RailSupportFeature.Config(BlockStateProvider.simple(Blocks.OAK_PLANKS)));
            register(context, MineTunnelsStructure.RAIL_BOULDER, ModFeatures.RAIL_TRAP.get(), new RailTrapFeature.Config(BlockStateProvider.simple(FunctionalBlocks.NORMAL_BOULDER.get())));
            register(context, MineTunnelsStructure.RAIL_DART, ModFeatures.RAIL_TRAP.get(), new RailTrapFeature.Config(BlockStateProvider.simple(FunctionalBlocks.DART_TRAP.get().defaultBlockState().setValue(BlockStateProperties.FACING, Direction.DOWN))));
            register(context, MineTunnelsStructure.RAIL_SPRUCE_LOG, ModFeatures.BLOCK_POST.get(), new BlockPostFeature.Config(BlockStateProvider.simple(Blocks.SPRUCE_LOG), true, 1, 1, Direction.DOWN, false));
            register(context, MineTunnelsStructure.RAIL_STONE_BRICKS, ModFeatures.BLOCK_POST.get(), new BlockPostFeature.Config(BlockStateProvider.simple(Blocks.STONE_BRICKS), true, 1, 1, Direction.DOWN, false));
            register(context, MineTunnelsStructure.RAIL_TUFF_BRICKS, ModFeatures.BLOCK_POST.get(), new BlockPostFeature.Config(BlockStateProvider.simple(Blocks.TUFF_BRICKS), true, 1, 1, Direction.DOWN, false));
            register(context, PLATINUM_VEIN_WITH_DETONATOR, ModFeatures.DETONATOR_FEATURE.get(), new DetonatorFeature.Config(Holder.direct(new ConfiguredFeature<>(Feature.ORE,
                    new OreConfiguration(List.of(
                            OreConfiguration.target(stoneOreReplaceables, OreBlocks.PLATINUM_ORE.get().defaultBlockState()),
                            OreConfiguration.target(deepslateOreReplaceables, OreBlocks.DEEPSLATE_PLATINUM_ORE.get().defaultBlockState())
                    ), 20)
            )), 32, 1));
            register(context, GOLD_VEIN_WITH_DETONATOR, ModFeatures.DETONATOR_FEATURE.get(), new DetonatorFeature.Config(Holder.direct(new ConfiguredFeature<>(Feature.ORE,
                    new OreConfiguration(List.of(
                            OreConfiguration.target(stoneOreReplaceables, Blocks.GOLD_ORE.defaultBlockState()),
                            OreConfiguration.target(deepslateOreReplaceables, Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState())
                    ), 27)
            )), 32, 1));
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

        private static void droopingVineTree(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, Block trunk, Block leaves, Block drooping_leave, int height) {
            register(context, key, ModFeatures.DROOPING_VINE_TREE.get(), new DroopingVineTreeFeature.Config(BlockStateProvider.simple(trunk), BlockStateProvider.simple(leaves), BlockStateProvider.simple(drooping_leave), height));
        }

        private static void herb(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, int tries, Block herbBlock) {
            register(context, key, Feature.RANDOM_PATCH, new RandomPatchConfiguration(tries, 7, 3, direct(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(herbBlock)), BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.AIR)))));
        }

        private static void simple(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, Block block) {
            simple(context, key, block.defaultBlockState());
        }

        private static void simple(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, BlockState blockState) {
            register(context, key, Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(blockState)));
        }

        private static <T extends Comparable<T>, V extends T> SimpleWeightedRandomList<BlockState> randomState(BlockState blockState, Property<T> property) {
            SimpleWeightedRandomList.Builder<BlockState> builder = SimpleWeightedRandomList.builder();
            for (Comparable<?> value : property.getPossibleValues()) {
                builder.add(blockState.setValue(property, (V) value), 1);
            }
            return builder.build();
        }

        private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> direct(F feature, FC config, PlacementModifier... modifiers) {
            return Holder.direct(new PlacedFeature(Holder.direct(new ConfiguredFeature<>(feature, config)), Arrays.stream(modifiers).toList()));
        }
    }

    private static class PlacedFeatures {
        private static final ResourceKey<PlacedFeature> AMBER_ORE = key("amber_ore");
        private static final ResourceKey<PlacedFeature> AMETHYST_ORE = key("amethyst_ore");
        private static final ResourceKey<PlacedFeature> ASH_HELLSTONE = key("ash_hellstone");
        private static final ResourceKey<PlacedFeature> NETHERRACK_HELLSTONE = key("netherrack_hellstone");
        private static final ResourceKey<PlacedFeature> COLD_CRYSTAL_ORE = key("cold_crystal_ore");
        private static final ResourceKey<PlacedFeature> CRIMTANE_ORE = key("crimtane_ore");
        private static final ResourceKey<PlacedFeature> CHLOROPHYTE_ORE = key("chlorophyte_ore");
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
        private static final ResourceKey<PlacedFeature> FOREST_POT = key("forest_pot");
        private static final ResourceKey<PlacedFeature> JUNGLE_POT = key("jungle_pot");
        private static final ResourceKey<PlacedFeature> CORRUPTION_POT = key("corruption_pot");
        private static final ResourceKey<PlacedFeature> CRIMSON_POT = key("crimson_pot");
        private static final ResourceKey<PlacedFeature> UNDERGROUND_DESERT_POT = key("underground_desert_pot");
        private static final ResourceKey<PlacedFeature> TUNDRA_POT = key("tundra_pot");
        private static final ResourceKey<PlacedFeature> CAVE_CHESTS = key("cave_chests");
        private static final ResourceKey<PlacedFeature> CAVE_CHESTS_SMALL = key("cave_chests_small");
        private static final ResourceKey<PlacedFeature> UNDERGROUND_CHESTS = key("underground_chests");
        private static final ResourceKey<PlacedFeature> UNDERGROUND_CHESTS_SMALL = key("underground_chests_small");
        private static final ResourceKey<PlacedFeature> FOREST_DROOPING_VINE = key("forest_drooping_vine");
        private static final ResourceKey<PlacedFeature> FOREST_CATTAILS = key("forest_cattails");
        private static final ResourceKey<PlacedFeature> BLINKROOT = key("blinkroot");
        private static final ResourceKey<PlacedFeature> DAYBLOOM = key("daybloom");
        private static final ResourceKey<PlacedFeature> DEATHWEED = key("deathweed");
        private static final ResourceKey<PlacedFeature> LIFE_MUSHROOM = key("life_mushroom");
        private static final ResourceKey<PlacedFeature> WATERLEAF = key("waterleaf");
        private static final ResourceKey<PlacedFeature> MOONGLOW = key("moonglow");
        private static final ResourceKey<PlacedFeature> UNDERGROUND_MOONGLOW = key("underground_moonglow");
        private static final ResourceKey<PlacedFeature> FIREBLOSSOM = key("fireblossom");
        private static final ResourceKey<PlacedFeature> SHIVERTHORN = key("shiverthorn");
        private static final ResourceKey<PlacedFeature> CORRUPT_DROOPING_VINE = key("corrupt_drooping_vine");
        private static final ResourceKey<PlacedFeature> CORRUPT_CATTAILS = key("corrupt_cattails");
        private static final ResourceKey<PlacedFeature> EBONY_TREE = key("ebony_tree");
        private static final ResourceKey<PlacedFeature> CORRUPT_GRASS = key("corrupt_grass");
        private static final ResourceKey<PlacedFeature> VILE_MUSHROOM = key("vile_mushroom");
        private static final ResourceKey<PlacedFeature> SHADOW_TREE = key("shadow_tree");
        private static final ResourceKey<PlacedFeature> CRIMSON_GRASS = key("crimson_grass");
        private static final ResourceKey<PlacedFeature> CRIMSON_DROOPING_VINE = key("crimson_drooping_vine");
        private static final ResourceKey<PlacedFeature> CRIMSON_CATTAILS = key("crimson_cattails");
        private static final ResourceKey<PlacedFeature> VICIOUS_MUSHROOM = key("vicious_mushroom");
        private static final ResourceKey<PlacedFeature> GLOWING_MUSHROOM = key("glowing_mushroom");
        private static final ResourceKey<PlacedFeature> GLOWING_MUSHROOM_LIFE_CRYSTAL = key("glowing_mushroom_life_crystal");
        private static final ResourceKey<PlacedFeature> GLOWING_MUSHROOM_TREE = key("glowing_mushroom_tree");
        private static final ResourceKey<PlacedFeature> HUGE_LIFE_MUSHROOM_TREE = key("huge_life_mushroom_tree");
        private static final ResourceKey<PlacedFeature> GLOWING_MUSHROOM_VINE = key("glowing_mushroom_vine");
        private static final ResourceKey<PlacedFeature> SILENT_DROOPING_VINE = key("silent_drooping_vine");
        private static final ResourceKey<PlacedFeature> GLOWING_MUSHROOM_CATTAILS = key("glowing_mushroom_cattails");
        private static final ResourceKey<PlacedFeature> ASH_TREE = key("ash_tree");
        private static final ResourceKey<PlacedFeature> ASH_GRASS = key("ash_grass");
        private static final ResourceKey<PlacedFeature> JUNGLE_ROSE = key("jungle_rose");
        private static final ResourceKey<PlacedFeature> JUNGLE_SPORE = key("jungle_spore");
        private static final ResourceKey<PlacedFeature> JUNGLE_DROOPING_VINE = key("jungle_drooping_vine");
        private static final ResourceKey<PlacedFeature> JUNGLE_CATTAILS = key("jungle_cattails");
        private static final ResourceKey<PlacedFeature> UNDERGROUND_JUNGLE_GRASS = key("underground_jungle_grass");
        private static final ResourceKey<PlacedFeature> UNDERGROUND_JUNGLE_BUSH = key("underground_jungle_bush");
        private static final ResourceKey<PlacedFeature> UNDERGROUND_JUNGLE_TREE = key("underground_jungle_tree");
        private static final ResourceKey<PlacedFeature> NATURES_GIFT = key("natures_gift");
        private static final ResourceKey<PlacedFeature> NO_TRAPS_GRAVITATION_TRAP = key("no_traps_gravitation_trap");
        private static final ResourceKey<PlacedFeature> NO_TRAPS_PNEUMATIC_TRAP = key("no_traps_pneumatic_trap");
        private static final ResourceKey<PlacedFeature> NO_TRAPS_SCULK_TRAP = key("no_traps_sculk_trap");
        private static final ResourceKey<PlacedFeature> NO_TRAPS_SHIMMER_TRAP = key("no_traps_shimmer_trap");
        private static final ResourceKey<PlacedFeature> NO_TRAPS_SCULK_SENSOR_WITH_TNT = key("no_traps_sculk_sensor_with_tnt");
        private static final ResourceKey<PlacedFeature> DART_TRAP = key("dart_trap");
        private static final ResourceKey<PlacedFeature> BOULDER_TRAP = key("boulder_trap");
        private static final ResourceKey<PlacedFeature> DEATH_CHEST_TRAP = key("death_chest_trap");
        private static final ResourceKey<PlacedFeature> LIFE_CRYSTAL = key("life_crystal");
        private static final ResourceKey<PlacedFeature> WATER_CHESTS = key("water_chests");
        private static final ResourceKey<PlacedFeature> ROLLING_CACTUS = key("rolling_cactus");
        private static final ResourceKey<PlacedFeature> SILT_BLOCK = key("silt_block");
        private static final ResourceKey<PlacedFeature> SLUSH = key("slush");
        private static final ResourceKey<PlacedFeature> CAVE_SANDSTONE_CHESTS = key("cave_sandstone_chests");
        private static final ResourceKey<PlacedFeature> UNDERGROUND_SANDSTONE_CHESTS = key("underground_sandstone_chests");
        private static final ResourceKey<PlacedFeature> SURFACE_CHESTS = key("surface_chests");
        private static final ResourceKey<PlacedFeature> CAVE_FROZEN_CHESTS = key("cave_frozen_chests");
        private static final ResourceKey<PlacedFeature> UNDERGROUND_FROZEN_CHESTS = key("underground_frozen_chests");
        private static final ResourceKey<PlacedFeature> PALM_TREE = key("palm_tree");
        private static final ResourceKey<PlacedFeature> BAOBAB_TREE = key("baobab_tree");
        private static final ResourceKey<PlacedFeature> GEMSTONE_CAVE = key("gemstone_cave");
        private static final ResourceKey<PlacedFeature> PEARL_TREE = key("pearl_tree");
        private static final ResourceKey<PlacedFeature> ADDITIONAL_ANCIENT_DEBRIS = key("additional_ancient_debris");
        private static final ResourceKey<PlacedFeature> PLATINUM_VEIN_WITH_DETONATOR = key("platinum_vein_with_detonator");
        private static final ResourceKey<PlacedFeature> GOLD_VEIN_WITH_DETONATOR = key("gold_vein_with_detonator");
        private static final ResourceKey<PlacedFeature> VOID_GRASS = key("void_grass");
        private static final ResourceKey<PlacedFeature> TALL_VOID_GRASS = key("tall_void_grass");
        private static final ResourceKey<PlacedFeature> SILVER_GRASS = key("silver_grass");
        private static final ResourceKey<PlacedFeature> SEA_SILVER_GRASS = key("sea_silver_grass");
        private static final ResourceKey<PlacedFeature> TALL_SILVER_GRASS = key("tall_silver_grass");
        private static final ResourceKey<PlacedFeature> VOID_VIOLET = key("void_violet");
        private static final ResourceKey<PlacedFeature> VOID_TREE = key("void_tree");
        private static final ResourceKey<PlacedFeature> VOID_TREE_LESS = key("void_tree_less");
        private static final ResourceKey<PlacedFeature> END_BROKEN_STONE = key("end_broken_stone");
        private static final ResourceKey<PlacedFeature> END_BROKEN_STONE_LESS = key("end_broken_stone_less");
        private static final ResourceKey<PlacedFeature> VOID_HUGE_STONE = key("void_huge_stone");
        private static final ResourceKey<PlacedFeature> END_HUGE_STONE = key("end_huge_stone");
        private static final ResourceKey<PlacedFeature> LUNAR_CORAL_HUGE_STONE = key("lunar_coral_huge_stone");
        private static final ResourceKey<PlacedFeature> DRAGONSAL_ORE = key("dragonsal_ore");
        private static final ResourceKey<PlacedFeature> LUNAR_CORAL = key("lunar_coral");
        private static final ResourceKey<PlacedFeature> MOONGLOW_WILLOW_TREE = key("moonglow_willow_tree");
        private static final ResourceKey<PlacedFeature> MOONGLOW_WILLOW_TREE_LESS = key("moonglow_willow_tree_less");
        private static final ResourceKey<PlacedFeature> WITHERED_SEA_SILK = key("withered_sea_silk");

        private static ResourceKey<PlacedFeature> key(String path) {
            return Confluence.asResourceKey(Registries.PLACED_FEATURE, path);
        }

        private static final SurfaceWaterDepthFilter surfaceWaterDepth0 = SurfaceWaterDepthFilter.forMaxDepth(0);
        private static final SecretFlagPlacement noTraps = SecretFlagPlacement.of(IWorldOptions.NT_MASK);
        private static final HeightmapPlacement oceanFloor = HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR);
        private static final HeightmapPlacement worldSurfaceWG = HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG);
        private static final HeightmapPlacement worldSurface = HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE);
        private static final BlockPredicate air = BlockPredicate.matchesBlocks(Blocks.AIR);
        private static final EnvironmentScanPlacement targetSolidAllowedAir = EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), air, 12);
        private static final EnvironmentScanPlacement targetSturdyAllowedAir = EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.hasSturdyFace(new Vec3i(0, -1, 0), Direction.UP), air, 12);
        private static final CountPlacement count2 = CountPlacement.of(2);
        private static final CountPlacement count3 = CountPlacement.of(3);
        private static final CountPlacement count4 = CountPlacement.of(4);
        private static final CountPlacement count5 = CountPlacement.of(5);
        private static final CountPlacement count14 = CountPlacement.of(14);
        private static final InSquarePlacement inSquare = InSquarePlacement.spread();
        private static final BiomeFilter biome = BiomeFilter.biome();
        private static final HeightRangePlacement bottomThroughTop = HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.TOP); // 所有高度
        private static final HeightRangePlacement throughSurface = HeightRangePlacement.uniform(VerticalAnchor.absolute(OverworldUtils.getSurfaceY()), VerticalAnchor.absolute(260)); // 地表层
        private static final HeightRangePlacement bottomThroughSurface = HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(260)); // 地底到地表层
        private static final HeightRangePlacement throughUnderground = HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(OverworldUtils.getSurfaceY())); // 地下层
        private static final HeightRangePlacement bottomThroughUnderground = HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(OverworldUtils.getSurfaceY())); // 地底到地下层
        private static final HeightRangePlacement throughCave = HeightRangePlacement.uniform(VerticalAnchor.BOTTOM, VerticalAnchor.absolute(0)); // 洞穴层
        private static final HeightRangePlacement the_end = HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(64)); // 洞穴层
        private static final RandomOffsetPlacement ySpread1 = RandomOffsetPlacement.vertical(ConstantInt.of(1));
        private static final RandomOffsetPlacement ySpreadN1 = RandomOffsetPlacement.vertical(ConstantInt.of(-1));
        private static final CountPlacement count1_9$2_1 = CountPlacement.of(new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(1), 9).add(ConstantInt.of(2), 1).build()));
        private static final CountPlacement count0_2$1_3 = CountPlacement.of(new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(0), 2).add(ConstantInt.of(1), 3).build()));
        private static final CountPlacement count1_5 = CountPlacement.of(new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(0), 4).add(ConstantInt.of(1), 1).build()));
        private static final CountPlacement count1_10 = CountPlacement.of(new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(0), 9).add(ConstantInt.of(1), 1).build()));
        private static final HeightRangePlacement heightRandom10_100 = HeightRangePlacement.uniform(
                VerticalAnchor.absolute(10),
                VerticalAnchor.absolute(100)
        );

        private static void bootstrap(BootstrapContext<PlacedFeature> context) {
            HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
            register(context, AMBER_ORE, configured.getOrThrow(ConfiguredFeatures.AMBER_ORE), inSquare, heightRangeTriangle(-52, 10), biome);
            register(context, AMETHYST_ORE, configured.getOrThrow(ConfiguredFeatures.AMETHYST_ORE), inSquare, heightRangeTriangle(-52, 10), biome);
            register(context, ASH_HELLSTONE, configured.getOrThrow(ConfiguredFeatures.ASH_HELLSTONE), count4, inSquare, heightRangeTriangle(0, 128), biome);
            register(context, NETHERRACK_HELLSTONE, configured.getOrThrow(ConfiguredFeatures.NETHERRACK_HELLSTONE), count4, inSquare, heightRangeTriangle(0, 128), biome);
            register(context, COLD_CRYSTAL_ORE, configured.getOrThrow(ConfiguredFeatures.COLD_CRYSTAL_ORE), inSquare, heightRangeTriangle(-52, 160), biome);
            register(context, CRIMTANE_ORE, configured.getOrThrow(ConfiguredFeatures.CRIMTANE_ORE), SecretFlagPlacement.of(IWorldOptions.THE_CRIMSON), count2, inSquare, heightRangeTriangle(-50, 30), biome);
            // 标记生成次数
            register(context, DEEPSLATE_ADAMANTITE_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_0), RarityFilter.onAverageOnceEvery(6), inSquare, heightRangeTriangle(-60, -40), biome);
            register(context, DEEPSLATE_ADAMANTITE_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_1), RarityFilter.onAverageOnceEvery(6), inSquare, heightRangeTriangle(-60, -40), biome);
            register(context, DEEPSLATE_ADAMANTITE_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_2), RarityFilter.onAverageOnceEvery(6), inSquare, heightRangeTriangle(-60, -40), biome);
            register(context, DEEPSLATE_COBALT_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_COBALT_ORE_STEP_0), RarityFilter.onAverageOnceEvery(2), inSquare, heightRangeTriangle(-60, -10), biome);
            register(context, DEEPSLATE_COBALT_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_COBALT_ORE_STEP_1), RarityFilter.onAverageOnceEvery(2), inSquare, heightRangeTriangle(-60, -10), biome);
            register(context, DEEPSLATE_COBALT_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_COBALT_ORE_STEP_2), RarityFilter.onAverageOnceEvery(2), inSquare, heightRangeTriangle(-60, -10), biome);
            register(context, DEEPSLATE_MYTHRIL_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_0), RarityFilter.onAverageOnceEvery(4), inSquare, heightRangeTriangle(-60, -25), biome);
            register(context, DEEPSLATE_MYTHRIL_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_1), RarityFilter.onAverageOnceEvery(4), inSquare, heightRangeTriangle(-60, -25), biome);
            register(context, DEEPSLATE_MYTHRIL_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_2), RarityFilter.onAverageOnceEvery(4), inSquare, heightRangeTriangle(-60, -25), biome);
            register(context, DEEPSLATE_ORICHALCUM_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_0), RarityFilter.onAverageOnceEvery(4), inSquare, heightRangeTriangle(-60, -25), biome);
            register(context, DEEPSLATE_ORICHALCUM_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_1), RarityFilter.onAverageOnceEvery(4), inSquare, heightRangeTriangle(-60, -25), biome);
            register(context, DEEPSLATE_ORICHALCUM_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_2), RarityFilter.onAverageOnceEvery(4), inSquare, heightRangeTriangle(-60, -25), biome);
            register(context, DEEPSLATE_PALLADIUM_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_0), RarityFilter.onAverageOnceEvery(2), inSquare, heightRangeTriangle(-60, -10), biome);
            register(context, DEEPSLATE_PALLADIUM_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_1), RarityFilter.onAverageOnceEvery(2), inSquare, heightRangeTriangle(-60, -10), biome);
            register(context, DEEPSLATE_PALLADIUM_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_2), RarityFilter.onAverageOnceEvery(2), inSquare, heightRangeTriangle(-60, -10), biome);
            register(context, DEEPSLATE_TITANIUM_ORE_STEP_0, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_TITANIUM_ORE_STEP_0), RarityFilter.onAverageOnceEvery(6), inSquare, heightRangeTriangle(-60, -40), biome);
            register(context, DEEPSLATE_TITANIUM_ORE_STEP_1, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_TITANIUM_ORE_STEP_1), RarityFilter.onAverageOnceEvery(6), inSquare, heightRangeTriangle(-60, -40), biome);
            register(context, DEEPSLATE_TITANIUM_ORE_STEP_2, configured.getOrThrow(ConfiguredFeatures.DEEPSLATE_TITANIUM_ORE_STEP_2), RarityFilter.onAverageOnceEvery(6), inSquare, heightRangeTriangle(-60, -40), biome);
            register(context, DEMONITE_ORE, configured.getOrThrow(ConfiguredFeatures.DEMONITE_ORE), SecretFlagPlacement.of(IWorldOptions.THE_CORRUPTION), count2, inSquare, heightRangeTriangle(-50, 30), biome);
            register(context, GELSTONE_ORE, configured.getOrThrow(ConfiguredFeatures.GELSTONE_ORE), inSquare, heightRangeTriangle(-52, 160), biome);
            register(context, CHLOROPHYTE_ORE, configured.getOrThrow(ConfiguredFeatures.CHLOROPHYTE_ORE), CountPlacement.of(12), inSquare, heightRangeTriangle(-52, 160), biome);
            register(context, JADE_ORE, configured.getOrThrow(ConfiguredFeatures.JADE_ORE), inSquare, heightRangeTriangle(-52, 10), biome);
            register(context, LEAD_ORE, configured.getOrThrow(ConfiguredFeatures.LEAD_ORE), CountPlacement.of(8), inSquare, heightRangeTriangle(-24, 56), biome);
            register(context, PLATINUM_ORE, configured.getOrThrow(ConfiguredFeatures.PLATINUM_ORE), SecretFlagPlacement.of(IWorldOptions.TC_MASK, true), count2, inSquare, heightRangeTriangle(-48, 10), biome);
            register(context, RUBY_ORE, configured.getOrThrow(ConfiguredFeatures.RUBY_ORE), inSquare, heightRangeTriangle(-52, 10), biome);
            register(context, SAPPHIRE_ORE, configured.getOrThrow(ConfiguredFeatures.SAPPHIRE_ORE), inSquare, heightRangeTriangle(-52, 10), biome);
            register(context, SILVER_ORE, configured.getOrThrow(ConfiguredFeatures.SILVER_ORE), CountPlacement.of(6), inSquare, heightRangeTriangle(-34, 28), biome);
            register(context, TIN_ORE, configured.getOrThrow(ConfiguredFeatures.TIN_ORE), CountPlacement.of(16), inSquare, heightRangeTriangle(0, 128), biome);
            register(context, TOPAZ_ORE, configured.getOrThrow(ConfiguredFeatures.TOPAZ_ORE), inSquare, heightRangeTriangle(-52, 10), biome);
            register(context, TUNGSTEN_ORE, configured.getOrThrow(ConfiguredFeatures.TUNGSTEN_ORE), SecretFlagPlacement.of(IWorldOptions.TC_MASK, true), count4, inSquare, heightRangeTriangle(-38, 20), biome);
            register(context, MARINE_GRAVEL, configured.getOrThrow(ConfiguredFeatures.MARINE_GRAVEL), CountPlacement.of(10), inSquare, bottomThroughTop, biome);
            register(context, OPAL_ORE, configured.getOrThrow(ConfiguredFeatures.OPAL_ORE), CountPlacement.of(7), inSquare, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(50)), biome);
            register(context, THIN_ICE_PATCH, configured.getOrThrow(ConfiguredFeatures.THIN_ICE_PATCH), RarityFilter.onAverageOnceEvery(2), inSquare, throughUnderground, biome);
            register(context, POWDER_SNOW_PATCH, configured.getOrThrow(ConfiguredFeatures.POWDER_SNOW_PATCH), RarityFilter.onAverageOnceEvery(2), inSquare, throughUnderground, biome);
            register(context, DESERT_FOSSIL, configured.getOrThrow(ConfiguredFeatures.DESERT_FOSSIL), count14, inSquare, bottomThroughTop, biome);
            register(context, FALLING_SAND_TRAP, configured.getOrThrow(ConfiguredFeatures.FALLING_SAND_TRAP), inSquare, bottomThroughUnderground, biome);
            register(context, UNDERGROUND_CHESTS, configured.getOrThrow(ConfiguredFeatures.UNDERGROUND_CHESTS), inSquare, bottomThroughSurface, targetSturdyAllowedAir, SurfaceRelativeThresholdFilter.of(Heightmap.Types.WORLD_SURFACE_WG, -110, -80), biome);
            register(context, FOREST_DROOPING_VINE, configured.getOrThrow(ConfiguredFeatures.FOREST_DROOPING_VINE), CountPlacement.of(60), inSquare, throughSurface, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.matchesBlocks(Blocks.DIRT, Blocks.STONE), air, 12), ySpreadN1, biome);
            register(context, FOREST_CATTAILS, configured.getOrThrow(ConfiguredFeatures.FOREST_CATTAILS), inSquare, RarityFilter.onAverageOnceEvery(4), worldSurfaceWG, BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.WATER)), biome);
            register(context, LIFE_MUSHROOM, configured.getOrThrow(ConfiguredFeatures.LIFE_MUSHROOM), RarityFilter.onAverageOnceEvery(32), count3, inSquare, worldSurfaceWG, biome);
            register(context, BLINKROOT, configured.getOrThrow(ConfiguredFeatures.BLINKROOT), count3, inSquare, bottomThroughUnderground, biome);
            register(context, DAYBLOOM, configured.getOrThrow(ConfiguredFeatures.DAYBLOOM), RarityFilter.onAverageOnceEvery(32), count2, inSquare, worldSurfaceWG, biome);
            register(context, DEATHWEED, configured.getOrThrow(ConfiguredFeatures.DEATHWEED), RarityFilter.onAverageOnceEvery(32), count3, inSquare, worldSurfaceWG, biome);
            register(context, WATERLEAF, configured.getOrThrow(ConfiguredFeatures.WATERLEAF), RarityFilter.onAverageOnceEvery(16), count2, inSquare, worldSurfaceWG, biome);
            register(context, MOONGLOW, configured.getOrThrow(ConfiguredFeatures.MOONGLOW), RarityFilter.onAverageOnceEvery(32), count5, inSquare, worldSurfaceWG, biome);
            register(context, UNDERGROUND_MOONGLOW, configured.getOrThrow(ConfiguredFeatures.MOONGLOW), count4, inSquare, bottomThroughUnderground, biome);
            register(context, FIREBLOSSOM, configured.getOrThrow(ConfiguredFeatures.FIREBLOSSOM), count3, inSquare, bottomThroughTop, biome);
            register(context, SHIVERTHORN, configured.getOrThrow(ConfiguredFeatures.SHIVERTHORN), RarityFilter.onAverageOnceEvery(32), count5, inSquare, worldSurfaceWG, biome);
            register(context, CORRUPT_DROOPING_VINE, configured.getOrThrow(ConfiguredFeatures.CORRUPT_DROOPING_VINE), CountPlacement.of(60), inSquare, throughSurface, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.matchesBlocks(Blocks.DIRT, NatureBlocks.EBONSTONE.get()), air, 12), ySpreadN1, biome);
            register(context, CORRUPT_CATTAILS, configured.getOrThrow(ConfiguredFeatures.CORRUPT_CATTAILS), inSquare, RarityFilter.onAverageOnceEvery(4), worldSurfaceWG, BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.WATER)), biome);
            register(context, EBONY_TREE, configured.getOrThrow(ModFeatures.Configured.EBONY_TREE), count1_9$2_1, inSquare, surfaceWaterDepth0, oceanFloor, biome);
            register(context, CORRUPT_GRASS, configured.getOrThrow(ConfiguredFeatures.CORRUPT_GRASS), CountPlacement.of(10), inSquare, HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), biome);
            register(context, VOID_GRASS, configured.getOrThrow(ConfiguredFeatures.VOID_GRASS), CountPlacement.of(30), inSquare, the_end, biome);
            register(context, TALL_VOID_GRASS, configured.getOrThrow(ConfiguredFeatures.TALL_VOID_GRASS), CountPlacement.of(10), inSquare, the_end, biome);
            register(context, SILVER_GRASS, configured.getOrThrow(ConfiguredFeatures.SILVER_GRASS), CountPlacement.of(30), inSquare, the_end, biome);
            register(context, SEA_SILVER_GRASS, configured.getOrThrow(ConfiguredFeatures.SILVER_GRASS), CountPlacement.of(10), inSquare, the_end, biome);
            register(context, TALL_SILVER_GRASS, configured.getOrThrow(ConfiguredFeatures.TALL_SILVER_GRASS), CountPlacement.of(10), inSquare, the_end, biome);
            register(context, VOID_VIOLET, configured.getOrThrow(ConfiguredFeatures.VOID_VIOLET), CountPlacement.of(5), inSquare, the_end, biome);
            register(context, VOID_TREE, configured.getOrThrow(ConfiguredFeatures.VOID_TREE), count1_9$2_1, inSquare, surfaceWaterDepth0, oceanFloor, biome);
            register(context, MOONGLOW_WILLOW_TREE, configured.getOrThrow(ConfiguredFeatures.MOONGLOW_WILLOW_TREE), count1_9$2_1, inSquare, surfaceWaterDepth0, oceanFloor, biome);
            register(context, VOID_TREE_LESS, configured.getOrThrow(ConfiguredFeatures.VOID_TREE), count1_10, inSquare, surfaceWaterDepth0, oceanFloor, biome);
            register(context, MOONGLOW_WILLOW_TREE_LESS, configured.getOrThrow(ConfiguredFeatures.MOONGLOW_WILLOW_TREE), count1_10, inSquare, worldSurfaceWG, biome);
            register(context, WITHERED_SEA_SILK, configured.getOrThrow(ConfiguredFeatures.WITHERED_SEA_SILK), inSquare, worldSurfaceWG, biome);
            register(context, END_BROKEN_STONE, configured.getOrThrow(ConfiguredFeatures.END_BROKEN_STONE), count0_2$1_3, inSquare, oceanFloor, biome);
            register(context, END_BROKEN_STONE_LESS, configured.getOrThrow(ConfiguredFeatures.END_BROKEN_STONE), count1_10, inSquare, oceanFloor, biome);
            register(context, VOID_HUGE_STONE, configured.getOrThrow(ConfiguredFeatures.VOID_HUGE_STONE), count1_5, inSquare, worldSurfaceWG, biome);
            register(context, END_HUGE_STONE, configured.getOrThrow(ConfiguredFeatures.END_HUGE_STONE), count1_5, inSquare, worldSurfaceWG, biome);
            register(context, LUNAR_CORAL_HUGE_STONE, configured.getOrThrow(ConfiguredFeatures.LUNAR_CORAL_HUGE_STONE), count1_5, inSquare, worldSurfaceWG, biome);
            register(context, LUNAR_CORAL, configured.getOrThrow(ConfiguredFeatures.LUNAR_CORAL), count1_5, inSquare, worldSurfaceWG, biome);
            register(context, DRAGONSAL_ORE, configured.getOrThrow(ConfiguredFeatures.DRAGONSAL_ORE), count3, inSquare, heightRandom10_100, biome);
            register(context, VILE_MUSHROOM, configured.getOrThrow(ConfiguredFeatures.VILE_MUSHROOM), RarityFilter.onAverageOnceEvery(32), count3, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), biome);
            register(context, SHADOW_TREE, configured.getOrThrow(ModFeatures.Configured.SHADOW_TREE), count1_9$2_1, inSquare, surfaceWaterDepth0, oceanFloor, biome);
            register(context, CRIMSON_GRASS, configured.getOrThrow(ConfiguredFeatures.CRIMSON_GRASS), CountPlacement.of(10), inSquare, HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING), biome);
            register(context, CRIMSON_DROOPING_VINE, configured.getOrThrow(ConfiguredFeatures.CRIMSON_DROOPING_VINE), CountPlacement.of(60), inSquare, throughSurface, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.matchesBlocks(Blocks.DIRT, NatureBlocks.CRIMSTONE.get()), air, 12), ySpreadN1, biome);
            register(context, CRIMSON_CATTAILS, configured.getOrThrow(ConfiguredFeatures.CRIMSON_CATTAILS), inSquare, RarityFilter.onAverageOnceEvery(4), worldSurfaceWG, BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.WATER)), biome);
            register(context, VICIOUS_MUSHROOM, configured.getOrThrow(ConfiguredFeatures.VICIOUS_MUSHROOM), RarityFilter.onAverageOnceEvery(32), count3, HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), biome);
            register(context, GLOWING_MUSHROOM, configured.getOrThrow(ConfiguredFeatures.GLOWING_MUSHROOM), CountPlacement.of(40), inSquare, bottomThroughUnderground, biome);
            register(context, GLOWING_MUSHROOM_LIFE_CRYSTAL, configured.getOrThrow(ConfiguredFeatures.LIFE_CRYSTAL), CountPlacement.of(6), inSquare, bottomThroughUnderground, targetSturdyAllowedAir, SurfaceRelativeThresholdFilter.of(Heightmap.Types.WORLD_SURFACE_WG, -110, -70), biome);
            register(context, GLOWING_MUSHROOM_TREE, configured.getOrThrow(ModFeatures.Configured.GLOWING_MUSHROOM_TREE), CountOnEveryLayerPlacement.of(3), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.MUD, NatureBlocks.MUSHROOM_GRASS_BLOCK.get()), air, 12), biome);
            register(context, HUGE_LIFE_MUSHROOM_TREE, configured.getOrThrow(ModFeatures.Configured.HUGE_LIFE_MUSHROOM_TREE), RarityFilter.onAverageOnceEvery(30), biome);
            register(context, GLOWING_MUSHROOM_VINE, configured.getOrThrow(ConfiguredFeatures.GLOWING_MUSHROOM_VINE), CountPlacement.of(144), inSquare, bottomThroughUnderground, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.matchesBlocks(Blocks.MUD, NatureBlocks.MUSHROOM_GRASS_BLOCK.get()), air, 12), ySpreadN1, biome);
            register(context, SILENT_DROOPING_VINE, configured.getOrThrow(ConfiguredFeatures.SILENT_DROOPING_VINE), CountPlacement.of(72), inSquare, bottomThroughUnderground, heightRandom10_100, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.matchesTag(ModTags.Blocks.END_PLANT_CAN_SURVIVE), air, 20), ySpreadN1, biome);
            register(context, GLOWING_MUSHROOM_CATTAILS, configured.getOrThrow(ConfiguredFeatures.GLOWING_MUSHROOM_CATTAILS), CountOnEveryLayerPlacement.of(32), bottomThroughUnderground, ySpreadN1, biome);
            register(context, ASH_TREE, configured.getOrThrow(ModFeatures.Configured.ASH_TREE), CountOnEveryLayerPlacement.of(4), targetSturdyAllowedAir, biome);
            register(context, ASH_GRASS, configured.getOrThrow(ConfiguredFeatures.ASH_GRASS), CountPlacement.of(20), inSquare, bottomThroughTop, biome);
            register(context, JUNGLE_ROSE, configured.getOrThrow(ConfiguredFeatures.JUNGLE_ROSE), count4, inSquare, bottomThroughSurface, biome);
            register(context, JUNGLE_SPORE, configured.getOrThrow(ConfiguredFeatures.JUNGLE_SPORE), count14, inSquare, bottomThroughSurface, targetSturdyAllowedAir, SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR, -110, -70), biome);
            register(context, JUNGLE_DROOPING_VINE, configured.getOrThrow(ConfiguredFeatures.JUNGLE_DROOPING_VINE), CountPlacement.of(90), inSquare, bottomThroughSurface, EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.matchesBlocks(Blocks.MUD, NatureBlocks.JUNGLE_GRASS_BLOCK.get()), air, 12), ySpreadN1, biome);
            register(context, JUNGLE_CATTAILS, configured.getOrThrow(ConfiguredFeatures.JUNGLE_CATTAILS), inSquare, RarityFilter.onAverageOnceEvery(4), worldSurfaceWG, BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(new Vec3i(0, -1, 0), Blocks.WATER)), biome);
            register(context, UNDERGROUND_JUNGLE_GRASS, configured.getOrThrow(ConfiguredFeatures.UNDERGROUND_JUNGLE_GRASS), CountPlacement.of(125), inSquare, bottomThroughUnderground, targetSolidAllowedAir, ySpread1, biome);
            register(context, UNDERGROUND_JUNGLE_BUSH, configured.getOrThrow(ConfiguredFeatures.UNDERGROUND_JUNGLE_BUSH), count4, inSquare, bottomThroughUnderground, targetSolidAllowedAir, ySpread1, biome);
            register(context, UNDERGROUND_JUNGLE_TREE, configured.getOrThrow(ConfiguredFeatures.UNDERGROUND_JUNGLE_TREE), count2, inSquare, bottomThroughUnderground, targetSolidAllowedAir, ySpread1, biome);
            register(context, NATURES_GIFT, configured.getOrThrow(ConfiguredFeatures.NATURES_GIFT), count3, inSquare, bottomThroughSurface, biome);
            register(context, NO_TRAPS_GRAVITATION_TRAP, configured.getOrThrow(ConfiguredFeatures.GRAVITATION_TRAP), noTraps, inSquare, bottomThroughUnderground, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.matchesBlocks(new Vec3i(0, 1, 0), Blocks.AIR), 12), biome);
            register(context, NO_TRAPS_PNEUMATIC_TRAP, configured.getOrThrow(ConfiguredFeatures.PNEUMATIC_TRAP), noTraps, inSquare, bottomThroughUnderground, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.matchesBlocks(new Vec3i(0, 1, 0), Blocks.AIR), 12), biome);
            register(context, NO_TRAPS_SCULK_TRAP, configured.getOrThrow(ConfiguredFeatures.SCULK_TRAP),
                    noTraps, RarityFilter.onAverageOnceEvery(10), inSquare, bottomThroughUnderground,
                    EnvironmentScanPlacement.scanningFor(
                            Direction.DOWN,
                            BlockPredicate.solid(),
                            BlockPredicate.matchesBlocks(new Vec3i(0, 1, 0), Blocks.AIR),
                            12
                    ), biome
            );
            register(context, NO_TRAPS_SHIMMER_TRAP, configured.getOrThrow(ConfiguredFeatures.SHIMMER_TRAP), noTraps, inSquare, bottomThroughUnderground, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.matchesBlocks(new Vec3i(0, 1, 0), Blocks.AIR), 12), biome);
            register(context, NO_TRAPS_SCULK_SENSOR_WITH_TNT, configured.getOrThrow(ConfiguredFeatures.SCULK_SENSOR_WITH_TNT), noTraps, RarityFilter.onAverageOnceEvery(127), inSquare, throughSurface, biome);
            register(context, DART_TRAP, configured.getOrThrow(ConfiguredFeatures.DART_TRAP), inSquare, bottomThroughUnderground, biome);
            register(context, BOULDER_TRAP, configured.getOrThrow(ConfiguredFeatures.BOULDER_TRAP), inSquare, bottomThroughUnderground, biome);
            register(context, DEATH_CHEST_TRAP, configured.getOrThrow(ConfiguredFeatures.DEATH_CHEST_TRAP), RarityFilter.onAverageOnceEvery(20), inSquare, throughCave, biome);
            register(context, LIFE_CRYSTAL, configured.getOrThrow(ConfiguredFeatures.LIFE_CRYSTAL), count3, inSquare, bottomThroughUnderground, targetSturdyAllowedAir, SurfaceRelativeThresholdFilter.of(Heightmap.Types.WORLD_SURFACE_WG, -110, -70), biome);
            register(context, WATER_CHESTS, configured.getOrThrow(ConfiguredFeatures.WATER_CHESTS),
                    RarityFilter.onAverageOnceEvery(8), inSquare, bottomThroughSurface,
                    EnvironmentScanPlacement.scanningFor(
                            Direction.DOWN,
                            BlockPredicate.hasSturdyFace(new Vec3i(0, -1, 0), Direction.UP),
                            BlockPredicate.matchesBlocks(Blocks.WATER),
                            12
                    ),
                    SurfaceRelativeThresholdFilter.of(Heightmap.Types.WORLD_SURFACE_WG, -80, 320), biome
            );
            register(context, ROLLING_CACTUS, configured.getOrThrow(ConfiguredFeatures.ROLLING_CACTUS), CountPlacement.of(90), inSquare, bottomThroughSurface, targetSturdyAllowedAir, SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR, -110, -30), biome);
            register(context, SILT_BLOCK, configured.getOrThrow(ConfiguredFeatures.SILT_BLOCK), count14, inSquare, bottomThroughTop, biome);
            register(context, SLUSH, configured.getOrThrow(ConfiguredFeatures.SLUSH), CountPlacement.of(10), inSquare, bottomThroughTop, biome);
            register(context, PALM_TREE, configured.getOrThrow(ModFeatures.Configured.PALM_TREE),
                    RarityFilter.onAverageOnceEvery(3), count1_9$2_1, inSquare, surfaceWaterDepth0, oceanFloor,
                    BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(NatureBlocks.PALM_LOG_BLOCKS.SAPLING.get().defaultBlockState(), Vec3i.ZERO)), biome);
            register(context, BAOBAB_TREE, configured.getOrThrow(ModFeatures.Configured.BAOBAB_TREE), biome, count1_9$2_1, inSquare, surfaceWaterDepth0, oceanFloor);
            register(context, GEMSTONE_CAVE, configured.getOrThrow(ConfiguredFeatures.GEMSTONE_CAVE), inSquare, RarityFilter.onAverageOnceEvery(55), bottomThroughUnderground, biome);
            register(context, PEARL_TREE, configured.getOrThrow(ModFeatures.Configured.PEARL_TREE), count1_9$2_1, inSquare, surfaceWaterDepth0, oceanFloor, biome);
            register(context, ADDITIONAL_ANCIENT_DEBRIS, configured.getOrThrow(ConfiguredFeatures.ADDITIONAL_ANCIENT_DEBRIS), count3, inSquare, heightRangeTriangle(0, 128), biome);
            register(context, PLATINUM_VEIN_WITH_DETONATOR, configured.getOrThrow(ConfiguredFeatures.PLATINUM_VEIN_WITH_DETONATOR), RarityFilter.onAverageOnceEvery(30), inSquare, bottomThroughUnderground, biome);
            register(context, GOLD_VEIN_WITH_DETONATOR, configured.getOrThrow(ConfiguredFeatures.GOLD_VEIN_WITH_DETONATOR), RarityFilter.onAverageOnceEvery(30), inSquare, bottomThroughUnderground, biome);

            gemTree(context, AMBER_TREE, configured.getOrThrow(ModFeatures.Configured.AMBER_TREE), NatureBlocks.AMBER_SAPLING.get());
            gemTree(context, AMETHYST_TREE, configured.getOrThrow(ModFeatures.Configured.AMETHYST_TREE), NatureBlocks.AMETHYST_SAPLING.get());
            gemTree(context, DIAMOND_TREE, configured.getOrThrow(ModFeatures.Configured.DIAMOND_TREE), NatureBlocks.DIAMOND_SAPLING.get());
            gemTree(context, JADE_TREE, configured.getOrThrow(ModFeatures.Configured.JADE_TREE), NatureBlocks.JADE_SAPLING.get());
            gemTree(context, RUBY_TREE, configured.getOrThrow(ModFeatures.Configured.RUBY_TREE), NatureBlocks.RUBY_SAPLING.get());
            gemTree(context, SAPPHIRE_TREE, configured.getOrThrow(ModFeatures.Configured.SAPPHIRE_TREE), NatureBlocks.SAPPHIRE_SAPLING.get());
            gemTree(context, TOPAZ_TREE, configured.getOrThrow(ModFeatures.Configured.TOPAZ_TREE), NatureBlocks.TOPAZ_SAPLING.get());

            evilAltar(context, CRIMSON_ALTAR_BIOME, configured.getOrThrow(ConfiguredFeatures.CRIMSON_ALTAR), false, 5);
            evilAltar(context, CRIMSON_ALTAR_WORLD, configured.getOrThrow(ConfiguredFeatures.CRIMSON_ALTAR), false, 1);
            evilAltar(context, DEMON_ALTAR_BIOME, configured.getOrThrow(ConfiguredFeatures.DEMON_ALTAR), true, 5);
            evilAltar(context, DEMON_ALTAR_WORLD, configured.getOrThrow(ConfiguredFeatures.DEMON_ALTAR), true, 1);

            pot(context, FOREST_POT, configured.getOrThrow(ConfiguredFeatures.FOREST_POT));
            pot(context, JUNGLE_POT, configured.getOrThrow(ConfiguredFeatures.JUNGLE_POT));
            pot(context, CORRUPTION_POT, configured.getOrThrow(ConfiguredFeatures.CORRUPTION_POT));
            pot(context, CRIMSON_POT, configured.getOrThrow(ConfiguredFeatures.CRIMSON_POT));
            pot(context, UNDERGROUND_DESERT_POT, configured.getOrThrow(ConfiguredFeatures.UNDERGROUND_DESERT_POT));
            pot(context, TUNDRA_POT, configured.getOrThrow(ConfiguredFeatures.TUNDRA_POT));

            chest(context, CAVE_CHESTS, configured.getOrThrow(ConfiguredFeatures.CAVE_CHESTS), count3, -110, -80);
            chest(context, CAVE_CHESTS_SMALL, configured.getOrThrow(ConfiguredFeatures.CAVE_CHESTS), count5, -80, -23);
            chest(context, UNDERGROUND_CHESTS_SMALL, configured.getOrThrow(ConfiguredFeatures.UNDERGROUND_CHESTS), count5, -80, -23);
            chest(context, CAVE_SANDSTONE_CHESTS, configured.getOrThrow(ConfiguredFeatures.CAVE_SANDSTONE_CHESTS), count2, -110, -80);
            chest(context, UNDERGROUND_SANDSTONE_CHESTS, configured.getOrThrow(ConfiguredFeatures.UNDERGROUND_SANDSTONE_CHESTS), count2, -80, -23);
            chest(context, SURFACE_CHESTS, configured.getOrThrow(ConfiguredFeatures.SURFACE_CHESTS), CountPlacement.of(27), -23, -2);
            chest(context, CAVE_FROZEN_CHESTS, configured.getOrThrow(ConfiguredFeatures.CAVE_FROZEN_CHESTS), count2, -110, -80);
            chest(context, UNDERGROUND_FROZEN_CHESTS, configured.getOrThrow(ConfiguredFeatures.UNDERGROUND_FROZEN_CHESTS), count2, -80, -23);

        }

        private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature, PlacementModifier... modifiers) {
            context.register(key, new PlacedFeature(feature, Arrays.stream(modifiers).toList()));
        }

        private static void gemTree(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature, Block saplingBlock) {
            register(context, key, feature, RarityFilter.onAverageOnceEvery(10), biome, inSquare, bottomThroughUnderground, targetSolidAllowedAir, ySpread1, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(saplingBlock.defaultBlockState(), Vec3i.ZERO)));
        }

        private static void evilAltar(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature, boolean corruption, int count) {
            register(context, key, feature, SecretFlagPlacement.of(corruption ? IWorldOptions.THE_CORRUPTION : IWorldOptions.THE_CRIMSON), biome, CountPlacement.of(count), inSquare, bottomThroughUnderground, targetSturdyAllowedAir, SurfaceRelativeThresholdFilter.of(Heightmap.Types.WORLD_SURFACE_WG, -110, -20));
        }

        private static void chest(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature, CountPlacement count, int minInclusive, int maxInclusive) {
            register(context, key, feature, biome, count, inSquare, bottomThroughSurface, targetSturdyAllowedAir, SurfaceRelativeThresholdFilter.of(Heightmap.Types.WORLD_SURFACE_WG, minInclusive, maxInclusive));
        }

        private static void pot(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> feature) {
            register(context, key, feature, biome, CountPlacement.of(26), inSquare, bottomThroughSurface, targetSturdyAllowedAir, SurfaceRelativeThresholdFilter.of(Heightmap.Types.WORLD_SURFACE_WG, -110, -2));
        }

        private static PlacementModifier heightRangeTriangle(int min, int max) {
            return HeightRangePlacement.triangle(VerticalAnchor.absolute(min), VerticalAnchor.absolute(max));
        }
    }

    private static class ConfiguredWorldCarvers {
        private static final ResourceKey<ConfiguredWorldCarver<?>> DESERT_CAVE_CARVER = key("desert_cave_carver");
        private static final ResourceKey<ConfiguredWorldCarver<?>> DEMONIC_CAVE_CARVER = key("demonic_cave_carver");
        private static final ResourceKey<ConfiguredWorldCarver<?>> GLOWING_MUSHROOM_CAVE_CARVER = key("glowing_mushroom_cave_carver");
        private static final ResourceKey<ConfiguredWorldCarver<?>> DRY_SEA_CARVER = key("dry_sea_carver");
        private static final ResourceKey<ConfiguredWorldCarver<?>> JUNGLE_CAVE_CARVER = key("jungle_cave_carver");
        private static final ResourceKey<ConfiguredWorldCarver<?>> WAVY_CAVE_CARVER = key("wavy_cave_carver");

        private static ResourceKey<ConfiguredWorldCarver<?>> key(String path) {
            return Confluence.asResourceKey(Registries.CONFIGURED_CARVER, path);
        }

        private static void bootstrap(BootstrapContext<ConfiguredWorldCarver<?>> context) {
            HolderGetter<Block> block = context.lookup(Registries.BLOCK);
            VerticalAnchor aboveBottom8 = VerticalAnchor.aboveBottom(8);
            VerticalAnchor absolute80 = VerticalAnchor.absolute(80);
            HolderSet<Block> replaceable = block.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES);
            context.register(DESERT_CAVE_CARVER, new ConfiguredWorldCarver<>(ModCarvers.DESERT_CAVE_CARVER.get(), new CarverConfiguration(
                    1,
                    UniformHeight.of(aboveBottom8, absolute80),
                    ConstantFloat.of(8),
                    aboveBottom8,
                    CarverDebugSettings.DEFAULT,
                    replaceable
            )));
            context.register(DEMONIC_CAVE_CARVER, new ConfiguredWorldCarver<>(ModCarvers.DEMONIC_CAVE_CARVER.get(), new DemonicCaveCarver.Config(new CarverConfiguration(
                    0.2F,
                    ConstantHeight.ZERO, // 没有用上的参数
                    ConstantFloat.of(4),
                    aboveBottom8,
                    CarverDebugSettings.DEFAULT,
                    replaceable
            ), UniformFloat.of(24, 48))));
            context.register(GLOWING_MUSHROOM_CAVE_CARVER, new ConfiguredWorldCarver<>(ModCarvers.GLOWING_MUSHROOM_CAVE_CARVER.get(), new CarverConfiguration(
                    0.6F,
                    UniformHeight.of(VerticalAnchor.absolute(-40), VerticalAnchor.absolute(-10)),
                    ConstantFloat.of(6),
                    aboveBottom8,
                    CarverDebugSettings.DEFAULT,
                    replaceable
            )));
            context.register(DRY_SEA_CARVER, new ConfiguredWorldCarver<>(ModCarvers.DRY_SEA_CARVER.get(), new CarverConfiguration(
                    0.3F,
                    ConstantHeight.ZERO, // 没有用上的参数
                    ConstantFloat.of(6),
                    aboveBottom8,
                    CarverDebugSettings.DEFAULT,
                    replaceable
            )));
            context.register(JUNGLE_CAVE_CARVER, new ConfiguredWorldCarver<>(ModCarvers.JUNGLE_CAVE_CARVER.get(), new CaveCarverConfiguration(
                    0.5F,
                    UniformHeight.of(aboveBottom8, VerticalAnchor.absolute(100)),
                    UniformFloat.of(0.1F, 0.9F),
                    aboveBottom8,
                    CarverDebugSettings.DEFAULT,
                    replaceable,
                    UniformFloat.of(2.8F, 5.6F),
                    UniformFloat.of(1.6F, 2.6F),
                    UniformFloat.of(-1, -0.4F)
            )));
            context.register(WAVY_CAVE_CARVER, new ConfiguredWorldCarver<>(ModCarvers.WAVY_CAVE_CARVER.get(), new CarverConfiguration(
                    0.2F,
                    UniformHeight.of(VerticalAnchor.absolute(-40), absolute80),
                    ConstantFloat.of(4),
                    aboveBottom8,
                    CarverDebugSettings.DEFAULT,
                    replaceable
            )));
        }
    }

    private static class BiomeModifierz {
        private static void bootstrap(BootstrapContext<BiomeModifier> context) {
            HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
            HolderSet<Biome> desert = biome.getOrThrow(Tags.Biomes.IS_DESERT);
            HolderSet<Biome> snowyIcy = new OrHolderSet<>(biome.getOrThrow(Tags.Biomes.IS_SNOWY), biome.getOrThrow(Tags.Biomes.IS_ICY));
            HolderSet<Biome> desertBadlands = new OrHolderSet<>(desert, biome.getOrThrow(Tags.Biomes.IS_BADLANDS));
            HolderSet<Biome> forestLike = biome.getOrThrow(ModTags.Biomes.IS_FOREST);
            HolderSet<Biome> overworld = biome.getOrThrow(Tags.Biomes.IS_OVERWORLD);
            HolderSet<Biome> jungle = biome.getOrThrow(Tags.Biomes.IS_JUNGLE);
            HolderSet<Biome> jungleLike = new OrHolderSet<>(jungle, biome.getOrThrow(Tags.Biomes.IS_LUSH));
            HolderSet<Biome> jungleAndLush = new OrHolderSet<>(biome.getOrThrow(Tags.Biomes.IS_JUNGLE), biome.getOrThrow(Tags.Biomes.IS_LUSH));


            HolderGetter<PlacedFeature> placedFeature = context.lookup(Registries.PLACED_FEATURE);
            Function<ResourceKey<PlacedFeature>, Holder<PlacedFeature>> factory = placedFeature::getOrThrow;

            addFeatures(context, "desert_uo", desert, HolderSet.direct(factory,
                    PlacedFeatures.AMBER_ORE
            ), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "desert_ud", desert, HolderSet.direct(factory,
                    PlacedFeatures.FALLING_SAND_TRAP
            ), GenerationStep.Decoration.UNDERGROUND_DECORATION);
            addFeatures(context, "desert_vd", desert, HolderSet.direct(factory,
                    PlacedFeatures.WATERLEAF
            ), GenerationStep.Decoration.VEGETAL_DECORATION);

            addFeatures(context, "desert_badlands_uo", desertBadlands, HolderSet.direct(factory,
                    PlacedFeatures.DESERT_FOSSIL
            ), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "desert_badlands_ud", desertBadlands, HolderSet.direct(factory,
                    //PlacedFeatures.CAVE_CHESTS_SMALL, PlacedFeatures.UNDERGROUND_CHESTS_SMALL, todo 会与下方产生feature order cycle
                    PlacedFeatures.UNDERGROUND_DESERT_POT,
                    PlacedFeatures.ROLLING_CACTUS,
                    PlacedFeatures.CAVE_SANDSTONE_CHESTS, PlacedFeatures.UNDERGROUND_SANDSTONE_CHESTS
            ), GenerationStep.Decoration.UNDERGROUND_DECORATION);

            addFeatures(context, "savana_vd", biome.getOrThrow(Tags.Biomes.IS_SAVANNA), HolderSet.direct(factory,
                    PlacedFeatures.BAOBAB_TREE
            ), GenerationStep.Decoration.VEGETAL_DECORATION);

            addFeatures(context, "snowy_icy_uo", snowyIcy, HolderSet.direct(factory,
                    PlacedFeatures.COLD_CRYSTAL_ORE,
                    PlacedFeatures.SLUSH
            ), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "snowy_icy_ud", snowyIcy, HolderSet.direct(factory,
                    PlacedFeatures.THIN_ICE_PATCH, PlacedFeatures.POWDER_SNOW_PATCH,
                    //PlacedFeatures.CAVE_CHESTS_SMALL, PlacedFeatures.UNDERGROUND_CHESTS_SMALL, todo 会与上方产生feature order cycle
                    PlacedFeatures.TUNDRA_POT,
                    PlacedFeatures.CAVE_FROZEN_CHESTS, PlacedFeatures.UNDERGROUND_FROZEN_CHESTS
            ), GenerationStep.Decoration.UNDERGROUND_DECORATION);
            addFeatures(context, "snowy_icy_vd", snowyIcy, HolderSet.direct(factory,
                    PlacedFeatures.SHIVERTHORN
            ), GenerationStep.Decoration.VEGETAL_DECORATION);

            addFeatures(context, "swamp_uo", biome.getOrThrow(Tags.Biomes.IS_SWAMP), HolderSet.direct(factory,
                    PlacedFeatures.GELSTONE_ORE
            ), GenerationStep.Decoration.UNDERGROUND_ORES);

            addFeatures(context, "overworld_uo", overworld, HolderSet.direct(factory,
                    PlacedFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_0, PlacedFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_1, PlacedFeatures.DEEPSLATE_ADAMANTITE_ORE_STEP_2,
                    PlacedFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_0, PlacedFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_1, PlacedFeatures.DEEPSLATE_MYTHRIL_ORE_STEP_2,
                    PlacedFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_0, PlacedFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_1, PlacedFeatures.DEEPSLATE_ORICHALCUM_ORE_STEP_2,
                    PlacedFeatures.DEEPSLATE_COBALT_ORE_STEP_0, PlacedFeatures.DEEPSLATE_COBALT_ORE_STEP_1, PlacedFeatures.DEEPSLATE_COBALT_ORE_STEP_2,
                    PlacedFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_0, PlacedFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_1, PlacedFeatures.DEEPSLATE_PALLADIUM_ORE_STEP_2,
                    PlacedFeatures.DEEPSLATE_TITANIUM_ORE_STEP_0, PlacedFeatures.DEEPSLATE_TITANIUM_ORE_STEP_1, PlacedFeatures.DEEPSLATE_TITANIUM_ORE_STEP_2,
                    PlacedFeatures.DEMONITE_ORE, PlacedFeatures.CRIMTANE_ORE, PlacedFeatures.PLATINUM_ORE, PlacedFeatures.TUNGSTEN_ORE, PlacedFeatures.SILVER_ORE, PlacedFeatures.LEAD_ORE, PlacedFeatures.TIN_ORE,
                    PlacedFeatures.RUBY_ORE, PlacedFeatures.TOPAZ_ORE, PlacedFeatures.AMETHYST_ORE, PlacedFeatures.JADE_ORE, PlacedFeatures.SAPPHIRE_ORE
            ), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "overworld_ud", overworld, HolderSet.direct(factory,
                    PlacedFeatures.AMBER_TREE, PlacedFeatures.AMETHYST_TREE, PlacedFeatures.DIAMOND_TREE, PlacedFeatures.JADE_TREE, PlacedFeatures.RUBY_TREE, PlacedFeatures.SAPPHIRE_TREE, PlacedFeatures.TOPAZ_TREE,
                    PlacedFeatures.CRIMSON_ALTAR_WORLD, PlacedFeatures.DEMON_ALTAR_WORLD,
                    PlacedFeatures.NO_TRAPS_GRAVITATION_TRAP, PlacedFeatures.NO_TRAPS_PNEUMATIC_TRAP, PlacedFeatures.NO_TRAPS_SCULK_TRAP, PlacedFeatures.NO_TRAPS_SHIMMER_TRAP,
                    PlacedFeatures.DART_TRAP, PlacedFeatures.BOULDER_TRAP, PlacedFeatures.DEATH_CHEST_TRAP,
                    PlacedFeatures.LIFE_CRYSTAL, PlacedFeatures.WATER_CHESTS, PlacedFeatures.PLATINUM_VEIN_WITH_DETONATOR, PlacedFeatures.GOLD_VEIN_WITH_DETONATOR
            ), GenerationStep.Decoration.UNDERGROUND_DECORATION);
            addFeatures(context, "overworld_vd", overworld, HolderSet.direct(factory,
                    PlacedFeatures.LIFE_MUSHROOM
            ), GenerationStep.Decoration.VEGETAL_DECORATION);
            addFeatures(context, "overworld_tlm", overworld, HolderSet.direct(factory,
                    PlacedFeatures.NO_TRAPS_SCULK_SENSOR_WITH_TNT
            ), GenerationStep.Decoration.TOP_LAYER_MODIFICATION);

            addFeatures(context, "beach_ocean_uo", new OrHolderSet<>(biome.getOrThrow(Tags.Biomes.IS_BEACH), biome.getOrThrow(Tags.Biomes.IS_OCEAN)), HolderSet.direct(factory,
                    PlacedFeatures.MARINE_GRAVEL, PlacedFeatures.OPAL_ORE
            ), GenerationStep.Decoration.UNDERGROUND_ORES);

            addFeatures(context, "forest_like_ud", forestLike, HolderSet.direct(factory,
                    PlacedFeatures.FOREST_POT, PlacedFeatures.CAVE_CHESTS, PlacedFeatures.UNDERGROUND_CHESTS,
                    PlacedFeatures.BLINKROOT,
                    PlacedFeatures.SURFACE_CHESTS, PlacedFeatures.GEMSTONE_CAVE
            ), GenerationStep.Decoration.UNDERGROUND_DECORATION);
            addFeatures(context, "forest_like_vd", forestLike, HolderSet.direct(factory,
                    PlacedFeatures.FOREST_DROOPING_VINE,
                    PlacedFeatures.FOREST_CATTAILS,
                    PlacedFeatures.DAYBLOOM
            ), GenerationStep.Decoration.VEGETAL_DECORATION);
            addFeatures(context, "forest_like_uo", forestLike, HolderSet.direct(factory,
                    PlacedFeatures.SILT_BLOCK
            ), GenerationStep.Decoration.UNDERGROUND_ORES);
            addFeatures(context, "jungle_like_vd", jungleLike, HolderSet.direct(factory,
                    PlacedFeatures.MOONGLOW,
                    PlacedFeatures.JUNGLE_ROSE,
                    PlacedFeatures.JUNGLE_SPORE,
                    PlacedFeatures.JUNGLE_DROOPING_VINE,
                    PlacedFeatures.JUNGLE_CATTAILS,
                    PlacedFeatures.UNDERGROUND_JUNGLE_GRASS,
                    PlacedFeatures.UNDERGROUND_JUNGLE_BUSH,
                    PlacedFeatures.UNDERGROUND_JUNGLE_TREE,
                    PlacedFeatures.NATURES_GIFT
            ), GenerationStep.Decoration.VEGETAL_DECORATION);
            addFeatures(context, "jungle_like_ud", jungleLike, HolderSet.direct(factory,
                    PlacedFeatures.JUNGLE_POT,
                    PlacedFeatures.UNDERGROUND_MOONGLOW
            ), GenerationStep.Decoration.UNDERGROUND_DECORATION);
            addFeatures(context, "jungle_like_uo", jungleLike, HolderSet.direct(factory,
                    PlacedFeatures.CHLOROPHYTE_ORE
            ), GenerationStep.Decoration.UNDERGROUND_ORES);

            addFeatures(context, "beach_vd", biome.getOrThrow(Tags.Biomes.IS_BEACH), HolderSet.direct(factory,
                    PlacedFeatures.PALM_TREE
            ), GenerationStep.Decoration.VEGETAL_DECORATION);

            addFeatures(context, "nether_uo", biome.getOrThrow(Tags.Biomes.IS_NETHER), HolderSet.direct(factory,
                    PlacedFeatures.NETHERRACK_HELLSTONE, PlacedFeatures.ADDITIONAL_ANCIENT_DEBRIS
            ), GenerationStep.Decoration.UNDERGROUND_ORES);

            HolderGetter<ConfiguredWorldCarver<?>> configuredWorldCarver = context.lookup(Registries.CONFIGURED_CARVER);
            addCarvers(context, "desert_air", desert, HolderSet.direct(configuredWorldCarver.getOrThrow(ConfiguredWorldCarvers.DESERT_CAVE_CARVER)), GenerationStep.Carving.AIR);
            addCarvers(context, "jungle_air", jungle, HolderSet.direct(configuredWorldCarver.getOrThrow(ConfiguredWorldCarvers.JUNGLE_CAVE_CARVER)), GenerationStep.Carving.AIR);
            addCarvers(context, "overworld_air", overworld, HolderSet.direct(configuredWorldCarver.getOrThrow(ConfiguredWorldCarvers.WAVY_CAVE_CARVER)), GenerationStep.Carving.AIR);

            register(context, createModifierKey("common_beach"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    biome.getOrThrow(Tags.Biomes.IS_BEACH),
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEAnimals.CRAB.get(), 7, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.TROPIC_SLIME.get(), 7, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOBLIN_SCOUT.get(), 15, 1, 1)
                    )
            ));
            register(context, createModifierKey("common_desert"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    desertBadlands,
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEAnimals.SCORPION.get(), 15, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.DESERT_SLIME.get(), 15, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.TOMB_CRAWLER.get(), 180, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.ANTLION_SWARMER.get(), 500, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GIANT_ANTLION_SWARMER.get(), 200, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.WOODEN_MIMIC.get(), 2, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOLDEN_MIMIC.get(), 2, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.MUMMY.get(), 35, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.LIGHT_LAMIA.get(), 45, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GHOUL.get(), 35, 2, 3),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.SAND_POACHER.get(), 45, 1, 1)
                    )
            ));
            register(context, createModifierKey("common_icy"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    snowyIcy,
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.ICE_BAT.get(), 140, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.UNDEAD_VIKING.get(), 140, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.SNOW_FLINX.get(), 130, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPIKED_ICE_SLIME.get(), 130, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.ICE_SLIME.get(), 15, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.WOODEN_MIMIC.get(), 2, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.ICE_MIMIC.get(), 6, 1, 1)
                    )
            ));
            register(context, createModifierKey("common_jungle"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    jungleAndLush,
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.HORNET.get(), 170, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.JUNGLE_BAT.get(), 40, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.JUNGLE_SLIME.get(), 40, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPIKED_JUNGLE_SLIME.get(), 100, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.MAN_EATER.get(), 150, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.SNATCHER.get(), 50, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.PIRANHA.get(), 40, 2, 3),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.ARAPAIMA.get(), 40, 2, 3),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.DERPLING.get(), 80, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.WOODEN_MIMIC.get(), 2, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOLDEN_MIMIC.get(), 2, 1, 1)
                            // todo 仅 Celebrationmk10 和 Get fixed boi 世界生成 new MobSpawnSettings.SpawnerData(TEMonsterEntities.JUNGLE_MIMIC.get(), 3, 1, 1)

                    )
            ));
            register(context, createModifierKey("common_overworld"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    overworld,
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.NYMPH.get(), 3, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEAnimals.FAIRY.get(), 3, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.HARPY.get(), 60, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.WYVERN.get(), 20, 1, 1)
                    )
            ));
            register(context, createModifierKey("common_swamp"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    biome.getOrThrow(Tags.Biomes.IS_SWAMP),
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.SWAMP_SLIME.get(), 90, 1, 3)
                    )
            ));
            register(context, createModifierKey("only_forest"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    biome.getOrThrow(Tags.Biomes.IS_FOREST),
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEAnimals.BUNNY.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.SQUIRREL.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.DUCK.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.BIRD.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.BLUE_JAY.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.CARDINAL.get(), 10, 1, 2)
                    )
            ));
            register(context, createModifierKey("common_forest"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    biome.getOrThrow(ModTags.Biomes.IS_FOREST),
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLACK_SLIME.get(), 60, 1, 3),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLUE_SLIME.get(), 30, 2, 4),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.CAVE_BAT.get(), 145, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GIANT_SHELLY.get(), 90, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRAWDAD.get(), 90, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GIANT_WORM.get(), 60, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GREEN_DUMPLING_SLIME.get(), 30, 1, 3),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GREEN_SLIME.get(), 45, 3, 3),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.PINK_SLIME.get(), 2, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.PURPLE_SLIME.get(), 15, 1, 3),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.RED_SLIME.get(), 45, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.YELLOW_SLIME.get(), 45, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEMON_EYE.get(), 65, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.POSSESS_ARMOR.get(), 65, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.WRAITH.get(), 65, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.FLYING_FISH.get(), 60, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.WOODEN_MIMIC.get(), 2, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOLDEN_MIMIC.get(), 2, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEAnimals.BUNNY.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.SQUIRREL.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.DUCK.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.BIRD.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.BLUE_JAY.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.CARDINAL.get(), 10, 1, 2),
                            new MobSpawnSettings.SpawnerData(TEAnimals.SNAIL.get(), 10, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEAnimals.WORM.get(), 15, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLUE_JELLYFISH.get(), 5, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GREEN_JELLYFISH.get(), 5, 1, 1)
                    )
            ));
            register(context, createModifierKey("common_nether_wastes"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    biome.getOrThrow(Tags.Biomes.IS_NETHER),
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.LAVA_SLIME.get(), 25, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.HELL_BAT.get(), 20, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.FIRE_IMP.get(), 13, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BONE_SERPENT.get(), 1, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEMON.get(), 7, 1, 1)
                    )
            ));
            register(context, createModifierKey("common_ocean"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    biome.getOrThrow(Tags.Biomes.IS_OCEAN),
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.PINK_JELLYFISH.get(), 1, 1, 1),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHARK.get(), 1, 1, 1)
                    )
            ));
            register(context, createModifierKey("flower_forest"), new BiomeModifiers.AddSpawnsBiomeModifier(
                    biome.getOrThrow(Tags.Biomes.IS_FLOWER_FOREST),
                    List.of(
                            new MobSpawnSettings.SpawnerData(TEAnimals.BUTTERFLY.get(), 30, 1, 3)
                    )
            ));
        }

        private static void addFeatures(BootstrapContext<BiomeModifier> context, String path, HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) {
            context.register(Confluence.asResourceKey(NeoForgeRegistries.Keys.BIOME_MODIFIERS, path), new BiomeModifiers.AddFeaturesBiomeModifier(biomes, features, step));
        }

        private static void addCarvers(BootstrapContext<BiomeModifier> context, String path, HolderSet<Biome> biomes, HolderSet<ConfiguredWorldCarver<?>> carvers, GenerationStep.Carving step) {
            context.register(Confluence.asResourceKey(NeoForgeRegistries.Keys.BIOME_MODIFIERS, path), new BiomeModifiers.AddCarversBiomeModifier(biomes, carvers, step));
        }

        private static ResourceKey<BiomeModifier> createModifierKey(String name) {return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Confluence.asResource("mob_spawner/" + name));}

        private static Holder.Reference<BiomeModifier> register(BootstrapContext<BiomeModifier> context, ResourceKey<BiomeModifier> key, BiomeModifier value) {
            return context.register(key, value, Lifecycle.stable());
        }

        private HolderSet.Direct<Biome> getHolderSet(HolderGetter<Biome> biomeLookup, ResourceKey<Biome>... biomeNames) {
            return HolderSet.direct(Arrays.stream(biomeNames).map(biomeLookup::getOrThrow).toList());
        }
    }

    private static class Biomes {
        private static void boostrap(BootstrapContext<Biome> context) {
            HolderGetter<ConfiguredWorldCarver<?>> worldCarvers = context.lookup(Registries.CONFIGURED_CARVER);
            HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

            context.register(ModBiomes.THE_CORRUPTION, new Biome.BiomeBuilder().hasPrecipitation(true).temperature(1).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-9030507).grassColorOverride(-9351806).skyColor(-10726554).fogColor(-10726554).waterColor(-12837542).waterFogColor(-11055776).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 30, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_SLIME.get(), 35, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_MIMIC.get(), 1, 1, 1))
                            .build())
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        addDefaultGenerations(builder);
                        builder.addCarver(GenerationStep.Carving.AIR, ConfiguredWorldCarvers.DEMONIC_CAVE_CARVER);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.EBONY_TREE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.CORRUPT_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.VILE_MUSHROOM);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.CORRUPT_DROOPING_VINE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.CORRUPT_CATTAILS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.DEATHWEED);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, PlacedFeatures.CORRUPTION_POT);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, PlacedFeatures.DEMON_ALTAR_BIOME);
                    })).build());
            context.register(ModBiomes.THE_CORRUPTION_DESERT, new Biome.BiomeBuilder().temperature(2).downfall(0).hasPrecipitation(false)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-9030507).grassColorOverride(-9351806).skyColor(-10726554).fogColor(-10726554).waterColor(-12837542).waterFogColor(-11055776).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 22, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_SLIME.get(), 35, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_MIMIC.get(), 1, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DARK_MUMMY.get(), 35, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DARK_LAMIA.get(), 45, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.VILE_GHOUL.get(), 35, 2, 3))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_CORRUPTION_TUNDRA, new Biome.BiomeBuilder().temperature(0).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-9030507).grassColorOverride(-9351806).skyColor(-10726554).fogColor(-10726554).waterColor(-12837542).waterFogColor(-11055776).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DECAYEDER.get(), 22, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEVOURER.get(), 3, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.EATER_OF_SOULS.get(), 75, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_SLIME.get(), 35, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CORRUPT_MIMIC.get(), 1, 1, 1))
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
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMERA.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSLIME.get(), 35, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_MIMIC.get(), 1, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HERPLING.get(), 60, 1, 1))
                            .build())
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        addDefaultGenerations(builder);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.SHADOW_TREE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.CRIMSON_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.CRIMSON_DROOPING_VINE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.CRIMSON_CATTAILS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.VICIOUS_MUSHROOM);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.DEATHWEED);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, PlacedFeatures.CRIMSON_ALTAR_BIOME);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, PlacedFeatures.CRIMSON_POT);
                    })).build()
            );
            context.register(ModBiomes.THE_CRIMSON_DESERT, new Biome.BiomeBuilder().temperature(2).downfall(0).hasPrecipitation(false)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-2282195).grassColorOverride(-4436402).skyColor(-8827314).fogColor(-8827314).waterColor(-7069664).waterFogColor(-7451572).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMERA.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSLIME.get(), 35, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_MIMIC.get(), 1, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_MUMMY.get(), 35, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DARK_LAMIA.get(), 45, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HERPLING.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.TAINTED_GHOUL.get(), 35, 2, 3))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_CRIMSON_TUNDRA, new Biome.BiomeBuilder().temperature(0).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-2282195).grassColorOverride(-4436402).skyColor(-8827314).fogColor(-8827314).waterColor(-7069664).waterFogColor(-7451572).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOOD_CRAWLER.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BLOODY_SPORE.get(), 30, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMERA.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FACE_MONSTER.get(), 60, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSLIME.get(), 35, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.CRIMSON_MIMIC.get(), 1, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HERPLING.get(), 60, 1, 1))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_HALLOW, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-16711703).grassColorOverride(-3999757).fogColor(-3346188).waterColor(-1554953).waterFogColor(-3345167).skyColor(-3346188).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.PIXIE.get(), 60, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LUMINOUS_SLIME.get(), 35, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HALLOWED_MIMIC.get(), 1, 1, 1))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_HALLOW_DESERT, new Biome.BiomeBuilder().temperature(2).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-16711703).grassColorOverride(-3999757).fogColor(-3347468).waterColor(-1554953).waterFogColor(-3345167).skyColor(-3346188).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.PIXIE.get(), 60, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LUMINOUS_SLIME.get(), 35, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HALLOWED_MIMIC.get(), 1, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LIGHT_MUMMY.get(), 35, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LIGHT_LAMIA.get(), 45, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DREAMER_GHOUL.get(), 35, 2, 3))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.THE_HALLOW_TUNDRA, new Biome.BiomeBuilder().temperature(0f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(-16711703).grassColorOverride(-3999757).fogColor(-3347468).waterColor(-1554953).waterFogColor(-3345167).skyColor(-3346188).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.PIXIE.get(), 60, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LUMINOUS_SLIME.get(), 35, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HALLOWED_MIMIC.get(), 1, 1, 1))
                            .build())
                    .generationSettings(BiomeGenerationSettings.EMPTY)
                    .build()
            );
            context.register(ModBiomes.ASH_FOREST, new Biome.BiomeBuilder().hasPrecipitation(false).temperature(2).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(10387789).grassColorOverride(9470285).fogColor(-10541025).waterColor(-10541025).waterFogColor(4159204).skyColor(-4592650).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEMON.get(), 10, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.VOODOO_DEMON.get(), 2, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FIRE_IMP.get(), 25, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BONE_SERPENT.get(), 1, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HELL_BAT.get(), 60, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LAVA_SLIME.get(), 80, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1))
                            // .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEAnimals.MAGMA_SNAIL.get(), 20, 1, 2))  //todo 地狱中小动物生成
                            // .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEAnimals.HELL_BUTTERFLY.get(), 20, 1, 2))
                            .build())
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.FIREBLOSSOM);
                        builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, PlacedFeatures.ASH_HELLSTONE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.ASH_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.ASH_TREE);
                    })).build()
            );
            context.register(ModBiomes.ASH_WASTELAND, new Biome.BiomeBuilder().hasPrecipitation(false).temperature(2).downfall(0)
                    .specialEffects(new BiomeSpecialEffects.Builder().foliageColorOverride(10387789).grassColorOverride(9470285).fogColor(-10541025).waterColor(-10541025).waterFogColor(4159204).skyColor(-4592650).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.DEMON.get(), 15, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.VOODOO_DEMON.get(), 4, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.FIRE_IMP.get(), 20, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.BONE_SERPENT.get(), 1, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HELL_BAT.get(), 40, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.LAVA_SLIME.get(), 40, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHADOW_MIMIC.get(), 1, 1, 1))
                            // .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEAnimals.MAGMA_SNAIL.get(), 20, 1, 2)) //todo 地狱中小动物生成
                            // .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEAnimals.HELL_BUTTERFLY.get(), 20, 1, 2))
                            .build())
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        builder.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, PlacedFeatures.ASH_HELLSTONE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.FIREBLOSSOM);
                    })).build()
            );
            context.register(ModBiomes.GLOWING_MUSHROOM, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(12638463).waterColor(4159204).waterFogColor(329011).skyColor(12638463).build())
                    .mobSpawnSettings(new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_BAT.get(), 60, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_SKELETON.get(), 60, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.SPORE_ZOMBIE.get(), 45, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.HAT_SPORE_ZOMBIE.get(), 15, 1, 2))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.WOODEN_MIMIC.get(), 1, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEMonsterEntities.GOLDEN_MIMIC.get(), 1, 1, 1))
                            .addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TEAnimals.GLOWING_SNAIL.get(), 10, 1, 2))
                            .build())
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        addDefaultGenerations(builder);
                        builder.addCarver(GenerationStep.Carving.AIR, ConfiguredWorldCarvers.GLOWING_MUSHROOM_CAVE_CARVER);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.HUGE_LIFE_MUSHROOM_TREE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.GLOWING_MUSHROOM);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.GLOWING_MUSHROOM_LIFE_CRYSTAL);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.GLOWING_MUSHROOM_TREE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.GLOWING_MUSHROOM_VINE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.GLOWING_MUSHROOM_CATTAILS);
                    })).build()
            );
            context.register(ModBiomes.CHORUS_FOREST, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0x000000).waterColor(0x000000).waterFogColor(0x000000).skyColor(0x000000).build())
                    .mobSpawnSettings(mobSpawnSettings(BiomeDefaultFeatures::endSpawns))
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.VOID_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.TALL_VOID_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.VOID_VIOLET);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.VOID_TREE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.VOID_TREE_LESS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.SILENT_DROOPING_VINE);
                        builder.addFeature(GenerationStep.Decoration.LAKES, PlacedFeatures.VOID_HUGE_STONE);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.DRAGONSAL_ORE);
                        builder.addFeature(GenerationStep.Decoration.RAW_GENERATION, PlacedFeatures.END_BROKEN_STONE);
                    })).build()
            );
            context.register(ModBiomes.CHORUS_PLAINS, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f).hasPrecipitation(false)
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0x000000).waterColor(0x000000).waterFogColor(0x000000).skyColor(0x000000).build())
                    .mobSpawnSettings(mobSpawnSettings(BiomeDefaultFeatures::endSpawns))
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.VOID_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.TALL_VOID_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.VOID_VIOLET);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.VOID_TREE_LESS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.SILENT_DROOPING_VINE);
                        builder.addFeature(GenerationStep.Decoration.LAKES, PlacedFeatures.VOID_HUGE_STONE);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.DRAGONSAL_ORE);
                        builder.addFeature(GenerationStep.Decoration.RAW_GENERATION, PlacedFeatures.END_BROKEN_STONE);
                    })).build()
            );
            context.register(ModBiomes.INVERSE_FOREST, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f).hasPrecipitation(false)
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0x000000).waterColor(0x000000).waterFogColor(0x000000).skyColor(0x000000).build())
                    .mobSpawnSettings(mobSpawnSettings(builder -> {
                        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.INVERSE_ENDERMAN.get(), 10, 4, 4));
                    }))
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.DRAGONSAL_ORE);
                    })).build()
            );
            context.register(ModBiomes.INVERSE_PLAINS, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f).hasPrecipitation(false)
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0x000000).waterColor(0x000000).waterFogColor(0x000000).skyColor(0x000000).build())
                    .mobSpawnSettings(mobSpawnSettings(builder -> {
                        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.INVERSE_ENDERMAN.get(), 10, 4, 4));
                    }))
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.DRAGONSAL_ORE);
                    })).build()
            );
            context.register(ModBiomes.MOONBLIGHT_FOREST, new Biome.BiomeBuilder().temperature(0.5f).downfall(0.5f).hasPrecipitation(false)
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0x000000).waterColor(0x000000).waterFogColor(0x000000).skyColor(0x000000).build())
                    .mobSpawnSettings(mobSpawnSettings(BiomeDefaultFeatures::endSpawns))
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.DRAGONSAL_ORE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.MOONGLOW_WILLOW_TREE);
                        builder.addFeature(GenerationStep.Decoration.LAKES, PlacedFeatures.END_HUGE_STONE);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.SILVER_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.TALL_SILVER_GRASS);
                    })).build()
            );
            context.register(ModBiomes.MOONBLIGHT_PLAINS, new Biome.BiomeBuilder().temperature(2f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0x000000).waterColor(0x000000).waterFogColor(0x000000).skyColor(0x000000).build())
                    .mobSpawnSettings(mobSpawnSettings(BiomeDefaultFeatures::endSpawns))
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.DRAGONSAL_ORE);
                        builder.addFeature(GenerationStep.Decoration.LAKES, PlacedFeatures.END_HUGE_STONE);
                        builder.addFeature(GenerationStep.Decoration.RAW_GENERATION, PlacedFeatures.END_BROKEN_STONE_LESS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.SILVER_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.TALL_SILVER_GRASS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.MOONGLOW_WILLOW_TREE_LESS);
                    })).build()
            );
            context.register(ModBiomes.MOONLIT_DRY_SEA, new Biome.BiomeBuilder().temperature(2f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0x000000).waterColor(0x000000).waterFogColor(0x000000).skyColor(0x000000).build())
                    .mobSpawnSettings(mobSpawnSettings(BiomeDefaultFeatures::endSpawns))
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        builder.addCarver(GenerationStep.Carving.AIR, ConfiguredWorldCarvers.DRY_SEA_CARVER);
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.DRAGONSAL_ORE);
                        builder.addFeature(GenerationStep.Decoration.LAKES, PlacedFeatures.LUNAR_CORAL);
                        builder.addFeature(GenerationStep.Decoration.LAKES, PlacedFeatures.LUNAR_CORAL_HUGE_STONE);
                        builder.addFeature(GenerationStep.Decoration.RAW_GENERATION, PlacedFeatures.END_BROKEN_STONE_LESS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.WITHERED_SEA_SILK);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.SEA_SILVER_GRASS);
                    })).build()
            );
            context.register(ModBiomes.DARK_MOON_FLATS, new Biome.BiomeBuilder().temperature(2f).downfall(0.5f)
                    .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0x000000).waterColor(0x000000).waterFogColor(0x000000).skyColor(0x000000).build())
                    .mobSpawnSettings(mobSpawnSettings(BiomeDefaultFeatures::endSpawns))
                    .generationSettings(biomeGenerationSettings(placedFeatures, worldCarvers, builder -> {
                        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatures.DRAGONSAL_ORE);
                        builder.addFeature(GenerationStep.Decoration.LAKES, PlacedFeatures.LUNAR_CORAL);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.MOONGLOW_WILLOW_TREE_LESS);
                        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatures.SEA_SILVER_GRASS);
                    })).build()
            );
        }

        private static MobSpawnSettings mobSpawnSettings(Consumer<MobSpawnSettings.Builder> consumer) {
            MobSpawnSettings.Builder builder = new MobSpawnSettings.Builder();
            consumer.accept(builder);
            return builder.build();
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

        private static BiomeGenerationSettings biomeGenerationSettings(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers, Consumer<BiomeGenerationSettings.Builder> consumer) {
            BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);
            consumer.accept(builder);
            return builder.build();
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
                    .withEffect(ModEnchantments.EffectComponentTypes.LESS_MANA_MORE_ATTACK.get(), new AddValue(LevelBasedValue.perLevel(0.5f)), isMagic)
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
                    .withEffect(ModEnchantments.EffectComponentTypes.MORE_MANA_MORE_ATTACK.get(), new AddValue(LevelBasedValue.perLevel(0.5f)), isMagic)
            );
        }

        private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
            context.register(key, builder.build(key.location()));
        }
    }

    public static class ProcessorListz {
        private static final ResourceKey<StructureProcessorList> DESERT_UNDERGROUND_CABINS = key("desert_underground_cabins");
        private static final ResourceKey<StructureProcessorList> SHIMMER_LAKE = key("shimmer_lake");

        private static ResourceKey<StructureProcessorList> key(String path) {
            return Confluence.asResourceKey(Registries.PROCESSOR_LIST, path);
        }

        private static void bootstrap(BootstrapContext<StructureProcessorList> context) {
            BlockMatchTest stoneMatchTest = new BlockMatchTest(Blocks.STONE);
            BlockState stone = Blocks.STONE.defaultBlockState();
            BlockMatchTest deepslateMatchTest = new BlockMatchTest(Blocks.DEEPSLATE);
            BlockState deepslate = Blocks.DEEPSLATE.defaultBlockState();

            context.register(DESERT_UNDERGROUND_CABINS, new StructureProcessorList(List.of(new RuleProcessor(List.of(
                    new ProcessorRule(new BlockMatchTest(Blocks.OAK_STAIRS), stoneMatchTest, stone),
                    new ProcessorRule(new BlockMatchTest(Blocks.OAK_STAIRS), deepslateMatchTest, deepslate),
                    new ProcessorRule(new BlockMatchTest(Blocks.SPRUCE_STAIRS), stoneMatchTest, stone),
                    new ProcessorRule(new BlockMatchTest(Blocks.SPRUCE_STAIRS), deepslateMatchTest, deepslate),
                    new ProcessorRule(new BlockMatchTest(Blocks.SPRUCE_SLAB), stoneMatchTest, stone),
                    new ProcessorRule(new BlockMatchTest(Blocks.SPRUCE_SLAB), deepslateMatchTest, deepslate),
                    new ProcessorRule(new BlockMatchTest(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS), AlwaysTrueTest.INSTANCE, Blocks.SPRUCE_PLANKS.defaultBlockState())
            )))));
            context.register(SHIMMER_LAKE, new StructureProcessorList(List.of(new RuleProcessor(List.of(
                    new ProcessorRule(new RandomBlockMatchTest(Blocks.DIAMOND_BLOCK, 0.3F), AlwaysTrueTest.INSTANCE, stone),
                    new ProcessorRule(new BlockMatchTest(Blocks.DIAMOND_BLOCK), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()),
                    new ProcessorRule(new RandomBlockMatchTest(Blocks.EMERALD_BLOCK, 0.05F), AlwaysTrueTest.INSTANCE, NatureBlocks.AETHERIUM_BLOCK.get().defaultBlockState()),
                    new ProcessorRule(new RandomBlockMatchTest(Blocks.EMERALD_BLOCK, 0.3F), AlwaysTrueTest.INSTANCE, stone),
                    new ProcessorRule(new BlockMatchTest(Blocks.EMERALD_BLOCK), AlwaysTrueTest.INSTANCE, ModBlocks.SHIMMER.get().defaultBlockState())
            )))));
        }
    }

    public static class TemplatePools {
        private static final ResourceKey<StructureTemplatePool> AIR$AIR = key("air/air");
        private static final ResourceKey<StructureTemplatePool> CRIMSON_FOSSIL$START = key("crimson_fossil/start");
        private static final ResourceKey<StructureTemplatePool> CRIMSON_FOSSIL$BEHIND = key("crimson_fossil/behind");
        private static final ResourceKey<StructureTemplatePool> CRIMSON_FOSSIL$FRONT = key("crimson_fossil/front");
        private static final ResourceKey<StructureTemplatePool> CRIMSON_FOSSIL$MAIN = key("crimson_fossil/main");
        private static final ResourceKey<StructureTemplatePool> DUNGEON_ALTAR$DUNGEON_ALTAR = key("dungeon_altar/dungeon_altar");
        private static final ResourceKey<StructureTemplatePool> DESERT_UNDERGROUND_CABINS$DESERT_UNDERGROUND_CABINS = key("desert_underground_cabins/desert_underground_cabins");
        private static final ResourceKey<StructureTemplatePool> EBONY_STONE_THORN$EBONY_STONE_THORN = key("ebony_stone_thorn/ebony_stone_thorn");

        private static ResourceKey<StructureTemplatePool> key(String path) {
            return Confluence.asResourceKey(Registries.TEMPLATE_POOL, path);
        }

        private static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
            HolderGetter<StructureTemplatePool> templatePool = context.lookup(Registries.TEMPLATE_POOL);
            HolderGetter<StructureProcessorList> processorList = context.lookup(Registries.PROCESSOR_LIST);
            Holder<StructureTemplatePool> emptyTemplatePool = templatePool.getOrThrow(Pools.EMPTY);
            Holder<StructureProcessorList> emptyProcessorList = processorList.getOrThrow(ProcessorLists.EMPTY);

            register(context, AIR$AIR, "confluence:air/air", emptyTemplatePool, emptyProcessorList);
            register(context, CRIMSON_FOSSIL$START, "confluence:crimson_fossil/dgh_0", emptyTemplatePool, emptyProcessorList);
            register(context, CRIMSON_FOSSIL$BEHIND, "confluence:crimson_fossil/dgh_1", emptyTemplatePool, emptyProcessorList);
            register(context, CRIMSON_FOSSIL$FRONT, "confluence:crimson_fossil/dgh_2", emptyTemplatePool, emptyProcessorList);
            register(context, CRIMSON_FOSSIL$MAIN, "confluence:crimson_fossil/crimson_fossil_start", emptyTemplatePool, emptyProcessorList);
            register(context, DUNGEON_ALTAR$DUNGEON_ALTAR, "confluence:dungeon_altar/dungeon_altar", emptyTemplatePool, emptyProcessorList);
            register(context, DESERT_UNDERGROUND_CABINS$DESERT_UNDERGROUND_CABINS, "confluence:desert_underground_cabins/desert_underground_cabins", emptyTemplatePool, processorList.getOrThrow(ProcessorListz.DESERT_UNDERGROUND_CABINS));
            register(context, EBONY_STONE_THORN$EBONY_STONE_THORN, "confluence:ebony_stone_thorn/ebony_stone_thorn", emptyTemplatePool, emptyProcessorList);
        }

        private static void register(BootstrapContext<StructureTemplatePool> context, ResourceKey<StructureTemplatePool> key, String location, Holder<StructureTemplatePool> fallback, Holder<StructureProcessorList> processors) {
            context.register(key, new StructureTemplatePool(fallback, Collections.singletonList(new Pair<>(SinglePoolElement.single(location, processors).apply(StructureTemplatePool.Projection.RIGID), 1))));
        }
    }

    public static class Structures {
        private static void boostrap(BootstrapContext<Structure> context) {
            HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
            HolderLookup.RegistryLookup<Biome> biomeLookup = registryLookup(Registries.BIOME, biome);
            HolderGetter<StructureTemplatePool> templatePool = context.lookup(Registries.TEMPLATE_POOL);
            HolderSet<Biome> overworld = biome.getOrThrow(Tags.Biomes.IS_OVERWORLD);
            HolderSet<Biome> notAquatic = new AndHolderSet<>(overworld, new NotHolderSet<>(biomeLookup, biome.getOrThrow(Tags.Biomes.IS_AQUATIC)));
            HolderSet<Biome> crimson = HolderSet.direct(biome.getOrThrow(ModBiomes.THE_CRIMSON));
            HolderSet<Biome> desertBadlands = new OrHolderSet<>(biome.getOrThrow(Tags.Biomes.IS_DESERT), biome.getOrThrow(Tags.Biomes.IS_BADLANDS));
            Optional<Heightmap.Types> worldSurfaceWg = Optional.of(Heightmap.Types.WORLD_SURFACE_WG);

            context.register(ModStructures.Keys.AIR, new JigsawStructure(
                    new Structure.StructureSettings(overworld),
                    templatePool.getOrThrow(TemplatePools.AIR$AIR),
                    Optional.empty(),
                    7,
                    UniformHeight.of(VerticalAnchor.absolute(64), VerticalAnchor.absolute(80)),
                    false,
                    Optional.empty(),
                    116,
                    List.of(),
                    JigsawStructure.DEFAULT_DIMENSION_PADDING,
                    LiquidSettings.IGNORE_WATERLOGGING
            ));
            context.register(ModStructures.Keys.CRIMSON_CAVE, new CrimsonCaveStructure(new Structure.StructureSettings(crimson, Map.of(), GenerationStep.Decoration.TOP_LAYER_MODIFICATION, TerrainAdjustment.NONE)));
            context.register(ModStructures.Keys.CRIMSON_FOSSIL, new JigsawStructure(
                    new Structure.StructureSettings(crimson, Map.of(), GenerationStep.Decoration.TOP_LAYER_MODIFICATION, TerrainAdjustment.NONE),
                    templatePool.getOrThrow(TemplatePools.CRIMSON_FOSSIL$START),
                    Optional.empty(),
                    7,
                    ConstantHeight.of(VerticalAnchor.absolute(-10)),
                    false,
                    worldSurfaceWg,
                    116,
                    List.of(),
                    JigsawStructure.DEFAULT_DIMENSION_PADDING,
                    LiquidSettings.APPLY_WATERLOGGING
            ));
            context.register(ModStructures.Keys.GRANITE_CAVE, new GraniteCaveStructure(new Structure.StructureSettings(overworld, Map.of(
                    MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create(
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.GRANITE_ELEMENTAL.get(), 30, 1, 1)
                    ))
            ), GenerationStep.Decoration.LOCAL_MODIFICATIONS, TerrainAdjustment.NONE)));
            context.register(ModStructures.Keys.MARBLE_CAVE, new MarbleCaveStructure(new Structure.StructureSettings(overworld, Map.of(), GenerationStep.Decoration.LOCAL_MODIFICATIONS, TerrainAdjustment.NONE)));
            context.register(ModStructures.Keys.DESERT_UNDERGROUND_CABINS, new JigsawStructure(
                    new Structure.StructureSettings(desertBadlands, Map.of(), GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.BEARD_THIN),
                    templatePool.getOrThrow(TemplatePools.DESERT_UNDERGROUND_CABINS$DESERT_UNDERGROUND_CABINS),
                    Optional.empty(),
                    7,
                    UniformHeight.of(VerticalAnchor.absolute(-50), VerticalAnchor.absolute(10)),
                    false,
                    Optional.empty(),
                    116,
                    List.of(),
                    JigsawStructure.DEFAULT_DIMENSION_PADDING,
                    LiquidSettings.IGNORE_WATERLOGGING
            ));
            context.register(ModStructures.Keys.DUNGEON, new DungeonStructure(new Structure.StructureSettings(notAquatic, Map.of(
                    MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create(
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.ANGER_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_ANGER_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_HELMET_ANGER_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.BIG_MUSCLE_ANGER_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.SHORT_BONES.get(), 240, 8, 9),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.DARK_CASTER.get(), 240, 2, 3),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.CURSED_SKULL.get(), 200, 3, 4),
                            new MobSpawnSettings.SpawnerData(TEMonsterEntities.DUNGEON_SLIME.get(), 120, 1, 2)
                    ))
            ), GenerationStep.Decoration.TOP_LAYER_MODIFICATION, TerrainAdjustment.NONE)));
            context.register(ModStructures.Keys.DUNGEON_ALTAR, new JigsawStructure(
                    new Structure.StructureSettings(notAquatic, Map.of(), GenerationStep.Decoration.TOP_LAYER_MODIFICATION, TerrainAdjustment.BEARD_THIN),
                    templatePool.getOrThrow(TemplatePools.DUNGEON_ALTAR$DUNGEON_ALTAR),
                    Optional.empty(),
                    7,
                    ConstantHeight.of(VerticalAnchor.absolute(0)),
                    false,
                    worldSurfaceWg,
                    116,
                    List.of(),
                    JigsawStructure.DEFAULT_DIMENSION_PADDING,
                    LiquidSettings.APPLY_WATERLOGGING
            ));
            context.register(ModStructures.Keys.EBONY_STONE_THORN, new JigsawStructure(
                    new Structure.StructureSettings(HolderSet.direct(biome.getOrThrow(ModBiomes.THE_CORRUPTION)), Map.of(), GenerationStep.Decoration.TOP_LAYER_MODIFICATION, TerrainAdjustment.NONE),
                    templatePool.getOrThrow(TemplatePools.EBONY_STONE_THORN$EBONY_STONE_THORN),
                    Optional.empty(),
                    7,
                    ConstantHeight.of(VerticalAnchor.absolute(-10)),
                    false,
                    worldSurfaceWg,
                    116,
                    List.of(),
                    JigsawStructure.DEFAULT_DIMENSION_PADDING,
                    LiquidSettings.APPLY_WATERLOGGING
            ));
            context.register(ModStructures.Keys.SHIMMER_LAKE, new ShimmerLakeStructure(new Structure.StructureSettings(overworld, Map.of(
                    MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create(
                            new MobSpawnSettings.SpawnerData(TEAnimals.FEALING.get(), 30, 1, 2)
                    ))
            ), GenerationStep.Decoration.VEGETAL_DECORATION, TerrainAdjustment.NONE)));
        }
    }

    public static class StructureSets {
        private static final ResourceKey<StructureSet> AIR = Confluence.asResourceKey(Registries.STRUCTURE_SET, "air");
        private static final ResourceKey<StructureSet> SHIMMER_LAKE = Confluence.asResourceKey(Registries.STRUCTURE_SET, "shimmer_lake");

        private static void bootstrap(BootstrapContext<StructureSet> context) {
            HolderGetter<Structure> structure = context.lookup(Registries.STRUCTURE);
            HolderGetter<StructureSet> structureSet = context.lookup(Registries.STRUCTURE_SET);
            HolderGetter<Biome> biome = context.lookup(Registries.BIOME);
            Holder<StructureSet> villages = structureSet.getOrThrow(BuiltinStructureSets.VILLAGES);
            Holder<StructureSet> air = structureSet.getOrThrow(AIR);

            context.register(AIR, new StructureSet(structure.getOrThrow(ModStructures.Keys.AIR), new ConcentricRingsStructurePlacement(
                    Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F,
                    11231243,
                    Optional.empty(),
                    1, 40, 10,
                    biome.getOrThrow(Tags.Biomes.IS_OVERWORLD)
            )));
            register(context, "crimson_cave", new StructureSet(structure.getOrThrow(ModStructures.Keys.CRIMSON_CAVE), new RandomSpreadStructurePlacement(
                    Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F,
                    22331363,
                    Optional.of(new StructurePlacement.ExclusionZone(villages, 8)),
                    10, 8,
                    RandomSpreadType.TRIANGULAR
            )));
            register(context, "crimson_fossil", new StructureSet(structure.getOrThrow(ModStructures.Keys.CRIMSON_FOSSIL), new RandomSpreadStructurePlacement(
                    Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F,
                    98522334,
                    Optional.of(new StructurePlacement.ExclusionZone(villages, 8)),
                    34, 8,
                    RandomSpreadType.TRIANGULAR
            )));
            register(context, "caves", new StructureSet(List.of(
                    new StructureSet.StructureSelectionEntry(structure.getOrThrow(ModStructures.Keys.GRANITE_CAVE), 1),
                    new StructureSet.StructureSelectionEntry(structure.getOrThrow(ModStructures.Keys.MARBLE_CAVE), 1)
            ), new RandomSpreadStructurePlacement(
                    Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F,
                    85917378,
                    Optional.of(new StructurePlacement.ExclusionZone(villages, 8)),
                    40, 20,
                    RandomSpreadType.TRIANGULAR
            )));
            register(context, "desert_underground_cabins", new StructureSet(structure.getOrThrow(ModStructures.Keys.DESERT_UNDERGROUND_CABINS), new RandomSpreadStructurePlacement(
                    Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F,
                    97960771,
                    Optional.of(new StructurePlacement.ExclusionZone(structureSet.getOrThrow(SHIMMER_LAKE), 10)),
                    5, 3,
                    RandomSpreadType.TRIANGULAR
            )));
            register(context, "dungeon", new StructureSet(structure.getOrThrow(ModStructures.Keys.DUNGEON), new ConcentricRingsStructurePlacement(
                    Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F,
                    0,
                    Optional.empty(),
                    48, 1, 128,
                    biome.getOrThrow(BiomeTags.STRONGHOLD_BIASED_TO)
            )));
            register(context, "dungeon_altar", new StructureSet(structure.getOrThrow(ModStructures.Keys.DUNGEON_ALTAR), new RandomSpreadStructurePlacement(
                    Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 0.6F,
                    69021806,
                    Optional.of(new StructurePlacement.ExclusionZone(air, 12)),
                    16, 8,
                    RandomSpreadType.TRIANGULAR
            )));
            register(context, "ebony_stone_thorn", new StructureSet(structure.getOrThrow(ModStructures.Keys.EBONY_STONE_THORN), new RandomSpreadStructurePlacement(
                    Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F,
                    56817723,
                    Optional.of(new StructurePlacement.ExclusionZone(villages, 8)),
                    10, 2,
                    RandomSpreadType.TRIANGULAR
            )));
            context.register(SHIMMER_LAKE, new StructureSet(structure.getOrThrow(ModStructures.Keys.SHIMMER_LAKE), new RandomSpreadStructurePlacement(
                    Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F,
                    66243647,
                    Optional.of(new StructurePlacement.ExclusionZone(air, 16)),
                    50, 30,
                    RandomSpreadType.TRIANGULAR
            )));
        }

        private static void register(BootstrapContext<StructureSet> context, String path, StructureSet set) {
            context.register(Confluence.asResourceKey(Registries.STRUCTURE_SET, path), set);
        }
    }
}
