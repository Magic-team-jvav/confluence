package org.confluence.mod.common.data.gen;

import com.google.common.collect.Iterables;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.BoxBlocks;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.mixin.accessor.LanguageProviderAccessor;

import java.util.Arrays;
import java.util.Map;
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

        add("config.jade.plugin_confluence.jade_mechanical_component", "Mechanical Info");

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

        add("item.confluence.meteorite_ingot.tooltip", "Warm to the touch");
        add("item.confluence.alpha.desc", "C418 - alpha");

        add("bossevent.confluence.boss_generate", "The %s has awakened!");
        add("bossevent.confluence.boss_death", "The %s been defeated!");
        add("bossevent.confluence.cthulhu_eye.leave", "The CthulhuEye leaved!");

        add("info.confluence.weather_radio.clear", "Weather: Clear, Wind Speed: %s");
        add("info.confluence.weather_radio.cloudy", "Weather: Cloudy, Wind Speed: %s");
        add("info.confluence.weather_radio.rain", "Weather: Rain, Wind Speed: %s");
        add("info.confluence.weather_radio.snow", "Weather: Snow, Wind Speed: %s");
        add("info.confluence.weather_radio.thunder", "Weather: Thunder, Wind Speed: %s");
        add("info.confluence.bait", "Bait Power: %s%%");
        add("info.confluence.network", "#%s Signal: %s");
        add("info.confluence.respawn_time", "Respawn Time: ");
        add("info.confluence.second", "s");

        add("key.confluence.hook", "Throwing Hook");

        add("curios.identifier.hook", "Hook");

        add("item.confluence.vanilla_potion", "Potion");
        add("item.confluence.magic_conch.pos", "Pos: [%s, %s, %s]");

        add("death.attack.falling_star", "%1$s got a response from a meteor");
        add("death.attack.boulder", "%1$s  is crushed by boulder");
        add("death.attack.thron", "%1$s  discovered that he had become a hedgehog.");

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
        add("tooltip.item.confluence.guide_to_plant_fiber_cordage.0", "Can precisely collect vines");
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
        add("painting.confluence.a_pigeon_delight.author", "BiliBili_一只鸽子悦");
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
        add("achievements.confluence.dungeon_heist.description", "Steal a key from the dungeon's undead denizens, and unlock one of their precious golden chests.");
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

        add("resourcepack.terraria_art", "Terraria Art");
        add("resourcepack.mainstream_connected_ores", "Mainstream Connected Ores");
        add("resourcepack.ter_armor", "Mainstream Terraria Armor Textures");

        add("event.confluence.blood_moon", "The Blood Moon is rising...");

        add("attribute.name.player.minion_capacity", "Minion Capacity");
        add("attribute.name.player.sentry_capacity", "Sentry Capacity");
        add("attribute.name.player.summon_damage", "Summon Damage");
        add("attribute.name.player.summon_knockback", "Summon Knockback");
        add("attribute.name.player.whip_range", "Whip Range");

        add("entity.minecraft.villager.confluence.sky_miller", "Sky Miller");

        add("container.confluence.workshop", "Workshop");
        add("title.confluence.workshop", "Workshop");

        add("gamerule.confluenceSpreadableChance", "Confluence Spreadable Chance");
        add("generator.confluence.the_corruption", "The Corruption");
        add("generator.confluence.tr_crimson", "The Crimson");

        add("title.confluence.wiki", "Confluence Wiki");
        add("wiki.confluence.back", "Return");
        add("title.confluence.group", "Group Wiki");
        add("wiki.confluence.item", "Items");
        add("wiki.confluence.type_accessories", "Accessories");
        add("wiki.confluence.type_arrow", "Arrows");
        add("wiki.confluence.type_axe", "Axes");
        add("wiki.confluence.type_bait", "Baits");
        add("wiki.confluence.type_bow", "Bows");
        add("wiki.confluence.type_fishing_pole", "Fishing Poles");
        add("wiki.confluence.type_food", "Foods");
        add("wiki.confluence.type_material", "Materials");
        add("wiki.confluence.type_misc", "Misc");
        add("wiki.confluence.type_quested_fish", "Quested Fish");
        add("wiki.confluence.type_sword", "Swords");
        add("wiki.confluence.type_terra_potion", "Terra Potions");
        add("wiki.confluence.setDamage", "Attack Damage: ");
        add("wiki.confluence.use", "Durability: ");
        add("wiki.confluence.speed", "Use Speed: ");
        add("wiki.confluence.enchantment", "Enchantment Level: ");
        add("wiki.confluence.ingredient", "Repair Ingredient: ");
        add("wiki.confluence.power", "Pickaxe Power: ");
        add("wiki.confluence.nutrition", "Nutrition: ");
        add("wiki.confluence.saturation", "Saturation: ");

        add("wiki.confluence.copper_short_sword", """
                The Copper Shortsword is an early-game metal shortsword.
                Its alternate ore counterpart is the Tin Shortsword.
                
                Like all shortswords, the Copper Shortsword has a very limited range
                and attacks with a stabbing motion at any direction / horizontally
                in front of the player, instead of an arc.
                
                This makes it almost useless against flying or jumping enemies,
                although its high attack rate makes it
                somewhat effective against weaker fighter type enemies.
                
                Its best boomerangModifier is Legendary.""");

        BoxBlocks.BLOCKS.getEntries().forEach(block -> add(block.get(), toTitleCase(block.getId().getPath())));
        DecorativeBlocks.BLOCKS.getEntries().forEach(block -> add(block.get(), toTitleCase(block.getId().getPath())));
        OreBlocks.BLOCKS.getEntries().forEach(block -> add(block.get(), toTitleCase(block.getId().getPath())));

        ModItems.ITEMS.getEntries().forEach(item -> {
            Item item1 = item.get();
            if (item1 instanceof BlockItem) return;
            add(item1, toTitleCase(item.getId().getPath()));
        });
        Map<String, String> data = ((LanguageProviderAccessor) this).getData();
        add(AccessoryItems.PHILOSOPHERS_STONE.get(), "Philosopher's Stone");
        AccessoryItems.ITEMS.getEntries().forEach(accessory -> {
            Item item = accessory.get();
            String descriptionId = item.getDescriptionId();
            if (data.containsKey(descriptionId)) return;
            add(descriptionId, toTitleCase(accessory.getId().getPath()));
        });
        SwordItems.ITEMS.getEntries().forEach(sword -> add(sword.get(), toTitleCase(sword.getId().getPath())));
        ArrowItems.ITEMS.getEntries().forEach(arrow -> add(arrow.get(), toTitleCase(arrow.getId().getPath())));
        BowItems.ITEMS.getEntries().forEach(bow -> add(bow.get(), toTitleCase(bow.getId().getPath())));
        PotionItems.ITEMS.getEntries().forEach(potion -> add(potion.get(), toTitleCase(potion.getId().getPath())));
    }
}
