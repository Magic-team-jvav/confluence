package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.confluence.mod.Confluence;

public final class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_1_LEVEL = register("needs_1_level");
        public static final TagKey<Block> NEEDS_2_LEVEL = register("needs_2_level");
        public static final TagKey<Block> NEEDS_3_LEVEL = register("needs_3_level");
        public static final TagKey<Block> NEEDS_4_LEVEL = register("needs_4_level");
        public static final TagKey<Block> NEEDS_5_LEVEL = register("needs_5_level");
        public static final TagKey<Block> NEEDS_6_LEVEL = register("needs_6_level");
        public static final TagKey<Block> NEEDS_7_LEVEL = register("needs_7_level");
        public static final TagKey<Block> NEEDS_8_LEVEL = register("needs_8_level");
        public static final TagKey<Block> NEEDS_9_LEVEL = register("needs_9_level");
        public static final TagKey<Block> UNBREAKABLE = register("unbreakable"); // 用于锯刃镐及以上
        public static final TagKey<Block> COINS = register("coins");
        public static final TagKey<Block> EASY_CRASH = register("easy_crash");
        public static final TagKey<Block> MINEABLE_WITH_PICKAXE_AXE = register("mineable_with_pickaxe_axe");
        public static final TagKey<Block> DROOPING_VINE_CAN_SURVIVE = register("drooping_vine_can_survive");
        public static final TagKey<Block> JEWELLERY_BRANCHES_ATTACHABLE = register("jewellery_branches_attachable");
        public static final TagKey<Block> ASH_LOG_BRANCHES_ATTACHABLE = register("ash_log_branches_attachable");
        public static final TagKey<Block> DESERT_FOSSIL_REPLACEMENT = register("desert_fossil_replacement");
        public static final TagKey<Block> SLUSH_REPLACEMENT = register("slush_replacement");
        public static final TagKey<Block> MARINE_GRAVEL_REPLACEMENT = register("marine_gravel_replacement");
        public static final TagKey<Block> COLD_CRYSTAL_ORE_REPLACEMENT = register("cold_crystal_ore_replacement");
        public static final TagKey<Block> GELSTONE_ORE_REPLACEMENT = register("gelstone_ore_replacement");
        public static final TagKey<Block> OPAL_ORE_REPLACEMENT = register("opal_ore_replacement");
        public static final TagKey<Block> ROPE = register("rope"); // 汇流的绳子
        public static final TagKey<Block> BALLOON = register("balloon"); // 汇流的气球
        public static final TagKey<Block> MINEABLE_WITH_HAMMER = register("mineable_with_hammer"); // 使用锤子挖掘更快
        public static final TagKey<Block> MINEABLE_WITH_HAMAXE = register("mineable_with_hamaxe"); // 锤斧
        public static final TagKey<Block> MINEABLE_WITH_HOE_SHOVEL = register("mineable_with_hoe_shovel"); // 锄锹
        public static final TagKey<Block> UNBREAKABLE_IF_CANNOT_HARVEST = register("unbreakable_if_cannot_harvest");
        public static final TagKey<Block> CURSED_FLAME_BASE_BLOCK = register("cursed_flame_base_block");
        public static final TagKey<Block> BLOODTHIRST_CRYSTALL_BASE_BLOCK = register("bloodthirst_crystall_base_block");
        public static final TagKey<Block> CORRODED_WORM_ROOTS_BASE_BLOCK = register("corroded_worm_roots_base_block");
        public static final TagKey<Block> DECOMPOSE_THE_SOURCE_EXTRACT_BASE_BLOCK = register("decompose_the_source_extract_base_block");
        public static final TagKey<Block> SPREADABLE_GRASS_BLOCK = register("spreadable_grass_block");
        public static final TagKey<Block> ENVIRONMENTAL_PRESERVATION = register("environmental_preservation");
        public static final TagKey<Block> RELIC = register("relic"); // 圣物
        public static final TagKey<Block> CATTAIL_CAN_SURVIVE = register("cattail_can_survive");
        public static final TagKey<Block> VOID_TREE_ROOT_CAN_CONNECT = register("void_tree_root_can_connect");
        public static final TagKey<Block> END_PLANT_CAN_SURVIVE = register("end_plant_can_survive");
        public static final TagKey<Block> DRAGONSAL_ORE_REPLACE = register("dragonsal_ore_replace");
        public static final TagKey<Block> END_BROKEN_STONE_CAN_MOVE = register("end_broken_stone_can_move");
        public static final TagKey<Block> LUNAR_CORAL_DRY = register("lunar_coral_dry");

        public static final TagKey<Block> PURE_CONVERSION_GRASS_BLOCK = register("pure_conversion_grass_block");
        public static final TagKey<Block> PURE_CONVERSION_JUNGLE_GRASS_BLOCK = register("pure_conversion_jungle_grass_block");
        public static final TagKey<Block> PURE_CONVERSION_SHORT_GRASS = register("pure_conversion_short_grass");
        public static final TagKey<Block> PURE_CONVERSION_PACKED_ICE = register("pure_conversion_packed_ice");
        public static final TagKey<Block> PURE_CONVERSION_ICE = register("pure_conversion_ice");
        public static final TagKey<Block> PURE_CONVERSION_SAND = register("pure_conversion_sand");
        public static final TagKey<Block> PURE_CONVERSION_SANDSTONE = register("pure_conversion_sandstone");
        public static final TagKey<Block> PURE_CONVERSION_HARDENED_SAND_BLOCK = register("pure_conversion_hardened_sand_block");
        public static final TagKey<Block> PURE_CONVERSION_MOIST_SAND_BLOCK = register("pure_conversion_moist_sand_block");
        public static final TagKey<Block> PURE_CONVERSION_CACTUS = register("pure_conversion_cactus");

        public static final TagKey<Block> CORRUPTION_CONVERSION_DIRT = register("corruption_conversion_dirt");
        public static final TagKey<Block> CORRUPTION_CONVERSION_GRASS_BLOCK = register("corruption_conversion_grass_block");
        public static final TagKey<Block> CORRUPTION_CONVERSION_JUNGLE_GRASS_BLOCK = register("corruption_conversion_jungle_grass_block");
        public static final TagKey<Block> CORRUPTION_CONVERSION_SHORT_GRASS = register("corruption_conversion_short_grass");
        public static final TagKey<Block> CORRUPTION_CONVERSION_PACKED_ICE = register("corruption_conversion_packed_ice");
        public static final TagKey<Block> CORRUPTION_CONVERSION_ICE = register("corruption_conversion_ice");
        public static final TagKey<Block> CORRUPTION_CONVERSION_SAND = register("corruption_conversion_sand");
        public static final TagKey<Block> CORRUPTION_CONVERSION_SANDSTONE = register("corruption_conversion_sandstone");
        public static final TagKey<Block> CORRUPTION_CONVERSION_HARDENED_SAND_BLOCK = register("corruption_conversion_hardened_sand_block");
        public static final TagKey<Block> CORRUPTION_CONVERSION_MOIST_SAND_BLOCK = register("corruption_conversion_moist_sand_block");
        public static final TagKey<Block> CORRUPTION_CONVERSION_CACTUS = register("corruption_conversion_cactus");

        public static final TagKey<Block> CRIMSON_CONVERSION_DIRT = register("crimson_conversion_dirt");
        public static final TagKey<Block> CRIMSON_CONVERSION_GRASS_BLOCK = register("crimson_conversion_grass_block");
        public static final TagKey<Block> CRIMSON_CONVERSION_JUNGLE_GRASS_BLOCK = register("crimson_conversion_jungle_grass_block");
        public static final TagKey<Block> CRIMSON_CONVERSION_SHORT_GRASS = register("crimson_conversion_short_grass");
        public static final TagKey<Block> CRIMSON_CONVERSION_PACKED_ICE = register("crimson_conversion_packed_ice");
        public static final TagKey<Block> CRIMSON_CONVERSION_ICE = register("crimson_conversion_ice");
        public static final TagKey<Block> CRIMSON_CONVERSION_SAND = register("crimson_conversion_sand");
        public static final TagKey<Block> CRIMSON_CONVERSION_SANDSTONE = register("crimson_conversion_sandstone");
        public static final TagKey<Block> CRIMSON_CONVERSION_HARDENED_SAND_BLOCK = register("crimson_conversion_hardened_sand_block");
        public static final TagKey<Block> CRIMSON_CONVERSION_MOIST_SAND_BLOCK = register("crimson_conversion_moist_sand_block");
        public static final TagKey<Block> CRIMSON_CONVERSION_CACTUS = register("crimson_conversion_cactus");

        public static final TagKey<Block> HALLOW_CONVERSION_GRASS_BLOCK = register("hallow_conversion_grass_block");
        public static final TagKey<Block> HALLOW_CONVERSION_SHORT_GRASS = register("hallow_conversion_short_grass");
        public static final TagKey<Block> HALLOW_CONVERSION_PACKED_ICE = register("hallow_conversion_packed_ice");
        public static final TagKey<Block> HALLOW_CONVERSION_ICE = register("hallow_conversion_ice");
        public static final TagKey<Block> HALLOW_CONVERSION_SAND = register("hallow_conversion_sand");
        public static final TagKey<Block> HALLOW_CONVERSION_SANDSTONE = register("hallow_conversion_sandstone");
        public static final TagKey<Block> HALLOW_CONVERSION_HARDENED_SAND_BLOCK = register("hallow_conversion_hardened_sand_block");
        public static final TagKey<Block> HALLOW_CONVERSION_MOIST_SAND_BLOCK = register("hallow_conversion_moist_sand_block");
        public static final TagKey<Block> HALLOW_CONVERSION_CACTUS = register("hallow_conversion_cactus");

        public static final TagKey<Block> CRIMSON_BLOCKS = register("crimson_blocks");
        public static final TagKey<Block> CRIMSON_DESERT_BLOCKS = register("crimson_desert_blocks");
        public static final TagKey<Block> CRIMSON_TUNDRA_BLOCKS = register("crimson_tundra_blocks");
        public static final TagKey<Block> CORRUPTION_BLOCKS = register("corruption_blocks");
        public static final TagKey<Block> CORRUPTED_DESERT_BLOCKS = register("corrupted_desert_blocks");
        public static final TagKey<Block> CORRUPTED_TUNDRA_BLOCKS = register("corrupted_tundra_blocks");
        public static final TagKey<Block> HALLOW_BLOCKS = register("hallow_blocks");
        public static final TagKey<Block> HALLOW_DESERT_BLOCKS = register("hallow_desert_blocks");
        public static final TagKey<Block> HALLOW_TUNDRA_BLOCKS = register("hallow_tundra_blocks");
        public static final TagKey<Block> GLOWING_MUSHROOM_BLOCKS = register("glowing_mushroom_blocks");

        public static final TagKey<Block> VINES = common("vines");
        public static final TagKey<Block> TOMBSTONE = common("tombstone");
        public static final TagKey<Block> CACTUS = common("cactus");

        public static final TagKey<Block> ORES_TIN = common("ores/tin");
        public static final TagKey<Block> ORES_LEAD = common("ores/lead");
        public static final TagKey<Block> ORES_SILVER = common("ores/silver");
        public static final TagKey<Block> ORES_TUNGSTEN = common("ores/tungsten");
        public static final TagKey<Block> ORES_PLATINUM = common("ores/platinum");
        public static final TagKey<Block> ORES_METEORITE = common("ores/meteorite");
        public static final TagKey<Block> ORES_DEMONITE = common("ores/demonite");
        public static final TagKey<Block> ORES_CRIMTANE = common("ores/crimtane");
        public static final TagKey<Block> ORES_HELLSTONE = common("ores/hellstone");

        public static final TagKey<Block> ORES_COBALT = common("ores/cobalt");
        public static final TagKey<Block> ORES_PALLADIUM = common("ores/palladium");
        public static final TagKey<Block> ORES_MYTHRIL = common("ores/mythril");
        public static final TagKey<Block> ORES_ORICHALCUM = common("ores/orichalcum");
        public static final TagKey<Block> ORES_ADAMANTITE = common("ores/adamantite");
        public static final TagKey<Block> ORES_TITANIUM = common("ores/titanium");

        public static final TagKey<Block> ORES_RUBY = common("ores/ruby");
        public static final TagKey<Block> ORES_AMBER = common("ores/amber");
        public static final TagKey<Block> ORES_TOPAZ = common("ores/topaz");
        public static final TagKey<Block> ORES_JADE = common("ores/jade");
        public static final TagKey<Block> ORES_SAPPHIRE = common("ores/sapphire");
        public static final TagKey<Block> ORES_AMETHYST = common("ores/amethyst");

        public static final TagKey<Block> STORAGE_BLOCKS_TIN = common("storage_blocks/tin");
        public static final TagKey<Block> STORAGE_BLOCKS_LEAD = common("storage_blocks/lead");
        public static final TagKey<Block> STORAGE_BLOCKS_SILVER = common("storage_blocks/silver");
        public static final TagKey<Block> STORAGE_BLOCKS_TUNGSTEN = common("storage_blocks/tungsten");
        public static final TagKey<Block> STORAGE_BLOCKS_PLATINUM = common("storage_blocks/platinum");
        public static final TagKey<Block> STORAGE_BLOCKS_METEORITE = common("storage_blocks/meteorite");
        public static final TagKey<Block> STORAGE_BLOCKS_DEMONITE = common("storage_blocks/demonite");
        public static final TagKey<Block> STORAGE_BLOCKS_CRIMTANE = common("storage_blocks/crimtane");
        public static final TagKey<Block> STORAGE_BLOCKS_HELLSTONE = common("storage_blocks/hellstone");

        public static final TagKey<Block> STORAGE_BLOCKS_COBALT = common("storage_blocks/cobalt");
        public static final TagKey<Block> STORAGE_BLOCKS_PALLADIUM = common("storage_blocks/palladium");
        public static final TagKey<Block> STORAGE_BLOCKS_MYTHRIL = common("storage_blocks/mythril");
        public static final TagKey<Block> STORAGE_BLOCKS_ORICHALCUM = common("storage_blocks/orichalcum");
        public static final TagKey<Block> STORAGE_BLOCKS_ADAMANTITE = common("storage_blocks/adamantite");
        public static final TagKey<Block> STORAGE_BLOCKS_TITANIUM = common("storage_blocks/titanium");

        public static final TagKey<Block> STORAGE_BLOCKS_RAW_TIN = common("storage_blocks/raw_tin");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_LEAD = common("storage_blocks/raw_lead");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_SILVER = common("storage_blocks/raw_silver");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_TUNGSTEN = common("storage_blocks/raw_tungsten");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_PLATINUM = common("storage_blocks/raw_platinum");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_METEORITE = common("storage_blocks/raw_meteorite");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_DEMONITE = common("storage_blocks/raw_demonite");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_CRIMTANE = common("storage_blocks/raw_crimtane");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_HELLSTONE = common("storage_blocks/raw_hellstone");

        public static final TagKey<Block> STORAGE_BLOCKS_RAW_COBALT = common("storage_blocks/raw_cobalt");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_PALLADIUM = common("storage_blocks/raw_palladium");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_MYTHRIL = common("storage_blocks/raw_mythril");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_ORICHALCUM = common("storage_blocks/raw_orichalcum");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_ADAMANTITE = common("storage_blocks/raw_adamantite");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_TITANIUM = common("storage_blocks/raw_titanium");

        public static final TagKey<Block> STORAGE_BLOCKS_RUBY = common("storage_blocks/ruby");
        public static final TagKey<Block> STORAGE_BLOCKS_AMBER = common("storage_blocks/amber");
        public static final TagKey<Block> STORAGE_BLOCKS_TOPAZ = common("storage_blocks/topaz");
        public static final TagKey<Block> STORAGE_BLOCKS_JADE = common("storage_blocks/jade");
        public static final TagKey<Block> STORAGE_BLOCKS_SAPPHIRE = common("storage_blocks/sapphire");
        public static final TagKey<Block> STORAGE_BLOCKS_AMETHYST = common("storage_blocks/amethyst");

        public static final TagKey<Block> STORAGE_BLOCKS_STURDY_FOSSIL = common("storage_blocks/sturdy_fossil");
        public static final TagKey<Block> STORAGE_BLOCKS_OPAL = common("storage_blocks/opal");
        public static final TagKey<Block> STORAGE_BLOCKS_GELSTONE = common("storage_blocks/gelstone");
        public static final TagKey<Block> STORAGE_BLOCKS_COLD_CRYSTAL = common("storage_blocks/cold_crystal");

        public static final TagKey<Block> STORAGE_BLOCKS_FLOATING_WHEAT_BALE = common("storage_blocks/floating_wheat_bale");

        private static TagKey<Block> common(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Block> register(String id) {
            return Confluence.asTagKey(Registries.BLOCK, id);
        }
    }

    public static class Items {
        public static final TagKey<Item> STORAGE_BLOCKS_TIN = common("storage_blocks/tin");
        public static final TagKey<Item> STORAGE_BLOCKS_LEAD = common("storage_blocks/lead");
        public static final TagKey<Item> STORAGE_BLOCKS_SILVER = common("storage_blocks/silver");
        public static final TagKey<Item> STORAGE_BLOCKS_TUNGSTEN = common("storage_blocks/tungsten");
        public static final TagKey<Item> STORAGE_BLOCKS_PLATINUM = common("storage_blocks/platinum");
        public static final TagKey<Item> STORAGE_BLOCKS_METEORITE = common("storage_blocks/meteorite");
        public static final TagKey<Item> STORAGE_BLOCKS_DEMONITE = common("storage_blocks/demonite");
        public static final TagKey<Item> STORAGE_BLOCKS_CRIMTANE = common("storage_blocks/crimtane");
        public static final TagKey<Item> STORAGE_BLOCKS_HELLSTONE = common("storage_blocks/hellstone");

        public static final TagKey<Item> STORAGE_BLOCKS_COBALT = common("storage_blocks/cobalt");
        public static final TagKey<Item> STORAGE_BLOCKS_PALLADIUM = common("storage_blocks/palladium");
        public static final TagKey<Item> STORAGE_BLOCKS_MYTHRIL = common("storage_blocks/mythril");
        public static final TagKey<Item> STORAGE_BLOCKS_ORICHALCUM = common("storage_blocks/orichalcum");
        public static final TagKey<Item> STORAGE_BLOCKS_ADAMANTITE = common("storage_blocks/adamantite");
        public static final TagKey<Item> STORAGE_BLOCKS_TITANIUM = common("storage_blocks/titanium");

        public static final TagKey<Item> STORAGE_BLOCKS_RUBY = common("storage_blocks/ruby");
        public static final TagKey<Item> STORAGE_BLOCKS_AMBER = common("storage_blocks/amber");
        public static final TagKey<Item> STORAGE_BLOCKS_TOPAZ = common("storage_blocks/topaz");
        public static final TagKey<Item> STORAGE_BLOCKS_JADE = common("storage_blocks/jade");
        public static final TagKey<Item> STORAGE_BLOCKS_SAPPHIRE = common("storage_blocks/sapphire");
        public static final TagKey<Item> STORAGE_BLOCKS_AMETHYST = common("storage_blocks/amethyst");

        public static final TagKey<Item> STORAGE_BLOCKS_RAW_TIN = common("storage_blocks/raw_tin");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_LEAD = common("storage_blocks/raw_lead");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_SILVER = common("storage_blocks/raw_silver");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_TUNGSTEN = common("storage_blocks/raw_tungsten");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_PLATINUM = common("storage_blocks/raw_platinum");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_METEORITE = common("storage_blocks/raw_meteorite");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_DEMONITE = common("storage_blocks/raw_demonite");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_CRIMTANE = common("storage_blocks/raw_crimtane");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_HELLSTONE = common("storage_blocks/raw_hellstone");

        public static final TagKey<Item> STORAGE_BLOCKS_RAW_COBALT = common("storage_blocks/raw_cobalt");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_PALLADIUM = common("storage_blocks/raw_palladium");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_MYTHRIL = common("storage_blocks/raw_mythril");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_ORICHALCUM = common("storage_blocks/raw_orichalcum");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_ADAMANTITE = common("storage_blocks/raw_adamantite");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_TITANIUM = common("storage_blocks/raw_titanium");

        public static final TagKey<Item> STORAGE_BLOCKS_STURDY_FOSSIL = common("storage_blocks/sturdy_fossil");
        public static final TagKey<Item> STORAGE_BLOCKS_OPAL = common("storage_blocks/opal");
        public static final TagKey<Item> STORAGE_BLOCKS_GELSTONE = common("storage_blocks/gelstone");
        public static final TagKey<Item> STORAGE_BLOCKS_COLD_CRYSTAL = common("storage_blocks/cold_crystal");

        public static final TagKey<Item> STORAGE_BLOCKS_FLOATING_WHEAT_BALE = common("storage_blocks/floating_wheat_bale");

        public static final TagKey<Item> ORES_TIN = common("ores/tin");
        public static final TagKey<Item> ORES_LEAD = common("ores/lead");
        public static final TagKey<Item> ORES_SILVER = common("ores/silver");
        public static final TagKey<Item> ORES_TUNGSTEN = common("ores/tungsten");
        public static final TagKey<Item> ORES_PLATINUM = common("ores/platinum");
        public static final TagKey<Item> ORES_METEORITE = common("ores/meteorite");
        public static final TagKey<Item> ORES_DEMONITE = common("ores/demonite");
        public static final TagKey<Item> ORES_CRIMTANE = common("ores/crimtane");
        public static final TagKey<Item> ORES_HELLSTONE = common("ores/hellstone");

        public static final TagKey<Item> ORES_COBALT = common("ores/cobalt");
        public static final TagKey<Item> ORES_PALLADIUM = common("ores/palladium");
        public static final TagKey<Item> ORES_MYTHRIL = common("ores/mythril");
        public static final TagKey<Item> ORES_ORICHALCUM = common("ores/orichalcum");
        public static final TagKey<Item> ORES_ADAMANTITE = common("ores/adamantite");
        public static final TagKey<Item> ORES_TITANIUM = common("ores/titanium");

        public static final TagKey<Item> ORES_RUBY = common("ores/ruby");
        public static final TagKey<Item> ORES_AMBER = common("ores/amber");
        public static final TagKey<Item> ORES_TOPAZ = common("ores/topaz");
        public static final TagKey<Item> ORES_JADE = common("ores/jade");
        public static final TagKey<Item> ORES_SAPPHIRE = common("ores/sapphire");
        public static final TagKey<Item> ORES_AMETHYST = common("ores/amethyst");

        public static final TagKey<Item> INGOTS_TIN = common("ingots/tin");
        public static final TagKey<Item> INGOTS_LEAD = common("ingots/lead");
        public static final TagKey<Item> INGOTS_SILVER = common("ingots/silver");
        public static final TagKey<Item> INGOTS_TUNGSTEN = common("ingots/tungsten");
        public static final TagKey<Item> INGOTS_PLATINUM = common("ingots/platinum");
        public static final TagKey<Item> INGOTS_METEORITE = common("ingots/meteorite");
        public static final TagKey<Item> INGOTS_DEMONITE = common("ingots/demonite");
        public static final TagKey<Item> INGOTS_CRIMTANE = common("ingots/crimtane");
        public static final TagKey<Item> INGOTS_HELLSTONE = common("ingots/hellstone");

        public static final TagKey<Item> INGOTS_COBALT = common("ingots/cobalt");
        public static final TagKey<Item> INGOTS_PALLADIUM = common("ingots/palladium");
        public static final TagKey<Item> INGOTS_MYTHRIL = common("ingots/mythril");
        public static final TagKey<Item> INGOTS_ORICHALCUM = common("ingots/orichalcum");
        public static final TagKey<Item> INGOTS_ADAMANTITE = common("ingots/adamantite");
        public static final TagKey<Item> INGOTS_TITANIUM = common("ingots/titanium");

        public static final TagKey<Item> INGOTS_HALLOWED = common("ingots/hallowed");
        public static final TagKey<Item> INGOTS_CHLOROPHYTE = common("ingots/chlorophyte");
        public static final TagKey<Item> NUGGETS_TIN = common("nuggets/tin");
        public static final TagKey<Item> NUGGETS_LEAD = common("nuggets/lead");
        public static final TagKey<Item> NUGGETS_SILVER = common("nuggets/silver");
        public static final TagKey<Item> NUGGETS_TUNGSTEN = common("nuggets/tungsten");
        public static final TagKey<Item> NUGGETS_PLATINUM = common("nuggets/platinum");
        public static final TagKey<Item> NUGGETS_METEORITE = common("nuggets/meteorite");
        public static final TagKey<Item> NUGGETS_DEMONITE = common("nuggets/demonite");
        public static final TagKey<Item> NUGGETS_CRIMTANE = common("nuggets/crimtane");
        public static final TagKey<Item> NUGGETS_HELLSTONE = common("nuggets/hellstone");

        public static final TagKey<Item> RAW_MATERIALS_TIN = common("raw_materials/tin");
        public static final TagKey<Item> RAW_MATERIALS_LEAD = common("raw_materials/lead");
        public static final TagKey<Item> RAW_MATERIALS_SILVER = common("raw_materials/silver");
        public static final TagKey<Item> RAW_MATERIALS_TUNGSTEN = common("raw_materials/tungsten");
        public static final TagKey<Item> RAW_MATERIALS_PLATINUM = common("raw_materials/platinum");
        public static final TagKey<Item> RAW_MATERIALS_METEORITE = common("raw_materials/meteorite");
        public static final TagKey<Item> RAW_MATERIALS_DEMONITE = common("raw_materials/demonite");
        public static final TagKey<Item> RAW_MATERIALS_CRIMTANE = common("raw_materials/crimtane");
        public static final TagKey<Item> RAW_MATERIALS_HELLSTONE = common("raw_materials/hellstone");

        public static final TagKey<Item> RAW_MATERIALS_COBALT = common("raw_materials/cobalt");
        public static final TagKey<Item> RAW_MATERIALS_PALLADIUM = common("raw_materials/palladium");
        public static final TagKey<Item> RAW_MATERIALS_MYTHRIL = common("raw_materials/mythril");
        public static final TagKey<Item> RAW_MATERIALS_ORICHALCUM = common("raw_materials/orichalcum");
        public static final TagKey<Item> RAW_MATERIALS_ADAMANTITE = common("raw_materials/adamantite");
        public static final TagKey<Item> RAW_MATERIALS_TITANIUM = common("raw_materials/titanium");

        public static final TagKey<Item> RAW_MATERIALS_CHLOROPHYTE = common("raw_materials/chlorophyte");

        public static final TagKey<Item> GEMS_RUBY = common("gems/ruby");
        public static final TagKey<Item> GEMS_AMBER = common("gems/amber");
        public static final TagKey<Item> GEMS_TOPAZ = common("gems/topaz");
        public static final TagKey<Item> GEMS_JADE = common("gems/jade");
        public static final TagKey<Item> GEMS_SAPPHIRE = common("gems/sapphire");
        public static final TagKey<Item> GEMS_AMETHYST = common("gems/amethyst");

        public static final TagKey<Item> RAW_MATERIALS_STURDY_FOSSIL = common("raw_materials/sturdy_fossil");
        public static final TagKey<Item> RAW_MATERIALS_OPAL = common("raw_materials/opal");
        public static final TagKey<Item> RAW_MATERIALS_GELSTONE = common("raw_materials/gelstone");
        public static final TagKey<Item> RAW_MATERIALS_COLD_CRYSTAL = common("raw_materials/cold_crystal");

        public static final TagKey<Item> RAW_MATERIALS_FLOATING_WHEAT = common("raw_materials/raw_materials_floating_wheat");

        public static final TagKey<Item> TOOLS_HAMMER = common("tools/hammer");
        public static final TagKey<Item> TOOLS_LANCE = common("tools/lance");
        public static final TagKey<Item> TOOLS_DRILL = common("tools/drill");
        public static final TagKey<Item> TOOLS_CHAINSAW = common("tools/tools_chainsaw");
        public static final TagKey<Item> TOOLS_REPEATER_CROSSBOW = common("tools/repeater_crossbow");
        public static final TagKey<Item> TOOLS_REPEATER = register("tools/repeater");

        public static final TagKey<Item> TOOLS_SHEAR = common("tools/shear");

        public static final TagKey<Item> SPEAR = register("spear"); // neoforge那边为三叉戟
        public static final TagKey<Item> COINS = register("coins");
        public static final TagKey<Item> AMMO = register("ammo");
        public static final TagKey<Item> PET = register("pet");
        public static final TagKey<Item> LIGHT_PET = register("light_pet");
        public static final TagKey<Item> MINECART = register("minecart");
        public static final TagKey<Item> HOOK = register("hook");
        public static final TagKey<Item> MOUNT = register("mount");
        public static final TagKey<Item> DYE = register("dye");
        public static final TagKey<Item> SHORT_SWORD = register("short_sword");

        public static final TagKey<Item> PROVIDE_MANA = register("provide_mana");
        public static final TagKey<Item> PROVIDE_LIFE = register("provide_life");

        public static final TagKey<Item> TORCH = register("torch");
        public static final TagKey<Item> PROVIDE_LIGHT = register("provide_light");
        public static final TagKey<Item> HARDMODE = register("hardmode"); // 用于防止微光分解出困难模式物品
        public static final TagKey<Item> BOTTOMLESS = register("bottomless");
        public static final TagKey<Item> DESERT_FOSSIL = register("desert_fossil");
        public static final TagKey<Item> JUNK = register("junk");
        public static final TagKey<Item> SLUSH = register("slush");
        public static final TagKey<Item> SILT_BLOCK = register("silt_block");
        public static final TagKey<Item> MARINE_GRAVEL = register("marine_gravel");
        public static final TagKey<Item> POO = register("poo");
        public static final TagKey<Item> EXTRACT_SAND = register("extract_sand");
        public static final TagKey<Item> EXTRACT_HONEY_BLOCK = register("extract_honey_block");
        public static final TagKey<Item> EXTRACT_MOSS = register("extract_moss");
        public static final TagKey<Item> CORALS = register("corals");
        public static final TagKey<Item> EVIL_MATERIAL = register("evil_material");
        public static final TagKey<Item> SEAFOOD_DINNER_MATERIALS = register("seafood_dinner_materials");// 制作海鲜大餐
        public static final TagKey<Item> GOLD_COOKING = register("gold_cooking");// 制作金美味

        public static final TagKey<Item> INITIAL_WOOD = register("initial_wood");  // 木套tag，用于合成木套而分离其他套装
        public static final TagKey<Item> QUESTED_FISHES = register("quested_fishes");  // 任务鱼
        public static final TagKey<Item> EMBLEM = register("emblem");  // 徽章
        public static final TagKey<Item> LEAD_AND_IRON = register("lead_and_iron");
        public static final TagKey<Item> GOLD_AND_PLATINUM = register("gold_and_platinum");
        public static final TagKey<Item> SHADOW_SCALE_AND_TISSUE_SAMPLE = register("shadow_scale_and_tissue_sample");
        public static final TagKey<Item> EVIL_INGOT = register("evil_ingot");
        public static final TagKey<Item> MANA_WEAPON = register("mana_weapon");
        public static final TagKey<Item> HARDMODE_RAW_MATERIALS = register("hardmode_raw_materials");
        public static final TagKey<Item> WINGS = register("wings");
        public static final TagKey<Item> BOSS_SUMMONING = register("boss_summoning");
        public static final TagKey<Item> COAL_ORE_SMELTING = register("coal_ore_smelting");
        public static final TagKey<Item> IRON_ORE_SMELTING = register("iron_ore_smelting");
        public static final TagKey<Item> TIN_ORE_SMELTING = register("tin_ore_smelting");
        public static final TagKey<Item> COPPER_ORE_SMELTING = register("copper_ore_smelting");
        public static final TagKey<Item> LEAD_ORE_SMELTING = register("lead_ore_smelting");
        public static final TagKey<Item> SILVER_ORE_SMELTING = register("silver_ore_smelting");
        public static final TagKey<Item> TUNGSTEN_ORE_SMELTING = register("tungsten_ore_smelting");
        public static final TagKey<Item> GOLD_ORE_SMELTING = register("gold_ore_smelting");
        public static final TagKey<Item> PLATINUM_ORE_SMELTING = register("platinum_ore_smelting");
        public static final TagKey<Item> DEMONITE_ORE_SMELTING = register("demonite_ore_smelting");
        public static final TagKey<Item> CRIMTANE_ORE_SMELTING = register("crimtane_ore_smelting");
        public static final TagKey<Item> METEORITE_ORE_SMELTING = register("meteorite_ore_smelting");
        public static final TagKey<Item> DIAMOND_ORE_SMELTING = register("diamond_ore_smelting");
        public static final TagKey<Item> EMERALD_ORE_SMELTING = register("emerald_ore_smelting");
        public static final TagKey<Item> REDSTONE_ORE_SMELTING = register("redstone_ore_smelting");
        public static final TagKey<Item> LAPIS_ORE_SMELTING = register("lapis_ore_smelting");

        public static final TagKey<Item> PREFIX_UNIVERSAL_ONLY = register("prefix_universal_only");
        public static final TagKey<Item> PREFIX_MELEE_ONLY = register("prefix_melee_only");
        public static final TagKey<Item> PREFIX_RANGED_ONLY = register("prefix_ranged_only");
        public static final TagKey<Item> PREFIX_MAGIC_ONLY = register("prefix_magic_only");
        public static final TagKey<Item> PREFIX_ACCESSORY_ONLY = register("prefix_accessory_only");

        public static final TagKey<Item> COBALT_ORE_SMELTING = common("cobalt_ore_smelting");
        public static final TagKey<Item> PALLADIUM_ORE_SMELTING = common("palladium_ore_smelting");
        public static final TagKey<Item> MYTHRIL_ORE_SMELTING = common("mythril_ore_smelting");
        public static final TagKey<Item> ORICHALCUM_ORE_SMELTING = common("orichalcum_ore_smelting");
        public static final TagKey<Item> ADAMANTITE_ORE_SMELTING = common("adamantite_ore_smelting");
        public static final TagKey<Item> TITANIUM_ORE_SMELTING = common("titanium_ore_smelting");

        public static final TagKey<Item> MOSS_ITEM = register("moss_item");
        public static final TagKey<Item> SUMMONER_WEAPON = register("summoner_weapon");
        public static final TagKey<Item> CROP_FORTUNE = register("crop_fortune");
        public static final TagKey<Item> TREASURE_BAG = register("treasure_bag");
        public static final TagKey<Item> FAST_BOW = register("fast_bow");
        public static final TagKey<Item> ABLE_TO_DESTROY_ALTAR = register("able_to_destroy_altar");
        public static final TagKey<Item> EXPLOSIVE = register("explosive"); // 爆炸物，用于爆破专家入住
        public static final TagKey<Item> SHOW_SIGNAL = register("show_signal"); // 手持可以显示信号连线
        public static final TagKey<Item> ROBE = register("robe"); // 用于巫师套装
        public static final TagKey<Item> MIMIC_SUMMON_KEY = register("mimic_summon_key"); // 宝箱怪召唤钥匙，用于计数

        public static final TagKey<Item> DEATH = register("death");
        public static final TagKey<Item> UNABLE_TO_APPLY_PREFIX = register("unable_to_apply_prefix"); // 不能上词缀
        public static final TagKey<Item> ANTIGRAVITY = register("antigravity"); // 无视重力
        public static final TagKey<Item> LAVA_PROOF_BAIT = register("lava_proof_bait"); // 防熔岩鱼饵
        public static final TagKey<Item> AUTO_ATTACK_WHITELIST = register("auto_attack_whitelist"); // 自动挥舞白名单
        public static final TagKey<Item> AUTO_ATTACK_BLACKLIST = register("auto_attack_blacklist"); // 自动挥舞黑名单

        public static final TagKey<Item> REPEATER_ENCHANTABLE = register("enchantable/repeater");
        public static final TagKey<Item> REPEATER_CROSSBOW_ENCHANTABLE = common("enchantable/repeater_crossbow");

        private static TagKey<Item> common(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Item> register(String id) {
            return Confluence.asTagKey(Registries.ITEM, id);
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> IS_CONFLUENCE = register("is_confluence");
        public static final TagKey<Biome> IS_FOREST = register("is_forest");
        public static final TagKey<Biome> SPREADABLE = register("spreadable");
        public static final TagKey<Biome> THE_CORRUPTION = register("the_corruption");
        public static final TagKey<Biome> THE_CRIMSON = register("the_crimson");
        public static final TagKey<Biome> THE_HALLOW = register("the_hallow");
        public static final TagKey<Biome> THE_CHORUS = register("the_chorus");
        public static final TagKey<Biome> THE_INVERSE = register("the_inverse");
        public static final TagKey<Biome> THE_MOONBLIGHT = register("the_moonblight");
        public static final TagKey<Biome> THE_END_SEA = register("the_end_sea");
        public static final TagKey<Biome> VANITY_TREES_REPLACEABLE = register("vanity_trees_replaceable");

        private static TagKey<Biome> register(String id) {
            return Confluence.asTagKey(Registries.BIOME, id);
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> FISHING_ABLE = register("fishing_able");
        public static final TagKey<Fluid> NOT_LAVA = register("not_lava");

        private static TagKey<Fluid> register(String id) {
            return Confluence.asTagKey(Registries.FLUID, id);
        }
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> SPAWN_AT_DUNGEON = register("spawn_at_dungeon"); // 允许生成在地牢的生物
        public static final TagKey<EntityType<?>> LAVA_BUG_NET_ALLOWS = register("lava_bug_net_allows"); // 熔岩萤火虫、岩浆蜗牛和地狱蝴蝶; 用于区分普通虫网与防熔岩虫网
        public static final TagKey<EntityType<?>> FEALING_TRANSMUTATION = register("fealing_transmutation"); // 可转化为飞灵的生物
        public static final TagKey<EntityType<?>> SPAWN_AT_GRAVEYARD = register("spawn_at_graveyard"); // 可以生成在墓地的生物
        public static final TagKey<EntityType<?>> DO_NOT_DROPS_EVIL_SOUL = register("do_not_drops_evil_soul"); // 不掉落光明或暗影之魂
        public static final TagKey<EntityType<?>> BESTIARY_WHITELIST = register("bestiary_whitelist"); // 怪物图鉴白名单
        public static final TagKey<EntityType<?>> BESTIARY_BLACKLIST = register("bestiary_blacklist"); // 怪物图鉴黑名单
        public static final TagKey<EntityType<?>> CRITTER_COMPANIONSHIP_WHITELIST = register("critter_companionship_whitelist"); // 小动物保护指南白名单
        public static final TagKey<EntityType<?>> CRITTER_COMPANIONSHIP_BLACKLIST = register("critter_companionship_blacklist"); // 小动物保护指南黑名单
        public static final TagKey<EntityType<?>> ENEMY_BANNER_WHITELIST = register("enemy_banner_whitelist"); // 旗帜白名单
        public static final TagKey<EntityType<?>> ENEMY_BANNER_BLACKLIST = register("enemy_banner_blacklist"); // 旗帜黑名单
        public static final TagKey<EntityType<?>> GORE_EFFECT_BLACKLIST = register("gore_effect_blacklist"); // 肢解效果黑名单
        public static final TagKey<EntityType<?>> NPC_INVULNERABLE_TO_PLAYER = register("npc_invulnerable_to_player"); // 能够免疫玩家伤害的NPC

        private static TagKey<EntityType<?>> register(String id) {
            return Confluence.asTagKey(Registries.ENTITY_TYPE, id);
        }
    }

    public static class Enchantments {
        public static final TagKey<Enchantment> MANA_IO_EXCLUSIVE = register("mana_io_exclusive");
        public static final TagKey<Enchantment> MENDING_EXCLUSIVE = register("mending_exclusive");
        public static final TagKey<Enchantment> MANA_AFFECTIVE_EXCLUSIVE = register("mana_affective_exclusive");
        public static final TagKey<Enchantment> MAGIC_ATTACK_EXCLUSIVE = register("magic_attack_exclusive");

        private static TagKey<Enchantment> register(String id) {
            return Confluence.asTagKey(Registries.ENCHANTMENT, id);
        }
    }

    public static class RecipeSerializers {
        public static final TagKey<RecipeSerializer<?>> AUTOMATION_IGNORE = TagKey.create(Registries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("create", "automation_ignore"));
    }
}
