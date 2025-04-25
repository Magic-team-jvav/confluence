package org.confluence.mod.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.mixin.accessor.DataMapProviderAccessor;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        valueBuilder()
            .add(Items.NETHERITE_SWORD, 6000)
            .add(Items.NETHERITE_PICKAXE, 6000)
            .add(Items.NETHERITE_AXE, 6000)

            .add(Items.NETHERITE_AXE, 3000)
            .add(Items.DIAMOND_SWORD, 3000)
            .add(Items.DIAMOND_AXE, 3000)

            .add(Items.GOLDEN_SWORD, 2800)
            .add(Items.GOLDEN_SWORD, 2800)
            .add(Items.GOLDEN_AXE, 2800)

            .add("minecraft:iron_sword", 2800)
            .add("minecraft:iron_axe", 2800)

            .add("minecraft:copper_ingot", 150)
            .add("minecraft:iron_ingot", 300)
            .add("minecraft:gold_ingot", 1200)
            .add("minecraft:netherite_ingot", 4000)

            .add("confluence:tin_ingot", 225)
            .add("confluence:lead_ingot", 450)
            .add("confluence:silver_ingot", 600)
            .add("confluence:tungsten_ingot", 900)
            .add("confluence:platinum_ingot", 1800)
            .add("confluence:meteorite_ingot", 1400)
            .add("confluence:demonite_ingot", 3000)
            .add("confluence:tr_crimson_ingot", 3900)
            .add("confluence:hellstone_ingot", 4000)

            .add("confluence:cobalt_ingot", 2100)
            .add("confluence:palladium_ingot", 2700)
            .add("confluence:mythril_ingot", 4400)
            .add("confluence:orichalcum_ingot", 5200)
            .add("confluence:adamantite_ingot", 6000)
            .add("confluence:titanium_ingot", 6800)
            .add("confluence:hallowed_ingot", 4000)
            .add("confluence:chlorophyte_ingot", 9000)
            .add("confluence:shroomite_ingot", 10000)
            .add("confluence:spectre_ingot", 10000)
            .add("confluence:luminite_ingot", 12000)

            .add("minecraft:raw_copper", 50)
            .add("minecraft:raw_iron", 100)
            .add("minecraft:raw_gold", 300)

            .add("confluence:raw_tin", 75)
            .add("confluence:raw_lead", 150)
            .add("confluence:raw_silver", 150)
            .add("confluence:raw_tungsten", 225)
            .add("confluence:raw_platinum", 450)
            .add("confluence:raw_meteorite", 200)
            .add("confluence:raw_demonite", 1000)
            .add("confluence:raw_tr_crimson", 1300)
            .add("confluence:raw_hellstone", 250)

            .add("confluence:raw_cobalt", 700)
            .add("confluence:raw_palladium", 900)
            .add("confluence:raw_mythril", 1100)
            .add("confluence:raw_orichalcum", 1300)
            .add("confluence:raw_adamantite", 1500)
            .add("confluence:raw_titanium", 1700)
            .add("confluence:raw_chlorophyte", 1500)
            .add("confluence:raw_luminite", 3000)

            .add("confluence:gel", 1)
            .add("confluence:pink_gel", 3)
            .add("confluence:silk", 200)
            .add("confluence:raw_asphalt", 1)

            .add("confluence:volcano", 5400)
            .add("confluence:blood_butcherer", 2700)
            .add("confluence:lights_bane", 2700)
            .add("confluence:enchanted_sword", 30000)
            .add("confluence:terragrim", 50000)
            .add("confluence:ice_blade", 4000)
            .add("confluence:cactus_sword", 360)
            .add("confluence:zombie_arm", 2800)
            .add("confluence:mandible_blade", 7000)
            .add("confluence:copper_short_sword", 210)
            .add("confluence:copper_board_sword", 630)
            .add("confluence:tin_short_sword", 315)
            .add("confluence:tin_board_sword", 945)

            .add("terra_entity:flamarang", 20000)
            .add("terra_entity:trimarang", 20000)
            .add("terra_entity:ice_boomerang", 10000)
            .add("terra_entity:shroomerang", 6000)
            .add("terra_entity:enchanted_boomerang", 10000)
            .add("terra_entity:wood_boomerang", 10000)
            .add("confluence:stylish_scissors", 350)

            .add("confluence:molten_fury", 5400)
            .add("confluence:tendon_bow", 3600)
            .add("confluence:demon_bow", 3600)
            .add("confluence:fossil_bow", 3000)
            .add("confluence:hunting_bow", 3800)
            .add("confluence:platinum_bow", 2100)
            .add("confluence:platinum_short_bow", 2100)
            .add("confluence:golden_bow", 1400)
            .add("confluence:golden_short_bow", 1400)
            .add("confluence:tungsten_bow", 1050)
            .add("confluence:tungsten_short_bow", 1050)
            .add("confluence:silver_bow", 700)
            .add("confluence:silver_short_bow", 700)
            .add("confluence:lead_bow", 420)
            .add("confluence:lead_short_bow", 420)
            .add("confluence:iron_bow", 280)
            .add("confluence:iron_short_bow", 280)
            .add("confluence:tin_bow", 150)
            .add("confluence:tin_short_bow", 150)
            .add("confluence:copper_bow", 70)
            .add("confluence:copper_short_bow", 70)
            .add("confluence:wooden_short_bow", 40)
            .add("minecraft:bow", 40)

            .add("confluence:molten_pickaxe", 5400)
            .add("confluence:deathbringer_pickaxe", 3600)
            .add("confluence:nightmare_pickaxe", 3600)
            .add("confluence:platinum_pickaxe", 3000)
            .add("confluence:golden_pickaxe", 2000)
            .add("confluence:tungsten_pickaxe", 1500)
            .add("confluence:silver_pickaxe", 1000)
            .add("confluence:lead_pickaxe", 600)
            .add("minecraft:iron_pickaxe", 400)
            .add("confluence:tin_pickaxe", 150)
            .add("confluence:copper_pickaxe", 50)

            .add("confluence:the_breaker", 3000)
            .add("confluence:flesh_grinder", 3000)
            .add("confluence:platinum_hammer", 2400)
            .add("confluence:golden_hammer", 1600)
            .add("confluence:tungsten_hammer", 1200)
            .add("confluence:silver_hammer", 800)
            .add("confluence:lead_hammer", 480)
            .add("confluence:iron_hammer", 320)
            .add("confluence:tin_hammer", 120)
            .add("confluence:copper_hammer", 80)
            .add("confluence:wooden_hammer", 10)

            .add("confluence:war_axe_of_the_night", 2700)
            .add("confluence:blood_lust_cluster", 2700)
            .add("confluence:platinum_axe", 1200)
            .add("confluence:golden_axe", 800)
            .add("confluence:tungsten_axe", 600)
            .add("confluence:silver_axe", 400)
            .add("confluence:lead_axe", 240)
            .add("confluence:iron_axe", 160)
            .add("confluence:tin_axe", 60)
            .add("confluence:copper_axe", 40)
            .add("minecraft:wooden_axe", 10)

            .add("confluence:ruby_staff", 2800)
            .add("confluence:amber_staff", 2800)
            .add("confluence:topaz_staff", 2800)
            .add("confluence:emerald_staff", 2800)
            .add("confluence:sapphire_staff", 2800)
            .add("confluence:diamond_staff", 2800)
            .add("confluence:amethyst_staff", 2800)
            .add("confluence:wand_of_sparking", 10000)
            .add("confluence:wand_of_frosting", 3500)
            .add("confluence:thunder_zapper", 2100)
            .add("confluence:vilethron", 15000)
            .add("confluence:weather_pain", 15000)
            .add("confluence:aqua_scepter", 17500)
            .add("confluence:flower_of_fire", 25000)
            .add("confluence:water_bolt", 15000)


            .add("confluence:bee_gun", 20000)
            .add("confluence:space_gun", 4000)

            .add("terra_curio:aglet", 25000)
            .add("terra_curio:amber_horseshoe_balloon", 30000)
            .add("terra_curio:ambhipian_boots", 20000)
            .add("terra_curio:ancient_chisel", 10000)
            .add("terra_curio:angler_earring", 10000)
            .add("terra_curio:ankh_charm", 30000)
            .add("terra_curio:ankh_shield", 50000)
            .add("terra_curio:anklet_of_the_wind", 10000)
            .add("terra_curio:architect_gizmo_pack", 40000)
            .add("terra_curio:arctic_diving_gear", 50000)
            .add("terra_curio:avenger_emblem", 60000)
            .add("terra_curio:balloon_pufferfish", 25000)
            .add("terra_curio:band_of_regeneration", 10000)
            .add("terra_curio:base_point", 60000)
            .add("terra_curio:bee_cloak", 30000)
            .add("terra_curio:berserkers_glove", 100000)
            .add("terra_curio:bezoar", 20000)
            .add("terra_curio:black_belt", 30000)
            .add("terra_curio:blindfold", 20000)
            .add("terra_curio:blizzard_in_a_balloon", 30000)
            .add("terra_curio:blizzard_in_a_bottle", 10000)
            .add("terra_curio:blue_horseshoe_balloon", 30000)
            .add("terra_curio:brain_of_confusion", 20000)
            .add("terra_curio:brick_layer", 100000)
            .add("terra_curio:bundle_of_balloons", 30000)
            .add("terra_curio:bundle_of_horseshoe_balloons", 40000)
            .add("terra_curio:celestial_shell", 140000)
            .add("terra_curio:celestial_starboard", 100000)
            .add("terra_curio:celestial_stone", 80000)
            .add("terra_curio:cell_phone", 80000)
            .add("terra_curio:climbing_claws", 25000)
            .add("terra_curio:cloud_in_a_balloon", 30000)
            .add("terra_curio:cloud_in_a_bottle", 10000)
            .add("terra_curio:cobalt_shield", 17500)
            .add("terra_curio:compass", 1750)
            .add("terra_curio:copper_watch", 140)
            .add("terra_curio:cross_necklace", 20000)
            .add("terra_curio:demon_heart", 20000)
            .add("terra_curio:depth_meter", 1750)
            .add("terra_curio:destroyer_emblem", 60000)
            .add("terra_curio:detoxification_capsule", 25000)
            .add("terra_curio:diving_gear", 20000)
            .add("terra_curio:diving_helmet", 1400)
            .add("terra_curio:dps_meter", 50000)
            .add("terra_curio:dunerider_boots", 10000)
            .add("terra_curio:energy_bar", 20000)
            .add("terra_curio:everlasting", 140000)
            .add("terra_curio:explorers_equipment", 30000)
            .add("terra_curio:extendo_grip", 100000)
            .add("terra_curio:eye_of_the_golem", 50000)
            .add("terra_curio:fairy_boots", 60000)
            .add("terra_curio:fart_in_a_balloon", 30000)
            .add("terra_curio:fart_in_a_jar", 10000)
            .add("terra_curio:fast_clock", 20000)
            .add("terra_curio:feral_claws", 10000)
            .add("terra_curio:fire_gauntlet", 60000)
            .add("terra_curio:fish_finder", 30000)
            .add("terra_curio:fishermans_pocket_guide", 30000)
            .add("terra_curio:flashlight", 20000)
            .add("terra_curio:flesh_knuckles", 80000)
            .add("terra_curio:flipper", 140)
            .add("terra_curio:flower_boots", 60000)
            .add("terra_curio:flurry_boots", 10000)
            .add("terra_curio:flying_carpet", 10000)
            .add("terra_curio:frog_flipper", 20000)
            .add("terra_curio:frog_gear", 50000)
            .add("terra_curio:frog_leg", 10000)
            .add("terra_curio:frog_webbing", 20000)
            .add("terra_curio:frostspark_boots", 70000)
            .add("terra_curio:frozen_shield", 80000)
            .add("terra_curio:frozen_turtle_shell", 45000)
            .add("terra_curio:goblin_tech", 30000)
            .add("terra_curio:gold_watch", 1400)
            .add("terra_curio:gps", 30000)
            .add("terra_curio:gravity_globe", 400000)
            .add("terra_curio:green_horseshoe_balloon", 30000)
            .add("terra_curio:hand_drill", 20000)
            .add("terra_curio:hand_of_creation", 80000)
            .add("terra_curio:hand_warmer", 10000)
            .add("terra_curio:hermes_boots", 10000)
            .add("terra_curio:hero_shield", 100000)
            .add("terra_curio:hive_pack", 20000)
            .add("terra_curio:holy_water", 20000)
            .add("terra_curio:honey_balloon", 20000)
            .add("terra_curio:honey_comb", 20000)
            .add("terra_curio:ice_skates", 10000)
            .add("terra_curio:inner_tube", 140)
            .add("terra_curio:jellyfish_diving_gear", 30000)
            .add("terra_curio:jellyfish_necklace", 10000)
            .add("terra_curio:lava_charm", 60000)
            .add("terra_curio:lava_waders", 100000)
            .add("terra_curio:life_form_analyzer", 100000)
            .add("terra_curio:lightning_boots", 60000)
            .add("terra_curio:lucky_horseshoe", 3500)
            .add("terra_curio:magic_mirror", 10000)
            .add("terra_curio:magic_quiver", 50000)
            .add("terra_curio:magiluminescence", 10000)
            .add("terra_curio:magma_skull", 25000)
            .add("terra_curio:magma_stone", 20000)
            .add("terra_curio:master_ninja_gear", 100000)
            .add("terra_curio:mechanical_glove", 50000)
            .add("terra_curio:metal_detector", 10000)
            .add("terra_curio:molten_charm", 75000)
            .add("terra_curio:molten_quiver", 75000)
            .add("terra_curio:molten_skull_rose", 50000)
            .add("terra_curio:moon_charm", 30000)
            .add("terra_curio:moon_shell", 80000)
            .add("terra_curio:moon_stone", 75000)
            .add("terra_curio:neptunes_shell", 75000)
            .add("terra_curio:nutrient_solution", 30000)
            .add("terra_curio:obsidian_horseshoe", 12000)
            .add("terra_curio:obsidian_rose", 20000)
            .add("terra_curio:obsidian_shield", 20000)
            .add("terra_curio:obsidian_skull", 3500)
            .add("terra_curio:obsidian_skull_rose", 30000)
            .add("terra_curio:obsidian_water_walking_boots", 60000)
            .add("terra_curio:paladins_shield", 60000)
            .add("terra_curio:panic_necklace", 15000)
            .add("terra_curio:pda", 50000)
            .add("terra_curio:pink_horseshoe_balloon", 30000)
            .add("terra_curio:platinum_watch", 2100)
            .add("terra_curio:portable_cement_mixer", 100000)
            .add("terra_curio:power_glove", 40000)
            .add("terra_curio:putrid_scent", 80000)
            .add("terra_curio:radar", 25000)
            .add("terra_curio:ranger_emblem", 20000)
            .add("terra_curio:recon_scope", 100000)
            .add("terra_curio:rek_3000", 30000)
            .add("terra_curio:rifle_scope", 30000)
            .add("terra_curio:rocket_boots", 50000)
            .add("terra_curio:royal_gel", 20000)
            .add("terra_curio:sailfish_boots", 10000)
            .add("terra_curio:sandstorm_in_a_balloon", 30000)
            .add("terra_curio:sandstorm_in_a_bottle", 10000)
            .add("terra_curio:searchlight", 20000)
            .add("terra_curio:sextant", 10000)
            .add("terra_curio:shackle", 140)
            .add("terra_curio:shark_tooth_necklace", 10000)
            .add("terra_curio:sharkron_balloon", 30000)
            .add("terra_curio:shield_of_cthulhu", 20000)
            .add("terra_curio:shiny_red_balloon", 15000)
            .add("terra_curio:shiny_stone", 50000)
            .add("terra_curio:shoe_spikes", 10000)
            .add("terra_curio:shot_put", 20000)
            .add("terra_curio:silver_watch", 700)
            .add("terra_curio:sniper_scope", 60000)
            .add("terra_curio:soaring_insignia", 100000)
            .add("terra_curio:sorcerer_emblem", 20000)
            .add("terra_curio:spectre_boots", 20000)
            .add("terra_curio:stalkers_quiver", 100000)
            .add("terra_curio:star_cloak", 20000)
            .add("terra_curio:star_veil", 20000)
            .add("terra_curio:step_stool", 25000)
            .add("terra_curio:stinger_necklace", 30000)
            .add("terra_curio:stopwatch", 50000)
            .add("terra_curio:sun_stone", 60000)
            .add("terra_curio:sweetheart_necklace", 20000)
            .add("terra_curio:tabi", 30000)
            .add("terra_curio:tally_counter", 10000)
            .add("terra_curio:terraspark_boots", 150000)
            .add("terra_curio:the_plan", 20000)
            .add("terra_curio:tiger_climbing_gear", 10000)
            .add("terra_curio:tin_watch", 210)
            .add("terra_curio:titan_glove", 20000)
            .add("terra_curio:toolbelt", 100000)
            .add("terra_curio:toolbox", 100000)
            .add("terra_curio:treasure_magnet", 40000)
            .add("terra_curio:trifold_map", 20000)
            .add("terra_curio:tsunami_in_a_bottle", 10000)
            .add("terra_curio:tungsten_watch", 1050)
            .add("terra_curio:vitamins", 20000)
            .add("terra_curio:warrior_emblem", 20000)
            .add("terra_curio:water_walking_boots", 40000)
            .add("terra_curio:weather_radio", 10000)
            .add("terra_curio:white_horseshoe_balloon", 30000)
            .add("terra_curio:worm_scarf", 20000)
            .add("terra_curio:yellow_horseshoe_balloon", 30000);
    }

    public ValueBuilder valueBuilder() {
        return (ValueBuilder) ((DataMapProviderAccessor) this).getBuilders().computeIfAbsent(ModDataMaps.VALUE, k -> new ValueBuilder());
    }

    public static class ValueBuilder extends DataMapProvider.Builder<ValueComponent, Item> {
        public ValueBuilder() {
            super(ModDataMaps.VALUE);
        }

        public ValueBuilder add(ItemLike itemLike, int value) {
            return (ValueBuilder) super.add(itemLike.asItem().builtInRegistryHolder(), new ValueComponent(value), false);
        }

        @Deprecated
        public ValueBuilder add(String id, int value) {
            return add(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)), value);
        }
    }

    /**
     * 将钱币数转成cost整数
     *
     * @param a 铂金
     * @param b 金
     * @param c 银
     * @param d 铜
     */
    private int warp(int a, int b, int c, int d) {
        return a * 1000000 + b * 10000 + c * 100 + d;
    }

    /**
     * 将钱币数转成cost整数
     *
     * @param b 金
     * @param c 银
     * @param d 铜
     */
    private int warp(int b, int c, int d) {
        return b * 10000 + c * 100 + d;
    }

    /**
     * 将钱币数转成cost整数
     *
     * @param c 银
     * @param d 铜
     */
    private int warp(int c, int d) {
        return c * 100 + d;
    }
}