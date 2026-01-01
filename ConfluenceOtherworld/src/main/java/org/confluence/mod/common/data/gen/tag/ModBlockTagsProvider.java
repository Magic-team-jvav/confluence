package org.confluence.mod.common.data.gen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.common.block.common.TombstoneBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.terraentity.init.TETags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static org.confluence.mod.Confluence.MODID;
import static org.confluence.mod.common.init.block.ChestBlocks.*;
import static org.confluence.mod.common.init.block.DecorativeBlocks.*;
import static org.confluence.mod.common.init.block.FunctionalBlocks.*;
import static org.confluence.mod.common.init.block.ModBlocks.*;
import static org.confluence.mod.common.init.block.NatureBlocks.*;
import static org.confluence.mod.common.init.block.OreBlocks.*;

@SuppressWarnings("all")
public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        LogBlockSet.acceptTags(this::tag);
        IntrinsicTagAppender<Block> mineableWithPickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        IntrinsicTagAppender<Block> mineableWithHammer = tag(ModTags.Blocks.MINEABLE_WITH_HAMMER);
        OreBlocks.acceptTag(mineableWithPickaxe);
        OreBlocks.acceptTag(tag(Tags.Blocks.ORES));
        StatueBlocks.acceptTag(mineableWithPickaxe);
        StatueBlocks.acceptTag(mineableWithHammer);
        tag(ModTags.Blocks.JEWELLERY_BRANCHES_ATTACHABLE).add(STONY_LOG.get());
        tag(ModTags.Blocks.ASH_LOG_BRANCHES_ATTACHABLE).add(ASH_LOG_BLOCKS.LOG.get());
        tag(ModTags.Blocks.OPAL_ORE_REPLACEMENT).add(DIATOMACEOUS.get());
        tag(ModTags.Blocks.DESERT_FOSSIL_REPLACEMENT).add(HARDENED_SAND_BLOCK.get(), HARDENED_RED_SAND_BLOCK.get());
        tag(ModTags.Blocks.SLUSH_REPLACEMENT).add(Blocks.PACKED_ICE, Blocks.SNOW_BLOCK);
        tag(ModTags.Blocks.MARINE_GRAVEL_REPLACEMENT).add(
                Blocks.GRAVEL,
                Blocks.STONE,
                Blocks.DEEPSLATE,
                HARDENED_SAND_BLOCK.get(),
                DIATOMACEOUS.get()
        );
        tag(ModTags.Blocks.COLD_CRYSTAL_ORE_REPLACEMENT).add(Blocks.PACKED_ICE, Blocks.SNOW_BLOCK);
        tag(ModTags.Blocks.GELSTONE_ORE_REPLACEMENT).add(Blocks.MUD, Blocks.CLAY, Blocks.STONE);
        tag(BlockTags.ANVIL).add(LEAD_ANVIL.get(), CHIPPED_LEAD_ANVIL.get(), DAMAGED_LEAD_ANVIL.get());
        tag(ModTags.Blocks.VINES).add(
                Blocks.VINE,
                Blocks.WEEPING_VINES,
                Blocks.WEEPING_VINES_PLANT,
                Blocks.TWISTING_VINES,
                Blocks.TWISTING_VINES_PLANT,
                Blocks.CAVE_VINES,
                Blocks.CAVE_VINES_PLANT,
                GLOWING_MUSHROOM_VINE.get(),
                FOREST_DROOPING_VINE.get(),
                JUNGLE_DROOPING_VINE.get(),
                CORRUPT_DROOPING_VINE.get(),
                CRIMSON_DROOPING_VINE.get(),
                HALLOW_DROOPING_VINE.get()
        );
        tag(ModTags.Blocks.EASY_CRASH).add(
                THIN_ICE_BLOCK.get(),
                SWORD_IN_STONE.get(),
                CRACKED_BLUE_BRICKS.get(),
                CRACKED_GREEN_BRICKS.get(),
                CRACKED_PINK_BRICKS.get(),
                CRISPY_HONEY_BLOCK.get()
        );
        tag(BlockTags.RAILS).add(EVER_POWERED_RAIL.get());

        tag(BlockTags.DIRT).add(
                CORRUPT_GRASS_BLOCK.get(),
                ASH_BLOCK.get(),
                CRIMSON_GRASS_BLOCK.get(),
                HALLOW_GRASS_BLOCK.get(),
                ASH_GRASS_BLOCK.get(),
                MUSHROOM_GRASS_BLOCK.get(),
                JUNGLE_GRASS_BLOCK.get()
        );
        tag(BlockTags.BEACON_BASE_BLOCKS).add(
                LEAD_BLOCK.get(),
                SILVER_BLOCK.get(),
                TUNGSTEN_BLOCK.get(),
                PLATINUM_BLOCK.get(),
                DEMONITE_BLOCK.get(),
                CRIMTANE_BLOCK.get(),
                HELLSTONE_BLOCK.get(),
                COBALT_BLOCK.get(),
                PALLADIUM_BLOCK.get(),
                ORICHALCUM_BLOCK.get(),
                ADAMANTITE_BLOCK.get(),
                TITANIUM_BLOCK.get(),
                HALLOWED_BLOCK.get(),
                CHLOROPHYTE_BLOCK.get(),
                SHROOMITE_BLOCK.get(),
                SPECTRE_BLOCK.get(),
                LUMINITE_BLOCK.get(),
                AMBER_BLOCK.get(),
                AMETHYST_BLOCK.get(),
                JADE_BLOCK.get(),
                RUBY_BLOCK.get(),
                SAPPHIRE_BLOCK.get(),
                TOPAZ_BLOCK.get(),
                OPAL_BLOCK.get(),
                GELSTONE_BLOCK.get(),
                STURDY_FOSSIL_BLOCK.get(),
                COLD_CRYSTAL_BLOCK.get()
        );
        tag(BlockTags.SCULK_REPLACEABLE).add(
                CORRUPT_GRASS_BLOCK.get(),
                ASH_BLOCK.get(),
                CRIMSON_GRASS_BLOCK.get(),
                HALLOW_GRASS_BLOCK.get(),
                ASH_GRASS_BLOCK.get(),
                MUSHROOM_GRASS_BLOCK.get(),
                JUNGLE_GRASS_BLOCK.get()
        );
        tag(BlockTags.SAND).add(CRIMSAND.get(), EBONSAND.get(), PEARLSAND.get());
        tag((BlockTags.ICE)).add(
                RED_ICE.get(),
                RED_PACKED_ICE.get(),
                PINK_ICE.get(),
                PINK_PACKED_ICE.get(),
                PURPLE_ICE.get(),
                PURPLE_PACKED_ICE.get()
        );
        tag(BlockTags.LOGS).add(OAK_LOG_BOULDER.get());

        tag(BlockTags.LEAVES).add(YELLOW_WILLOW_DROOPING_LEAVES.get());
        tag(ModTags.Blocks.DROOPING_VINE_CAN_SURVIVE).addTag(BlockTags.LEAVES);
        tag(TETags.Blocks.HONEY).add(HONEY.get());

        tag(BlockTags.OVERWORLD_NATURAL_LOGS).add(
                EBONY_LOG_BLOCKS.LOG.get(),
                SHADOW_LOG_BLOCKS.LOG.get(),
                PALM_LOG_BLOCKS.LOG.get(),
                PEARL_LOG_BLOCKS.LOG.get(),
                YELLOW_WILLOW_LOG_BLOCKS.LOG.get(),
                LIVING_LOG_BLOCKS.LOG.get(),
                LIVING_MAHOGANY_LOG_BLOCKS.LOG.get(),
                BAOBAB_LOG_BLOCKS.LOG.get()
        );
        mineableWithPickaxe.add(
                RUBY_BLOCK.get(),
                AMBER_BLOCK.get(),
                TOPAZ_BLOCK.get(),
                SAPPHIRE_BLOCK.get(),
                AMETHYST_BLOCK.get(),
                JADE_BLOCK.get(),
                SNOW_BRICKS.get(),
                SNOW_BRICKS_STAIRS.get(),
                SNOW_BRICKS_SLAB.get(),
                BLUE_ICE_BRICKS.get(),
                BLUE_ICE_BRICKS_STAIRS.get(),
                BLUE_ICE_BRICKS_SLAB.get(),
                PACKED_ICE_BRICKS.get(),
                PACKED_ICE_BRICKS_STAIRS.get(),
                PACKED_ICE_BRICKS_SLAB.get(),
                COPPER_BRICKS.get(),
                COPPER_BRICKS_STAIRS.get(),
                COPPER_BRICKS_SLAB.get(),
                TIN_BRICKS.get(),
                TIN_BRICKS_STAIRS.get(),
                TIN_BRICKS_SLAB.get(),
                IRON_BRICKS.get(),
                IRON_BRICKS_STAIRS.get(),
                IRON_BRICKS_SLAB.get(),
                LEAD_BRICKS.get(),
                LEAD_BRICKS_STAIRS.get(),
                LEAD_BRICKS_SLAB.get(),
                SILVER_BRICKS.get(),
                SILVER_BRICKS_STAIRS.get(),
                SILVER_BRICKS_SLAB.get(),
                TUNGSTEN_BRICKS.get(),
                TUNGSTEN_BRICKS_STAIRS.get(),
                TUNGSTEN_BRICKS_SLAB.get(),
                GOLDEN_BRICKS.get(),
                GOLDEN_BRICKS_STAIRS.get(),
                GOLDEN_BRICKS_SLAB.get(),
                PLATINUM_BRICKS.get(),
                PLATINUM_BRICKS_STAIRS.get(),
                PLATINUM_BRICKS_SLAB.get(),
                DEMONITE_ORE_BRICKS.get(),
                DEMONITE_ORE_BRICKS_STAIRS.get(),
                DEMONITE_ORE_BRICKS_SLAB.get(),
                EBONSTONE_BRICKS.get(),
                EBONSTONE_BRICKS_STAIRS.get(),
                EBONSTONE_BRICKS_SLAB.get(),
                METEORITE_BRICKS.get(),
                METEORITE_BRICKS_STAIRS.get(),
                METEORITE_BRICKS_SLAB.get(),
                CRIMTANE_ORE_BRICKS.get(),
                CRIMTANE_ORE_BRICKS_STAIRS.get(),
                CRIMTANE_ORE_BRICKS_SLAB.get(),
                CRIMSTONE_BRICKS.get(),
                CRIMSTONE_BRICKS_STAIRS.get(),
                CRIMSTONE_BRICKS_SLAB.get(),
                PEARLSTONE_BRICKS.get(),
                PEARLSTONE_BRICKS_STAIRS.get(),
                PEARLSTONE_BRICKS_SLAB.get(),
                GREEN_CANDY_BLOCK.get(),
                RED_CANDY_BLOCK.get(),
                SUN_PLATE.get(),
                SUN_PLATE_SLAB.get(),
                SUN_PLATE_STAIRS.get(),
                SKYWARE_DOOR.get(),
                SKYWARE_GLASS_DOOR.get(),
                DISC_BLOCK.get(),
                OBSIDIAN_BRICKS.get(),
                MECHANICAL_FRAGILE_OBSIDIAN_BRICKS.get(),
                OBSIDIAN_BRICKS_SLAB.get(),
                OBSIDIAN_BRICKS_STAIRS.get(),
                OBSIDIAN_BRICKS_DOOR.get(),
                OBSIDIAN_SMALL_BRICKS.get(),
                SMOOTH_OBSIDIAN.get(),
                POLISHED_GRANITE.get(),
                GRANITE_COLUMN.get(),
                GRANITE_BRICKS.get(),
                CHISELED_OBSIDIAN_BRICKS.get(),

                MARBLE_COLUMN.get(),
                MARBLE_BRICKS.get(),
                MARBLE_SMALL_BRICKS.get(),
                CRACKED_MARBLE_BRICKS.get(),
                GILDED_MARBLE.get(),
                POLISHED_MARBLE.get(),

                CRYSTAL_BLOCK.get(),
                BLUE_BRICKS.get(),
                GREEN_BRICKS.get(),
                PINK_BRICKS.get(),
                BLUE_BRICK_COLUMN.get(),
                GREEN_BRICK_COLUMN.get(),
                PINK_BRICK_COLUMN.get(),
                CHISELED_BLUE_BRICKS.get(),
                CHISELED_GREEN_BRICKS.get(),
                CHISELED_PINK_BRICKS.get(),
                BLUE_BRICK_STAIRS.get(),
                GREEN_BRICK_STAIRS.get(),
                PINK_BRICK_STAIRS.get(),
                BLUE_BRICK_SLAB.get(),
                GREEN_BRICK_SLAB.get(),
                PINK_BRICK_SLAB.get(),
                RUBY_CHAIN.get(),
                AMBER_CHAIN.get(),
                TOPAZ_CHAIN.get(),
                JADE_CHAIN.get(),
                SAPPHIRE_CHAIN.get(),
                DIAMOND_CHAIN.get(),
                AMETHYST_CHAIN.get(),
                SILK_CHAIN.get(),
                BONE_CHAIN.get(),
                LIFE_CRYSTAL_BLOCK.get(),
                COBBLED_EBONSTONE.get(),
                COBBLED_CRIMSTONE.get(),
                COBBLED_PEARLSTONE.get(),
                HARDENED_SAND_BLOCK.get(),
                HARDENED_RED_SAND_BLOCK.get(),
                HARDENED_EBONSAND_BLOCK.get(),
                HARDENED_CRIMSAND_BLOCK.get(),
                HARDENED_PEARLSAND_BLOCK.get(),
                EBONSTONE.get(),
                EBONSANDSTONE.get(),
                CRIMSTONE.get(),
                CRIMSANDSTONE.get(),
                PEARLSTONE.get(),
                PEARLSANDSTONE.get(),
                DESERT_FOSSIL.get(),
                EXTRACTINATOR.get(),
                SKY_MILL.get(),
                COOKING_POT.get(),
                HELLFORGE.get(),
                ALCHEMY_TABLE.get(),
                WEATHER_VANE.get(),
                SOLIDIFIER.get(),
                CAULDRON.get(),
                CHIPPED_LEAD_ANVIL.get(),
                LEAD_ANVIL.get(),
                DAMAGED_LEAD_ANVIL.get(),
                HEAVY_WORK_BENCH.get(),
                DART_TRAP.get(),
                STONE_DART_TRAP.get(),
                DEEPSLATE_DART_TRAP.get(),
                SHIMMER_TRAP.get(),
                GRAVITATION_TRAP.get(),
                PNEUMATIC_TRAP.get(),
                STONY_LOG.get(),
                STONE_PRESSURE_PLATE.get(),
                DEEPSLATE_PRESSURE_PLATE.get(),
                SIGNAL_ADAPTER.get(),
                SWITCH.get(),
                TIMERS_BLOCK_1_1.get(),
                TIMERS_BLOCK_3_1.get(),
                TIMERS_BLOCK_5_1.get(),
                TIMERS_BLOCK_1_2.get(),
                TIMERS_BLOCK_1_4.get(),
                DETONATOR.get(),
                EVER_POWERED_RAIL.get(),
                JUNGLE_HIVE_BLOCK.get(),
                THIN_ICE_BLOCK.get(),
                WINTER_MARROW_BLOCK.get(),
                SAFE.get(),
                ANNOUNCEMENT_BOX.get(),
                WALL_ANNOUNCEMENT_BOX.get(),
                AETHERIUM_BRICKS.get(),
                REMAINS_BLOCK.get(),
                SPIKE.get(),

                TOMBSTONE.get(),
                GRAVE_MARKER.get(),
                CROSS_GRAVE_MARKER.get(),
                HEADSTONE.get(),
                GRAVESTONE.get(),
                OBELISK.get(),
                GOLDEN_TOMBSTONE.get(),
                GOLDEN_GRAVE_MARKER.get(),
                GOLDEN_CROSS_GRAVE_MARKER.get(),
                GOLDEN_HEADSTONE.get(),
                GOLDEN_GRAVESTONE.get(),
                AETHERIUM_BLOCK.get(),
                DARK_AETHERIUM_BLOCK.get(),
                CRISPY_HONEY_BLOCK.get(),

                GOLDEN_CHEST.get(),
                DEATH_GOLDEN_CHEST.get(),
                SHADOW_CHEST.get(),
                FROZEN_CHEST.get(),
                IVY_CHEST.get(),
                WATER_CHEST.get(),
                SKYWARE_CHEST.get(),
                JUNGLE_CHEST.get(),
                CORRUPTION_CHEST.get(),
                CRIMSON_CHEST.get(),
                HALLOWED_CHEST.get(),
                ICE_CHEST.get(),
                DESERT_CHEST.get(),
                OCEAN_CHEST.get(),
                UNIVERSE_CHEST.get(),
                MECHANIC_SAFE_CHEST.get(),

                ASPHALT_BLOCK.get(),

                GRANITE.get(),
                MARBLE.get(),

                TUFF_BOOTH.get(),
                HEART_LANTERN.get(),
                STAR_IN_A_BOTTLE.get(),
                SOUL_OF_FLIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_LIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_FRIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_NIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_MIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_SIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_BRIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_VOIGHT_IN_A_BOTTLE.get(),

                CrateBlocks.IRON_CRATE.get(),
                CrateBlocks.GOLDEN_CRATE.get(),
                CrateBlocks.SKY_CRATE.get(),
                CrateBlocks.CORRUPT_CRATE.get(),
                CrateBlocks.CRIMSON_CRATE.get(),
                CrateBlocks.HALLOWED_CRATE.get(),
                CrateBlocks.DUNGEON_CRATE.get(),
                CrateBlocks.OASIS_CRATE.get(),
                CrateBlocks.OBSIDIAN_CRATE.get(),
                CrateBlocks.OCEAN_CRATE.get(),

                CrateBlocks.MYTHRIL_CRATE.get(),
                CrateBlocks.TITANIUM_CRATE.get(),
                CrateBlocks.AZURE_CRATE.get(),
                CrateBlocks.DEFILED_CRATE.get(),
                CrateBlocks.HEMATIC_CRATE.get(),
                CrateBlocks.DIVINE_CRATE.get(),
                CrateBlocks.STOCKADE_CRATE.get(),
                CrateBlocks.MIRAGE_CRATE.get(),
                CrateBlocks.HELLSTONE_CRATE.get(),
                CrateBlocks.SEASIDE_CRATE.get(),

                OPAL_BLOCK.get(),
                GELSTONE_BLOCK.get(),
                STURDY_FOSSIL_BLOCK.get(),
                COLD_CRYSTAL_BLOCK.get(),
                CRYSTAL_SHARDS.get(),
                GELATIN_CRYSTAL.get(),

                AMMO_BOX.get(),
                BEWITCHING_TABLE.get(),
                SHARPENING_STATION.get(),
                SILLY_BALLOON_MACHINE.get(),
                PIGGY_BANK.get(),
                MYTHRIL_ANVIL.get(),
                ORICHALCUM_ANVIL.get(),
                TITANIUM_FORGE.get(),
                ADAMANTITE_FORGE.get(),

                SANDSTONE_CHEST.get(),

                KING_SLIME_RELIC.get(),
                EYE_OF_CTHULHU_RELIC.get(),
                BRAIN_OF_CTHULHU_RELIC.get(),
                EATER_OF_WORLDS_RELIC.get(),
                QUEEN_BEE_RELIC.get(),
                DEERCLOPS_RELIC.get(),
                SKELETRON_RELIC.get(),
                WALL_OF_FLESH_RELIC.get(),
                HILL_OF_FLESH_RELIC.get()
        );

        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> mineableWithShovel = tag(BlockTags.MINEABLE_WITH_SHOVEL);
        mineableWithShovel.add(
                SLUSH.get(),
                SILT_BLOCK.get(),
                MARINE_GRAVEL.get(),
                DIATOMACEOUS.get(),
                EBONSAND.get(),
                PEARLSAND.get(),
                CRIMSAND.get(),
                EBONSAND_LAYER_BLOCK.get(),
                PEARLSAND_LAYER_BLOCK.get(),
                CRIMSAND_LAYER_BLOCK.get(),
                SAND_LAYER_BLOCK.get(),
                RED_SAND_LAYER_BLOCK.get(),
                ASH_BLOCK.get(),
                MUSHROOM_GRASS_BLOCK.get(),
                JUNGLE_GRASS_BLOCK.get(),
                CORRUPT_GRASS_BLOCK.get(),
                HALLOW_GRASS_BLOCK.get(),
                CRIMSON_GRASS_BLOCK.get(),
                CORRUPT_JUNGLE_GRASS_BLOCK.get(),
                CRIMSON_JUNGLE_GRASS_BLOCK.get(),
                ASH_GRASS_BLOCK.get(),
                MOISTENED_SAND_BLOCK.get(),
                MOISTENED_RED_SAND_BLOCK.get(),
                MOISTENED_CRIMSAND_BLOCK.get(),
                MOISTENED_PEARLSAND_BLOCK.get(),
                MOISTENED_EBONSAND_BLOCK.get(),
                ASH_PATH.get(),
                JUNGLE_PATH.get(),
                MUSHROOM_PATH.get(),
                POO.get(),
                POO_BLOCK.get()
        );

        IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block> mineableWithHoe = tag(BlockTags.MINEABLE_WITH_HOE);
        mineableWithHoe.add(FLOATING_WHEAT_BALE.get(), SCULK_TRAP.get());
        CrateBlocks.BLOCKS.getEntries().forEach(block -> mineableWithHoe.add(block.get()));

        tag(BlockTags.PLANKS).add(
                CHISELED_OAK_PLANKS.get(),
                CHISELED_SPRUCE_PLANKS.get()
        );

        tag(BlockTags.MINEABLE_WITH_AXE).add(
                WOOD_STONE_SLATTED_BLOCKS.get(),
                DEATH_WOODEN_CHEST.get(),
                LIVING_WOOD_CHEST.get(),
                DUNGEON_CHEST.get(),
                SPORE_ROOT_BLOCK.get(),

                TRADITIONAL_DYNASTY_DOOR.get(),

                ANNOUNCEMENT_BOX.get(),
                WALL_ANNOUNCEMENT_BOX.get(),
                TREE_HOLES_BLOCK.get(),
                MAGIC_MAIL_BOX.get(),
                KEG.get(),
                SAWMILL.get(),
                LOOM.get(),
                DYE_VAT.get(),
                LIFE_CAMPFIRE.get(),

                CrateBlocks.WOODEN_CRATE.get(),
                CrateBlocks.PEARLWOOD_CRATE.get(),
                CrateBlocks.FROZEN_CRATE.get(),
                CrateBlocks.BOREAL_CRATE.get(),
                CrateBlocks.JUNGLE_CRATE.get(),
                CrateBlocks.BRAMBLE_CRATE.get(),
                CrateBlocks.SAVANNA_CRATE.get(),
                CrateBlocks.WILD_CRATE.get(),

                NatureBlocks.GLOWING_MUSHROOM_INDUSIUM_BLOCK.get(),
                NatureBlocks.GLOWING_MUSHROOM_STEM_BLOCK.get(),
                NatureBlocks.GLOWING_MUSHROOM_PILEUS_BLOCK.get(),
                NatureBlocks.LIFE_MUSHROOM_INDUSIUM_BLOCK.get(),
                NatureBlocks.LIFE_MUSHROOM_STEM_BLOCK.get(),
                NatureBlocks.LIFE_MUSHROOM_PILEUS_BLOCK.get(),

                CORRUPT_CACTUS.get(),
                CRIMSON_CACTUS.get(),
                HALLOW_CACTUS.get()
        );
        tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL).addTag(ModTags.Blocks.NEEDS_2_LEVEL);
        tag(BlockTags.INCORRECT_FOR_GOLD_TOOL).addTag(ModTags.Blocks.NEEDS_2_LEVEL);
        tag(BlockTags.INCORRECT_FOR_STONE_TOOL).addTag(ModTags.Blocks.NEEDS_2_LEVEL);
        tag(BlockTags.INCORRECT_FOR_IRON_TOOL).addTag(ModTags.Blocks.NEEDS_3_LEVEL);
        tag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL).addTag(ModTags.Blocks.NEEDS_4_LEVEL);
        tag(BlockTags.INCORRECT_FOR_NETHERITE_TOOL).addTag(ModTags.Blocks.NEEDS_6_LEVEL);

        tag(ModTags.Blocks.NEEDS_1_LEVEL).addTags(
                Tags.Blocks.STORAGE_BLOCKS_COAL,
                Tags.Blocks.ORES_COAL,

                ModTags.Blocks.STORAGE_BLOCKS_RAW_TIN,
                ModTags.Blocks.STORAGE_BLOCKS_TIN,
                ModTags.Blocks.ORES_TIN,

                Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER,
                Tags.Blocks.STORAGE_BLOCKS_COPPER,
                Tags.Blocks.ORES_COPPER,

                ModTags.Blocks.STORAGE_BLOCKS_RAW_LEAD,
                ModTags.Blocks.STORAGE_BLOCKS_LEAD,
                ModTags.Blocks.ORES_LEAD,

                Tags.Blocks.STORAGE_BLOCKS_RAW_IRON,
                Tags.Blocks.STORAGE_BLOCKS_IRON,
                Tags.Blocks.ORES_IRON,

                ModTags.Blocks.STORAGE_BLOCKS_RAW_SILVER,
                ModTags.Blocks.STORAGE_BLOCKS_SILVER,
                ModTags.Blocks.ORES_SILVER,

                ModTags.Blocks.STORAGE_BLOCKS_RAW_TUNGSTEN,
                ModTags.Blocks.STORAGE_BLOCKS_TUNGSTEN,
                ModTags.Blocks.ORES_TUNGSTEN,

                Tags.Blocks.STORAGE_BLOCKS_DIAMOND,
                Tags.Blocks.ORES_DIAMOND,

                ModTags.Blocks.STORAGE_BLOCKS_RUBY,
                ModTags.Blocks.ORES_RUBY,

                ModTags.Blocks.STORAGE_BLOCKS_AMBER,
                ModTags.Blocks.ORES_AMBER,

                ModTags.Blocks.STORAGE_BLOCKS_TOPAZ,
                ModTags.Blocks.ORES_TOPAZ,

                ModTags.Blocks.STORAGE_BLOCKS_JADE,
                ModTags.Blocks.ORES_JADE,

                ModTags.Blocks.STORAGE_BLOCKS_SAPPHIRE,
                ModTags.Blocks.ORES_SAPPHIRE,

                ModTags.Blocks.STORAGE_BLOCKS_AMETHYST,
                ModTags.Blocks.ORES_AMETHYST,

                ModTags.Blocks.TOMBSTONE,

                ModTags.Blocks.STORAGE_BLOCKS_RAW_PLATINUM,
                ModTags.Blocks.STORAGE_BLOCKS_PLATINUM,
                ModTags.Blocks.ORES_PLATINUM
        ).add(
                SANCTIFICATION_EMERALD_ORE.get(),
                CORRUPTION_EMERALD_ORE.get(),
                FLESHIFICATION_EMERALD_ORE.get(),

                SNOW_BRICKS.get(),
                SNOW_BRICKS_STAIRS.get(),
                SNOW_BRICKS_SLAB.get(),
                BLUE_ICE_BRICKS.get(),
                BLUE_ICE_BRICKS_STAIRS.get(),
                BLUE_ICE_BRICKS_SLAB.get(),
                PACKED_ICE_BRICKS.get(),
                PACKED_ICE_BRICKS_STAIRS.get(),
                PACKED_ICE_BRICKS_SLAB.get(),
                COPPER_BRICKS.get(),
                COPPER_BRICKS_STAIRS.get(),
                COPPER_BRICKS_SLAB.get(),
                TIN_BRICKS.get(),
                TIN_BRICKS_STAIRS.get(),
                TIN_BRICKS_SLAB.get(),
                IRON_BRICKS.get(),
                IRON_BRICKS_STAIRS.get(),
                IRON_BRICKS_SLAB.get(),
                LEAD_BRICKS.get(),
                LEAD_BRICKS_STAIRS.get(),
                LEAD_BRICKS_SLAB.get(),
                SILVER_BRICKS.get(),
                SILVER_BRICKS_STAIRS.get(),
                SILVER_BRICKS_SLAB.get(),
                TUNGSTEN_BRICKS.get(),
                TUNGSTEN_BRICKS_STAIRS.get(),
                TUNGSTEN_BRICKS_SLAB.get(),
                GOLDEN_BRICKS.get(),
                GOLDEN_BRICKS_STAIRS.get(),
                GOLDEN_BRICKS_SLAB.get(),
                PLATINUM_BRICKS.get(),
                PLATINUM_BRICKS_STAIRS.get(),
                PLATINUM_BRICKS_SLAB.get(),
                DEMONITE_ORE_BRICKS.get(),
                DEMONITE_ORE_BRICKS_STAIRS.get(),
                DEMONITE_ORE_BRICKS_SLAB.get(),
                EBONSTONE_BRICKS.get(),
                EBONSTONE_BRICKS_STAIRS.get(),
                EBONSTONE_BRICKS_SLAB.get(),
                METEORITE_BRICKS.get(),
                METEORITE_BRICKS_STAIRS.get(),
                METEORITE_BRICKS_SLAB.get(),
                CRIMTANE_ORE_BRICKS.get(),
                CRIMTANE_ORE_BRICKS_STAIRS.get(),
                CRIMTANE_ORE_BRICKS_SLAB.get(),
                CRIMSTONE_BRICKS.get(),
                CRIMSTONE_BRICKS_STAIRS.get(),
                CRIMSTONE_BRICKS_SLAB.get(),
                PEARLSTONE_BRICKS.get(),
                PEARLSTONE_BRICKS_STAIRS.get(),
                PEARLSTONE_BRICKS_SLAB.get(),
                GREEN_CANDY_BLOCK.get(),
                RED_CANDY_BLOCK.get(),
                SUN_PLATE.get(),
                SUN_PLATE_SLAB.get(),
                SUN_PLATE_STAIRS.get(),
                SKYWARE_DOOR.get(),
                DISC_BLOCK.get(),
                SKYWARE_GLASS_DOOR.get(),
                AETHERIUM_BRICKS.get(),
                POLISHED_GRANITE.get(),
                GRANITE_COLUMN.get(),
                GRANITE_BRICKS.get(),

                MARBLE_COLUMN.get(),
                MARBLE_BRICKS.get(),
                MARBLE_SMALL_BRICKS.get(),
                CRACKED_MARBLE_BRICKS.get(),
                GILDED_MARBLE.get(),
                POLISHED_MARBLE.get(),

                CRYSTAL_BLOCK.get(),
                RUBY_CHAIN.get(),
                AMBER_CHAIN.get(),
                TOPAZ_CHAIN.get(),
                JADE_CHAIN.get(),
                SAPPHIRE_CHAIN.get(),
                DIAMOND_CHAIN.get(),
                AMETHYST_CHAIN.get(),
                SILK_CHAIN.get(),
                BONE_CHAIN.get(),
                COBBLED_EBONSTONE.get(),
                COBBLED_CRIMSTONE.get(),
                COBBLED_PEARLSTONE.get(),
                HARDENED_SAND_BLOCK.get(),
                HARDENED_RED_SAND_BLOCK.get(),
                EXTRACTINATOR.get(),
                DART_TRAP.get(),
                STONE_DART_TRAP.get(),
                DEEPSLATE_DART_TRAP.get(),
                SHIMMER_TRAP.get(),
                GRAVITATION_TRAP.get(),
                PNEUMATIC_TRAP.get(),
                STONY_LOG.get(),
                SIGNAL_ADAPTER.get(),
                SWITCH.get(),
                TIMERS_BLOCK_1_1.get(),
                TIMERS_BLOCK_3_1.get(),
                TIMERS_BLOCK_5_1.get(),
                TIMERS_BLOCK_1_2.get(),
                TIMERS_BLOCK_1_4.get(),
                DETONATOR.get(),

                DESERT_FOSSIL.get(),
                GELSTONE_ORE.get(),
                SPORE_ROOT_BLOCK.get(),
                COLD_CRYSTAL_ORE.get(),
                WINTER_MARROW_BLOCK.get(),
                THIN_ICE_BLOCK.get(),
                CRISPY_HONEY_BLOCK.get(),
                NatureBlocks.GRANITE.get(),
                MARBLE.get(),
                POLISHED_GRANITE.get(),

                SAFE.get(),
                SPIKE.get(),
                ASPHALT_BLOCK.get(),
                REMAINS_BLOCK.get(),
                MYTHRIL_ANVIL.get(),
                ORICHALCUM_ANVIL.get(),
                TITANIUM_FORGE.get(),
                ADAMANTITE_FORGE.get(),

                AETHERIUM_BLOCK.get(),
                DARK_AETHERIUM_BLOCK.get(),
                OPAL_BLOCK.get(),
                GELSTONE_BLOCK.get(),
                STURDY_FOSSIL_BLOCK.get(),
                COLD_CRYSTAL_BLOCK.get(),

                HEART_LANTERN.get(),
                STAR_IN_A_BOTTLE.get(),
                TUFF_BOOTH.get(),
                SOUL_OF_FLIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_LIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_FRIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_NIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_MIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_SIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_BRIGHT_IN_A_BOTTLE.get(),
                SOUL_OF_VOIGHT_IN_A_BOTTLE.get(),

                KING_SLIME_RELIC.get(),
                EYE_OF_CTHULHU_RELIC.get(),
                BRAIN_OF_CTHULHU_RELIC.get(),
                EATER_OF_WORLDS_RELIC.get(),
                QUEEN_BEE_RELIC.get(),
                DEERCLOPS_RELIC.get(),
                SKELETRON_RELIC.get(),
                WALL_OF_FLESH_RELIC.get(),
                HILL_OF_FLESH_RELIC.get()
        );
        tag(ModTags.Blocks.NEEDS_2_LEVEL).addTags(
                ModTags.Blocks.STORAGE_BLOCKS_RAW_METEORITE,
                ModTags.Blocks.STORAGE_BLOCKS_METEORITE,
                ModTags.Blocks.ORES_METEORITE
        );
        tag(ModTags.Blocks.NEEDS_3_LEVEL).addTags(
                ModTags.Blocks.STORAGE_BLOCKS_RAW_DEMONITE,
                ModTags.Blocks.STORAGE_BLOCKS_DEMONITE,
                ModTags.Blocks.ORES_DEMONITE,

                ModTags.Blocks.STORAGE_BLOCKS_RAW_CRIMTANE,
                ModTags.Blocks.STORAGE_BLOCKS_CRIMTANE,
                ModTags.Blocks.ORES_CRIMTANE,

                Tags.Blocks.ORES_NETHERITE_SCRAP,
                Tags.Blocks.STORAGE_BLOCKS_NETHERITE,

                Tags.Blocks.OBSIDIANS
        );
        tag(ModTags.Blocks.NEEDS_4_LEVEL).add(
                HARDENED_EBONSAND_BLOCK.get(),
                HARDENED_CRIMSAND_BLOCK.get(),
                HARDENED_PEARLSAND_BLOCK.get(),
                EBONSTONE.get(),
                EBONSANDSTONE.get(),
                CRIMSTONE.get(),
                CRIMSANDSTONE.get(),
                PEARLSTONE.get(),
                PEARLSANDSTONE.get(),
                HELLSTONE.get(),
                HELLFORGE.get(),
                HELLSTONE_BLOCK.get(),
                RAW_HELLSTONE_BLOCK.get(),
                ASH_HELLSTONE.get(),
                HELLSTONE_BRICKS.get()
        );
        tag(ModTags.Blocks.NEEDS_5_LEVEL).addTags(
                ModTags.Blocks.STORAGE_BLOCKS_RAW_COBALT,
                ModTags.Blocks.STORAGE_BLOCKS_COBALT,
                ModTags.Blocks.ORES_COBALT,

                ModTags.Blocks.STORAGE_BLOCKS_RAW_PALLADIUM,
                ModTags.Blocks.STORAGE_BLOCKS_PALLADIUM,
                ModTags.Blocks.ORES_PALLADIUM
        ).add(
                BLUE_BRICKS.get(),
                GREEN_BRICKS.get(),
                PINK_BRICKS.get(),
                CHISELED_BLUE_BRICKS.get(),
                CHISELED_GREEN_BRICKS.get(),
                CHISELED_PINK_BRICKS.get(),
                BLUE_BRICK_STAIRS.get(),
                GREEN_BRICK_STAIRS.get(),
                PINK_BRICK_STAIRS.get(),
                BLUE_BRICK_SLAB.get(),
                GREEN_BRICK_SLAB.get(),
                PINK_BRICK_SLAB.get(),
                BLUE_BRICK_COLUMN.get(),
                GREEN_BRICK_COLUMN.get(),
                PINK_BRICK_COLUMN.get()
        );
        tag(ModTags.Blocks.NEEDS_6_LEVEL).addTags(
                ModTags.Blocks.STORAGE_BLOCKS_RAW_MYTHRIL,
                ModTags.Blocks.STORAGE_BLOCKS_MYTHRIL,
                ModTags.Blocks.ORES_MYTHRIL,

                ModTags.Blocks.STORAGE_BLOCKS_RAW_ORICHALCUM,
                ModTags.Blocks.STORAGE_BLOCKS_ORICHALCUM,
                ModTags.Blocks.ORES_ORICHALCUM
        );
        tag(ModTags.Blocks.NEEDS_7_LEVEL).addTags(
                ModTags.Blocks.STORAGE_BLOCKS_RAW_ADAMANTITE,
                ModTags.Blocks.STORAGE_BLOCKS_ADAMANTITE,
                ModTags.Blocks.ORES_ADAMANTITE,

                ModTags.Blocks.STORAGE_BLOCKS_RAW_TITANIUM,
                ModTags.Blocks.STORAGE_BLOCKS_TITANIUM,
                ModTags.Blocks.ORES_TITANIUM
        );
        tag(ModTags.Blocks.NEEDS_1_LEVEL).addTags(ModTags.Blocks.NEEDS_2_LEVEL, ModTags.Blocks.NEEDS_3_LEVEL, ModTags.Blocks.NEEDS_4_LEVEL, ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_2_LEVEL).addTags(ModTags.Blocks.NEEDS_3_LEVEL, ModTags.Blocks.NEEDS_4_LEVEL, ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_3_LEVEL).addTags(ModTags.Blocks.NEEDS_4_LEVEL, ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_4_LEVEL).addTags(ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_5_LEVEL).addTags(ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_6_LEVEL).addTags(ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_7_LEVEL).addTags(ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_8_LEVEL).addTags(ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_9_LEVEL).add(LIHZAHRD_DOOR.get(), LIHZAHRD_BRICKS.get(), SUPER_DART_TRAP.get());

        tag(ModTags.Blocks.MINEABLE_WITH_PICKAXE_AXE).addTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE);
        tag(ModTags.Blocks.DROOPING_VINE_CAN_SURVIVE).addTag(
                BlockTags.LEAVES
        ).add(
                MUSHROOM_GRASS_BLOCK.get(),
                JUNGLE_GRASS_BLOCK.get(),
                HALLOW_GRASS_BLOCK.get(),
                CRIMSON_GRASS_BLOCK.get(),
                CORRUPT_GRASS_BLOCK.get(),
                Blocks.MUD,
                Blocks.DIRT,
                Blocks.GRASS_BLOCK,
                Blocks.DIRT_PATH,
                Blocks.COARSE_DIRT,
                Blocks.PODZOL,
                Blocks.ROOTED_DIRT,
                Blocks.MUDDY_MANGROVE_ROOTS,
                HELLSTONE.get(),
                ASH_HELLSTONE.get(),
                RAW_HELLSTONE_BLOCK.get(),
                HELLSTONE_BLOCK.get(),
                ASH_PATH.get(),
                JUNGLE_PATH.get(),
                MUSHROOM_PATH.get()
        );
        tag(ModTags.Blocks.COINS).add(COPPER_COIN.get(), SILVER_COIN.get(), GOLD_COIN.get(), PLATINUM_COIN.get()); // 绿宝石币不要放进去

        tag(ModTags.Blocks.NEEDS_5_LEVEL).add(
                DEEPSLATE_COBALT_ORE.get(),
                RAW_COBALT_BLOCK.get(),
                COBALT_BLOCK.get(),
                DEEPSLATE_PALLADIUM_ORE.get(),
                RAW_PALLADIUM_BLOCK.get(),
                PALLADIUM_BLOCK.get()
        );
        tag(ModTags.Blocks.NEEDS_6_LEVEL).add(
                DEEPSLATE_MYTHRIL_ORE.get(),
                RAW_MYTHRIL_BLOCK.get(),
                MYTHRIL_BLOCK.get(),
                DEEPSLATE_ORICHALCUM_ORE.get(),
                RAW_ORICHALCUM_BLOCK.get(),
                ORICHALCUM_BLOCK.get()
        );
        tag(ModTags.Blocks.NEEDS_7_LEVEL).add(
                DEEPSLATE_ADAMANTITE_ORE.get(),
                RAW_ADAMANTITE_BLOCK.get(),
                ADAMANTITE_BLOCK.get(),
                DEEPSLATE_TITANIUM_ORE.get(),
                RAW_TITANIUM_BLOCK.get(),
                TITANIUM_BLOCK.get()
        );
        tag(ModTags.Blocks.NEEDS_8_LEVEL).add(
                HALLOWED_BLOCK.get(),
                CHLOROPHYTE_ORE.get(),
                RAW_CHLOROPHYTE_BLOCK.get(),
                CHLOROPHYTE_BLOCK.get(),
                SHROOMITE_BLOCK.get(),
                SPECTRE_BLOCK.get()
        );
        tag(ModTags.Blocks.NEEDS_9_LEVEL).add(
                RAW_LUMINITE_BLOCK.get(),
                LUMINITE_BLOCK.get()
        );
        tag(ModTags.Blocks.ROPE).add(ROPE.get(), VINE_ROPE.get(), SILK_ROPE.get(), WEB_ROPE.get());
        tag(Tags.Blocks.ROPES).add(ROPE.get(), VINE_ROPE.get(), SILK_ROPE.get(), WEB_ROPE.get());
        tag(BlockTags.STAIRS).add(
                SUN_PLATE_STAIRS.get(),
                OBSIDIAN_BRICKS_STAIRS.get(),
                BLUE_BRICK_STAIRS.get(),
                GREEN_BRICK_STAIRS.get(),
                PINK_BRICK_STAIRS.get(),
                SANDSTONE_BRICKS_STAIRS.get(),
                RED_SANDSTONE_BRICKS_STAIRS.get(),
                EBONSANDSTONE_BRICKS_STAIRS.get(),
                CRIMSANDSTONE_BRICKS_STAIRS.get(),
                PEARLSANDSTONE_BRICKS_STAIRS.get(),
                SNOW_BRICKS_STAIRS.get(),
                BLUE_ICE_BRICKS_STAIRS.get(),
                PACKED_ICE_BRICKS_STAIRS.get(),
                COPPER_BRICKS_STAIRS.get(),
                LEAD_BRICKS_STAIRS.get(),
                GOLDEN_BRICKS_STAIRS.get(),
                IRON_BRICKS_STAIRS.get(),
                SILVER_BRICKS_STAIRS.get(),
                PLATINUM_BRICKS_STAIRS.get(),
                METEORITE_BRICKS_STAIRS.get(),
                TIN_BRICKS_STAIRS.get(),
                TUNGSTEN_BRICKS_STAIRS.get(),
                CRIMTANE_ORE_BRICKS_STAIRS.get(),
                CRIMSTONE_BRICKS_STAIRS.get(),
                EBONSTONE_BRICKS_STAIRS.get(),
                PEARLSTONE_BRICKS_STAIRS.get(),
                DEMONITE_ORE_BRICKS_STAIRS.get()
        );
        tag(BlockTags.SLABS).add(
                SUN_PLATE_SLAB.get(),
                OBSIDIAN_BRICKS_SLAB.get(),
                BLUE_BRICK_SLAB.get(),
                GREEN_BRICK_SLAB.get(),
                PINK_BRICK_SLAB.get(),
                SANDSTONE_BRICKS_SLAB.get(),
                RED_SANDSTONE_BRICKS_SLAB.get(),
                EBONSANDSTONE_BRICKS_SLAB.get(),
                CRIMSANDSTONE_BRICKS_SLAB.get(),
                PEARLSANDSTONE_BRICKS_SLAB.get(),
                SNOW_BRICKS_SLAB.get(),
                BLUE_ICE_BRICKS_SLAB.get(),
                PACKED_ICE_BRICKS_SLAB.get(),
                COPPER_BRICKS_SLAB.get(),
                LEAD_BRICKS_SLAB.get(),
                GOLDEN_BRICKS_SLAB.get(),
                IRON_BRICKS_SLAB.get(),
                SILVER_BRICKS_SLAB.get(),
                PLATINUM_BRICKS_SLAB.get(),
                METEORITE_BRICKS_SLAB.get(),
                TIN_BRICKS_SLAB.get(),
                TUNGSTEN_BRICKS_SLAB.get(),
                CRIMTANE_ORE_BRICKS_SLAB.get(),
                CRIMSTONE_BRICKS_SLAB.get(),
                EBONSTONE_BRICKS_SLAB.get(),
                PEARLSTONE_BRICKS_SLAB.get(),
                DEMONITE_ORE_BRICKS_SLAB.get()
        );
        tag(BlockTags.WALLS).add(
                SANDSTONE_BRICKS_WALL.get(),
                RED_SANDSTONE_BRICKS_WALL.get(),
                EBONSANDSTONE_BRICKS_WALL.get(),
                CRIMSANDSTONE_BRICKS_WALL.get(),
                PEARLSANDSTONE_BRICKS_WALL.get()
        );
        tag(BlockTags.DOORS).add(
                SKYWARE_DOOR.get(),
                SKYWARE_GLASS_DOOR.get(),
                OBSIDIAN_BRICKS_DOOR.get(),
                LIHZAHRD_DOOR.get(),
                TRADITIONAL_DYNASTY_DOOR.get()
        );
        tag(ModTags.Blocks.MINEABLE_WITH_HAMMER)
                .addTag(BlockTags.WALLS)
                .addTag(BlockTags.STAIRS)
                .addTag(BlockTags.DOORS)
                .addTag(BlockTags.PLANKS)
                .addTag(BlockTags.STONE_BRICKS)
                .addTag(BlockTags.SLABS)
                .add(
                        SHADOW_ORB.get(),
                        CRIMSON_HEART.get(),
                        DEMON_ALTAR.get(),
                        CRIMSON_ALTAR.get(),
                        ENCHANTED_BLUE_BRICKS.get(),
                        ENCHANTED_FRAGILE_BLUE_BRICKS.get(),
                        ENCHANTED_GREEN_BRICKS.get(),
                        ENCHANTED_FRAGILE_GREEN_BRICKS.get(),
                        ENCHANTED_PINK_BRICKS.get(),
                        ENCHANTED_FRAGILE_PINK_BRICKS.get(),

                        FunctionalBlocks.MECHANICAL_FRAGILE_OBSIDIAN_BRICKS.get(),
                        FunctionalBlocks.FRAGILE_BLUE_BRICKS.get(),
                        FunctionalBlocks.FRAGILE_GREEN_BRICKS.get(),
                        FunctionalBlocks.FRAGILE_PINK_BRICKS.get(),

                        HELLSTONE_BRICKS.get(),
                        SANDSTONE_BRICKS.get(),
                        SNOW_BRICKS.get(),
                        BLUE_ICE_BRICKS.get(),
                        PACKED_ICE_BRICKS.get(),
                        COPPER_BRICKS.get(),
                        LEAD_BRICKS.get(),
                        GOLDEN_BRICKS.get(),
                        IRON_BRICKS.get(),
                        SILVER_BRICKS.get(),
                        PLATINUM_BRICKS.get(),
                        METEORITE_BRICKS.get(),
                        TIN_BRICKS.get(),
                        TUNGSTEN_BRICKS.get(),
                        CRIMTANE_ORE_BRICKS.get(),
                        CRIMSTONE_BRICKS.get(),
                        EBONSTONE_BRICKS.get(),
                        PEARLSTONE_BRICKS.get(),
                        DEMONITE_ORE_BRICKS.get(),
                        OBSIDIAN_BRICKS.get(),
                        OBSIDIAN_SMALL_BRICKS.get(),
                        CHISELED_OBSIDIAN_BRICKS.get(),
                        RAINBOW_BRICKS.get(),
                        GRANITE_BRICKS.get(),
                        GRANITE_COLUMN.get(),
                        POLISHED_GRANITE.get(),
                        MARBLE_COLUMN.get(),
                        MARBLE_BRICKS.get(),
                        CRACKED_MARBLE_BRICKS.get(),
                        MARBLE_SMALL_BRICKS.get(),
                        GILDED_MARBLE.get(),
                        POLISHED_MARBLE.get(),
                        LIHZAHRD_BRICKS.get(),
                        BLUE_BRICKS.get(),
                        GREEN_BRICKS.get(),
                        PINK_BRICKS.get(),
                        BLUE_BRICK_COLUMN.get(),
                        GREEN_BRICK_COLUMN.get(),
                        PINK_BRICK_COLUMN.get(),
                        CHISELED_BLUE_BRICKS.get(),
                        CHISELED_GREEN_BRICKS.get(),
                        CHISELED_PINK_BRICKS.get(),
                        AETHERIUM_BRICKS.get(),
                        CRACKED_BLUE_BRICKS.get(),
                        CRACKED_GREEN_BRICKS.get(),
                        CRACKED_PINK_BRICKS.get(),
                        ENCHANTED_BLUE_BRICKS.get(),
                        ENCHANTED_GREEN_BRICKS.get(),
                        ENCHANTED_PINK_BRICKS.get()
                );
        tag(ModTags.Blocks.MINEABLE_WITH_HAMAXE).addTag(ModTags.Blocks.MINEABLE_WITH_HAMMER).addTag(BlockTags.MINEABLE_WITH_AXE);
        tag(ModTags.Blocks.MINEABLE_WITH_HOE_SHOVEL).addTag(BlockTags.MINEABLE_WITH_SHOVEL).addTag(BlockTags.MINEABLE_WITH_HOE);

        tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).addTags(
                ModTags.Blocks.NEEDS_4_LEVEL
        );
        tag(Tags.Blocks.BUDS).add(
                CRYSTAL_SHARDS.get(),
                GELATIN_CRYSTAL.get()
        );
        tag(Tags.Blocks.CHAINS).add(
                AMBER_CHAIN.get(),
                RUBY_CHAIN.get(),
                SAPPHIRE_CHAIN.get(),
                SILK_CHAIN.get(),
                DIAMOND_CHAIN.get(),
                BONE_CHAIN.get(),
                TOPAZ_CHAIN.get(),
                JADE_CHAIN.get(),
                AMETHYST_CHAIN.get()
        );
        {
            ChestBlocks.BLOCKS.getEntries().forEach(block -> tag(Tags.Blocks.CHESTS).add(block.get()));
            ChestBlocks.DEATH_CHESTS.forEach(block -> tag(Tags.Blocks.CHESTS_TRAPPED).add(block.get()));
            tag(Tags.Blocks.CHESTS_WOODEN).add(ChestBlocks.IVY_CHEST.get(), ChestBlocks.DEATH_WOODEN_CHEST.get(), ChestBlocks.LIVING_WOOD_CHEST.get());
        }
        tag(Tags.Blocks.COBBLESTONES_NORMAL).add(COBBLED_EBONSTONE.get(), COBBLED_PEARLSTONE.get(), COBBLED_CRIMSTONE.get());
        tag(Tags.Blocks.DYED).add(
                PURE_GLASS.get(),
                WHITE_PURE_GLASS.get(),
                LIGHT_GRAY_PURE_GLASS.get(),
                GRAY_PURE_GLASS.get(),
                BLACK_PURE_GLASS.get(),
                BROWN_PURE_GLASS.get(),
                RED_PURE_GLASS.get(),
                ORANGE_PURE_GLASS.get(),
                YELLOW_PURE_GLASS.get(),
                LIME_PURE_GLASS.get(),
                GREEN_PURE_GLASS.get(),
                CYAN_PURE_GLASS.get(),
                LIGHT_BLUE_PURE_GLASS.get(),
                BLUE_PURE_GLASS.get(),
                PURPLE_PURE_GLASS.get(),
                MAGENTA_PURE_GLASS.get(),
                PINK_PURE_GLASS.get()
        );
        tag(Tags.Blocks.DYED_WHITE).add(WHITE_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_LIGHT_GRAY).add(LIGHT_GRAY_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_GRAY).add(GRAY_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_BLACK).add(BLACK_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_BROWN).add(BROWN_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_RED).add(RED_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_ORANGE).add(ORANGE_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_YELLOW).add(YELLOW_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_LIME).add(LIME_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_GREEN).add(GREEN_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_CYAN).add(CYAN_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_LIGHT_BLUE).add(LIGHT_BLUE_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_BLUE).add(BLUE_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_PURPLE).add(PURPLE_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_MAGENTA).add(MAGENTA_PURE_GLASS.get());
        tag(Tags.Blocks.DYED_PINK).add(PINK_PURE_GLASS.get());

        tag(Tags.Blocks.GLASS_BLOCKS).add(
                PURE_GLASS.get(),
                WHITE_PURE_GLASS.get(),
                LIGHT_GRAY_PURE_GLASS.get(),
                GRAY_PURE_GLASS.get(),
                BLACK_PURE_GLASS.get(),
                BROWN_PURE_GLASS.get(),
                RED_PURE_GLASS.get(),
                ORANGE_PURE_GLASS.get(),
                YELLOW_PURE_GLASS.get(),
                LIME_PURE_GLASS.get(),
                GREEN_PURE_GLASS.get(),
                CYAN_PURE_GLASS.get(),
                LIGHT_BLUE_PURE_GLASS.get(),
                BLUE_PURE_GLASS.get(),
                PURPLE_PURE_GLASS.get(),
                MAGENTA_PURE_GLASS.get(),
                PINK_PURE_GLASS.get()
        );
        tag(Tags.Blocks.GLASS_BLOCKS_COLORLESS).add(PURE_GLASS.get());
        tag(Tags.Blocks.GLASS_BLOCKS_CHEAP).add(
                PURE_GLASS.get(),
                WHITE_PURE_GLASS.get(),
                LIGHT_GRAY_PURE_GLASS.get(),
                GRAY_PURE_GLASS.get(),
                BLACK_PURE_GLASS.get(),
                BROWN_PURE_GLASS.get(),
                RED_PURE_GLASS.get(),
                ORANGE_PURE_GLASS.get(),
                YELLOW_PURE_GLASS.get(),
                LIME_PURE_GLASS.get(),
                GREEN_PURE_GLASS.get(),
                CYAN_PURE_GLASS.get(),
                LIGHT_BLUE_PURE_GLASS.get(),
                BLUE_PURE_GLASS.get(),
                PURPLE_PURE_GLASS.get(),
                MAGENTA_PURE_GLASS.get(),
                PINK_PURE_GLASS.get()
        );
        tag(Tags.Blocks.HIDDEN_FROM_RECIPE_VIEWERS).add(
                DEEPSLATE_COBALT_ORE.get(),
                RAW_COBALT_BLOCK.get(),
                DEEPSLATE_PALLADIUM_ORE.get(),
                RAW_PALLADIUM_BLOCK.get(),
                DEEPSLATE_MYTHRIL_ORE.get(),
                RAW_MYTHRIL_BLOCK.get(),
                DEEPSLATE_ORICHALCUM_ORE.get(),
                RAW_ORICHALCUM_BLOCK.get(),
                DEEPSLATE_ADAMANTITE_ORE.get(),
                RAW_ADAMANTITE_BLOCK.get(),
                DEEPSLATE_TITANIUM_ORE.get(),
                RAW_TITANIUM_BLOCK.get(),
                HALLOWED_BLOCK.get(),
                CHLOROPHYTE_ORE.get(),
                RAW_CHLOROPHYTE_BLOCK.get(),
                CHLOROPHYTE_BLOCK.get(),
                SHROOMITE_BLOCK.get(),
                SPECTRE_BLOCK.get()
        );
        tag(Tags.Blocks.OBSIDIANS).add(
                CHISELED_OBSIDIAN_BRICKS.get(),
                OBSIDIAN_BRICKS.get(),
                MECHANICAL_FRAGILE_OBSIDIAN_BRICKS.get(),
                OBSIDIAN_BRICKS_SLAB.get(),
                OBSIDIAN_BRICKS_STAIRS.get(),
                OBSIDIAN_BRICKS_DOOR.get(),
                OBSIDIAN_SMALL_BRICKS.get()
        );
        tag(Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK).add(ASH_BLOCK.get());
        tag(Tags.Blocks.ORE_RATES_SINGULAR).addTags(
                ModTags.Blocks.ORES_LEAD,
                ModTags.Blocks.ORES_SILVER,
                ModTags.Blocks.ORES_TUNGSTEN,
                ModTags.Blocks.ORES_RUBY,
                ModTags.Blocks.ORES_AMBER,
                ModTags.Blocks.ORES_TOPAZ,
                ModTags.Blocks.ORES_JADE,
                ModTags.Blocks.ORES_SAPPHIRE,
                ModTags.Blocks.ORES_AMETHYST,
                ModTags.Blocks.ORES_PLATINUM,
                ModTags.Blocks.ORES_COBALT,
                ModTags.Blocks.ORES_PALLADIUM,
                ModTags.Blocks.ORES_MYTHRIL,
                ModTags.Blocks.ORES_ORICHALCUM,
                ModTags.Blocks.ORES_ADAMANTITE,
                ModTags.Blocks.ORES_TITANIUM
        ).add(
                SANCTIFICATION_IRON_ORE.get(),
                CORRUPTION_IRON_ORE.get(),
                FLESHIFICATION_IRON_ORE.get(),

                SANCTIFICATION_DIAMOND_ORE.get(),
                CORRUPTION_DIAMOND_ORE.get(),
                FLESHIFICATION_DIAMOND_ORE.get(),

                SANCTIFICATION_EMERALD_ORE.get(),
                CORRUPTION_EMERALD_ORE.get(),
                FLESHIFICATION_EMERALD_ORE.get()
        );
        tag(Tags.Blocks.ORE_RATES_DENSE).addTag(
                ModTags.Blocks.ORES_TIN
        ).add(
                CORRUPTION_COPPER_ORE.get(),
                FLESHIFICATION_COPPER_ORE.get(),
                SANCTIFICATION_COPPER_ORE.get(),
                SANCTIFICATION_COAL_ORE.get(),
                CORRUPTION_COAL_ORE.get(),
                FLESHIFICATION_COAL_ORE.get(),
                CORRUPTION_REDSTONE_ORE.get(),
                FLESHIFICATION_REDSTONE_ORE.get(),
                SANCTIFICATION_REDSTONE_ORE.get(),
                CORRUPTION_LAPIS_ORE.get(),
                FLESHIFICATION_LAPIS_ORE.get(),
                SANCTIFICATION_LAPIS_ORE.get()
        );
        tag(Tags.Blocks.ORES_COAL).add(SANCTIFICATION_COAL_ORE.get(), CORRUPTION_COAL_ORE.get(), FLESHIFICATION_COAL_ORE.get());
        tag(Tags.Blocks.ORES_COPPER).add(SANCTIFICATION_COPPER_ORE.get(), CORRUPTION_COPPER_ORE.get(), FLESHIFICATION_COPPER_ORE.get());
        tag(Tags.Blocks.ORES_DIAMOND).add(SANCTIFICATION_DIAMOND_ORE.get(), CORRUPTION_DIAMOND_ORE.get(), FLESHIFICATION_DIAMOND_ORE.get());
        tag(Tags.Blocks.ORES_EMERALD).add(SANCTIFICATION_EMERALD_ORE.get(), CORRUPTION_EMERALD_ORE.get(), FLESHIFICATION_EMERALD_ORE.get());
        tag(Tags.Blocks.ORES_GOLD).add(SANCTIFICATION_GOLD_ORE.get(), CORRUPTION_GOLD_ORE.get(), FLESHIFICATION_GOLD_ORE.get());
        tag(Tags.Blocks.ORES_IRON).add(SANCTIFICATION_IRON_ORE.get(), CORRUPTION_IRON_ORE.get(), FLESHIFICATION_IRON_ORE.get());
        tag(Tags.Blocks.ORES_LAPIS).add(SANCTIFICATION_LAPIS_ORE.get(), CORRUPTION_LAPIS_ORE.get(), FLESHIFICATION_LAPIS_ORE.get());
        tag(Tags.Blocks.ORES_REDSTONE).add(SANCTIFICATION_REDSTONE_ORE.get(), CORRUPTION_REDSTONE_ORE.get(), FLESHIFICATION_REDSTONE_ORE.get());
        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(
                DEEPSLATE_TIN_ORE.get(),
                DEEPSLATE_LEAD_ORE.get(),
                DEEPSLATE_SILVER_ORE.get(),
                DEEPSLATE_TUNGSTEN_ORE.get(),
                DEEPSLATE_PLATINUM_ORE.get(),
                DEEPSLATE_COBALT_ORE.get(),
                DEEPSLATE_PALLADIUM_ORE.get(),
                DEEPSLATE_MYTHRIL_ORE.get(),
                DEEPSLATE_ORICHALCUM_ORE.get(),
                DEEPSLATE_ADAMANTITE_ORE.get(),
                DEEPSLATE_TITANIUM_ORE.get(),

                DEEPSLATE_RUBY_ORE.get(),
                DEEPSLATE_TOPAZ_ORE.get(),
                DEEPSLATE_JADE_ORE.get(),
                DEEPSLATE_SAPPHIRE_ORE.get(),
                DEEPSLATE_AMETHYST_ORE.get()
        );
        tag(Tags.Blocks.ORES_IN_GROUND_NETHERRACK).add(HELLSTONE.get());
        tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(
                TIN_ORE.get(),
                LEAD_ORE.get(),
                SILVER_ORE.get(),
                TUNGSTEN_ORE.get(),
                PLATINUM_ORE.get(),
                DEMONITE_ORE.get(),
                CRIMTANE_ORE.get(),

                RUBY_ORE.get(),
                TOPAZ_ORE.get(),
                JADE_ORE.get(),
                SAPPHIRE_ORE.get(),
                AMETHYST_ORE.get()
        );
        tag(Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES).add(HEAVY_WORK_BENCH.get());
        tag(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES).add(HELLFORGE.get());
        tag(Tags.Blocks.SANDS).add(EBONSAND.get(), CRIMSAND.get(), PEARLSAND.get());
        tag(Tags.Blocks.SANDSTONE_BLOCKS).add(HARDENED_EBONSAND_BLOCK.get(), HARDENED_CRIMSAND_BLOCK.get(), HARDENED_PEARLSAND_BLOCK.get());
        tag(Tags.Blocks.SANDSTONE_RED_BLOCKS).add(HARDENED_RED_SAND_BLOCK.get());
        tag(Tags.Blocks.STONES).add(EBONSTONE.get(), CRIMSTONE.get(), PEARLSTONE.get(), GRANITE.get(), MARBLE.get());
        tag(Tags.Blocks.VILLAGER_JOB_SITES).add(SKY_MILL.get(), COOKING_POT.get());
        tag(BlockTags.OVERWORLD_CARVER_REPLACEABLES).add(EBONSTONE.get(), CRIMSTONE.get(), HARDENED_SAND_BLOCK.get(), HARDENED_RED_SAND_BLOCK.get());
        tag(ModTags.Blocks.UNBREAKABLE_IF_CANNOT_HARVEST).addTags(ModTags.Blocks.NEEDS_2_LEVEL, ModTags.Blocks.NEEDS_3_LEVEL, ModTags.Blocks.NEEDS_4_LEVEL, ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL, Tags.Blocks.ORES_NETHERITE_SCRAP, Tags.Blocks.STORAGE_BLOCKS_NETHERITE);
        {
            tag(ModTags.Blocks.ORES_TIN).add(TIN_ORE.get(), DEEPSLATE_TIN_ORE.get(), CORRUPTION_TIN_ORE.get(), FLESHIFICATION_TIN_ORE.get(), SANCTIFICATION_TIN_ORE.get());
            tag(ModTags.Blocks.ORES_LEAD).add(LEAD_ORE.get(), DEEPSLATE_LEAD_ORE.get(), CORRUPTION_LEAD_ORE.get(), FLESHIFICATION_LEAD_ORE.get(), SANCTIFICATION_LEAD_ORE.get());
            tag(ModTags.Blocks.ORES_SILVER).add(SILVER_ORE.get(), DEEPSLATE_SILVER_ORE.get(), CORRUPTION_SILVER_ORE.get(), FLESHIFICATION_SILVER_ORE.get(), SANCTIFICATION_SILVER_ORE.get());
            tag(ModTags.Blocks.ORES_TUNGSTEN).add(TUNGSTEN_ORE.get(), DEEPSLATE_TUNGSTEN_ORE.get(), CORRUPTION_TUNGSTEN_ORE.get(), FLESHIFICATION_TUNGSTEN_ORE.get(), SANCTIFICATION_TUNGSTEN_ORE.get());
            tag(ModTags.Blocks.ORES_PLATINUM).add(PLATINUM_ORE.get(), DEEPSLATE_PLATINUM_ORE.get(), CORRUPTION_PLATINUM_ORE.get(), FLESHIFICATION_PLATINUM_ORE.get(), SANCTIFICATION_PLATINUM_ORE.get());
            tag(ModTags.Blocks.ORES_METEORITE).add(METEORITE_ORE.get());
            tag(ModTags.Blocks.ORES_DEMONITE).add(DEMONITE_ORE.get(), DEEPSLATE_DEMONITE_ORE.get(), CORRUPTION_DEMONITE_ORE.get(), FLESHIFICATION_DEMONITE_ORE.get(), SANCTIFICATION_DEMONITE_ORE.get());
            tag(ModTags.Blocks.ORES_CRIMTANE).add(CRIMTANE_ORE.get(), DEEPSLATE_CRIMTANE_ORE.get(), CORRUPTION_CRIMTANE_ORE.get(), FLESHIFICATION_CRIMTANE_ORE.get(), SANCTIFICATION_CRIMTANE_ORE.get());
            tag(ModTags.Blocks.ORES_HELLSTONE).add(HELLSTONE.get(), ASH_HELLSTONE.get());

            tag(ModTags.Blocks.ORES_COBALT).add(DEEPSLATE_COBALT_ORE.get());
            tag(ModTags.Blocks.ORES_PALLADIUM).add(DEEPSLATE_PALLADIUM_ORE.get());
            tag(ModTags.Blocks.ORES_MYTHRIL).add(DEEPSLATE_MYTHRIL_ORE.get());
            tag(ModTags.Blocks.ORES_ORICHALCUM).add(DEEPSLATE_ORICHALCUM_ORE.get());
            tag(ModTags.Blocks.ORES_ADAMANTITE).add(DEEPSLATE_ADAMANTITE_ORE.get());
            tag(ModTags.Blocks.ORES_TITANIUM).add(RAW_TITANIUM_BLOCK.get());

            tag(ModTags.Blocks.ORES_RUBY).add(RUBY_ORE.get(), DEEPSLATE_RUBY_ORE.get(), CORRUPTION_RUBY_ORE.get(), FLESHIFICATION_RUBY_ORE.get(), SANCTIFICATION_RUBY_ORE.get());
            tag(ModTags.Blocks.ORES_AMBER).add(AMBER_ORE.get(), RED_SAND_AMBER_ORE.get(), CORRUPTION_AMBER_ORE.get(), FLESHIFICATION_AMBER_ORE.get(), SANCTIFICATION_AMBER_ORE.get());
            tag(ModTags.Blocks.ORES_TOPAZ).add(TOPAZ_ORE.get(), DEEPSLATE_TOPAZ_ORE.get(), CORRUPTION_TOPAZ_ORE.get(), FLESHIFICATION_TOPAZ_ORE.get(), SANCTIFICATION_TOPAZ_ORE.get());
            tag(ModTags.Blocks.ORES_JADE).add(JADE_ORE.get(), DEEPSLATE_JADE_ORE.get(), CORRUPTION_JADE_ORE.get(), FLESHIFICATION_JADE_ORE.get(), SANCTIFICATION_JADE_ORE.get());
            tag(ModTags.Blocks.ORES_SAPPHIRE).add(SAPPHIRE_ORE.get(), DEEPSLATE_SAPPHIRE_ORE.get(), CORRUPTION_SAPPHIRE_ORE.get(), FLESHIFICATION_SAPPHIRE_ORE.get(), SANCTIFICATION_SAPPHIRE_ORE.get());
            tag(ModTags.Blocks.ORES_AMETHYST).add(AMETHYST_ORE.get(), DEEPSLATE_AMETHYST_ORE.get(), CORRUPTION_AMETHYST_ORE.get(), FLESHIFICATION_AMETHYST_ORE.get(), SANCTIFICATION_AMETHYST_ORE.get());

            tag(Tags.Blocks.ORES).addTags(
                    ModTags.Blocks.ORES_TIN,
                    ModTags.Blocks.ORES_LEAD,
                    ModTags.Blocks.ORES_SILVER,
                    ModTags.Blocks.ORES_TUNGSTEN,
                    ModTags.Blocks.ORES_PLATINUM,
                    ModTags.Blocks.ORES_METEORITE,
                    ModTags.Blocks.ORES_DEMONITE,
                    ModTags.Blocks.ORES_CRIMTANE,
                    ModTags.Blocks.ORES_HELLSTONE,
                    ModTags.Blocks.ORES_COBALT,
                    ModTags.Blocks.ORES_PALLADIUM,
                    ModTags.Blocks.ORES_MYTHRIL,
                    ModTags.Blocks.ORES_ORICHALCUM,
                    ModTags.Blocks.ORES_ADAMANTITE,
                    ModTags.Blocks.ORES_TITANIUM,
                    ModTags.Blocks.ORES_RUBY,
                    ModTags.Blocks.ORES_AMBER,
                    ModTags.Blocks.ORES_TOPAZ,
                    ModTags.Blocks.ORES_JADE,
                    ModTags.Blocks.ORES_SAPPHIRE,
                    ModTags.Blocks.ORES_AMETHYST
            );
        }
        {
            tag(ModTags.Blocks.STORAGE_BLOCKS_TIN).add(TIN_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_LEAD).add(LEAD_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_SILVER).add(SILVER_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_TUNGSTEN).add(TUNGSTEN_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_PLATINUM).add(PLATINUM_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_METEORITE).add(METEORITE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_DEMONITE).add(DEMONITE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_CRIMTANE).add(CRIMTANE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_HELLSTONE).add(HELLSTONE_BLOCK.get());

            tag(ModTags.Blocks.STORAGE_BLOCKS_COBALT).add(COBALT_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_PALLADIUM).add(PALLADIUM_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_MYTHRIL).add(MYTHRIL_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_ORICHALCUM).add(ORICHALCUM_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_ADAMANTITE).add(ADAMANTITE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_TITANIUM).add(TITANIUM_BLOCK.get());

            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_TIN).add(RAW_TIN_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_LEAD).add(RAW_LEAD_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_SILVER).add(RAW_SILVER_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_TUNGSTEN).add(RAW_TUNGSTEN_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_PLATINUM).add(RAW_PLATINUM_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_METEORITE).add(RAW_METEORITE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_DEMONITE).add(RAW_DEMONITE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_CRIMTANE).add(RAW_CRIMTANE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_HELLSTONE).add(RAW_HELLSTONE_BLOCK.get());

            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_COBALT).add(RAW_COBALT_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_PALLADIUM).add(RAW_PALLADIUM_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_MYTHRIL).add(RAW_MYTHRIL_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_ORICHALCUM).add(RAW_ORICHALCUM_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_ADAMANTITE).add(RAW_ADAMANTITE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_TITANIUM).add(RAW_TITANIUM_BLOCK.get());

            tag(ModTags.Blocks.STORAGE_BLOCKS_RUBY).add(RUBY_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_AMBER).add(AMBER_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_TOPAZ).add(TOPAZ_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_JADE).add(JADE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_SAPPHIRE).add(SAPPHIRE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_AMETHYST).add(AMETHYST_BLOCK.get());

            tag(ModTags.Blocks.STORAGE_BLOCKS_STURDY_FOSSIL).add(STURDY_FOSSIL_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_OPAL).add(OPAL_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_GELSTONE).add(GELSTONE_BLOCK.get());
            tag(ModTags.Blocks.STORAGE_BLOCKS_COLD_CRYSTAL).add(COLD_CRYSTAL_BLOCK.get());

            tag(ModTags.Blocks.STORAGE_BLOCKS_FLOATING_WHEAT_BALE).add(FLOATING_WHEAT_BALE.get());

            tag(Tags.Blocks.STORAGE_BLOCKS).addTags(
                    ModTags.Blocks.STORAGE_BLOCKS_TIN,
                    ModTags.Blocks.STORAGE_BLOCKS_LEAD,
                    ModTags.Blocks.STORAGE_BLOCKS_SILVER,
                    ModTags.Blocks.STORAGE_BLOCKS_TUNGSTEN,
                    ModTags.Blocks.STORAGE_BLOCKS_PLATINUM,
                    ModTags.Blocks.STORAGE_BLOCKS_METEORITE,
                    ModTags.Blocks.STORAGE_BLOCKS_DEMONITE,
                    ModTags.Blocks.STORAGE_BLOCKS_CRIMTANE,
                    ModTags.Blocks.STORAGE_BLOCKS_HELLSTONE,

                    ModTags.Blocks.STORAGE_BLOCKS_COBALT,
                    ModTags.Blocks.STORAGE_BLOCKS_PALLADIUM,
                    ModTags.Blocks.STORAGE_BLOCKS_MYTHRIL,
                    ModTags.Blocks.STORAGE_BLOCKS_ORICHALCUM,
                    ModTags.Blocks.STORAGE_BLOCKS_ADAMANTITE,
                    ModTags.Blocks.STORAGE_BLOCKS_TITANIUM,

                    ModTags.Blocks.STORAGE_BLOCKS_RAW_TIN,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_LEAD,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_SILVER,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_TUNGSTEN,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_PLATINUM,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_METEORITE,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_DEMONITE,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_CRIMTANE,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_HELLSTONE,

                    ModTags.Blocks.STORAGE_BLOCKS_RAW_COBALT,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_PALLADIUM,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_MYTHRIL,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_ORICHALCUM,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_ADAMANTITE,
                    ModTags.Blocks.STORAGE_BLOCKS_RAW_TITANIUM,

                    ModTags.Blocks.STORAGE_BLOCKS_RUBY,
                    ModTags.Blocks.STORAGE_BLOCKS_AMBER,
                    ModTags.Blocks.STORAGE_BLOCKS_TOPAZ,
                    ModTags.Blocks.STORAGE_BLOCKS_JADE,
                    ModTags.Blocks.STORAGE_BLOCKS_SAPPHIRE,
                    ModTags.Blocks.STORAGE_BLOCKS_AMETHYST,

                    ModTags.Blocks.STORAGE_BLOCKS_FLOATING_WHEAT_BALE
            );
        }
        tag(ModTags.Blocks.TOMBSTONE).add(TOMBSTONES.keySet().stream().map(DeferredBlock::get).toArray(TombstoneBlock[]::new));
        tag(BlockTags.STONE_BRICKS).add(
                BLUE_BRICKS.get(),
                CHISELED_BLUE_BRICKS.get(),
                FRAGILE_BLUE_BRICKS.get(),
                GREEN_BRICKS.get(),
                CHISELED_GREEN_BRICKS.get(),
                FRAGILE_GREEN_BRICKS.get(),
                PINK_BRICKS.get(),
                CHISELED_PINK_BRICKS.get(),
                FRAGILE_PINK_BRICKS.get()
        );
        tag(BlockTags.BASE_STONE_OVERWORLD).add(EBONSTONE.get(), CRIMSTONE.get(), PEARLSTONE.get());
        {
            tag(ModTags.Blocks.PURE_CONVERSION_GRASS_BLOCK).add(CORRUPT_GRASS_BLOCK.get(), CRIMSON_GRASS_BLOCK.get(), HALLOW_GRASS_BLOCK.get());
            tag(ModTags.Blocks.PURE_CONVERSION_JUNGLE_GRASS_BLOCK).add(CORRUPT_JUNGLE_GRASS_BLOCK.get(), CRIMSON_JUNGLE_GRASS_BLOCK.get());
            tag(ModTags.Blocks.PURE_CONVERSION_SHORT_GRASS).add(HALLOW_GRASS.get(), CRIMSON_GRASS.get(), CORRUPT_GRASS.get());
            tag(ModTags.Blocks.PURE_CONVERSION_PACKED_ICE).add(RED_PACKED_ICE.get(), PINK_PACKED_ICE.get(), PURPLE_PACKED_ICE.get());
            tag(ModTags.Blocks.PURE_CONVERSION_ICE).add(RED_ICE.get(), PINK_ICE.get(), PURPLE_ICE.get());
            tag(ModTags.Blocks.PURE_CONVERSION_SAND).add(PEARLSAND.get(), CRIMSAND.get(), EBONSAND.get());
            tag(ModTags.Blocks.PURE_CONVERSION_SANDSTONE).add(CRIMSANDSTONE.get(), PEARLSANDSTONE.get(), EBONSANDSTONE.get());
            tag(ModTags.Blocks.PURE_CONVERSION_HARDENED_SAND_BLOCK).add(HARDENED_PEARLSAND_BLOCK.get(), HARDENED_CRIMSAND_BLOCK.get(), HARDENED_EBONSAND_BLOCK.get());
            tag(ModTags.Blocks.PURE_CONVERSION_MOIST_SAND_BLOCK).add(MOISTENED_PEARLSAND_BLOCK.get(), MOISTENED_CRIMSAND_BLOCK.get(), MOISTENED_EBONSAND_BLOCK.get());
            tag(ModTags.Blocks.PURE_CONVERSION_CACTUS).add(CORRUPT_CACTUS.get(), CRIMSON_CACTUS.get(), HALLOW_CACTUS.get());
        }
        {
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_DIRT).add(Blocks.MUD);
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_GRASS_BLOCK).addTag(BlockTags.CONVERTABLE_TO_MUD).add(Blocks.GRASS_BLOCK, HALLOW_GRASS_BLOCK.get(), CRIMSON_GRASS.get());
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_JUNGLE_GRASS_BLOCK).add(JUNGLE_GRASS_BLOCK.get(), CRIMSON_JUNGLE_GRASS_BLOCK.get());
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_SHORT_GRASS).add(Blocks.SHORT_GRASS, Blocks.FERN, HALLOW_GRASS.get(), CRIMSON_GRASS.get());
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_PACKED_ICE).add(Blocks.PACKED_ICE, RED_PACKED_ICE.get(), PINK_PACKED_ICE.get());
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_ICE).add(Blocks.ICE, RED_ICE.get(), PINK_ICE.get());
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_SAND).add(Blocks.SAND, Blocks.RED_SAND, PEARLSAND.get(), CRIMSAND.get());
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_SANDSTONE).add(Blocks.SANDSTONE, Blocks.RED_SANDSTONE, CRIMSANDSTONE.get(), PEARLSANDSTONE.get());
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_HARDENED_SAND_BLOCK).add(HARDENED_SAND_BLOCK.get(), HARDENED_RED_SAND_BLOCK.get(), HARDENED_PEARLSAND_BLOCK.get(), HARDENED_CRIMSAND_BLOCK.get());
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_MOIST_SAND_BLOCK).add(MOISTENED_SAND_BLOCK.get(), MOISTENED_RED_SAND_BLOCK.get(), MOISTENED_PEARLSAND_BLOCK.get(), MOISTENED_CRIMSAND_BLOCK.get());
            tag(ModTags.Blocks.CORRUPTION_CONVERSION_CACTUS).add(Blocks.CACTUS);
        }
        {
            tag(ModTags.Blocks.CRIMSON_CONVERSION_DIRT).add(Blocks.MUD);
            tag(ModTags.Blocks.CRIMSON_CONVERSION_GRASS_BLOCK).addTag(BlockTags.CONVERTABLE_TO_MUD).add(Blocks.GRASS_BLOCK, CORRUPT_GRASS_BLOCK.get(), HALLOW_GRASS.get());
            tag(ModTags.Blocks.CRIMSON_CONVERSION_JUNGLE_GRASS_BLOCK).add(JUNGLE_GRASS_BLOCK.get(), CORRUPT_JUNGLE_GRASS_BLOCK.get());
            tag(ModTags.Blocks.CRIMSON_CONVERSION_SHORT_GRASS).add(Blocks.SHORT_GRASS, Blocks.FERN, CORRUPT_GRASS.get(), HALLOW_GRASS.get());
            tag(ModTags.Blocks.CRIMSON_CONVERSION_PACKED_ICE).add(Blocks.PACKED_ICE, PURPLE_PACKED_ICE.get(), PINK_PACKED_ICE.get());
            tag(ModTags.Blocks.CRIMSON_CONVERSION_ICE).add(Blocks.ICE, PURPLE_ICE.get(), PINK_ICE.get());
            tag(ModTags.Blocks.CRIMSON_CONVERSION_SAND).add(Blocks.SAND, Blocks.RED_SAND, EBONSAND.get(), PEARLSAND.get());
            tag(ModTags.Blocks.CRIMSON_CONVERSION_SANDSTONE).add(Blocks.SANDSTONE, Blocks.RED_SANDSTONE, EBONSANDSTONE.get(), PEARLSANDSTONE.get());
            tag(ModTags.Blocks.CRIMSON_CONVERSION_HARDENED_SAND_BLOCK).add(HARDENED_SAND_BLOCK.get(), HARDENED_RED_SAND_BLOCK.get(), HARDENED_PEARLSAND_BLOCK.get(), HARDENED_EBONSAND_BLOCK.get());
            tag(ModTags.Blocks.CRIMSON_CONVERSION_MOIST_SAND_BLOCK).add(MOISTENED_SAND_BLOCK.get(), MOISTENED_RED_SAND_BLOCK.get(), MOISTENED_EBONSAND_BLOCK.get(), MOISTENED_PEARLSAND_BLOCK.get());
            tag(ModTags.Blocks.CRIMSON_CONVERSION_CACTUS).add(Blocks.CACTUS);
        }
        {
            tag(ModTags.Blocks.HALLOW_CONVERSION_GRASS_BLOCK).addTag(BlockTags.CONVERTABLE_TO_MUD).add(Blocks.GRASS_BLOCK, CORRUPT_GRASS_BLOCK.get(), CRIMSON_GRASS.get());
            tag(ModTags.Blocks.HALLOW_CONVERSION_SHORT_GRASS).add(Blocks.SHORT_GRASS, Blocks.FERN, CORRUPT_GRASS.get(), CRIMSON_GRASS.get());
            tag(ModTags.Blocks.HALLOW_CONVERSION_PACKED_ICE).add(Blocks.PACKED_ICE, PURPLE_PACKED_ICE.get(), RED_PACKED_ICE.get());
            tag(ModTags.Blocks.HALLOW_CONVERSION_ICE).add(Blocks.ICE, PURPLE_ICE.get(), RED_ICE.get());
            tag(ModTags.Blocks.HALLOW_CONVERSION_SAND).add(Blocks.SAND, Blocks.RED_SAND, EBONSAND.get(), CRIMSAND.get());
            tag(ModTags.Blocks.HALLOW_CONVERSION_SANDSTONE).add(Blocks.SANDSTONE, Blocks.RED_SANDSTONE, EBONSANDSTONE.get(), CRIMSANDSTONE.get());
            tag(ModTags.Blocks.HALLOW_CONVERSION_HARDENED_SAND_BLOCK).add(HARDENED_SAND_BLOCK.get(), HARDENED_RED_SAND_BLOCK.get(), HARDENED_EBONSAND_BLOCK.get(), HARDENED_CRIMSAND_BLOCK.get());
            tag(ModTags.Blocks.HALLOW_CONVERSION_MOIST_SAND_BLOCK).add(MOISTENED_SAND_BLOCK.get(), MOISTENED_RED_SAND_BLOCK.get(), MOISTENED_EBONSAND_BLOCK.get(), MOISTENED_CRIMSAND_BLOCK.get());
            tag(ModTags.Blocks.HALLOW_CONVERSION_CACTUS).add(Blocks.CACTUS);
        }
        tag(ModTags.Blocks.CURSED_FLAME_BASE_BLOCK).add(
                EBONSTONE.get(),
                COBBLED_EBONSTONE.get(),
                HARDENED_EBONSAND_BLOCK.get(),
                EBONSANDSTONE.get(),
                EBONSAND.get()
        );
        tag(ModTags.Blocks.BLOODTHIRST_CRYSTALL_BASE_BLOCK).add(CRIMSON_GRASS_BLOCK.get(), CRIMSON_JUNGLE_GRASS_BLOCK.get());
        tag(ModTags.Blocks.CORRODED_WORM_ROOTS_BASE_BLOCK).add(CORRUPT_GRASS_BLOCK.get(), CORRUPT_JUNGLE_GRASS_BLOCK.get());
        tag(ModTags.Blocks.DECOMPOSE_THE_SOURCE_EXTRACT_BASE_BLOCK).add(EBONSTONE.get(), COBBLED_EBONSTONE.get());
        tag(BlockTags.FEATURES_CANNOT_REPLACE).add(
                BLUE_BRICKS.get(),
                GREEN_BRICKS.get(),
                PINK_BRICKS.get(),
                CHISELED_BLUE_BRICKS.get(),
                CHISELED_GREEN_BRICKS.get(),
                CHISELED_PINK_BRICKS.get()
        );
        tag(ModTags.Blocks.UNBREAKABLE).add(
                Blocks.BEDROCK,
                Blocks.COMMAND_BLOCK,
                Blocks.CHAIN_COMMAND_BLOCK,
                Blocks.REPEATING_COMMAND_BLOCK,
                Blocks.BARRIER,
                Blocks.STRUCTURE_BLOCK
        );
        tag(ModTags.Blocks.SPREADABLE_GRASS_BLOCK).add(
                Blocks.GRASS_BLOCK,
                JUNGLE_GRASS_BLOCK.get(),
                CORRUPT_JUNGLE_GRASS_BLOCK.get(),
                CRIMSON_JUNGLE_GRASS_BLOCK.get(),
                MUSHROOM_GRASS_BLOCK.get(),
                ASH_GRASS_BLOCK.get()
        );
        tag(ModTags.Blocks.CRIMSON_BLOCKS).addTags(
                ModTags.Blocks.CRIMSON_DESERT_BLOCKS,
                ModTags.Blocks.CRIMSON_TUNDRA_BLOCKS
        ).add(
                CRIMSON_GRASS_BLOCK.get(),
                SHADOW_LOG_BLOCKS.LOG.get(),
                SHADOW_LOG_BLOCKS.LEAVES.get(),
                CRIMSTONE.get(),
                COBBLED_CRIMSTONE.get(),
                CRIMSON_GRASS.get(),
                FLESHIFICATION_REDSTONE_ORE.get(),
                FLESHIFICATION_COAL_ORE.get(),
                FLESHIFICATION_LAPIS_ORE.get(),
                FLESHIFICATION_COPPER_ORE.get(),
                FLESHIFICATION_IRON_ORE.get(),
                FLESHIFICATION_EMERALD_ORE.get(),
                FLESHIFICATION_DIAMOND_ORE.get(),
                FLESHIFICATION_GOLD_ORE.get(),
                FLESHIFICATION_TIN_ORE.get(),
                FLESHIFICATION_LEAD_ORE.get(),
                FLESHIFICATION_SILVER_ORE.get(),
                FLESHIFICATION_TUNGSTEN_ORE.get(),
                FLESHIFICATION_PLATINUM_ORE.get(),
                FLESHIFICATION_DEMONITE_ORE.get(),
                FLESHIFICATION_CRIMTANE_ORE.get()
        );
        tag(ModTags.Blocks.CORRUPTION_BLOCKS).addTags(
                ModTags.Blocks.CORRUPTED_DESERT_BLOCKS,
                ModTags.Blocks.CORRUPTED_TUNDRA_BLOCKS
        ).add(
                CORRUPT_GRASS_BLOCK.get(),
                EBONY_LOG_BLOCKS.LOG.get(),
                EBONY_LOG_BLOCKS.LEAVES.get(),
                EBONSTONE.get(),
                COBBLED_EBONSTONE.get(),
                CORRUPT_GRASS.get(),
                CORRUPTION_REDSTONE_ORE.get(),
                CORRUPTION_COAL_ORE.get(),
                CORRUPTION_LAPIS_ORE.get(),
                CORRUPTION_COPPER_ORE.get(),
                CORRUPTION_IRON_ORE.get(),
                CORRUPTION_EMERALD_ORE.get(),
                CORRUPTION_DIAMOND_ORE.get(),
                CORRUPTION_GOLD_ORE.get(),
                CORRUPTION_TIN_ORE.get(),
                CORRUPTION_LEAD_ORE.get(),
                CORRUPTION_SILVER_ORE.get(),
                CORRUPTION_TUNGSTEN_ORE.get(),
                CORRUPTION_PLATINUM_ORE.get(),
                CORRUPTION_DEMONITE_ORE.get(),
                CORRUPTION_CRIMTANE_ORE.get()
        );
        tag(ModTags.Blocks.HALLOW_BLOCKS).addTags(
                ModTags.Blocks.HALLOW_DESERT_BLOCKS,
                ModTags.Blocks.HALLOW_TUNDRA_BLOCKS
        ).add(
                HALLOW_GRASS_BLOCK.get(),
                PEARL_LOG_BLOCKS.LOG.get(),
                PEARL_LOG_BLOCKS.LEAVES.get(),
                PEARLSTONE.get(),
                COBBLED_PEARLSTONE.get(),
                HALLOW_GRASS.get(),
                SANCTIFICATION_REDSTONE_ORE.get(),
                SANCTIFICATION_COAL_ORE.get(),
                SANCTIFICATION_LAPIS_ORE.get(),
                SANCTIFICATION_COPPER_ORE.get(),
                SANCTIFICATION_IRON_ORE.get(),
                SANCTIFICATION_EMERALD_ORE.get(),
                SANCTIFICATION_DIAMOND_ORE.get(),
                SANCTIFICATION_GOLD_ORE.get(),
                SANCTIFICATION_TIN_ORE.get(),
                SANCTIFICATION_LEAD_ORE.get(),
                SANCTIFICATION_SILVER_ORE.get(),
                SANCTIFICATION_TUNGSTEN_ORE.get(),
                SANCTIFICATION_PLATINUM_ORE.get(),
                SANCTIFICATION_DEMONITE_ORE.get(),
                SANCTIFICATION_CRIMTANE_ORE.get()
        );
        tag(TETags.Blocks.NPC_HOUSE_CONSTITUTE).add(
                WHITE_PAPER_PANE_LAMP.get(),
                MALACHITE_PAPER_PANE_LAMP.get()
        );
        tag(ModTags.Blocks.CACTUS).add(
                Blocks.CACTUS,
                CRIMSON_CACTUS.get(),
                CORRUPT_CACTUS.get(),
                HALLOW_CACTUS.get()
        );
        tag(ModTags.Blocks.RELIC).add(RELIC_BLOCKS.stream().map(DeferredHolder::get).toArray(Block[]::new));
        { // 没沙子是防止河流里的沙子被误判成沙漠
            tag(ModTags.Blocks.HALLOW_DESERT_BLOCKS).add(
                PEARLSAND.get(),
                PEARLSANDSTONE.get(),
                HARDENED_PEARLSAND_BLOCK.get(),
                MOISTENED_PEARLSAND_BLOCK.get());
            tag(ModTags.Blocks.HALLOW_TUNDRA_BLOCKS).add(PINK_ICE.get(), PINK_PACKED_ICE.get());
            tag(ModTags.Blocks.CRIMSON_DESERT_BLOCKS).add(
                CRIMSAND.get(),
                CRIMSANDSTONE.get(),
                HARDENED_CRIMSAND_BLOCK.get(),
                MOISTENED_CRIMSAND_BLOCK.get());
            tag(ModTags.Blocks.CRIMSON_TUNDRA_BLOCKS).add(RED_ICE.get(), RED_PACKED_ICE.get());
            tag(ModTags.Blocks.CORRUPTED_DESERT_BLOCKS).add(
                EBONSAND.get(),
                EBONSANDSTONE.get(),
                HARDENED_EBONSAND_BLOCK.get(),
                MOISTENED_EBONSAND_BLOCK.get());
            tag(ModTags.Blocks.CORRUPTED_TUNDRA_BLOCKS).add(PURPLE_ICE.get(), PURPLE_PACKED_ICE.get());
        }
        tag(ModTags.Blocks.GLOWING_MUSHROOM_BLOCKS).add(
            MUSHROOM_GRASS_BLOCK.get(),
            MUSHROOM_PATH.get(),
            GLOWING_MUSHROOM.get(),
            GLOWING_MUSHROOM_CATTAILS_BODY.get(),
            GLOWING_MUSHROOM_CATTAILS_HEAD.get(),
            GLOWING_MUSHROOM_INDUSIUM_BLOCK.get(),
            GLOWING_MUSHROOM_STEM_BLOCK.get(),
            GLOWING_MUSHROOM_PILEUS_BLOCK.get(),
            GLOWING_MUSHROOM_VINE.get(),
            GLOWING_MUSHROOM_MOSS.get()
        );
        tag(ModTags.Blocks.ENVIRONMENTAL_PRESERVATION).add(
                        Blocks.BAMBOO,
                        Blocks.SHORT_GRASS, Blocks.TALL_GRASS,
                        Blocks.FERN, Blocks.LARGE_FERN,
                        Blocks.DEAD_BUSH,
                        Blocks.SUGAR_CANE,
                        Blocks.CHORUS_PLANT, Blocks.CHORUS_FLOWER,
                        Blocks.SWEET_BERRY_BUSH,
                        Blocks.NETHER_WART,
                        Blocks.COCOA,
                        Blocks.VINE, Blocks.GLOW_LICHEN,
                        Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM,
                        Blocks.WARPED_FUNGUS, Blocks.CRIMSON_FUNGUS,
                        Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM,
                        Blocks.MOSS_CARPET,
                        Blocks.PINK_PETALS,
                        Blocks.BIG_DRIPLEAF,
                        Blocks.BIG_DRIPLEAF_STEM,
                        Blocks.SMALL_DRIPLEAF,

                        NatureBlocks.DESERT_GRASS.get(),
                        NatureBlocks.DESERT_TALL_GRASS.get(),
                        NatureBlocks.CORRUPT_GRASS.get(),
                        NatureBlocks.CRIMSON_GRASS.get(),
                        NatureBlocks.HALLOW_GRASS.get(),
                        NatureBlocks.NATURES_GIFT.get(),
                        JUNGLE_ROSE.get(),
                        GREEN_MOSS.get(),
                        BROWN_MOSS.get(),
                        RED_MOSS.get(),
                        BLUE_MOSS.get(),
                        PURPLE_MOSS.get(),
                        LAVA_MOSS.get(),
                        KRYPTON_MOSS.get(),
                        XENON_MOSS.get(),
                        ARGON_MOSS.get(),
                        NEON_MOSS.get(),
                        HELIUM_MOSS.get(),
                        GLOWING_MUSHROOM_MOSS.get(),
                        CATTAILS_BODY.get(),
                        JUNGLE_CATTAILS_BODY.get(),
                        GLOWING_MUSHROOM_CATTAILS_BODY.get(),
                        HALLOW_CATTAILS_BODY.get(),
                        EBONY_CATTAILS_BODY.get(),
                        CRIMSON_CATTAILS_BODY.get(),
                        CATTAILS_HEAD.get(),
                        JUNGLE_CATTAILS_HEAD.get(),
                        GLOWING_MUSHROOM_CATTAILS_HEAD.get(),
                        HALLOW_CATTAILS_HEAD.get(),
                        EBONY_CATTAILS_HEAD.get(),
                        CRIMSON_CATTAILS_HEAD.get(),
                        YELLOW_WILLOW_DROOPING_LEAVES.get(),
                        GLOWING_MUSHROOM_VINE.get(),
                        FOREST_DROOPING_VINE.get(),
                        JUNGLE_DROOPING_VINE.get(),
                        CORRUPT_DROOPING_VINE.get(),
                        CRIMSON_DROOPING_VINE.get(),
                        HALLOW_DROOPING_VINE.get(),
                        SHIMMER_DROOPING_VINE_PLANT.get(),
                        SMALL_DESERT_PLANT.get(),
                        BIG_DESERT_PLANT.get(),
                        SMALL_CACTUS.get(),
                        BLINKING_ROYAL_SHIMMERLILY.get()
                )
                .addTags(BlockTags.FLOWERS, BlockTags.LEAVES, BlockTags.SAPLINGS, BlockTags.CROPS)
                .addOptionalTag(ResourceLocation.fromNamespaceAndPath("twilightforest", "portal/decoration"));
        tag(BlockTags.CROPS).add(
                WATERLEAF.get(),
                FIREBLOSSOM.get(),
                MOONGLOW.get(),
                BLINKROOT.get(),
                SHIVERTHORN.get(),
                DAYBLOOM.get(),
                DEATHWEED.get()
        );
        tag(BlockTags.CAMPFIRES).add(
                LIFE_CAMPFIRE.get()
        );
        WaystonesHelper.blockTags(this::tag);
    }
}
