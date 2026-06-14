package org.confluence.mod.common.event;

import org.confluence.lib.api.event.NameFixRegisterEvent;

public final class RenameEvents {
    public static void blockWithItemNameFixRegister(NameFixRegisterEvent.BlockWithItem event) {
        event
                // 1.1.2 -> 1.1.3
                .register("confluence:freeze_crate", "confluence:frozen_crate")
                .register("confluence:ebony_stone", "confluence:ebonstone")
                .register("confluence:pearl_stone", "confluence:pearlstone")
                .register("confluence:tr_crimson_stone", "confluence:crimstone")
                .register("confluence:ebony_cobblestone", "confluence:cobbled_ebonstone")
                .register("confluence:pearl_cobblestone", "confluence:cobbled_pearlstone")
                .register("confluence:tr_crimson_cobblestone", "confluence:cobbled_crimstone")
                .register("confluence:ebony_sandstone", "confluence:ebonsandstone")
                .register("confluence:tr_crimson_sandstone", "confluence:crimsandstone")
                .register("confluence:pearl_sandstone", "confluence:pearlsandstone")
                .register("confluence:ebony_sand", "confluence:ebonsand")
                .register("confluence:pearl_sand", "confluence:pearlsand")
                .register("confluence:crimson_sand", "confluence:crimsand")
                .register("confluence:ebony_sand_layer_block", "confluence:ebonsand_layer_block")
                .register("confluence:tr_crimson_sand_layer_block", "confluence:crimsand_layer_block")
                .register("confluence:pearl_sand_layer_block", "confluence:pearlsand_layer_block")
                .register("confluence:red_hardened_sand_block", "confluence:hardened_red_sand_block")
                .register("confluence:ebony_hardened_sand_block", "confluence:hardened_ebonsand_block")
                .register("confluence:pearl_hardened_sand_block", "confluence:hardened_pearlsand_block")
                .register("confluence:tr_crimson_hardened_sand_block", "confluence:hardened_crimsand_block")
                .register("confluence:ebony_moist_sand_block", "confluence:moistened_ebonsand_block")
                .register("confluence:pearl_moist_sand_block", "confluence:moistened_pearlsand_block")
                .register("confluence:tr_crimson_moist_sand_block", "confluence:moistened_crimsand_block")
                .register("confluence:moist_sand_block", "confluence:moistened_sand_block")
                .register("confluence:red_moist_sand_block", "confluence:moistened_red_sand_block")
                .register("confluence:tr_lava_bricks", "confluence:hellstone_bricks")
                .register("confluence:tr_amethyst_ore", "confluence:amethyst_ore")
                .register("confluence:deepslate_tr_amethyst_ore", "confluence:deepslate_amethyst_ore")
                .register("confluence:sanctification_tr_amethyst_ore", "confluence:sanctification_amethyst_ore")
                .register("confluence:corruption_tr_amethyst_ore", "confluence:corruption_amethyst_ore")
                .register("confluence:fleshification_tr_amethyst_ore", "confluence:fleshification_amethyst_ore")
                .register("confluence:tr_crimson_ore", "confluence:crimtane_ore")
                .register("confluence:deepslate_tr_crimson_ore", "confluence:deepslate_crimtane_ore")
                .register("confluence:sanctification_tr_crimson_ore", "confluence:sanctification_crimtane_ore")
                .register("confluence:corruption_tr_crimson_ore", "confluence:corruption_crimtane_ore")
                .register("confluence:fleshification_tr_crimson_ore", "confluence:fleshification_crimtane_ore")
                .register("confluence:raw_tr_crimson_block", "confluence:raw_crimtane_block")
                .register("confluence:tr_crimson_block", "confluence:crimtane_block")
                .register("confluence:tr_crimson_grass_block", "confluence:crimson_grass_block")
                .register("confluence:tr_crimson_jungle_grass_block", "confluence:crimson_jungle_grass_block")
                .register("confluence:tr_crimson_drooping_vine", "confluence:crimson_drooping_vine")
                .register("confluence:tr_crimson_grass", "confluence:crimson_grass")
                .register("confluence:tr_crimson_cattails_body", "confluence:crimson_cattails_body")
                .register("confluence:tr_crimson_cattails_head", "confluence:crimson_cattails_head")
                .register("confluence:tr_crimson_pot", "confluence:crimson_pot")
                .register("confluence:tr_crimson_crate", "confluence:crimson_crate")
                .register("confluence:tr_crimson_cattails", "confluence:crimson_cattails")
                .register("confluence:tr_crimson_ore_bricks", "confluence:crimtane_ore_bricks")
                .register("confluence:tr_crimson_rock_bricks", "confluence:crimstone_bricks")
                .register("confluence:tr_amethyst_branches", "confluence:amethyst_branches")
                .register("confluence:tr_amethyst_sapling", "confluence:amethyst_sapling")
                .register("confluence:tr_amethyst_block", "confluence:amethyst_block")
                .register("confluence:tr_polished_granite", "confluence:polished_granite")
                .register("confluence:tr_copper_bricks", "confluence:copper_bricks")
                .register("confluence:tr_gold_bricks", "confluence:golden_bricks")
                .register("confluence:tr_iron_bricks", "confluence:iron_bricks")
                .register("confluence:ebony_rock_bricks", "confluence:ebonstone_bricks")
                .register("confluence:pearl_rock_bricks", "confluence:pearlstone_bricks")
                .register("confluence:tr_obsidian_bricks", "confluence:obsidian_bricks")
                .register("confluence:tr_obsidian_small_bricks", "confluence:obsidian_small_bricks")
                .register("confluence:tr_smooth_obsidian", "confluence:smooth_obsidian")
                .register("confluence:tr_oak_planks", "confluence:chiseled_oak_planks")
                .register("confluence:tr_northland_planks", "confluence:chiseled_spruce_planks")
                .register("confluence:tr_granite_column", "confluence:granite_column")
                .register("confluence:tr_emerald_ore", "confluence:jade_ore")
                .register("confluence:deepslate_tr_emerald_ore", "confluence:deepslate_jade_ore")
                .register("confluence:sanctification_tr_emerald_ore", "confluence:sanctification_jade_ore")
                .register("confluence:corruption_tr_emerald_ore", "confluence:corruption_jade_ore")
                .register("confluence:fleshification_tr_emerald_ore", "confluence:fleshification_jade_ore")
                .register("confluence:tr_emerald_block", "confluence:jade_block")
                .register("confluence:emerald_branches", "confluence:jade_branches")
                .register("confluence:emerald_sapling", "confluence:jade_sapling")
                .register("confluence:emerald_chain", "confluence:jade_chain")
                // 1.1.4 -> 1.1.5
                .register("confluence:golden_coin", "confluence:gold_coin")
                // 1.1.5 -> 1.2.0
                .register("confluence:cattails_head", "confluence:cattail_block")
                .register("confluence:cattails_body", "confluence:cattail_block")
                .register("confluence:jungle_cattails_head", "confluence:jungle_cattail_block")
                .register("confluence:jungle_cattails_body", "confluence:jungle_cattail_block")
                .register("confluence:glowing_mushroom_cattais_head", "confluence:glowing_mushroom_cattail_block")
                .register("confluence:glowing_mushroom_cattais_body", "confluence:glowing_mushroom_cattail_block")
                .register("confluence:hallow_cattails_head", "confluence:hallow_cattail_block")
                .register("confluence:hallow_cattails_body", "confluence:hallow_cattail_block")
                .register("confluence:ebony_cattails_head", "confluence:ebony_cattail_block")
                .register("confluence:ebony_cattails_body", "confluence:ebony_cattail_block")
                .register("confluence:crimson_cattails_head", "confluence:crimson_cattail_block")
                .register("confluence:crimson_cattails_body", "confluence:crimson_cattail_block");
    }

