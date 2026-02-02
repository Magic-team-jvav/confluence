package org.confluence.mod.common.data.gen.language;

import java.util.function.BiConsumer;

public class PatchouliLanguageSubProvider implements LanguageSubProvider {
    private final BiConsumer<String, String> consumer;

    public PatchouliLanguageSubProvider(BiConsumer<String, String> consumer, boolean isEn) {
        this.consumer = consumer;
        addTranslations(isEn);
    }

    @Override
    public void english() {
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
    }

    @Override
    public void chinese() {

        // 帕秋莉手册
        add("patchouli.confluence.otherworld_note.name", "来世手记");
        add("patchouli.confluence.otherworld_note.landing_text", "暗流涌动，世界的命运岌岌可危，邪恶势力的阴影笼罩着两个世界。命运的交汇之时，融合的时代悄然来临。你将在这两大世界的融合中，开始一段崭新的冒险之旅，挑战未知，拯救世界，或为自己谋求力量。");
        add("patchouli.confluence.otherworld_note.appearance", "外观");
        add("patchouli.confluence.otherworld_note.example", "一个例子");
        add("patchouli.confluence.otherworld_note.gui", "GUI界面");
        add("patchouli.confluence.otherworld_note.how_to_use", "使用方式");
        // 章节
        add("patchouli.confluence.otherworld_note.accessories.name", "饰品");
        add("patchouli.confluence.otherworld_note.accessories.description", "两只手 两只脚 21个戒指🤔");
        add("patchouli.confluence.otherworld_note.boss_checklist.name", "Boss 列表");
        add("patchouli.confluence.otherworld_note.boss_checklist.description", "不止比苦力怕可怕。");
        add("patchouli.confluence.otherworld_note.crafting_stations.name", "制作站");
        add("patchouli.confluence.otherworld_note.crafting_stations.description", "合成台...工作台...制作台...");
        add("patchouli.confluence.otherworld_note.fishing.name", "钓鱼");
        add("patchouli.confluence.otherworld_note.fishing.description", "钓鱼佬永不空军！");
        add("patchouli.confluence.otherworld_note.food.name", "食物");
        add("patchouli.confluence.otherworld_note.food.description", "这是什么？吃一口。");
        add("patchouli.confluence.otherworld_note.mana.name", "魔力");
        add("patchouli.confluence.otherworld_note.mana.description", "你是一个巫师，哈利。");
        add("patchouli.confluence.otherworld_note.material.name", "材料");
        add("patchouli.confluence.otherworld_note.material.description", "恭喜发材！");
        add("patchouli.confluence.otherworld_note.mob.name", "生物");
        add("patchouli.confluence.otherworld_note.mob.description", "史莱姆是动物还是植物？");
        add("patchouli.confluence.otherworld_note.tool.name", "工具");
        add("patchouli.confluence.otherworld_note.tool.description", "这不是铁镐么！");
        add("patchouli.confluence.otherworld_note.weapon.name", "武器");
        add("patchouli.confluence.otherworld_note.weapon.description", "吃我一剑！");
        add("patchouli.confluence.otherworld_note.world.name", "世界");
        add("patchouli.confluence.otherworld_note.world.description", "正在生成世界中...*i正在隐藏宝藏...*i正在隐藏更多宝藏...*i正在隐藏更多更多宝藏...");

        // 条目
        // 饰品
        // Boss 列表
        add("patchouli.confluence.otherworld_note.boss_checklist.how_to_summon", "召唤方式");

        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.name", "克苏鲁之脑");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.0", "克苏鲁之脑看上去就是个裸露的大脑，不过真的非常大");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.1", "*z$(#AA0000)“出没在让人毛骨悚然的猩红之地的巨大恶魔大脑。”*c*w");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.2", "在克苏鲁之脑的防御解除之前无法攻击它，需要杀死所有视神经元才能开始攻击。");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.2.0", "视神经元");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.3", "单片镜爱好者。");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.4", "当视神经元被消灭完，克苏鲁之脑就会裸露出它的心脏并召唤出 3 个无法攻击的克苏鲁之脑幻象。");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.4.0", "克苏鲁之脑幻象");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.5", "*o“假作真时真亦假，无为有处有还无。”*b*t——摘自《红楼梦》*c");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.6", "在!w:cr!猩红之地*c使用血腥脊椎，或者摧毁 3 个生成于血腥世界中的猩红心脏来召唤克苏鲁之脑。*b需要在晚上才能召唤。");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.7", "血腥脊椎是一个用于召唤Boss的物品，看起来像是个被剥离的脊椎。*b它可以用来在猩红之地立刻召唤克苏鲁之脑。*2在!cs:a!祭坛*c中使用 30 个毒粉和 15 个椎骨合成血腥脊椎。");
        add("patchouli.confluence.otherworld_note.boss_checklist.brain_of_cthulhu.8", "*o$(#555555)“一截被剥离下来了的身体组织，上面斑驳着血痂块与肌肉组织，很难想象它是从哪种生物体内，以何种方式取出来的。它似乎在尝试着与你对话...*c");

        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.name", "世界吞噬怪");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.0", "世界吞噬怪是一只体型巨大的...蠕虫？看上去就挺像是地底蠕虫的腐化版。");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.1", "*z$(#AA00AA)“居住在腐化之地的巨虫。”*c*w");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.2", "当任何一段体节被击杀时，它会分裂！只有当所有体节被击杀才算真正击败世界吞噬怪。");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.2.0", "世界吞噬怪体节");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.3", "节节高升（字面意思）。");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.4", "在!w:co!腐化之地*c使用蠕虫诱饵，或者摧毁 3 个生成于腐化世界中的暗影珠来召唤世界吞噬怪。*b可以在任意时间召唤。");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.5", "蠕虫诱饵是一个用于召唤Boss的物品，看起来像是一块腐烂的肉。*b它可以用来在腐化之地立刻召唤世界吞噬怪。*2在!cs:a!祭坛*c中使用 30 个魔粉和 15 个腐肉合成蠕虫诱饵。");
        add("patchouli.confluence.otherworld_note.boss_checklist.eater_of_worlds.6", "*o$(#555555)“闻起来像一块烂掉了的排骨，似乎对那些病变了的泰拉生物有极强的吸引力。*c");

        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.name", "克苏鲁之眼");
        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.0", "作为一只巨大的眼睛，克苏鲁之眼又快又狠（不太准）。*b当克苏鲁之眼血量低于一定数值，它会将其眼睛变为嘴发出吼叫声并进入狂暴。*b在狂暴时，克苏鲁之眼只会疯狂的向你冲刺。*2此时是真的快准狠了。");
        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.1", "*z$(#9A5CC6)“只在夜间出没的危险眼球怪。”*c*w");
        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.2", "在任何地方使用可疑眼球来召唤克苏鲁之眼。*b有时克苏鲁之眼会在晚上出现：$(#AA0000)“你感到有个邪恶的东西在看着你……”*c*2需要在晚上才能召唤。");
        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.3", "可疑眼球是一个用于召唤Boss的物品，看起来像是个被剥离的眼球。*b它可以用来在任何地点立刻召唤克苏鲁之眼。*2在!cs:a!祭坛*c中使用 6 个晶状体合成可疑眼球。");
        add("patchouli.confluence.otherworld_note.boss_checklist.eye_of_cthulhu.4", "*o$(#555555)“一颗死气沉沉，目光呆滞的眼球，尽管它不具备攻击性，但它似乎比那些夜间会在半空中飞来飞去的同僚们更为危险。它好像在看着你。”");

        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.name", "史莱姆王");
        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.0", "史莱姆王作为一只超大史莱姆，它不止比普通史莱姆跳的更高更远，当你离它过远时它还会瞬移！*2它还有顶巨大的王冠！");
        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.1", "*z$(#555555)“所有黏滑生物的首领。”*c*w");
        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.2", "在任何地方使用史莱姆皇冠，或者在史莱姆雨中击杀 150 只史莱姆来召唤史莱姆王。*2可以在任意时间召唤。");
        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.3", "史莱姆皇冠是一个用于召唤Boss的物品，看起来像是个戴着王冠的小蓝色史莱姆。*b它可以用来在任何地点立刻召唤史莱姆王。*2在!cs:a!祭坛*c中使用 20 个!ma:g!凝胶*c和金冠/铂金冠合成史莱姆皇冠。");
        add("patchouli.confluence.otherworld_note.boss_checklist.king_slime.4", "*o$(#555555)“一只小巧的王冠，看上去是为那些人畜无害的可爱凝胶生物的加冕仪式所准备的。戴上它可能不是个好选择。”*c");

        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.name", "蜂王");
        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.0", "字面意思，它是蜜蜂们的女王，所以当它开始抖动它的体节时会召唤大群的小蜜蜂来攻击你。*b它底下的蜂巢当然不是装饰，可是货真价实会发射毒针的！*b小心蜂王偶尔会朝你俯冲！*2*o绝对不要把蜂王带出丛林，当蜂王的眼睛变红时请尽快结束战斗或者……*c*l$(#FF0000)跑！");
        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.1", "*z$(#FFAA00)“统治丛林蜂巢的女王。”*c*w");
        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.2", "在丛林使用憎恶之蜂，或者在蜂巢破坏幼虫来召唤蜂王。*2可以在任意时间召唤。");
        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.3", "憎恶之蜂是一个用于召唤Boss的物品，看起来像是个蜜蜂。*b它可以用来在丛林中立刻召唤蜂王。*2在!cs:hwb!重型工作台*c中使用 5 个蜂蜜块、5 个丛林蜂巢、毒刺和蜂蜜瓶合成憎恶之蜂。");
        add("patchouli.confluence.otherworld_note.boss_checklist.queen_bee.4", "*o$(#555555)“她似乎嫉妒厌恶那些荧光真菌孢子的气息。一团尚未成型的幼蜂，摸上去感觉像粘稠的蜂蜜...蜂王与她的下属们对它们甜蜜领地的保护欲不知何时渐渐发展为对非同类生物的嫉妒排斥与憎恶。群蜂振翅值声撼动了丛林中最为厚重的叶片。”");

        // 制作站
        add("patchouli.confluence.otherworld_note.crafting_stations.alchemy_table.0", "炼药桌差不多就是酿造台的Plus版，当然不能用于制作酿造台的配方，而是制作特殊的酿药配方。*b在!cs:hwb!重型工作台*c中使用木板、酿造台、骷髅头颅、蜡烛和玻璃瓶制作而成。");
        add("patchouli.confluence.otherworld_note.crafting_stations.alchemy_table.1", "左右 6 个格子放材料，中上塞水瓶（或者其他特殊的东西），然后下方就会有成品了。");
        add("patchouli.confluence.otherworld_note.crafting_stations.alchemy_table.2", "如果乱赛东西，你的合成可能会变得奇怪。");

        add("patchouli.confluence.otherworld_note.crafting_stations.altar.name", "祭坛");
        add("patchouli.confluence.otherworld_note.crafting_stations.altar.0", "祭坛是天然生成的制作站，主要出现在!w:co!腐化之地*c或!w:cr!猩红之地*c中的裂隙或周围，较少出现在地下。*b它们用于制作许多Boss的召唤物品和其他特殊物品。*b它们不能被拿走、拾取、放置、或制作。");
        add("patchouli.confluence.otherworld_note.crafting_stations.altar.1", "将物品放入祭坛，潜行右击将物品取出，空手状态下左击使用祭坛进行合成");
        add("patchouli.confluence.otherworld_note.crafting_stations.altar.2", "恶魔祭坛通常出现在腐化之地的地下，在世界各地的地下少量出现。");
        add("patchouli.confluence.otherworld_note.crafting_stations.altar.3", "猩红祭坛通常出现在腐化之地的地下，在世界各地的地下少量出现。");

        add("patchouli.confluence.otherworld_note.crafting_stations.extractinator.0", "提炼机是一种可以将砂砾、泥沙块、雪泥块、海洋砂砾和沙漠化石转换为更有价值的物品（例如矿石，钱币，和宝石），或将钓鱼垃圾转换为低级鱼饵的功能方块。*2*o垃圾回收站 3000。");
        add("patchouli.confluence.otherworld_note.crafting_stations.extractinator.1", "手持物品对着提炼机长按右键即可，手中的物品会以极快的速度被提炼，提炼的结果会以掉落物形式掉出。*2rua~狂~风~刀~法~！");
        add("patchouli.confluence.otherworld_note.crafting_stations.extractinator.2", "散落一地的物品。");
        add("patchouli.confluence.otherworld_note.crafting_stations.extractinator.3", "在木匣中可以获取提炼机。");

        add("patchouli.confluence.otherworld_note.crafting_stations.heavy_work_bench.0", "重型工作台有点像工作台的升级版，但是不能用于制作工作台的 3x3 配方，而是制作属于重型工作台的 4x4 配方。*b它可用于制作各种建筑方块、工具和装饰物品。");
        add("patchouli.confluence.otherworld_note.crafting_stations.heavy_work_bench.1", "其实它差不多就是普通的工作台，不过使用 4x4 的配方。");
        add("patchouli.confluence.otherworld_note.crafting_stations.heavy_work_bench.2", "如果配方相同，可以使用右侧的箭头切换成品。");

        add("patchouli.confluence.otherworld_note.crafting_stations.hellforge.0", "比高炉还耐烧（而且兼容高炉的配方！），就是说这是个高炉的升级版。*b在!w:nt!下界塔*c的某个地方会里有概率生成地狱熔炉。");
        add("patchouli.confluence.otherworld_note.crafting_stations.hellforge.1", "在中间 4 个地方输入材料，左下角放燃料，稍等片刻成品就会在右边出现了。");
        add("patchouli.confluence.otherworld_note.crafting_stations.hellforge.2", "不太符合熔炉和高炉的样子，是吧？但是也不是不好看。");

        add("patchouli.confluence.otherworld_note.crafting_stations.lead_anvil.0", "其实铅砧就是铁砧的换皮，功能和铁砧一模一样。*b似乎...比铁砧便宜？");
        add("patchouli.confluence.otherworld_note.crafting_stations.lead_anvil.1", "就是铁砧，那不然呢。");

        add("patchouli.confluence.otherworld_note.crafting_stations.sky_mill.0", "听起来看上去很厉害，但是实际上就是个造家具的。*b在!w:hi!天堂岛*c的箱子里有概率生成天磨。");
        add("patchouli.confluence.otherworld_note.crafting_stations.sky_mill.1", "就像切石机一样，即放即用。");
        add("patchouli.confluence.otherworld_note.crafting_stations.sky_mill.2", "下面三处放材料，右边选择配方，顶上就会有成品了。");

        add("patchouli.confluence.otherworld_note.crafting_stations.workshop.0", "工匠作坊可以用来制作许多的饰品，而且不需要按顺序放！*b在!cs:hwb!重型工作台*c中使用木板、红色羊毛、书、锻造台、铁砧或!cs:la!铅砧*c和皮革制作而成。*2一切都是哥布林商人的阴谋！");
        add("patchouli.confluence.otherworld_note.crafting_stations.workshop.1", "12 个材料格包围着中间的成品格即可合成。");
        add("patchouli.confluence.otherworld_note.crafting_stations.workshop.2", "如果配方相同，可以使用右侧的箭头切换成品。");

        // 钓鱼
        add("patchouli.confluence.otherworld_note.fishing.bait.name", "鱼饵");
        add("patchouli.confluence.otherworld_note.fishing.bait.0", "当然不是必须要鱼饵才能钓鱼，但是有鱼饵能钓上更好的战利品！");

        add("patchouli.confluence.otherworld_note.fishing.crates.name", "宝匣");
        add("patchouli.confluence.otherworld_note.fishing.crates.0", "宝匣是可堆叠的摸彩袋类型物品，其中包含的东西是随机的，包括药水、鱼饵、有用的物品、钱币、金属锭、甚至还有矿石。*b越是少见的宝匣类型内含的东西价值就越高。");

        // 食物
        add("patchouli.confluence.otherworld_note.food.cloud_bread.name", "云朵面包");
        add("patchouli.confluence.otherworld_note.food.cloud_bread.0", "云朵面团用于制作云朵面包的物品，使用 3 个飘飘麦合成云朵面团。");
        add("patchouli.confluence.otherworld_note.food.cloud_bread.1", "食用后恢复5点饥饿值，30点饱和度。*b且食用后会获得15秒漂浮II、30秒缓降II、5分钟膳食II。");
        add("patchouli.confluence.otherworld_note.food.cloud_bread.2", "相较于普通面包，它可不需要排成一条。");
        add("patchouli.confluence.otherworld_note.food.cloud_bread.3", "我怎么在飞啊~");

        add("patchouli.confluence.otherworld_note.food.fruit.name", "水果");
        add("patchouli.confluence.otherworld_note.food.fruit.0", "水果是一种食物，除了砂糖橘外，都可以直接食用。*b食用所有的水果都可以恢复 4 点饥饿值和 12 点饱和度。*b食用水果还可以获得 5 分钟的膳食。*b杨桃、火龙果和葡萄是例外，它们可以恢复 6 点饥饿值和 42 点饱和度（由于溢出，所以实际上最多只有 20 点），并提供 5 分钟膳食II和 50 秒饥饿延缓。");
        add("patchouli.confluence.otherworld_note.food.fruit.1", "破坏各种树叶后有概率掉落各种水果。*b所有的水果都可以微光嬗变为仙馔密酒。");
        add("patchouli.confluence.otherworld_note.food.fruit.2", "砂糖橘是一种水果，无法直接食用。*b需要手持砂糖橘右击以拨开为拨开的砂糖橘。");
        add("patchouli.confluence.otherworld_note.food.fruit.3", "已拨开的砂糖橘，可食用。食用后给予 5 分钟膳食，恢复 4 点饥饿值和 12 点饱和度。");

        // 魔法
        add("patchouli.confluence.otherworld_note.mana.mana_regeneration.name", "恢复魔力");
        add("patchouli.confluence.otherworld_note.mana.mana_regeneration.0", "魔力在不使用时会自行再生，直到达到玩家的当前最大值。自然再生速率会受玩家是否在移动中、和人物当前有多少魔力所影响。魔力也可以通过药水，饰品和拾取星星再生。");

        add("patchouli.confluence.otherworld_note.mana.mana_capacity.name", "魔力容量");
        add("patchouli.confluence.otherworld_note.mana.mana_capacity.0", "每个人物都会以 20 魔力容量（魔力条上一颗星星）开局。用魔力水晶可以将玩家的魔力容量永久性提升 20，为魔力条添上另一颗星星。这最多可以进行 9 次，加上一开始的那颗星星，一共会有 10 颗星星，代表 200 魔力。");

        // 材料
        add("patchouli.confluence.otherworld_note.material.coin.name", "钱币");
        add("patchouli.confluence.otherworld_note.material.coin.0", "钱币包括四种不同等级的材质：铜、银、金、铂金。还有特殊的绿宝石币，作用后面再说。*b可以这么获取钱币：*i击杀敌对生物有概率；*i战利品箱子；*i!cs:e!提炼机*c。*2*o“有了钱地球才会转，孩子。”   --蟹老板");
        add("patchouli.confluence.otherworld_note.material.coin.1", "钱币的4个等级是可以相互转化的，使用百进制。*b例如：每100个铜币可以做成1个银币。*b1个铂金币等于：*i100个金币；*i10,000个银币*i1,000,000个铜币*b即一个铂金币等于1x10^6个铜币。");
        add("patchouli.confluence.otherworld_note.material.coin.2", "你拾取的、通过快捷键放入的或从收纳物品（如箱子）中取出的钱币会自动结合成更高等级的钱币。*b然而，手动将钱币移入收纳物品时，它们不会自动结合，此时需要再拾取一次货币方可自动结合。*b也可以手动合并钱币。手持钱币对着空气潜行右击以合成上级钱币；!cs:hwb!重型工作台*c也可以将钱币结合为上级/下级钱币。");
        add("patchouli.confluence.otherworld_note.material.coin.3", "钱币还可以被放置在方块上。*b放置后的钱币堆可以被徒手破坏，在破坏后会返还所有钱币。手持钱币右击方块即可将钱币放下。方块形式的钱币可以单独成堆，也可以混杂叠放；没有堆叠高度限制，只要钱币足够多就可以一直放置下去；方块形式的钱币还拥有类似沙子的物理特性。");
        add("patchouli.confluence.otherworld_note.material.coin.4", "绿宝石币可以用于与银行家交易。*b银行家是一种新增的村民职业，工作站点是方块形式的金币/铂金币，可以与其交易一些东西。*2当玩家死亡时，所拥有的钱币都会掉落。");
        add("patchouli.confluence.otherworld_note.material.coin.5", "金金金金金金金金金");

        add("patchouli.confluence.otherworld_note.material.corruption_material.name", "腐化材料");
        add("patchouli.confluence.otherworld_note.material.corruption_material.0", "!w:co!腐化之地*c中生物的掉落物和结构里的宝箱会有各种各样的材料。*2邪恶...也是种力量。");
        add("patchouli.confluence.otherworld_note.material.corruption_material.1", "击杀噬魂怪、吞噬怪和腐骴概率掉落。");
        add("patchouli.confluence.otherworld_note.material.corruption_material.2", "击杀吞噬怪掉落。");
        add("patchouli.confluence.otherworld_note.material.corruption_material.3", "击杀世界吞噬怪掉落。");
        add("patchouli.confluence.otherworld_note.material.corruption_material.4", "一种生成在腐化之地的蘑菇，用于制作魔粉进而制作蠕虫诱饵。");
        add("patchouli.confluence.otherworld_note.material.corruption_material.5", "魔法灵感菇🍄~");

        add("patchouli.confluence.otherworld_note.material.crimson_material.name", "猩红材料");
        add("patchouli.confluence.otherworld_note.material.crimson_material.0", "!w:cr!猩红之地*c中生物的掉落物和结构里的宝箱会有各种各样的材料。*2邪恶...也是种力量。");
        add("patchouli.confluence.otherworld_note.material.crimson_material.1", "击杀血爬虫、脸怪、猩红咯迈拉和血腥芽孢概率掉落。");
        add("patchouli.confluence.otherworld_note.material.crimson_material.2", "击杀视神经元掉落，也会出现在克苏鲁之脑宝藏袋中。");
        add("patchouli.confluence.otherworld_note.material.crimson_material.3", "一种生成在猩红之地的蘑菇，用于制作毒粉进而制作血腥脊柱。");
        add("patchouli.confluence.otherworld_note.material.crimson_material.4", "小黑子你们闹够没！");

        add("patchouli.confluence.otherworld_note.material.falling_star.name", "坠落之星");
        add("patchouli.confluence.otherworld_note.material.falling_star.0", "坠落之星会在整个夜间期间随机生成，坠落后在地上会发光，使其在夜晚中容易找到。它可以用来做魔力水晶从而提升自己的法力上限。*b坠落时，它会对砸到的生物造成 100 点（50 颗心）伤害。*2*o“夜晚，星星在坠落，洒满全世界。它们的用途极为广泛。");
        add("patchouli.confluence.otherworld_note.material.falling_star.1", "*o如果你看到了，一定要拿到手，因为星星在日出后就会消失。”*t--向导");

        add("patchouli.confluence.otherworld_note.material.gel.name", "凝胶");
        add("patchouli.confluence.otherworld_note.material.gel.0", "凝胶是构成一些史莱姆的物品。*b击杀大多数种类的史莱姆生物即可获得凝胶。*2注意：凝胶并不是史莱姆球，而是一种可食的燃料，凝胶可燃但黏不住东西；*b史莱姆球黏附性大但不能燃烧。*2*o食品级和工业级的区别。");
        add("patchouli.confluence.otherworld_note.material.gel.1", "据说还有种稀有的粉色凝胶...");

        add("patchouli.confluence.otherworld_note.material.gems.name", "宝石");
        add("patchouli.confluence.otherworld_note.material.gems.0", "宝石是一种材料，用于合成。*b会在提炼机和箱子中出现。*2为了规避“紫水晶”，原“紫晶”改为“紫晶石”");

        //生物
        add("patchouli.confluence.otherworld_note.mob.slime.name", "史莱姆");
        add("patchouli.confluence.otherworld_note.mob.slime.0", "相较于原有的史莱姆，这些新的史莱姆更加多元化。");
        add("patchouli.confluence.otherworld_note.mob.slime.1", "绿色史莱姆≠Minecraft史莱姆。");
        add("patchouli.confluence.otherworld_note.mob.slime.2", "这个不是腐化，可能是踩到青金石了。");
        add("patchouli.confluence.otherworld_note.mob.slime.3", "这个不是猩红，可能是吃到虞美人了。");

        //工具
        add("patchouli.confluence.otherworld_note.tool.axe.name", "斧");
        add("patchouli.confluence.otherworld_note.tool.axe.0", "斧作为一种兼备工具与武器为一体的工具，你会想要一把的！");
        add("patchouli.confluence.otherworld_note.tool.axe.1", "再生之斧是使用食人怪藤蔓、丛林孢子、再生法杖制作的斧。");
        add("patchouli.confluence.otherworld_note.tool.axe.2", "金斧是使用红玉、金斧和金锭制作的斧。*b*o不要与原版的金斧混淆。");
        add("patchouli.confluence.otherworld_note.tool.axe.3", "暗夜战斧是使用魔矿锭制作的斧，特点是不会损坏！");
        add("patchouli.confluence.otherworld_note.tool.axe.4", "嗜血狂斧是使用猩红矿锭制作的斧，特点是不会损坏！");

        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.name", "无底桶");
        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.0", "无底桶可以看做是一个不会消耗的桶，可以源源不断的放置液体。*2无底桶在参与合成时不会被消耗！");
        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.1", "无底水桶 = 2个水桶");
        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.2", "无底熔岩桶 = 无尽能源");
        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.3", "纯天然无污染！");
        add("patchouli.confluence.otherworld_note.tool.bottomless_bucket.4", "小心掉进去！");

        add("patchouli.confluence.otherworld_note.tool.bug_net.name", "虫网");
        add("patchouli.confluence.otherworld_note.tool.bug_net.0", "一把小虫网，可以用来抓虫，进而钓鱼。*2（不过目前版本并没有虫）（悲）");
        add("patchouli.confluence.otherworld_note.tool.bug_net.1", "一把黄金制成的虫网，甚至可以用来抓小型动物（比如猪、牛）！*2（但是还是不能抓虫）（悲）");

        add("patchouli.confluence.otherworld_note.tool.fishing_rod.name", "钓竿");
        add("patchouli.confluence.otherworld_note.tool.fishing_rod.0", "那根木头棍子当然可以用一辈子，但是更高级的鱼竿会有更多的战利品！");
        add("patchouli.confluence.otherworld_note.tool.fishing_rod.1", "强化钓竿是使用铅锭/铁锭制作的钓竿，比普通钓竿更耐用！");
        add("patchouli.confluence.otherworld_note.tool.fishing_rod.2", "玻璃钢钓竿是在丛林地下小屋搜刮宝箱获得的钓竿。");
        add("patchouli.confluence.otherworld_note.tool.fishing_rod.3", "甲虫钓竿是在绿洲匣抽取获得的钓竿。");

        add("patchouli.confluence.otherworld_note.tool.hammer.name", "锤");
        add("patchouli.confluence.otherworld_note.tool.hammer.0", "锤是一种基本工具，可快速破坏如墙、泥土、树叶和木板这类硬度较低的物品。*b当其破坏方块时，以被目标方块为中心，3x3 范围内的方块也会一起被破坏。");
        add("patchouli.confluence.otherworld_note.tool.hammer.1", "金锤是使用金锭制作的锤，可能稍微有些不耐用？");
        add("patchouli.confluence.otherworld_note.tool.hammer.2", "魔锤是使用魔矿锭制作的锤，特点是不会损坏！");
        add("patchouli.confluence.otherworld_note.tool.hammer.3", "血肉锤是使用猩红矿锭制作的锤，特点是不会损坏！");

        add("patchouli.confluence.otherworld_note.tool.hook.name", "抓钩");
        add("patchouli.confluence.otherworld_note.tool.hook.0", "抓钩是一类有助于玩家穿越地形的工具。*b使用时，抓钩会发射链条来钩住方块表面并将玩家拉向它。*2将抓钩装备至饰品栏中的抓钩栏后，对准想要前往的地方按下F键即可发射抓钩。*b到达目标地点后跳跃即可。");
        add("patchouli.confluence.otherworld_note.tool.hook.1", "抓钩主要有宝石钩和其他各种特殊抓钩。");

        add("patchouli.confluence.otherworld_note.tool.misc.name", "杂项");
        add("patchouli.confluence.otherworld_note.tool.misc.0", "各种各样的武器百花八门，千奇百怪的功能各式各样！");
        add("patchouli.confluence.otherworld_note.tool.misc.1", "一面美观的镜子，可以送你回家！*2不，它不是魔镜，不会告诉你谁是最美的。");
        add("patchouli.confluence.otherworld_note.tool.misc.2", "也是一面美观的镜子，不过可能有点冻手...*2其实冰块是甜的，不信你可以舔一下尝尝！");

        add("patchouli.confluence.otherworld_note.tool.paint.name", "油漆");
        add("patchouli.confluence.otherworld_note.tool.paint.0", "油漆可以用于改变方块的颜色，几乎所有可放置物品都可以被油漆涂色。*b油漆需要使用漆刷或油漆滚刷来刷到方块上，若持有喷漆器，那么方块在放置时会自动被喷涂上色，每次上色都会消耗 1 个油漆。*b基本的油漆可以在油漆工NPC处购买。");
        add("patchouli.confluence.otherworld_note.tool.paint.1", "若有多个颜色的油漆，那么在物品栏最前方的颜色会被最先使用（顺序为从上到下，从左到右）；*b若玩家的物品栏和背包中均有油漆，且它们的颜色不同，那么物品栏中的颜色会被优先使用。");
        add("patchouli.confluence.otherworld_note.tool.paint.2", "漆刷是用于为整个方块上色的一种工具。*2油漆滚刷用于为方块单面上色的一种工具。*2其实直接挖掉方块也能使油漆失效...");
        add("patchouli.confluence.otherworld_note.tool.paint.3", "漆铲是一种用于移除油漆的工具，令玩家无需破坏并重新放置方块来去除油漆。*2手持漆铲右击需要除漆的方块，即可将方块上的油漆去除；*b漆铲默认为整个方块除漆，若想为单面去漆，只需潜行右击方块即可。");
        add("patchouli.confluence.otherworld_note.tool.paint.4", "看来还是直接挖掉更省事...");

        add("patchouli.confluence.otherworld_note.tool.pickaxe.name", "镐");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.0", "镐作为一种兼备工具与武器为一体的工具，你会想要一把的！");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.1", "仙人掌镐是使用仙人掌制作的镐，非常的好用。");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.2", "金镐是使用红玉、金镐和金锭制作的镐。*b*o不要与原版的金镐混淆。");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.3", "恶魔镐是使用魔矿锭和暗影鳞片制作的镐，特点是不会损坏！");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.4", "死亡使者镐是使用猩红矿锭和组织样本制作的镐，特点是不会损坏！");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.5", "化石镐是使用坚固化石制作的镐，又老又坚固！");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.6", "熔岩镐是一个骷髅王前的镐，使用狱石矿锭和烈焰棒制作，十分坚固！");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.7", "骨镐是一把骷髅王前的镐，相较同等级的其他镐，它的挖掘速度更快。");
        add("patchouli.confluence.otherworld_note.tool.pickaxe.8", "掠夺鲨是一把通过钓鱼获取的镐。*b*o鲨鲨，嘿嘿:P");

        add("patchouli.confluence.otherworld_note.tool.rope.name", "绳索");
        add("patchouli.confluence.otherworld_note.tool.rope.0", "几段看上去平平无奇的绳子，但是危机情况下也许会救你一命！");
        add("patchouli.confluence.otherworld_note.tool.rope.1", "把零散的绳子集合起来，效果更佳！");

        //武器
        add("patchouli.confluence.otherworld_note.weapon.arrow.name", "箭");
        add("patchouli.confluence.otherworld_note.weapon.arrow.0", "");
        add("patchouli.confluence.otherworld_note.weapon.arrow.1", "");
        add("patchouli.confluence.otherworld_note.weapon.arrow.2", "");
        add("patchouli.confluence.otherworld_note.weapon.arrow.3", "");
        add("patchouli.confluence.otherworld_note.weapon.arrow.4", "");
        add("patchouli.confluence.otherworld_note.weapon.arrow.5", "");

        add("patchouli.confluence.otherworld_note.weapon.bomb.name", "炸弹");
        add("patchouli.confluence.otherworld_note.weapon.bomb.0", "炸弹是一种道具，主要可用于采矿时的爆破。*b可以在地下自然生成的金箱中开到，也有概率在罐子打碎后获得。");
        add("patchouli.confluence.otherworld_note.weapon.bomb.1", "黏性炸弹可以用炸弹和凝胶合成获得。*b黏性炸弹与炸弹的属性基本一致，区别是当抛出的黏性炸弹接触方块时会粘在方块上。");
        add("patchouli.confluence.otherworld_note.weapon.bomb.2", "弹力炸弹可以用炸弹和粉凝胶合成获得。*b弹力炸弹与炸弹的属性基本一致，区别是当抛出的弹力炸弹接触方块时会弹起，弹起的高度随下落的高度增高而增高，但不会超过下落时的高度。");
        add("patchouli.confluence.otherworld_note.weapon.bomb.3", "甲虫炸弹是一种道具，主要可用于采矿时的爆破。*b可以使用炸弹和坚固化石合成，也有概率在沙漠罐子打碎后获得。");

        add("patchouli.confluence.otherworld_note.weapon.boomerang.name", "回旋镖");
        add("patchouli.confluence.otherworld_note.weapon.boomerang.0", "木回旋镖是一把基础的回旋镖，后续可以升级成更高级的回旋镖。");
        add("patchouli.confluence.otherworld_note.weapon.boomerang.1", "附魔回旋镖是使用木回旋镖制作的回旋镖，增加了伤害还无法破坏。");
        add("patchouli.confluence.otherworld_note.weapon.boomerang.2", "蘑菇回旋镖是一把回旋镖，击杀孢子蝙蝠后有概率掉落。");
        add("patchouli.confluence.otherworld_note.weapon.boomerang.3", "烈焰回旋镖是使用附魔回旋镖制作的回旋镖。");
        add("patchouli.confluence.otherworld_note.weapon.boomerang.4", "三尖回旋镖是使用附魔回旋镖、附魔回旋镖和冰雪回旋镖制作的回旋镖。");

        add("patchouli.confluence.otherworld_note.weapon.bow.name", "弓");
        add("patchouli.confluence.otherworld_note.weapon.bow.0", "作为射手的的武器之一，弓也不一定只有射手才能用。");
        add("patchouli.confluence.otherworld_note.weapon.bow.1", "石骸弓是使用坚固化石制作的弓，会将剑转化成化石箭。");
        add("patchouli.confluence.otherworld_note.weapon.bow.2", "");
        add("patchouli.confluence.otherworld_note.weapon.bow.3", "恶魔弓是使用魔矿锭制作的弓，特点是不会损坏！");
        add("patchouli.confluence.otherworld_note.weapon.bow.4", "肌腱弓是使用猩红矿锭制作的弓，特点是不会损坏！");
        add("patchouli.confluence.otherworld_note.weapon.bow.5", "浴火之怒弓是使用狱石矿锭制作的弓，特点是不会损坏！");
        add("patchouli.confluence.otherworld_note.weapon.bow.6", "");

        add("patchouli.confluence.otherworld_note.weapon.dynamite.name", "雷管");
        add("patchouli.confluence.otherworld_note.weapon.dynamite.0", "雷管是一种道具，主要可用于采矿时的爆破。*b可以在地下自然生成的金箱中开到。");
        add("patchouli.confluence.otherworld_note.weapon.dynamite.1", "黏性雷管可以用雷管和凝胶在工作台上合成获得。*b黏性雷管与雷管的属性基本一致，区别是当抛出的黏性雷管接触方块时会粘在方块上。");
        add("patchouli.confluence.otherworld_note.weapon.dynamite.2", "弹力雷管可以用雷管和粉凝胶在工作台上合成获得。*b弹力雷管与雷管的属性基本一致，区别是当抛出的弹力雷管接触方块时会弹起，弹起的高度随下落的高度增高而增高，但不会超过下落时的高度。");
        add("patchouli.confluence.otherworld_note.weapon.dynamite.3", "甲虫炸弹是一种道具，主要可用于采矿时的爆破。*b可以使用炸弹和坚固化石合成，也有概率在沙漠罐子打碎后获得。");

        add("patchouli.confluence.otherworld_note.weapon.short_bow.name", "短弓");
        add("patchouli.confluence.otherworld_note.weapon.short_bow.0", "短弓是一种武器，相较于普通的弓，它们的速度更快，所需材料较少，但伤害低。");
        add("patchouli.confluence.otherworld_note.weapon.short_bow.1", "不是取火钻!");

        add("patchouli.confluence.otherworld_note.weapon.short_sword.name", "短剑");
        add("patchouli.confluence.otherworld_note.weapon.short_sword.0", "短剑是一种武器，相较于普通的剑/阔剑，它们的速度更快，所需材料较少，但伤害低。");
        add("patchouli.confluence.otherworld_note.weapon.short_sword.1", "小匕首可爱捏。");

        add("patchouli.confluence.otherworld_note.weapon.sword.name", "阔剑");
        add("patchouli.confluence.otherworld_note.weapon.sword.0", "作为战士的的武器之一，阔剑也不一定只有战士才能用。");
        add("patchouli.confluence.otherworld_note.weapon.sword.1", "仙人掌剑是使用仙人掌制作的阔剑。");
        add("patchouli.confluence.otherworld_note.weapon.sword.2", "金阔剑是使用红玉、金剑和金锭制作的镐。*b*o不要与原版的金剑混淆。");

        //世界
        add("patchouli.confluence.otherworld_note.world.ash_forest.name", "灰烬木林");
        add("patchouli.confluence.otherworld_note.world.ash_forest.0", "灰烬木林是一种会在下界生成的生物群系，这里生长着大量的灰烬木。*b灰烬木林几乎都由灰烬块组成，最上方则被灰烬草方块覆盖，灰烬草在其上生长。*2灰烬木在这里生长，玩家可以在这里获取到灰烬木及其衍生物。火焰花同样会在这里生成。");
        add("patchouli.confluence.otherworld_note.world.ash_forest.1", "*z$(#AAAAAA)“烬墟生芽启新痕。”");
        add("patchouli.confluence.otherworld_note.world.ash_forest.2", "*o*t灰暗的尘土死气沉沉，*b*t赤红的世界生机勃勃");

        add("patchouli.confluence.otherworld_note.world.ash_wasteland.name", "灰烬荒地");
        add("patchouli.confluence.otherworld_note.world.ash_wasteland.0", "灰烬荒地是一种荒芜的生物群系，会在下界生成。*b灰烬荒地几乎都由灰烬块组成，地形则类似下界荒地。*2灰烬狱石会在这里较多地生成，并且相当显眼，所以这里算得上是一个挖掘狱石的好去处。*b火焰花同样会在这里生成。");
        add("patchouli.confluence.otherworld_note.world.ash_wasteland.1", "*z$(#AAAAAA)“灼世熔烬埋终章”");
        add("patchouli.confluence.otherworld_note.world.ash_wasteland.2", "*o*t灰暗的尘土隐藏宝物，*b*t赤红的世界危机四伏。");

        add("patchouli.confluence.otherworld_note.world.enchanted_sword_shrine.name", "附魔剑冢");
        add("patchouli.confluence.otherworld_note.world.enchanted_sword_shrine.0", "附魔剑冢是一种会在世界中随机生成的结构，整体风格类似于繁茂洞穴。*b在正上方会有很长的狭长空间连通地面（尽管通常不会连通）。");
        add("patchouli.confluence.otherworld_note.world.enchanted_sword_shrine.1", "*z$(#B4684D)“隔绝世界的神器”");
        add("patchouli.confluence.otherworld_note.world.enchanted_sword_shrine.2", "*o*t深埋于月光流转的岩层，隐藏在邪恶祭坛的裂隙，青苔斑驳的岩石暗暗埋葬着星芒碎屑。*b*t躲避于尘世之外的洞穴，藏身在世间隐蔽的角落，深蓝锋利的宝剑仍然闪烁着点点星光。");

        add("patchouli.confluence.otherworld_note.world.heaven_island.name", "天堂岛");
        add("patchouli.confluence.otherworld_note.world.heaven_island.0", "天堂岛是生成于世界上空的漂浮岛屿，有天堂岛和天堂湖两种变种。*2天堂岛由云朵包裹着一片陆地组成，包含一个天空小屋，小屋中必定包含两个箱子。*b天堂湖全部由云块和水组成，可以在这里钓鱼。*2*o钓鱼佬永不空军！！！");
        add("patchouli.confluence.otherworld_note.world.heaven_island.1", "*z$(#9A5CC6)“崇高的宝藏”");
        add("patchouli.confluence.otherworld_note.world.heaven_island.2", "*o*t浮岛悬于云霭之间，土石基座托起千年苔痕，残破神殿流淌着凝固的星辉。*b*t飘湖立于云顶之中，浮云包围支撑生命之源，隐秘蓝水保存着流淌的生命。");
        add("patchouli.confluence.otherworld_note.world.heaven_island.3", "*z$(#9A5CC6)“寂静的生命”");

        add("patchouli.confluence.otherworld_note.world.living_tree.name", "生命树");
        add("patchouli.confluence.otherworld_note.world.living_tree.0", "生命树是一种大型的树形结构，它只会在森林群系生成。*b生命树地表部分的底部的内部是空心的，其中有一个箱子，内容与其他地表箱子一致。*b在其内部还有一条通往地下部分的通道，顺着通道往下走，可以发现一个小房间，房间内有一个包含独特物品的箱子。");
        add("patchouli.confluence.otherworld_note.world.living_tree.1", "*z$(#55FF55)“健康的生命之树”");
        add("patchouli.confluence.otherworld_note.world.living_tree.2", "*o*t滕阔的巨树屹立世间，*b*t森罗的邪恶引起质变。");

        add("patchouli.confluence.otherworld_note.world.nether_tower.name", "下界塔");
        add("patchouli.confluence.otherworld_note.world.nether_tower.0", "下界塔是一种会生成在下界岩浆海的附近的结构。只会生成在下界荒地和灰烬荒地两个生物群系中。*b由两个主塔和连接它们的桥梁组成，各区域都分布着资源。主塔共有三层，两塔存在着一些差异：一座底层为绯红木板，另一座则为诡异木板，通过建筑下半部分的色系即可区分它们。");
        add("patchouli.confluence.otherworld_note.world.nether_tower.1", "*z$(#FF5555)“炎热的藏宝地”");
        add("patchouli.confluence.otherworld_note.world.nether_tower.2", "*o*t酷热的塔中隐藏着宝藏，*b*t堵塞的冤魂诉说着肮脏。");

        add("patchouli.confluence.otherworld_note.world.queen_bee_hive.name", "蜂王蜂巢");
        add("patchouli.confluence.otherworld_note.world.queen_bee_hive.0", "蜂王蜂巢是一个巨型蜂巢，自然生成于丛林群系的地下。*b主要由丛林蜂巢构成，在靠底部的部位有一摊较大的蜂蜜池。*2*l注意！在蜂蜜池中间会有一个以丛林蜂巢为底座、被稀薄蜂蜜块包裹的幼虫，如果将其破坏将会召唤出蜂王！");
        add("patchouli.confluence.otherworld_note.world.queen_bee_hive.1", "*z$(#FFFF55)“甜蜜的炸弹”");
        add("patchouli.confluence.otherworld_note.world.queen_bee_hive.2", "*o*t甜蜜的蜂蜜暗藏着剧毒，*b*t严密的淤泥包裹着诅猝。");

        add("patchouli.confluence.otherworld_note.world.shimmer_lake.name", "微光湖");
        add("patchouli.confluence.otherworld_note.world.shimmer_lake.0", "微光湖是一种会在世界中随机生成的结构，其中会大量生成微光。*b在微光湖的中央，有着一大池的微光，周围环绕着几棵宝石树。");
        add("patchouli.confluence.otherworld_note.world.shimmer_lake.1", "*z$(#FF55FF)“美丽而静谧，虚幻而空灵”");
        add("patchouli.confluence.otherworld_note.world.shimmer_lake.2", "*o*t微光湖似那星穹坠落的碎片，液态星光在寂静中泛起涟漪。*b*t秘境被柔蓝雾气笼罩，湖面如镜倒悬着永恒暮色，浸入其中便化作虚无剪影，与亿万星辰的呢喃共沉于无垠梦境。");

        add("patchouli.confluence.otherworld_note.world.sky_village.name", "天空村庄");
        add("patchouli.confluence.otherworld_note.world.sky_village.0", "天空村庄是一种生成于世界上空的建筑，就像!w:hi!一样。天空村庄由多个浮岛组成，中心浮岛和其他浮岛之间由桥梁连接着，其它浮岛有农田浮岛和房屋浮岛，而房屋浮岛有以下类型：*i铁匠铺；*i石匠屋；*i天文台；*i马厩岛；*i大教堂；*i图书馆。");
        add("patchouli.confluence.otherworld_note.world.sky_village.1", "*z$(#B4684D)“漂浮的桃花源”");
        add("patchouli.confluence.otherworld_note.world.sky_village.2", "*o*t浮岛聚成集体，辅道链接文明，腐盗远离人间。*b*t桃园浮于孤寂，陶院保护孤独，涛冤断绝世间。");
        add("patchouli.confluence.otherworld_note.world.sky_village.3", "*z$(#B4684D)“孤寂之家”");

        add("patchouli.confluence.otherworld_note.world.the_corruption.name", "腐化之地");
        add("patchouli.confluence.otherworld_note.world.the_corruption.0", "腐化之地是一种邪恶生物群系，有着深紫色的废土、死亡、与腐烂的主题，会生成腐化主题的敌怪。*b腐化之地群系会在邪恶生物群系为腐化的世界中自然生成。也可以自行创造腐化之地，当一个区块内的腐化系邪恶方块（例如黑檀石块）超过 256 个时，这个区块的生物群系会变为腐化之地。*b相应的，当邪恶方块不足 256 个时，这个区块的群系会变为原本的生物群系。");
        add("patchouli.confluence.otherworld_note.world.the_corruption.1", "*z$(#AA00AA)“正在使世界变得邪恶”");
        add("patchouli.confluence.otherworld_note.world.the_corruption.2", "*o*t腐化之地是一片充满裂隙和悬崖的危险区域，弥漫着邪恶与死亡的气息。*b*t它是由生物的邪念所产生的毒瘤，滋生出可怕的畸形怪物，这些怪物妒忌一切生灵。");

        add("patchouli.confluence.otherworld_note.world.the_crimson.name", "猩红之地");
        add("patchouli.confluence.otherworld_note.world.the_crimson.0", "猩红之地是一种邪恶生物群系，有着肉体感染的红色血腥主题，会生成血腥主题的敌怪。*b猩红之地群系会在邪恶生物群系为猩红的世界中自然生成。也可以自行创造猩红之地，当一个区块内的猩红系邪恶方块（例如猩红石块）超过 256 个时，这个区块的生物群系会变为猩红之地。*b相应的，当邪恶方块不足 256 个时，这个区块的群系会变为原本的生物群系。");
        add("patchouli.confluence.otherworld_note.world.the_crimson.1", "*z$(#AA0000)“正在使世界变得血腥”");
        add("patchouli.confluence.otherworld_note.world.the_crimson.2", "*o*t古时，泰拉瑞亚的人民犯下了愚昧的错误：他们屈服于猩红，视其为神明，并献祭自己的血肉。*b*t最终，他们被同化成扭曲的生命，与猩红的野心一同蔓延在大地上。");
    }

    @Override
    public void add(String key, String value) {
        consumer.accept(key, value);
    }
}
