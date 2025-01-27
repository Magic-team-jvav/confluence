package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
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
        public static final TagKey<Block> TORCH = register("torch");
        public static final TagKey<Block> HARDMODE = register("hardmode");
        public static final TagKey<Block> COIN_PILE = register("coin_pile");
        public static final TagKey<Block> EASY_CRASH = register("easy_crash");
        public static final TagKey<Block> VINES = register("vines");
        public static final TagKey<Block> MINEABLE_WITH_PICKAXE_AXE = register("mineable_with_pickaxe_axe");
        public static final TagKey<Block> DROOPING_VINE_CAN_SURVIVE = register("drooping_vine_can_survive");
        public static final TagKey<Block> JEWELLERY_BRANCHES_ATTACHABLE = register("jewellery_branches_attachable");
        public static final TagKey<Block> ASH_LOG_BRANCHES_ATTACHABLE = register("ash_log_branches_attachable");
        public static final TagKey<Block> TOMBSTONE = register("tombstone");
        public static final TagKey<Block> ROPE = register("rope");

        private static TagKey<Block> c(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
        private static TagKey<Block> register(String id) {
            return BlockTags.create(Confluence.asResource(id));
        }
    }

    public static class Items {
        // neoforge
        public static final TagKey<Item> INGOTS_TIN = c("ingots/tin");
        public static final TagKey<Item> INGOTS_SILVER = c("ingots/silver");
        public static final TagKey<Item> INGOTS_TUNGSTEN = c("ingots/tungsten");
        public static final TagKey<Item> INGOTS_PLATINUM = c("ingots/platinum");

        public static final TagKey<Item> COINS = register("coins");
        public static final TagKey<Item> AMMO = register("ammo");
        public static final TagKey<Item> PET = register("pet");
        public static final TagKey<Item> LIGHT_PET = register("light_pet");
        public static final TagKey<Item> MINECART = register("minecart");
        public static final TagKey<Item> HOOK = register("hook");
        public static final TagKey<Item> DYE = register("dye");

        public static final TagKey<Item> PROVIDE_MANA = register("provide_mana");
        public static final TagKey<Item> PROVIDE_LIFE = register("provide_life");
        public static final TagKey<Item> TORCH = register("torch"); // todo
        public static final TagKey<Item> HARDMODE = register("hardmode"); // todo
        public static final TagKey<Item> BOTTOMLESS = register("bottomless");
        public static final TagKey<Item> FRUIT = register("fruit");
        public static final TagKey<Item> SAPLING = register("sapling");
        public static final TagKey<Item> DESERT_FOSSIL = register("desert_fossil");
        public static final TagKey<Item> GRAVEL = register("gravel");
        public static final TagKey<Item> JUNK = register("junk");
        public static final TagKey<Item> SLUSH = register("slush");
        public static final TagKey<Item> MARINE_GRAVEL = register("marine_gravel");
        public static final TagKey<Item> CORALS = register("corals");
        public static final TagKey<Item> EVIL_MATERIAL = register("evil_material");
        public static final TagKey<Item> WOODEN_COMBUSTIBLES = register("wooden_combustibles");  // 可燃木材系列
        public static final TagKey<Item> CHARCOAL_CAN_BE_BURNED = register("charcoal_can_be_burned");  // 可烧成木炭
        public static final TagKey<Item> LEAD_AND_IRON = register("lead_and_iron");
        public static final TagKey<Item> DEMONITE_AND_CRIMSON_INGOT = register("demonite_and_crimson_ingot");
        public static final TagKey<Item> HAMMER = register("hammer");
        public static final TagKey<Item> MANA_WEAPON = register("mana_weapon");
        public static final TagKey<Item> PREFIX_UNIVERSAL_ONLY = register("prefix_universal_only");
        public static final TagKey<Item> HARDMODE_ORES = register("hardmode_ores");
        public static final TagKey<Item> WINGS = register("wings");
        public static final TagKey<Item> BOSS_SUMMING = register("boss_summing");
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
        public static final TagKey<Item> TR_CRIMSON_ORE_SMELTING = register("tr_crimson_ore_smelting");
        public static final TagKey<Item> RUBY_ORE_SMELTING = register("ruby_ore_smelting");
        public static final TagKey<Item> TOPAZ_ORE_SMELTING = register("topaz_ore_smelting");
        public static final TagKey<Item> AMBER_ORE_SMELTING = register("amber_ore_smelting");
        public static final TagKey<Item> TR_EMERALD_ORE_SMELTING = register("tr_emerald_ore_smelting");
        public static final TagKey<Item> DIAMOND_ORE_SMELTING = register("diamond_ore_smelting");
        public static final TagKey<Item> SAPPHIRE_ORE_SMELTING = register("sapphire_ore_smelting");
        public static final TagKey<Item> TR_AMETHYST_ORE_SMELTING = register("tr_amethyst_ore_smelting");
        public static final TagKey<Item> REDSTONE_ORE_SMELTING = register("redstone_ore_smelting");
        public static final TagKey<Item> MOSS_ITEM = register("moss_item");
        public static final TagKey<Item> SUMMONER_WEAPON = register("summoner_weapon");
        public static final TagKey<Item> CROP_FORTUNE = register("crop_fortune");
        public static final TagKey<Item> TREASURE_BAG = register("treasure_bag");

        private static TagKey<Item> c(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        private static TagKey<Item> register(String id) {
            return ItemTags.create(Confluence.asResource(id));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> IS_CONFLUENCE = register("is_confluence");
        public static final TagKey<Biome> SPREADABLE = register("spreadable");
        public static final TagKey<Biome> THE_CORRUPTION = register("the_corruption");
        public static final TagKey<Biome> TR_CRIMSON = register("tr_crimson");
        public static final TagKey<Biome> THE_HALLOW = register("the_hallow");
        public static final TagKey<Biome> VANITY_TREES_REPLACEABLE = register("vanity_trees_replaceable");

        private static TagKey<Biome> register(String id) {
            return TagKey.create(Registries.BIOME, Confluence.asResource(id));
        }
    }

    public static final TagKey<Fluid> FISHING_ABLE = FluidTags.create(Confluence.asResource("fishing_able"));
    public static final TagKey<Fluid> NOT_LAVA = FluidTags.create(Confluence.asResource("not_lava"));

    public static final TagKey<DamageType> HARMFUL_EFFECT = TagKey.create(Registries.DAMAGE_TYPE, Confluence.asResource("harmful_effect"));
}