    public static void blockNameFixRegister(NameFixRegisterEvent.Block event) {
        event
                // 1.1.2 -> 1.1.3
                .register("confluence:copper_coin_pile", "confluence:copper_coin")
                .register("confluence:silver_coin_pile", "confluence:silver_coin")
                .register("confluence:golden_coin_pile", "confluence:golden_coin")
                .register("confluence:platinum_coin_pile", "confluence:platinum_coin")
                .register("confluence:emerald_coin_pile", "confluence:emerald_coin");
    }

    public static void itemNameFixRegister(NameFixRegisterEvent.Item event) {
        event
                // 1.1.2 -> 1.1.3
                .register("confluence:copper_board_sword", "confluence:copper_broadsword")
                .register("confluence:tin_board_sword", "confluence:tin_broadsword")
                .register("confluence:lead_board_sword", "confluence:lead_broadsword")
                .register("confluence:silver_board_sword", "confluence:silver_broadsword")
                .register("confluence:tungsten_board_sword", "confluence:tungsten_broadsword")
                .register("confluence:golden_board_sword", "confluence:golden_broadsword")
                .register("confluence:platinum_board_sword", "confluence:platinum_broadsword")
                .register("confluence:tr_crimson_ingot", "confluence:crimtane_ingot")
                .register("confluence:raw_tr_crimson", "confluence:raw_crimtane")
                .register("confluence:tr_emerald", "confluence:jade")
                .register("confluence:emerald_minecart", "confluence:jade_minecart")
                .register("confluence:emerald_hook", "confluence:jade_hook")
                .register("confluence:emerald_staff", "confluence:jade_staff")
                .register("confluence:tr_amethyst", "confluence:amethyst")
                .register("confluence:tr_crimson_seed", "confluence:crimson_seed")
                .register("confluence:tr_clownfish", "confluence:clownfish")
                .register("confluence:tr_salmon", "confluence:salmon")
                .register("confluence:red_light_saber", "confluence:red_phaseblade")
                .register("confluence:orange_light_saber", "confluence:orange_phaseblade")
                .register("confluence:yellow_light_saber", "confluence:yellow_phaseblade")
                .register("confluence:green_light_saber", "confluence:green_phaseblade")
                .register("confluence:blue_light_saber", "confluence:blue_phaseblade")
                .register("confluence:purple_light_saber", "confluence:purple_phaseblade")
                .register("confluence:white_light_saber", "confluence:white_phaseblade")
                .register("confluence:demon_ocnch", "confluence:demon_conch")
                // 1.1.3 -> 1.1.4
                .register("confluence:night_edge", "confluence:nights_edge")
                // 1.1.4 -> 1.1.5
                .register("confluence:crystal_shards_item", "confluence:crystal_shards")
                .register("confluence:throwing_knives", "confluence:throwing_knive")
                // 1.1.5 -> 1.2.0
                .register("confluence:cap_tunabeard", "confluence:capn_tunabeard")
                .register("confluence:obsidian_fish", "confluence:obsidifish")
                .register("terra_moment:slime_rain", "confluence:slime_rain")
                .register("terra_moment:blood_tear", "confluence:blood_tear")
                .register("terra_moment:goblin_battle_standard", "confluence:goblin_battle_standard")
                .register("confluence:cattails", "confluence:cattail")
                .register("confluence:jungle_cattails", "confluence:jungle_cattail")
                .register("confluence:glowing_mushroom_cattails", "confluence:glowing_mushroom_cattail")
                .register("confluence:hallow_cattails", "confluence:hallow_cattail")
                .register("confluence:ebony_cattails", "confluence:ebony_cattail")
                .register("confluence:crimson_cattails", "confluence:crimson_cattail")
                // 1.2.4 -> 1.3.0
                .register("confluence:blue_brick_slab", "confluence:blue_bricks_slab")
                .register("confluence:blue_brick_stairs", "confluence:blue_bricks_stairs")
        ;
    }

    public static void biomeNameFixRegister(NameFixRegisterEvent.Biome event) {
        event
                // 1.1.2 -> 1.1.3
                .register("confluence:tr_crimson", "confluence:the_crimson")
                .register("confluence:tr_crimson_desert", "confluence:the_crimson_desert")
                .register("confluence:tr_crimson_tundra", "confluence:the_crimson_tundra");
    }
}
