package org.confluence.mod.common.init;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.worldgen.SecretFlagPlacementModifier;
import org.confluence.mod.common.worldgen.feature.*;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModFeatures {
    public static final Predicate<BlockState> IS_BASE_STONE = state -> state.is(BlockTags.BASE_STONE_OVERWORLD);
    public static final Predicate<BlockState> IS_REPLACEABLE = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, Confluence.MODID);
    public static final DeferredRegister<PlacementModifierType<?>> MODIFIER_TYPES = DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, Confluence.MODID);

    public static final Supplier<BoulderTrapFeature> BOULDER_TRAP = FEATURES.register("boulder_trap", () -> new BoulderTrapFeature(BoulderTrapFeature.Config.CODEC));
    public static final Supplier<DartTrapFeature> DART_TRAP = FEATURES.register("dart_trap", () -> new DartTrapFeature(DartTrapFeature.Config.CODEC));
    public static final Supplier<ColumnPatchFeature> COLUMN_PATCH = FEATURES.register("column_patch", () -> new ColumnPatchFeature(ColumnPatchFeature.Config.CODEC));
    public static final Supplier<DeathChestTrapFeature> DEATH_CHEST_TRAP = FEATURES.register("death_chest_trap", () -> new DeathChestTrapFeature(DeathChestTrapFeature.Config.CODEC));
    public static final Supplier<WithDetonatorFeature> WITH_DETONATOR = FEATURES.register("with_detonator", () -> new WithDetonatorFeature(WithDetonatorFeature.Config.CODEC));
    public static final Supplier<FallingSandTrapFeature> FALLING_SAND_TRAP = FEATURES.register("falling_sand_trap", () -> new FallingSandTrapFeature(FallingSandTrapFeature.Config.CODEC));
    public static final Supplier<SculkSensorWithTNTFeature> SCULK_SENSOR_WITH_TNT = FEATURES.register("sculk_sensor_with_tnt", () -> new SculkSensorWithTNTFeature(SculkSensorWithTNTFeature.Config.CODEC));

    public static final Supplier<JewelryTreeFeature> JEWELRY_TREE = FEATURES.register("jewelry_tree", () -> new JewelryTreeFeature(JewelryTreeFeature.Config.CODEC));
    public static final Supplier<SimpleBlockNBTFeature> SIMPLE_BLOCK_NBT = FEATURES.register("simple_block_nbt", () -> new SimpleBlockNBTFeature(SimpleBlockNBTFeature.Config.CODEC));
    public static final Supplier<PalmTreeFeature> PALM_TREE = FEATURES.register("palm_tree", () -> new PalmTreeFeature(PalmTreeFeature.Config.CODEC));
    public static final Supplier<QueenBeeHiveFeature> QUEEN_BEE_HIVE = FEATURES.register("queen_bee_hive", () -> new QueenBeeHiveFeature(QueenBeeHiveFeature.Config.CODEC));
    public static final Supplier<DroopingVineTreeFeature> DROOPING_VINE_TREE = FEATURES.register("drooping_vine_tree", () -> new DroopingVineTreeFeature(DroopingVineTreeFeature.Config.CODEC));
    public static final Supplier<DroopingBlockFeature> DROOPING_BLOCK = FEATURES.register("drooping_block", () -> new DroopingBlockFeature(DroopingBlockFeature.Config.CODEC));
    public static final Supplier<MushroomTreeFeature> MUSHROOM_TREE = FEATURES.register("mushroom_tree", () -> new MushroomTreeFeature(MushroomTreeFeature.Config.CODEC));
    public static final Supplier<BaobabTreeFeature> BAOBAB_TREE = FEATURES.register("baobab_tree", () -> new BaobabTreeFeature(BaobabTreeFeature.Config.CODEC));
    public static final Supplier<BranchTreeFeature> BRANCH_TREE = FEATURES.register("branch_tree", () -> new BranchTreeFeature(BranchTreeFeature.Config.CODEC));
    public static final Supplier<MeteoriteFeature> METEORITE = FEATURES.register("meteorite", () -> new MeteoriteFeature(MeteoriteFeature.Config.CODEC));
    public static final Supplier<RailSupportFeature> RAIL_SUPPORT = FEATURES.register("rail_support", () -> new RailSupportFeature(RailSupportFeature.Config.CODEC));
    public static final Supplier<RailTrapFeature> RAIL_TRAP = FEATURES.register("rail_trap", () -> new RailTrapFeature(RailTrapFeature.Config.CODEC));
    public static final Supplier<GroundBlockFeature> GROUND_BLOCK = FEATURES.register("ground_block", () -> new GroundBlockFeature(GroundBlockFeature.Config.CODEC));

    public static final Supplier<PlacementModifierType<SecretFlagPlacementModifier>> SECRET_FLAG_PLACEMENT_MODIFIER = MODIFIER_TYPES.register("secret_flag", () -> () -> SecretFlagPlacementModifier.CODEC);

    public static Tuple<BlockPos, BlockState> getPressurePlate(WorldGenLevel level, BlockPos supportPos) {
        boolean isDeepslate = level.isStateAtPosition(supportPos, blockState -> blockState.is(Blocks.DEEPSLATE));
        if (ModSecretSeeds.NO_TRAPS.match(level.getLevel().getServer())) {
            return new Tuple<>(supportPos, (isDeepslate
                    ? FunctionalBlocks.DEEPSLATE_PRESSURE_BLOCK
                    : FunctionalBlocks.STONE_PRESSURE_BLOCK).get().defaultBlockState());
        }
        return new Tuple<>(supportPos.above(), (isDeepslate
                ? FunctionalBlocks.DEEPSLATE_PRESSURE_PLATE
                : FunctionalBlocks.STONE_PRESSURE_PLATE).get().defaultBlockState());
    }

    public static BlockState getDartTrap(WorldGenLevel level, BlockPos pos, Direction facing) {
        if (ModSecretSeeds.NO_TRAPS.match(level.getLevel().getServer())) {
            return (level.isStateAtPosition(pos, blockState -> blockState.is(Blocks.DEEPSLATE))
                    ? FunctionalBlocks.DEEPSLATE_DART_TRAP
                    : FunctionalBlocks.STONE_DART_TRAP).get().defaultBlockState().setValue(BlockStateProperties.FACING, facing);
        }
        return FunctionalBlocks.DART_TRAP.get().defaultBlockState().setValue(BlockStateProperties.FACING, facing);
    }

    public static BlockState getBoulder(WorldGenLevel level, RandomSource random, BlockState original) {
        if (ModSecretSeeds.BOULDER_WORLD.match(level.getLevel().getServer())) {
            int i = random.nextInt(3);
            if (i == 0) return FunctionalBlocks.FOLLOWER_BOULDER.get().defaultBlockState();
            if (i == 1) return FunctionalBlocks.EXPLODE_BOULDER.get().defaultBlockState();
        }
        return original;
    }

    public static @Nullable INetworkEntity getNetworkEntity(WorldGenLevel level, BlockPos blockPos) {
        if (level.getBlockEntity(blockPos) instanceof INetworkEntity entity) {
            return entity;
        }
        LibUtils.devRun(() -> Confluence.LOGGER.error("Failed to fetch mechanical block entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        return null;
    }

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
        MODIFIER_TYPES.register(eventBus);
    }

    public static class Configured {
        public static final ResourceKey<ConfiguredFeature<?, ?>> SHADOW = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("trees_set/trees_crimson"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> EBONY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("trees_set/trees_corruption"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> PALM = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("palm_tree_checked"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> PEARL = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("trees_set/trees_hallow"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> RUBY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("ruby_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> AMBER = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("amber_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> TOPAZ = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("topaz_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> JADE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("jade_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> DIAMOND = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("diamond_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> SAPPHIRE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("sapphire_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> AMETHYST = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("amethyst_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> ASH = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("ash_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> LIVING = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("living_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_YELLOW_WILLOW = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("yellow_willow"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> BAOBAB = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("baobab_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> GLOWING_MUSHROOM = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("glowing_mushroom_tree"));
    }

    public static class Placed {
        public static final ResourceKey<PlacedFeature> PLACED_TREES_CORRUPTION = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("trees_corruption"));
        public static final ResourceKey<PlacedFeature> PLACED_CORRUPTION_POT = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("corruption_pot"));
        public static final ResourceKey<PlacedFeature> PLACED_CORRUPT_GRASS = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("corrupt_grass"));
        public static final ResourceKey<PlacedFeature> PLACED_DEMON_ALTAR = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("demon_altar"));
        public static final ResourceKey<PlacedFeature> PLACED_VILE_MUSHROOM = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("vile_mushroom"));
        public static final ResourceKey<PlacedFeature> PLACED_THE_CORRUPTION_TREE_CHECKED_0 = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("the_corruption_tree_checked_0"));
        public static final ResourceKey<PlacedFeature> PLACED_THE_CORRUPTION_TREE_CHECKED_1 = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("the_corruption_tree_checked_1"));
        public static final ResourceKey<PlacedFeature> PLACED_THE_CORRUPTION_TREE_CHECKED_2 = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("the_corruption_tree_checked_2"));

        public static final ResourceKey<PlacedFeature> PLACED_TREES_CRIMSON = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("trees_crimson"));
        public static final ResourceKey<PlacedFeature> PLACED_CRIMSON_ALTAR = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("crimson_altar"));
        public static final ResourceKey<PlacedFeature> PLACED_CRIMSON_POT = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("crimson_pot"));
        public static final ResourceKey<PlacedFeature> PLACED_CRIMSON_GRASS = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("crimson_grass"));
        public static final ResourceKey<PlacedFeature> PLACED_CRIMSON_TREE_CHECKED_0 = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("crimson_tree_checked_0"));
        public static final ResourceKey<PlacedFeature> PLACED_CRIMSON_TREE_CHECKED_1 = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("crimson_tree_checked_1"));
        public static final ResourceKey<PlacedFeature> PLACED_VICIOUS_MUSHROOM = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("vicious_mushroom"));

        public static final ResourceKey<PlacedFeature> PLACED_GLOWING_MUSHROOM = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("glowing_mushroom"));
        public static final ResourceKey<PlacedFeature> PLACED_GLOWING_MUSHROOM_LIFE_CRYSTAL = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("glowing_mushroom_life_crystal"));
        public static final ResourceKey<PlacedFeature> PLACED_GLOWING_MUSHROOM_TREE = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("glowing_mushroom_tree"));
        public static final ResourceKey<PlacedFeature> PLACED_GLOWING_MUSHROOM_VINE = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("glowing_mushroom_vine"));

        public static final ResourceKey<PlacedFeature> PLACED_ASH_HELLSTONE_GENERATES = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("ash_hellstone_generates"));
        public static final ResourceKey<PlacedFeature> PLACED_ASH_GRASS = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("ash_grass"));
        public static final ResourceKey<PlacedFeature> PLACED_ASH_TREE = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("ash_tree"));
        public static final ResourceKey<PlacedFeature> PLACED_FIREBLOSSOM = ResourceKey.create(Registries.PLACED_FEATURE, Confluence.asResource("fireblossom"));
    }

    public static final class TreeGrowers {
        public static final TreeGrower SHADOW_GROWER = registerSmallTree("shadow", Configured.SHADOW);
        public static final TreeGrower EBONY_GROWER = registerSmallTree("ebony", Configured.EBONY);
        public static final TreeGrower PALM_GROWER = registerSmallTree("palm", Configured.PALM);
        public static final TreeGrower PEARL_GROWER = registerSmallTree("pearl", Configured.PEARL);
        public static final TreeGrower RUBY_GROWER = registerSmallTree("ruby", Configured.RUBY);
        public static final TreeGrower AMBER_GROWER = registerSmallTree("amber", Configured.AMBER);
        public static final TreeGrower TOPAZ_GROWER = registerSmallTree("topaz", Configured.TOPAZ);
        public static final TreeGrower JADE_GROWER = registerSmallTree("jade", Configured.JADE);
        public static final TreeGrower DIAMOND_GROWER = registerSmallTree("diamond", Configured.DIAMOND);
        public static final TreeGrower SAPPHIRE_GROWER = registerSmallTree("sapphire", Configured.SAPPHIRE);
        public static final TreeGrower AMETHYST_GROWER = registerSmallTree("amethyst", Configured.AMETHYST);
        public static final TreeGrower ASH_GROWER = registerSmallTree("ash", Configured.ASH);
        public static final TreeGrower LIVING_GROWER = registerSmallTree("living", Configured.LIVING);
        public static final TreeGrower YELLOW_WILLOW_GROWER = registerSmallTree("yellow_willow_grower", Configured.CONFIGURED_YELLOW_WILLOW);
        public static final TreeGrower BAOBAB_GROWER = registerBigTree("baobab_grower", Configured.BAOBAB);

        private static TreeGrower registerSmallTree(String name, ResourceKey<ConfiguredFeature<?, ?>> tree) {
            return new TreeGrower(Confluence.MODID + ":" + name, Optional.empty(), Optional.of(tree), Optional.empty());
        }

        private static TreeGrower registerBigTree(String name, ResourceKey<ConfiguredFeature<?, ?>> tree) {
            return new TreeGrower(Confluence.MODID + ":" + name, Optional.of(tree), Optional.empty(), Optional.empty());
        }
    }
}
