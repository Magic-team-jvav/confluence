package org.confluence.mod.common.init;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.worldgen.feature.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModFeatures {
    public static final Predicate<BlockState> IS_BASE_STONE = state -> state.is(BlockTags.BASE_STONE_OVERWORLD);
    public static final Predicate<BlockState> IS_REPLACEABLE = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, Confluence.MODID);

    public static final Supplier<BoulderTrapFeature> BOULDER_TRAP = FEATURES.register("boulder_trap", () -> new BoulderTrapFeature(BoulderTrapFeature.Config.CODEC));
    public static final Supplier<DartTrapFeature> DART_TRAP = FEATURES.register("dart_trap", () -> new DartTrapFeature(DartTrapFeature.Config.CODEC));
    public static final Supplier<ThinIcePatchFeature> THIN_ICE_PATCH = FEATURES.register("thin_ice_patch", () -> new ThinIcePatchFeature(ThinIcePatchFeature.Config.CODEC));
    public static final Supplier<DeathChestTrapFeature> DEATH_CHEST_TRAP = FEATURES.register("death_chest_trap", () -> new DeathChestTrapFeature(DeathChestTrapFeature.Config.CODEC));
    public static final Supplier<WithDetonatorFeature> WITH_DETONATOR = FEATURES.register("with_detonator", () -> new WithDetonatorFeature(WithDetonatorFeature.Config.CODEC));
    public static final Supplier<FallingSandTrapFeature> FALLING_SAND_TRAP = FEATURES.register("falling_sand_trap", () -> new FallingSandTrapFeature(FallingSandTrapFeature.Config.CODEC));

    public static final Supplier<JewelryTreeFeature> JEWELRY_TREE = FEATURES.register("jewelry_tree", () -> new JewelryTreeFeature(JewelryTreeFeature.Config.CODEC));
    public static final Supplier<SimpleBlockNBTFeature> SIMPLE_BLOCK_NBT = FEATURES.register("simple_block_nbt", () -> new SimpleBlockNBTFeature(SimpleBlockNBTFeature.Config.CODEC));
    public static final Supplier<PalmTreeFeature> PALM_TREE = FEATURES.register("palm_tree", () -> new PalmTreeFeature(PalmTreeFeature.Config.CODEC));
    public static final Supplier<LivingTreeFeature> LIVING_TREE = FEATURES.register("living_tree", () -> new LivingTreeFeature(LivingTreeFeature.Config.CODEC));
    public static final Supplier<QueenBeeHiveFeature> QUEEN_BEE_HIVE = FEATURES.register("queen_bee_hive", () -> new QueenBeeHiveFeature(QueenBeeHiveFeature.Config.CODEC));
    public static final Supplier<YellowWillowFeature> YELLOW_WILLOW = FEATURES.register("yellow_willow", () -> new YellowWillowFeature(YellowWillowFeature.Config.CODEC));
    public static final Supplier<MeteoriteFeature> METEORITE = FEATURES.register("meteorite", () -> new MeteoriteFeature(MeteoriteFeature.Config.CODEC));

    public static final ResourceKey<ConfiguredFeature<?, ?>> SHADOW = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("trees_set/trees_tr_crimson"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> EBONY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("trees_set/trees_corruption"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALM = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("palm_tree_checked"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> PEARL = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("trees_set/trees_hallow"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("ruby_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> AMBER = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("amber_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> TOPAZ = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("topaz_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> EMERALD = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("emerald_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> DIAMOND = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("diamond_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAPPHIRE = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("sapphire_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> TR_AMETHYST = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("tr_amethyst_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASH = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("ash_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> LIVING = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("living_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_YELLOW_WILLOW = ResourceKey.create(Registries.CONFIGURED_FEATURE, Confluence.asResource("yellow_willow"));

    public static @NotNull BlockState getPressurePlate(WorldGenLevel level, BlockPos supportPos) {
        return level.isStateAtPosition(supportPos, blockState -> blockState.is(Blocks.DEEPSLATE))
                ? FunctionalBlocks.DEEPSLATE_PRESSURE_PLATE.get().defaultBlockState()
                : FunctionalBlocks.STONE_PRESSURE_PLATE.get().defaultBlockState();
    }

    public static @Nullable INetworkEntity getNetworkEntity(WorldGenLevel level, BlockPos blockPos) {
        if (level.getBlockEntity(blockPos) instanceof INetworkEntity entity) {
            return entity;
        }
        Confluence.LOGGER.error("Failed to fetch mechanical block entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return null;
    }

    public static @Nullable BlockEntity getBlockEntity(WorldGenLevel level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity == null) {
            Confluence.LOGGER.error("Failed to fetch block entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ());
            return null;
        }
        return blockEntity;
    }

    public static boolean safeSetBlock(WorldGenLevel level, BlockPos pos, BlockState state, Predicate<BlockState> oldState) {
        if (oldState.test(level.getBlockState(pos))) {
            return level.setBlock(pos, state, 3);
        }
        return false;
    }

    public static boolean isPosAir(WorldGenLevel level, BlockPos blockPos) {
        return level.isStateAtPosition(blockPos, BlockBehaviour.BlockStateBase::isAir);
    }

    public static boolean isPosSturdy(WorldGenLevel level, BlockPos blockPos, Direction face) {
        return level.isStateAtPosition(blockPos, blockState -> blockState.isFaceSturdy(level, blockPos, face));
    }

    public static final class TreeGrowers {
        public static final TreeGrower SHADOW_GROWER = register("shadow", SHADOW);
        public static final TreeGrower EBONY_GROWER = register("ebony", EBONY);
        public static final TreeGrower PALM_GROWER = register("palm", PALM);
        public static final TreeGrower PEARL_GROWER = register("pearl", PEARL);
        public static final TreeGrower RUBY_GROWER = register("ruby", RUBY);
        public static final TreeGrower AMBER_GROWER = register("amber", AMBER);
        public static final TreeGrower TOPAZ_GROWER = register("topaz", TOPAZ);
        public static final TreeGrower EMERALD_GROWER = register("emerald", EMERALD);
        public static final TreeGrower DIAMOND_GROWER = register("diamond", DIAMOND);
        public static final TreeGrower SAPPHIRE_GROWER = register("sapphire", SAPPHIRE);
        public static final TreeGrower TR_AMETHYST_GROWER = register("tr_amethyst", TR_AMETHYST);
        public static final TreeGrower ASH_GROWER = register("ash", ASH);
        public static final TreeGrower LIVING_GROWER = register("living", LIVING);
        public static final TreeGrower YELLOW_WILLOW_GROWER = register("yellow_willow_grower", CONFIGURED_YELLOW_WILLOW);

        private static TreeGrower register(String name, ResourceKey<ConfiguredFeature<?, ?>> tree) {
            return new TreeGrower(Confluence.MODID + ":" + name, Optional.empty(), Optional.of(tree), Optional.empty());
        }
    }
}
