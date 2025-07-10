package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
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
        public static final TagKey<Block> TORCH = register("torch"); // todo
        public static final TagKey<Block> HARDMODE = register("hardmode"); // todo
        public static final TagKey<Block> COINS = register("coins");
        public static final TagKey<Block> EASY_CRASH = register("easy_crash");
        public static final TagKey<Block> VINES = register("vines");
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
        public static final TagKey<Block> TOMBSTONE = register("tombstone");
        public static final TagKey<Block> ROPE = register("rope");
        public static final TagKey<Block> MINEABLE_WITH_HAMMER = register("mineable_with_hammer"); // 使用锤子挖掘更快
        public static final TagKey<Block> MINEABLE_WITH_HAMAXE = register("mineable_with_hamaxe"); // 锤斧
        public static final TagKey<Block> MINEABLE_WITH_HOE_SHOVEL = register("mineable_with_hoe_shovel"); // 锄锹
        public static final TagKey<Block> UNBREAKABLE_IF_CANNOT_HARVEST = register("unbreakable_if_cannot_harvest");
        public static final TagKey<Block> HALLOW_CONVERSION_GRASS_BLOCK = register("hallow_conversion_grass_block");
        public static final TagKey<Block> HALLOW_CONVERSION_JUNGLE_GRASS_BLOCK = register("hallow_conversion_jungle_grass_block");
        public static final TagKey<Block> HALLOW_CONVERSION_SHORT_GRASS = register("hallow_conversion_short_grass");
        public static final TagKey<Block> HALLOW_CONVERSION_PACKED_ICE = register("hallow_conversion_packed_ice");
        public static final TagKey<Block> HALLOW_CONVERSION_ICE = register("hallow_conversion_ice");
        public static final TagKey<Block> HALLOW_CONVERSION_SAND = register("hallow_conversion_sand");
        public static final TagKey<Block> HALLOW_CONVERSION_SANDSTONE = register("hallow_conversion_sandstone");
        public static final TagKey<Block> HALLOW_CONVERSION_HARDENED_SAND_BLOCK = register("hallow_conversion_hardened_sand_block");
        public static final TagKey<Block> HALLOW_CONVERSION_MOIST_SAND_BLOCK = register("hallow_conversion_moist_sand_block");
        public static final TagKey<Block> CURSED_FLAME_BASE_BLOCK = register("cursed_flame_base_block");
        public static final TagKey<Block> BLOODTHIRST_CRYSTALL_BASE_BLOCK = register("bloodthirst_crystall_base_block");
        public static final TagKey<Block> CORRODED_WORM_ROOTS_BASE_BLOCK = register("corroded_worm_roots_base_block");
        public static final TagKey<Block> DECOMPOSE_THE_SOURCE_EXTRACT_BASE_BLOCK = register("decompose_the_source_extract_base_block");

        public static final TagKey<Block> TIN_BLOCK = c("storage_blocks/tin");
        public static final TagKey<Block> LEAD_BLOCK = c("storage_blocks/lead");
        public static final TagKey<Block> SLIVER_BLOCK = c("storage_blocks/silver");
        public static final TagKey<Block> TUNGSTEN_BLOCK = c("storage_blocks/tungsten");
        public static final TagKey<Block> PLATINUM_BLOCK = c("storage_blocks/platinum");
        public static final TagKey<Block> METEORITE_BLOCK = c("storage_blocks/meteorite");
        public static final TagKey<Block> DEMONITE_BLOCK = c("storage_blocks/demonite");
        public static final TagKey<Block> CRIMTANE_BLOCK = c("storage_blocks/crimtane");
        public static final TagKey<Block> HELLSTONE_BLOCK = c("storage_blocks/hellstone");
        public static final TagKey<Block> RAW_TIN_BLOCK = c("storage_blocks/raw_tin");
        public static final TagKey<Block> RAW_LEAD_BLOCK = c("storage_blocks/raw_lead");
        public static final TagKey<Block> RAW_SLIVER_BLOCK = c("storage_blocks/raw_silver");
        public static final TagKey<Block> RAW_TUNGSTEN_BLOCK = c("storage_blocks/raw_tungsten");
        public static final TagKey<Block> RAW_PLATINUM_BLOCK = c("storage_blocks/raw_platinum");
        public static final TagKey<Block> RAW_METEORITE_BLOCK = c("storage_blocks/raw_meteorite");
        public static final TagKey<Block> RAW_DEMONITE_BLOCK = c("storage_blocks/raw_demonite");
        public static final TagKey<Block> RAW_CRIMTANE_BLOCK = c("storage_blocks/raw_crimtane");
        public static final TagKey<Block> RAW_HELLSTONE_BLOCK = c("storage_blocks/raw_hellstone");

        private static TagKey<Block> c(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Block> register(String id) {
            return BlockTags.create(Confluence.asResource(id));
        }
    }

    public static class Items {
        public static final TagKey<Item> INGOTS_TIN = c("ingots/tin");
        public static final TagKey<Item> TIN_BLOCK = c("storage_blocks/tin");
        public static final TagKey<Item> INGOTS_LEAD = c("ingots/lead");
        public static final TagKey<Item> LEAD_BLOCK = c("storage_blocks/lead");
        public static final TagKey<Item> INGOTS_SILVER = c("ingots/silver");
        public static final TagKey<Item> SILVER_BLOCK = c("storage_blocks/silver");
        public static final TagKey<Item> INGOTS_TUNGSTEN = c("ingots/tungsten");
        public static final TagKey<Item> TUNGSTEN_BLOCK = c("storage_blocks/tungsten");
        public static final TagKey<Item> INGOTS_PLATINUM = c("ingots/platinum");
        public static final TagKey<Item> PLATINUM_BLOCK = c("storage_blocks/platinum");
        public static final TagKey<Item> INGOTS_METEORITE = c("ingots/meteorite");
        public static final TagKey<Item> METEORITE_BLOCK = c("storage_blocks/meteorite");
        public static final TagKey<Item> INGOTS_DEMONITE = c("ingots/demonite");
        public static final TagKey<Item> DEMONITE_BLOCK = c("storage_blocks/demonite");
        public static final TagKey<Item> INGOTS_CRIMTANE = c("ingots/crimtane");
        public static final TagKey<Item> CRIMTANE_BLOCK = c("storage_blocks/crimtane");
        public static final TagKey<Item> INGOTS_HELLSTONE = c("ingots/hellstone");
        public static final TagKey<Item> HELLSTONE_BLOCK = c("storage_blocks/hellstone");

        public static final TagKey<Item> RUBY_BLOCK = c("storage_blocks/ruby");
        public static final TagKey<Item> AMBER_BLOCK = c("storage_blocks/amber");
        public static final TagKey<Item> TOPAZ_BLOCK = c("storage_blocks/topaz");
        public static final TagKey<Item> JADE_BLOCK = c("storage_blocks/jade");
        public static final TagKey<Item> SAPPHIRE_BLOCK = c("storage_blocks/sapphire");
        public static final TagKey<Item> AMETHYST_BLOCK = c("storage_blocks/amethyst");

        public static final TagKey<Item> STURDY_FOSSIL_BLOCK = c("storage_blocks/sturdy_fossil");
        public static final TagKey<Item> OPAL_BLOCK = c("storage_blocks/opal");
        public static final TagKey<Item> GELSTONE_BLOCK = c("storage_blocks/gelstone");
        public static final TagKey<Item> COLD_CRYSTAL_BLOCK = c("storage_blocks/cold_crystal");

        public static final TagKey<Item> RAW_MATERIALS_TIN = c("raw_materials/tin");
        public static final TagKey<Item> RAW_MATERIALS_TIN_BLOCK = c("storage_blocks/raw_tin");
        public static final TagKey<Item> RAW_MATERIALS_LEAD = c("raw_materials/lead");
        public static final TagKey<Item> RAW_MATERIALS_LEAD_BLOCK = c("storage_blocks/raw_lead");
        public static final TagKey<Item> RAW_MATERIALS_SILVER = c("raw_materials/silver");
        public static final TagKey<Item> RAW_MATERIALS_SILVER_BLOCK = c("storage_blocks/raw_silver");
        public static final TagKey<Item> RAW_MATERIALS_TUNGSTEN = c("raw_materials/tungsten");
        public static final TagKey<Item> RAW_MATERIALS_TUNGSTEN_BLOCK = c("storage_blocks/raw_tungsten");
        public static final TagKey<Item> RAW_MATERIALS_PLATINUM = c("raw_materials/platinum");
        public static final TagKey<Item> RAW_MATERIALS_PLATINUM_BLOCK = c("storage_blocks/raw_platinum");
        public static final TagKey<Item> RAW_MATERIALS_METEORITE = c("raw_materials/meteorite");
        public static final TagKey<Item> RAW_MATERIALS_METEORITE_BLOCK = c("storage_blocks/raw_meteorite");
        public static final TagKey<Item> RAW_MATERIALS_DEMONITE = c("raw_materials/demonite");
        public static final TagKey<Item> RAW_MATERIALS_DEMONITE_BLOCK = c("storage_blocks/raw_demonite");
        public static final TagKey<Item> RAW_MATERIALS_CRIMTANE = c("raw_materials/crimtane");
        public static final TagKey<Item> RAW_MATERIALS_CRIMTANE_BLOCK = c("storage_blocks/raw_crimtane");
        public static final TagKey<Item> RAW_MATERIALS_HELLSTONE = c("raw_materials/hellstone");
        public static final TagKey<Item> RAW_MATERIALS_HELLSTONE_BLOCK = c("storage_blocks/raw_hellstone");

        public static final TagKey<Item> GEMS_RUBY = c("gems/ruby");
        public static final TagKey<Item> GEMS_AMBER = c("gems/amber");
        public static final TagKey<Item> GEMS_TOPAZ = c("gems/topaz");
        public static final TagKey<Item> GEMS_JADE = c("gems/jade");
        public static final TagKey<Item> GEMS_SAPPHIRE = c("gems/sapphire");
        public static final TagKey<Item> GEMS_AMETHYST = c("gems/amethyst");

        public static final TagKey<Item> RAW_MATERIALS_STURDY_FOSSIL = c("raw_materials/sturdy_fossil");
        public static final TagKey<Item> RAW_MATERIALS_OPAL = c("raw_materials/opal");
        public static final TagKey<Item> RAW_MATERIALS_GELSTONE = c("raw_materials/gelstone");
        public static final TagKey<Item> RAW_MATERIALS_COLD_CRYSTAL = c("raw_materials/cold_crystal");

        public static final TagKey<Item> NUGGETS_TIN = c("nuggets/lead");

        public static final TagKey<Item> ORES_TIN = c("ores/tin");
        public static final TagKey<Item> ORES_LEAD = c("ores/lead");
        public static final TagKey<Item> ORES_SILVER = c("ores/silver");
        public static final TagKey<Item> ORES_TUNGSTEN = c("ores/tungsten");
        public static final TagKey<Item> ORES_PLATINUM = c("ores/platinum");

        public static final TagKey<Item> HAMMERS = c("tools/hammers");
        public static final TagKey<Item> LANCES = c("tools/lances");
        public static final TagKey<Item> WEAPONS = c("weapons");

        public static final TagKey<Item> COINS = register("coins");
        public static final TagKey<Item> AMMO = register("ammo");
        public static final TagKey<Item> PET = register("pet");
        public static final TagKey<Item> LIGHT_PET = register("light_pet");
        public static final TagKey<Item> MINECART = register("minecart");
        public static final TagKey<Item> HOOK = register("hook");
        public static final TagKey<Item> DYE = register("dye");

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
        public static final TagKey<Item> CORALS = register("corals");
        public static final TagKey<Item> EVIL_MATERIAL = register("evil_material");
        public static final TagKey<Item> SEAFOOD_DINNER_MATERIALS = register("seafood_dinner_materials");// 制作海鲜大餐
        public static final TagKey<Item> GOLD_COOKING = register("gold_cooking");// 制作金美味
        public static final TagKey<Item> WOODEN_COMBUSTIBLES = register("wooden_combustibles");  // 可燃木材系列
        public static final TagKey<Item> INITIAL_WOOD = register("initial_wood");  // 木套tag，用于合成木套而分离其他套装
        public static final TagKey<Item> QUESTED_FISHES = register("quested_fishes");  // 任务鱼
        public static final TagKey<Item> EMBLEM = register("emblem");  // 徽章
        public static final TagKey<Item> LEAD_AND_IRON = register("lead_and_iron");
        public static final TagKey<Item> SHADOW_SCALE_AND_TISSUE_SAMPLE = register("shadow_scale_and_tissue_sample");
        public static final TagKey<Item> EVIL_INGOT = register("evil_ingot");
        public static final TagKey<Item> MANA_WEAPON = register("mana_weapon");
        public static final TagKey<Item> PREFIX_UNIVERSAL_ONLY = register("prefix_universal_only");
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
        public static final TagKey<Item> RUBY_ORE_SMELTING = register("ruby_ore_smelting");
        public static final TagKey<Item> TOPAZ_ORE_SMELTING = register("topaz_ore_smelting");
        public static final TagKey<Item> AMBER_ORE_SMELTING = register("amber_ore_smelting");
        public static final TagKey<Item> JADE_ORE_SMELTING = register("jade_ore_smelting");
        public static final TagKey<Item> DIAMOND_ORE_SMELTING = register("diamond_ore_smelting");
        public static final TagKey<Item> EMERALD_ORE_SMELTING = register("emerald_ore_smelting");
        public static final TagKey<Item> SAPPHIRE_ORE_SMELTING = register("sapphire_ore_smelting");
        public static final TagKey<Item> AMETHYST_ORE_SMELTING = register("amethyst_ore_smelting");
        public static final TagKey<Item> REDSTONE_ORE_SMELTING = register("redstone_ore_smelting");
        public static final TagKey<Item> MOSS_ITEM = register("moss_item");
        public static final TagKey<Item> SUMMONER_WEAPON = register("summoner_weapon");
        public static final TagKey<Item> CROP_FORTUNE = register("crop_fortune");
        public static final TagKey<Item> EVIL_KEY = register("evil_key");
        public static final TagKey<Item> TREASURE_BAG = register("treasure_bag");
        public static final TagKey<Item> FAST_BOW = register("fast_bow");
        public static final TagKey<Item> ABLE_TO_DESTROY_ALTAR = register("able_to_destroy_altar");
        public static final TagKey<Item> EXPLOSIVE = register("explosive"); // 爆炸物，用于爆破专家入住
        public static final TagKey<Item> SHOW_SIGNAL = register("show_signal"); // 手持可以显示信号连线

        public static final TagKey<Item> DEATH = register("death");

        private static TagKey<Item> c(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Item> register(String id) {
            return ItemTags.create(Confluence.asResource(id));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> IS_CONFLUENCE = register("is_confluence");
        public static final TagKey<Biome> IS_FOREST = register("is_forest");
        public static final TagKey<Biome> SPREADABLE = register("spreadable");
        public static final TagKey<Biome> THE_CORRUPTION = register("the_corruption");
        public static final TagKey<Biome> THE_CRIMSON = register("the_crimson");
        public static final TagKey<Biome> THE_HALLOW = register("the_hallow");
        public static final TagKey<Biome> VANITY_TREES_REPLACEABLE = register("vanity_trees_replaceable");

        private static TagKey<Biome> register(String id) {
            return Confluence.asTagKey(Registries.BIOME, id);
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> FISHING_ABLE = FluidTags.create(Confluence.asResource("fishing_able"));
        public static final TagKey<Fluid> NOT_LAVA = FluidTags.create(Confluence.asResource("not_lava"));
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> SPAWN_AT_DUNGEON = Confluence.asTagKey(Registries.ENTITY_TYPE, "spawn_at_dungeon"); // 允许生成在地牢的生物
    }

    public static class Enchantments {
        public static final TagKey<Enchantment> MANA_IO_EXCLUSIVE = Confluence.asTagKey(Registries.ENCHANTMENT, "mana_io_exclusive");
        public static final TagKey<Enchantment> MENDING_EXCLUSIVE = Confluence.asTagKey(Registries.ENCHANTMENT, "mending_exclusive");
        public static final TagKey<Enchantment> MANA_AFFECTIVE_EXCLUSIVE = Confluence.asTagKey(Registries.ENCHANTMENT, "mana_affective_exclusive");
        public static final TagKey<Enchantment> MAGIC_ATTACK_EXCLUSIVE = Confluence.asTagKey(Registries.ENCHANTMENT, "magic_attack_exclusive");
    }
}
