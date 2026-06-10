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
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.worldgen.SecretFlagPlacement;
import org.confluence.mod.common.worldgen.feature.*;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ModFeatures {
    public static final Predicate<BlockState> IS_BASE_STONE = state -> state.is(BlockTags.BASE_STONE_OVERWORLD);
    public static final Predicate<BlockState> IS_REPLACEABLE = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, Confluence.MODID);
    public static final DeferredRegister<PlacementModifierType<?>> MODIFIER_TYPES = DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, Confluence.MODID);

    public static final Supplier<BoulderTrapFeature> BOULDER_TRAP = FEATURES.register("boulder_trap", () -> new BoulderTrapFeature(BoulderTrapFeature.Config.CODEC));
    public static final Supplier<DartTrapFeature> DART_TRAP = FEATURES.register("dart_trap", () -> new DartTrapFeature(DartTrapFeature.Config.CODEC));
    public static final Supplier<ColumnPatchFeature> COLUMN_PATCH = FEATURES.register("column_patch", () -> new ColumnPatchFeature(ColumnPatchFeature.Config.CODEC));
    public static final Supplier<DeathChestTrapFeature> DEATH_CHEST_TRAP = FEATURES.register("death_chest_trap", () -> new DeathChestTrapFeature(DeathChestTrapFeature.Config.CODEC));
    public static final Supplier<FallingSandTrapFeature> FALLING_SAND_TRAP = FEATURES.register("falling_sand_trap", () -> new FallingSandTrapFeature(FallingSandTrapFeature.Config.CODEC));
    public static final Supplier<SculkSensorWithTNTFeature> SCULK_SENSOR_WITH_TNT = FEATURES.register("sculk_sensor_with_tnt", () -> new SculkSensorWithTNTFeature(SculkSensorWithTNTFeature.Config.CODEC));

    public static final Supplier<SimpleBlockNBTFeature> SIMPLE_BLOCK_NBT = FEATURES.register("simple_block_nbt", () -> new SimpleBlockNBTFeature(SimpleBlockNBTFeature.Config.CODEC));
    public static final Supplier<PalmTreeFeature> PALM_TREE = FEATURES.register("palm_tree", () -> new PalmTreeFeature(PalmTreeFeature.Config.CODEC));
    public static final Supplier<DroopingVineTreeFeature> DROOPING_VINE_TREE = FEATURES.register("drooping_vine_tree", () -> new DroopingVineTreeFeature(DroopingVineTreeFeature.Config.CODEC));
    public static final Supplier<BlockPostFeature> BLOCK_POST = FEATURES.register("block_post", () -> new BlockPostFeature(BlockPostFeature.Config.CODEC));
    public static final Supplier<CattailsFeature> CATTAILS = FEATURES.register("cattails", () -> new CattailsFeature(CattailsFeature.Config.CODEC));
    public static final Supplier<MushroomTreeFeature> MUSHROOM_TREE = FEATURES.register("mushroom_tree", () -> new MushroomTreeFeature(MushroomTreeFeature.Config.CODEC));
    public static final Supplier<HugeMushroomTreeFeature> HUGE_MUSHROOM_TREE = FEATURES.register("huge_mushroom_tree", () -> new HugeMushroomTreeFeature(HugeMushroomTreeFeature.Config.CODEC));
    public static final Supplier<BaobabTreeFeature> BAOBAB_TREE = FEATURES.register("baobab_tree", () -> new BaobabTreeFeature(BaobabTreeFeature.Config.CODEC));
    public static final Supplier<PineTreeFeature> PINE_TREE = FEATURES.register("pine_tree", () -> new PineTreeFeature(PineTreeFeature.Config.CODEC));
    public static final Supplier<MoonglowWillowTreeFeature> MOONGLOW_WILLOW_TREE = FEATURES.register("moonglow_willow_tree", () -> new MoonglowWillowTreeFeature(MoonglowWillowTreeFeature.Config.CODEC));
    public static final Supplier<LunarCoralFeature> LUNAR_CORAL = FEATURES.register("lunar_coral", () -> new LunarCoralFeature(LunarCoralFeature.Config.CODEC));
    public static final Supplier<VoidTreeFeature> VOID_TREE = FEATURES.register("void_tree", () -> new VoidTreeFeature(VoidTreeFeature.Config.CODEC));
    public static final Supplier<BrokenStoneFeature> BROKEN_STONE = FEATURES.register("broken_stone", () -> new BrokenStoneFeature(BrokenStoneFeature.Config.CODEC));
    public static final Supplier<HugeStoneFeature> HUGE_STONE = FEATURES.register("huge_stone", () -> new HugeStoneFeature(HugeStoneFeature.Config.CODEC));
    public static final Supplier<BilayerOreFeature> BILAYER_ORE = FEATURES.register("bilayer_ore", () -> new BilayerOreFeature(BilayerOreFeature.Config.CODEC));
    public static final Supplier<ChineseStylePineTreeFeature> CHINESE_STYLE_PINE_TREE = FEATURES.register("chinese_style_pine_tree", () -> new ChineseStylePineTreeFeature(ChineseStylePineTreeFeature.Config.CODEC));
    public static final Supplier<BranchTreeFeature> BRANCH_TREE = FEATURES.register("branch_tree", () -> new BranchTreeFeature(BranchTreeFeature.Config.CODEC));
    public static final Supplier<MeteoriteFeature> METEORITE = FEATURES.register("meteorite", () -> new MeteoriteFeature(MeteoriteFeature.Config.CODEC));
    public static final Supplier<RailSupportFeature> RAIL_SUPPORT = FEATURES.register("rail_support", () -> new RailSupportFeature(RailSupportFeature.Config.CODEC));
    public static final Supplier<RailTrapFeature> RAIL_TRAP = FEATURES.register("rail_trap", () -> new RailTrapFeature(RailTrapFeature.Config.CODEC));
    public static final Supplier<GroundBlockFeature> GROUND_BLOCK = FEATURES.register("ground_block", () -> new GroundBlockFeature(GroundBlockFeature.Config.CODEC));
    public static final Supplier<GroundBlockNBTFeature> GROUND_BLOCK_NBT = FEATURES.register("ground_block_nbt", () -> new GroundBlockNBTFeature(GroundBlockNBTFeature.Config.CODEC));
    public static final Supplier<GemstoneCaveFeature> GEMSTONE_CAVE = FEATURES.register("gemstone_cave", () -> new GemstoneCaveFeature(GemstoneCaveFeature.Config.CODEC));
    public static final Supplier<DetonatorFeature> DETONATOR_FEATURE = FEATURES.register("detonator", () -> new DetonatorFeature(DetonatorFeature.Config.CODEC));
    public static final Supplier<PlantPatchFeature> PLANT_PATCH = FEATURES.register("plant_patch", () -> new PlantPatchFeature(PlantPatchFeature.Config.CODEC)); // todo 使用

    public static final Supplier<PlacementModifierType<SecretFlagPlacement>> SECRET_FLAG_PLACEMENT_MODIFIER = MODIFIER_TYPES.register("secret_flag", () -> () -> SecretFlagPlacement.CODEC);

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
        LibUtils.devRun(() -> Confluence.LOGGER.warn("Failed to fetch mechanical block entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        return null;
    }

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
        MODIFIER_TYPES.register(eventBus);
    }

    public static class Configured {
        public static final ResourceKey<ConfiguredFeature<?, ?>> SHADOW_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("shadow_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> EBONY_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("ebony_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> PALM_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("palm_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> PEARL_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("pearl_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> RUBY_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("ruby_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> AMBER_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("amber_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> TOPAZ_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("topaz_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> JADE_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("jade_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> DIAMOND_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("diamond_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> SAPPHIRE_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("sapphire_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> AMETHYST_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("amethyst_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> ASH_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("ash_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> YELLOW_WILLOW_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("yellow_willow_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> BAOBAB_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("baobab_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> PINE_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("pine_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> MOONGLOW_WILLOW_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("moonglow_willow_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> CHINESE_PINE_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("chinese_pine_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> GLOWING_MUSHROOM_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("glowing_mushroom_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> LIFE_MUSHROOM_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("life_mushroom_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_LIFE_MUSHROOM_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("huge_life_mushroom_tree"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> VOID_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("void_tree"));
    }

    public static final class TreeGrowers {
        public static final TreeGrower SHADOW_GROWER = registerSmallTree("shadow", Configured.SHADOW_TREE);
        public static final TreeGrower EBONY_GROWER = registerSmallTree("ebony", Configured.EBONY_TREE);
        public static final TreeGrower PALM_GROWER = registerSmallTree("palm", Configured.PALM_TREE);
        public static final TreeGrower PEARL_GROWER = registerSmallTree("pearl", Configured.PEARL_TREE);
        public static final TreeGrower RUBY_GROWER = registerSmallTree("ruby", Configured.RUBY_TREE);
        public static final TreeGrower AMBER_GROWER = registerSmallTree("amber", Configured.AMBER_TREE);
        public static final TreeGrower TOPAZ_GROWER = registerSmallTree("topaz", Configured.TOPAZ_TREE);
        public static final TreeGrower JADE_GROWER = registerSmallTree("jade", Configured.JADE_TREE);
        public static final TreeGrower DIAMOND_GROWER = registerSmallTree("diamond", Configured.DIAMOND_TREE);
        public static final TreeGrower SAPPHIRE_GROWER = registerSmallTree("sapphire", Configured.SAPPHIRE_TREE);
        public static final TreeGrower AMETHYST_GROWER = registerSmallTree("amethyst", Configured.AMETHYST_TREE);
        public static final TreeGrower ASH_GROWER = registerSmallTree("ash", Configured.ASH_TREE);
        public static final TreeGrower PINE_GROWER = registerSmallTree("pine", Configured.PINE_TREE);
        public static final TreeGrower CHINESE_PINE_GROWER = registerSmallTree("chinese_pine", Configured.CHINESE_PINE_TREE);
        public static final TreeGrower YELLOW_WILLOW_GROWER = registerSmallTree("yellow_willow", Configured.YELLOW_WILLOW_TREE);
        public static final TreeGrower BAOBAB_GROWER = registerBigTree("baobab", Configured.BAOBAB_TREE);
        public static final TreeGrower VOID_GROWER = registerSmallTree("void", Configured.VOID_TREE);

        private static TreeGrower registerSmallTree(String name, ResourceKey<ConfiguredFeature<?, ?>> tree) {
            return new TreeGrower(Confluence.asPlainId(name), Optional.empty(), Optional.of(tree), Optional.empty());
        }

        private static TreeGrower registerBigTree(String name, ResourceKey<ConfiguredFeature<?, ?>> tree) {
            return new TreeGrower(Confluence.asPlainId(name), Optional.of(tree), Optional.empty(), Optional.empty());
        }
    }
}
