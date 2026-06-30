package org.confluence.mod.common.init;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.block.natural.SimpleMegaTreeGrower;
import org.confluence.mod.common.block.natural.SimpleTreeGrower;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.worldgen.SecretFlagPlacement;
import org.confluence.mod.common.worldgen.feature.*;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public final class ModFeatures {
    public static final Predicate<BlockState> IS_BASE_STONE = state -> state.is(BlockTags.BASE_STONE_OVERWORLD);
    public static final Predicate<BlockState> IS_REPLACEABLE = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Confluence.MODID);
    public static final DeferredRegister<PlacementModifierType<?>> MODIFIER_TYPES = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, Confluence.MODID);

    public static final RegistryObject<BoulderTrapFeature> BOULDER_TRAP = FEATURES.register("boulder_trap", () -> new BoulderTrapFeature(BoulderTrapFeature.Config.CODEC));
    public static final RegistryObject<DartTrapFeature> DART_TRAP = FEATURES.register("dart_trap", () -> new DartTrapFeature(DartTrapFeature.Config.CODEC));
    public static final RegistryObject<ColumnPatchFeature> COLUMN_PATCH = FEATURES.register("column_patch", () -> new ColumnPatchFeature(ColumnPatchFeature.Config.CODEC));
    public static final RegistryObject<DeathChestTrapFeature> DEATH_CHEST_TRAP = FEATURES.register("death_chest_trap", () -> new DeathChestTrapFeature(DeathChestTrapFeature.Config.CODEC));
    public static final RegistryObject<FallingSandTrapFeature> FALLING_SAND_TRAP = FEATURES.register("falling_sand_trap", () -> new FallingSandTrapFeature(FallingSandTrapFeature.Config.CODEC));
    public static final RegistryObject<SculkSensorWithTNTFeature> SCULK_SENSOR_WITH_TNT = FEATURES.register("sculk_sensor_with_tnt", () -> new SculkSensorWithTNTFeature(SculkSensorWithTNTFeature.Config.CODEC));

    public static final RegistryObject<SimpleBlockNBTFeature> SIMPLE_BLOCK_NBT = FEATURES.register("simple_block_nbt", () -> new SimpleBlockNBTFeature(SimpleBlockNBTFeature.Config.CODEC));
    public static final RegistryObject<PalmTreeFeature> PALM_TREE = FEATURES.register("palm_tree", () -> new PalmTreeFeature(PalmTreeFeature.Config.CODEC));
    public static final RegistryObject<DroopingVineTreeFeature> DROOPING_VINE_TREE = FEATURES.register("drooping_vine_tree", () -> new DroopingVineTreeFeature(DroopingVineTreeFeature.Config.CODEC));
    public static final RegistryObject<BlockPostFeature> BLOCK_POST = FEATURES.register("block_post", () -> new BlockPostFeature(BlockPostFeature.Config.CODEC));
    public static final RegistryObject<CattailsFeature> CATTAILS = FEATURES.register("cattails", () -> new CattailsFeature(CattailsFeature.Config.CODEC));
    public static final RegistryObject<MushroomTreeFeature> MUSHROOM_TREE = FEATURES.register("mushroom_tree", () -> new MushroomTreeFeature(MushroomTreeFeature.Config.CODEC));
    public static final RegistryObject<HugeMushroomTreeFeature> HUGE_MUSHROOM_TREE = FEATURES.register("huge_mushroom_tree", () -> new HugeMushroomTreeFeature(HugeMushroomTreeFeature.Config.CODEC));
    public static final RegistryObject<BaobabTreeFeature> BAOBAB_TREE = FEATURES.register("baobab_tree", () -> new BaobabTreeFeature(BaobabTreeFeature.Config.CODEC));
    public static final RegistryObject<PineTreeFeature> PINE_TREE = FEATURES.register("pine_tree", () -> new PineTreeFeature(PineTreeFeature.Config.CODEC));
    public static final RegistryObject<MoonglowWillowTreeFeature> MOONGLOW_WILLOW_TREE = FEATURES.register("moonglow_willow_tree", () -> new MoonglowWillowTreeFeature(MoonglowWillowTreeFeature.Config.CODEC));
    public static final RegistryObject<LunarCoralFeature> LUNAR_CORAL = FEATURES.register("lunar_coral", () -> new LunarCoralFeature(LunarCoralFeature.Config.CODEC));
    public static final RegistryObject<VoidTreeFeature> VOID_TREE = FEATURES.register("void_tree", () -> new VoidTreeFeature(VoidTreeFeature.Config.CODEC));
    public static final RegistryObject<BrokenStoneFeature> BROKEN_STONE = FEATURES.register("broken_stone", () -> new BrokenStoneFeature(BrokenStoneFeature.Config.CODEC));
    public static final RegistryObject<HugeStoneFeature> HUGE_STONE = FEATURES.register("huge_stone", () -> new HugeStoneFeature(HugeStoneFeature.Config.CODEC));
    public static final RegistryObject<BilayerOreFeature> BILAYER_ORE = FEATURES.register("bilayer_ore", () -> new BilayerOreFeature(BilayerOreFeature.Config.CODEC));
    public static final RegistryObject<ChineseStylePineTreeFeature> CHINESE_STYLE_PINE_TREE = FEATURES.register("chinese_style_pine_tree", () -> new ChineseStylePineTreeFeature(ChineseStylePineTreeFeature.Config.CODEC));
    public static final RegistryObject<BranchTreeFeature> BRANCH_TREE = FEATURES.register("branch_tree", () -> new BranchTreeFeature(BranchTreeFeature.Config.CODEC));
    public static final RegistryObject<MeteoriteFeature> METEORITE = FEATURES.register("meteorite", () -> new MeteoriteFeature(MeteoriteFeature.Config.CODEC));
    public static final RegistryObject<RailSupportFeature> RAIL_SUPPORT = FEATURES.register("rail_support", () -> new RailSupportFeature(RailSupportFeature.Config.CODEC));
    public static final RegistryObject<RailTrapFeature> RAIL_TRAP = FEATURES.register("rail_trap", () -> new RailTrapFeature(RailTrapFeature.Config.CODEC));
    public static final RegistryObject<GroundBlockFeature> GROUND_BLOCK = FEATURES.register("ground_block", () -> new GroundBlockFeature(GroundBlockFeature.Config.CODEC));
    public static final RegistryObject<GroundBlockNBTFeature> GROUND_BLOCK_NBT = FEATURES.register("ground_block_nbt", () -> new GroundBlockNBTFeature(GroundBlockNBTFeature.Config.CODEC));
    public static final RegistryObject<GemstoneCaveFeature> GEMSTONE_CAVE = FEATURES.register("gemstone_cave", () -> new GemstoneCaveFeature(GemstoneCaveFeature.Config.CODEC));
    public static final RegistryObject<DetonatorFeature> DETONATOR_FEATURE = FEATURES.register("detonator", () -> new DetonatorFeature(DetonatorFeature.Config.CODEC));
    public static final RegistryObject<PlantPatchFeature> PLANT_PATCH = FEATURES.register("plant_patch", () -> new PlantPatchFeature(PlantPatchFeature.Config.CODEC)); // todo 使用

    public static final RegistryObject<PlacementModifierType<SecretFlagPlacement>> SECRET_FLAG_PLACEMENT_MODIFIER = MODIFIER_TYPES.register("secret_flag", () -> () -> SecretFlagPlacement.CODEC);

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
        public static final AbstractTreeGrower SHADOW_GROWER = registerSmallTree(Configured.SHADOW_TREE);
        public static final AbstractTreeGrower EBONY_GROWER = registerSmallTree(Configured.EBONY_TREE);
        public static final AbstractTreeGrower PALM_GROWER = registerSmallTree(Configured.PALM_TREE);
        public static final AbstractTreeGrower PEARL_GROWER = registerSmallTree(Configured.PEARL_TREE);
        public static final AbstractTreeGrower RUBY_GROWER = registerSmallTree(Configured.RUBY_TREE);
        public static final AbstractTreeGrower AMBER_GROWER = registerSmallTree(Configured.AMBER_TREE);
        public static final AbstractTreeGrower TOPAZ_GROWER = registerSmallTree(Configured.TOPAZ_TREE);
        public static final AbstractTreeGrower JADE_GROWER = registerSmallTree(Configured.JADE_TREE);
        public static final AbstractTreeGrower DIAMOND_GROWER = registerSmallTree(Configured.DIAMOND_TREE);
        public static final AbstractTreeGrower SAPPHIRE_GROWER = registerSmallTree(Configured.SAPPHIRE_TREE);
        public static final AbstractTreeGrower AMETHYST_GROWER = registerSmallTree(Configured.AMETHYST_TREE);
        public static final AbstractTreeGrower ASH_GROWER = registerSmallTree(Configured.ASH_TREE);
        public static final AbstractTreeGrower PINE_GROWER = registerSmallTree(Configured.PINE_TREE);
        public static final AbstractTreeGrower CHINESE_PINE_GROWER = registerSmallTree(Configured.CHINESE_PINE_TREE);
        public static final AbstractTreeGrower YELLOW_WILLOW_GROWER = registerSmallTree(Configured.YELLOW_WILLOW_TREE);
        public static final AbstractMegaTreeGrower BAOBAB_GROWER = registerBigTree(Configured.BAOBAB_TREE);
        public static final AbstractTreeGrower VOID_GROWER = registerSmallTree(Configured.VOID_TREE);

        private static AbstractTreeGrower registerSmallTree(ResourceKey<ConfiguredFeature<?, ?>> tree) {
            return new SimpleTreeGrower(tree);
        }

        private static AbstractMegaTreeGrower registerBigTree(ResourceKey<ConfiguredFeature<?, ?>> tree) {
            return new SimpleMegaTreeGrower(tree);
        }
    }
}
