package org.confluence.mod.common.data.gen;

import com.google.common.collect.Iterables;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffectStrategies;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.mixin.accessor.LanguageProviderAccessor;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.confluence.mod.common.component.prefix.ModPrefix.*;

public class ModEnglishProvider extends LanguageProvider {
    public ModEnglishProvider(PackOutput output) {
        super(output, Confluence.MODID, "en_us");
    }

    private static String toTitleCase(String raw) {
        return Arrays.stream(raw.split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    @Override
    protected void addTranslations() {
        add("config.jade.plugin_confluence.jade_network_component", "Mechanical Info");

        add("creativetab.confluence.building_blocks", "Confluence | Buildings");
        add("creativetab.confluence.natural_blocks", "Confluence | Naturals");
        add("creativetab.confluence.materials", "Confluence | Materials");
        add("creativetab.confluence.tools", "Confluence | Tools");
        add("creativetab.confluence.warriors", "Confluence | Warriors");
        add("creativetab.confluence.rangers", "Confluence | Rangers");
        add("creativetab.confluence.mages", "Confluence | Mages");
        add("creativetab.confluence.summoners", "Confluence | Summoners");
        add("creativetab.confluence.misc", "Confluence | Miscellaneous");
        add("creativetab.confluence.food_and_potions", "Confluence | Food & Potions");
        add("creativetab.confluence.armors", "Confluence | Armors");
        add("creativetab.terra_curio", "Confluence | Accessories");
        add("creativetab.confluence.mechanical", "Confluence | Mechanical");
        add("creativetab.confluence.developer", "Confluence | Developer");

        add("chat.type.advancement.achievement", "%s has achieved the achievement %s");
        add("options.difficulty.legendary", "§aLegendary");

        add("item.confluence.meteorite_ingot.tooltip", "Warm to the touch");
        add("jukebox_song.confluence.alpha", "C418 - alpha");

        add("bossevent.confluence.boss_generate", "The %s has awakened!");
        add("bossevent.confluence.boss_death", "The %s been defeated!");
        add("bossevent.confluence.cthulhu_eye.leave", "The CthulhuEye leaved!");

        add("item.confluence.afterlife_notes", "Afterlife Notes");
        add("text.confluence.afterlife_notes", "Adventurer, this world is full of challenges and opportunities. Use this journal to uncover its secrets and face its trials. Keep exploring to find power and treasure. Your journey has just begun.                  — The Guide");

        add("worldgen.confluence.placing_traps", "Placing Traps");
        add("worldgen.confluence.generating_bees", "Generating bees");
        add("worldgen.confluence.generating_wavy_caves", "Generating wavy caves");
        add("worldgen.confluence.not_placing_traps", "Not placing traps");
        add("worldgen.confluence.placing_boulders", "Placing Boulders");
        add("secret_seed.the_constant.in_darkness_for_3_second", "It is very dark...you feel in danger...");

        add("info.confluence.weather_radio.clear", "Weather: Clear, Wind Speed: %s");
        add("info.confluence.weather_radio.cloudy", "Weather: Cloudy, Wind Speed: %s");
        add("info.confluence.weather_radio.rain", "Weather: Rain, Wind Speed: %s");
        add("info.confluence.weather_radio.snow", "Weather: Snow, Wind Speed: %s");
        add("info.confluence.weather_radio.thunder", "Weather: Thunder, Wind Speed: %s");
        add("info.confluence.bait", "Bait Power: %s%%");
        add("info.confluence.network", "#%s Signal: %s");
        add("info.confluence.respawn_time", "Respawn Time: ");
        add("info.confluence.second", "s");
        add("info.confluence.drops_money", "dropped");
        add("info.confluence.drops_money.platinum", " %s platinum");
        add("info.confluence.drops_money.gold", " %s gold");
        add("info.confluence.drops_money.silver", " %s silver");
        add("info.confluence.drops_money.copper", " %s copper");

        add("key.confluence.hook", "Throwing Hook");

        add("death.attack.falling_star", "%1$s got a response from a meteor");
        add("death.attack.boulder", "%1$s  is crushed by boulder");
        add("death.attack.thron", "%1$s  discovered that he had become a hedgehog.");

        add("selections.confluence.magic_conch", "Responding to the call of The Ocean [%s]");
        add("selections.confluence.demon_conch", "Responding to the call of The Lava [%s]");

        add("tooltip.item.confluence.adhesive_bandage.0", "Immune to bleeding");
        add("tooltip.item.confluence.medicated_bandage.0", "Immune to poison and bleeding");
        add("tooltip.item.confluence.pocket_mirror.0", "Immune to petrification");
        add("tooltip.item.confluence.reflective_shades.0", "Immune to darkness and petrification");
        add("tooltip.item.confluence.armor_polish.0", "Immune to armor degradation");
        add("tooltip.item.confluence.armor_bracing.0", "Immune to armor degradation and weakness");
        add("tooltip.item.confluence.megaphone.0", "Immune to silence");
        add("tooltip.item.confluence.nazar.0", "Immune to curse");
        add("tooltip.item.confluence.countercurse_mantra.0", "Immune to silence and curse");
        add("tooltip.item.confluence.natures_gift.0", "Reduces mana usage by 6%");
        add("tooltip.item.confluence.mana_flower.0", "Reduces mana usage by 8%");
        add("tooltip.item.terra_curio.mana_flower.1", "Automatically uses mana potions when needed");
        add("tooltip.item.confluence.celestial_magnet.0", "Increases pickup range for mana stars");
        add("tooltip.item.confluence.celestial_emblem.0", "Increases pickup range for mana stars, increases magic damage by 15%");
        add("tooltip.item.confluence.magnet_flower.0", "Reduces mana usage by 8%");
        add("tooltip.item.terra_curio.magnet_flower.1", "Automatically uses mana potions when needed");
        add("tooltip.item.terra_curio.magnet_flower.2", "Increases pickup range for mana stars");
        add("tooltip.item.confluence.arcane_flower.0", "Reduces mana usage by 8%");
        add("tooltip.item.terra_curio.arcane_flower.1", "Automatically uses mana potions when needed");
        add("tooltip.item.terra_curio.arcane_flower.2", "Enemies are less likely to target you");
        add("tooltip.item.confluence.band_of_starpower.0", "Increases maximum mana by 20");
        add("tooltip.item.confluence.mana_regeneration_band.0", "Increases maximum mana by 20");
        add("tooltip.item.terra_curio.mana_regeneration_band.1", "Increases mana regeneration speed");
        add("tooltip.item.confluence.magic_cuffs.0", "Increases maximum mana by 20");
        add("tooltip.item.terra_curio.magic_cuffs.1", "Restores mana when damaged");
        add("tooltip.item.confluence.celestial_cuffs.0", "Increases pickup range for mana stars");
        add("tooltip.item.terra_curio.celestial_cuffs.1", "Restores mana when damaged");
        add("tooltip.item.terra_curio.celestial_cuffs.2", "Increases maximum mana by 20");
        add("tooltip.item.confluence.mana_cloak.0", "Collecting stars restores mana");
        add("tooltip.item.terra_curio.mana_cloak.1", "Reduces mana usage by 8%");
        add("tooltip.item.terra_curio.mana_cloak.2", "Automatically uses mana potions when needed");
        add("tooltip.item.terra_curio.mana_cloak.3", "Stars will fall when you take damage");
        add("tooltip.item.confluence.philosophers_stone.0", "Reduces healing potion cooldown");
        add("tooltip.item.confluence.charm_of_myths.0", "Provides health regeneration, reduces healing potion cooldown");
        add("tooltip.item.confluence.mechanical_lens.0", "Gives enhanced wiring vision");
        add("tooltip.item.confluence.high_test_fishing_line.0", "Fishing line will never break");
        add("tooltip.item.confluence.angler_earring.0", "Increases fishing power");
        add("tooltip.item.confluence.fishing_bobber.0", "Increases fishing power");
        add("tooltip.item.confluence.glowing_fishing_bobber.0", "Increases fishing power, fishing bobber glows");
        add("tooltip.item.confluence.lava_moss_fishing_bobber.0", "Increases fishing power, fishing bobber glows");
        add("tooltip.item.confluence.helium_moss_fishing_bobber.0", "Increases fishing power, fishing bobber glows");
        add("tooltip.item.confluence.neon_moss_fishing_bobber.0", "Increases fishing power, fishing bobber glows");
        add("tooltip.item.confluence.argon_moss_fishing_bobber.0", "Increases fishing power, fishing bobber glows");
        add("tooltip.item.confluence.krypton_moss_fishing_bobber.0", "Increases fishing power, fishing bobber glows");
        add("tooltip.item.confluence.xenon_moss_fishing_bobber.0", "Increases fishing power, fishing bobber glows");
        add("tooltip.item.confluence.tackle_box.0", "Reduces bait consumption rate by 5%");
        add("tooltip.item.confluence.angler_tackle_bag.0", "Fishing line will never break, reduces bait consumption rate by 5%");
        add("tooltip.item.confluence.lavaproof_fishing_hook.0", "Can fish in lava with any bait or rod");
        add("tooltip.item.confluence.lavaproof_tackle_bag.0", "Can fish in lava with any bait or rod");
        add("tooltip.item.terra_curio.lavaproof_tackle_bag.1", "Fishing line will never break, reduces bait consumption rate by 5%");
        add("tooltip.item.confluence.lucky_coin.0", "Hitting enemies may drop extra coins");
        add("tooltip.item.confluence.gold_ring.0", "Increases coin pickup range");
        add("tooltip.item.confluence.discount_card.0", "Reduces shop prices by 20%");
        add("tooltip.item.confluence.coin_ring.0", "Hitting enemies may drop extra coins, increases coin pickup range");
        add("tooltip.item.confluence.greedy_ring.0", "Hitting enemies may drop extra coins, increases coin pickup range, reduces shop prices by 20%");
        add("tooltip.item.confluence.spectre_goggles.0", "Provides ghost vision to interact with echo blocks");
        add("tooltip.item.confluence.guide_to_plant_fiber_cordage.0", "Allows the collection of Vine Rope from vines");
        add("tooltip.item.confluence.summoner_emblem.0", "Increases summoner damage by 15%");
        add("tooltip.item.confluence.apprentices_scarf.0", "Increases sentry limit by 1, increases summoner damage by 10%");
        add("tooltip.item.confluence.huntresss_buckler.0", "Increases sentry limit by 1, increases summoner damage by 10%");
        add("tooltip.item.confluence.monks_belt.0", "Increases sentry limit by 1, increases summoner damage by 10%");
        add("tooltip.item.confluence.squires_shield.0", "Increases sentry limit by 1, increases summoner damage by 10%");
        add("tooltip.item.confluence.hercules_beetle.0", "Increases summoner damage by 15%, increases minion knockback");
        add("tooltip.item.confluence.necromantic_scroll.0", "Increases minion limit by 1, increases summoner damage by 10%");
        add("tooltip.item.confluence.papyrus_scarab.0", "Increases minion limit by 1, increases summoner damage by 15%, increases minion knockback");
        add("tooltip.item.confluence.pygmy_necklace.0", "Increases minion limit by 1");
        add("tooltip.item.confluence.fledgling_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.chromatic_cloak.0", "Immunity to Shimmer Phasing.Hold Shift to Phase while submerged in Shimmer");
        add("tooltip.item.confluence.paint_scraper", "Used to remove paint or coatings.Press Shift + Right-click to remove only one side.");
        add("tooltip.item.confluence.coin", "Using it while crouch to upgrade tier");
        add("tooltip.item.confluence.penetration", "Penetrates Count");
        add("tooltip.item.confluence.on_hit_effects", "Effects");
        add("tooltip.item.confluence.max_count", "Max Count");
        add("tooltip.item.confluence.fly_speed", "Fly Speed");
        add("tooltip.item.confluence.bow_full_pull_on_hit_effects", "Full Pull Effects");
        add("tooltip.item.confluence.has_proj", "Has Projectile");
        add("tooltip.item.confluence.arrow_transform", "Wooden Arrow Transform");
        add("tooltip.item.confluence.additional_attack_damage", "Additional Attack Damage");
        add("tooltip.item.confluence.no_gravity", "No Gravity");
        add("tooltip.item.confluence.cause_fire", "Causes Fire");
        add("tooltip.item.confluence.can_penetrate", "Can Penetrate");



        add("tooltip.item.confluence.radio_thing.0", "Allows the user to see the world differently");
        add("tooltip.item.terra_curio.radio_thing.1", "'Forbidden Knowledge echoes from the radio...'");


        add("jei.tooltip.item.confluence.bezoar.0", "It has a Chance to be dropped from Hornet，Moss Hornet and Toxic Sludge.");
        add("jei.tooltip.item.confluence.holy_water.0", "It has a Chance to be dropped from Wither Skeleton.");
        add("jei.tooltip.item.confluence.vitamins.0", "It has a Chance to be dropped from Corruptor or Floaty Gross.");
        add("jei.tooltip.item.confluence.energy_bar.0", "It has a Chance to be dropped from Zombified Piglin.");
        add("jei.tooltip.item.confluence.blindfold.0", "It has a Chance to be dropped from Blood Mummy，Dark Mummy，Crimslime，Corrupt Slime and Shadow Slime.");
        add("jei.tooltip.item.confluence.flashlight.0", "It can be found in Chests in the Stronghold.");
        add("jei.tooltip.item.confluence.fast_clock.0", "It has a Chance to be dropped from Mummy, Pixie and Wraith.");
        add("jei.tooltip.item.confluence.trifold_map.0", "It has a Chance to be dropped from Clown, Giant Bat and Light Mummy.");
        add("jei.tooltip.item.confluence.hand_drill.0", "It has a Chance to be dropped from Elder Guardian.");
        add("jei.tooltip.item.confluence.shot_put.0", "It can be found in Chests in the Stronghold.");
        add("jei.tooltip.item.confluence.star_cloak.0", "It has a Chance to be dropped from Mimic.");
        add("jei.tooltip.item.confluence.black_belt.0", "It has a Chance to be dropped from Bone Lee.");
        add("jei.tooltip.item.confluence.sun_stone.0", "It has a Chance to be dropped from Golem.");
        add("jei.tooltip.item.confluence.moon_stone.0", "It has a Chance to be dropped from Vampire ");
        add("jei.tooltip.item.confluence.moon_charm.0", "It has a Chance to be dropped from Werewolf .");
        add("jei.tooltip.item.confluence.neptunes_shell.0", "It has a Chance to be dropped from Creature from the Deep .");
        add("jei.tooltip.item.confluence.cobalt_shield.0", "It can be obtained with a chance from Dungeon Chest, Dungeon Crate, or Fence Crate (Golden Lock Boxes).");
        add("jei.tooltip.item.confluence.cross_necklace.0", "It has a Chance to be dropped from Mimic.");
        add("jei.tooltip.item.confluence.ranger_emblem.0", "It has a Chance to be dropped from Wall of Flesh.");
        add("jei.tooltip.item.confluence.warrior_emblem.0", "It has a Chance to be dropped from Wall of Flesh.");
        add("jei.tooltip.item.confluence.sorcerer_emblem.0", "It has a Chance to be dropped from Wall of Flesh.");
        add("jei.tooltip.item.confluence.eye_of_the_golem.0", "It has a Chance to be dropped from Golem.");
        add("jei.tooltip.item.confluence.feral_claws.0", "It can be obtained with a chance from Ivy Chest, Jungle Crate/Bramble Crate, and chest in Jungle Pyramids (Minecraft).");
        add("jei.tooltip.item.confluence.titan_glove.0", "It has a Chance to be dropped from Mimic.");
        add("jei.tooltip.item.confluence.flesh_knuckles.0", "It has a Chance to be dropped from Crimson Mimic.");
        add("jei.tooltip.item.confluence.paladins_shield.0", "It has a Chance to be dropped from Crimson Paladin.");
        add("jei.tooltip.item.confluence.frozen_turtle_shell.0", "It has a Chance to be dropped from Ice Tortoise.");
        add("jei.tooltip.item.confluence.honey_comb.0", "It has a Chance to be dropped from Queen Bee.");
        add("jei.tooltip.item.confluence.shark_tooth_necklace.0", "It has a Chance to be dropped from Blood Zombie and Drippler.");
        add("jei.tooltip.item.confluence.panic_necklace.0", "It can be obtained with a chance from Crimson Crate/Hematic Crate and Crimson Heart.");
        add("jei.tooltip.item.confluence.magic_quiver.0", "It has a Chance to be dropped from Skeleton Archer.");

        add("jei.tooltip.item.confluence.rifle_scope.0", "It has a Chance to be dropped from Skeleton Sniper.");
        add("jei.tooltip.item.confluence.magma_stone.0", "It has a Chance to be dropped from Hellbat and Lava Bat.");
        add("jei.tooltip.item.confluence.obsidian_rose.0", "It has a Chance to be dropped from Fire Imp.");
        add("jei.tooltip.item.confluence.putrid_scent.0", "It has a Chance to be dropped from Corrupt Mimic.");
        add("jei.tooltip.item.confluence.shackle.0", "It has a Chance to be dropped from Zombie.");
        add("jei.tooltip.item.confluence.toolbelt.0", "Can be shopped from the Goblin Tinkerer.");
        add("jei.tooltip.item.confluence.toolbox.0", "It can be obtained with a chance from gifts.");
        add("jei.tooltip.item.confluence.extendo_grip.0", "Can be purchased from the Traveling Merchant.");
        add("jei.tooltip.item.confluence.portable_cement_mixer.0", "Can be purchased from the Traveling Merchant.");
        add("jei.tooltip.item.confluence.brick_layer.0", "Can be purchased from the Traveling Merchant.");
        add("jei.tooltip.item.confluence.ancient_chisel.0", "It can be obtained with a chance from Oasis Crate/Mirage Crate and Sandstone Chest.");
        add("jei.tooltip.item.confluence.band_of_regeneration.0", "It can be obtained with a chance from Underground Chest.");
        add("jei.tooltip.item.confluence.depth_meter.0", "It has a Chance to be dropped from Cave Bat, Giant Bat, Ice Bat, Jungle Bat, Crawdad, Giant Shelly and Salamander.");
        add("jei.tooltip.item.confluence.compass.0", "It has a Chance to be dropped from Armored Viking, Undead Viking, Mother Slime, Piranha, Snow Flinx, Crawdad, Giant Shelly, Salamander.");
        add("jei.tooltip.item.confluence.radar.0", "Can be purchased from the Skeleton Merchant and It can be obtained with a chance from Wooden Crate/Pearlwood Crate, and Surface Chest.");
        add("jei.tooltip.item.confluence.life_form_analyzer.0", "Can be purchased from the Traveling Merchant.");
        add("jei.tooltip.item.confluence.tally_counter.0", "It has a Chance to be dropped from Angry Bones, Cursed Skull and Dark Caster");
        add("jei.tooltip.item.confluence.metal_detector.0", "It has a Chance to be dropped from Nymph.");
        add("jei.tooltip.item.confluence.dps_meter.0", "It has a Chance to be dropped from Angry Bones, Cursed Skull and Dark Caster");
        add("jei.tooltip.item.confluence.fishermans_pocket_guide.0", "It can be obtained with a chance from the Angler quests.");
        add("jei.tooltip.item.confluence.weather_radio.0", "It can be obtained with a chance from the Angler quests.");
        add("jei.tooltip.item.confluence.sextant.0", "It can be obtained with a chance from the Angler quests.");
        add("jei.tooltip.item.confluence.step_stool.0", "It can be obtained with a chance from Wooden Crate/Pearlwood Crate, and Surface Chest.");
        add("jei.tooltip.item.confluence.flying_carpet.0", "It can be obtained with a chance from Pyramid and Desert Pyramids chest.");
        add("jei.tooltip.item.confluence.aglet.0", "It can be obtained with a chance from Wooden Crate/Pearlwood Crate, and Surface Chest.");
        add("jei.tooltip.item.confluence.magiluminescence.0", "Crafted from.");
        add("jei.tooltip.item.confluence.lava_charm.0", "It can be obtained with a chance from Obsidian Crate/Hellstone Crate and Cave Chest.");
        add("jei.tooltip.item.confluence.climbing_claws.0", "It can be obtained with a chance from Wooden Crate/Pearlwood Crate, and Surface Chest.");
        add("jei.tooltip.item.confluence.shoe_spikes.0", "It can be obtained with a chance from Cave Chest and Rich Mahogany Chest.");
        add("jei.tooltip.item.confluence.tabi.0", "It has a Chance to be dropped from Bone Lee.");
        add("jei.tooltip.item.confluence.hermes_boots.0", "It can be obtained with a chance from Underground Chest.");
        add("jei.tooltip.item.confluence.flurry_boots.0", "It can be obtained with a chance from Boreal Crate/Frozen Crate, Frozen Chest and Ice Mimic.");
        add("jei.tooltip.item.confluence.sailfish_boots.0", "It can be obtained with a chance from Wooden Crate/Iron Crate, Pearlwood Crate/Mythril Crate, Shipwreck Chest and Ocean Ruins Chest.");
        add("jei.tooltip.item.confluence.dunerider_boots.0", "It can be obtained with a chance from Oasis Crate/Mirage Crate, Sandstone Chest and Desert Pyramids chest.");
        add("jei.tooltip.item.confluence.water_walking_boots.0", "It can be obtained with a chance from Ocean Crate/Seaside Crate, Water Chest and Ocean Ruins Chest.");
        add("jei.tooltip.item.confluence.cloud_in_a_bottle.0", "It can be obtained with a chance from Underground Chest and Cave Chest.");
        add("jei.tooltip.item.confluence.sandstorm_in_a_bottle.0", "It can be obtained with a chance from Oasis Crate/Mirage Crate, Pyramid Chest and Desert Pyramids chest.");
        add("jei.tooltip.item.confluence.fart_in_a_jar.0", "Crafted from.");
        add("jei.tooltip.item.confluence.diving_helmet.0", "It has a Chance to be dropped from Shark.");
        add("jei.tooltip.item.confluence.diving_helmet.1", "It has a Chance to be dropped from Drowned.");
        add("jei.tooltip.item.confluence.tsunami_in_a_bottle.0", "It can be obtained with a chance from Wooden Crate/Iron Crate, Pearlwood Crate/Mythril Crate, Shipwreck Chest and Ocean Ruins Chest.");
        add("jei.tooltip.item.confluence.shiny_red_balloon.0", "It can be obtained with a chance from Azure Crate/Sky Crate and Skyware Chest.");
        add("jei.tooltip.item.confluence.lucky_horseshoe.0", "It can be obtained with a chance from Azure Crate/Sky Crate and Skyware Chest.");
        add("jei.tooltip.item.confluence.inner_tube.0", "It can be obtained with a chance from Ocean Crate/Seaside Crate and Water Chest.");
        add("jei.tooltip.item.confluence.flipper.0", "It can be obtained with a chance from Ocean Crate/Seaside Crate, Water Chest and Ocean Ruins Chest.");
        add("jei.tooltip.item.confluence.jellyfish_necklace.0", "It has a Chance to be dropped from Pink Jellyfish, Green Jellyfish and Blue Jellyfish.");
        add("jei.tooltip.item.confluence.arctic_diving_gear.0", "Crafted from.");
        add("jei.tooltip.item.confluence.frog_leg.0", "It can be obtained with a chance by fishing in any biome..");
        add("jei.tooltip.item.confluence.treasure_magnet.0", "It can be obtained with a chance from Obsidian Lock Box and Shadow Chest.");
        add("jei.tooltip.item.confluence.flower_boots.0", "It can be obtained with a chance from Ivy Chest, Jungle Crate/Bramble Crate and chest in Jungle Pyramids (Minecraft).");
        add("jei.tooltip.item.confluence.angler_earring.0", "It can be obtained with a chance from the Angler quests..");
        add("jei.tooltip.item.confluence.royal_gel.0", "It can be obtained by defeating the King Slime in Normal difficulty or Higher difficulty.");
        add("jei.tooltip.item.confluence.shield_of_cthulhu.0", "It can be obtained by defeating the Eye of Cthuhu in Normal difficulty or Higher difficulty.");
        add("jei.tooltip.item.confluence.worm_scarf.0", "It can be obtained by defeating the Eater of Worlds in Normal difficulty or Higher difficulty.");
        add("jei.tooltip.item.confluence.brain_of_confusion.0", "It can be obtained by defeating the Eye of Cthuhu in Normal difficulty or Higher difficulty.");
        add("jei.tooltip.item.confluence.hive_pack.0", "It can be obtained by defeating the Queen Bee in Normal difficulty or Higher difficulty.");
        add("jei.tooltip.item.confluence.demon_heart.0", "It can be obtained by defeating the Wall of Flesh in Normal difficulty or Higher difficulty.");
        add("jei.tooltip.item.confluence.shiny_stone.0", "It can be obtained by defeating the Golem in Normal difficulty or Higher difficulty.");
        add("jei.tooltip.item.confluence.soaring_insignia.0", "It can be obtained by defeating the Empress of Light in Normal difficulty or Higher difficulty.");
        add("jei.tooltip.item.confluence.gravity_globe.0", "It can be obtained by defeating the Moon Lord in Normal difficulty or Higher difficulty.");
        add("jei.tooltip.item.confluence.celestial_starboard.0", "It can be obtained by defeating the Moon Lord in Normal difficulty or Higher difficulty.");


        add("painting.confluence.magic_harp.title", "MAGIC_HARP");
        add("painting.confluence.magic_harp.author", "BiliBili_魔法竖琴waaa，看上去傻傻的...");
        add("painting.confluence.westernat.title", "WESTERNAT");
        add("painting.confluence.westernat.author", "BiliBili_Westernat233，MC21世纪以来，最具有印象派主义的白桦树绘画!");
        add("painting.confluence.cooobrid.title", "COOOBRID");
        add("painting.confluence.cooobrid.author", "BiliBili_事一只一只一只鸽子，事一只只会咕咕咕的鸽子");
        add("painting.confluence.nakinosi.title", "NAKINOSI");
        add("painting.confluence.nakinosi.author", "BiliBili_咕咕咕的屑枕头，世界上最好看的渐变头发！");
        add("painting.confluence.maker.title", "MAKER");
        add("painting.confluence.maker.author", "BiliBili_Maker-2333，是Maker不是Marker！");
        add("painting.confluence.mustard_oasis.title", "MUSTARD_OASIS");
        add("painting.confluence.mustard_oasis.author", "BiliBili_芥末Oasis，芥末配fish，豪赤😋");
        add("painting.confluence.a_pigeon_delight.title", "A_PIGEON_DELIGHT");
        add("painting.confluence.sheep_mink.title", "SHEEP_MINK");
        add("painting.confluence.sheep_mink.author", "BiliBili_眠羊敏克，“啊？我打json？”");
        add("painting.confluence.voila.title", "VOILA");
        add("painting.confluence.voila.author", "BiliBili_风起下片灬");
        add("painting.confluence.xuanyu_1725.title", "XUANYU");
        add("painting.confluence.xuanyu_1725.author", "BiliBili_轩宇1725");
        add("painting.confluence.shadow_end.title", "SHADOW_END");
        add("painting.confluence.shadow_end.author", "BiliBili_影末子");
        add("painting.confluence.kl_jiana.title", "Kaleb Langley");
        add("painting.confluence.kl_jiana.author", "BiliBili_KalebLangley");
        add("painting.confluence.hunao.title", "HUNAO");
        add("painting.confluence.hunao.author", "BiliBili_小胡闹鸭");
        add("painting.confluence.sihuai_2412.title", "SIHUAI_2412");
        add("painting.confluence.sihuai_2412.author", "BiliBili_思怀_2412");
        add("painting.confluence.old_sheep.title", "OLD_SHEEP");
        add("painting.confluence.old_sheep.author", "BiliBili_我叫老绵羊");
        add("painting.confluence.slime_dragon.title", "SLIME_DRAGON");
        add("painting.confluence.slime_dragon.author", "BiliBili_小史龙吖Slime_Dragon");
        add("painting.confluence.khaki_coffee_beans.title", "KHAKI_COFFEE_BEANS");
        add("painting.confluence.khaki_coffee_beans.author", "BiliBili_卡其色咖啡豆");
        add("painting.confluence.uqtqu_day.title", "UQTQU_DAY");
        add("painting.confluence.uqtqu_day.author", "BiliBili__昼泽_");
        add("painting.confluence.emerald_shenyi.title", "EMERALD_SHENYI");
        add("painting.confluence.emerald_shenyi.author", "BiliBili_Emerald_审翼");
        add("painting.confluence.chromatic.title", "CHROMATIC");
        add("painting.confluence.chromatic.author", "BiliBili_陌林_Chromatic");
        add("painting.confluence.the_great_papyrus.title", "THE_GREAT_PAPYRUS");
        add("painting.confluence.the_great_papyrus.author", "BiliBili_事伟大的papyrus呀");
        add("painting.confluence.kulou_d.title", "KULOU_D");
        add("painting.confluence.kulou_d.author", "BiliBili_KuLou_D");
        add("painting.confluence.in_the_gap_of_the_cloud_sea.title", "云海隙间");
        add("painting.confluence.in_the_gap_of_the_cloud_sea.author", "鹰角豆");

        add("painting.confluence.confluence.title", "CONFLUENCE");
        add("painting.confluence.confluence.author", "Confluence Of The Afterlife");
        add("painting.confluence.the_twilight_of_dawn.title", "The Twilight Of Dawn");
        add("painting.confluence.the_twilight_of_dawn.author", "The converging journey has reached a fork, and the moment to face powerful foes will inevitably arrive.");

        // new
        add("achievements.toast.complete", "Achievement achieved!");
        add("achievements.confluence.new_world.title", "Old World, New Journey!");
        add("achievements.confluence.new_world.description", "The afterlife of convergence and exchange");
        add("achievements.confluence.timber.title", "Timber!! ");
        add("achievements.confluence.timber.description", "Chop down your first tree.");
        add("achievements.confluence.benched.title", "Benched");
        add("achievements.confluence.benched.description", "Craft your first Crafting Table.");
        add("achievements.confluence.star_power.title", "Star Power");
        add("achievements.confluence.star_power.description", "Craft a mana crystal multiOut of fallen stars, and consume it.");
        add("achievements.confluence.you_can_do_it.title", "You Can Do It!");
        add("achievements.confluence.you_can_do_it.description", "Survive your character's first full night.");
        add("achievements.confluence.watch_your_step.title", "Watch Your Step!");
        add("achievements.confluence.watch_your_step.description", "Become a victim to a nasty underground trap.");
        add("achievements.confluence.vehicular_manslaughter.title", "Vehicular Manslaughter");
        add("achievements.confluence.vehicular_manslaughter.description", "Defeat an enemy by running it over with a minecart.");
        add("achievements.confluence.unusual_survival_strategies.title", "Unusual Survival Strategies");
        add("achievements.confluence.unusual_survival_strategies.description", "Delay death from drowning by drinking water. It doesn't make much sense, but you did what you had to do.");
        add("achievements.confluence.topped_off.title", "Topped Off");
        add("achievements.confluence.topped_off.description", "Attain maximum life and mana possible without accessories or buffs.");
        add("achievements.confluence.rock_bottom.title", "Rock Bottom");
        add("achievements.confluence.rock_bottom.description", "The only way is up!");
        add("achievements.confluence.photosynthesis.title", "Photosynthesis");
        add("achievements.confluence.photosynthesis.description", "Mine chlorophyte, an organic ore found deep among the thickest of flora.");
        add("achievements.confluence.miner_for_fire.title", "Miner for Fire");
        add("achievements.confluence.miner_for_fire.description", "Craft a molten pickaxe using the hottest of materials.");
        add("achievements.confluence.matching_attire.title", "Matching Attire");
        add("achievements.confluence.matching_attire.description", "Equip armor in all the armor slots");
        add("achievements.confluence.marathon_medalist.title", "Marathon Medalist ");
        add("achievements.confluence.marathon_medalist.description", "Travel a total of 26.2 miles on foot.");
        add("achievements.confluence.lucky_break.title", "Lucky Break");
        add("achievements.confluence.lucky_break.description", "Survive a long fall with just a sliver of health remaining.");
        add("achievements.confluence.like_a_boss.title", "Like a Boss");
        add("achievements.confluence.like_a_boss.description", "Obtain a boss-summoning item.");
        add("achievements.confluence.its_getting_hot_in_here.title", "It's Getting Hot in Here");
        add("achievements.confluence.its_getting_hot_in_here.description", "Spelunk deep enough to reach the molten underworld.");
        add("achievements.confluence.into_orbit.title", "Into Orbit");
        add("achievements.confluence.into_orbit.description", "You can only go down from here!");
        add("achievements.confluence.hot_reels.title", "Hot Reels!");
        add("achievements.confluence.hot_reels.description", "Drop a lure in a pool of lava for a pre-fried haul!");
        add("achievements.confluence.head_in_the_clouds.title", "Head in the Clouds");
        add("achievements.confluence.head_in_the_clouds.description", "Equip a pair of wings.");
        add("achievements.confluence.glorious_golden_pole.title", "Glorious Golden Pole");
        add("achievements.confluence.glorious_golden_pole.description", "Obtain a golden fishing rod.");
        add("achievements.confluence.get_a_life.title", "Get a Life");
        add("achievements.confluence.get_a_life.description", "Consume a life fruit, which grows in the thick of subterranean jungle grass.");
        add("achievements.confluence.extra_shiny.title", "Extra Shiny! ");
        add("achievements.confluence.extra_shiny.description", "Mine a powerful ore that has been newly blessed upon your world.");
        add("achievements.confluence.dungeon_heist.title", "Dungeon Heist  ");
        add("achievements.confluence.dungeon_heist.description", "Steal a key from the dungeon's undead denizens, and unlock one of their precious golden chest.");
        add("achievements.confluence.drax_attax.title", "Drax Attax");
        add("achievements.confluence.drax_attax.description", "Craft a drax or pickaxe axe using hallowed bars, and the souls of the three mechanical bosses.");
        add("achievements.confluence.dead_men_tell_no_tales.title", "Dead Men Tell No Tales");
        add("achievements.confluence.dead_men_tell_no_tales.description", "You were so preoccupied with whether or not you could open the chest that you didn't stop to think if you should.");
        add("achievements.confluence.completely_awesome.title", "Completely Awesome");
        add("achievements.confluence.completely_awesome.description", "Obtain a minishark.");
        add("achievements.confluence.bulldozer.title", "Bulldozer ");
        add("achievements.confluence.bulldozer.description", "Destroy a total of 10,000 blocks.");
        add("achievements.confluence.begone_evil.title", "Begone, Evil! ");
        add("achievements.confluence.begone_evil.description", "Smash a demon or crimson altar with a powerful, holy hammer.");


        add("achievements.confluence.ooo_shinny.title", "Ooo! Shiny!");
        add("achievements.confluence.ooo_shinny.description", "Mine your first nugget of ore with a pickaxe.");
        add("achievements.confluence.i_am_loot.title", "I Am Loot!");
        add("achievements.confluence.i_am_loot.description", "Discover a golden chest underground and take a peek at its contents.");
        add("achievements.confluence.hold_on_tight.title", "Hold on Tight!");
        add("achievements.confluence.hold_on_tight.description", "Equip your first grappling hook.");
        add("achievements.confluence.heavy_metal.title", "Heavy Metal");
        add("achievements.confluence.heavy_metal.description", "Obtain an anvil made from iron or lead.");
        add("achievements.confluence.heart_breaker.title", "Heart Breaker");
        add("achievements.confluence.heart_breaker.description", "Discover and smash your first heart crystal underground.");
        add("achievements.confluence.hammer_time.title", "Stop! Hammer Time! ");
        add("achievements.confluence.hammer_time.description", "Obtain your first hammer via crafting or otherwise.");
        add("achievements.confluence.boots_of_the_hero.title", "Boots of the Hero");
        add("achievements.confluence.boots_of_the_hero.description", "Forged from the finest boots of fire and ice.");
        add("achievements.confluence.black_mirror.title", "Black Mirror");
        add("achievements.confluence.black_mirror.description", "You'll never leave home without it again.");
        add("achievements.confluence.ankhumulation_complete.title", "Ankhumulation Complete");
        add("achievements.confluence.ankhumulation_complete.description", "The finest protection from unpleasant maladies and ailments.");
        add("achievements.confluence.a_shimmer_in_the_dark.title", "A Shimmer In The Dark");
        add("achievements.confluence.a_shimmer_in_the_dark.description", "Shimmer an item into another item. What other transmutations can you find?");

        add("achievements.confluence.pretty_in_pink.title", "Pretty in Pink");
        add("achievements.confluence.pretty_in_pink.description", "Kill pinky.");
        add("achievements.confluence.slippery_shinobi.title", "Slippery Shinobi ");
        add("achievements.confluence.slippery_shinobi.description", "Defeat King Slime, the lord of all things slimy.");
        add("achievements.confluence.eye_on_you.title", "Eye on You");
        add("achievements.confluence.eye_on_you.description", "“Defeat the Eye of Cthulhu, an ocular menace who only appears at night.");

        add("confluence.configuration.dropsMoney", "Coin Drop");
        add("confluence.configuration.dropsMoney.tooltip", "When enabled, your coins will drop upon death.");
        add("confluence.configuration.autoStackGelsColor", "Auto Stack Gels by Color");
        add("confluence.configuration.autoStackGelsColor.tooltip", "When enabled, gels of different colors you pick up will stack together.");
        add("confluence.configuration.fletchingMenu", "Fletching Menu");
        add("confluence.configuration.fletchingMenu.tooltip", "When enabled, the modification for the fletching menu will be applied in Confluence.");
        add("confluence.configuration.shimmer_decompose", "Shimmer Decompose");
        add("confluence.configuration.shimmer_decompose.tooltip", "When enabled, shimmer liquid can decompose items into materials.");
        add("confluence.configuration.fallingStarFrequency", "Falling Star Frequency");
        add("confluence.configuration.fallingStarFrequency.tooltip", "Defines the frequency of falling stars during the night.");
        add("confluence.configuration.defaultRespawnTimeMin", "Default Minimum Respawn Time");
        add("confluence.configuration.defaultRespawnTimeMax", "Default Maximum Respawn Time");
        add("confluence.configuration.bossRespawnTimeMin", "Minimum Respawn Time (during boss fights)");
        add("confluence.configuration.bossRespawnTimeMax", "Maximum Respawn Time (during boss fights)");
        add("confluence.configuration.showWindParticles", "Show Wind Particles");
        add("confluence.configuration.HUD", "HUD");
        add("confluence.configuration.Mana", "Mana");
        add("confluence.configuration.Armor", "Armor");
        add("confluence.configuration.Health", "Health");
        add("confluence.configuration.Food", "Food Saturation");
        add("confluence.configuration.terraStyleHealth", "Terra Style Health");
        add("confluence.configuration.terraStyleArmor", "Terra Style Armor");
        add("confluence.configuration.terraStyleFood", "Terra Style Food Saturation");
        add("confluence.configuration.healthStyle", "Health Style");
        add("confluence.configuration.manaStyle", "Mana Style");
        add("confluence.configuration.armorStyle", "Armor Style");
        add("confluence.configuration.foodStyle", "Food Saturation Style");
        add("confluence.configuration.armorStyle.legacy_horizontal", "Armor Style: Elegant - Horizontal");
        add("confluence.configuration.armorStyle.legacy_diagonal", "Armor Style: Elegant - Diagonal");
        add("confluence.configuration.armorStyle.legacy_vertical", "Armor Style: Elegant - Vertical");
        add("confluence.configuration.armorStyle.overlay", "Armor Style: Overlay");
        add("confluence.configuration.manaStyle.legacy", "Mana Style: Elegant");
        add("confluence.configuration.manaStyle.overlay", "Mana Style: Overlay");
        add("confluence.configuration.healthStyle.legacy", "Health Style: Elegant");
        add("confluence.configuration.healthStyle.overlay", "Health Style: Overlay");
        add("confluence.configuration.foodStyle.legacy", "Food Saturation Style: Elegant");
        add("confluence.configuration.foodStyle.overlay", "Food Saturation Style: Overlay");
        add("confluence.configuration.leftEffectIcon", "Left Effect Icon");
        add("confluence.configuration.Entity", "Entity Effects");
        add("confluence.configuration.hurtRedOverlay", "Hurt Red Overlay");
        add("confluence.configuration.bloodyEffect", "Bloody Effect");
        add("confluence.configuration.goreEffect", "Gore Effect");
        add("confluence.configuration.damageIndicator", "Damage Indicator");
        add("confluence.configuration.Gameplay", "Gameplay Mechanics");
        add("confluence.configuration.Gameplay.button", "Gameplay Mechanics Definition");
        add("confluence.configuration.PlayerDeath", "Player Death Mechanics");
        add("confluence.configuration.PlayerDeath.button", "Player Death Mechanics");
        add("confluence.configuration.PlayerDeath.tooltip", "Defines the effects when a player dies.");
        add("confluence.configuration.showMoneyDrops", "Show Money Drops on Death Screen");
// Separator
        add("confluence.configuration.Mana.button", "Mana");
        add("confluence.configuration.Armor.button", "Armor");
        add("confluence.configuration.Health.button", "Health");
        add("confluence.configuration.Food.button", "Food Saturation");
        add("confluence.configuration.manaStyle.tooltip", "Mana Style");
        add("confluence.configuration.Mana.tooltip", "About Mana Display");
        add("confluence.configuration.Food.tooltip", "About Food Display");
        add("confluence.configuration.section.confluence.client.toml", "Client Display Settings");
        add("confluence.configuration.section.confluence.common.toml", "Gameplay Settings");
        add("confluence.configuration.leftEffectIcon.tooltip", "When enabled, potion effect icons are displayed on the left side of the screen.");
        add("confluence.configuration.Entity.button", "Entity-related Visual Effects");
        add("confluence.configuration.HUD.tooltip", "About HUD Display");
        add("confluence.configuration.showWindParticles.tooltip", "Adjust the value to decide how many wind particles are visible.");
        add("confluence.configuration.HUD.button", "About HUD Display");

        add("confluence.configuration.terraStyleHealth.tooltip", "When enabled, health is displayed in Terra style.");
        add("confluence.configuration.healthStyle.tooltip", "Health Display");
        add("confluence.configuration.Health.tooltip", "About Health Display");
        add("confluence.configuration.terraStyleArmor.tooltip", "When enabled, armor is displayed in Terra style.");
        add("confluence.configuration.Armor.tooltip", "About Armor Display");
        add("confluence.configuration.title", "Configuration Screen");
        add("confluence.configuration.section.confluence.client.toml.title", "Client-side Configuration");
        add("confluence.configuration.Entity.tooltip", "Entity-related Visual Effects");

        add("confluence.configuration.section.confluence.common.toml.title", "Server-side Configuration");

        add("prefix.confluence.tooltip.plus", "+%s%% %s");
        add("prefix.confluence.tooltip.take", "-%s%% %s");
        add("prefix.confluence.tooltip.add", "+%s %s");
        add("prefix.confluence.tooltip.mana_cost", "Mana Cost");
        add("prefix.confluence.tooltip.additional_mana", "Additional Mana");
        add("prefix.confluence.quick", "Quick");
        add("prefix.confluence.hasty", "Hasty");
        add("prefix.confluence.deadly", "Deadly");
        Iterables.concat(Universal.VALUES, Common.VALUES, Melee.VALUES, Ranged.VALUES, Magic.VALUES, Accessory.VALUES).forEach(prefix -> {
            String name = prefix.name();
            if ("quick".equals(name) || "deadly".equals(name) || "hasty".equals(name)) return;
            add("prefix.confluence." + name, toTitleCase(name));
        });

        add("fluid_type.confluence.shimmer", "Shimmer");
        add("fluid_type.confluence.honey", "Honey");

        add("title.confluence.shimmer_transmutation", "Shimmer Transmutation");
        add("condition.confluence.shimmer_transmutation", "Required game phase: %s");
        add("title.confluence.altar", "Altar");
        add("title.confluence.sky_mill", "Sky Mill");
        add("container.confluence.sky_mill", "Sky Mill");
        add("title.confluence.heavy_work_bench", "Heavy Work Bench");
        add("container.confluence.heavy_work_bench", "Heavy Work Bench");
        add("title.confluence.hellforge", "Hell Forge");
        add("container.confluence.hellforge", "Hell Forge");
        add("container.confluence.alchemy_table", "Alchemy Table");
        add("title.confluence.alchemy_table", "Alchemy Table");
        add("condition.confluence.requires_fuel", "Requires Fuel");
        add("container.confluence.fletching_table", "Fletching Table");


        add("block.confluence.timers_block_1_1", "1 Second Timer");
        add("block.confluence.timers_block_3_1", "3 Second Timer");
        add("block.confluence.timers_block_5_1", "5 Second TImer");
        add("block.confluence.timers_block_1_2", "1/2 Second Timer");
        add("block.confluence.timers_block_1_4", "1/4 Second TImer");
        add("block.confluence.base_chest_block.locked_golden", "§rLocked Golden Chest");
        add("block.confluence.base_chest_block.unlocked_golden", "§rGolden Chest");
        add("block.confluence.base_chest_block.death_golden", "§rDeath Golden Chest");
        add("block.confluence.base_chest_block.locked_shadow", "§rLocked Shadow Chest");
        add("block.confluence.base_chest_block.unlocked_shadow", "§rShadow Chest");
        add("block.confluence.base_chest_block.death_shadow", "§rDeath Shadow Chest");
        add("block.confluence.base_chest_block.unlocked_lvy", "§rLvy Chest");
        add("block.confluence.base_chest_block.death_lvy", "§rDeath Lvy Chest");
        add("block.confluence.base_chest_block.unlocked_frozen", "§rFrozen Chest");
        add("block.confluence.base_chest_block.death_frozen", "§rDeath Frozen Chest");
        add("block.confluence.base_chest_block.unlocked_water", "§rWater Chest");
        add("block.confluence.base_chest_block.death_water", "§rDeath Water Chest");
        add("block.confluence.base_chest_block.unlocked_skyware", "§rSkyware Chest");
        add("block.confluence.base_chest_block.death_skyware", "§rDeath Skyware Chest");
        add("block.confluence.base_chest_block.unlocked_normal", "§rWooden Chest");
        add("block.confluence.base_chest_block.death_normal", "§rDeath Wooden Chest");

        add("resourcepack.terraria_art", "Terraria Art");
        add("resourcepack.terraria_armor", "Terraria-Like Armor");

        add("event.confluence.blood_moon", "The Blood Moon is rising...");
        add("event.confluence.meteorite", "A meteorite has landed!");

        add("attribute.name.player.minion_capacity", "Minion Capacity");
        add("attribute.name.player.sentry_capacity", "Sentry Capacity");
        add("attribute.name.player.summon_damage", "Summon Damage");
        add("attribute.name.player.summon_knockback", "Summon Knockback");
        add("attribute.name.player.whip_range", "Whip Range");

        add("entity.minecraft.villager.confluence.sky_miller", "Sky Miller");
        add("entity.minecraft.villager.confluence.banker", "Banker");
        add("entity.confluence.dart", "Dart");

        add("container.confluence.workshop", "Workshop");
        add("title.confluence.workshop", "Workshop");

        add("gamerule.confluenceSpreadableChance", "Confluence Spreadable Chance");
        add("generator.confluence.the_corruption", "The Corruption");
        add("generator.confluence.tr_crimson", "The Crimson");

        add("biome.confluence.ash_forest", "Ash Forest");
        add("biome.confluence.ash_wasteland", "Ash Wasteland");
        add("biome.confluence.glowing_mushroom", "Glowing Mushroom");
        add("biome.confluence.the_corruption", "The Corruption");
        add("biome.confluence.the_corruption_desert", "The Corruption Desert");
        add("biome.confluence.the_corruption_tundra", "The Corruption Tundra");
        add("biome.confluence.the_hallow", "The Hallow");
        add("biome.confluence.the_hallow_desert", "The Hallow Desert");
        add("biome.confluence.the_hallow_tundra", "The Hallow Tundra");
        add("biome.confluence.tr_crimson", "The Crimson");
        add("biome.confluence.tr_crimson_desert", "The Crimson Desert");
        add("biome.confluence.tr_crimson_tundra", "The Crimson Tundra");

        Consumer<DeferredHolder<Block, ? extends Block>> blockAction = block -> add(block.get(), toTitleCase(block.getId().getPath()));
        CrateBlocks.BLOCKS.getEntries().forEach(blockAction);
        DecorativeBlocks.BLOCKS.getEntries().forEach(blockAction);
        FunctionalBlocks.BLOCKS.getEntries().forEach(blockAction);
        ModBlocks.BLOCKS.getEntries().forEach(blockAction);
        MusicBoxBlocks.BLOCKS.getEntries().forEach(blockAction);
        NatureBlocks.BLOCKS.getEntries().forEach(blockAction);
        OreBlocks.BLOCKS.getEntries().forEach(blockAction);
        PotBlocks.BLOCKS.getEntries().forEach(blockAction);
        StatueBlocks.BLOCKS.getEntries().forEach(blockAction);

        Consumer<DeferredHolder<Item, ? extends Item>> itemAction = item -> add(item.get(), toTitleCase(item.getId().getPath()));
        add(AccessoryItems.PHILOSOPHERS_STONE.get(), "Philosopher's Stone");
        add(ModItems.BOREDOMS_PACT_FALLING_RESOLVE.get(), "Boredom's Pact - Falling Resolve");
        AccessoryItems.ITEMS.getEntries().forEach(itemAction);
        ArmorItems.ITEMS.getEntries().forEach(itemAction);
        ArrowItems.ITEMS.getEntries().forEach(itemAction);
        AxeItems.ITEMS.getEntries().forEach(itemAction);
        BaitItems.ITEMS.getEntries().forEach(itemAction);
        BoomerangItems.ITEMS.getEntries().forEach(itemAction);
        BowItems.ITEMS.getEntries().forEach(itemAction);
        ConsumableItems.ITEMS.getEntries().forEach(itemAction);
        VanityArmorItems.ITEMS.getEntries().forEach(itemAction);
        DrillItems.ITEMS.getEntries().forEach(itemAction);
        FishingPoleItems.ITEMS.getEntries().forEach(itemAction);
        FoodItems.ITEMS.getEntries().forEach(itemAction);
        HammerItems.ITEMS.getEntries().forEach(itemAction);
        HookItems.ITEMS.getEntries().forEach(itemAction);
        IconItems.ITEMS.getEntries().forEach(itemAction);
        ManaStaffItems.ITEMS.getEntries().forEach(itemAction);
        MaterialItems.ITEMS.getEntries().forEach(itemAction);
        MinecartItems.ITEMS.getEntries().forEach(itemAction);
        ModItems.ITEMS.getEntries().forEach(itemAction);
        ModItems.HIDDEN.getEntries().forEach(itemAction);
        ModItems.BLOCK_ITEMS.getEntries().forEach(itemAction);
        PickaxeAxeItems.ITEMS.getEntries().forEach(itemAction);
        PickaxeItems.ITEMS.getEntries().forEach(itemAction);
        PotionItems.ITEMS.getEntries().forEach(itemAction);
        QuestedFishes.ITEMS.getEntries().forEach(itemAction);
        SwordItems.ITEMS.getEntries().forEach(itemAction);
        ToolItems.ITEMS.getEntries().forEach(itemAction);

        ModEffects.EFFECTS.getEntries().forEach(effect -> add(effect.get(), toTitleCase(effect.getId().getPath())));
        ModEntities.ENTITIES.getEntries().forEach(entity -> add(entity.get(), toTitleCase(entity.getId().getPath())));
        ModEffectStrategies.EFFECT_STRATEGY.getEntries().forEach(strategy -> add(strategy.get().getTranslationKey(), toTitleCase(strategy.get().getDescription_en_us())));
    }

    @Override
    public void add(Block key, String name) {
        String descriptionId = key.getDescriptionId();
        if (!((LanguageProviderAccessor) this).getData().containsKey(descriptionId)) {
            super.add(descriptionId, name);
        }
    }

    @Override
    public void add(Item key, String name) {
        String descriptionId = key.getDescriptionId();
        if (!((LanguageProviderAccessor) this).getData().containsKey(descriptionId)) {
            super.add(descriptionId, name);
        }
    }

    @Override
    public void add(MobEffect key, String name) {
        String descriptionId = key.getDescriptionId();
        if (!((LanguageProviderAccessor) this).getData().containsKey(descriptionId)) {
            super.add(descriptionId, name);
        }
    }

    @Override
    public void add(EntityType<?> key, String name) {
        String descriptionId = key.getDescriptionId();
        if (!((LanguageProviderAccessor) this).getData().containsKey(descriptionId)) {
            super.add(descriptionId, name);
        }
    }
}
