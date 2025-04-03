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
import org.confluence.mod.client.gui.TooltipManager;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.integration.ponder.PonderHelper;
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
        add("config.jade.plugin_confluence.jade_ponder_component", "Ponder Info");

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
        add("chat.confluence.magic_conch", "The location where you listen to the sound of the ocean [%s] has been recorded");
        add("chat.confluence.demon_conch", "The location where you listen to the sound of the demon [%s] has been recorded");
        add("options.difficulty.legendary", "§aLegendary");

        add("item.confluence.meteorite_ingot.tooltip", "Warm to the touch");
        add("jukebox_song.confluence.alpha", "C418 - alpha");

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
        add("info.confluence.respawn_time", "Respawn Time: %ss");
        add("info.confluence.drops_money", "dropped");
        add("info.confluence.drops_money.platinum", " %s platinum");
        add("info.confluence.drops_money.gold", " %s gold");
        add("info.confluence.drops_money.silver", " %s silver");
        add("info.confluence.drops_money.copper", " %s copper");

        add("key.confluence.hook", "Throwing Hook");
        add("key.confluence.specular_detail", "Detail observation of visual potions");


        add("key.confluence.gameplay", "Confluence Key Settings");
        add("key.confluence.healing", "Quick Use Health Potion");
        add("key.confluence.mana", "Quick Use Mana Potion");
        add("key.confluence.extra_inventory", "Quick Open Extra Slot");

        add("death.attack.falling_star", "%1$s got a response from a meteor");
        add("death.attack.boulder", "%1$s  is crushed by boulder");
        add("death.attack.thron", "%1$s  discovered that he had become a hedgehog.");
        add("death.attack.darkness", "%1$s was killed by something in the dark!");

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
        add("tooltip.item.confluence.fledgling_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.chromatic_cloak.0", "Immunity to Shimmer Phasing.Hold Shift to Phase while submerged in Shimmer");
        add("tooltip.item.confluence.paint_scraper", "Used to remove paint or coatings.Press Shift + Right-click to remove only one side.");
        add("tooltip.item.confluence.paint_sprayer.0", "Automatically paints or coats placed objects.");
        add("tooltip.item.confluence.coin", "Using it while crouch to upgrade tier");

        add("tooltip.item.confluence.bow_full_pull_on_hit_effects", "Full Pull Effects");
        add("tooltip.item.confluence.max_count", "Arrow Count");
        add("tooltip.item.confluence.on_hit_effects", "On Hit Effects");

        add("tooltip.item.confluence.has_proj", "Has Projectile");
        add("tooltip.item.confluence.has_proj.damage", "- Damage");
        add("tooltip.item.confluence.has_proj.speed", "- Speed");
        add("tooltip.item.confluence.has_proj.cooldown", "- Cooldown");
        add("tooltip.item.confluence.has_proj.track_type", "- Track Type");


        add("tooltip.item.confluence.arrow_transform", "Wooden Arrow Transform");
        add("tooltip.item.confluence.additional_attack_damage", "Additional Attack Damage");
        add("tooltip.item.confluence.no_gravity", "No Gravity");
        add("tooltip.item.confluence.cause_fire", "Causes Fire");
        add("tooltip.item.confluence.can_penetrate", "Can Penetrate");


        add("tooltip.item.confluence.radio_thing.0", "Allows the user to see the world differently");
        add("tooltip.item.terra_curio.radio_thing.1", "'Forbidden Knowledge echoes from the radio...'");


        add("tooltip.item.confluence.copper_short_sword.0", "The smallest fragment of the divine weapon's power has been with you since the convergence of the two worlds... until the journey's end.");
        add("tooltip.item.confluence.copper_short_sword.1", "\"We are so awesome!\" said the copper short sword.");
        add("tooltip.item.confluence.starfury.0", "A small part of the divine weapon's power is condensed within the clouds, transforming into a crystalline and sparkling star.");
        add("tooltip.item.confluence.starfury.1", "\"The wrath of the heavens pours down.\"");
        add("tooltip.item.confluence.enchanted_sword.0", "A small part of the divine weapon's power is buried in a cave, condensed into a bleak sword intent.");
        add("tooltip.item.confluence.enchanted_sword.1", "\"A flash of light in the dim sword tomb.\"");

        add("tooltip.item.confluence.bee_keeper.0", "A small part of the divine weapon's power is possessed by the swarm of bees in the jungle, becoming a buzzing hive.");
        add("tooltip.item.confluence.bee_keeper.1", "\"Sweet on the outside, sharp on the inside.\"");


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
        add("achievements.confluence.worm_fodder.title", "Worm Fodder");
        add("achievements.confluence.worm_fodder.description", "Defeat the Eater of Worlds, a massive worm who dwells in the corruption.");
        add("achievements.confluence.mastermind.title", "Mastermind ");
        add("achievements.confluence.mastermind.description", "Defeat the Brain of Cthulhu, an enormous demon brain which haunts the creeping crimson.");
        add("achievements.confluence.sting_operation.title", "Sting Operation ");
        add("achievements.confluence.sting_operation.description", "Defeat the Queen Bee, the matriarch of the jungle hives.");
        add("achievements.confluence.smashing_poppet.title", "Smashing, Poppet!");
        add("achievements.confluence.smashing_poppet.description", "Using explosives or your trusty hammer, smash a Shadow Orb or Crimson Heart in the evil parts of your world.");
        add("achievements.confluence.dye_hard.title", "Dye Hard");
        add("achievements.confluence.dye_hard.description", "Equip a dye in every possible dye slot.");
        add("achievements.confluence.fashion_statement.title", "Fashion Statement");
        add("achievements.confluence.fashion_statement.description", "Equip armor or vanity clothing in all four social slots.");
        add("achievements.confluence.the_cavalry.title", "The Cavalry");
        add("achievements.confluence.the_cavalry.description", "Equip a mount.");
        add("achievements.confluence.deceiver_of_fools.title", "Deceiver of Fools");
        add("achievements.confluence.deceiver_of_fools.description", "Kill a nymph.");
        add("achievements.confluence.not_the_bees.title", "Not the Bees!");
        add("achievements.confluence.not_the_bees.description", "Fire a Bee Gun while wearing a full set of Bee Armor.");
        add("achievements.confluence.bloodbath.title", "Bloodbath");
        add("achievements.confluence.bloodbath.description", "Survive a blood moon, a nocturnal event where the rivers run red and monsters swarm aplenty.");
        add("achievements.confluence.sticky_situation.title", "Sticky Situation");
        add("achievements.confluence.sticky_situation.description", "Survive the slime rain, where gelatinous organisms fall from the sky in droves.");


        add("confluence.configuration.achievementToast", "Enable Terra Style Achievements");
        add("confluence.configuration.achievementToast.tooltip", "Disable it if you want to use the default progress style.");
        add("confluence.configuration.playerOurMusic", "Enable Terra Music");
        add("confluence.configuration.playerOurMusic.tooltip", "Enable Terra's music, it will play in appropriate environments");
        add("confluence.configuration.dropsMoney", "Coin Drops");
        add("confluence.configuration.dropsMoney.tooltip", "When enabled, characters will drop coins upon death.");
        add("confluence.configuration.Paints", "Paint Function Settings");
        add("confluence.configuration.Paints.tooltip", "Some compatibility issues may be caused by the paint function, so you need to adjust the relevant options here.");
        add("confluence.configuration.Paints.button", "About Paint");
        add("confluence.configuration.paintsReplaceTexture", "Paint Replace Texture");
        add("confluence.configuration.paintsReplaceTexture.tooltip", "When enabled, the paint will use a replacement grayscale texture, making the appearance on materials more natural.");
        add("confluence.configuration.autoStackGelsColor", "Auto Stack Gels by Color");
        add("confluence.configuration.autoStackGelsColor.tooltip", "When enabled, gels of different colors you pick up will stack together");
        add("confluence.configuration.bannedModForPaints", "Mod Paints Blacklist");
        add("confluence.configuration.bannedModForPaints.button", "Enter Mod ID to use the blacklist");
        add("confluence.configuration.bannedModForPaints.tooltip", "If the paints from this mod cause rendering issues with blocks from other mods, enter the MOD ID to prevent that mod's blocks from using the paint color");
        add("confluence.configuration.fletchingMenu", "Fletching Table Menu");
        add("confluence.configuration.fletchingMenu.tooltip", "When enabled, the fletching table will be modified by Conflux");
        add("confluence.configuration.shimmer_decompose", "Shimmer Decompose");
        add("confluence.configuration.shimmer_decompose.tooltip", "When enabled, Shimmer liquid can decompose items into raw materials");
        add("confluence.configuration.fallingStarInterval", "Falling Star Interval");
        add("confluence.configuration.fallingStarInterval.tooltip", "Defines the interval of falling stars appearing at night");
        add("confluence.configuration.returnPotionGlassBottle", "Return Glass Bottles for Potions");
        add("confluence.configuration.returnPotionGlassBottle.tooltip", "Decides whether to return the glass bottle after using a potion");
        add("confluence.configuration.rightClickRideMinecart", "Right click to ride a minecart");
        add("confluence.configuration.rightClickRideMinecart.tooltip", "When enabled, when you right-click on a rail, it will automatically ride a minecart");
        add("confluence.configuration.defaultRespawnTimeMin", "Default Minimum Respawn Time");
        add("confluence.configuration.defaultRespawnTimeMin.tooltip", "Set the default minimum respawn time");
        add("confluence.configuration.defaultRespawnTimeMax", "Default Maximum Respawn Time");
        add("confluence.configuration.defaultRespawnTimeMax.tooltip", "Set the default maximum respawn time");
        add("confluence.configuration.bossRespawnTimeMin", "Minimum Respawn Time (Boss Battle)");
        add("confluence.configuration.bossRespawnTimeMax", "Maximum Respawn Time (Boss Battle)");
        add("confluence.configuration.bossRespawnTimeMax.tooltip", "Maximum respawn time for when dying in a boss battle");
        add("confluence.configuration.bossRespawnTimeMin.tooltip", "Minimum respawn time for when dying in a boss battle");
        add("confluence.configuration.showWindParticles", "Wind Particles Ratio");
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
        add("confluence.configuration.leftEffectIcon", "Left Potion Effect Icon");
        add("confluence.configuration.Entity", "Entity Effects");
        add("confluence.configuration.bloodyEffect", "Blood Effect");
        add("confluence.configuration.bloodyEffect.tooltip", "Blood particle splashing");
        add("confluence.configuration.goreEffect", "Gore Effect");
        add("confluence.configuration.goreEffect.off", "Off");
        add("confluence.configuration.goreEffect.confluence", "Only Conflux Entities");
        add("confluence.configuration.goreEffect.confluence_vanilla", "Conflux and Vanilla Entities");
        add("confluence.configuration.goreEffect.all", "All Entities");
        add("confluence.configuration.goreEffect.tooltip", "The gore effect will be specially adapted to Conflux and Vanilla entities, while other mod entities will use a generic method with no guaranteed effect.");
        add("confluence.configuration.damageIndicator", "Damage Indicator");
        add("confluence.configuration.healIndicator", "Heal Indicator");
        add("confluence.configuration.damageIndicator.tooltip", "Enable to display damage numbers");
        add("confluence.configuration.healIndicator.tooltip", "Enable to display heal numbers");
        add("confluence.configuration.Gameplay", "Gameplay Mechanics");
        add("confluence.configuration.Gameplay.button", "Define Gameplay Mechanics");
        add("confluence.configuration.Gameplay.tooltip", "Some gameplay mechanics can be defined by you");
        add("confluence.configuration.PlayerDeath", "Player Death Mechanics");
        add("confluence.configuration.PlayerDeath.button", "Player Death Mechanics");
        add("confluence.configuration.PlayerDeath.tooltip", "Defines the effects when a player dies");
        add("confluence.configuration.showMoneyDrops", "Show Coin Drops on Death Screen");
        add("confluence.configuration.showMoneyDrops.tooltip", "Enable to display the amount of coins dropped on the death screen");
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
        add("condition.confluence.shimmer_transmutation.before_skeletron", "Required game phase: Before Skeletron");
        add("condition.confluence.shimmer_transmutation.after_skeletron", "Required game phase: After Skeletron");
        add("condition.confluence.shimmer_transmutation.wall_of_flesh", "Required game phase: After Wall Of Flesh");
        add("condition.confluence.shimmer_transmutation.mechanical_bosses", "Required game phase: After Mechanical Bosses");
        add("condition.confluence.shimmer_transmutation.plantera", "Required game phase: After Plantera");
        add("condition.confluence.shimmer_transmutation.golem", "Required game phase: After Golem");
        add("condition.confluence.shimmer_transmutation.moon_lord", "Required game phase: After Moon Lord");
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
        add("title.confluence.fletching_table", "Fletching Table");
        add("title.confluence.touhoulittlemaid", "Touhou Little Maid Supplies");
        add("title.confluence.npc_trade", "NPC Trading");
        add("title.confluence.cooking_pot", "Cooking Pot");

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
        add("block.confluence.base_chest_block.unlocked_sandstone", "§rSandstone Chest");
        add("block.confluence.base_chest_block.death_sandstone", "§rDeath Sandstone Chest");
        add("block.confluence.base_chest_block.unlocked_living_wood", "§rLiving Wood Chest");
        add("block.confluence.base_chest_block.death_living_wood", "§rDeath Living Wood Chest");


        add("resourcepack.terraria_art", "Terraria Art");
        add("resourcepack.terraria_armor", "Terraria-Like Armor");

        add("event.confluence.blood_moon", "The Blood Moon is rising...");
        add("event.confluence.meteorite", "A meteorite has landed!");
        add("event.confluence.meteorite.ready", "A meteorite will falling!");
        add("event.confluence.shadow_orb_broken.0", "A horrible chill goes down your spine...");
        add("event.confluence.shadow_orb_broken.1", "Screams echo around you...");
        add("event.confluence.crimson_heart_broken.0", "A horrible chill goes down your spine...");
        add("event.confluence.crimson_heart_broken.1", "Screams echo around you...");
        add("event.confluence.eye_of_cthulhu", "You feel an evil presence watching you...");

        add("entity.minecraft.villager.confluence.sky_miller", "Sky Miller");
        add("entity.minecraft.villager.confluence.banker", "Banker");
        add("entity.confluence.dart", "Dart");
        add("entity.confluence.frozen_zombie", "Frozen Zombie");
        add("entity.confluence.raincoat_zombie", "Raincoat Zombie");
        add("entity.confluence.undead_miner", "Undead Miner");

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

        add(TooltipManager.prefix, "** Sponsor Item **");

        // TouhouLittleMaid

        add("task.confluence.use_life_crystal", "Use Life Crystal");
        add("task.confluence.use_life_crystal.desc", "Mail will use life crystal to heal herself");
        add("task.confluence.use_life_crystal.condition.has_life_crystal", "Mainhand holds life crystal");


        add("equipment_benediction.set_switcher.confluence.cold_crystal_set", "Cold Crystal Set");
        add("equipment_benediction.set_switcher.confluence.cold_crystal_set.data.0", "Increase maximum mana by 20, Critical Hit Rate increased by 4%");
        add("equipment_benediction.set_switcher.confluence.cold_crystal_set.data.1", "Increase maximum mana by 20, Magic Damage increased by 4%");
        add("equipment_benediction.set_switcher.confluence.cold_crystal_set.data.2", "Magic Damage increased by 4%");
        add("equipment_benediction.set_switcher.confluence.cold_crystal_set.data.3", "Critical Hit Rate increased by 4%");
        add("equipment_benediction.set_switcher.confluence.cold_crystal_set.data.4", "Magic Attack will have an additional Frostbite effect");

        add("equipment_benediction.set_switcher.confluence.mining_set", "Mining Set");
        add("equipment_benediction.set_switcher.confluence.mining_set.data.0", "Provides lighting");
        add("equipment_benediction.set_switcher.confluence.mining_set.data.1", "+10% Mining Speed");
        add("equipment_benediction.set_switcher.confluence.mining_set.data.2", "+10% Mining Speed");

        add("equipment_benediction.set_switcher.confluence.shadow_set", "Shadow Set");
        add("equipment_benediction.set_switcher.confluence.shadow_set.data.0", "Critical Hit Rate increased by 3.5%");
        add("equipment_benediction.set_switcher.confluence.shadow_set.data.1", "Critical Hit Rate increased by 3.5%");
        add("equipment_benediction.set_switcher.confluence.shadow_set.data.2", "Critical Hit Rate increased by 3.5%");
        add("equipment_benediction.set_switcher.confluence.shadow_set.data.3", "Critical Hit Rate increased by 3.5%");
        add("equipment_benediction.set_switcher.confluence.shadow_set.data.4", "Increased movement speed and acceleration");

        add("equipment_benediction.set_switcher.confluence.crimson_set", "Crimson Set");
        add("equipment_benediction.set_switcher.confluence.crimson_set.data.0", "Damage increased by 2%");
        add("equipment_benediction.set_switcher.confluence.crimson_set.data.1", "Damage increased by 2%");
        add("equipment_benediction.set_switcher.confluence.crimson_set.data.2", "Damage increased by 2%");
        add("equipment_benediction.set_switcher.confluence.crimson_set.data.3", "Damage increased by 2%");
        add("equipment_benediction.set_switcher.confluence.crimson_set.data.4", "Increases regeneration by 50%");

        add("equipment_benediction.set_switcher.confluence.snow_set", "Snowproof Set");
        add("equipment_benediction.set_switcher.confluence.snow_set.data.0", "Immunity to Frozen-type debuffs");
        add("equipment_benediction.set_switcher.confluence.snow_set.data.1", "Immunity to Frozen-type debuffs");

        add("equipment_benediction.set_switcher.confluence.bee_set", "Bee Set");
        add("equipment_benediction.set_switcher.confluence.bee_set.data.0", "Summon damage increased by 4%, Summon limit increased by 1");
        add("equipment_benediction.set_switcher.confluence.bee_set.data.1", "Summon damage increased by 4%");
        add("equipment_benediction.set_switcher.confluence.bee_set.data.2", "Summon limit increased by 1");
        add("equipment_benediction.set_switcher.confluence.bee_set.data.3", "Summon damage increased by 5%");
        add("equipment_benediction.set_switcher.confluence.bee_set.data.4", "Summon damage increased by 10%");

        add("equipment_benediction.set_switcher.confluence.molten_set", "Molten Set");
        add("equipment_benediction.set_switcher.confluence.molten_set.data.0", "Critical Hit Rate increased by 7%");
        add("equipment_benediction.set_switcher.confluence.molten_set.data.1", "Melee damage increased by 7%");
        add("equipment_benediction.set_switcher.confluence.molten_set.data.2", "Melee speed increased by 3.5%");
        add("equipment_benediction.set_switcher.confluence.molten_set.data.3", "Melee speed increased by 3.5%");
        add("equipment_benediction.set_switcher.confluence.molten_set.data.4", "Melee damage increased by 10%, Immunity to Fire");

        add("equipment_benediction.set_switcher.confluence.meteor_set", "Meteor Set");
        add("equipment_benediction.set_switcher.confluence.meteor_set.data.0", "Magic damage increased by 7%");
        add("equipment_benediction.set_switcher.confluence.meteor_set.data.1", "Magic damage increased by 7%");
        add("equipment_benediction.set_switcher.confluence.meteor_set.data.2", "Magic damage increased by 7%");
        add("equipment_benediction.set_switcher.confluence.meteor_set.data.3", "Magic damage increased by 7%");
        add("equipment_benediction.set_switcher.confluence.meteor_set.data.4", "Space Gun magic consumption reduced to 0");

        add("equipment_benediction.set_switcher.confluence.gladiator_set", "Gladiator Set");
        add("equipment_benediction.set_switcher.confluence.gladiator_set.data.0", "Immunity to Knockback");

        add("equipment_benediction.set_switcher.confluence.fossil_set", "Fossil Set");
        add("equipment_benediction.set_switcher.confluence.fossil_set.data.0", "Critical Hit Rate increased by 4%");
        add("equipment_benediction.set_switcher.confluence.fossil_set.data.1", "Ranged damage increased by 2.5%");
        add("equipment_benediction.set_switcher.confluence.fossil_set.data.2", "Ranged damage increased by 2.5%");
        add("equipment_benediction.set_switcher.confluence.fossil_set.data.3", "Critical Hit Rate increased by 4%");
        add("equipment_benediction.set_switcher.confluence.fossil_set.data.4", "Chance of not consuming ammo increased by 20%");

        add("equipment_benediction.set_switcher.confluence.ninja_set", "Ninja Set");
        add("equipment_benediction.set_switcher.confluence.ninja_set.data.0", "Critical Hit Rate increased by 2%");
        add("equipment_benediction.set_switcher.confluence.ninja_set.data.1", "Critical Hit Rate increased by 2%");
        add("equipment_benediction.set_switcher.confluence.ninja_set.data.2", "Critical Hit Rate increased by 2%");
        add("equipment_benediction.set_switcher.confluence.ninja_set.data.3", "Critical Hit Rate increased by 2%");
        add("equipment_benediction.set_switcher.confluence.ninja_set.data.4", "Movement speed increased by 20%");

        add("equipment_benediction.set_switcher.confluence.spore_root_set", "Spore Root Set");
        add("equipment_benediction.set_switcher.confluence.spore_root_set.data.0", "Summon damage increased by 2%, Summon limit increased by 1");
        add("equipment_benediction.set_switcher.confluence.spore_root_set.data.1", "Summon damage increased by 3%");
        add("equipment_benediction.set_switcher.confluence.spore_root_set.data.2", "Summon damage increased by 3%");
        add("equipment_benediction.set_switcher.confluence.spore_root_set.data.3", "Summon damage increased by 2%");
        add("equipment_benediction.set_switcher.confluence.spore_root_set.data.4", "Summon limit increased by 1");

        add("equipment_benediction.set_switcher.confluence.cactus_set", "Cactus Set");
        add("equipment_benediction.set_switcher.confluence.cactus_set.data.0", "Attackers take damage from cactus thorns");


        PonderHelper.addTranslateKeys(this::add, true);


        Consumer<DeferredHolder<Block, ? extends Block>> blockAction = block -> add(block.get(), toTitleCase(block.getId().getPath()));
        add(ModBlocks.COPPER_COIN_PILE.get(), "Copper Coin");
        add(ModBlocks.SILVER_COIN_PILE.get(), "Silver Coin");
        add(ModBlocks.GOLDEN_COIN_PILE.get(), "Golden Coin");
        add(ModBlocks.PLATINUM_COIN_PILE.get(), "Platinum Coin");
        add(ModBlocks.EMERALD_COIN_PILE.get(), "Emerald Coin");
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
        BowItems.ITEMS.getEntries().forEach(itemAction);
        ConsumableItems.ITEMS.getEntries().forEach(itemAction);
        VanityArmorItems.ITEMS.getEntries().forEach(itemAction);
        DrillItems.ITEMS.getEntries().forEach(itemAction);
        FishingPoleItems.ITEMS.getEntries().forEach(itemAction);
        FoodItems.ITEMS.getEntries().forEach(itemAction);
        HamaxeItems.ITEMS.getEntries().forEach(itemAction);
        HammerItems.ITEMS.getEntries().forEach(itemAction);
        HoeItems.ITEMS.getEntries().forEach(itemAction);
        HookItems.ITEMS.getEntries().forEach(itemAction);
        IconItems.ITEMS.getEntries().forEach(itemAction);
        LightPetItems.ITEMS.getEntries().forEach(itemAction);
        ManaWeaponItems.ITEMS.getEntries().forEach(itemAction);
        MaterialItems.ITEMS.getEntries().forEach(itemAction);
        MinecartItems.ITEMS.getEntries().forEach(itemAction);
        ModItems.ITEMS.getEntries().forEach(itemAction);
        ModItems.HIDDEN.getEntries().forEach(itemAction);
        ModItems.BLOCK_ITEMS.getEntries().forEach(itemAction);
        PaintItems.ITEMS.getEntries().forEach(itemAction);
        PickaxeAxeItems.ITEMS.getEntries().forEach(itemAction);
        PickaxeItems.ITEMS.getEntries().forEach(itemAction);
        PotionItems.ITEMS.getEntries().forEach(itemAction);
        QuestedFishes.ITEMS.getEntries().forEach(itemAction);
        ShovelItems.ITEMS.getEntries().forEach(itemAction);
        SwordItems.ITEMS.getEntries().forEach(itemAction);
        ToolItems.ITEMS.getEntries().forEach(itemAction);
        TreasureBagItems.ITEMS.getEntries().forEach(itemAction);

        ModEffects.EFFECTS.getEntries().forEach(effect -> add(effect.get(), toTitleCase(effect.getId().getPath())));
        ModEntities.ENTITIES.getEntries().forEach(entity -> add(entity.get(), toTitleCase(entity.getId().getPath())));
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
