package org.confluence.terraentity.data.gen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.entity.npc.house.HouseDetectInfo;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.TEItems;
import org.confluence.terraentity.init.item.*;
import org.confluence.terraentity.integration.curios.CuriosHelper;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.confluence.terraentity.TerraEntity.MODID;

public class TEEnglishProvider extends LanguageProvider {
    public TEEnglishProvider(PackOutput output) {
        super(output, MODID, "en_us");
    }

    private static String toTitleCase(String raw) {
        return Arrays.stream(raw.split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    @Override
    protected void addTranslations() {

        Consumer<DeferredHolder<Item, ? extends Item>> itemAction = item -> add(item.get(), toTitleCase(item.getId().getPath()));
        TESpawnEggItems.ITEMS.getEntries().forEach(itemAction);
        TESummonItems.ITEMS.getEntries().forEach(itemAction);
        TEWhipItems.ITEMS.getEntries().forEach(itemAction);
        TEBoomerangItems.ITEMS.getEntries().forEach(itemAction);
        TERideableItems.ITEMS.getEntries().forEach(itemAction);
        TEPetItems.ITEMS.getEntries().forEach(itemAction);
        TEBossSummonsItems.ITEMS.getEntries().forEach(itemAction);
        TEYoyosItems.ITEMS.getEntries().forEach(itemAction);
        add(TEItems.HOUSE_DETECTOR.get(), "House Detector");
        TEEntities.ENTITIES.getEntries().forEach(entity -> add(entity.get(), toTitleCase(entity.getId().getPath())));
        TEEffects.EFFECTS.getEntries().forEach(effect -> add(effect.get(), toTitleCase(effect.getId().getPath())));

        add("terra_entity.trade_lock.drawer.time.title", "Time");
        add("terra_entity.trade_lock.drawer.time.title.expect", "Except");
        add("terra_entity.trade_lock.drawer.npc_exist.title", "%s is nearby");
        add("terra_entity.trade_lock.drawer.mood.title", "Mood");
        add("terra_entity.trade_lock.drawer.kill_entity.title", "Kill");
        add("terra_entity.trade_lock.drawer.biome.title", "Any of Biomes");
        add("terra_entity.trade_lock.drawer.biome.title.tag", "Biome Tag");
        add("terra_entity.trade_lock.drawer.and.title", "All should be satisfied");
        add("terra_entity.trade_lock.drawer.not.title", "None should be satisfied");
        add("terra_entity.trade_lock.drawer.or.title", "Any can be satisfied");

        add("itemGroup.terraentity.title", "Terra Entity");

        add("title.terra_entity.npc_trade", "Terra Shop");
        add("title.terra_entity.npc_trade.task.daily", "Daily Task");
        add("title.terra_entity.npc_trade.task.fixed_level", "Fixed Level Task");
        add("title.terra_entity.npc_trade.task.random", "Random Task");
        add("title.terra_entity.npc_trade.task.dynamic_reward", "Dynamic Reward Task");
        add("title.terra_entity.npc_trade.task.progress", "Progress Task");

        add("container.terra_entity.chester", "Chester");

        add("key.terra_entity.ride", "Use Ride(need CuriosAPI");


        add("entity.terra_entity.mother_slime", "Mother Slime");
        add("entity.terra_entity.baby_slime", "Baby Slime");

        add("message.terra_entity.trade.not_enough_items", "Not Enough Items");




        // Config
        add("terra_entity.configuration.boss_clear_when_no_target", "Clear Boss When No Target");
        add("terra_entity.configuration.boss_attributes_multiplier_health", "Boss Attributes Multiplier-Health");
        add("terra_entity.configuration.boss_attributes_multiplier_damage", "Boss Attributes Multiplier-Damage");
        add("terra_entity.configuration.boss_no_physics", "BOSS have no physics");
        add("terra_entity.configuration.boss_leave_on_day", "Specify BOSS Leave on Day");
        add("terra_entity.configuration.boss_keep_wandering", "BOSS keep wandering");


        add("terra_entity.configuration.display_summon_items", "Display Summon Items In Creative Tab");
        add("terra_entity.configuration.enhance_all_monster", "Enhance All Monster");
        add("terra_entity.configuration.monster_attributes_multiplier_health", "Monster Attributes Multiplier-Health");
        add("terra_entity.configuration.monster_attributes_multiplier_damage", "Monster Attributes Multiplier-Damage");
        add("terra_entity.configuration.spawn_without_light", "Spawn monsters without light");
        add("terra_entity.configuration.chance_to_spawn_slime_on_zombie_head", "Chance to Spawn Slime on Zombie Head");
        add("terra_entity.configuration.enemy_spawn_chance_apply_all", "Enemy Spawn Chance Apply to all monsters");
        add("terra_entity.configuration.enemy_spawn_chance", "Enemy of Terra Entity Spawn Chance ");
        add("terra_entity.configuration.enable_entity_motion_blur", "Enable Entity Motion Blur");
        add("terra_entity.configuration.behavior_tree_web_viewer_server_port", "Behavior Tree Web Viewer Server Port");


        add("terra_entity.configuration.boss_bar_style", "Boss Health Bar Style");
        add("terra_entity.configuration.boss_bar_number_offset_x", "Boss Health Bar Number Offset-X");
        add("terra_entity.configuration.boss_bar_number_offset_y", "Boss Health Bar Number Offset-Y");
        add("terra_entity.configuration.generate_projectile_particle", "Generate Projectile Particle");


        add("terra_entity.configuration.enableNonSpiderModel", "Spider Harmonization");
        add("terra_entity.configuration.enableNonSpiderModel.tooltip", "Enable this option if you dislike spiders or want to beautify them.(This configuration change requires a game restart!)");

        add("terra_entity.configuration.npc_chat_bubble_style", "NPC Chat Bubble Style");


        // Tooltip
        add("tooltic.terra_entity.summon_item.desc", "Hold right-click to release all summons");


        add("tooltip.terra_entity.summon_item_cost", "Summon Cost: %d");
        add("tooltip.terra_entity.summon_item_entity", "Summon Entity: %s");
        add("tooltip.terra_entity.summon_info", "Summon Info: %d / %d");
        add("tooltip.terra_entity.whip.hit_effect", "Hit Effect:");
        add("tooltip.terra_entity.whip.hit_effect_beneficial", "Farmer's Flogging");
        add("tooltip.terra_entity.house_detect.mode", "House Detect Mode: ");
        add("tooltip.terra_entity.house_detect.mode.check", "Check");
        add("tooltip.terra_entity.house_detect.mode.check.owner", "Owner");
        add("tooltip.terra_entity.house_detect.mode.add", "Add");
        add("tooltip.terra_entity.house_detect.mode.add.failed", "Add House failed. House exists!");
        add("tooltip.terra_entity.house_detect.mode.add.success", "Add House success!");
        add("tooltip.terra_entity.house_detect.mode.delete", "Delete");
        add("tooltip.terra_entity.house_detect.mode.delete.success", "Delete House success!");
        add("tooltip.terra_entity.house_detect.not_npc", "You should point at an NPC!");
        add("tooltip.terra_entity.house_detect.no_detect", "You should check before that!");
        add(HouseDetectInfo.DetectType.TOO_LARGE.getTranslationKey(), "House Too Large!");
        add(HouseDetectInfo.DetectType.TOO_SMALL.getTranslationKey(), "House Too Small!");
        add(HouseDetectInfo.DetectType.NO_DYNAMIC_LIGHT.getTranslationKey(), "House No Dynamic Light!");
        add(HouseDetectInfo.DetectType.NO_CHAIR.getTranslationKey(), "House No Chair!");
        add(HouseDetectInfo.DetectType.NO_TABLE.getTranslationKey(), "House No Table!");
        add(HouseDetectInfo.DetectType.FOUND_HOUSE.getTranslationKey(), "Found House!");
        add("tooltip.terra_entity.house_detect.info", "Press shift and right click to switch mode.");

        add("tooltip.terra_entity.chester.desc", "Press shift and right click to switch target container. You can bind a container to it.");
        add("tooltip.terra_entity.chester.current", "Current Container");

            // boomerang
        add("tooltip.terra_entity.boomerang.penetration", "Penetrates Count");
        add("tooltip.terra_entity.boomerang.on_hit_effects", "Effects");
        add("tooltip.terra_entity.boomerang.max_count", "Max Count");
        add("tooltip.terra_entity.boomerang.fly_speed", "Fly Speed");

        // yoyos
        add("tooltip.terra_entity.yoyo.max_range", "Max Range");
        add("tooltip.terra_entity.yoyo.exist_time", "Using Time");
        add("tooltip.terra_entity.yoyo.hit_effect", "Hit Effect");

        // rideable
        add("tooltip.terra_entity.rideable_item.desc", "Press hotkey to ride. Default R .");

        // attribute
        add("attribute.name.player.summon_damage", "Summon Damage");
        add("attribute.name.player.mark_damage", "Mark Damage");
        add("attribute.name.player.whip_range", "Whip Range");
        add("attribute.name.player.summon_knockback", "Summon Knockback");
        add("attribute.name.player.minion_capacity", "Minion Capacity");
        add("attribute.name.player.sentry_capacity", "Sentry Capacity");

        // track
        add("terra_entity.track_type.simple", "Simple Track");
        add("terra_entity.track_type.basis", "Basis Track");

        // hit effect

        add("terra_entity.effect.strategy.set_fire", "Set fire");
        add("terra_entity.effect.strategy.yoyo_bee", "Summon Bee");


        add("terra_entity.effect.strategy.mud", "Mud");
        add("terra_entity.effect.strategy.bat", "Blood absorb +1 hp");
        add("terra_entity.effect.strategy.lights_bane", "Summon lights bane");
        add("terra_entity.effect.strategy.bee_keeper", "Summon bees");
        add("terra_entity.effect.strategy.snapthorn", "Poisoned");
        add("terra_entity.effect.strategy.firecracker", "Hell fire");

        add("terra_entity.effect.strategy.frozen_burn_5_sec", "frozen burn 10 seconds");
        add("terra_entity.effect.strategy.frozen_burn_3_sec_50_chance", "50% chance frozen burn 3 seconds");
        add("terra_entity.effect.strategy.tentacle_spikes", "Tentacle spikes");
        add("terra_entity.effect.strategy.hunting_4_sec", "Hunting 4 seconds");
        add("terra_entity.effect.strategy.hell_fire_5_sec", "Hell fire 5 seconds");
        add("terra_entity.effect.strategy.set_fire_5_sec", "Set fire 5 seconds");
        add("terra_entity.effect.strategy.forzen_burn_5_sec", "Forzen Burn 5 seconds");
        add("terra_entity.effect.strategy.blood_butchered", "Blood Butchered");
        add("terra_entity.effect.strategy.bei_dou", "Random 5 effects:\\nfrozen burn IV 10 seconds\\nhell fire IV 10 seconds\\nwither IV 10 seconds\\npoison ⅳ 10 seconds\\ninstant harm VIII");

        add("terra_entity.effect.strategy.strength", "Anger");


        // TouhouLittleMaid
        add("task.terra_entity.boomerang_attack", "Boomerang Attack");
        add("task.terra_entity.boomerang_attack.desc", "Maid attacks enemies with a boomerang.");
        add("task.terra_entity.boomerang_attack.condition.has_boomerang", "Mainhand holds a boomerang");

        // curios
        add("curios.identifier." + CuriosHelper.MOUNT_KEY, "Mount");
        add("curios.identifier." + CuriosHelper.PET_KEY, "Pet");
        add("curios.identifier." + CuriosHelper.LIGHT_PET_KEY, "Light Pet");

        // 附魔
        add("enchantment.terra_entity.multi_boomerang", "Multi Shoot Boomerang");
        add("enchantment.terra_entity.multi_boomerang.desc", "Fires an additional boomerang");
        add("enchantment.terra_entity.summoner_pact", "Summoner Pact");
        add("enchantment.terra_entity.summoner_pact.desc", "Summons extra minions");
        add("enchantment.terra_entity.whip_sweep", "Whip Sweep");
        add("enchantment.terra_entity.whip_sweep.desc", "Chance to deal a wide area of damage");


        // npc
        add("dialogs.terra_entity.trade", "Trade");
        add("dialogs.terra_entity.summon", "Summon");
        add("dialogs.terra_entity.dialog", "Dialog");
        add("dialogs.terra_entity.quest", "Quest");

        add("dialogs.terra_entity.guide.0", "My job is to offer suggestions for your upcoming tasks. I recommend that you come and talk to me whenever you encounter any difficulties.");
        add("dialogs.terra_entity.guide.1", "They said there would be someone to tell you how to survive in this place... Oh, wait a moment. That person is me.");
        add("dialogs.terra_entity.guide.2", "You should stay at home at night. It's very dangerous to wander outside in the dark.");
        add("dialogs.terra_entity.guide.3", "In the Confluence world, you will obtain multiple times the treasure, but this also means taking on multiple times the risk.");
        add("dialogs.terra_entity.guide.4", "As far as I know, there are more humans in this world than in our original world.");
        add("dialogs.terra_entity.guide.5", "Sorry, sometimes I have to open the door.");
        add("dialogs.terra_entity.guide.6", "Those guys that can explode are more threatening than the average surface monsters!");
        add("dialogs.terra_entity.guide.7", "The life mushrooms on the grass can sometimes save your life.");
        add("dialogs.terra_entity.guide.8", "There are Crystal Hearts underground, which can be used to increase your maximum health. You can use a pickaxe to break them.");
        add("dialogs.terra_entity.guide.9", "There is a lake with magical powers underground, and it's very rare.");
        add("dialogs.terra_entity.guide.10", "At night, stars are falling and spreading all over the world. They have extremely wide uses. If you see them, you must get them, because the stars will disappear after sunrise.");
        add("dialogs.terra_entity.guide.11", "No matter what is spreading wildly, you will realize that it's time to stop them.");
        add("dialogs.terra_entity.guide.12", "If you want to survive, you need to make weapons and build a house. First, cut down trees and collect wood.");
        add("dialogs.terra_entity.guide.13", "After you have a sword, you can try to collect some gel from slimes. Use wooden sticks and gel to make torches!");
        add("dialogs.terra_entity.guide.14", "If you have some ores, you need to smelt them into ingots before you can use them to make items. This requires a furnace!");
        add("dialogs.terra_entity.guide.15", "If you combine lenses on the altar, you may be able to find a way to summon a powerful monster. However, it's better to use it at night.");

        add("dialogs.terra_entity.nurse.0", "I need to have a serious talk with the Guide. How many times a week do you get severely burned by lava exactly?");
        add("dialogs.terra_entity.nurse.1", "See that old man wandering around the dungeon? He looks like he's in trouble.");
        add("dialogs.terra_entity.nurse.2", "Hey, has the Arms Dealer ever mentioned going to see a doctor or something? Just asking.");
        add("dialogs.terra_entity.nurse.3", "Got into trouble with the thugs again?");
        add("dialogs.terra_entity.nurse.4", "Don't be such a child! I've seen worse.");
        add("dialogs.terra_entity.nurse.5", "Did it hurt when you did that? Don't do that.");

        add("dialogs.terra_entity.demolitionist.0", "Explosives are really popular nowadays. Buy some right away!");
        add("dialogs.terra_entity.demolitionist.1", "Today is a great day to court death!");
        add("dialogs.terra_entity.demolitionist.2", "Let me see what happens if I do this... (BOOM!)... Oh, sorry, did you still need that leg?");
        add("dialogs.terra_entity.demolitionist.3", "Take a look at my goods; they're all at amazing prices.");
        add("dialogs.terra_entity.demolitionist.4", "Dynamite, this is my special panacea prepared just for you. It can cure all kinds of problems.");
        add("dialogs.terra_entity.demolitionist.5", "Want to get through those evil stones, huh? Why not just blow them up with explosives!");

        add("dialogs.terra_entity.goblin_tinkerer.0", "Goblins get angry so easily. In fact, they can start a war over some rags!");
        add("dialogs.terra_entity.goblin_tinkerer.1", "To be honest, most goblins aren't real rocket scientists. Well, some of them are.");
        add("dialogs.terra_entity.goblin_tinkerer.2", "Do you know why everyone carries these spiky balls around? Because I don't.");
        add("dialogs.terra_entity.goblin_tinkerer.3", "I've just finished my latest creation! This version won't explode violently even if you blow or suck on it really hard.");
        add("dialogs.terra_entity.goblin_tinkerer.4", "Goblin thieves aren't very good at stealing. They can't even steal from an unlocked chest!");
        add("dialogs.terra_entity.goblin_tinkerer.5", "Yo, I heard you like rockets and running shoes, so I added some rockets to your running shoes.");

        add("dialogs.terra_entity.arms_dealer.0", "Dude, get your hands off my gun!");
        add("dialogs.terra_entity.arms_dealer.1", "Hey, bro, this isn't a movie. You need to prepare ammunition separately.");
        add("dialogs.terra_entity.arms_dealer.2", "I see you're eyeing the Minishark... You can't even imagine how it's made.");
        add("dialogs.terra_entity.arms_dealer.3", "I want to buy something from the Nurse. What did you say? She doesn't sell anything?");
        add("dialogs.terra_entity.arms_dealer.4", "Flying Fish? I call it target practice!");
        add("dialogs.terra_entity.arms_dealer.5", "Don't waste your time with the Demolitionist. I've got everything you need right here.");

        add("dialogs.terra_entity.merchant.0", "Swords beat paper! Buy one right away.");
        add("dialogs.terra_entity.merchant.1", "Do you want apples? Do you want carrots? Do you want pineapples? All we have are torches.");
        add("dialogs.terra_entity.merchant.2", "Take a look at my dirt blocks; they're really earthy.");
        add("dialogs.terra_entity.merchant.3", "You have no idea how much dirt blocks can sell for in other places.");
        add("dialogs.terra_entity.merchant.4", "One day they will tell your legend... It's sure to be a good story.");
        add("dialogs.terra_entity.merchant.5", "Kosh, kapleck Mog. Oh, sorry, that's Klingon, which means 'Buy or die.'");

        add("dialogs.terra_entity.painter.0", "I know the difference between turquoise and teal. But I'm not going to tell you.");
        add("dialogs.terra_entity.painter.1", "The titanium white is all used up. Don't ask.");
        add("dialogs.terra_entity.painter.2", "Try mixing pink and purple. It'll definitely work, I swear!");
        add("dialogs.terra_entity.painter.3", "No, no, no... There are many kinds of gray! Don't make me start...");
        add("dialogs.terra_entity.painter.4", "I hope it stops raining. The paint still hasn't dried. It would be a disaster if it rains!");
        add("dialogs.terra_entity.painter.5", "I tried organizing a paintball war, but everyone just wanted food and decorations.");

        add("dialogs.terra_entity.dryad.0", "Stay safe! Both worlds need you!");
        add("dialogs.terra_entity.dryad.1", "The hourglass of time is slowly running out. And you're not aging gracefully.");
        add("dialogs.terra_entity.dryad.2", "Two goblins walked into a bar, and one of them said to the other: 'A glass of beer?'");
        add("dialogs.terra_entity.dryad.3", "What does it mean by saying I'm all talk and no action?");
        add("dialogs.terra_entity.dryad.4", "You must stop the spread of evil.");
        add("dialogs.terra_entity.dryad.5", "This world is much vaster... And the power of nature is stronger too.");

        add("dialogs.terra_entity.dye_trader.0", "I bring you the richest colors in exchange for your wealth.");
        add("dialogs.terra_entity.dye_trader.1", "Honey, your clothes are so monotonous. You really have to learn how to dye your dull clothes!");
        add("dialogs.terra_entity.dye_trader.2", "The only wood I'm willing to dye is mahogany. Dyeing any other wood is a waste.");
        add("dialogs.terra_entity.dye_trader.3", "Oh, no, no, that won't do. Even if you have money, you have to trade me with rare plant samples!");
        add("dialogs.terra_entity.dye_trader.4", "These dye bottles? Sorry, my dear friend, these are not for sale. I only accept the rarest plants in exchange for them!");
        add("dialogs.terra_entity.dye_trader.5", "You think you can fool my eyes? I don't think so! I only accept the rarest flowers in exchange for these special bottles.");

        add("dialogs.terra_entity.angler.0", "I'm bummed out! There's probably been fish that have gone extinct before I even was born, and that's not fair!");
        add("dialogs.terra_entity.angler.1", "Whaaaat?! Can't you see I'm winding up fishing line??");
        add("dialogs.terra_entity.angler.2", "There's no chefs in all of %s, so I have to cook all this fish myself! ");
        add("dialogs.terra_entity.angler.3", "I don't have a mommy or a daddy, but I have a lot of fish! It's close enough!");
        add("dialogs.terra_entity.angler.4", "Let a kid give you some advice, never touch your tongue to an ice block! Wait, forget what I said, I totally want to see you do it!");
        add("dialogs.terra_entity.angler.5", "Ever heard of a barking fish?! I haven't, I'm just wondering if you did!");
        add("dialogs.terra_entity.angler.6", "Hey! Watch it! I'm setting up traps for my biggest prank ever! No one will see it coming! Don't you dare tell anyone!");
        add("dialogs.terra_entity.angler.7", "%s is filled to the brim with the most outlandish kinds of fish!");
        add("dialogs.terra_entity.angler.stat.0", "Did you know I have %s amazingly awesome fish now!? That's because as far as errand monkeys go, you're actually a bit useful!");
        add("dialogs.terra_entity.angler.stat.1", "Wooow! You've bothered me, like, %s times! If I didn't have a cool fish for each time, I would be really mad!");
        add("dialogs.terra_entity.angler.wakeup.0", "Thanks, I guess, for saving me or whatever. You'd be a great helper minion!");
        add("dialogs.terra_entity.angler.wakeup.1", "Wha? Who might you be? I totally wasn't just drowning or anything!");
        add("dialogs.terra_entity.angler.wakeup.2", "You saved me! You're awful nice, I could use you... er, I mean, totally hire you to do some awesome stuff for me!");
        add("dialogs.terra_entity.angler.task_ready.0", "Psst! I might have a job for you. Don't think you can say no, either!");
        add("dialogs.terra_entity.angler.task_ready.1", "I want a fish and you're going to find me one! Ask me about it!");
        add("dialogs.terra_entity.angler.task_ready.2", "%1$s wants YOU as the official %2$s errand monkey!");
        add("dialogs.terra_entity.angler.task_ready.3", "Hey! Just the sacrifi- I mean competent fishing master that I've been looking for! ");
        add("dialogs.terra_entity.angler.task_succeed.0", "Oh! Thanks for the fish I asked for, now scram!");
        add("dialogs.terra_entity.angler.task_succeed.1", "You make a great errand monkey! Now go away!");
        add("dialogs.terra_entity.angler.task_succeed.2", "Muahahahahaha! You did it! You're still in one piece though, how boring!");
        add("dialogs.terra_entity.angler.task_succeed.3", "Woah!? You actually did what I asked, and survived! Nice, hand it over and beat it!");
        add("dialogs.terra_entity.angler.task_succeed.4", "Awesome catch! It's all going according to plan! He he he!");
        add("dialogs.terra_entity.angler.task_finished.0", "I have enough fish! I don't need your help right now!");
        add("dialogs.terra_entity.angler.task_finished.1", "You have entertained me enough for today, go.");
        add("dialogs.terra_entity.angler.task_finished.2", "I don't have anything for you to do right now.");
        add("dialogs.terra_entity.angler.task_finished.3", "一Only one fish a day, please go away!");
        add("dialogs.terra_entity.angler.task_finished.4", "I haven't even used the last fish you gave me.  I don't need another.");
        add("dialogs.terra_entity.angler.task_finished.5", "You are done, the grand %s dismisses you!");

        add("dialogs.terra_entity.female_angler.0", "What a pity! Some fish may have disappeared even before I was born. I hope we can protect the remaining ones well~");
        add("dialogs.terra_entity.female_angler.1", "Ah... I'm so sorry! Did I disturb you while reeling in my line? I really apologize!");
        add("dialogs.terra_entity.female_angler.2", "Although there's no chef in the whole %s, being able to cook fish with my own hands is a kind of happiness!");
        add("dialogs.terra_entity.female_angler.3", "I don't have a mom or dad, but I have so many lovely fish to keep me company! I'm already very content~");
        add("dialogs.terra_entity.female_angler.4", "Take my advice, sweetie~ Never touch ice cubes with your tongue! You'll get frostbite, so make sure to take good care of yourself!");
        add("dialogs.terra_entity.female_angler.5", "Have you ever heard of fish that can make sounds? I think I've seen some before! Would you like to talk about it with me?");
        add("dialogs.terra_entity.female_angler.6", "Oh dear! Be careful~ I set up some little decorations, not traps! I just wanted to give everyone a small surprise, please don't misunderstand~");
        add("dialogs.terra_entity.female_angler.7", "The %s is full of lovely and unique fish everywhere! It's truly wonderful!");
        add("dialogs.terra_entity.female_angler.stat.0", "You know what? I've already collected %s beautiful fish! Thank you so much—I could never have done it without your help!");
        add("dialogs.terra_entity.female_angler.stat.1", "Oh no! I've bothered you %s times already, I'm so terribly sorry! But every time I get to see such beautiful fish, it makes me so happy!");
        add("dialogs.terra_entity.female_angler.wakeup.0", "Thank you so much! I really don't know how to repay you—you're an amazing friend!");
        add("dialogs.terra_entity.female_angler.wakeup.1", "Oh! It's you~ I just slipped a moment ago, I wasn't drowning! Thank you for caring so much！");
        add("dialogs.terra_entity.female_angler.wakeup.2", "Thank you for saving me! You're such a kind person~ If you don't mind, I'd like to ask you to help me with a small errand—but it's totally fine if you don't want to!");
        add("dialogs.terra_entity.female_angler.task_ready.0", "Hello there~ I have a tiny favor to ask. Could you help me if you're free? It's absolutely fine if you're not!");
        add("dialogs.terra_entity.female_angler.task_ready.1", "I'm looking for a special kind of fish. It would mean the world to me if you'd help find it! No rush at all—let's take our time to go over the details~");
        add("dialogs.terra_entity.female_angler.task_ready.2", "%1$s would like to formally invite you to become %2$s's fishing helper! Would you accept?");
        add("dialogs.terra_entity.female_angler.task_ready.3", "Hello! You must be the amazing fishing master I've been looking for! Could I ask for your help, please?");
        add("dialogs.terra_entity.female_angler.task_succeed.0", "Wow! Thank you for catching the fish I wanted—thank you so much! You must be tired, take it easy~");
        add("dialogs.terra_entity.female_angler.task_succeed.1", "You did an absolutely wonderful job! You've worked hard—go rest for a bit~");
        add("dialogs.terra_entity.female_angler.task_succeed.2", "I'm so happy! You finished safely, that's such a relief! I was worried about you the whole time!");
        add("dialogs.terra_entity.female_angler.task_succeed.3", "Oh my goodness! You not only completed the task but also came back safe and sound—you're incredible! Please hand me the fish, and go get some rest~");
        add("dialogs.terra_entity.female_angler.task_succeed.4", "We got it! Everything went perfectly~ Thank you for your help, I really appreciate it!");
        add("dialogs.terra_entity.female_angler.task_finished.0", "I have enough fish now! Thank you so much for all your help—you've done so much for me~");
        add("dialogs.terra_entity.female_angler.task_finished.1", "Thank you so much for today! I had such a lovely time with you! Go take care of your own things now~");
        add("dialogs.terra_entity.female_angler.task_finished.2", "I don't have any errands for you right now, but thank you so much for stopping by anyway~");
        add("dialogs.terra_entity.female_angler.task_finished.3", "I have enough fish for today—you've worked hard! Take care, and goodbye for now~");
        add("dialogs.terra_entity.female_angler.task_finished.4", "I still have the fish you gave me last time safely stored away. I don't need more for now, but thank you so much all the same!");
        add("dialogs.terra_entity.female_angler.task_finished.5", "Thank you so much! Having a helper like you is such a blessing to %s~");

        add("dialogs.terra_entity.old_man.0", "I cannot let you enter until you free me of my curse.");
        add("dialogs.terra_entity.old_man.1", "Stranger, do you possess the strength to defeat my master?");
        add("dialogs.terra_entity.old_man.2", "Defeat my master, and I will grant you passage into the Dungeon.。");
        add("dialogs.terra_entity.old_man.3", "Come back at night if you wish to enter.");

        add("dialogs.terra_entity.traveling_merchant.0", "Hmm, you look like you could use an Angel Statue! They slice, and dice, and make everything nice!");
        add("dialogs.terra_entity.traveling_merchant.1", "I don't refund for \"buyer's remorse...\" Or for any other reason, really.");
        add("dialogs.terra_entity.traveling_merchant.2", "Buy now and get free shipping!");
        add("dialogs.terra_entity.traveling_merchant.3", "I sell wares from places that might not even exist!");
        add("dialogs.terra_entity.traveling_merchant.4", "You want two penny farthings!? Make it one and we have a deal.");
        add("dialogs.terra_entity.traveling_merchant.5", "Combination hookah and coffee maker! Also makes julienne fries!");
        add("dialogs.terra_entity.traveling_merchant.6", "Come and have a look! One pound fish! Very, very good! One pound fish!");
        add("dialogs.terra_entity.traveling_merchant.7", "If you're looking for junk, you've come to the wrong place.");
        add("dialogs.terra_entity.traveling_merchant.8", "A thrift shop?  No, I am only selling the highest quality items on the market.");

        add("dialogs.terra_entity.mechanic.0", "Did you make sure your device was plugged in?");
        add("dialogs.terra_entity.mechanic.1", "Oh, you know what this house needs? More blinking lights.");
        add("dialogs.terra_entity.mechanic.2", "DON'T MOVE. I DROPPED MY CONTACT.");
        add("dialogs.terra_entity.mechanic.3", "Thank you! Sooner or later, I'll end up like the other skeletons in the dungeon.");
        add("dialogs.terra_entity.mechanic.4", "I don't quite remember what happened in there. Three, maybe four important things...");
        add("dialogs.terra_entity.mechanic.5", "Oh yes, the Signal Adapter! It can connect the redstone here to the wires perfectly.");

        add("dialogs.terra_entity.witch_doctor.0", "Which doctor am I? The Witch Doctor am I.");
        add("dialogs.terra_entity.witch_doctor.1", "Choose wisely, my commodities are volatile and my dark arts, mysterious.");
        add("dialogs.terra_entity.witch_doctor.2", "The heart of magic is nature. The nature of hearts is magic.");
        add("dialogs.terra_entity.witch_doctor.3", "I sense a kindred spirit in the Etherian Dark Mages. A pity they are our enemies, I would have liked to learn from them.");


        add("dialogs.terra_entity.clothier.0", "Thanks again for freeing me from my curse. Felt like something jumped up and bit me.");
        add("dialogs.terra_entity.clothier.1", "Mama always said I would make a great tailor.");
        add("dialogs.terra_entity.clothier.2", "Life's like a box of clothes; you never know what you are gonna wear!");
        add("dialogs.terra_entity.clothier.3", "Of course embroidery is hard! If it wasn't hard, no one would do it! That's what makes it great.");
        add("dialogs.terra_entity.clothier.4", "I know everything they is to know about the clothierin' business.");
        add("dialogs.terra_entity.clothier.5", "Being cursed was lonely, so I once made a friend out of leather. I named him Wilson.");
        add("dialogs.terra_entity.clothier.6", "I keep having vague memories of tying up a woman and throwing her in a dungeon.");

        add("dialogs.terra_entity.zoologist.0", "I collected critters like you once, then I took a cursed fox bite to the knee!");
        add("dialogs.terra_entity.zoologist.1", "I may not know, like, a whole lot...but I can talk your head off about nature and critters and animals and wildlife and...");
        add("dialogs.terra_entity.zoologist.2", "My older bro calls me a lycanthrope. It means I'm like, part animal or something. He'd know, though, because he spends all his time outside!");
        add("dialogs.terra_entity.zoologist.3", "I love animals, like, a lot! I tried to pet this weird looking fox one time, he sooo bit me, and now I became like one! Rad!");
        add("dialogs.terra_entity.zoologist.4", "Staahp pulling on my tail, bro, it's totally real, and like...totally hurts when you pull on it!");
        add("dialogs.terra_entity.zoologist.5", "Oh, THESE ears? Haha, totes better to hear you with, my dear!");
        add("dialogs.terra_entity.zoologist.6", "This one time, at critter camp, I woke up one morning and everything was torn apart! Like, wow, how did I sleep through THAT?!");
        add("dialogs.terra_entity.zoologist.7", "Wow, like, I've never seen a full moon. For some reason, it's like I pass out every time one's around!");
        add("dialogs.terra_entity.zoologist.8", "I have noooo idea how I got here, but it's mega rad.");

        add("dialogs.terra_entity.party_girl.0", "We have to talk. It's... it's about parties.");
        add("dialogs.terra_entity.party_girl.1", "I can't decide what I like more: parties, or after-parties.");
        add("dialogs.terra_entity.party_girl.2", "We should set up a blinkroot party, and we should also set up an after-party.");
        add("dialogs.terra_entity.party_girl.3", "Put up a disco ball and then I'll show you how to party.");
        add("dialogs.terra_entity.party_girl.4", "I went to Sweden once, they party hard, why aren't you like that?");
        add("dialogs.terra_entity.party_girl.5", "My name's Party Girl but people call me party pooper. Yeah I don't know, it sounds cool though.");
        add("dialogs.terra_entity.party_girl.6", "Do you party? Sometimes? Hm, okay then we can talk...");

        add("dialogs.terra_entity.truffle.0", "As if living underground wasn't bad enough, jerks like you come in while I'm sleeping and steal my children.");
        add("dialogs.terra_entity.truffle.1", "I tried to lick myself the other day to see what the big deal was, everything started glowing blue.");
        add("dialogs.terra_entity.truffle.2", "Everytime I see the color blue, it makes me depressed and lazy.");
        add("dialogs.terra_entity.truffle.3", "You haven't seen any pigs around here have you? My brother lost his leg to one.");
        add("dialogs.terra_entity.truffle.4", "I don't know the 'Truffle Shuffle,' so stop asking!");
        add("dialogs.terra_entity.truffle.5", "There's been such a huge rumor that's being spread about me, 'If you can't beat him, eat him!'");
        add("dialogs.terra_entity.truffle.6", "I feel there are more of my kind here...");

        add("mood.terra_entity.goblin_tinkerer.like.dye_trader", "Dye Trader understands how fun it is to mix things together, I can respect that!");
        add("mood.terra_entity.goblin_tinkerer.love.mechanic", "Mechanic makes my cardiac core function improperly, it appears I love how that feels!");
        add("mood.terra_entity.goblin_tinkerer.dislike.clothier", "I detect eerie vibes from <name of Clothier>, as if they contain dark secrets. I don't like the feeling.");
        add("mood.terra_entity.guide.hate.painter", "I hate that Painter is around. The world is fine the way it was made!");
        add("mood.terra_entity.guide.like.clothier", "I'm quite fond of Clothier, we have a lot in common.");
        add("mood.terra_entity.guide.like.zoologist", "I'm quite fond of Zoologist, we have a lot in common.");
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
        add("mood.terra_entity.nurse.hate.zoologist", "Oh, I hate treating Zoologist , so difficult!");
        add("mood.terra_entity.truffle.love.guide", "I love Guide for being able to talk to me without mysteriously getting hungry.");
        add("mood.terra_entity.truffle.like.dye_trader", " Dryad treats me with respect, as though I'm a true part of nature. I don't know how to feel about that, except I like it.");
        add("mood.terra_entity.truffle.dislike.clothier", " Clothier has tried to eat me so many times. I swear, one time they weren't even human! I, obviously, dislike it.");
        add("mood.terra_entity.truffle.hate.witch_doctor", "Witch Doctor has tried to throw me into a pot filled with other unusual ingredients. I hate that.");
        add("mood.terra_entity.clothier.love.truffle", "Truffle? I hadn't seen anything so delicious in my life.");
        add("mood.terra_entity.clothier.dislike.nurse", "For some reason, being around Nurse makes me feel uneasy.");
        add("mood.terra_entity.clothier.hate.mechanic", "I hate Mechanic and I don't know why.");
        add("mood.terra_entity.party_girl.dislike.merchant", "I think Merchant is a killjoy at parties.");
        add("mood.terra_entity.party_girl.love.zoologist", "I love that Zoologist always dazzles at my parties.");
        add("mood.terra_entity.witch_doctor.like.dryad", "the Dryad is a kindred spirit of nature, my soul is at peace in their presence.");
        add("mood.terra_entity.witch_doctor.like.guide", "the Guide is a kindred spirit of nature, my soul is at peace in their presence.");
        add("mood.terra_entity.witch_doctor.dislike.nurse", "I dislike the practices of the Nurse. True healing cannot come from metal and glass.");
        add("mood.terra_entity.witch_doctor.hate.truffle", "Fury fills my being as abominations sprout from tainted earth - I speak of the Truffle.");
        add("mood.terra_entity.mechanic.love.goblin_tinkerer", "Umm...Goblin Tinkerer makes my heart flutter, I need to get that checked!");
        add("mood.terra_entity.mechanic.dislike.arms_dealer", "I don't really like that Arms Dealer won't leave me alone!");
        add("mood.terra_entity.mechanic.hate.clothier", "I hate how Clothier doesn't know how to treat a woman!");
        add("mood.terra_entity.zoologist.love.witch_doctor", "I can't explain it. I have like, a thing for Witch Doctor. Is it because of the tail?");
        add("mood.terra_entity.zoologist.dislike.angler", "I don't like how cruel Angler is!");
        add("mood.terra_entity.zoologist.like.female_angler", "She didn't really hurt them.");
        add("mood.terra_entity.zoologist.hate.arms_dealer", "I really totally hate what Arms Dealer does to animals!");
        // sound
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
        add("terra_entity.subtitle.summon_money_trough", "Money Trough: Summon");
        add("terra_entity.subtitle.antlion_death", "Antlion: Death");
        add("terra_entity.subtitle.antlion_hurt", "Antlion: Hurt");
        add("terra_entity.subtitle.antlion_free", "Antlion: Movement");
        add("terra_entity.subtitle.antlion_swarmer_death", "Antlion Swarmer: Death");
        add("terra_entity.subtitle.antlion_swarmer_free", "Antlion Swarmer: Movement");
        add("terra_entity.subtitle.bat_death", "Bat: Death");
        add("terra_entity.subtitle.beetle_death", "Cochineal Beetle: Death");
        add("terra_entity.subtitle.blood_jelly_death", "Blood Jelly: Death");
        add("terra_entity.subtitle.blood_jelly_free", "Blood Jelly: Floating");
        add("terra_entity.subtitle.bone_serpent_death", "Bone Serpent: Death");
        add("terra_entity.subtitle.demon_death", "Demon: Death");
        add("terra_entity.subtitle.demon_free", "Demon: Roar");
        add("terra_entity.subtitle.demon_hurt", "Demon: Hurt");
        add("terra_entity.subtitle.dungeon_spirit_death", "Dungeon Spirit: Dissipate");
        add("terra_entity.subtitle.dungeon_spirit_free", "Dungeon Spirit: Drifting");
        add("terra_entity.subtitle.dungeon_spirit_hurt", "Dungeon Spirit: Hit");
        add("terra_entity.subtitle.granite_golem_death", "Granite Golem: Collapse");
        add("terra_entity.subtitle.granite_golem_hurt", "Granite Golem: Damaged");
        add("terra_entity.subtitle.granite_golem_free", "Granite Golem: Walking");
        add("terra_entity.subtitle.jellyfish_death", "Jellyfish: Burst");
        add("terra_entity.subtitle.jellyfish_free", "Jellyfish: Swimming");
        add("terra_entity.subtitle.jellyfish_hurt", "Jellyfish: Agitated");
        add("terra_entity.subtitle.pixie_death", "Pixie: Vanish");
        add("terra_entity.subtitle.pixie_free", "Pixie: Fluttering");
        add("terra_entity.subtitle.pixie_hurt", "Pixie: Startled");
        add("terra_entity.subtitle.sand_shoot", "Sand: Shoot");
        add("terra_entity.subtitle.soul_death", "Spirit: Annihilate");
        add("terra_entity.subtitle.tr_zombie_free", "Zombie: Growl");
        add("terra_entity.subtitle.unicorn_death", "Unicorn: Whimper");
        add("terra_entity.subtitle.unicorn_hurt", "Unicorn: Cry");
        add("terra_entity.subtitle.wyvern_death", "Wyvern: Crash");
        add("terra_entity.subtitle.wyvern_hurt", "Wyvern: Roar");
        add("terra_entity.subtitle.the_hungry_death", "Hungry Ghost: Perish");
        add("terra_entity.subtitle.the_hungry_hurt", "Hungry Ghost: Hit");
        add("terra_entity.subtitle.wall_of_flesh_hurt", "Flesh Wall: Injured");
        add("terra_entity.subtitle.wall_of_flesh_roar", "Flesh Wall: Roar");
        add("terra_entity.subtitle.wall_of_flesh_summon", "Flesh Wall: Vomit");
    }
}
