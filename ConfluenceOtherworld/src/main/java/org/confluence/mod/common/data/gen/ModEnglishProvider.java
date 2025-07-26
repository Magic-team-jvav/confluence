package org.confluence.mod.common.data.gen;

import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.lib.mixin.accessor.LanguageProviderAccessor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.TooltipManager;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.integration.create.ponder.PonderHelper;
import org.confluence.mod.integration.waystones.WaystonesHelper;

import java.util.function.Consumer;

import static org.confluence.mod.common.component.prefix.ModPrefix.GROUPS;

public class ModEnglishProvider extends LanguageProvider {
    public ModEnglishProvider(PackOutput output) {
        super(output, Confluence.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("config.jade.plugin_confluence.jade_network_component", "Mechanical Info");
        add("config.jade.plugin_confluence.jade_ponder_component", "Ponder Info");
        add("config.jade.plugin_confluence.jade_tombstone_info", "Tombstone Info");

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
        add("creativetab.confluence.mechanical", "Confluence | Mechanical");
        add("creativetab.confluence.developer", "Confluence | Developer");

        add("chat.type.advancement.achievement", "%s has achieved the achievement %s");
        add("chat.confluence.magic_conch", "The location where you listen to the sound of the ocean [%s] has been recorded");
        add("chat.confluence.demon_conch", "The location where you listen to the sound of the demon [%s] has been recorded");
        add("options.difficulty.legendary", "§aLegendary");
        add("message.confluence.choking", "You're choking and need to drink water");
        add("message.confluence.advancement_combat_techniques", "The book's knowledge empowers your villagers!");
        add("message.confluence.toolmode.tip", "Shift & Right click on the air to switch mode");
        add("message.confluence.toolmode.current", "Current Mode: ");
        add("message.confluence.hamaxe.mode.0", "Hammer & Axe");
        add("message.confluence.hamaxe.mode.1", "Axe");
        add("message.confluence.hoe_shovel.mode.0", "Shovel");
        add("message.confluence.hoe_shovel.mode.1", "Hoe");
        add("message.confluence.altar_tips.0", " put in an item, ");
        add("message.confluence.altar_tips.1", " take out an item, ");
        add("message.confluence.altar_tips.2", " craft, ");
        add("message.confluence.altar_tips.3", " quickly craft");
        add("message.confluence.lock.need", "need: ");
        add("message.confluence.lock.or", " or ");
        add("message.confluence.dungeon_not_found", "Failed to find the Dungeon");

        add("commands.confluence.reforge.cannot_be_reforged", "This item cannot be reforged (or cannot find an item that needs to be reforged)!");
        add("commands.confluence.reforge.unknown_prefix_type", "Unknown prefix type (or reforge failure)!");
        add("commands.confluence.reforge.success", "Successfully reforged to: %s");
        add("commands.confluence.reforge.clear.success", "The prefix has been successfully cleared");
        add("commands.confluence.reforge.set.unavailable_group", "This item cannot have this prefix applied!");

        add("enchantment.confluence.mana_regeneration", "Mana Regeneration");
        add("enchantment.confluence.mana_regeneration.desc", "Increases the amount of mana that naturally regenerates");
        add("enchantment.confluence.efficient_magic", "Efficient Magic");
        add("enchantment.confluence.efficient_magic.desc", "The less mana, the less mana is consumed");
        add("enchantment.confluence.mana_mending", "Mana Mending");
        add("enchantment.confluence.mana_mending.desc", "When the mana used once exceeds the threshold, the item's durability is restored, and the amount recovered is rounded down to the amount above the threshold");
        add("enchantment.confluence.celestial_absorption", "Celestial Absorption");
        add("enchantment.confluence.celestial_absorption.desc", "Magic weapons that deal magic damage have a chance to drop stars");
        add("enchantment.confluence.soothed_mana", "Soothed Mana");
        add("enchantment.confluence.soothed_mana.desc", "Reduces the duration of Mana Sickness");
        add("enchantment.confluence.arcane_protection", "Arcane Protection");
        add("enchantment.confluence.arcane_protection.desc", "The mana drawn when taking damage is the value of the maximum mana value multiplied by a certain ratio, and the damage canceled is the value of the product of the damage taken by that ratio");
        add("enchantment.confluence.spell_desperation", "Spell Desperation");
        add("enchantment.confluence.spell_desperation.desc", "The lower the remaining mana ratio, the higher the attack");
        add("enchantment.confluence.mystic_surge", "Mystic Surge");
        add("enchantment.confluence.mystic_surge.desc", "The higher the remaining mana ratio, the higher the attack");

        add("gamerule.confluenceSpreadableChance", "Confluence Spreadable Chance");
        add("generator.confluence.the_corruption", "The Corruption");
        add("generator.confluence.the_crimson", "The Crimson");

        add("tooltip.price.platinum", "Platinum ");
        add("tooltip.price.gold", "Gold ");
        add("tooltip.price.silver", "Silver ");
        add("tooltip.price.copper", "Copper ");
        add("tooltip.price.sell", "Sell: ");

        add("tooltip.jei.state_properties", "Required State Properties:");
        add("tooltip.jei.count_range", "Count: %s-%s");
        add("tooltip.jei.count_exact", "Count: %s");
        add("tooltip.jei.drop_chance", "Chance: %s%%");

        add("tooltip.item.confluence.meteorite_ingot.0", "Warm to the touch");
        add("tooltip.item.confluence.encumbering_stone.0", "Prevents item pickups while locked");
        add("tooltip.item.confluence.encumbering_stone.1", "Right click in inventory to unlock");
        add("tooltip.item.confluence.encumbering_stone.2", "'You are over-encumbered'");
        add("tooltip.item.confluence.super_absorbant_sponge.0", "Capable of soaking up an endless amount of water");
        add("tooltip.item.confluence.honey_absorbant_sponge.0", "Capable of soaking up an endless amount of honey");
        add("tooltip.item.confluence.lava_absorbant_sponge.0", "Capable of soaking up an endless amount of lava");
        add("tooltip.item.confluence.ultra_absorbant_sponge.0", "Capable of soaking up an endless amount of liquid");
        add("tooltip.item.confluence.wire_cutter.0", "Removes wire");
        add("tooltip.item.confluence.extractinator.0", "Placing silt/slush/fossil/gravel/marine_gravel piles into the extractinator turns them into something more useful");
        add("tooltip.item.confluence.sky_mill.0", "Used for special crafting");
        add("tooltip.item.confluence.heavy_work_bench.0", "Used for advanced crafting");
        add("tooltip.item.confluence.sawmill.0", "Used for advanced wood crafting");
        add("tooltip.item.confluence.alchemy_table.0", "33% chance to not consume potion crafting ingredients");
        add("tooltip.item.confluence.solidifier.0", "Used to craft objects");
        add("tooltip.item.confluence.hardmode_anvil.0", "Used to craft items from mythril, orichalcum, adamantite, and titanium ingots");
        add("tooltip.item.confluence.sharpening_station.0", "Increases armor penetration for melee weapons");
        add("tooltip.item.confluence.ammo_box.0", "20% chance to save ammo");
        add("tooltip.item.confluence.bewitching_table.0", "Right click to have more minions");
        add("tooltip.item.confluence.keg.0", "Used for brewing ale");
        add("tooltip.item.confluence.chlorophyte_extractinator.0", "Placing silt/slush/fossil/gravel/marine_gravel piles into the extractinator turns them into something more useful");
        add("tooltip.item.confluence.chlorophyte_extractinator.1", "Place contaminated blocks into the extractinator to purify them");
        add("tooltip.item.confluence.chlorophyte_extractinator.2", "Other items placed inside may have interesting effects");
        add("tooltip.item.confluence.blend_o_matic.0", "Used to craft objects");
        add("tooltip.item.confluence.meat_grinder.0", "Used to craft objects");
        add("tooltip.item.confluence.life_campfire.0", "Life regen is increased when near a campfire");
        add("tooltip.item.confluence.piggy_bank.0", "Can be used to store your items");
        add("tooltip.item.confluence.piggy_bank.1", "Stored items can only be accessed by you");
        add("tooltip.item.confluence.safe.0", "Can be used to store your items");
        add("tooltip.item.confluence.safe.1", "Stored items can only be accessed by you");
        add("tooltip.item.confluence.echo_block.0", "Only visible with Echo Sight");
        add("tooltip.item.confluence.advanced_combat_techniques.0", "Increases the defense and strength of all villagers");
        add("tooltip.item.confluence.advanced_combat_techniques.1", "'Contains offensive and defensive fighting techniques'");
        add("tooltip.item.confluence.advanced_combat_techniques_volume_two.0", "Increases the defense and strength of all villagers");
        add("tooltip.item.confluence.advanced_combat_techniques_volume_two.1", "'Contains offensive and defensive fighting techniques, volume two!'");
        add("tooltip.item.confluence.binoculars.0", "Expand the FOV when in use, and adjust the zoom with the mouse wheel");
        add("tooltip.item.confluence.meteor_compass.0", "Only the position of the last meteorite are saved");
        add("tooltip.item.confluence.gel.0", "'Both tasty and flammable'");
        add("tooltip.item.confluence.npc_invitation.0", "Use it to invite a new batch of NPCs in the current area!");
        add("tooltip.item.confluence.red_potion.0", "'Only for those who are worthy'");
        add("tooltip.item.confluence.mug.0", "Collect ale at the Keg");
        add("tooltip.item.confluence.hellforge.0", "Used for smelting hellstone materials. Smelting regular minerals has a residual heat effect.");
        add("tooltip.item.confluence.magic_conch.0", "Right-click a block in the Beach biome to make the sea remember you");
        add("tooltip.item.confluence.demon_conch.0", "Right-click a Nether Portal block to make the Nether remember you.");
        add("tooltip.item.confluence.bait.common.0", "When placed in the inventory, it will be automatically used while fishing, prioritizing the bait in the off-hand.");
        add("tooltip.item.confluence.crate.common.0", "Hold the right mouse button to open, and hold Shift while clicking the right mouse button to place.");
        add("tooltip.item.confluence.right_click.common.0", "Hold the right mouse button to open.");
        add("tooltip.item.confluence.raw_asphalt.0", "Use a Blend-O-Matic to make asphalt blocks");
        add("tooltip.item.confluence.empty_dropper.0", "Right-click on the droplet to remove it (requires block assist aiming)");

        add("tooltip.item.confluence.slime_crown.0", "Right - click to summon the King Slime");
        add("tooltip.item.confluence.slime_crown.1", "A small crown that seems to be prepared for the coronation ceremony of those cute and harmless gel - like creatures.");
        add("tooltip.item.confluence.slime_crown.2", "“Wearing it may not be a good choice”");
        add("tooltip.item.confluence.suspicious_looking_eye.0", "Right - click to summon the Eye of Cthulhu. It only appears at night.");
        add("tooltip.item.confluence.suspicious_looking_eye.1", "A lifeless and dull - looking eyeball. Although it is not aggressive, it seems more dangerous than its peers that fly in the air at night.");
        add("tooltip.item.confluence.suspicious_looking_eye.2", "“It seems to be looking at you…”");
        add("tooltip.item.confluence.worm_food.0", "Right - click to summon the Eater of Worlds. It shuttles through the huge rift filled with corruptive poisonous fog.");
        add("tooltip.item.confluence.worm_food.1", "It smells like a rotten rib and seems to have a strong attraction to those mutated Terraria creatures.");
        add("tooltip.item.confluence.worm_food.2", "“It wants to devour more... more and more and more and more…”");
        add("tooltip.item.confluence.bloody_spine.0", "Right - click to summon the Brain of Cthulhu. It will wake up after smelling the smell of flesh and pus in the Crimson.");
        add("tooltip.item.confluence.bloody_spine.1", "A piece of detached body tissue, mottled with scabs and muscle tissue. It's hard to imagine how it was taken out of which creature.");
        add("tooltip.item.confluence.bloody_spine.2", "“It seems to be trying to talk to you... or maybe...”");
        add("tooltip.item.confluence.abeemination.0", "Right - click to summon the Queen Bee. Only the dirty miasma in the tropical broad - leaved rainforest can suppress her fury.");
        add("tooltip.item.confluence.abeemination.1", "She seems to extremely dislike the smell of fluorescent fungal spores.");
        add("tooltip.item.confluence.abeemination.2", "A mass of unformed young bees. It feels like sticky honey when you touch it... The Queen Bee and her subordinates' protectiveness of their sweet territory has somehow gradually developed into extreme repulsion and hatred towards non - similar creatures.");
        add("tooltip.item.confluence.abeemination.3", "“The flapping of the bees' wings shakes the thickest leaves in the jungle”");
        add("tooltip.item.confluence.clothier_voodoo_doll.0", "'You are a terrible person'");
        add("tooltip.item.confluence.guide_voodoo_doll.0", "'You are a terrible person'");

        add("tooltip.item.confluence.tokyo_teddy_bear.0", "A self - abased girl said like a broken teddy bear:");
        add("tooltip.item.confluence.tokyo_teddy_bear.1", "           Let me tell you");
        add("tooltip.item.confluence.tokyo_teddy_bear.2", "           The words of all - knowing and all - powerful");
        add("tooltip.item.confluence.tokyo_teddy_bear.3", "           Other than the mind");
        add("tooltip.item.confluence.tokyo_teddy_bear.4", "           Is no longer needed");
        add("tooltip.item.confluence.tokyo_teddy_bear.5", "——A story told by a spider");
        add("tooltip.item.confluence.paradox_interactive_medal.0", "Proof of having played Hearts of Iron, Victoria, Europa Universalis, Crusader Kings, and Cities: Skylines at the same time.");
        add("tooltip.item.confluence.kind_miside_ring.0", "“The ring will lead you to the right direction, dear”");
        add("tooltip.item.confluence.failed_skull.0", "A creeper had its body forcibly transformed by piglins and can explode multiple times. The piglins wanted to use it as a biological weapon to invade the Overworld, but it escaped unexpectedly.");
        add("tooltip.item.confluence.ice_tofu_brick.0", "它现在不能吃了，但不妨碍它把你吃了");
        add("tooltip.item.confluence.pink_cola.0", "An ordinary bottle of pink cola. Maybe there was a whole case originally?");
        add("tooltip.item.confluence.dongdongs_flatbread.0", "Freshly baked flatbread on the Netherrack. Come and have a taste!");
        add("tooltip.item.confluence.boredoms_pact_falling_resolve.0", "「无聊之咒·陨志」");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.1", "(Boredom's Pact - Falling Resolve)");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.2", "           ");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.3", "The blood of the indolent has soaked into the stardust core, condensing into this breathing cursed stone.");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.4", "When in motion, the veins of the earth surge, and blades cleave through the long night; when still, the earth's heart beats, and the sky opens its single eye.");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.5", "The ancient god inscribed punishment into the contract: eight heartbeats of stillness summon the judgment of a falling star.");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.6", "It doesn't remain silent like a golem - it cackles when boulders shatter shinbones:");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.7", "'Look, even the stones understand the way of survival better than your legs.'");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.8", "The wearer will eventually understand that so - called 'invincibility' merely means outrunning death by a single second.");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.9", "And the soul has long been crushed into dust in the rock crevices, more desolate than the empty shell of a golem.");

        // Text items ↓
        add("item.confluence.afterlife_notes", "Afterlife Notes");
        add("item.confluence.village_exploration", "Origin of Village Exploration");
        add("item.confluence.research_on_wheat_mutation", "Research on Wheat Mutation");
        add("item.confluence.research_on_cloud_blocks_1", "Research on Cloud Blocks I");
        add("item.confluence.research_on_cloud_blocks_2", "Research on Cloud Blocks II");
        add("item.confluence.meteor_diary", "Meteor Diary");

        // note
        add("item.confluence.mysterious_note.name_0", "Small Note with Bite Marks");
        add("lore.confluence.mysterious_note_0", "Oops, you've found it. Don't tell others that the puppy is hiding here UQYQU");
        add("item.confluence.mysterious_note.name_1", "Small Note with Orange Scent");
        add("lore.confluence.mysterious_note_1", "Hey, since you've found it, I'll tell you. In fact, one of the animals sleeping with you is probably an informant for the prison pillow.");
        add("item.confluence.mysterious_note.name_2", "Digitized Small Note");
        add("lore.confluence.mysterious_note_2", "The original brave didn't ask for anything in return. They only hoped that all souls yearning for adventure could freely pick up these precious gifts and shine their own light in the adventure. Please guard this kindness from the brave and don't let the shadow of interests obscure the light of freedom and hope in the afterlife.");
        add("item.confluence.mysterious_note.name_3", "Gentle Small Note");
        add("lore.confluence.mysterious_note_3", "I hope that the adventurer who sees this note can have a smooth study, a successful job, and everything goes well in life. No matter what the haze is, it will pass quickly because we all have to move forward towards tomorrow.");
        add("item.confluence.mysterious_note.name_4", "Half-Eaten Small Note");
        add("lore.confluence.mysterious_note_4", "When will Confluence Fun Stuff start production? I've been waiting until I'm stupid.");


        add("lore.confluence.mysterious_note.handwriting_0", "✒Rushed handwriting");
        add("lore.confluence.mysterious_note.handwriting_1", "✒Neat handwriting");
        add("lore.confluence.mysterious_note.handwriting_2", "✒Graceful handwriting");
        add("lore.confluence.mysterious_note.handwriting_3", "✒Mysterious handwriting");

        add("item.confluence.mysterious_note.name_structure_0", "The note left in the dungeon");
        add("item.confluence.mysterious_note.name_structure_1", "The note left in the chest");

        add("lore.confluence.mysterious_note_structure_0_0", "I have secretly investigated this dungeon... Instead of being considered a dungeon, it is rather seen as a long lost city. Besides the creaking skeletons, there are several buildings like apartments and a wide variety of public structures. These skeletons are said to be the locals here but nobody knows how they could've fallen into this situation...");
        add("lore.confluence.mysterious_note_structure_0_1", "However, indeed, the wonder is those rare rooms which look like armories with a huge sword on top. Each \"armory\" contains plenty equipment, with a locked chest... which I am unable to open but it lets me know that it contains valuables.");
        add("lore.confluence.mysterious_note_structure_1_0", "Those terrifying monsters are truly mad! I wish to leave this place... However, I found some keys in these strange structures resembling churches and those disgusting slimy monsters... There must be treasure here, but I haven't found it yet...");
        add("lore.confluence.mysterious_note_structure_2_0", "Mysterious clues of an evil ritual were etched into the enigmatic murals I found within the ancient ruins… I sketched them down. I had no idea what these so-called \"Crying Obsidian\" were before, but now I’ve finally found them on these towering, ancient doorframes… There must be countless secrets in this world that I still don’t know about. Additionally, near the patterns of the murals, I discovered several inscriptions. \"Confusion, confusion, look towards the direction of the falling entity. The guide that directs you to the extraterrestrial object can still offer you new guidance. Sacrifice, using the weeping stone deity. Behold, its deep, lifeless eye sockets, pointing towards its homeland, long since departed.\"");

        add("item.confluence.mysterious_slate.name_0", "Serious Slate");
        add("lore.confluence.mysterious_slate_0", "“A fun fact: In fact, summoners need to communicate with their summoned creatures spiritually. The armor protection can't be too thick, otherwise it will affect the communication.”");
        add("item.confluence.mysterious_slate.name_1", "Exceptionally Ancient Slate");
        add("lore.confluence.mysterious_slate_1", "“It all started when the Moon Lord sold the... (The content is covered by blood, and the whole picture can't be seen clearly)”");

        add("text.building_0", "Under construction");
        add("text.building_1", "Version %s is expected to be complete");
        add("text.building_2", "Don't come close!");

        add("text.confluence.afterlife_notes", "Adventurer, the new world is full of endless challenges and opportunities. This notebook will help you understand the mysteries of this world and guide you in the face of monsters and difficulties. Only by continuous exploration can you discover more power and treasures. Your journey has just begun. —— Guide");
        add("text.confluence.village_exploration", "The world's mutation has quietly arrived. The dark evil thoughts of living beings have erupted one after another, and the physical invasions from the outside world have followed in quick succession. All available resources have been awakened. The arrival of the new world brings both the shadow of destruction and opens up new possibilities. Buildings soar into the sky like flying birds, which is amazing. The clouds that were once out of reach have now turned into solid blocks, reflecting the longings in people's hearts. On the journey of exploration, they have mastered unprecedented knowledge and discovered new plants, as if they have found a corner of tranquility in the hustle and bustle. In that area");
        add("text.confluence.village_exploration_0", "On the pure land, new hope is quietly germinating, bringing long - lost peace.");
        add("text.confluence.research_on_wheat_mutation", "We found that the wheat we brought began to turn white and yellow. At first, we thought they couldn't adapt to this strange environment. Until a cloud block floated by gently, it dyed the wheat with the colors of the rosy clouds and transformed it into a new kind of plant. We can't help but be skeptical about this mutated product - until someone is hungry and eager for food. Strangely enough, even though they are full, their bodies seem to become lighter, as if gradually getting away from the burden of the earth. In this strange world, changes and confusions are intertwined, and we begin to re - examine");
        add("text.confluence.research_on_wheat_mutation_0", "Re - examine the essence of food and the miracle of life.");
        add("text.confluence.research_on_cloud_blocks_1", "Cloud blocks, non - toxic, are composed of ice crystals condensed in the sky, with different contents. After a long - term contact with the alien planet, these clouds have gradually become solid and can bear the weight of an adult, protecting them from the impact of strong kinetic energy. However, the plants nearby have begun to mutate, and we are still confused about how this strange change came about. At this mysterious intersection, the clouds and plants weave an unknown story, as if nature is quietly writing a new chapter.");
        add("text.confluence.research_on_cloud_blocks_2", "As the research deepens, we gradually find that cloud blocks and a plant called cloud - weaving grass are actually of the same nature. Cloud - weaving grass grows on cloud blocks, quietly collecting water vapor in the high - altitude until a new cloud block is born. Now, this plant has been transplanted to the cloud flowerbed and has become an important building resource for us. In this mysterious space, plants and clouds blend, weaving endless possibilities and delivering the power to build dreams to the earth.");
        add("text.confluence.meteor_diary", "They cut through the night sky, making monsters afraid; while we often make quiet wishes on meteors. Perhaps, they really have invisible magic. Children look up, full of joy, chasing that faint light; they grow quietly from the clouds and then gently fall from the clouds. On this night stage, meteors twinkle with the light of hope, warming every expectant heart.");

        add("lore.confluence.village_exploration", "It's hard to tell the exact age, but it seems to be very well - packaged...");
        add("lore.confluence.research_on_wheat_mutation", "There are some powders mixed in the pages, but it doesn't seem to be the powder from the aging of the pages...");
        add("lore.confluence.research_on_cloud_blocks_1", "It's obviously a very thick book, but it feels so light in the hand. Judging from the title, it seems there is another volume?");
        add("lore.confluence.research_on_cloud_blocks_2", "The pages feel very soft, just like silk. Judging from the title, it seems there is another volume?");
        add("lore.confluence.meteor_diary", "It's a very thin book, but it seems to have a faint glow.");

        add("author.confluence.the_ancestor_of_explorers", "The Original Initiator");
        add("author.confluence.sheila", "Sheila");
        add("author.confluence.lorissa", "Lorissa");
        add("author.confluence.annaleigh", "Annaleigh");

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
        add("info.confluence.weather_radio.thunder_snow", "Weather: Thunder Snow, Wind Speed: %s");
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

        add("death.attack.dungeon_altar", "Steve died when he was about to click on the bottom block with a meteor compass");

        add("death.attack.falling_star", "%1$s got a response from a meteor");
        add("death.attack.boulder", "%1$s is crushed by boulder");
        add("death.attack.darkness", "%1$s was killed by something in the dark!");
        add("death.attack.summon_damage_type", "%1$s was flogged mercilessly");
        add("death.attack.summoner_damage_type", "%1$s failed to communicate in time");
        add("death.attack.frost_burn_damage_type", "%1$s felt warm before the end");
        add("death.attack.pass_armor_damage_type", "%1$s was pierced through along with their armor");

        add("selections.confluence.magic_conch", "Responding to the call of The Ocean [%s]");
        add("selections.confluence.demon_conch", "Responding to the call of The Lava [%s]");

        add("tooltip.item.confluence.adhesive_bandage.0", "Immune to bleeding");
        add("tooltip.item.confluence.medicated_bandage.0", "Immune to poison and bleeding");
        add("tooltip.item.confluence.pocket_mirror.0", "Immune to petrification");
        add("tooltip.item.confluence.reflective_shades.0", "Immune to blindness and petrification");
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
        add("tooltip.item.terra_curio.mechanical_lens.1", "Right-click in the backpack to toggle on/off.");
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
        add("tooltip.item.terra_curio.spectre_goggles.1", "Right-click in the backpack to toggle on/off.");
        add("tooltip.item.confluence.guide_to_plant_fiber_cordage.0", "Allows the collection of Vine Rope from vines");
        add("tooltip.item.confluence.fledgling_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.chromatic_cloak.0", "Immunity to Shimmer Phasing.Hold Shift to Phase while submerged in Shimmer");
        add("tooltip.item.confluence.paint_scraper", "Used to remove paint or coatings.Press Shift + Right-click to remove only one side.");
        add("tooltip.item.confluence.paint_sprayer.0", "Automatically paints or coats placed objects.");
        add("tooltip.item.confluence.coin", "Sneak through the air and right-click to merge into a primary coin");
        add("tooltip.item.confluence.hardmode_convertor.0", "Right-clicking on the ground immediately turns the current world into Hardmode");
        add("tooltip.item.confluence.life_crystal.0", "Permanently increases maximum life by 4");
        add("tooltip.item.confluence.life_fruit.0", "Permanently increases maximum life by 1");
        add("tooltip.item.confluence.mana_crystal.0", "Permanently increases maximum mana by 20");
        add("tooltip.item.confluence.arcane_crystal.0", "Permanently increases mana regeneration");
        add("tooltip.item.confluence.vital_crystal.0", "Permanently boosts life regeneration");
        add("tooltip.item.confluence.aegis_apple.0", "Permanently increases defense");
        add("tooltip.item.confluence.ambrosia.0", "Permanently increases mining and building speed");
        add("tooltip.item.confluence.gummy_worm.0", "Permanently increases fishing skill");
        add("tooltip.item.confluence.galaxy_pearl.0", "Permanently increases luck");
        add("tooltip.item.confluence.minecart_upgrade_kit.0", "Permanently grants boosted speed and a defensive probe for minecarts");
        add("tooltip.item.confluence.minecart_upgrade_kit.1", "'Free Mechanical Cart included!'");
        add("tooltip.item.confluence.artisan_loaf.0", "Consume to permanently increase block interaction range");
        add("tooltip.item.confluence.artisan_loaf.1", "'Legendary Bread that once reminded Teddy of home'");

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

        add("tooltip.confluence.damage", "Damage: %s");
        add("tooltip.confluence.mana_cost", "Mana Cost: %s");
        add("tooltip.confluence.velocity", "Projectile Speed: %s");
        add("tooltip.confluence.cooldown", "Cooldown: %s");

        add("tooltip.item.confluence.radio_thing.0", "Allows the user to see the world differently");
        add("tooltip.item.terra_curio.radio_thing.1", "'Forbidden Knowledge echoes from the radio...'");

        add("tooltip.item.confluence.sweet_sword.0", "au'undertale: above nothingness' written by 一只屑水缡");
        add("tooltip.item.confluence.piglin_stew.0", "The last thing a Piglin would crave before starving, yet they never got to taste it...");

        add("tooltip.item.confluence.copper_short_sword.0", "The smallest fragment of the divine weapon's power has been with you since the convergence of the two worlds... until the journey's end.");
        add("tooltip.item.confluence.copper_short_sword.1", "\"We are so awesome!\" said the copper short sword.");
        add("tooltip.item.confluence.umbrella.0", "You will fall slower while holding this");
        add("tooltip.item.confluence.tragic_umbrella.0", "You will fall slower while holding this");
        add("tooltip.item.confluence.breathing_reed.0", "Increases breath time and allows breathing in water");
        add("tooltip.item.confluence.starfury.0", "A small part of the divine weapon's power is condensed within the clouds, transforming into a crystalline and sparkling star.");
        add("tooltip.item.confluence.starfury.1", "\"The wrath of the heavens pours down.\"");
        add("tooltip.item.confluence.enchanted_sword.0", "A small part of the divine weapon's power is buried in a cave, condensed into a bleak sword intent.");
        add("tooltip.item.confluence.enchanted_sword.1", "\"A flash of light in the dim sword tomb.\"");
        add("tooltip.item.confluence.bee_keeper.0", "A small part of the divine weapon's power is possessed by the swarm of bees in the jungle, becoming a buzzing hive.");
        add("tooltip.item.confluence.bee_keeper.1", "\"Sweet on the outside, sharp on the inside.\"");

        add("tooltip.item.confluence.soul_of_light.0", "'The essence of light creatures'");
        add("tooltip.item.confluence.soul_of_night.0", "'The essence of dark creatures'");
        add("tooltip.item.confluence.soul_of_flight.0", "'The essence of powerful flying creatures'");
        add("tooltip.item.confluence.soul_of_might.0", "'The essence of the destroyer'");
        add("tooltip.item.confluence.soul_of_sight.0", "'The essence of omniscient watchers'");
        add("tooltip.item.confluence.soul_of_fright.0", "'The essence of pure terror'");
        add("tooltip.item.confluence.golden_key.0", "“Opens one locked Gold Chest”");
        add("tooltip.item.confluence.golden_dungeon_key.0", "“Open a locked dungeon chest or a golden lockbox”");
        add("tooltip.item.confluence.shadow_key.0", "“Opens all Shadow Chests and Obsidian Lock Boxes”");
        add("tooltip.item.confluence.temple_key.0", "“Opens the jungle temple door”");
        add("tooltip.item.confluence.jungle_key.0", "“Opens the jungle room door and jungle chest in the dungeon”");
        add("tooltip.item.confluence.corruption_key.0", "“Opens the evil room door and corruption chest in the dungeon”");
        add("tooltip.item.confluence.crimson_key.0", "“Opens the evil room door and crimson chest in the dungeon”");
        add("tooltip.item.confluence.hallowed_key.0", "“Opens the hallowed room door and hallowed chest in the dungeon”");
        add("tooltip.item.confluence.frozen_key.0", "“Opens the ice room door and ice chest in the dungeon”");
        add("tooltip.item.confluence.desert_key.0", "“Opens the desert room door and desert chest in the dungeon”");
        add("tooltip.item.confluence.ocean_key.0", "“Opens the ocean room door and ocean chest in the dungeon”");
        add("tooltip.item.confluence.universe_key.0", "“Opens the space room door and space chest in the dungeon”");
        add("tooltip.item.confluence.rust_iron_key.0", "“Opens the laboratory room door in the dungeon”");
        add("tooltip.item.confluence.mechanic_safe_key.0", "“Opens mechanic safe chest in the dungeon”");
        add("tooltip.item.confluence.dungeon_compass.0", "“Wear it, and the eerie skull will guide your way”");
        add("tooltip.item.confluence.golden_lock_box.0", "“Right click to open”");
        add("tooltip.item.confluence.golden_lock_box.1", "“Requires a Dungeon Golden Key”");
        add("tooltip.item.confluence.obsidian_lock_box.0", "“Right click to open”");
        add("tooltip.item.confluence.obsidian_lock_box.1", "“Requires a Shadow Key”");

        add("tooltip.item.confluence.angel_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.demon_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.fairy_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.fin_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.frozen_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.harpy_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.jetpack.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.leaf_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.bat_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.bee_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.butterfly_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.flame_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.hoverboard.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.bone_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.mothron_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.spectre_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.beetle_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.festive_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.spooky_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.tattered_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.steampunk_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.betsys_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.empress_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.fishron_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.nebula_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.vortex_booster.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.solar_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.stardust.0", "Allows flight and slow fall");


        add("confluence.configuration.Compatibility", "Compatibility Mechanism");
        add("confluence.configuration.Compatibility.button", "Settings for compatibility with other mods");
        add("confluence.configuration.ArsNouveau", "Ars Nouveau");
        add("confluence.configuration.IronsSpell", "Iron's Spells 'n Spellbooks");
        add("confluence.configuration.FTB", "FTB Chunks");
        add("confluence.configuration.Xaero", "Xaero's World Map");
        add("confluence.configuration.Waystones", "Waystones");
        add("confluence.configuration.convertArsNouveauMana", "Use Confluence's mana system when enabled");
        add("confluence.configuration.convertIronsSpellMana", "Use Confluence's mana system when enabled");
        add("confluence.configuration.ftbChunksWormholePotion", "Enable wormhole potion functionality");
        add("confluence.configuration.xaerosMapWormholePotion", "Enable wormhole potion functionality");
        add("confluence.configuration.xaerosMapPylonWaypoint", "Enable pylon waypoint display");
        add("confluence.configuration.waystonesPylonNonCost", "Enable pylons to not consume experience");

        add("confluence.configuration.instantlyHardmodeConversion", "Instant Hard Mode Conversion");
        add("confluence.configuration.instantlyHardmodeConversion.tooltip", "When enabled, the transition to Hard Mode will be accelerated and occur with a complete freeze. Please assess your computer's performance before enabling this configuration.");
        add("confluence.configuration.wrappedCrimson_heart", "Exposed Crimson Heart");
        add("confluence.configuration.wrappedCrimson_heart.tooltip", "When enabled, newly generated Crimson Caverns will spawn exposed Crimson Hearts");
        add("confluence.configuration.sellPriceDisplay", "Sell Price Display");
        add("confluence.configuration.sellPriceDisplay.tooltip", "Toggles the timing to see the price of the item when it is sold to NPCs");
        add("confluence.configuration.sellPriceDisplay.never", "Not displayed at any time");
        add("confluence.configuration.sellPriceDisplay.everywhere", "Displayed at all times");
        add("confluence.configuration.sellPriceDisplay.trade_screen", "Only displayed in the trading screen");
        add("confluence.configuration.Recipe", "Crafting Recipe System");
        add("confluence.configuration.Recipe.button", "Crafting Recipe System");
        add("confluence.configuration.Recipe.tooltip", "Settings related to crafting recipes");
        add("confluence.configuration.Spawning", "Spawning Mechanism");
        add("confluence.configuration.Spawning.button", "Spawning Mechanism");
        add("confluence.configuration.Spawning.tooltip", "Settings related to entity spawning");
        add("confluence.configuration.Falling Star", "Falling Star");
        add("confluence.configuration.Falling Star.button", "Falling Star");
        add("confluence.configuration.Falling Star.tooltip", "Settings controlling the spawning of falling stars");
        add("confluence.configuration.NPC", "NPC");
        add("confluence.configuration.NPC.tooltip", "Settings controlling NPC spawning");
        add("confluence.configuration.doNPCSpawning", "NPC Spawning");
        add("confluence.configuration.doNPCSpawning.button", "NPC Spawning");
        add("confluence.configuration.doNPCSpawning.tooltip", "When enabled, NPCs will spawn");
        add("confluence.configuration.npcSpawnInterval", "NPC Spawn Interval");
        add("confluence.configuration.npcSpawnInterval.tooltip", "Defines the interval between NPC spawns");
        add("confluence.configuration.broadcastNpcMsg", "Broadcast NPC Messages");
        add("confluence.configuration.broadcastNpcMsg.tooltip", "When enabled, NPC-related messages will be broadcast in the chat window.");
        add("confluence.configuration.doMeteoriteSpawning", "Meteorite Spawning");
        add("confluence.configuration.doMeteoriteSpawning.tooltip", "When enabled, meteorites will fall on unloaded areas");
        add("confluence.configuration.doFallingStarSpawning", "Falling Star Spawning");
        add("confluence.configuration.doFallingStarSpawning.tooltip", "When enabled, falling stars will spawn at night");
        add("confluence.configuration.fallingStarInterval", "Falling Star Spawn Interval");
        add("confluence.configuration.fallingStarInterval.tooltip", "Defines the interval between falling star spawns at night");
        add("confluence.configuration.WorldGeneration", "World Generation Mechanism");
        add("confluence.configuration.WorldGeneration.button", "World Generation Mechanism");
        add("confluence.configuration.WorldGeneration.tooltip", "Settings related to world generation");
        add("confluence.configuration.brewing_stand_recipe", "Terra Potion Brewing Stand Recipe");
        add("confluence.configuration.brewing_stand_recipe.tooltip", "When enabled, the brewing stand can brew Terra potions.(This configuration change requires a game restart!)");
        add("confluence.configuration.alertPlayerDungeon", "Dungeon Guardian Warning");
        add("confluence.configuration.alertPlayerDungeon.tooltip", "When enabled, there will be three roars as warnings before the dungeon guardian appears.");
        add("confluence.configuration.achievementToast", "Enable Terra Style Achievements");
        add("confluence.configuration.achievementToast.tooltip", "Disable it if you want to use the default progress style.");
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
        add("confluence.configuration.returnPotionGlassBottle", "Return Glass Bottles for Potions");
        add("confluence.configuration.returnPotionGlassBottle.tooltip", "Decides whether to return the glass bottle after using a potion");
        add("confluence.configuration.rightClickRideMinecart", "Right click to ride a minecart");
        add("confluence.configuration.rightClickRideMinecart.tooltip", "When enabled, when you right-click on a rail, it will automatically ride a minecart");
        add("confluence.configuration.announcementBoxDistance", "Announcement Box Distance");
        add("confluence.configuration.announcementBoxDistance.tooltip", "The maximum information sending distance of the Announcement Box");
        add("confluence.configuration.dropsTombstone", "Drops Tombstone");
        add("confluence.configuration.dropsTombstone.tooltip", "When enabled，allows player to drops tombstone after death");
        add("confluence.configuration.defaultRespawnTimeMin", "Default Minimum Respawn Time");
        add("confluence.configuration.defaultRespawnTimeMin.tooltip", "Set the default minimum respawn time");
        add("confluence.configuration.defaultRespawnTimeMax", "Default Maximum Respawn Time");
        add("confluence.configuration.defaultRespawnTimeMax.tooltip", "Set the default maximum respawn time");
        add("confluence.configuration.bossRespawnTimeMin", "Minimum Respawn Time (Boss Battle)");
        add("confluence.configuration.bossRespawnTimeMax", "Maximum Respawn Time (Boss Battle)");
        add("confluence.configuration.bossRespawnTimeMax.tooltip", "Maximum respawn time for when dying in a boss battle");
        add("confluence.configuration.bossRespawnTimeMin.tooltip", "Minimum respawn time for when dying in a boss battle");
        add("confluence.configuration.brewingStandRecipe", "Terra Potion Brewing Stand Recipes");
        add("confluence.configuration.brewingStandRecipe.tooltip", "When enabled, the Brewing Stand can craft Terra Potions. (Restart the game after changing this setting!)");
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
        add("confluence.configuration.healthOffsetX", "Health Offset X");
        add("confluence.configuration.healthOffsetY", "Health Offset Y");
        add("confluence.configuration.manaStyle", "Mana Style");
        add("confluence.configuration.manaOffsetX", "Mana Offset X");
        add("confluence.configuration.manaOffsetY", "Mana Offset Y");
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
        add("confluence.configuration.extraInventoryButtonOffsetX", "Extra Inventory Button Offset X");
        add("confluence.configuration.extraInventoryButtonOffsetY", "Extra Inventory Button Offset Y");
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
        add("confluence.configuration.altarTips", "Altar Tips");
        add("confluence.configuration.altarTips.tooltip", "You can turn off the tips by yourself after learning");
        add("confluence.configuration.ammoSlotsBlacklist", "Ammo Slot Automatically Picks-up Blacklist");
        add("confluence.configuration.ammoSlotsBlacklist.tooltip", "Items with IDs or tags in the blacklist will not automatically enter the ammo slot");
        add("confluence.configuration.shimmerDecompose", "Shimmer Decomposition");
        add("confluence.configuration.shimmerDecompose.tooltip", "When enabled, Shimmer can decompose items into raw materials.");
        add("confluence.configuration.starPhase", "Stellar Phase");
        add("confluence.configuration.starPhase.tooltip", "Currently has no function. Not recommended to enable.");
        add("confluence.configuration.wrappedCrimsonHeart", "Wrapped Crimson Heart");
        add("confluence.configuration.wrappedCrimsonHeart.tooltip", "When enabled, newly generated Crimson Caverns will contain Wrapped Crimson Hearts.");
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
        add("confluence.configuration.section.confluence.common.toml.title", "Common Configuration");

        add("biome.confluence.ash_forest", "Ash Forest");
        add("biome.confluence.ash_wasteland", "Ash Wasteland");
        add("biome.confluence.glowing_mushroom", "Glowing Mushroom");
        add("biome.confluence.the_corruption", "The Corruption");
        add("biome.confluence.the_corruption_desert", "The Corruption Desert");
        add("biome.confluence.the_corruption_tundra", "The Corruption Tundra");
        add("biome.confluence.the_hallow", "The Hallow");
        add("biome.confluence.the_hallow_desert", "The Hallow Desert");
        add("biome.confluence.the_hallow_tundra", "The Hallow Tundra");
        add("biome.confluence.the_crimson", "The Crimson");
        add("biome.confluence.the_crimson_desert", "The Crimson Desert");
        add("biome.confluence.the_crimson_tundra", "The Crimson Tundra");

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
        add("achievements.confluence.dungeon_heist.description", "Steal a key from the undead residents of the dungeon and open one of their precious dungeon chests.");
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
        add("achievements.confluence.its_hard.title", "It's Hard!");
        add("achievements.confluence.its_hard.description", "Unleash the ancient spirits of light and darkness across your world, enabling much stronger foes and showering the world with dazzling treasures (and rainbows!).");
        add("achievements.confluence.no_hobo.title", "No Hobo");
        add("achievements.confluence.no_hobo.description", "Build a house suitable enough for your first town NPC, such as the guide, to move into.");
        add("achievements.confluence.wheres_my_honey.title", "Where's My Honey? ");
        add("achievements.confluence.wheres_my_honey.description", "Discover a large bee's hive deep in the jungle.");
        add("achievements.confluence.temple_raider.title", "Temple Raider");
        add("achievements.confluence.temple_raider.description", "Breach the impenetrable walls of the jungle temple.");
        add("achievements.confluence.sword_of_the_hero.title", "Sword of the Hero");
        add("achievements.confluence.sword_of_the_hero.description", "Obtain a Terra Blade, forged from the finest blades of light and darkness.");
        add("achievements.confluence.sick_throw.title", "Sick Throw");
        add("achievements.confluence.sick_throw.description", "Obtain the Terrarian.");
        add("achievements.confluence.big_booty.title", "Big Booty");
        add("achievements.confluence.big_booty.description", "Unlock one of the dungeon's large, mysterious chests with a special key.");
        add("achievements.confluence.prismancer.title", "Prismancer");
        add("achievements.confluence.prismancer.description", "Obtain a rainbow rod.");
        add("achievements.confluence.kill_the_sun.title", "Kill the Sun");
        add("achievements.confluence.kill_the_sun.description", "Survive a solar eclipse, a day darker than night filled with creatures of horror.");
        add("achievements.confluence.jeepers_creepers.title", "Jeepers Creepers");
        add("achievements.confluence.jeepers_creepers.description", "Stumble into a spider cavern in the underground.");
        add("achievements.confluence.funkytown.title", "Funkytown");
        add("achievements.confluence.funkytown.description", "Build or encounter a glowing mushroom field above the surface.");
        add("achievements.confluence.infinity_1_sword.title", "Infinity +1 Sword");
        add("achievements.confluence.infinity_1_sword.description", "Obtain the Zenith, the culmination of a journey forged into the ultimate sword.");
        add("achievements.confluence.feast_of_midas.title", "Feast of Midas");
        add("achievements.confluence.feast_of_midas.description", "Obtain Golden Delight, the highest quality meal made from the highest quality . . . ingredients.");
        add("achievements.confluence.it_can_talk.title", "It Can Talk?!");
        add("achievements.confluence.it_can_talk.description", "Build a house in a mushroom biome and have the Truffle move in.");
        add("achievements.confluence.boned.title", "Boned");
        add("achievements.confluence.boned.description", "Defeat Skeletron, the cursed guardian of the dungeon.");
        add("achievements.confluence.still_hungry.title", "Still Hungry");
        add("achievements.confluence.still_hungry.description", "Defeat the Wall of Flesh, the master and core of the world who arises after a great, burning sacrifice.");
        add("achievements.confluence.buckets_of_bolts.title", "Buckets of Bolts");
        add("achievements.confluence.buckets_of_bolts.description", "Defeat the three nocturnal mechanical menaces: the Twins, the Destroyer, and Skeletron Prime.");
        add("achievements.confluence.the_great_southern_plantkill.title", "The Great Southern Plantkill");
        add("achievements.confluence.the_great_southern_plantkill.description", "Defeat Plantera, the overgrown monstrosity of the jungle's depths.");
        add("achievements.confluence.lihzahrdian_idol.title", "Lihzahrdian Idol");
        add("achievements.confluence.lihzahrdian_idol.description", "Defeat Golem, the stone-faced ritualistic idol of the lihzahrd tribe.");
        add("achievements.confluence.fish_out_of_water.title", "Fish Out of Water");
        add("achievements.confluence.fish_out_of_water.description", "Defeat Duke Fishron, mutant terror of the sea.");
        add("achievements.confluence.obsessive_devotion.title", "Obsessive Devotion");
        add("achievements.confluence.obsessive_devotion.description", "Defeat the Ancient Cultist, fanatical leader of the dungeon coven.");
        add("achievements.confluence.star_destroyer.title", "Star Destroyer");
        add("achievements.confluence.star_destroyer.description", "Defeat the four celestial towers of the moon.");
        add("achievements.confluence.champion_of_terraria.title", "Champion of Terraria");
        add("achievements.confluence.champion_of_terraria.description", "Defeat the Moon Lord.");
        add("achievements.confluence.goblin_punter.title", "Goblin Punter");
        add("achievements.confluence.goblin_punter.description", "Triumph over a goblin invasion, a ragtag regiment of crude, barbaric, pointy-eared warriors and their shadowflame sorcerers.");
        add("achievements.confluence.walk_the_plank.title", "Walk the Plank");
        add("achievements.confluence.walk_the_plank.description", "Triumph over a pirate invasion, a group of pillagers from the sea out for your booty... and your life!");
        add("achievements.confluence.do_you_want_to_slay_a_snowman.title", "Do You Want to Slay a Snowman?");
        add("achievements.confluence.do_you_want_to_slay_a_snowman.description", "Triumph over the frost legion, a festive family of maniacal snowman mobsters.");
        add("achievements.confluence.tin_foil_hatter.title", "Tin-Foil Hatter");
        add("achievements.confluence.tin_foil_hatter.description", "Triumph over a martian invasion, when beings from out of this world come to scramble your brains and probe you in uncomfortable places.");
        add("achievements.confluence.baleful_harvest.title", "Baleful Harvest");
        add("achievements.confluence.baleful_harvest.description", "Reach the 15th wave of a pumpkin moon, where evil lurks among the autumn harvest.");
        add("achievements.confluence.ice_scream.title", "Ice Scream");
        add("achievements.confluence.ice_scream.description", "Reach the 15th wave of a frost moon, where the festive season quickly degrades into madness.");
        add("achievements.confluence.real_estate_agent.title", "Real Estate Agent");
        add("achievements.confluence.real_estate_agent.description", "Have all possible town NPCs living in your world.");
        add("achievements.confluence.mecha_mayhem.title", "Mecha Mayhem");
        add("achievements.confluence.mecha_mayhem.description", "Do battle against the Twins, the Destroyer, and Skeletron Prime simultaneously and emerge victorious.");
        add("achievements.confluence.gelatin_world_tour.title", "Gelatin World Tour");
        add("achievements.confluence.gelatin_world_tour.description", "Defeat every type of slime there is!");
        add("achievements.confluence.there_are_some_who_call_him.title", "There are Some Who Call Him...");
        add("achievements.confluence.there_are_some_who_call_him.description", "Kill Tim.");
        add("achievements.confluence.throwing_lines.title", "Throwing Lines");
        add("achievements.confluence.throwing_lines.description", "Throw a yoyo.");
        add("achievements.confluence.the_frequent_flyer.title", "The Frequent Flyer");
        add("achievements.confluence.the_frequent_flyer.description", "Spend over 1 gold being treated by the nurse.");
        add("achievements.confluence.til_death.title", "Til Death...");
        add("achievements.confluence.til_death.description", "Kill the groom.");
        add("achievements.confluence.archaeologist.title", "Archaeologist");
        add("achievements.confluence.archaeologist.description", "Kill Doctor Bones.");
        add("achievements.confluence.rainbows_and_unicorns.title", "Rainbows and Unicorns");
        add("achievements.confluence.rainbows_and_unicorns.description", "Fire a rainbow gun while riding on a unicorn.");
        add("achievements.confluence.you_and_what_army.title", "You and What Army?");
        add("achievements.confluence.you_and_what_army.description", "Command nine summoned minions simultaneously.");
        add("achievements.confluence.servant_in_training.title", "Servant-in-Training");
        add("achievements.confluence.servant_in_training.description", "Complete your 1st quest for the angler.");
        add("achievements.confluence.good_little_slave.title", "Good Little Slave");
        add("achievements.confluence.good_little_slave.description", "Complete your 10th quest for the angler.");
        add("achievements.confluence.trout_monkey.title", "Trout Monkey");
        add("achievements.confluence.trout_monkey.description", "Complete your 25th quest for the angler.");
        add("achievements.confluence.fast_and_fishious.title", "Fast and Fishious");
        add("achievements.confluence.fast_and_fishious.description", "Complete your 50th quest for the angler.");
        add("achievements.confluence.supreme_helper_minion.title", "Supreme Helper Minion!");
        add("achievements.confluence.supreme_helper_minion.description", "Complete a grand total of 200 quests for the angler.");
        add("achievements.confluence.slayer_of_worlds.title", "Slayer of Worlds");
        add("achievements.confluence.slayer_of_worlds.description", "Defeat every boss in Terraria.");
        add("achievements.confluence.fae_flayer.title", "Fae Flayer");
        add("achievements.confluence.fae_flayer.description", "Defeat the Empress of Light, responsible for all those flashy lights and glitter.");
        add("achievements.confluence.just_desserts.title", "Just Desserts");
        add("achievements.confluence.just_desserts.description", "Defeat Queen Slime, giving the coup-de-grace to the sovereign of all that jiggles.");
        add("achievements.confluence.dont_dread_on_me.title", "Don't Dread on Me");
        add("achievements.confluence.dont_dread_on_me.description", "Defeat the Dreadnautilus, murderous mollusk lurking beneath the surface of the sanguine seas.");
        add("achievements.confluence.hero_of_etheria.title", "Hero of Etheria");
        add("achievements.confluence.hero_of_etheria.description", "Repel the strongest forces the Old One's Army can muster.");
        add("achievements.confluence.a_rather_blustery_day.title", "A Rather Blustery Day");
        add("achievements.confluence.a_rather_blustery_day.description", "Fly a kite on a windy day.");
        add("achievements.confluence.heliophobia.title", "Heliophobia");
        add("achievements.confluence.heliophobia.description", "Trick a gnome into turning into stone!");
        add("achievements.confluence.leading_landlord.title", "Leading Landlord");
        add("achievements.confluence.leading_landlord.description", "Meet with a tenant who's as happy as they possibly can be!");
        add("achievements.confluence.feeling_petty.title", "Feeling Petty");
        add("achievements.confluence.feeling_petty.description", "Deliver headpats to the town pet.");
        add("achievements.confluence.hey_listen.title", "Hey! Listen!");
        add("achievements.confluence.hey_listen.description", "Encounter a fairy.");
        add("achievements.confluence.jolly_jamboree.title", "Jolly Jamboree");
        add("achievements.confluence.jolly_jamboree.description", "What you're celebrating doesn't matter, just throw a party already!");
        add("achievements.confluence.an_eye_for_an_eye.title", "An Eye For An Eye");
        add("achievements.confluence.an_eye_for_an_eye.description", "Defeat Deerclops, the chilly one-eyed monstrosity from a foreign land.");
        add("achievements.confluence.torch_god.title", "Torch God");
        add("achievements.confluence.torch_god.description", "Invoked the wrath of the God of Torches, and survived long enough to earn its blessing.");
        add("achievements.confluence.a_rare_realm.title", "A Rare Realm");
        add("achievements.confluence.a_rare_realm.description", "Some very special seeds can lead to unique and rewarding experiences. Can you find one?");
        add("achievements.confluence.the_great_slime_mitosis.title", "The Great Slime Mitosis");
        add("achievements.confluence.the_great_slime_mitosis.description", "Find all of the Slime Pets and have them move in!");
        add("achievements.confluence.and_good_riddance.title", "And Good Riddance!");
        add("achievements.confluence.and_good_riddance.description", "Completely purify all Corruption, Crimson, and Hallow from your world, until the Dryad is satisfied!");
        add("achievements.confluence.to_infinity_and_beyond.title", "To Infinity... and Beyond! ");
        add("achievements.confluence.to_infinity_and_beyond.description", "Fly a Kwad Racer into outer space.");
        add("achievements.confluence.quiet_neighborhood.title", "Quiet Neighborhood");
        add("achievements.confluence.quiet_neighborhood.description", "Enter a misty graveyard filled with the surly dead.");
        add("achievements.confluence.robbing_the_grave.title", "Robbing the Grave");
        add("achievements.confluence.robbing_the_grave.description", "Obtain a rare treasure from a difficult monster in the dungeon.");

        add("prefix.confluence.tooltip.plus", "+%s%% %s");
        add("prefix.confluence.tooltip.take", "-%s%% %s");
        add("prefix.confluence.tooltip.add", "+%s %s");
        add("prefix.confluence.tooltip.mana_cost", "Mana Cost");
        add("prefix.confluence.tooltip.additional_mana", "Additional Mana");
        add("prefix.confluence.quick", "Quick");
        add("prefix.confluence.hasty", "Hasty");
        add("prefix.confluence.deadly", "Deadly");
        GROUPS.values().stream().flatMap(map -> map.values().stream()).forEach(prefix -> {
            String name = prefix.name();
            if ("quick".equals(name) || "deadly".equals(name) || "hasty".equals(name)) return;
            add("prefix.confluence." + name, LibUtils.toTitleCase(name));
        });

        add("fluid_type.confluence.shimmer", "Shimmer");
        add("fluid_type.confluence.honey", "Honey");

        add("condition.confluence.shimmer_transmutation.before_skeletron", "Required game phase: Before Skeletron");
        add("condition.confluence.shimmer_transmutation.after_skeletron", "Required game phase: After Skeletron");
        add("condition.confluence.shimmer_transmutation.wall_of_flesh", "Required game phase: After Wall Of Flesh");
        add("condition.confluence.shimmer_transmutation.mechanical_bosses", "Required game phase: After Mechanical Bosses");
        add("condition.confluence.shimmer_transmutation.plantera", "Required game phase: After Plantera");
        add("condition.confluence.shimmer_transmutation.golem", "Required game phase: After Golem");
        add("condition.confluence.shimmer_transmutation.moon_lord", "Required game phase: After Moon Lord");
        add("condition.confluence.requires_fuel", "Requires Fuel");

        add("container.confluence.sky_mill", "Sky Mill");
        add("container.confluence.safe", "Safe");
        add("container.confluence.heavy_work_bench", "Heavy Work Bench");
        add("container.confluence.hellforge", "Hell Forge");
        add("container.confluence.alchemy_table", "Alchemy Table");
        add("container.confluence.cooking_pot", "Cooking Pot");
        add("container.confluence.cauldron", "Cauldron");
        add("container.confluence.crystal_ball", "Crystal Ball");
        add("container.confluence.fletching_table", "Fletching Table");
        add("container.confluence.piggy_bank", "Piggy Bank");
        add("container.confluence.sawmill", "Sawmill");
        add("container.confluence.tree_holes", "Tree Holes");
        add("container.confluence.npc_shop", "Npc Shop");
        add("container.confluence.solidifier", "Solidifier");
        add("container.confluence.mythril_anvil", "Mythril Anvil");
        add("container.confluence.orichalcum_anvil", "Orichalcum Anvil");

        add("title.confluence.shimmer_transmutation", "Shimmer Transmutation");
        add("title.confluence.altar", "Altar");
        add("title.confluence.sky_mill", "Sky Mill");
        add("title.confluence.heavy_work_bench", "Heavy Work Bench");
        add("title.confluence.hellforge", "Hell Forge");
        add("title.confluence.crystal_ball", "Crystal Ball");
        add("title.confluence.alchemy_table", "Alchemy Table");
        add("title.confluence.cooking_pot", "Cooking Pot");
        add("title.confluence.solidifier", "Solidifier");
        add("title.confluence.fletching_table", "Fletching Table");
        add("title.confluence.touhoulittlemaid", "Touhou Little Maid Supplies");
        add("title.confluence.npc_trade", "NPC Trading");
        add("title.confluence.sawmill", "Sawmill");
        add("title.confluence.mythril_anvil", "Mythril Anvil");
        add("title.confluence.orichalcum_anvil", "Orichalcum Anvil");

        // Override
        add("item.confluence.encumbering_stone.disable", "Encumbering Stone: Disable");
        add("item.confluence.paint", "Paint");
        add(AccessoryItems.PHILOSOPHERS_STONE.get(), "Philosopher's Stone");
        add(ModItems.BOREDOMS_PACT_FALLING_RESOLVE.get(), "Boredom's Pact - Falling Resolve");
        add(FunctionalBlocks.BLEND_O_MATIC.get(), "Blend-O-Matic");
        add(StatueBlocks.N0_STATUE.get(), "'0' Statue");
        add(StatueBlocks.N1_STATUE.get(), "'1' Statue");
        add(StatueBlocks.N2_STATUE.get(), "'2' Statue");
        add(StatueBlocks.N3_STATUE.get(), "'3' Statue");
        add(StatueBlocks.N4_STATUE.get(), "'4' Statue");
        add(StatueBlocks.N5_STATUE.get(), "'5' Statue");
        add(StatueBlocks.N6_STATUE.get(), "'6' Statue");
        add(StatueBlocks.N7_STATUE.get(), "'7' Statue");
        add(StatueBlocks.N8_STATUE.get(), "'8' Statue");
        add(StatueBlocks.N9_STATUE.get(), "'9' Statue");
        add(VanityArmorItems.DEAD_MANS_SWEATER.get(), "Dead Man's Sweater");
        add(SwordItems.NIGHTS_EDGE.get(), "Night's Edge");

        add("block.confluence.timers_1_1", "1 Second Timer");
        add("block.confluence.timers_3_1", "3 Second Timer");
        add("block.confluence.timers_5_1", "5 Second TImer");
        add("block.confluence.timers_1_2", "1/2 Second Timer");
        add("block.confluence.timers_1_4", "1/4 Second TImer");

        add("resourcepack.terraria_art", "Terraria Art");
        add("resourcepack.terraria_armor", "Terraria-Like Armor");

        add("event.confluence.meteorite", "A meteorite has landed!");
        add("event.confluence.meteorite.ready", "A meteorite is falling!");
        add("event.confluence.shadow_orb_broken.0", "A horrible chill goes down your spine...");
        add("event.confluence.shadow_orb_broken.1", "Screams echo around you...");
        add("event.confluence.crimson_heart_broken.0", "A horrible chill goes down your spine...");
        add("event.confluence.crimson_heart_broken.1", "Screams echo around you...");
        add("event.confluence.eye_of_cthulhu", "You feel an evil presence watching you...");
        add("event.confluence.hardmode_conversion.pass", "There is a conversion mission in the world!");
        add("event.confluence.hardmode_conversion.hardmode", "The world type has been changed to Hardmode");
        add("event.confluence.hardmode_conversion.starting", "Conversion data preparation, please wait");
        add("event.confluence.hardmode_conversion.instantly", "The conversion time will depend on your computer's performance, usually 90 seconds");
        add("event.confluence.hardmode_conversion.generate_data.sanctification", "Sanctification data: %s entries, estimated to take %s seconds");
        add("event.confluence.hardmode_conversion.started", "Conversion data is ready, and the conversion is begining");
        add("event.confluence.hardmode_conversion.finished", "\"The ancient spirits of light and dark have been released.\"");
        add("event.confluence.hardmode_conversion.welcome", "Welcome to Terraria");
        add("event.confluence.npc.arrived", "%2$s the %1$s has arrived!");
        add("event.confluence.npc.slain", "%2$s the %1$s was slain...");
        add("event.confluence.npc.left", "%s has left!");
        add("event.confluence.traveling_merchant.departed", "%s the Traveling Merchant has departed!");

        add("entity.minecraft.villager.confluence.sky_miller", "Sky Miller");
        add("entity.minecraft.villager.confluence.chef", "Chef");
        add("entity.minecraft.villager.confluence.banker", "Banker");
        add("entity.minecraft.villager.sky", "Sky Miller");
        add("entity.minecraft.villager.coin", "Banker");
        add("entity.minecraft.villager.chef", "Chef");
        add("entity.confluence.dart", "Dart");
        add("entity.confluence.frozen_zombie", "Frozen Zombie");
        add("entity.confluence.raincoat_zombie", "Raincoat Zombie");
        add("entity.confluence.undead_miner", "Undead Miner");

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

        add("equipment_benediction.set_switcher.confluence.heim_set", "Heim Set");
        add("equipment_benediction.set_switcher.confluence.heim_set.data.0", "Extend underwater breathing time by 5% and increase melee damage by 1%");
        add("equipment_benediction.set_switcher.confluence.heim_set.data.1", "After successfully using the shield to intercept the next attack, increase damage by 20% within three seconds. The damage increase stops when you launch an attack and hit or get hit during these three seconds. Increase melee damage by 1%");
        add("equipment_benediction.set_switcher.confluence.heim_set.data.2", "Increase critical hit rate by 2% and increase melee damage by 1%");
        add("equipment_benediction.set_switcher.confluence.heim_set.data.3", "Increase underwater movement speed by 5% and increase melee damage by 1%");
        add("equipment_benediction.set_switcher.confluence.heim_set.data.4", "Grant you 4 HP of damage absorption, and grant it again every 3 seconds");

        add("equipment_benediction.set_switcher.confluence.necro_set", "Necro Set");
        add("equipment_benediction.set_switcher.confluence.necro_set.data.0", "5% increased ranged damage");
        add("equipment_benediction.set_switcher.confluence.necro_set.data.1", "5% increased ranged damage");
        add("equipment_benediction.set_switcher.confluence.necro_set.data.2", "2.5% increased ranged damage");
        add("equipment_benediction.set_switcher.confluence.necro_set.data.3", "2.5% increased ranged damage");
        add("equipment_benediction.set_switcher.confluence.necro_set.data.4", "Critical Hit Rate increased by 10%");

        add("equipment_benediction.set_switcher.confluence.jungle_set", "Jungle set");
        add("equipment_benediction.set_switcher.confluence.jungle_set.data.0", "Increases maximum mana by 40,6% increased magic critical strike chance");
        add("equipment_benediction.set_switcher.confluence.jungle_set.data.1", "Increases maximum mana by 20,6% increased magic critical strike chance");
        add("equipment_benediction.set_switcher.confluence.jungle_set.data.2", "Increases maximum mana by 10,3% increased magic critical strike chance");
        add("equipment_benediction.set_switcher.confluence.jungle_set.data.3", "Increases maximum mana by 10,3% increased magic critical strike chance");
        add("equipment_benediction.set_switcher.confluence.jungle_set.data.4", "16% reduced mana costs");

        add("equipment_benediction.set_switcher.confluence.ash_set", "Ash set");
        add("equipment_benediction.set_switcher.confluence.ash_set.data.0", "Reduce fire damage by 50%");

        add("equipment_benediction.set_switcher.confluence.pumpkin_set", "Pumpkin set");
        add("equipment_benediction.set_switcher.confluence.pumpkin_set.data.0", "Damage increased by 10%");

        add("equipment_benediction.set_switcher.confluence.obsidian_set", "Obsidian Set");
        add("equipment_benediction.set_switcher.confluence.obsidian_set.data.0", "Increases summon damage by 8%");
        add("equipment_benediction.set_switcher.confluence.obsidian_set.data.1", "Increases your max number of minions by 1");
        add("equipment_benediction.set_switcher.confluence.obsidian_set.data.2", "Increases summon damage by 4%");
        add("equipment_benediction.set_switcher.confluence.obsidian_set.data.3", "Increases summon damage by 4%");
        add("equipment_benediction.set_switcher.confluence.obsidian_set.data.4", "Increases whip range by 30% and speed by 15%,Increases summon damage by 15%");

        add("equipment_benediction.set_switcher.confluence.wizard_set", "Wizard Set");
        add("equipment_benediction.set_switcher.confluence.wizard_set.data.0", "5% increased magic damage");
        add("equipment_benediction.set_switcher.confluence.wizard_set.data.1", "6% increased magic damage and critical strike chance");
        add("equipment_benediction.set_switcher.confluence.wizard_set.data.2", "Increases maximum mana by 20,5% reduced mana cost");
        add("equipment_benediction.set_switcher.confluence.wizard_set.data.3", "Increases maximum mana by 40,7% reduced mana cost");
        add("equipment_benediction.set_switcher.confluence.wizard_set.data.4", "Increases maximum mana by 40,9% reduced mana cost");
        add("equipment_benediction.set_switcher.confluence.wizard_set.data.5", "Increases maximum mana by 60,11% reduced mana cost");
        add("equipment_benediction.set_switcher.confluence.wizard_set.data.6", "Increases maximum mana by 60,13% reduced mana cost");
        add("equipment_benediction.set_switcher.confluence.wizard_set.data.7", "Increases maximum mana by 80,15% reduced mana cost");
        add("equipment_benediction.set_switcher.confluence.wizard_set.data.8", "Increases maximum mana by 60,13% reduced mana cost");

        add("equipment_benediction.set_switcher.confluence.flinx_set", "Flinx set");
        add("equipment_benediction.set_switcher.confluence.flinx_set.data.0", "Increases summon damage by 5%,Increases your max number of minions by 1");




        // npc dialogs
        add("dialogs.confluence.guide.0", "My job is to offer suggestions for your upcoming tasks. I recommend that you come and talk to me whenever you encounter any difficulties.");
        add("dialogs.confluence.guide.1", "They said there would be someone to tell you how to survive in this place... Oh, wait a moment. That person is me.");
        add("dialogs.confluence.guide.2", "You should stay at home at night. It's very dangerous to wander outside in the dark.");
        add("dialogs.confluence.guide.3", "In the Confluence world, you will obtain multiple times the treasure, but this also means taking on multiple times the risk.");
        add("dialogs.confluence.guide.4", "As far as I know, there are more humans in this world than in our original world.");
        add("dialogs.confluence.guide.5", "Sorry, sometimes I have to open the door.");
        add("dialogs.confluence.guide.6", "Those guys that can explode are more threatening than the average surface monsters!");
        add("dialogs.confluence.guide.7", "The life mushrooms on the grass can sometimes save your life.");
        add("dialogs.confluence.guide.8", "There are Crystal Hearts underground, which can be used to increase your maximum health. You can use a pickaxe to break them.");
        add("dialogs.confluence.guide.9", "There is a lake with magical powers underground, and it's very rare.");
        add("dialogs.confluence.guide.10", "At night, stars are falling and spreading all over the world. They have extremely wide uses. If you see them, you must get them, because the stars will disappear after sunrise.");
        add("dialogs.confluence.guide.11", "No matter what is spreading wildly, you will realize that it's time to stop them.");
        add("dialogs.confluence.guide.12", "If you want to survive, you need to make weapons and build a house. First, cut down trees and collect wood.");
        add("dialogs.confluence.guide.13", "After you have a sword, you can try to collect some gel from slimes. Use wooden sticks and gel to make torches!");
        add("dialogs.confluence.guide.14", "If you have some ores, you need to smelt them into ingots before you can use them to make items. This requires a furnace!");
        add("dialogs.confluence.guide.15", "If you combine lenses on the altar, you may be able to find a way to summon a powerful monster. However, it's better to use it at night.");
        add("dialogs.confluence.guide.jei_check", "In Minecraft, I can't help you look up recipes, but I know a mod called JEI that can help you.");

        add("dialogs.confluence.nurse.0", "I need to have a serious talk with the Guide. How many times a week do you get severely burned by lava exactly?");
        add("dialogs.confluence.nurse.1", "See that old man wandering around the dungeon? He looks like he's in trouble.");
        add("dialogs.confluence.nurse.2", "Hey, has the Arms Dealer ever mentioned going to see a doctor or something? Just asking.");
        add("dialogs.confluence.nurse.3", "Got into trouble with the thugs again?");
        add("dialogs.confluence.nurse.4", "Don't be such a child! I've seen worse.");
        add("dialogs.confluence.nurse.5", "Did it hurt when you did that? Don't do that.");
        add("dialogs.confluence.nurse.player_killed_by", "Have you been killed %2$s times by %1$s? I'm curious who took your flag");

        add("dialogs.confluence.demolitionist.0", "Explosives are really popular nowadays. Buy some right away!");
        add("dialogs.confluence.demolitionist.1", "Today is a great day to court death!");
        add("dialogs.confluence.demolitionist.2", "Let me see what happens if I do this... (BOOM!)... Oh, sorry, did you still need that leg?");
        add("dialogs.confluence.demolitionist.3", "Take a look at my goods; they're all at amazing prices.");
        add("dialogs.confluence.demolitionist.4", "Dynamite, this is my special panacea prepared just for you. It can cure all kinds of problems.");
        add("dialogs.confluence.demolitionist.5", "Want to get through those evil stones, huh? Why not just blow them up with explosives!");

        add("dialogs.confluence.goblin_tinkerer.0", "Goblins get angry so easily. In fact, they can start a war over some rags!");
        add("dialogs.confluence.goblin_tinkerer.1", "To be honest, most goblins aren't real rocket scientists. Well, some of them are.");
        add("dialogs.confluence.goblin_tinkerer.2", "Do you know why everyone carries these spiky balls around? Because I don't.");
        add("dialogs.confluence.goblin_tinkerer.3", "I've just finished my latest creation! This version won't explode violently even if you blow or suck on it really hard.");
        add("dialogs.confluence.goblin_tinkerer.4", "Goblin thieves aren't very good at stealing. They can't even steal from an unlocked chest!");
        add("dialogs.confluence.goblin_tinkerer.5", "Yo, I heard you like rockets and running shoes, so I added some rockets to your running shoes.");

        add("dialogs.confluence.arms_dealer.0", "Dude, get your hands off my gun!");
        add("dialogs.confluence.arms_dealer.1", "Hey, bro, this isn't a movie. You need to prepare ammunition separately.");
        add("dialogs.confluence.arms_dealer.2", "I see you're eyeing the Minishark... You can't even imagine how it's made.");
        add("dialogs.confluence.arms_dealer.3", "I want to buy something from the Nurse. What did you say? She doesn't sell anything?");
        add("dialogs.confluence.arms_dealer.4", "Flying Fish? I call it target practice!");
        add("dialogs.confluence.arms_dealer.5", "Don't waste your time with the Demolitionist. I've got everything you need right here.");

        add("dialogs.confluence.merchant.0", "Swords beat paper! Buy one right away.");
        add("dialogs.confluence.merchant.1", "Do you want apples? Do you want carrots? Do you want pineapples? All we have are torches.");
        add("dialogs.confluence.merchant.2", "Take a look at my dirt blocks; they're really earthy.");
        add("dialogs.confluence.merchant.3", "You have no idea how cheap these trees can be overseas.");
        add("dialogs.confluence.merchant.4", "One day they will tell your legend... It's sure to be a good story.");
        add("dialogs.confluence.merchant.5", "Kosh, kapleck Mog. Oh, sorry, that's Klingon, which means 'Buy or die.'");

        add("dialogs.confluence.painter.0", "I know the difference between turquoise and teal. But I'm not going to tell you.");
        add("dialogs.confluence.painter.1", "The titanium white is all used up. Don't ask.");
        add("dialogs.confluence.painter.2", "Try mixing pink and purple. It'll definitely work, I swear!");
        add("dialogs.confluence.painter.3", "No, no, no... There are many kinds of gray! Don't make me start...");
        add("dialogs.confluence.painter.4", "I hope it stops raining. The paint still hasn't dried. It would be a disaster if it rains!");
        add("dialogs.confluence.painter.5", "I tried organizing a paintball war, but everyone just wanted food and decorations.");

        add("dialogs.confluence.dryad.0", "Stay safe! Both worlds need you!");
        add("dialogs.confluence.dryad.1", "The hourglass of time is slowly running out. And you're not aging gracefully.");
        add("dialogs.confluence.dryad.2", "Two goblins walked into a bar, and one of them said to the other: 'A glass of beer?'");
        add("dialogs.confluence.dryad.3", "What does it mean by saying I'm all talk and no action?");
        add("dialogs.confluence.dryad.4", "You must stop the spread of evil.");
        add("dialogs.confluence.dryad.5", "This world is much vaster... And the power of nature is stronger too.");

        add("dialogs.confluence.dye_trader.0", "I bring you the richest colors in exchange for your wealth.");
        add("dialogs.confluence.dye_trader.1", "Honey, your clothes are so monotonous. You really have to learn how to dye your dull clothes!");
        add("dialogs.confluence.dye_trader.2", "The only wood I'm willing to dye is mahogany. Dyeing any other wood is a waste.");
        add("dialogs.confluence.dye_trader.3", "Oh, no, no, that won't do. Even if you have money, you have to trade me with rare plant samples!");
        add("dialogs.confluence.dye_trader.4", "These dye bottles? Sorry, my dear friend, these are not for sale. I only accept the rarest plants in exchange for them!");
        add("dialogs.confluence.dye_trader.5", "You think you can fool my eyes? I don't think so! I only accept the rarest flowers in exchange for these special bottles.");

        add("dialogs.confluence.angler.0", "Thanks, I guess, for saving me and all that. You're an excellent lackey!");
        add("dialogs.confluence.angler.1", "What? Who are you? I definitely wasn't drowning or anything!");
        add("dialogs.confluence.angler.2", "You saved me! You're so kind. I can boss you around... Uh, I mean, hire you to do some amazing things for me!");
        add("dialogs.confluence.angler.3", "I don't have a mom or a dad, but I have a lot of fish! That's enough!");
        add("dialogs.confluence.angler.4", "Hey! Watch out! I've set a lot of traps for the greatest prank in history! No one will notice! Try telling anyone and see what happens!");
        add("dialogs.confluence.angler.5", "Have you ever heard of a fish that can make noise?! I haven't. I just want to know if you have!");

        add("dialogs.confluence.old_man.0", "I cannot let you enter until you free me of my curse.");
        add("dialogs.confluence.old_man.1", "Stranger, do you possess the strength to defeat my master?");
        add("dialogs.confluence.old_man.2", "Defeat my master, and I will grant you passage into the Dungeon.。");
        add("dialogs.confluence.old_man.3", "Come back at night if you wish to enter.");

        add("dialogs.confluence.traveling_merchant.0", "Hmm, you look like you could use an Angel Statue! They slice, and dice, and make everything nice!");
        add("dialogs.confluence.traveling_merchant.1", "I don't refund for \"buyer's remorse...\" Or for any other reason, really.");
        add("dialogs.confluence.traveling_merchant.2", "Buy now and get free shipping!");
        add("dialogs.confluence.traveling_merchant.3", "I sell wares from places that might not even exist!");
        add("dialogs.confluence.traveling_merchant.4", "You want two penny farthings!? Make it one and we have a deal.");
        add("dialogs.confluence.traveling_merchant.5", "Combination hookah and coffee maker! Also makes julienne fries!");
        add("dialogs.confluence.traveling_merchant.6", "Come and have a look! One pound fish! Very, very good! One pound fish!");
        add("dialogs.confluence.traveling_merchant.7", "If you're looking for junk, you've come to the wrong place.");
        add("dialogs.confluence.traveling_merchant.8", "A thrift shop?  No, I am only selling the highest quality items on the market.");

        add("dialogs.confluence.mechanic.0", "Did you make sure your device was plugged in?");
        add("dialogs.confluence.mechanic.1", "Oh, you know what this house needs? More blinking lights.");
        add("dialogs.confluence.mechanic.2", "DON'T MOVE. I DROPPED MY CONTACT.");
        add("dialogs.confluence.mechanic.3", "Thank you! Sooner or later, I'll end up like the other skeletons in the dungeon.");
        add("dialogs.confluence.mechanic.4", "I don't quite remember what happened in there. Three, maybe four important things...");
        add("dialogs.confluence.mechanic.5", "Oh yes, the Signal Adapter! It can connect the redstone here to the wires perfectly.");

        add("dialogs.confluence.witch_doctor.0", "Which doctor am I? The Witch Doctor am I.");
        add("dialogs.confluence.witch_doctor.1", "Choose wisely, my commodities are volatile and my dark arts, mysterious.");
        add("dialogs.confluence.witch_doctor.2", "The heart of magic is nature. The nature of hearts is magic.");
        add("dialogs.confluence.witch_doctor.3", "I sense a kindred spirit in the Etherian Dark Mages. A pity they are our enemies, I would have liked to learn from them.");

        add("dialogs.confluence.clothier.0", "Thanks again for freeing me from my curse. Felt like something jumped up and bit me.");
        add("dialogs.confluence.clothier.1", "Mama always said I would make a great tailor.");
        add("dialogs.confluence.clothier.2", "Life's like a box of clothes; you never know what you are gonna wear!");
        add("dialogs.confluence.clothier.3", "Of course embroidery is hard! If it wasn't hard, no one would do it! That's what makes it great.");
        add("dialogs.confluence.clothier.4", "I know everything they is to know about the clothierin' business.");
        add("dialogs.confluence.clothier.5", "Being cursed was lonely, so I once made a friend out of leather. I named him Wilson.");
        add("dialogs.confluence.clothier.6", "I keep having vague memories of tying up a woman and throwing her in a dungeon.");

        add("dialogs.confluence.party_girl.0", "We have to talk. It's... it's about parties.");
        add("dialogs.confluence.party_girl.1", "I can't decide what I like more: parties, or after-parties.");
        add("dialogs.confluence.party_girl.2", "We should set up a blinkroot party, and we should also set up an after-party.");
        add("dialogs.confluence.party_girl.3", "Put up a disco ball and then I'll show you how to party.");
        add("dialogs.confluence.party_girl.4", "I went to Sweden once, they party hard, why aren't you like that?");
        add("dialogs.confluence.party_girl.5", "My name's Party Girl but people call me party pooper. Yeah I don't know, it sounds cool though.");
        add("dialogs.confluence.party_girl.6", "Do you party? Sometimes? Hm, okay then we can talk...");

        add("dialogs.confluence.truffle.0", "As if living underground wasn't bad enough, jerks like you come in while I'm sleeping and steal my children.");
        add("dialogs.confluence.truffle.1", "I tried to lick myself the other day to see what the big deal was, everything started glowing blue.");
        add("dialogs.confluence.truffle.2", "Everytime I see the color blue, it makes me depressed and lazy.");
        add("dialogs.confluence.truffle.3", "You haven't seen any pigs around here have you? My brother lost his leg to one.");
        add("dialogs.confluence.truffle.4", "I don't know the 'Truffle Shuffle,' so stop asking!");
        add("dialogs.confluence.truffle.5", "There's been such a huge rumor that's being spread about me, 'If you can't beat him, eat him!'");
        add("dialogs.confluence.truffle.6", "I feel there are more of my kind here...");

        add("mood.terra_entity.goblin_tinkerer.like.dye_trader", "Dye Trader understands how fun it is to mix things together, I can respect that!");
        add("mood.terra_entity.goblin_tinkerer.love.mechanic", "Mechanic makes my cardiac core function improperly, it appears I love how that feels!");
        add("mood.terra_entity.goblin_tinkerer.dislike.clothier", "I detect eerie vibes from <name of Clothier>, as if they contain dark secrets. I don't like the feeling.");
        add("mood.terra_entity.guide.hate.painter", "I hate that Painter is around. The world is fine the way it was made!");
        add("mood.terra_entity.guide.like.clothier", "I'm quite fond of Clothier, we have a lot in common.");
        add("mood.terra_entity.arms_dealer.hate.demolitionist", "I'd REALLY like to use the Demolitionist as a range target sometime.");
        add("mood.terra_entity.arms_dealer.love.nurse", "Think Nurse the Nurse ever, ya know, checks me out?");
        add("mood.terra_entity.angler.like.demolitionist", "the Demolitionist actually knows what they're doing, unlike some OTHER people! I kinda like that!");
        add("mood.terra_entity.angler.like.party_girl", "the Party Girl actually knows what they're doing, unlike some OTHER people! I kinda like that!");
        add("mood.terra_entity.female_angler.dislike.demolitionist", "The Demolitionist is too noisy! No one comes to see my fish anymore!");
        add("mood.terra_entity.female_angler.dislike.party_girl", "The Party Girl always steals the spotlight at parties. No one notices my fish!");
        add("mood.terra_entity.dye_trader.like.arms_dealer", "Arms Dealer has good eyes for vividness and business, I like it, yes?");
        add("mood.terra_entity.dye_trader.like.painter", "Painter has good eyes for vividness and business, I like it, yes?");
        add("mood.terra_entity.demolitionist.dislike.arms_dealer", "I wanna strap Arms Dealer to a rocket and watch what happens!");
        add("mood.terra_entity.demolitionist.dislike.goblin_tinkerer", "I wanna strap Goblin Tinkerer to a rocket and watch what happens!");
        add("mood.terra_entity.demolitionist.like.mechanic", "Mechanic is a good friend I like, helps me load the gunpowder!");
        add("mood.terra_entity.painter.love.dryad", "I would really love to paint Dryad... because of the vivid colors, of course!");
        add("mood.terra_entity.painter.like.party_girl", "Party Girl and I like the same shade of pink! That's a friend, in my book!");
        add("mood.terra_entity.painter.dislike.truffle", "Truffle is just too bland for my tastes, I dislike associating with dull types.");
        add("mood.terra_entity.dryad.dislike.angler", "I don't like that Angler has no respect for other beings.");
        add("mood.terra_entity.dryad.like.female_angler", "She catches those fish to better study nature and tries not to harm them. That's good.");
        add("mood.terra_entity.dryad.like.witch_doctor", "I like that Witch Doctor resonates with every fiber of my being");
        add("mood.terra_entity.dryad.like.truffle", "I like that Truffle resonates with every fiber of my being");
        add("mood.terra_entity.merchant.like.nurse", "Nurse makes loads of money, I like deep pockets.");
        add("mood.terra_entity.merchant.hate.angler", "I hate Angler's terrible personality!");
        add("mood.terra_entity.merchant.like.female_angler", "She may seem all looks, but she's actually quite nice!");
        add("mood.terra_entity.nurse.love.arms_dealer", "What? Arms Dealer? I don't have a crush! I don't! Shut up!");
        add("mood.terra_entity.nurse.dislike.dryad", "I don't like Dryad that much, kinda weirds me out.");
        add("mood.terra_entity.nurse.dislike.party_girl", "I don't like Party Girl that much, kinda weirds me out.");
        add("mood.terra_entity.truffle.love.guide", "I love Guide for being able to talk to me without mysteriously getting hungry.");
        add("mood.terra_entity.truffle.like.dye_trader", " Dryad treats me with respect, as though I'm a true part of nature. I don't know how to feel about that, except I like it.");
        add("mood.terra_entity.truffle.dislike.clothier", " Clothier has tried to eat me so many times. I swear, one time they weren't even human! I, obviously, dislike it.");
        add("mood.terra_entity.truffle.hate.witch_doctor", "Witch Doctor has tried to throw me into a pot filled with other unusual ingredients. I hate that.");
        add("mood.terra_entity.clothier.love.truffle", "Truffle? I hadn't seen anything so delicious in my life.");
        add("mood.terra_entity.clothier.dislike.nurse", "For some reason, being around Nurse makes me feel uneasy.");
        add("mood.terra_entity.clothier.hate.mechanic", "I hate Mechanic and I don't know why.");
        add("mood.terra_entity.party_girl.dislike.merchant", "I think Merchant is a killjoy at parties.");
        add("mood.terra_entity.witch_doctor.like.dryad", "the Dryad is a kindred spirit of nature, my soul is at peace in their presence.");
        add("mood.terra_entity.witch_doctor.like.guide", "the Guide is a kindred spirit of nature, my soul is at peace in their presence.");
        add("mood.terra_entity.witch_doctor.dislike.nurse", "I dislike the practices of the Nurse. True healing cannot come from metal and glass.");
        add("mood.terra_entity.witch_doctor.hate.truffle", "Fury fills my being as abominations sprout from tainted earth - I speak of the Truffle.");
        add("mood.terra_entity.mechanic.love.goblin_tinkerer", "Umm...Goblin Tinkerer makes my heart flutter, I need to get that checked!");
        add("mood.terra_entity.mechanic.dislike.arms_dealer", "I don't really like that Arms Dealer won't leave me alone!");
        add("mood.terra_entity.mechanic.hate.clothier", "I hate how Clothier doesn't know how to treat a woman!");

        // Patchouli Guide
        add("patchouli.confluence.otherworld_note.name", "Otherworld Note");
        add("patchouli.confluence.otherworld_note.landing_text", "*bHave you felt the dark undercurrents surging?*bThe fate of your world is at stake, but not only yours.*bThe shadow of evil forces shrouds two worlds, their fates converging.*bThe era of confluence has arrived.*bYou begin your adventure as these two worlds collide: challenge the unknown, save the world, or seek power for yourself.");
        add("patchouli.confluence.otherworld_note.appearance", "Appearance");
        add("patchouli.confluence.otherworld_note.example", "Example");
        add("patchouli.confluence.otherworld_note.gui", "GUI");
        add("patchouli.confluence.otherworld_note.how_to_use", "How to use");
        // Categories
        add("patchouli.confluence.otherworld_note.accessories.name", "Accessories");
        add("patchouli.confluence.otherworld_note.accessories.description", "Two hands, two feet, 21 rings?🤔");
        add("patchouli.confluence.otherworld_note.boss_checklist.name", "Boss checklist");
        add("patchouli.confluence.otherworld_note.boss_checklist.description", "What's Scarier Than a Creeper?");//"More horrible than Creepers."
        add("patchouli.confluence.otherworld_note.crafting_stations.name", "Crafting stations");
        add("patchouli.confluence.otherworld_note.crafting_stations.description", "Crafting Table.. Workbench.. Crafting Table..");//"Crafting table... Workbench... Synthesis table..."
        add("patchouli.confluence.otherworld_note.fishing.name", "Fishing");
        add("patchouli.confluence.otherworld_note.fishing.description", "Teach a Man To Fish..");//"A fisherman never catches empty-handed!"
        add("patchouli.confluence.otherworld_note.food.name", "Food");//"Foods"
        add("patchouli.confluence.otherworld_note.food.description", "What Even Is This? *bTakes a bite.");//"What's this? Have a taste."
        add("patchouli.confluence.otherworld_note.mana.name", "Mana");
        add("patchouli.confluence.otherworld_note.mana.description", "You're a wizard, Harry.");
        add("patchouli.confluence.otherworld_note.material.name", "Material");//Materials
        add("patchouli.confluence.otherworld_note.material.description", "Show Me The Money!");//"Material with you!"
        add("patchouli.confluence.otherworld_note.mob.name", "Mob");//Mobs
        add("patchouli.confluence.otherworld_note.mob.description", "Are Slimes Animals or Plants?\n");//"Is slime an animal or a plant?"
        add("patchouli.confluence.otherworld_note.tool.name", "Tool");//Tools
        add("patchouli.confluence.otherworld_note.tool.description", "Let's Dig In!");//"Isn't this a pickaxe!"
        add("patchouli.confluence.otherworld_note.weapon.name", "Weapon");//Weapons
        add("patchouli.confluence.otherworld_note.weapon.description", "It's Dangerous To Go Alone!");//"Take my sword!"
        add("patchouli.confluence.otherworld_note.world.name", "World");//Worlds
        add("patchouli.confluence.otherworld_note.world.description", "Loading Terrain.. *iHiding loot*iHiding more loot*iHiding MORE loot");//"Loading Terrain...*iHiding treasures...*iHiding more treasures...*iHiding more and more treasures..."

        // Entries
        // Accessories
        // Boss Checklist
        add("patchouli.confluence.otherworld_note.boss_checklist.how_to_summon", "How to summon");

        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.name", "Brian of Cthulhu");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.0", "Brains this size come with a side of madness.");//"The Brain of Cthulhu looks like an exposed brain, but it's really big."
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.1", "*z$(#AA0000)“An enormous demon brain which haunts the creeping crimson.”*c*w");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.2", "The Brain of Cthulhu will be invulnerable until its defenses are removed. You must first kill its 20 Visual Neuron");//"The Brain of Cthulhu cannot be attacked until its defenses are down, all of its 20 Visual Neurons must be killed before."
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.2.0", "Visual Neuron");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.3", "Monocle Enthusiast");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.4", "When all of the eyeballs are destroyed, the Brain of Cthulhu will summon 3 invincible illusions, but will expose its heart.");//"When all the Vision Neurons are destroyed, the Brain of Cthulhu exposes its heart and summons 3 Brain of Cthulhu Illusions that are invulnerable."
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.4.0", "Brain Fake");//Brain of Cthulhu Illusions
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.5", "*o“It is both true and false. When false is made true, true becomes false. When nothing is made, something becomes nothing..”*c*b*t--Excerpt from *'A Dream of Red Mansions*'");//"*o“When the false is made to appear true, the true becomes false; when the non-existent is made to appear existent, the existent becomes non-existent.”*b*t--A Dream of Red Mansions*c"
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.6", "Use a Bloody Spine in !w:cr!the Crimson biome*c, or destroy 3 Crimson Hearts generated in a Crimson world to summon the Brain of Cthulhu.*bMust be summoned at night.");//"Use the Bloody Spine in !w:cr!The Crimson*c, or destroy 3 Crimson Hearts that spawn in a Crimson world to summon the Brain of Cthulhu.*bOnly summoned at night."
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.7", "Bloody Spine is a craftable boss-summoning item that looks like a stripped spine. *bIt can be used to instantly summon the Brain of Cthulhu in the Crimson. *2To craft Bloody Spine, use 30 Vicious Powder and 15 Vertebra on the !cs:a!Altar*c.");//"The Bloody Spine is an item that looks like a stripped spine used to summon the boss. *bIt can be used to instantly summon the Brain of Cthulhu in the Crimson.*2Use 30 Vicious Powder and 15 Vertebrae on the !cs:a!Altar*c to craft a Bloody Spine."
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.8", "*z$(#9A5CC6)*'A piece of Cthulhu ripped from his body centuries ago in a bloody war. It wanders the night seeking its host body... and revenge.*'");//"*o$(#555555)“A piece of peeled body tissue, mottled with blood clots and muscle tissue, it is hard to imagine what kind of organism it was taken from, and how it was taken out. It seems to be trying to talk to you...*c"

        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.name", "Eater of Worlds");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.0", "Proof that corruption is contagious - even giant worms catch it.");//"The Eater of Worlds is a giant...worm? It looks like a corrupted version of the Worm from underground."
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.1", "*z$(#AA00AA)*'A massive worm who dwells in the corruption.*'*c*w");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.2", "When any segment is killed, it will split! The Eater of Worlds is only truly defeated when all its segments are killed.");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.2.0", "Eater Of Worlds Segment");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.3", "Rising, Step-by-Step (literally)");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.4", "Use Worm Bait in !w:co!the Corruption*c, or destroy 3 Shadow Orbs that spawn in corrupted worlds, to summon the Eater of Worlds. *2Can be summoned at any time of day.");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.5", "Worm Bait is a craftable boss-summoning item that looks like a piece of rotten meat. *bIt can be used to instantly summon the Eater of Worlds in the Corruption. *2To craft Worm Bait, use 30 Vile Powder and 15 Rotten Flesh on the !cs:a!Altar*c.");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.6", "*z$(#555555)*'It smells like rotten pork ribs and seems to strongly attract those diseased subterranean creatures.*'");

        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.name", "Eye of Cthulhu");
        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.0", "The Eye of Cthulhu is fast and fierce, but, as a giant eye, surprisingly inaccurate. *bWhen low on health, its retina will open with sharp teeth. *bIn this second phase, the Eye of Cthulhu goes berserk. *2While berserk, its accuracy, speed, and strength will all increase.");
        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.1", "*z$(#9A5CC6)*'An ocular menace who only appears at night.*'*c*w");
        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.2", "Use the Suspicious Looking Eye anywhere to summon the Eye of Cthulhu. *bSometimes the Eye of Cthulhu will appear at night： $(#AA0000)*'You feel that there is an evil thing watching you..*'*c*2Must be summoned at night.");
        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.3", "The Suspicious Looking Eye is a craftable boss-summoning item that looks like a gouged eye. *bIt can be used to instantly summon the Eye of Cthulhu at any location. *2To craft Worm Bait, *cuse 6 lenses on the !cs:a!Altar*c.");
        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.4", "*o$(#555555)*'$(#555555)“A lifeless glassy eye. Although not always aggressive, it seems much more dangerous than its counterparts that fly around in the air at night. It seems to be watching you.*'");

        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.name", "King Slime");
        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.0", "The King Slime is a massive slime. Not only can it jump higher and farther than ordinary slimes, but it can also teleport when you get too far away from it. *2Well, it also has a huge crown！");
        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.1", "*z$(#555555)*'The lord of all things slimy.*'*c*w");
        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.2", "Use the Slime Crown anywhere, or kill 150 slimes in Slime Rain, to summon the King Slime. *2Can be summoned at any time.");
        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.3", "The Slime Crown is a craftable boss-summoning item that looks like a small blue slime wearing a crown. *bIt can be used to instantly summon the King Slime at any location. *2To craft a Slime Crown, *cuse 20 Gel and a Gold or Platinum Crown on the !cs:a!Altar*c.");
        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.4", "*o$(#555555)*'A tiny crown that looks like it was made for the coronation of one of those cute, harmless gelatin creatures. Probably not a good idea to wear it.*'");

        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.name", "Queen Bee");
        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.0", "The Queen Bee is the ruler of all bees, so, by shaking its body segments, it will summon a large swarm of bees to attack you. *bThe beehive which hangs underneath it is certainly not decorative and will shoot poisonous stingers! *br*Be mindful that the Queen Bee occasionally dives in at you.*2*oNever take the Queen Bee out of the jungle. When the Queen Bee's eyes turn red, end the fight as soon as possible or..*c*l$(#FF0000) RUN*c");
        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.1", "*z$(#FFAA00)“The matriarch of the jungle hives.。”*c*w");
        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.2", "Use the Abomination Bee in the jungle, or destroy the larvae in the beehive to summon the World-Eater monster. *2can be summoned at any time.");
        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.3", "The Abeemination is a craftable boss-summoning item that looks like a Baby Bee. *bIt can be used to instantly summon the King Bee at Jungle. *2To craft a Abeemination, *cuse 5 Honey Block, 5 Jungle Hive Block, Stinger and Honey Bottle on the !cs:a!Altar*c.");
        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.4", "*o$(#555555)*'She seemed to be jealous and disgusted by the scent of those fluorescent fungus spores. A cluster of still-undeveloped young bees felt like sticky honey to the touch... The queen bee's protective instinct for her sweet territory gradually transformed into jealousy, exclusion, and hatred towards non-kindred beings. The flapping of wings shook the heaviest leaves in the jungle.*'");

        // Work Station
        add("patchouli.confluence.otherworld_note.crafting_stations.appearance", "appearance");
        add("patchouli.confluence.otherworld_note.crafting_stations.example", "A Example");
        add("patchouli.confluence.otherworld_note.crafting_stations.gui", "GUI");
        add("patchouli.confluence.otherworld_note.crafting_stations.how_to_use", "How to Use");

        add("patchouli.confluence.otherworld_note.crafting_stations.alchemy_table.0", "The Alchemy Table is almost the Plus version of the brewing table. Of course, it cannot be used to make the brewing table formula, but to make special brewing formulas. *b is made of wood boards, brewing tables, skulls, candles and glass bottles in!cs:hwb! Heavy duty tables*c.");
        add("patchouli.confluence.otherworld_note.crafting_stations.alchemy_table.1", "Put materials on the 6 grids on the left and right, stuff the water bottle (or other special things) in the middle and upper, and then there will be finished products below.");
        add("patchouli.confluence.otherworld_note.crafting_stations.alchemy_table.2", "If you race something randomly, your synthesis may become weird.");

        add("patchouli.confluence.otherworld_note.crafting_stations.altar.name", "Altar");
        add("patchouli.confluence.otherworld_note.crafting_stations.altar.0", " are naturally-occurring crafting stations found mainly in and around!w:co!The Corruption*c or !w:cr!The Crimson*c, rarely seen underground。*bThey are used to make many boss-summoning and other special items.*bThey cannot be taken away, picked up, placed, or made.");
        add("patchouli.confluence.otherworld_note.crafting_stations.altar.1", "Place the item on the altar, right-click to take the item out while sneaking, and left-click to use the altar for synthesis when hands are empty.");
        add("patchouli.confluence.otherworld_note.crafting_stations.altar.2", "Demon Altar usually appear underground in corruption lands, with a few appearing underground in various parts of the world.");
        add("patchouli.confluence.otherworld_note.crafting_stations.altar.3", "Crimson Altar usually appear underground in corruption lands, with a few appearing underground in various parts of the world.");

        add("patchouli.confluence.otherworld_note.crafting_stations.extractinator.0", "A refiner is a functional block that can convert gravel, silt blocks, snow silt blocks, marine gravel and desert fossils into more valuable items (such as ores, coins, and gems), or convert fishing waste into low-level baits.*2*oGarbage Recycle Bin 3000.");
        add("patchouli.confluence.otherworld_note.crafting_stations.extractinator.1", "Just press the right button to the refiner by holding the item in your hand. The items in your hand will be refined at an extremely fast speed, and the result of the refining will fall out in the form of a drop.*2MAX SPEED!!!");
        add("patchouli.confluence.otherworld_note.crafting_stations.extractinator.2", "Items scattered all over the floor.");
        add("patchouli.confluence.otherworld_note.crafting_stations.extractinator.3", "You can get a refiner in a Wooden Crate.");

        add("patchouli.confluence.otherworld_note.crafting_stations.heavy_work_bench.0", "The heavy workbench is a bit like an upgraded version of the workbench, but it cannot be used to make the 3x3 formula for the workbench, but instead makes the 4x4 formula for the heavy workbench.*bIt can be used to make a variety of building blocks, tools and decorative items.");
        add("patchouli.confluence.otherworld_note.crafting_stations.heavy_work_bench.1", "In fact, it's almost an ordinary workbench, but uses a 4x4 formula.");
        add("patchouli.confluence.otherworld_note.crafting_stations.heavy_work_bench.2", "If the recipe is the same, you can use the arrow on the right to switch the finished product.");

        add("patchouli.confluence.otherworld_note.crafting_stations.hellforge.0", "It is more resistant to burning than blast furnace (and compatible with the formula of blast furnace!), which means this is an upgraded version of blast furnace.*bSomewhere in !w:nt!Nether Tower*c there will be a probability of generating the Hell furnace.");
        add("patchouli.confluence.otherworld_note.crafting_stations.hellforge.1", "Enter the material in the middle 4 places, put fuel in the lower left corner, and wait for a moment and the finished product will appear on the right.");
        add("patchouli.confluence.otherworld_note.crafting_stations.hellforge.2", "It doesn't fit the appearance of a furnace and a blast furnace, right? But it's not bad.");

        add("patchouli.confluence.otherworld_note.crafting_stations.lead_anvil.0", "In fact, lead anvil is the skin replacement of anvil, and its function is exactly the same as that of anvil.*2It seems... cheaper than anvil?");
        add("patchouli.confluence.otherworld_note.crafting_stations.lead_anvil.1", "It's an anvil, otherwise.");

        add("patchouli.confluence.otherworld_note.crafting_stations.sky_mill.0", "It sounds amazing, but it is actually a furniture builder.*bIn the box of !w:hi!Heaven Island*c, there is a chance to generate a heavenly grind.");
        add("patchouli.confluence.otherworld_note.crafting_stations.sky_mill.1", "Just like a stone cutting machine, it is ready to use.");
        add("patchouli.confluence.otherworld_note.crafting_stations.sky_mill.2", "Place the materials below three places, select the formula on the right, and there will be a finished product on the top.");

        add("patchouli.confluence.otherworld_note.crafting_stations.workshop.0", "Craftsman workshops can be used to make many accessories, and they do not need to be placed in order!*bMade in!cs:hwb!Heavy Work Bench*c with Wooden Planks, Red Wool, Book, Smithing Table, Anvil or !cs:la!Lead Anvil*c and Leather.*2Everything is the scheme of the Goblin Tinkerer!");
        add("patchouli.confluence.otherworld_note.crafting_stations.workshop.1", "12 material grids can be synthesized by surrounding the finished product in the middle.");
        add("patchouli.confluence.otherworld_note.crafting_stations.workshop.2", "If the recipe is the same, you can use the arrow on the right to switch the finished product.");

        // Fishing
        add("patchouli.confluence.otherworld_note.fishing.bait.name", "Fishing Bait");
        add("patchouli.confluence.otherworld_note.fishing.bait.0", "While not strictly required for fishing, using bait will yield better loot!");

        add("patchouli.confluence.otherworld_note.fishing.crates.name", "Crates");
        add("patchouli.confluence.otherworld_note.fishing.crates.0", "Crates are stackable loot bag-type items containing randomized contents, including Potions, Fishing Bait, useful items, Coins, Ingots, and even Ores.*bThe rarer the type of crate, the more valuable its contents.");

        // Food
        add("patchouli.confluence.otherworld_note.food.cloud_bread.name", "Cloud Bread");
        add("patchouli.confluence.otherworld_note.food.cloud_bread.0", "Cloud Dough is used to craft Cloud Bread, which requires 3 Floating Wheat to synthesize.");
        add("patchouli.confluence.otherworld_note.food.cloud_bread.1", "Restores 5 hunger and 30 saturation.*bGrants Levitation II (0:15), Slow Falling II (0:30), and Nourishment II (5:00).");
        add("patchouli.confluence.otherworld_note.food.cloud_bread.2", "Unlike regular bread, it doesn't require linear arrangement for crafting.");
        add("patchouli.confluence.otherworld_note.food.cloud_bread.3", "Why am I flying~");

        add("patchouli.confluence.otherworld_note.food.fruit.name", "Fruits");
        add("patchouli.confluence.otherworld_note.food.fruit.0", "Fruits are edible food items (except Mandarin Orange) that restore 4 hunger and 12 saturation.*bConsuming any fruit grants 5-minute Nourishment.*bStar Fruit, Dragon Fruit, and Grapes are exceptions - they restore 6 hunger and 42 saturation (capped at 20 due to overflow), providing 5-minute Nourishment II and 50-second Hunger Delay.");
        add("patchouli.confluence.otherworld_note.food.fruit.1", "Breaking various leaves may yield fruits.*bAll fruits can be prismatically transmuted into Ambrosia.");
        add("patchouli.confluence.otherworld_note.food.fruit.2", "Mandarin Orange is a special fruit that cannot be consumed directly.*bIt must be peeled by right-clicking while holding to obtain Peeled Mandarin Orange.");
        add("patchouli.confluence.otherworld_note.food.fruit.3", "Edible peeled citrus. Restores 4 hunger and 12 saturation, granting 5-minute Nourishment.");

        // Mana
        add("patchouli.confluence.otherworld_note.mana.mana_regeneration.name", "Mana Regeneration");
        add("patchouli.confluence.otherworld_note.mana.mana_regeneration.0", "Mana naturally regenerates when not in use until reaching the character's current maximum capacity.*bNatural regeneration rate is affected by movement state and current Mana level.*bAdditional regeneration can be achieved through potions, accessories, or collecting Mana Stars.");

        add("patchouli.confluence.otherworld_note.mana.mana_capacity.name", "Mana Capacity");
        add("patchouli.confluence.otherworld_note.mana.mana_capacity.0", "Each character starts with 20 Mana Capacity (represented by one star on the Mana Bar).*bUsing Mana Crystals permanently increases capacity by 20 per crystal, adding another star.*bThis enhancement can be applied 9 times, totaling 10 stars (including the initial one) for 200 Mana.");

        // Material
        add("patchouli.confluence.otherworld_note.material.coin.name", "Coins");
        add("patchouli.confluence.otherworld_note.material.coin.0", "Coins come in four tiers: Copper, Silver, Gold, Platinum.*bSpecial Emerald Coin has unique functions explained later.*bAcquisition methods:*iDropped by hostile mobs;*iLoot chests;*i!cs:e!Refiner*c.*2*o\"Money makes the world go 'round, kid.\"   --Mr. Krabs");
        add("patchouli.confluence.otherworld_note.material.coin.1", "Coins can be converted between tiers using base-100 system.*bExample: 100 Copper Coins = 1 Silver Coin.*b1 Platinum Coin equals:*i100 Gold Coins;*i10,000 Silver Coins;*i1,000,000 Copper Coins*b(1 Platinum = 1x10^6 Copper).");
        add("patchouli.confluence.otherworld_note.material.coin.2", "Coins picked up, hotkey-deposited, or taken from containers auto-stack.*bManual container transfers require re-pickup for auto-stacking.*bManual merging: Hold coins and sneak right-click to upgrade.*b!cs:hwb!Heavy Workbench*c handles tier conversion.");
        add("patchouli.confluence.otherworld_note.material.coin.3", "Coins can be placed as blocks.*bCoin piles break into coins when destroyed.*bRight-click blocks while holding coins to place.*bPlaced coins have sand-like physics, no stacking limits.");
        add("patchouli.confluence.otherworld_note.material.coin.4", "Emerald Coins trade with Bankers.*bBankers use Gold/Platinum blocks as workstations.*2All coins drop on player death.");
        add("patchouli.confluence.otherworld_note.material.coin.5", "GoldGoldGoldGoldGoldGoldGoldGoldGold");

        add("patchouli.confluence.otherworld_note.material.corruption_material.name", "Corruption Materials");
        add("patchouli.confluence.otherworld_note.material.corruption_material.0", "!w:co!Corrupted Lands*c yield special drops from mobs and chests.*2Evil... is power.");
        add("patchouli.confluence.otherworld_note.material.corruption_material.1", "Dropped by Soul Eaters, Devourers, and Rotten Corpses.");
        add("patchouli.confluence.otherworld_note.material.corruption_material.2", "Dropped by Devourers.");
        add("patchouli.confluence.otherworld_note.material.corruption_material.3", "Dropped by World Devourers.");
        add("patchouli.confluence.otherworld_note.material.corruption_material.4", "Corruption-exclusive mushroom for crafting Worm Bait.");
        add("patchouli.confluence.otherworld_note.material.corruption_material.5", "Arcane Inspiration Shroom🍄~");

        add("patchouli.confluence.otherworld_note.material.crimson_material.name", "Crimson Materials");
        add("patchouli.confluence.otherworld_note.material.crimson_material.0", "!w:cr!Crimson Lands*c yield special drops from mobs and chests.*2Evil... is power.");
        add("patchouli.confluence.otherworld_note.material.crimson_material.1", "Dropped by Blood Crawlers, Face Horrors, and Crimson Chimeras.");
        add("patchouli.confluence.otherworld_note.material.crimson_material.2", "Dropped by Oculi Neurons, found in Brain of Cthulhu treasure bags.");
        add("patchouli.confluence.otherworld_note.material.crimson_material.3", "Crimson-exclusive mushroom for crafting Bloody Spine.");
        add("patchouli.confluence.otherworld_note.material.crimson_material.4", "Enough with the blackpink jokes!");

        add("patchouli.confluence.otherworld_note.material.falling_star.name", "Falling Stars");
        add("patchouli.confluence.otherworld_note.material.falling_star.0", "Stars randomly fall at night, glowing for easy spotting.*bUsed to craft Mana Crystals for capacity upgrades.*bDeals 100 damage (50❤) on impact.*2*o\"Stars fall through the night sky, their uses manifold...");
        add("patchouli.confluence.otherworld_note.material.falling_star.1", "*o...Claim them before dawn, lest they vanish with daylight.\"*t--Guide");

        add("patchouli.confluence.otherworld_note.material.gel.name", "Gel");
        add("patchouli.confluence.otherworld_note.material.gel.0", "Slime constituent material from most slime mobs.*2Note: Gel ≠ Slimeballs.*bEdible fuel (flammable/non-sticky)*bSlimeballs (sticky/non-flammable)*2*oFood-grade vs industrial-grade");
        add("patchouli.confluence.otherworld_note.material.gel.1", "Rumored existence of rare Pink Gel...");

        add("patchouli.confluence.otherworld_note.material.gems.name", "Gems");
        add("patchouli.confluence.otherworld_note.material.gems.0", "Crafting materials found in Refiners and chests.*2\"Amethyst\" renamed to \"Exotic Amethyst\" to avoid conflicts.");

        // Mob
        add("patchouli.confluence.otherworld_note.mob.slime.name", "Slimes");
        add("patchouli.confluence.otherworld_note.mob.slime.0", "These new slimes exhibit greater diversity compared to original variants.");
        add("patchouli.confluence.otherworld_note.mob.slime.1", "Green Slime ≠ Minecraft Slime.");
        add("patchouli.confluence.otherworld_note.mob.slime.2", "This coloration isn't corruption - perhaps stepped on lapis lazuli.");
        add("patchouli.confluence.otherworld_note.mob.slime.3", "This hue isn't crimson - maybe ate too many poppies.");

        // Tool
        add("patchouli.confluence.otherworld_note.tool.axe.name", "Axe");
        add("patchouli.confluence.otherworld_note.tool.axe.0", "Axes serve dual purposes as tools and weapons - you'll want one!");
        add("patchouli.confluence.otherworld_note.tool.axe.1", "Rejuvenation Axe: Crafted with Maneater Vine, Jungle Spores, and Rejuvenation Staff.");
        add("patchouli.confluence.otherworld_note.tool.axe.2", "Golden Axe: Made from Ruby, Gold Axe, and Gold Bars.*b*oNot to be confused with Vanilla Gold Axe.");
        add("patchouli.confluence.otherworld_note.tool.axe.3", "Night's Edge Axe: Demonite Bar-crafted, unbreakable!");
        add("patchouli.confluence.otherworld_note.tool.axe.4", "Bloodlust Axe: Crimson Bar-forged, indestructible!");

        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.name", "Bottomless Bucket");
        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.0", "Infinite liquid container - never depletes when placing.*2Not consumed in crafting!");
        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.1", "Bottomless Water Bucket = 2 Water Buckets");
        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.2", "Bottomless Lava Bucket = Unlimited Power");
        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.3", "100% eco-friendly!");
        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.4", "Watch your step!");

        add("patchouli.confluence.otherworld_note.tool.bug_net.name", "Bug Net");
        add("patchouli.confluence.otherworld_note.tool.bug_net.0", "Catches insects for fishing...*2(No bugs in current version) (sad)");
        add("patchouli.confluence.otherworld_note.tool.bug_net.1", "Golden net captures small animals (pigs/cows)!*2(Still no bugs) (sadder)");

        add("patchouli.confluence.otherworld_note.tool.fishing_rod.name", "Fishing Rod");
        add("patchouli.confluence.otherworld_note.tool.fishing_rod.0", "Basic rods last forever, but upgraded versions yield better loot!");
        add("patchouli.confluence.otherworld_note.tool.fishing_rod.1", "Reinforced Rod: Lead/Iron Bars construction, extra durable!");
        add("patchouli.confluence.otherworld_note.tool.fishing_rod.2", "Fiberglass Rod: Found in underground jungle chests.");
        add("patchouli.confluence.otherworld_note.tool.fishing_rod.3", "Beetle Rod: Obtained from Oasis Crates.");

        add("patchouli.confluence.otherworld_note.tool.hammer.name", "Hammer");
        add("patchouli.confluence.otherworld_note.tool.hammer.0", "Base tool for breaking walls/dirt/leaves with 3x3 area effect.*bDestroys adjacent blocks when breaking targets.");
        add("patchouli.confluence.otherworld_note.tool.hammer.1", "Golden Hammer: Gold Bar-made, slightly fragile?");
        add("patchouli.confluence.otherworld_note.tool.hammer.2", "Demon Hammer: Demonite Bar-forged, unbreakable!");
        add("patchouli.confluence.otherworld_note.tool.hammer.3", "Flesh Hammer: Crimson Bar-crafted, indestructible!");

        add("patchouli.confluence.otherworld_note.tool.hook.name", "Grappling Hook");
        add("patchouli.confluence.otherworld_note.tool.hook.0", "Terrain traversal tool that launches chains.*bEquip in accessory slot, press F to fire.*bJump to detach.");
        add("patchouli.confluence.otherworld_note.tool.hook.1", "Includes gem hooks and special variants.");

        add("patchouli.confluence.otherworld_note.tool.misc.name", "Miscellaneous");
        add("patchouli.confluence.otherworld_note.tool.misc.0", "Weapons with bizarre functions and whimsical designs!");
        add("patchouli.confluence.otherworld_note.tool.misc.1", "Magic Mirror: Teleports you home!*2No, it won't name the fairest.");
        add("patchouli.confluence.otherworld_note.tool.misc.2", "Frozen Mirror: Chilly to hold...*bThe ice is actually sweet - try licking it!");

        add("patchouli.confluence.otherworld_note.tool.paint.name", "Paint");
        add("patchouli.confluence.otherworld_note.tool.paint.0", "Recolors blocks using Brushes/Rollers/Sprayers.*bConsumes 1 paint per use.*bBasic paints sold by Painter NPC.");
        add("patchouli.confluence.otherworld_note.tool.paint.1", "Inventory order priority: Top-left first.*bHotbar takes precedence over backpack.");
        add("patchouli.confluence.otherworld_note.tool.paint.2", "Paint Brush: Full-block coloring.*bPaint Roller: Single-face application.*bMining removes paint.");
        add("patchouli.confluence.otherworld_note.tool.paint.3", "Paint Scraper: Removes paint without breaking blocks.*bSneak+right-click for single-face removal.");
        add("patchouli.confluence.otherworld_note.tool.paint.4", "Honestly, just break the block...");

        add("patchouli.confluence.otherworld_note.tool.pickaxe.name", "Pickaxe");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.0", "Essential dual-purpose tool - a must-have!");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.1", "Cactus Pickaxe: Surprisingly effective desert tool.");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.2", "Golden Pickaxe: Ruby-enhanced.*b*oDistinct from Vanilla version.");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.3", "Nightmare Pickaxe: Demonite+Scales, unbreakable!");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.4", "Deathbringer Pickaxe: Crimson+Tissue, indestructible!");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.5", "Fossil Pickaxe: Ancient yet sturdy!");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.6", "Molten Pickaxe: Hellstone Bars+Blaze Rods, pre-Skeletron!");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.7", "Bone Pickaxe: Faster mining pre-Skeletron.");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.8", "Reaver Shark: Obtained by fishing.*b*oShorky sharky :P");

        add("patchouli.confluence.otherworld_note.tool.rope.name", "Rope");
        add("patchouli.confluence.otherworld_note.tool.rope.0", "Ordinary ropes that could save your life in peril!");
        add("patchouli.confluence.otherworld_note.tool.rope.1", "Combine strands for enhanced functionality!");

        // Weapon
        add("patchouli.confluence.otherworld_note.weapon.arrow.name", "Arrows");
        add("patchouli.confluence.otherworld_note.weapon.arrow.0", "");
        add("patchouli.confluence.otherworld_note.weapon.arrow.1", "");
        add("patchouli.confluence.otherworld_note.weapon.arrow.2", "");
        add("patchouli.confluence.otherworld_note.weapon.arrow.3", "");
        add("patchouli.confluence.otherworld_note.weapon.arrow.4", "");
        add("patchouli.confluence.otherworld_note.weapon.arrow.5", "");

        add("patchouli.confluence.otherworld_note.weapon.bomb.name", "Bombs");
        add("patchouli.confluence.otherworld_note.weapon.bomb.0", "Explosives for mining, found in Golden Chests or broken pots.");
        add("patchouli.confluence.otherworld_note.weapon.bomb.1", "Sticky Bomb: Bomb + Gel.*bAdheres to surfaces on impact.");
        add("patchouli.confluence.otherworld_note.weapon.bomb.2", "Bouncy Bomb: Bomb + Pink Gel.*bRebounds with height matching fall distance.");
        add("patchouli.confluence.otherworld_note.weapon.bomb.3", "Beetle Bomb: Bomb + Sturdy Fossil.*bFound in desert pots.");

        add("patchouli.confluence.otherworld_note.weapon.boomerang.name", "Boomerangs");
        add("patchouli.confluence.otherworld_note.weapon.boomerang.0", "Wooden Boomerang - foundational throwable weapon.");
        add("patchouli.confluence.otherworld_note.weapon.boomerang.1", "Enchanted Boomerang: Upgraded version with unbreakable trait.");
        add("patchouli.confluence.otherworld_note.weapon.boomerang.2", "Shroomerang: Dropped by Spore Bats.");
        add("patchouli.confluence.otherworld_note.weapon.boomerang.3", "Flamearang: Enchanted Boomerang + Hellstone.");
        add("patchouli.confluence.otherworld_note.weapon.boomerang.4", "Tri-point Boomerang: Combines enchanted and frost variants.");

        add("patchouli.confluence.otherworld_note.weapon.bow.name", "Bows");
        add("patchouli.confluence.otherworld_note.weapon.bow.0", "Ranger's weapon, usable by all classes.");
        add("patchouli.confluence.otherworld_note.weapon.bow.1", "Fossilized Bow: Converts swords into fossil arrows.");
        add("patchouli.confluence.otherworld_note.weapon.bow.2", "");
        add("patchouli.confluence.otherworld_note.weapon.bow.3", "Demon Bow: Demonite Bars - unbreakable!");
        add("patchouli.confluence.otherworld_note.weapon.bow.4", "Tendon Bow: Crimson Bars - indestructible!");
        add("patchouli.confluence.otherworld_note.weapon.bow.5", "Hellfire Bow: Hellstone Bars - eternal flame!");
        add("patchouli.confluence.otherworld_note.weapon.bow.6", "");

        add("patchouli.confluence.otherworld_note.weapon.dynamite.name", "Dynamite");
        add("patchouli.confluence.otherworld_note.weapon.dynamite.0", "High-powered explosive found in Golden Chests.");
        add("patchouli.confluence.otherworld_note.weapon.dynamite.1", "Sticky Dynamite: Clings to surfaces.");
        add("patchouli.confluence.otherworld_note.weapon.dynamite.2", "Bouncy Dynamite: Ricochets with momentum.");
        add("patchouli.confluence.otherworld_note.weapon.dynamite.3", "Beetle Dynamite: Sturdy Fossil variant for desert mining.");

        add("patchouli.confluence.otherworld_note.weapon.short_bow.name", "Shortbows");
        add("patchouli.confluence.otherworld_note.weapon.short_bow.0", "Rapid-fire bows with lower damage output.");
        add("patchouli.confluence.otherworld_note.weapon.short_bow.1", "*oNot a flint drill!*c");

        add("patchouli.confluence.otherworld_note.weapon.short_sword.name", "Shortswords");
        add("patchouli.confluence.otherworld_note.weapon.short_sword.0", "Fast-attacking blades with reduced damage.");
        add("patchouli.confluence.otherworld_note.weapon.short_sword.1", "Such a cute little dagger~");

        add("patchouli.confluence.otherworld_note.weapon.sword.name", "Broadswords");
        add("patchouli.confluence.otherworld_note.weapon.sword.0", "Warrior's weapon, accessible to all.");
        add("patchouli.confluence.otherworld_note.weapon.sword.1", "Cactus Sword: Prickly desert weapon.");
        add("patchouli.confluence.otherworld_note.weapon.sword.2", "Golden Broadsword: Ruby-enhanced.*b*oDistinct from Vanilla Gold Sword.");

        // World
        add("patchouli.confluence.otherworld_note.world.appearance", "Appearance");

        add("patchouli.confluence.otherworld_note.world.ash_forest.name", "Ash Forest");
        add("patchouli.confluence.otherworld_note.world.ash_forest.0", "Ashwood is a biome that will generate in the lower realm, where a large amount of ash grows.*bThe ash forest is almost all composed of ashes blocks, and the top is covered by ash grass blocks, which grow on it.*2Ash grows here, and players can obtain ash and its derivatives here.*bFlame flowers will also be generated here.");
        add("patchouli.confluence.otherworld_note.world.ash_forest.1", "*z$(#AAAAAA)“The buds and new marks are born in the ashes.”");
        add("patchouli.confluence.otherworld_note.world.ash_forest.2", "*o*tThe gray dust is lifeless and dull,*b*tThe crimson world is fraught with danger.");

        add("patchouli.confluence.otherworld_note.world.ash_wasteland.name", "Ash Wasteland");
        add("patchouli.confluence.otherworld_note.world.ash_wasteland.0", "Ash Wasteland is a barren biome that generates in the lower realms.*bAsh Wasteland is almost all composed of ashes, and the terrain is similar to the lower boundary wasteland.*2Ashes Prison Stones will be generated more here and are quite conspicuous, so this is a good place to dig out Prison Stones.*bFlame flowers will also be generated here.");
        add("patchouli.confluence.otherworld_note.world.ash_wasteland.1", "*z$(#AAAAAA)“The Final Chapter of the Burning Ember”");
        add("patchouli.confluence.otherworld_note.world.ash_wasteland.2", "*o*tThe dark dust hides treasures,*b*tThe crimson world is fraught with danger.");

        add("patchouli.confluence.otherworld_note.world.enchanted_sword_shrine.name", "Enchanted Sword Shrine");
        add("patchouli.confluence.otherworld_note.world.enchanted_sword_shrine.0", "Enchanted Sword Shrine is a structure that will randomly generate in the world, with an overall style similar to a lush cave.*bThere will be a long and narrow space directly above that connects the ground (although it is not usually connected).");
        add("patchouli.confluence.otherworld_note.world.enchanted_sword_shrine.1", "*z$(#B4684D)“The magical artifact that isolates the world”");
        add("patchouli.confluence.otherworld_note.world.enchanted_sword_shrine.2", "*o*tBuried deep in the rock formations flowing through the moonlight, hidden in the cracks of the evil altar, the mottled rocks are hidden secretly with star-black debris.*b*tHiding from caves outside the world and hiding in hidden corners of the world, the deep blue and sharp sword still shines with starlight.");

        add("patchouli.confluence.otherworld_note.world.heaven_island.name", "Heaven Island");
        add("patchouli.confluence.otherworld_note.world.heaven_island.0", "Heaven Island is a floating island generated over the world, with two variants: Heaven Island and Paradise Lake.*2Heaven Island is composed of clouds wrapped in a piece of land, containing a sky hut, which must contain two boxes.*bHeaven Lake is all made up of clouds and water, and you can fish here.*2*oFishing on Aky!");
        add("patchouli.confluence.otherworld_note.world.heaven_island.1", "*z$(#9A5CC6)“The noble treasure”");
        add("patchouli.confluence.otherworld_note.world.heaven_island.2", "*o*tThe floating island hangs among the clouds, the earth and stone bases hold up the marks of thousands of years of moss, and the broken temple is filled with solidified starlight.*b*tThe floating lake stands in the top of the clouds, the floating clouds surround the source of life, and the hidden blue water preserves the flowing life.");
        add("patchouli.confluence.otherworld_note.world.heaven_island.3", "*z$(#9A5CC6)“Silent life”");

        add("patchouli.confluence.otherworld_note.world.living_tree.name", "Living Tree");
        add("patchouli.confluence.otherworld_note.world.living_tree.0", "The Living Tree is a large tree-like structure that only generates in forest ecosystems.*bThe inside of the bottom part of the Living Tree's surface is hollow, containing a box with contents consistent with other surface boxes.*bInside, there is also a passage leading to the underground section. If you follow the passage down, you can find a small room containing a box with unique items.");
        add("patchouli.confluence.otherworld_note.world.living_tree.1", "*z$(#55FF55)“Healthy Tree of Life”");
        add("patchouli.confluence.otherworld_note.world.living_tree.2", "*o*tThe towering giant trees stand firm in the world,*b*tSenluo's evil causes qualitative change.");

        add("patchouli.confluence.otherworld_note.world.nether_tower.name", "Nether Tower");
        add("patchouli.confluence.otherworld_note.world.nether_tower.0", "The Netherlands Tower is a structure that generates near the Nether Magma Sea. It will only spawn in the Nether Wasteland and Ash Wasteland biomes.*bIt consists of two main towers and the bridge connecting them, and resources are distributed in each area. The main tower has three floors, and there are some differences between the two towers: one is a crimson wood board on the bottom, and the other is a weird wood board on the bottom half of the building. They can be distinguished by the color system of the lower half of the building.");
        add("patchouli.confluence.otherworld_note.world.nether_tower.1", "*z$(#FF5555)“Hot treasure land”");
        add("patchouli.confluence.otherworld_note.world.nether_tower.2", "*o*tThere are treasures hidden in the scorching tower.*b*tThe blocked wronged soul tells the dirty.");

        add("patchouli.confluence.otherworld_note.world.queen_bee_hive.name", "Queen Bee Hive");
        add("patchouli.confluence.otherworld_note.world.queen_bee_hive.0", "Queen Bee Hive is a giant hive that naturally arises from the underground of the jungle cluster.*bIt is mainly composed of jungle honeycombs, and there is a larger honey pool near the bottom.*2*lNotice! In the middle of the honey pool there will be a larva covered with thin honey blocks based on the jungle hive. If it is destroyed, the queen bee will be summoned!");
        add("patchouli.confluence.otherworld_note.world.queen_bee_hive.1", "*z$(#FFFF55)“Sweet bomb”");
        add("patchouli.confluence.otherworld_note.world.queen_bee_hive.2", "*o*tSweet honey is hidden in poison.*b*tThe silt is wrapped in the silt.");

        add("patchouli.confluence.otherworld_note.world.shimmer_lake.name", "Shimmer Lake");
        add("patchouli.confluence.otherworld_note.world.shimmer_lake.0", "Shimmer Lake is a structure that will randomly generate in the world where a large number of shimmer lights are generated.*bIn the middle of Shimmer Lake, there is a large pool of shimmering light surrounded by several gem trees.");
        add("patchouli.confluence.otherworld_note.world.shimmer_lake.1", "*z$(#FF55FF)“Beautiful and quiet, illusory and empty”");
        add("patchouli.confluence.otherworld_note.world.shimmer_lake.2", "*o*tShimmer Lake is like the falling fragments of the starry sky, and the liquid starlight ripples in the silence.*b*tThe secret realm is shrouded in soft blue mist, and the lake is hanging upside down like a mirror with the eternal twilight. When it is immersed in it, it turns into a silhouette of nothingness, sinking into the endless dream with the whispers of billions of stars.");

        add("patchouli.confluence.otherworld_note.world.sky_village.name", "Sky Village");
        add("patchouli.confluence.otherworld_note.world.sky_village.0", "Sky Village is a building generated over the world, just like!w:hi! Paradise Island*c. Sky Village consists of multiple floating islands, the central floating island and other floating islands are connected by bridges. Other floating islands include farmland floating islands and house floating islands, and house floating islands have the following types: *iBlacksmith shop; *iStonemason House; *iObservatory; *iStable Island; *iCathedral; *iLibrary.");
        add("patchouli.confluence.otherworld_note.world.sky_village.1", "*z$(#B4684D)“The floating peach blossom source.”");
        add("patchouli.confluence.otherworld_note.world.sky_village.2", "*o*tThe floating island gathers into a collective, the auxiliary roads connect civilization, and the corrupt thieves stay away from the world.*b*tPeach orchards float in solitude, courtyard safeguards loneliness, and the waves sever ties with the world.");
        add("patchouli.confluence.otherworld_note.world.sky_village.3", "*z$(#B4684D)“The lonely home”");

        add("patchouli.confluence.otherworld_note.world.the_corruption.name", "The Corruption");
        add("patchouli.confluence.otherworld_note.world.the_corruption.0", "The Corruption is an evil biome with deep purple wasteland, death, and decay themes that generate corruption-themed enemies.*bThe Corruption group will naturally generate in a world where the evil biome is corrupted. You can also create The Corruption yourself. When there are more than 256 corrupt evil blocks (such as ebony stones) in a block, the biome of this block will become The Corruption.*bCorrespondingly, when there are less than 256 evil blocks, the cluster of this block will become the original biome.");
        add("patchouli.confluence.otherworld_note.world.the_corruption.1", "*z$(#AA00AA)“Making the world evil”");
        add("patchouli.confluence.otherworld_note.world.the_corruption.2", "*o*tThe Corruption is a dangerous area filled with rifts and cliffs, filled with the breath of evil and death.*b*tIt is a cancer produced by the evil thoughts of living beings, breeding terrible deformed monsters, which are jealous of all living beings.");

        add("patchouli.confluence.otherworld_note.world.the_crimson.name", "The Crimson");
        add("patchouli.confluence.otherworld_note.world.the_crimson.0", "The Crimson is an evil biome with a red bloody theme of physical infection that generates bloody enemies.*bThe Crimson group will naturally spawn in a world where the evil biome is scarlet. You can also create The Crimson by yourself. When there are more than 256 scarlet evil blocks (such as scarlet stones) in a block, the biome of this block will become The Crimson.*bCorrespondingly, when there are less than 256 evil blocks, the cluster of this block will become the original biome.");
        add("patchouli.confluence.otherworld_note.world.the_crimson.1", "*z$(#AA0000)“Making the world bloody”");
        add("patchouli.confluence.otherworld_note.world.the_crimson.2", "*o*tIn ancient times, the people of Terraria made a foolish mistake: they succumbed to scarlet, regarded it as a god, and sacrificed their flesh and blood.*b*tEventually, they are assimilated into twisted lives, spreading across the earth with scarlet ambitions.");
        // sound
        add("confluence.subtitle.transmission", "Transmission Magic: Activated");
        add("confluence.subtitle.lightsaber_open", "Lightsaber: Activated");
        add("confluence.subtitle.regular_staff_shoot", "Magic: Cast");
        add("confluence.subtitle.regular_staff_shoot_2", "Magic: Burst");
        add("confluence.subtitle.frozen_broken", "Frost Magic: Shatter");
        add("confluence.subtitle.frozen_arrow", "Frost Magic: Fire");
        add("confluence.subtitle.cooldown_recovery", "Cooldown: Ready");
        add("confluence.subtitle.bow_cooldown_recovery", "Bow Cooldown: Charged");
        add("confluence.subtitle.decoupling", "Hook: Detach");
        add("confluence.subtitle.achievements", "Achievement: Unlocked");
        add("confluence.subtitle.shimmer_detachment", "Creature: Exuding Shimmer");
        add("confluence.subtitle.shimmer_evolution", "Shimmer: Transmute");
        add("confluence.subtitle.shimmer_immersion", "Creature: Immersed in Shimmer");
        add("confluence.subtitle.transmutation_use", "Mystic Power: Channel");
        add("confluence.subtitle.hook_attach", "Grappling Hook: Attached");
        add("confluence.subtitle.hook_shoot", "Grappling Hook: Fired");
        add("confluence.subtitle.shimmer_item_interactions", "Item: Immersed in Shimmer");
        add("confluence.subtitle.star", "Falling Star: Shine");
        add("confluence.subtitle.star_lands", "Falling Star: Landed");
        add("confluence.subtitle.terra_operation", "Action: Operate");
        add("confluence.subtitle.life_crystal_use", "Life Crystal: Consume");
        add("confluence.subtitle.mana_star_use", "Mana Star: Consume");
        add("confluence.subtitle.coins", "Coins: Jingle");
        add("confluence.subtitle.coins_small", "Small Coins: Collected");
        add("confluence.subtitle.coins_medium", "Medium Coins: Collected");
        add("confluence.subtitle.coins_large", "Large Coins: Collected");

        add("terra_curio.subtitle.transmission", "Transmission Magic: Activated");
        add("terra_curio.subtitle.fart_sound", "Player: Fart Sound");
        add("terra_curio.subtitle.double_jump", "Player: Double Jump");
        add("terra_curio.subtitle.shoes_walk", "Shoes: Walking");
        add("terra_curio.subtitle.rocket_boots_boost", "Rocket Boots: Boost");
        add("terra_curio.subtitle.rocket_boots_stop", "Rocket Boots: Stop");

        add("terra_entity.subtitle.routine_hurt", "Mob: Hurt");
        add("terra_entity.subtitle.routine_death", "Mob: Death");
        add("terra_entity.subtitle.roar", "Boss: Roar");
        add("terra_entity.subtitle.hurried_roaring", "Boss: Hurried Roar");
        add("terra_entity.subtitle.blood_crawler_death", "Blood Crawler: Death");
        add("terra_entity.subtitle.blood_crawler_free", "Blood Crawler: Blood Flow");
        add("terra_entity.subtitle.blood_crawler_hurt", "Blood Crawler: Hurt");
        add("terra_entity.subtitle.bloody_spore_death", "Bloody Spore: Death");
        add("terra_entity.subtitle.bloody_spore_fuse", "Bloody Spore: Gestation");
        add("terra_entity.subtitle.bloody_spore_hit", "Bloody Spore: Hit");
        add("terra_entity.subtitle.drippler_death", "Drippler: Death");
        add("terra_entity.subtitle.drippler_hurt", "Drippler: Hurt");
        add("terra_entity.subtitle.metal_death", "Metal Mob: Death");
        add("terra_entity.subtitle.metal_hurt", "Metal Mob: Hurt");
        add("terra_entity.subtitle.visual_neuron_death", "Visual Neuron: Death");
        add("terra_entity.subtitle.visual_neuron_hurt", "Visual Neuron: Hurt");
        add("terra_entity.subtitle.dig_sound", "Worm Creature: Digging");
        add("terra_entity.subtitle.giant_shelly_death", "Giant Shelly: Death");
        add("terra_entity.subtitle.giant_shelly_free_0", "Giant Shelly: Rolling");
        add("terra_entity.subtitle.giant_shelly_free_1", "Giant Shelly: Crawling");
        add("terra_entity.subtitle.giant_shelly_hurt", "Giant Shelly: Hurt");
        add("terra_entity.subtitle.tr_zombie_death", "Zombie: Death");
        add("terra_entity.subtitle.tr_skeleton_hurt", "Skeleton: Hurt");
        add("terra_entity.subtitle.waving", "Player: Waving");
        add("terra_entity.subtitle.use_mounts", "Player: Summon Mount");
        add("terra_entity.subtitle.decayeder_ambient", "Decayeder: Rubbing Body");
        add("terra_entity.subtitle.decayeder_death", "Decayeder: Death");
        add("terra_entity.subtitle.decayeder_hurt", "Decayeder: Hurt");
        add("terra_entity.subtitle.decayeder_step", "Decayeder: Footsteps");
        add("terra_entity.subtitle.whip_attack", "Whip: Lash");
        add("terra_entity.subtitle.routine_summon", "Summon: Summon");
        add("terra_entity.subtitle.summon_hornet", "Hornet: Summon");
        add("terra_entity.subtitle.summon_eye", "Flying Summon: Summon");
        add("terra_entity.subtitle.summon_imp", "Imp: Summon");
        // Tags
        add("tag.fluid.confluence.fishing_able", "Can Fishing Fluid");
        add("tag.fluid.confluence.not_lava", "Not Lava");
        add("tag.item.confluence.ammo", "Ammo");
        add("tag.item.confluence.boss_summoning", "Boss Summoning");
        add("tag.item.confluence.bottomless", "Bottomless");
        add("tag.item.confluence.coins", "Coins");
        add("tag.item.confluence.corals", "Corals");
        add("tag.item.confluence.crop_fortune", "Crop Fortune");
        add("tag.item.confluence.demonite_and_crimson_ingot", "Evil Ingot");
        add("tag.item.confluence.dye", "Dye");
        add("tag.item.confluence.evil_material", "Evil Material");
        add("tag.item.confluence.fast_bow", "Fast Bow");
        add("tag.item.confluence.gold_cooking", "Golden Cooking");
        add("tag.item.confluence.hardmode_ores", "Haed-Mode Ores");
        add("tag.item.confluence.hook", "Hook");
        add("tag.item.confluence.lead_and_iron", "Lead and Iron");
        add("tag.item.confluence.light_pet", "Light Pet");
        add("tag.item.confluence.mana_weapon", "Magic Weapon");
        add("tag.item.confluence.minecart", "Minecart");
        add("tag.item.confluence.moss_item", "Moss");
        add("tag.item.confluence.provide_life", "Provide Life");
        add("tag.item.confluence.provide_light", "Provide Light");
        add("tag.item.confluence.provide_mana", "Provide Mana");
        add("tag.item.confluence.shadow_scale_and_tissue_sample", "Shadow Scale and Tissue Sample");
        add("tag.item.confluence.summoner_weapon", "Summoner Weapon");
        add("tag.item.confluence.torch", "Torch");
        add("tag.item.confluence.treasure_bag", "Treasure Bag");
        add("tag.item.confluence.wings", "Wings");

        Consumer<DeferredHolder<Block, ? extends Block>> blockAction = block -> add(block.get(), LibUtils.toTitleCase(block.getId().getPath()));
        ChestBlocks.BLOCKS.getEntries().forEach(blockAction);
        CrateBlocks.BLOCKS.getEntries().forEach(blockAction);
        DecorativeBlocks.BLOCKS.getEntries().forEach(blockAction);
        FunctionalBlocks.BLOCKS.getEntries().forEach(blockAction);
        ModBlocks.BLOCKS.getEntries().forEach(blockAction);
        NatureBlocks.BLOCKS.getEntries().forEach(blockAction);
        OreBlocks.BLOCKS.getEntries().forEach(blockAction);
        PotBlocks.BLOCKS.getEntries().forEach(blockAction);
        StatueBlocks.BLOCKS.getEntries().forEach(blockAction);

        Consumer<DeferredHolder<Item, ? extends Item>> itemAction = item -> add(item.get(), LibUtils.toTitleCase(item.getId().getPath()));
        AccessoryItems.ITEMS.getEntries().forEach(itemAction);
        ArmorItems.ITEMS.getEntries().forEach(itemAction);
        ArrowItems.ITEMS.getEntries().forEach(itemAction);
        AxeItems.ITEMS.getEntries().forEach(itemAction);
        BaitItems.ITEMS.getEntries().forEach(itemAction);
        BoatItems.forEach(itemAction);
        BowItems.ITEMS.getEntries().forEach(itemAction);
        ConsumableItems.ITEMS.getEntries().forEach(itemAction);
        VanityArmorItems.ITEMS.getEntries().forEach(itemAction);
        DrillItems.ITEMS.getEntries().forEach(itemAction);
        FishingPoleItems.ITEMS.getEntries().forEach(itemAction);
        FoodItems.ITEMS.getEntries().forEach(itemAction);
        HamaxeItems.ITEMS.getEntries().forEach(itemAction);
        HoeShovelItems.ITEMS.getEntries().forEach(itemAction);
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
        LanceItems.ITEMS.getEntries().forEach(itemAction);
        GunItems.ITEMS.getEntries().forEach(itemAction);

        ModEffects.EFFECTS.getEntries().forEach(effect -> add(effect.get(), LibUtils.toTitleCase(effect.getId().getPath())));
        ModEntities.ENTITIES.getEntries().forEach(entity -> add(entity.get(), LibUtils.toTitleCase(entity.getId().getPath())));

        add(TooltipManager.prefix, "** Sponsor Item **");

        add("item.confluence.spawn_eggs", "%s Spawn Egg");

        // TouhouLittleMaid
        add("task.confluence.use_life_crystal", "Use Life Crystal");
        add("task.confluence.use_life_crystal.desc", "Mail will use life crystal to heal herself");
        add("task.confluence.use_life_crystal.condition.has_life_crystal", "Mainhand holds life crystal");

        PonderHelper.addTranslateKeys(this::add, true);
        WaystonesHelper.addTranslateKeys((block, s) -> add(Util.makeDescriptionId("block", block.getId()), s), true);
    }

    @Override
    public void add(String key, String value) {
        if (!((LanguageProviderAccessor) this).getData().containsKey(key)) {
            super.add(key, value);
        }
    }
}
