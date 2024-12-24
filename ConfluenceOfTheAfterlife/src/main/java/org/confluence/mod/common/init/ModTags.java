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

        private static TagKey<Block> register(String id) {
            return BlockTags.create(Confluence.asResource(id));
        }
    }

    public static class Items {
        public static final TagKey<Item> HOOK = curios("hook");
        public static final TagKey<Item> MINECART = curios("minecart");

        public static final TagKey<Item> INGOTS_TIN = neoforgeTag("ingots/tin");
        public static final TagKey<Item> INGOTS_SILVER = neoforgeTag("ingots/silver");
        public static final TagKey<Item> INGOTS_TUNGSTEN = neoforgeTag("ingots/tungsten");
        public static final TagKey<Item> INGOTS_PLATINUM = neoforgeTag("ingots/platinum");

        public static final TagKey<Item> PROVIDE_MANA = register("provide_mana");
        public static final TagKey<Item> PROVIDE_LIFE = register("provide_life");
        public static final TagKey<Item> COIN = register("coin");
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
        public static final TagKey<Item> CORAL = register("coral");
        public static final TagKey<Item> TR_PLANKS = register("tr_planks");
        public static final TagKey<Item> WOODEN_COMBUSTIBLES = register("wooden_combustibles");  // 可燃木材系列
        public static final TagKey<Item> COMPOST = register("compost");  // 堆肥
        public static final TagKey<Item> LEAD_AND_IRON = register("lead_and_iron");
        public static final TagKey<Item> EBONY_AND_CRIMSON_INGOT = register("ebony_and_crimson_ingot");
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
        public static final TagKey<Item> EBONY_ORE_SMELTING = register("ebony_ore_smelting");
        public static final TagKey<Item> TR_CRIMSON_ORE_SMELTING = register("tr_crimson_ore_smelting");
        public static final TagKey<Item> RUBY_ORE_SMELTING = register("ruby_ore_smelting");
        public static final TagKey<Item> TOPAZ_ORE_SMELTING = register("topaz_ore_smelting");
        public static final TagKey<Item> AMBER_ORE_SMELTING = register("amber_ore_smelting");
        public static final TagKey<Item> TR_EMERALD_ORE_SMELTING = register("tr_emerald_ore_smelting");
        public static final TagKey<Item> DIAMOND_ORE_SMELTING = register("diamond_ore_smelting");
        public static final TagKey<Item> SAPPHIRE_ORE_SMELTING = register("sapphire_ore_smelting");
        public static final TagKey<Item> TR_AMETHYST_ORE_SMELTING = register("tr_amethyst_ore_smelting");
        public static final TagKey<Item> REDSTONE_ORE_SMELTING = register("redstone_ore_smelting");


        private static TagKey<Item> curios(String id) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", id));
        }

        private static TagKey<Item> neoforgeTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("neoforge", name));
        }

        private static TagKey<Item> register(String id) {
            return ItemTags.create(Confluence.asResource(id));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> SPREADABLE = register("spreadable");
        public static final TagKey<Biome> THE_CORRUPTION = register("the_corruption");

        private static TagKey<Biome> register(String id) {
            return TagKey.create(Registries.BIOME, Confluence.asResource(id));
        }
    }

    public static final TagKey<Fluid> FISHING_ABLE = FluidTags.create(Confluence.asResource("fishing_able"));
    public static final TagKey<Fluid> NOT_LAVA = FluidTags.create(Confluence.asResource("not_lava"));

    public static final TagKey<DamageType> HARMFUL_EFFECT = TagKey.create(Registries.DAMAGE_TYPE, Confluence.asResource("harmful_effect"));
}
