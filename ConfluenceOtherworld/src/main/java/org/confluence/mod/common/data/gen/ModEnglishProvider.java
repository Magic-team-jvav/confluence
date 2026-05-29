package org.confluence.mod.common.data.gen;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.mixin.accessor.LanguageProviderAccessor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.data.gen.language.*;
import org.confluence.mod.common.data.saved.MoonPhase;
import org.confluence.mod.common.data.saved.Team;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.integration.create.CreateHelper;
import org.confluence.mod.integration.create.ponder.PonderHelper;
import org.confluence.mod.integration.waystones.WaystonesHelper;
import org.confluence.terra_curio.common.init.TCEffects;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.utils.RecipeDrawerUtils;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ModEnglishProvider extends LanguageProvider {
    CompletableFuture<HolderLookup.Provider> lookup;
    HolderLookup.Provider registries;

    public ModEnglishProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        this(output, "en_us", lookup);
    }

    public ModEnglishProvider(PackOutput output, String locale, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, Confluence.MODID, locale);
        this.lookup = lookup;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return lookup.thenCompose(registries -> {
            this.registries = registries;
            return super.run(cache);
        });
    }

    @Override
    protected void addTranslations() {
        var dimensionsLookup = registries.lookup(Registries.DIMENSION_TYPE);
        dimensionsLookup.ifPresent(dimensionTypeRegistryLookup -> addDefaultRegistryTranslations(dimensionTypeRegistryLookup, Registries.DIMENSION_TYPE.location().getPath()));

        var fluidLookup = registries.lookup(Registries.FLUID);
        fluidLookup.ifPresent(fluidRegistryLookup -> addDefaultRegistryTranslations(fluidRegistryLookup, Registries.FLUID.location().getPath()));

        var biomeLookup = registries.lookup(Registries.BIOME);
        biomeLookup.ifPresent(biomeRegistryLookup -> addDefaultRegistryTranslations(biomeRegistryLookup, Registries.BIOME.location().getPath()));

        for (var moonPhase : MoonPhase.values()) {
            add(moonPhase.getSerializedName(), formatString(moonPhase.name()));
        }

        add("confluence.trade_lock.drawer.position.title", "Position");
        add("confluence.trade_lock.drawer.position.and", "and");
        add("confluence.trade_lock.drawer.moon_phase.title", "Moon Phase");
        add("confluence.trade_lock.drawer.fishing.requires_hook", "Requires Fishing While Trading");
        add("confluence.trade_lock.drawer.fishing.fishing_in", "Be Fishing in");
        add("confluence.trade_lock.drawer.environment.block", "Block");
        add("confluence.trade_lock.drawer.environment.fluid", "Fluid");
        add("confluence.trade_lock.drawer.environment.block_state_predicate", "Block State");
        add("confluence.trade_lock.drawer.dimension.title", "Dimension");
        add("confluence.trade_lock.drawer.date.lunar", "Lunar");
        add("confluence.trade_lock.drawer.bestiary.title", "Bestiary Pages Unlocked");
        add("confluence.trade_lock.drawer.any_boss_defeated.title", "Any Boss Defeated");
        add("confluence.trade_lock.drawer.environment.radius", "In radius of %s blocks");

        add("confluence.prefix_separator", " ");
        add("confluence.game_event", "Game Event");

        add("confluence.difficulty_notice.ask", "You are in mediumcore! Do you want to change to softcore?");
        add("confluence.difficulty_notice.sure", "Sure");
        add("confluence.difficulty_notice.sure.tip", "Set keepInventory to true");
        add("confluence.difficulty_notice.never", "Never");
        add("confluence.difficulty_notice.never.tip", "Don't show this message again");
        add("confluence.difficulty_notice.tip", "Hold Alt to select");
        add("confluence.difficulty_notice.sure.done", "Has been set to softcore");
        add("confluence.difficulty_notice.never.done", "Will never ask for softcore");

        // 自然方块分类
        add("itemGroup.confluence.ebony", "Ebony Wood");
        add("itemGroup.confluence.pearl", "Pearl Wood");
        add("itemGroup.confluence.shadow", "Shadow Wood");
        add("itemGroup.confluence.palm", "Palm Wood");
        add("itemGroup.confluence.baobab", "Baobab Wood");
        add("itemGroup.confluence.yellow_willow", "Yellow Willow Wood");
        add("itemGroup.confluence.spooky", "Spooky Wood");
        add("itemGroup.confluence.living", "Living Wood");
        add("itemGroup.confluence.living_mahogany", "Living Mahogany Wood");
        add("itemGroup.confluence.ash", "Ash Wood");
        add("itemGroup.confluence.void", "Void Wood");
        add("itemGroup.confluence.gaze", "Gaze Wood");
        add("itemGroup.confluence.moonglow_willow", "Moonglow Willow Wood");
        add("itemGroup.confluence.dynasty", "Dynasty Wood");
        add("itemGroup.confluence.pine", "Pine Wood");
        add("itemGroup.confluence.fey", "Fey Wood");
        add("itemGroup.confluence.stone_tree", "Stone Tree");
        add("itemGroup.confluence.pot", "Pots");

        add("itemGroup.confluence.natural_environment", "Natural Environment");
        add("itemGroup.confluence.corruption", "Corruption");
        add("itemGroup.confluence.hallow", "Hallow");
        add("itemGroup.confluence.crimson", "Crimson");
        add("itemGroup.confluence.mushroom", "Mushroom");
        add("itemGroup.confluence.desert", "Desert");
        add("itemGroup.confluence.jungle", "Jungle");
        add("itemGroup.confluence.end", "The End");
        add("itemGroup.confluence.nether", "Nether");
        add("itemGroup.confluence.skyland", "Sky Island");
        add("itemGroup.confluence.snow", "Snow");
        add("itemGroup.confluence.ocean", "Ocean");
        add("itemGroup.confluence.crops", "Crops");
        add("itemGroup.confluence.branches", "Branches");
        add("itemGroup.confluence.vines", "Vines");
        add("itemGroup.confluence.shimmer", "Shimmer");
        add("itemGroup.confluence.moss", "Moss");
        add("itemGroup.confluence.special_plants", "Special Plants");
        add("itemGroup.confluence.miscellaneous", "Miscellaneous");
        add("itemGroup.confluence.sanctification_ores", "Hallowed Ores");
        add("itemGroup.confluence.corruption_ores", "Corruption Ores");
        add("itemGroup.confluence.fleshification_ores", "Crimson Ores");
        add("itemGroup.confluence.normal_ores", "Normal Ores");
        add("itemGroup.confluence.raw_ore_blocks", "Raw Ore Blocks");
        add("itemGroup.confluence.ore_storage_blocks", "Ore Storage Blocks");
// 建筑方块分类

        add("itemGroup.confluence.gloom_obsidian_bricks", "Gloom Obsidian Bricks");
        add("itemGroup.confluence.blue_ice_bricks", "Blue Ice Bricks");
        add("itemGroup.confluence.packed_ice_bricks", "Packed Ice Bricks");
        add("itemGroup.confluence.sandstone_bricks", "Sandstone Bricks");
        add("itemGroup.confluence.red_sandstone_bricks", "Red Sandstone Bricks");
        add("itemGroup.confluence.ebonsandstone_bricks", "Ebonsandstone Bricks");
        add("itemGroup.confluence.pearlsandstone_bricks", "Pearlsandstone Bricks");
        add("itemGroup.confluence.crimsandstone_bricks", "Crimsandstone Bricks");
        add("itemGroup.confluence.snow_bricks", "Snow Bricks");
        add("itemGroup.confluence.aetherium_bricks", "Aetherium Bricks");
        add("itemGroup.confluence.rainbow_bricks", "Rainbow Bricks");
        add("itemGroup.confluence.copper_bricks", "Copper Bricks");
        add("itemGroup.confluence.tin_bricks", "Tin Bricks");
        add("itemGroup.confluence.iron_bricks", "Iron Bricks");
        add("itemGroup.confluence.lead_bricks", "Lead Bricks");
        add("itemGroup.confluence.silver_bricks", "Silver Bricks");
        add("itemGroup.confluence.tungsten_bricks", "Tungsten Bricks");
        add("itemGroup.confluence.golden_bricks", "Gold Bricks");
        add("itemGroup.confluence.platinum_bricks", "Platinum Bricks");
        add("itemGroup.confluence.demonite_ore_bricks", "Demonite Bricks");
        add("itemGroup.confluence.ebonstone_bricks", "Ebonstone Bricks");
        add("itemGroup.confluence.meteorite_bricks", "Meteorite Bricks");
        add("itemGroup.confluence.crimtane_ore_bricks", "Crimtane Bricks");
        add("itemGroup.confluence.crimstone_bricks", "Crimstone Bricks");
        add("itemGroup.confluence.pearlstone_bricks", "Pearlstone Bricks");
        add("itemGroup.confluence.sun_plate", "Sun Plate");
        add("itemGroup.confluence.disc_block", "Disc Block");
        add("itemGroup.confluence.moon_plate", "Moon Plate");
        add("itemGroup.confluence.obsidian_bricks", "Obsidian Bricks");
        add("itemGroup.confluence.granite_bricks", "Granite Bricks");
        add("itemGroup.confluence.marble_bricks", "Marble Bricks");
        add("itemGroup.confluence.blue_bricks", "Blue Dungeon Bricks");
        add("itemGroup.confluence.green_bricks", "Green Dungeon Bricks");
        add("itemGroup.confluence.pink_bricks", "Pink Dungeon Bricks");

        add("itemGroup.confluence.glass", "Glass");
        add("itemGroup.confluence.special_building", "Special Building");
        add("itemGroup.confluence.chains", "Chains");
        add("itemGroup.confluence.doors", "Doors");
        add("itemGroup.confluence.statue", "Statues");
        add("itemGroup.confluence.boss_relics", "Boss Relics");
        add("itemGroup.confluence.balloons", "Balloons");
        add("itemGroup.confluence.gem_blocks", "Gem Blocks");
        add("itemGroup.confluence.fur_wool", "Fur & Wool");
// 机械功能方块
        add("itemGroup.confluence.boulders", "Boulders");
        add("itemGroup.confluence.redstone_circuit_traps", "Redstone Traps");
        add("itemGroup.confluence.trigger", "Triggers");
        add("itemGroup.confluence.crafting_stations", "Crafting Stations");
        add("itemGroup.confluence.storage", "Storage");
        add("itemGroup.confluence.souls", "Soul Bottles");
        add("itemGroup.confluence.misc_functional", "Misc Functional");
        add("itemGroup.confluence.pylon", "Pylon");
// 材料
        add("itemGroup.confluence.metal_materials", "Metal Materials");
        add("itemGroup.confluence.natural_materials", "Natural Materials");
        add("itemGroup.confluence.souls_special", "Souls & Special Materials");
        add("itemGroup.confluence.monster_drops", "Monster Drops");
        add("itemGroup.confluence.plants_herbs", "Plants & Herbs");
        add("itemGroup.confluence.crafting_materials", "Crafting Materials");
// 杂项
        add("itemGroup.confluence.treasure_bag", "Treasure Bags");
        add("itemGroup.confluence.tombstone", "Tombstones");
        add("itemGroup.confluence.bait", "Bait");
        add("itemGroup.confluence.quested_fish", "Quest Fish");
        add("itemGroup.confluence.crate", "Crates");
        add("itemGroup.confluence.paint", "Paint");
        add("itemGroup.confluence.throwing_weapons", "Throwing Weapons");
        add("itemGroup.confluence.bombs_explosives", "Bombs & Explosives");
        add("itemGroup.confluence.boss_event_summons", "Boss & Event Summons");
        add("itemGroup.confluence.environment_items", "Environment Items");
        add("itemGroup.confluence.gain", "Boosts");
        add("itemGroup.confluence.loot_gifts", "Loot & Gifts");
        add("itemGroup.confluence.coins", "Coins");
// 工具
        add("itemGroup.confluence.ropes", "Ropes");
        add("itemGroup.confluence.wand", "Wands");
        add("itemGroup.confluence.wiring_tools", "Wiring Tools");
        add("itemGroup.confluence.keys", "Keys");
        add("itemGroup.confluence.buckets_liquids", "Buckets & Liquids");
        add("itemGroup.confluence.nets", "Nets");
        add("itemGroup.confluence.utility_tools", "Utility Tools");
        add("itemGroup.confluence.axe", "Axe");
        add("itemGroup.confluence.pickaxe", "Pickaxe");
        add("itemGroup.confluence.pickaxe_axe", "Pickaxe Axe");
        add("itemGroup.confluence.drill", "Drill");
        add("itemGroup.confluence.chainsaw", "Chainsaw");
        add("itemGroup.confluence.hamaxe", "Hamaxe");
        add("itemGroup.confluence.how_shovel", "Hoe Shovel");
        add("itemGroup.confluence.garden_shears", "Garden Shears");
        add("itemGroup.confluence.hammer", "Hammer");
        add("itemGroup.confluence.hook", "Hook");
        add("itemGroup.confluence.minecart", "Minecart");
        add("itemGroup.confluence.fishing_pole", "Fishing Pole");
        add("itemGroup.confluence.hoe", "Hoe");
        add("itemGroup.confluence.shovel", "Shovel");
        add("itemGroup.confluence.boat", "Boat");
        add("itemGroup.confluence.chest_boat", "Chest Boat");
// 盔甲
        add("itemGroup.confluence.cactus_armor", "Cactus Armor");
        add("itemGroup.confluence.plank_armor", "Plank Armor");
        add("itemGroup.confluence.ebony_armor", "Ebony Armor");
        add("itemGroup.confluence.shadow_plank_armor", "Shadow Wood Armor");
        add("itemGroup.confluence.pearl_armor", "Pearl Armor");
        add("itemGroup.confluence.ash_armor", "Ash Armor");
        add("itemGroup.confluence.pumpkin_armor", "Pumpkin Armor");
        add("itemGroup.confluence.white_pumpkin_armor", "White Pumpkin Armor");
        add("itemGroup.confluence.thief_armor", "Thief Armor");
        add("itemGroup.confluence.reinforced_mail_armor", "Reinforced Mail Armor");
        add("itemGroup.confluence.climbing_armor", "Climbing Armor");
        add("itemGroup.confluence.battle_robe_armor", "Battle Robe Armor");
        add("itemGroup.confluence.hunters_armor", "Hunters Armor");
        add("itemGroup.confluence.guards_armor", "Guards Armor");
        add("itemGroup.confluence.spelunker_armor", "Spelunker Armor");
        add("itemGroup.confluence.battle_robe_armor", "Battle Robe Armor");
        add("itemGroup.confluence.splendid_robe_armor", "Splendid Robe Armor");
        add("itemGroup.confluence.archers_armor", "Archers Armor");
        add("itemGroup.confluence.phantom_armor", "Phantom Armor");
        add("itemGroup.confluence.hermit_armor", "Hermit Armor");
        add("itemGroup.confluence.blue_hermit_armor", "Blue Hermit Armor");
        add("itemGroup.confluence.scale_mail_armor", "Scale Mail Armor");
        add("itemGroup.confluence.rain_wear", "Rain Coat");
        add("itemGroup.confluence.snow_insulated_wear", "Snow Insulated Wear");
        add("itemGroup.confluence.pink_snow_insulated_wear", "Pink Snow Insulated Wear");
        add("itemGroup.confluence.obsidian_armor", "Obsidian Armor");
        add("itemGroup.confluence.gladiator_armor", "Gladiator Armor");
        add("itemGroup.confluence.meteor_armor", "Meteor Armor");
        add("itemGroup.confluence.copper_armor", "Copper Armor");
        add("itemGroup.confluence.tin_armor", "Tin Armor");
        add("itemGroup.confluence.lead_armor", "Lead Armor");
        add("itemGroup.confluence.silver_armor", "Silver Armor");
        add("itemGroup.confluence.tungsten_armor", "Tungsten Armor");
        add("itemGroup.confluence.golden_armor", "Gold Armor");
        add("itemGroup.confluence.platinum_armor", "Platinum Armor");
        add("itemGroup.confluence.fossil_armor", "Fossil Armor");
        add("itemGroup.confluence.bee_armor", "Bee Armor");
        add("itemGroup.confluence.ninja_armor", "Ninja Armor");
        add("itemGroup.confluence.spore_root_armor", "Spore Root Armor");
        add("itemGroup.confluence.cold_crystal_armor", "Cold Crystal Armor");
        add("itemGroup.confluence.heim_armor", "Heim Armor");
        add("itemGroup.confluence.shadow_armor", "Shadow Armor");
        add("itemGroup.confluence.crimson_armor", "Crimson Armor");
        add("itemGroup.confluence.mining_armor", "Mining Armor");
        add("itemGroup.confluence.angler_wear", "Angler Outfit");
        add("itemGroup.confluence.molten_armor", "Molten Armor");
        add("itemGroup.confluence.necro_armor", "Necro Armor");
        add("itemGroup.confluence.seeker_armor", "Seeker Armor");
        add("itemGroup.confluence.jungle_armor", "Jungle Armor");
        add("itemGroup.confluence.spider_armor", "Spider Armor");
        add("itemGroup.confluence.tiki_armor", "Tiki Armor");
        add("itemGroup.confluence.cobalt_armor", "Cobalt Armor");
        add("itemGroup.confluence.palladium_armor", "Palladium Armor");
        add("itemGroup.confluence.mythril_armor", "Mythril Armor");
        add("itemGroup.confluence.orichalcum_armor", "Orichalcum Armor");
        add("itemGroup.confluence.adamantite_armor", "Adamantite Armor");
        add("itemGroup.confluence.titanium_armor", "Titanium Armor");
        add("itemGroup.confluence.crystal_assassin_armor", "Crystal Assassin Armor");
        add("itemGroup.confluence.hallowed_armor", "Hallowed Armor");
        add("itemGroup.confluence.magic_robes", "Magic Robes");
        add("itemGroup.confluence.dyes", "Dyes");
        add("itemGroup.confluence.tuxedo_set", "Tuxedo Set");
        add("itemGroup.confluence.plumbers_set", "Plumber Set");
        add("itemGroup.confluence.heros_set", "Hero Set");
        add("itemGroup.confluence.archaeologists_set", "Archaeologist Set");
        add("itemGroup.confluence.clothiers_set", "Clothier Set");
        add("itemGroup.confluence.familiar_set", "Familiar Set");
        add("itemGroup.confluence.doctors_set", "Doctor Set");
        add("itemGroup.confluence.guy_fawkes_set", "Guy Fawkes Set");
        add("itemGroup.confluence.mummy_set", "Mummy Set");

        add("itemGroup.confluence.boomerang", "Boomerang");
        add("itemGroup.confluence.spear", "Spear");
        add("itemGroup.confluence.lance", "Lance");
        add("itemGroup.confluence.short_swords", "Short Swords");
        add("itemGroup.confluence.pre_hardmode_broadswords", "Pre-Hardmode Broadswords");
        add("itemGroup.confluence.hardmode_broadswords", "Hardmode Broadswords");
        add("itemGroup.confluence.yoyo", "Yoyo");

        add("itemGroup.confluence.short_bow", "Short Bow");
        add("itemGroup.confluence.bow", "Bow");
        add("itemGroup.confluence.crossbow", "Crossbow");
        add("itemGroup.confluence.arrow", "Arrows");
        add("itemGroup.confluence.bullet", "Bullets");

        add("itemGroup.confluence.gun", "Guns");

        add("itemGroup.confluence.crimson_entity", "Crimson");
        add("itemGroup.confluence.corruption_entity", "Corruption");
        add("itemGroup.confluence.hallow_entity", "Hallow");
        add("itemGroup.confluence.desert_entity", "Desert");
        add("itemGroup.confluence.jungle_entity", "Jungle");
        add("itemGroup.confluence.ice_entity", "Ice");
        add("itemGroup.confluence.forest_entity", "Forest");
        add("itemGroup.confluence.underground_entity", "Underground");
        add("itemGroup.confluence.mushroom_entity", "Mushroom");
        add("itemGroup.confluence.dungeon_entity", "Dungeon");
        add("itemGroup.confluence.nether_entity", "Nether");
        add("itemGroup.confluence.sky_entity", "Sky");
        add("itemGroup.confluence.mimic_entity", "Mimic");
        add("itemGroup.confluence.goblin_entity", "Goblin Army");
        add("itemGroup.confluence.water_entity", "Water Creature");
        add("itemGroup.confluence.insect_entity", "Insect");
        add("itemGroup.confluence.npc_entity", "NPC");
        add("itemGroup.confluence.boss_entity", "Boss");
        add("itemGroup.confluence.misc_entity", "Misc");

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
        add("chat.confluence.crystal_marked", "Void energy resonated. Position and face recorded.");
        add("chat.confluence.link_too_far", "The resonance is too weak. Connection failed (Maximum distance: 100 blocks).");
        add("chat.confluence.link_not_opposite", "Dimensional misalignment. Faces must be opposite to each other.");
        add("chat.confluence.crystal_cleared", "Void link data has been cleared.");
        add("chat.confluence.link_success", "A void link has been established successfully!");
        add("chat.confluence.link_same_block", "Cannot link a root to itself!");
        add("options.difficulty.legendary", "§aLegendary");
        add("message.confluence.choking", "You're choking and need to drink water");
        add("message.confluence.advancement_combat_techniques", "The book's knowledge empowers your villagers!");
        add("message.confluence.toolmode.tip", "Sneak & Right click on the air to switch mode");
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
        add("message.confluence.peddlers_satchel", "The Traveling Merchant's satchel deepens!");
        add("message.confluence.house_detect.occupied", "House has been occupied by %2$s the %1$s!");
        add("message.confluence.house_detect.npc_not_fount", "NPC not found nearby!");
        add("message.confluence.house_select.tip1", "Press Esc to escape, hold Alt to select");
        add("message.confluence.house_select.tip2", "Right Click to check or add/remove house");
        add("message.confluence.house_select.check", "Check");
        add("message.confluence.guide_voo_doo_doll.fail", "This powerful sacrificial item can only be used in the Nether!");
        add("message.confluence.slime_rain.start", "Slime is falling from the sky!");
        add("message.confluence.slime_rain.end", "Slime has stopped falling from the sky.");
        add("message.confluence.blood_moon.start", "The Blood Moon is rising...");
        add("message.confluence.goblin_army.ready", "A goblin army is approaching!");
        add("message.confluence.goblin_army.start", "A goblin army has arrived!");
        add("message.confluence.goblin_army.victory", "The Goblin Army has been defeated!");
        add("message.confluence.scrying_orb.singleplayer", "All you see is your reflection in the orb.");
        add("message.confluence.scrying_orb.alone", "You're by yourself...");
        add("message.confluence.on_team", "On %s Team"); // capital
        add("message.confluence.no_team", "No Team");
        add("message.confluence.join_team.button", "Join %s Team"); // capital
        add("message.confluence.join_team", "%1$s has joined the %2$s team.");
        add("message.confluence.leave_team.button", "Join No Team");
        add("message.confluence.leave_team", "%s is no longer on a team.");
        add("message.confluence.enable_pvp.button", "Enable PvP");
        add("message.confluence.enable_pvp", "%s has enabled PvP!");
        add("message.confluence.disable_pvp.button", "Disable PvP");
        add("message.confluence.disable_pvp", "%s has disabled PvP!");
        add("message.confluence.too_easy.ready", "Too Easy!");

        for (Team team : Team.TEAMS) {
            String name = team.getSerializedName();
            String key = "team.confluence." + name;
            String titleCase = LibUtils.toTitleCase(name);
            add(key, titleCase);
            add(key + ".lower_case", titleCase.toLowerCase(Locale.ROOT));
        }

        add("commands.confluence.reforge.cannot_be_reforged", "This item cannot be reforged (or cannot find an item that needs to be reforged)!");
        add("commands.confluence.reforge.unknown_prefix_type", "Unknown prefix type (or reforge failure)!");
        add("commands.confluence.reforge.success", "Successfully reforged to: %s");
        add("commands.confluence.reforge.clear.success", "The prefix has been successfully cleared");
        add("commands.confluence.reforge.set.unavailable_group", "This item cannot have this prefix applied!");
        add("commands.confluence.arguments.prefix.invalid", "Invalid Prefix!");
        add("commands.confluence.arguments.game_event.unknown", "Unknown Game Event!");

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
        add("tooltip.jei.shimmer_black_list", "The shimmer will prevent the item from transmutation or decompose");
        add("tooltip.jei.brewing_stand_terra_potion", "The intermediate product is a Chaos Potion, which needs to be used again as a raw material and mixed with the next ingredient.");
        add("tooltip.jei.intermediate_product", "Intermediate Product");

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
        add("tooltip.item.confluence.meteor_compass.0", "Only the position of the last meteorite are saved");
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
        add("tooltip.item.confluence.gel.0", "'Both tasty and flammable'");
        add("tooltip.item.confluence.npc_invitation.0", "Use it to invite a new batch of NPCs in the current area!");
        add("tooltip.item.confluence.red_potion.0", "'Only for those who are worthy'");
        add("tooltip.item.confluence.mug.0", "Collect ale at the Keg");
        add("tooltip.item.confluence.hellforge.0", "Used for smelting hellstone materials. Smelting regular minerals has a residual heat effect.");
        add("tooltip.item.confluence.magic_conch.0", "Right-click a block in the Beach biome to make the sea remember you");
        add("tooltip.item.confluence.demon_conch.0", "Right-click a Nether Portal block to make the Nether remember you.");
        add("tooltip.item.confluence.bait.common.0", "When placed in the inventory, it will be automatically used while fishing, prioritizing the bait in the off-hand.");
        add("tooltip.item.confluence.crate.common.0", "Hold the right mouse button to open, and Sneak while clicking the right mouse button to place.");
        add("tooltip.item.confluence.right_click.common.0", "Hold the right mouse button to open.");
        add("tooltip.item.confluence.raw_asphalt.0", "Use a Blend-O-Matic to make asphalt blocks");
        add("tooltip.item.confluence.empty_dropper.0", "Right-click on the droplet to remove it (requires block assist aiming)");
        add("tooltip.item.confluence.guide_to_critter_companionship.0", "Prevents you from hurting critters while in the inventory");
        add("tooltip.item.confluence.guide_to_critter_companionship.1", "Right click to deactivate/reactivate effects");
        add("tooltip.item.confluence.guide_to_environmental_preservation.0", "Prevents you from accidentally destroying the environment while in the inventory");
        add("tooltip.item.confluence.guide_to_environmental_preservation.1", "Right click to deactivate/reactivate effects");
        add("tooltip.item.confluence.guide_to_peaceful_coexistence.0", "Prevents you from hurting critters while in the inventory");
        add("tooltip.item.confluence.guide_to_peaceful_coexistence.1", "Prevents you from accidentally destroying the environment while in the inventory");
        add("tooltip.item.confluence.guide_to_peaceful_coexistence.2", "Right click to deactivate/reactivate effects");
//        add("tooltip.item.confluence.fallen_soul_core.0", "Channel spirits through the celestial stars; right-click to switch magic types");
        add("tooltip.item.confluence.repeater.0", "Hold left click to fire");
        add("tooltip.item.confluence.repeater.1", "Right click in inventory to retrieve arrows");
        add("tooltip.item.confluence.wireable.0", "Wireable");
        add("tooltip.item.confluence.can_be_extractinated.0", "Can Be Extractinated");

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
        add("tooltip.item.terra_curio.guide_voodoo_doll.1", "Hold and press to switch type");
        add("tooltip.item.confluence.guide_voodoo_doll.wall.0", "\"Karma's fire consumes the puppet, the veil of flesh is raised\"");
        add("tooltip.item.confluence.guide_voodoo_doll.wall.1", "Cast this innocent doll into the Underworld's magma, and the Wall of Flesh shall emerge from hell's far side; only the Underworld's magma can satisfy the harsh heat demands of the burning sacrifice");
        add("tooltip.item.confluence.guide_voodoo_doll.wall.2", "Sealed within Its heart is the world's most primal, untamed power, which shall fully unleash when Its mortal form crumbles.");
        add("tooltip.item.confluence.guide_voodoo_doll.wall.3", "\"How does it feel to watch the one who guided your every step writhe in the agony of being burned alive?\"");
        add("tooltip.item.confluence.guide_voodoo_doll.wall.4", "Summons the Wall of Flesh");
        add("tooltip.item.confluence.guide_voodoo_doll.hill.0", "\"Karma's fire consumes the puppet, scarlet peaks surge with a tide of blood\"");
        add("tooltip.item.confluence.guide_voodoo_doll.hill.1", "Cast this innocent doll into the Underworld's magma, and the Hill of Flesh shall hatch from its nurturing cradle; only the Underworld's magma can satisfy the harsh heat demands of the burning sacrifice");
        add("tooltip.item.confluence.guide_voodoo_doll.hill.2", "Sealed within Its heart is the world's most primal, untamed power, which shall fully unleash when Its mortal form crumbles.");
        add("tooltip.item.confluence.guide_voodoo_doll.hill.3", "\"How does it feel to watch the one who guided your every step writhe in the agony of being burned alive?\"");
        add("tooltip.item.confluence.guide_voodoo_doll.hill.4", "Summons the Hill of Flesh");
        add("tooltip.item.confluence.hardmode_forge.0", "Used to smelt adamantite and titanium ore");
        add("tooltip.item.confluence.loom.0", "Used for crafting cloth");
        add("tooltip.item.confluence.dye_vat.0", "Used to Craft Dyes");
        add("tooltip.item.confluence.tuff_booth.0", "Right-click the upper part to place items, right-click the lower part with a carpet to lay it, and right-click the lower part with a name tag to display the item name");
        add("tooltip.item.confluence.heart_lantern.0", "Increases life regeneration when placed nearby");
        add("tooltip.item.confluence.star_in_a_bottle.0", "Increases mana regeneration when placed nearby");
        add("tooltip.item.confluence.jousting_lance.0", "Build momentum to increase attack power");
        add("tooltip.item.confluence.jousting_lance.1", "'Have at thee!'");
        add("tooltip.item.confluence.hallowed_jousting_lance.0", "Build momentum to increase attack power");
        add("tooltip.item.confluence.shadow_jousting_lance.0", "Build momentum to increase attack power");
        add("tooltip.item.confluence.rotten_bone_dust.0", "Right-click to spawn vegetation in the Corruption, or corrupt Skeletons");
        add("tooltip.item.confluence.bloodstained_powder.0", "Right-click to spawn vegetation in the Crimson, or infest Creepers with Crimson");
        add("tooltip.item.confluence.deer_thing.0", "Right-click to summon the Deerclops, which will emerge amidst the frigid winds of the Tundra");
        add("tooltip.item.confluence.deer_thing.1", "A frigid eye that glimmers with the sight of another world in its socket");
        add("tooltip.item.confluence.deer_thing.2", "\"Something big is coming... Don't draw Klaus here again!\"");
        add("tooltip.item.confluence.enemy_banner.0", "Nearby players get a bonus against: %s");

        add("tooltip.item.confluence.tokyo_teddy_bear.0", "A self - abased girl said like a broken teddy bear:");
        add("tooltip.item.confluence.tokyo_teddy_bear.1", "           Let me tell you");
        add("tooltip.item.confluence.tokyo_teddy_bear.2", "           The words of all - knowing and all - powerful");
        add("tooltip.item.confluence.tokyo_teddy_bear.3", "           Other than the mind");
        add("tooltip.item.confluence.tokyo_teddy_bear.4", "           Is no longer needed");
        add("tooltip.item.confluence.tokyo_teddy_bear.5", "——A story told by a spider");
        add("tooltip.item.confluence.paradox_interactive_medal.0", "Proof of having played Hearts of Iron, Victoria, Europa Universalis, Crusader Kings, and Cities: Skylines at the same time.");
        add("tooltip.item.confluence.kind_miside_ring.0", "“The ring will lead you to the right direction, dear”");
        add("tooltip.item.confluence.failed_skull.0", "A creeper had its body forcibly transformed by piglins and can explode multiple times. The piglins wanted to use it as a biological weapon to invade the Overworld, but it escaped unexpectedly.");
        add("tooltip.item.confluence.ice_tofu_brick.0", "It’s not just about ramming things head-on to take them down.");
        add("tooltip.item.confluence.pink_cola.0", "An ordinary bottle of pink cola. Maybe there was a whole case originally?");
        add("tooltip.item.confluence.dongdongs_flatbread.0", "Freshly baked flatbread on the Netherrack. Come and have a taste!");
        add("tooltip.item.confluence.boredoms_pact_falling_resolve.0", "(Boredom's Pact - Falling Resolve)");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.1", "           ");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.2", "The blood of the indolent has soaked into the stardust core, condensing into this breathing cursed stone.");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.3", "When in motion, the veins of the earth surge, and blades cleave through the long night; when still, the earth's heart beats, and the sky opens its single eye.");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.4", "The ancient god inscribed punishment into the contract: eight heartbeats of stillness summon the judgment of a falling star.");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.5", "It doesn't remain silent like a golem - it cackles when boulders shatter shinbones:");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.6", "'Look, even the stones understand the way of survival better than your legs.'");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.7", "The wearer will eventually understand that so - called 'invincibility' merely means outrunning death by a single second.");
        add("tooltip.item.terra_curio.boredoms_pact_falling_resolve.8", "And the soul has long been crushed into dust in the rock crevices, more desolate than the empty shell of a golem.");

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
        add("worldgen.confluence.too_easy", "Creating easy world...");
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

        add("death.attack.dungeon_altar", "Steve was one step away from uncovering the secret by right-clicking tuff with the meteor compass.");

        add("death.attack.falling_star", "%1$s got a response from a meteor");
        add("death.attack.falling_star.player", "%1$s's wish was finally granted in the presence of %2$s");
        add("death.attack.boulder", "%1$s is crushed by boulder");
        add("death.attack.darkness", "%1$s was killed by something in the dark!");
        add("death.attack.darkness.player", "%1$s tried to flee from %2$s, yet never anticipated the scheming of the puppet master");
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
        add("tooltip.item.confluence.tackle_box.0", "Decreases chance of bait consumption");
        add("tooltip.item.confluence.angler_tackle_bag.0", "Fishing line will never break, decreases chance of bait consumption, increases fishing power by 10");
        add("tooltip.item.confluence.lavaproof_fishing_hook.0", "Allows fishing in lava");
        add("tooltip.item.confluence.lavaproof_tackle_bag.0", "Fishing line will never break, decreases chance of bait consumption, increases fishing power by 10");
        add("tooltip.item.terra_curio.lavaproof_tackle_bag.1", "Allows fishing in lava");
        add("tooltip.item.confluence.lucky_coin.0", "Hitting enemies may drop extra coins");
        add("tooltip.item.confluence.gold_ring.0", "Increases coin pickup range");
        add("tooltip.item.confluence.discount_card.0", "Reduces shop prices by 20%");
        add("tooltip.item.confluence.coin_ring.0", "Hitting enemies may drop extra coins, increases coin pickup range");
        add("tooltip.item.confluence.greedy_ring.0", "Hitting enemies may drop extra coins, increases coin pickup range, reduces shop prices by 20%");
        add("tooltip.item.confluence.spectre_goggles.0", "Provides ghost vision to interact with echo blocks");
        add("tooltip.item.terra_curio.spectre_goggles.1", "Right-click in the backpack to toggle on/off.");
        add("tooltip.item.confluence.guide_to_plant_fiber_cordage.0", "Allows the collection of Vine Rope from vines");
        add("tooltip.item.confluence.fledgling_wings.0", "Allows flight and slow fall");
        add("tooltip.item.confluence.chromatic_cloak.0", "Immunity to Shimmer Phasing, Hold Sneak to Phase while submerged in Shimmer");
        add("tooltip.item.confluence.paintbrush.0", "Used with paint to color blocks");
        add("tooltip.item.confluence.paintbrush.1", "Can also apply coatings");
        add("tooltip.item.confluence.paint_roller.0", "Used with paint to color walls");
        add("tooltip.item.confluence.paint_roller.1", "Can also apply coatings");
        add("tooltip.item.confluence.paint_scraper.0", "Used to remove paint or coatings. Sneak to remove only one side");
        add("tooltip.item.confluence.paint_scraper.1", "Can sometimes collect moss");
        add("tooltip.item.confluence.paint_sprayer.0", "Automatically paints or coats placed objects.");
        add("tooltip.item.confluence.coin.0", "Sneak and right-click to merge into a primary coin");
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
        add("tooltip.item.confluence.water_candle.0", "Holding this may attract unwanted attention");
        add("tooltip.item.confluence.peace_candle.0", "Makes surrounding creatures less hostile");
        add("tooltip.item.confluence.blood_tear.0", "Summons the Blood Moon");
        add("tooltip.item.confluence.blood_tear.1", "'What a horrible night to have a curse.'");
        add("tooltip.item.confluence.goblin_battle_standard.0", "Summons a Goblin Army ");

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

        add("tooltip.confluence.attack_damage", "Damage: %s");
        add("tooltip.confluence.mana_cost", "Mana Cost: %s");
        add("tooltip.confluence.velocity", "Projectile Speed: %s");
        add("tooltip.confluence.cooldown", "Cooldown: %s");
        add("tooltip.confluence.attack_duration", "Attack Duration: %s");
        add("tooltip.confluence.attack_interval", "Attack Interval: %s");
        add("tooltip.confluence.attack_distance", "Attack Distance: %s");
        add("tooltip.confluence.knockback", "Knockback: %s");
        add("tooltip.confluence.disabled", "Disabled");
        add("tooltip.confluence.pickaxe_power", "Pickaxe Power: %s%%");
        add("tooltip.confluence.hammer_power", "Hammer Power: %s%%");
        add("tooltip.confluence.armor_penetration", "Armor Penetration: %s");
        add("tooltip.confluence.effect_duration", "%s minutes duration");

        add("tooltip.item.confluence.radio_thing.0", "Allows the user to see the world differently");
        add("tooltip.item.terra_curio.radio_thing.1", "'Forbidden Knowledge echoes from the radio...'");

        add("tooltip.item.confluence.sweet_sword.0", "au'undertale: above nothingness' written by 一只屑水缡");
        add("tooltip.item.confluence.piglin_stew.0", "The last thing a Piglin would crave before starving, yet they never got to taste it...");

        add("tooltip.item.confluence.copper_short_sword.0", "The smallest fragment of the divine weapon's power has been with you since the confluence of the two worlds... until the journey's end.");
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
        add("tooltip.item.confluence.star_steel_sword.0", "Upon hitting an enemy, has a chance to make them drop a Mana Star");
        add("tooltip.item.confluence.star_steel_sword.1", "Within 1 second after picking up a Mana Star, triggers a 2.5x critical damage multiplier");
        add("tooltip.item.confluence.magic_missile.0", "Casts a controllable missile");
        add("tooltip.item.confluence.flamelash.0", "Summons a controllable ball of fire");
        add("tooltip.item.confluence.rainbow_rod.0", "Casts a controllable rainbow");
        add("tooltip.item.confluence.crystal_serpent.0", "Shoots an explosive crystal charge");

        add("tooltip.item.confluence.soul_of_light.0", "'The essence of light creatures'");
        add("tooltip.item.confluence.soul_of_night.0", "'The essence of dark creatures'");
        add("tooltip.item.confluence.soul_of_flight.0", "'The essence of powerful flying creatures'");
        add("tooltip.item.confluence.soul_of_might.0", "'The essence of the destroyer'");
        add("tooltip.item.confluence.soul_of_sight.0", "'The essence of omniscient watchers'");
        add("tooltip.item.confluence.soul_of_fright.0", "'The essence of pure terror'");
        add("tooltip.item.confluence.soul_of_bright.0", "The essence of wisdom forged in rebirth");
        add("tooltip.item.confluence.soul_of_voight.0", "The essence of the dragon born from the void");
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
        add("tooltip.item.confluence.dungeon_compass.0", "--“The eerie skull will guide your way”");
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
        add("tooltip.item.confluence.peddlers_satchel.0", "Permanently increases items sold by the Traveling Merchant");
        add("tooltip.item.confluence.bug_net.0", "Used to catch critters");
        add("tooltip.item.confluence.lavaproof_bug_net.0", "'For when things get too hot to handle'");
        add("tooltip.item.confluence.golden_bug_net.0", "Used to catch critters");
        add("tooltip.item.confluence.golden_bug_net.1", "Can catch lava critters too!");
        add("tooltip.item.confluence.dev_bug_net.0", "Can catch most creatures!");
        add("tooltip.item.confluence.lucy_the_axe.0", "'I love Lucy!'");
        add("tooltip.item.confluence.key_of_light.0", "Charged with the essence of many souls");
        add("tooltip.item.confluence.key_of_night.0", "Charged with the essence of many souls");

        add("tooltip.confluence.void_crystal.clear_hint", "Sneak + Right-click to clear recorded data");
        add("tooltip.confluence.void_crystal.pos", "§7Recorded Position: §fX:%d Y:%d Z:%d");
        add("tooltip.confluence.void_crystal.face", "§7Facing: §d%s");
        add("tooltip.confluence.void_crystal.empty", "§8Right-click a Void Root to begin linking.");

        add("attribute.name.repeater.arrow_capacity", "Arrow Capacity");
        add("attribute.name.repeater.attack_speed", "Arrow Speed");
        add("attribute.name.repeater.concurrency_count", "Concurrency Count");
        add("attribute.name.repeater.firing_interval", "Firing Interval");
        add("attribute.name.repeater.knockback", "Knockback");
        add("attribute.name.repeater.reload_speed", "Reload Speed");
        add("attribute.name.repeater.torrent_count", "Torrent Count");

        new ConfigurationLanguageSubProvider(this::add, true);

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
        add("biome.minecraft.confluence_sky", "Space");
        add("biome.confluence.chorus_forest", "Chorus Forest");
        add("biome.confluence.chorus_plains", "Chorus Plains");
        add("biome.confluence.dark_moon_flats", "Dark Moon Flats");
        add("biome.confluence.inverse_forest", "Inverse Forest");
        add("biome.confluence.inverse_plains", "Inverse Plains");
        add("biome.confluence.moonblight_forest", "Moonblight Forest");
        add("biome.confluence.moonblight_plains", "Moonblight Plains");
        add("biome.confluence.moonlit_dry_sea", "Moonlit Dry Sea");

        new AchievementsLanguageSubProvider(this::add, true);

        add("prefix.confluence.tooltip.plus", "+%s%% %s");
        add("prefix.confluence.tooltip.take", "-%s%% %s");
        add("prefix.confluence.tooltip.add", "+%s %s");
        add("prefix.confluence.tooltip.mana_cost", "Mana Cost");
        add("prefix.confluence.tooltip.additional_mana", "Additional Mana");
        add("prefix.confluence.tooltip.four_classes_damage", "Four Classes Damage");
        add("prefix.confluence.quick", "Quick");
        add("prefix.confluence.hasty", "Hasty");
        add("prefix.confluence.deadly", "Deadly");
        ModPrefix.GROUPS.values().stream().flatMap(map -> map.values().stream()).forEach(prefix -> {
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
        add("container.confluence.adamantite_forge", "Adamantite Forge");
        add("container.confluence.titanium_forge", "Titanium Forge");
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
        add("container.confluence.loom", "Loom");
        add("container.confluence.dye_vat", "Dye Vat");
        add("container.confluence.dye_mix", "Dye Mix");

        add("title.confluence.shimmer_transmutation", "Shimmer Transmutation");
        add("title.confluence.altar", "Altar");
        add("title.confluence.sky_mill", "Sky Mill");
        add("title.confluence.heavy_work_bench", "Heavy Work Bench");
        add("title.confluence.hellforge", "Hell Forge");
        add("title.confluence.hardmode_anvil", "Hardmode Anvil");
        add("title.confluence.hardmode_forge", "Hardmode Forge");
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
        add("title.confluence.loom", "Loom");
        add("title.confluence.dye_vat", "Dye Vat");
        add("title.confluence.brewing_stand_terra_potion", "Use the Brewing Stand to craft Terra Potions");
        add("title.confluence.armor_set_bonus", "Armor Set Bonus");

        add("button.confluence.dye_vat", "Dye Vat");
        add("button.confluence.dye_mix", "Dye Mix");

        // Override
        add("item.confluence.encumbering_stone.disable", "Encumbering Stone: Disable");
        add("item.confluence.guide_to_critter_companionship.disable", "Guide to Critter Companionship: Disable");
        add("item.confluence.guide_to_environmental_preservation.disable", "Guide to Environmental Preservation: Disable");
        add("item.confluence.guide_to_peaceful_coexistence.disable", "Guide to Peaceful Coexistence: Disable");
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
        add(ConsumableItems.PEDDLERS_SATCHEL.get(), "Peddler's Satchel");
        add(QuestedFishes.CAPN_TUNABEARD.get(), "Cap'n Tunabeard");

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
        add("event.confluence.npc_invitation", "Invitation delivered! A new batch of NPCs will be added in the region from chunk pos [%1$s, %2$s] to [%3$s, %4$s]!");

        add("event.confluence.reveal_step0", "Your world has been blessed with Cobalt and Palladium!");
        add("event.confluence.reveal_step1", "Your world has been blessed with Mythril and Orichalcum!");
        add("event.confluence.reveal_step2", "Your world has been blessed with Adamantite and Titanium!");
        add("event.confluence.reveal_step3", "Your world has been blessed with more Cobalt and Palladium!");
        add("event.confluence.reveal_step4", "Your world has been blessed with more Mythril and Orichalcum!");
        add("event.confluence.reveal_step5", "Your world has been blessed with more Adamantite and Titanium!");
        add("event.confluence.reveal_step6", "Your world has been blessed with the maximum amount of Cobalt and Palladium!");
        add("event.confluence.reveal_step7", "Your world has been blessed with the maximum amount of Mythril and Orichalcum!");
        add("event.confluence.reveal_step8", "Your world has been blessed with the maximum amount of Adamantite and Titanium!");

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

        new DialogsLanguageSubProvider(this::add, true);

        new PatchouliLanguageSubProvider(this::add, true);

        // sound
        add("confluence.subtitle.transmission", "Transmission Magic: Activated");
        add("confluence.subtitle.lightsaber_open", "Lightsaber: Activated");
        add("confluence.subtitle.regular_staff_shoot", "Magic: Cast");
        add("confluence.subtitle.regular_staff_shoot_2", "Magic: Burst");
        add("confluence.subtitle.regular_staff_shoot_3", "Magic: Jet");
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
        add("confluence.subtitle.lucyaxe_talk", "Lucy Axe: Coquettish Remonstrance");
        add("confluence.subtitle.repeater_item_aerial_shooting", "Repeater Crossbow: Load Arrows");
        add("confluence.subtitle.crystal_vile_shard_shoot", "Crystal Vile Shard: Extend");

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

        new BestiaryLanguageSubProvider(this::add, true);

        // Armor Bonus
        add("armor_set_bonus.when_applied", "Set Bonus:");
        // Mining Set
        add("tooltip.item.confluence.mining_helmet.0", "Provides light when worn");
        add("armor_set_bonus.confluence.mining_set.0", "10% increased mining speed");
        // Plank Set
        add("armor_set_bonus.confluence.plank_set.0", "+1 Armor");
        add("armor_set_bonus.confluence.pearlwood_set.0", "+1 Armor");

        add("armor_set_bonus.confluence.ash_set.0", "Reduces lava contact damage by 50%");
        add("armor_set_bonus.confluence.ash_set.1", "Reduces fire duration taken by 35%");
        // Base Ore
        add("armor_set_bonus.confluence.copper_set.0", "+1 Armor");
        add("armor_set_bonus.confluence.tin_set.0", "+2 Armor");
        add("armor_set_bonus.confluence.lead_set.0", "+1 Armor");
        add("armor_set_bonus.confluence.silver_set.0", "+2 Armor");
        add("armor_set_bonus.confluence.tungsten_set.0", "+1 Armor");
        add("armor_set_bonus.confluence.golden_set.0", "+1 Armor");
        add("armor_set_bonus.confluence.platinum_set.0", "+2 Armor");
        // Snow Set
        add("armor_set_bonus.confluence.snow_set.0", "Cannot be frozen or chilled");
        add("armor_set_bonus.confluence.pink_snow_set.0", "Cannot be frozen or chilled");
        // Angler Set
        add("tooltip.item.confluence.angler_hat.0", "Increases fishing power by 5");
        add("tooltip.item.confluence.angler_vest.0", "Increases fishing power by 5");
        add("tooltip.item.confluence.angler_pants.0", "Increases fishing power by 5");
        add("armor_set_bonus.confluence.angler_set.0", "Decreased enemy spawn rate");
        // Cactus Set
        add("armor_set_bonus.confluence.cactus_set.0", "Attackers take damage from the cactus spines");
        // Pumpkin Set
        add("armor_set_bonus.confluence.pumpkin_set.0", "10% increased damage");
        // White Pumpkin Set
        add("armor_set_bonus.confluence.white_pumpkin_set.0", "Melee critical hits restore 1 Soul Point");
        // Thief Set
        add("armor_set_bonus.confluence.thief_set.0", "Melee attack speed increased by 4%");
        add("armor_set_bonus.confluence.thief_set.1", "Ranged damage increased by 4%");
        // Reinforced Mail Set
        add("armor_set_bonus.confluence.reinforced_mail_set.0", "5% chance to dodge attacks");
        add("armor_set_bonus.confluence.reinforced_mail_set.1", "7% damage reduction");
        add("armor_set_bonus.confluence.reinforced_mail_set.2", "Movement speed reduced by 15% for 3 seconds after jumping");
        // Climbing Set
        add("armor_set_bonus.confluence.climbing_set.0", "75% Knockback Resistance");
        add("armor_set_bonus.confluence.climbing_set.1", "20% chance to resist debuffs inflicted by enemies");
        add("armor_set_bonus.confluence.climbing_set.2", "Step height increased by 0.5 blocks");
        // Ninja Set
        add("armor_set_bonus.confluence.ninja_set.0", "20% increased movement speed");
        // Hunters Set
        add("armor_set_bonus.confluence.hunters_set.0", "Applies Hunt effect to struck enemies");
        // Guards Set
        add("armor_set_bonus.confluence.guards_set.0", "10% chance to save ammo");
        // Spelunker Set
        add("armor_set_bonus.confluence.spelunker_set.0", "Increases your max number of minions by 1");
        add("armor_set_bonus.confluence.spelunker_set.1", "Increase the duration of spelunker potions by 2 minutes");
        // Splendid Robe Armor
        add("armor_set_bonus.confluence.splendid_robe_set.0", "7% increased movement speed");
        // Archer Set
        add("armor_set_bonus.confluence.archers_set.0", "Arrow speed increased by 5%");
        // Soul Dancer Robe
        add("armor_set_bonus.confluence.souldancer_robe_set.0", "For 10 seconds after collecting souls, gain 8% critical strike chance and 7% movement speed");
        add("armor_set_bonus.confluence.souldancer_robe_set.1", "For 10 seconds after collecting souls, gain 7% damage reduction");
        // Hermit Set
        add("armor_set_bonus.confluence.hermit_set.0", "Increase maximum soul capacity by 20");
        // Blue Hermit Set
        add("armor_set_bonus.confluence.blue_hermit_set.0", "Spend 10 souls to reduce the duration of drug resistance by 3 seconds");
        // Phantom Set
        add("armor_set_bonus.confluence.phantom_set.0", "Gain +1 souls per pick-up, +2 souls per pick-up during nighttime");
        // Fossil Set
        add("armor_set_bonus.confluence.fossil_set.0", "20% chance to save ammo");
        // Cold Crystal Set
        add("tooltip.item.confluence.cold_crystal_helmet.0", "Increases maximum mana by 20");
        add("tooltip.item.confluence.cold_crystal_chestplate.0", "Increases maximum mana by 20");
        add("armor_set_bonus.confluence.cold_crystal_set.0", "Magic Attack will have an additional Frostbite effect");
        // Spore Root Set
        add("armor_set_bonus.confluence.spore_root_set.0", "Increases your max number of minions by 1");
        // Heim Set
        add("tooltip.item.confluence.heim_helmet.0", "Extend underwater breathing time by 5%");
        add("armor_set_bonus.confluence.heim_set.0", "Grant you 4 health of absorption, and grant it again every 5 seconds");
        // Bee Set
        add("armor_set_bonus.confluence.bee_set.0", "Increases summon damage by 10%");
        // Obsidian Set
        add("armor_set_bonus.confluence.obsidian_set.0", "Increases whip range by 30% and speed by 15%,Increases summon damage by 15%");
        add("armor_set_bonus.confluence.obsidian_set.1", "Increases summon damage by 15%");
        // Gladiator Set
        add("armor_set_bonus.confluence.gladiator_set.0", "Immunity to Knockback");
        // Wizard Set
        add("tooltip.item.confluence.amber_robe.0", "Increases maximum mana by 60");
        add("tooltip.item.confluence.amber_robe.1", "13% reduced mana cost");
        add("tooltip.item.confluence.diamond_robe.0", "Increases maximum mana by 80");
        add("tooltip.item.confluence.diamond_robe.1", "15% reduced mana cost");
        add("tooltip.item.confluence.mystic_robe.0", "6% increased magic damage and critical strike chance");
        add("tooltip.item.confluence.mystic_robe.1", "10% reduced mana cost");
        add("tooltip.item.confluence.ruby_robe.0", "Increases maximum mana by 60");
        add("tooltip.item.confluence.ruby_robe.1", "13% reduced mana cost");
        add("tooltip.item.confluence.jade_robe.0", "Increases maximum mana by 60");
        add("tooltip.item.confluence.jade_robe.1", "11% reduced mana cost");
        add("tooltip.item.confluence.sapphire_robe.0", "Increases maximum mana by 40");
        add("tooltip.item.confluence.sapphire_robe.1", "9% reduced mana cost");
        add("tooltip.item.confluence.topaz_robe.0", "Increases maximum mana by 40");
        add("tooltip.item.confluence.topaz_robe.1", "7% reduced mana cost");
        add("tooltip.item.confluence.amethyst_robe.0", "Increases maximum mana by 20");
        add("tooltip.item.confluence.amethyst_robe.1", "5% reduced mana cost");
        add("tooltip.item.confluence.wizard_hat.0", "5% increased magic damage");
        add("tooltip.item.confluence.magic_hat.0", "6% increased magic damage and critical strike chance");
        // Meteor Set
        add("armor_set_bonus.confluence.meteor_set.0", "Space Gun magic consumption reduced to 0");
        // Jungle Set
        add("tooltip.item.confluence.jungle_helmet.0", "Increases maximum mana by 40");
        add("tooltip.item.confluence.jungle_chestplate.0", "Increases maximum mana by 20");
        add("tooltip.item.confluence.jungle_leggings.0", "Increases maximum mana by 20");
        add("armor_set_bonus.confluence.jungle_set.0", "16% reduced mana costs");
        // Necro Set
        add("armor_set_bonus.confluence.necro_set.0", "10% increased critical strike chance");
        // Seeker set
        add("armor_set_bonus.confluence.seeker_set.0", "Increases Soul Capacity by 40");
        add("armor_set_bonus.confluence.seeker_set.1", "Deal 10% increased damage when Soul is above 20");
        // Shadow Set
        add("armor_set_bonus.confluence.shadow_set.0", "Increased movement speed and acceleration");
        // Crimson Set
        add("armor_set_bonus.confluence.crimson_set.0", "Greatly increased life regen");
        // Molten Set
        add("armor_set_bonus.confluence.molten_set.0", "10% extra melee damage");
        add("armor_set_bonus.confluence.molten_set.1", "Cannot be set on fire");
        // Diamond Set
        add("armor_set_bonus.confluence.diamond_set.0", "Grant Fortune II");
        // Netherite Set
        add("armor_set_bonus.confluence.netherite_set.0", "Cannot be set on fire and immune to lava");
        add("armor_set_bonus.confluence.netherite_set.1", "5% increased movement speed");
        add("armor_set_bonus.confluence.netherite_set.2", "8% increased damage");
        add("armor_set_bonus.confluence.netherite_set.3", "Restores durability when submerged in lava");
        // Spider Set
        add("armor_set_bonus.confluence.spider_set.0", "Increases summon damage by 12%");
        // HardMode Ore
        add("tooltip.item.confluence.cobalt_hat.0", "Increases maximum mana by 40");
        add("armor_set_bonus.confluence.cobalt_helmet_set.0", "15% increased melee speed");
        add("armor_set_bonus.confluence.cobalt_mask_set.0", "20% chance to save ammo");
        add("armor_set_bonus.confluence.cobalt_hat_set.0", "14% reduced mana costs");

        add("tooltip.item.confluence.palladium_headgear.0", "Increases maximum mana by 60");
        add("armor_set_bonus.confluence.palladium_mask_set.0", "Greatly increases life regeneration after striking an enemy");
        add("armor_set_bonus.confluence.palladium_helmet_set.0", "Greatly increases life regeneration after striking an enemy");
        add("armor_set_bonus.confluence.palladium_headgear_set.0", "Greatly increases life regeneration after striking an enemy");

        add("tooltip.item.confluence.mythril_hood.0", "Increases maximum mana by 60");
        add("armor_set_bonus.confluence.mythril_hood_set.0", "17% reduced mana costs");
        add("armor_set_bonus.confluence.mythril_helmet_set.0", "10% increased critical strike chance");
        add("armor_set_bonus.confluence.mythril_hat_set.0", " 20% chance to save ammo");

        add("tooltip.item.confluence.orichalcum_headgear.0", "Increases maximum mana by 80");
        add("armor_set_bonus.confluence.orichalcum_headgear_set.0", "Flower petals will fall on your target for extra damage");
        add("armor_set_bonus.confluence.orichalcum_mask_set.0", "Flower petals will fall on your target for extra damage");
        add("armor_set_bonus.confluence.orichalcum_helmet_set.0", "Flower petals will fall on your target for extra damage");

        add("tooltip.item.confluence.adamantite_headgear.0", "Increases maximum mana by 80");
        add("armor_set_bonus.confluence.adamantite_headgear_set.0", "19% reduced mana costs");
        add("armor_set_bonus.confluence.adamantite_helmet_set.0", "20% increased melee and movement speed");
        add("armor_set_bonus.confluence.adamantite_mask_set.0", "25% chance to save ammo");

        add("tooltip.item.confluence.titanium_headgear.0", "Increases maximum mana by 100");
        add("armor_set_bonus.confluence.titanium_mask_set.0", "Attacking generates a defensive barrier of titanium shards");
        add("armor_set_bonus.confluence.titanium_helmet_set.0", "Attacking generates a defensive barrier of titanium shards");
        add("armor_set_bonus.confluence.titanium_headgear_set.0", "Attacking generates a defensive barrier of titanium shards");

        // Tiki Set
        add("armor_set_bonus.confluence.tiki_set.0", "Increases your max number of minions by 1");
        add("armor_set_bonus.confluence.tiki_set.1", "Increases whip range by 20%");

        // Title
        add("title.confluence.window.0", "Confluence: Dig Peon, Dig!");
        add("title.confluence.window.1", "Confluence: Epic Dirt");
        add("title.confluence.window.2", "Confluence: Adaman-TIGHT!");
        add("title.confluence.window.3", "Confluence: Sand is Overpowered");
        add("title.confluence.window.4", "Terraria Part 3: The Return of the Guide");
        add("title.confluence.window.5", "Confluence: A Bunnies Tale");
        add("title.confluence.window.6", "Confluence: Dr. Bones and The Temple of Blood Moon");
        add("title.confluence.window.7", "Confluence: Slimeassic Park");
        add("title.confluence.window.8", "Confluence: The Grass is Greener on This Side");
        add("title.confluence.window.9", "Confluence: Small Blocks, Not for Children Under the Age of 5");
        add("title.confluence.window.10", "Confluence: Digger T' Blocks");
        add("title.confluence.window.11", "Confluence: There is No Cow Layer");
        add("title.confluence.window.12", "Confluence: Suspicous Looking Eyeballs");
        add("title.confluence.window.13", "Confluence: Purple Grass!");
        add("title.confluence.window.14", "Confluence: No one Dug Behind!");
        add("title.confluence.window.15", "Confluence: The Water Fall Of Content!");
        add("title.confluence.window.16", "Confluence: Earthbound");
        add("title.confluence.window.17", "Confluence: Dig Dug Ain't Got Nuthin on Me");
        add("title.confluence.window.18", "Confluence: Ore's Well That Ends Well");
        add("title.confluence.window.19", "Confluence: Judgement Clay");
        add("title.confluence.window.20", "Confluence: Terrestrial Trouble");
        add("title.confluence.window.21", "Confluence: Obsessive-Compulsive Discovery Simulator");
        add("title.confluence.window.22", "Confluence: Red Dev Redemption");
        add("title.confluence.window.23", "Confluence: Rise of the Slimes");
        add("title.confluence.window.24", "Confluence: Now with more things to kill you!");
        add("title.confluence.window.25", "Confluence: Rumors of the Guides' death were greatly exaggerated");
        add("title.confluence.window.26", "Confluence: I Pity the Tools...");
        add("title.confluence.window.27", "Confluence: I Pity the Tools");
        add("title.confluence.window.28", "Confluence: A spelunker says 'What'?");
        add("title.confluence.window.29", "Confluence: So then I said 'Something about a PC update....'");
        add("title.confluence.window.30", "Confluence: May the blocks be with you");
        add("title.confluence.window.31", "Confluence: Better than life");
        add("title.confluence.window.32", "Confluence: Confluence: Confluence:");
        add("title.confluence.window.33", "Confluence: Now in 2D");
        add("title.confluence.window.34", "Confluence: Coming soon to a computer near you");
        add("title.confluence.window.35", "Confluence: Dividing by zero");
        add("title.confluence.window.36", "Confluence: Now with SOUND");
        add("title.confluence.window.37", "Confluence: Press F3+2");
        add("title.confluence.window.38", "Confluence: You sand bro?");
        add("title.confluence.window.39", "Confluence: A good day to dig hard");
        add("title.confluence.window.40", "Confluence: Can You Use Magic-Harp?");
        add("title.confluence.window.41", "Confluence: I don't know that-- aaaaa!");
        add("title.confluence.window.42", "Confluence: What's that purple spiked thing?");
        add("title.confluence.window.43", "Confluence: I wanna be the guide");
        add("title.confluence.window.44", "Confluence: Cthulhu is mad... and is missing an eye!");
        add("title.confluence.window.45", "Confluence: NOT THE BEES!!!");
        add("title.confluence.window.46", "Confluence: Legend of Maxx");
        add("title.confluence.window.47", "Confluence: Cult of MagicHarpWaaaa");
        add("title.confluence.window.48", "Confluence 2: Electric Boogaloo");
        add("title.confluence.window.49", "Confluence: Also try Terraria!");
        add("title.confluence.window.50", "Confluence: Also try Breath of the Wild!");
        add("title.confluence.window.51", "Confluence: Also try Stardew Valley!");
        add("title.confluence.window.52", "Confluence: Also try Core Keeper!");
        add("title.confluence.window.53", "Confluence: Also try Project Zomboid!");
        add("title.confluence.window.54", "Confluence: Also try RainbowBridge!");
        add("title.confluence.window.55", "Confluence: I just wanna know where the gold at?");
        add("title.confluence.window.56", "Confluence: Now with more ducks!");
        add("title.confluence.window.57", "Confluence: 1 + 1 = 10");
        add("title.confluence.window.58", "Confluence: Infinite Plantera");
        add("title.confluence.window.59", "Confluence: Now with microtransactions!");
        add("title.confluence.window.60", "Confluence: Built on Blockchain Technology");
        add("title.confluence.window.61", "Confluence: Now with even more Ocram!");
        add("title.confluence.window.62", "Confluence: Now with even more and more Ocram!");
        add("title.confluence.window.63", "Confluence: Installation service available for just $2.89 now!");
        add("title.confluence.window.64", "Confluence: Touch Grass Simulator");
        add("title.confluence.window.65", "Confluence: Don't dig up!");
        add("title.confluence.window.66", "Confluence: For the worthy!");
        add("title.confluence.window.67", "Confluence: Now with even more Ocram!");
        add("title.confluence.window.68", "Confluence: Shut Up and Dig Gaiden!");
        add("title.confluence.window.69", "Confluence: Also try Don't Starve!");
        add("title.confluence.window.70", "Confluence: Fusion Rise!");

        // Variant
        add("entity.minecraft.zombie.slime", "Slime Zombie");
        add("entity.minecraft.zombie.raincoat", "Raincoat Zombie");
        add("entity.minecraft.zombie.frozen", "Frost Zombie");
        add("entity.terra_entity.duck.0", "Wild Duck");
        add("entity.terra_entity.duck.1", "Duck");
        add("entity.terra_entity.demon_eye.dilated", "Dilated Demon Eye");
        add("entity.terra_entity.demon_eye.dilated_small", "Small Dilated Demon Eye");
        add("entity.terra_entity.demon_eye.sleepy", "Sleepy Demon Eye");
        add("entity.terra_entity.demon_eye.sleepy_big", "Big Sleepy Demon Eye");
        add("entity.terra_entity.demon_eye.purple", "Purple Demon Eye");
        add("entity.terra_entity.demon_eye.purple_big", "Big Purple Demon Eye");
        add("entity.terra_entity.demon_eye.normal", "Demon Eye");
        add("entity.terra_entity.demon_eye.normal_big", "Big Demon Eye");
        add("entity.terra_entity.demon_eye.green", "Green Demon Eye");
        add("entity.terra_entity.demon_eye.green_small", "Small Green Demon Eye");
        add("entity.terra_entity.demon_eye.cataract", "Cataract Demon Eye");
        add("entity.terra_entity.demon_eye.cataract_big", "Big Cataract Demon Eye");
        add("entity.terra_entity.worm.0", "Enchanted Nightcrawler");
        add("entity.terra_entity.worm.1", "Gold Worm");
        add("entity.terra_entity.worm.2", "Worm");
        add("entity.terra_entity.grasshopper.0", "Gold Grasshopper");
        add("entity.terra_entity.grasshopper.1", "Grasshopper");
        add("entity.terra_entity.ladybug.0", "Gold Ladybug");
        add("entity.terra_entity.ladybug.1", "Ladybug");
        add("entity.terra_entity.fealing.0", "Flying Spirit");
        add("entity.terra_entity.fairy.0", "Pink Fairy");
        add("entity.terra_entity.fairy.1", "Green Fairy");
        add("entity.terra_entity.fairy.2", "Blue Fairy");
        add("entity.terra_entity.scorpion.0", "Black Scorpion");
        add("entity.terra_entity.scorpion.1", "Scorpion");
        add("entity.terra_entity.squirrel.0", "Gray Squirrel");
        add("entity.terra_entity.squirrel.1", "Red Squirrel");
        add("entity.terra_entity.jewel_squirrel.0", "Amber Squirrel");
        add("entity.terra_entity.jewel_squirrel.1", "Gold Squirrel");
        add("entity.terra_entity.jewel_squirrel.2", "Amethyst Squirrel");
        add("entity.terra_entity.jewel_squirrel.3", "Diamond Squirrel");
        add("entity.terra_entity.jewel_squirrel.4", "Emerald Squirrel");
        add("entity.terra_entity.jewel_squirrel.5", "Ruby Squirrel");
        add("entity.terra_entity.jewel_squirrel.6", "Sapphire Squirrel");
        add("entity.terra_entity.jewel_squirrel.7", "Topaz Squirrel");
        add("entity.terra_entity.jewel_bunny.0", "Amber Bunny");
        add("entity.terra_entity.jewel_bunny.1", "Amethyst Bunny");
        add("entity.terra_entity.jewel_bunny.2", "Diamond Bunny");
        add("entity.terra_entity.jewel_bunny.3", "Emerald Bunny");
        add("entity.terra_entity.jewel_bunny.4", "Gold Bunny");
        add("entity.terra_entity.jewel_bunny.5", "Ruby Bunny");
        add("entity.terra_entity.jewel_bunny.6", "Sapphire Bunny");
        add("entity.terra_entity.jewel_bunny.7", "Topaz Bunny");
        add("entity.terra_entity.butterfly.0", "Gold Butterfly");
        add("entity.terra_entity.butterfly.1", "Julia Butterfly");
        add("entity.terra_entity.butterfly.2", "Monarch Butterfly");
        add("entity.terra_entity.butterfly.3", "Purple Emperor Butterfly");
        add("entity.terra_entity.butterfly.4", "Red Admiral Butterfly");
        add("entity.terra_entity.butterfly.5", "Sulphur Butterfly");
        add("entity.terra_entity.butterfly.6", "Tree Nymph Butterfly");
        add("entity.terra_entity.butterfly.7", "Ulysses Butterfly");
        add("entity.terra_entity.butterfly.8", "Zebra Swallowtail Butterfly");
        add("entity.terra_entity.dragonfly.0", "Black Dragonfly");
        add("entity.terra_entity.dragonfly.1", "Blue Dragonfly");
        add("entity.terra_entity.dragonfly.2", "Gold Dragonfly");
        add("entity.terra_entity.dragonfly.3", "Green Dragonfly");
        add("entity.terra_entity.dragonfly.4", "Orange Dragonfly");
        add("entity.terra_entity.dragonfly.5", "Red Dragonfly");
        add("entity.terra_entity.dragonfly.6", "Yellow Dragonfly");
        // Special world seeds
        add("title.confluence.secret_seeds_selection.empty", "Info");
        add("description.confluence.secret_seeds_selection.empty", "Please choose how your world will be built with the options above.");
        add("title.confluence.secret_seeds_selection.normal", "Normal");
        add("description.confluence.secret_seeds_selection.normal", "Welcome to Confluence! Embark on your adventures with the original Terraria experience and gameplay.");
        add("title.confluence.secret_seeds_selection.drunk_world", "Drunk");
        add("description.confluence.secret_seeds_selection.drunk_world", "Crimson & Corruption? Together? Madness. This is the land of world generation gone wild, leading to a Terraria adventure like no other! Go home worldgen, you are drunk!");
        add("title.confluence.secret_seeds_selection.not_the_bees", "Not The Bees");
        add("description.confluence.secret_seeds_selection.not_the_bees", "Enter a land oozing with syrupy honey, where the buzzing of wings and thr threat of venomous singers awaits around every turn! (Wicker helmet not included)");
        add("title.confluence.secret_seeds_selection.for_the_worthy", "For The Worthy");
        add("description.confluence.secret_seeds_selection.for_the_worthy", "Are you Worthy? This is a world of no mercy Forget what you know and sharpen your skills, lest you be weighed, measured, and found wanting...");
        add("title.confluence.secret_seeds_selection.celebrationmk10", "Celebration MK 10");
        add("description.confluence.secret_seeds_selection.celebrationmk10", "10 years of Terraria! Join the party in this colorful world of whimsy - who knows, maybe the team left some gifts behind for you to open.");
        add("title.confluence.secret_seeds_selection.the_constant", "The Constant");
        add("description.confluence.secret_seeds_selection.the_constant", "Bringing the Don't Starve Together adventure into Terraria, can you survive persistent hunger as you seek to save Minecraft? Oh, and don't forget to stay in the light!");
        add("title.confluence.secret_seeds_selection.dont_dig_up", "Remix");
        add("description.confluence.secret_seeds_selection.dont_dig_up", "A land where up is down and down is up. Turn Minecraft on its head in this inverted adventure. Will you be brave enough to explore The Up?");
        add("title.confluence.secret_seeds_selection.no_traps", "No Traps");
        add("description.confluence.secret_seeds_selection.no_traps", "Whoa, Traps. Ya hate 'em, right? I hate them myself! This world has definitely been stripped of any and all traps... we think. Maybe.");
        add("title.confluence.secret_seeds_selection.get_fixed_boi", "Zenith");
        add("description.confluence.secret_seeds_selection.get_fixed_boi", "For the Worthy wasn't enough? Featuring aspects of every other world seed combined with some brand new surprises, this world is here to break you. Get Fixed, boi!");
        add("title.confluence.secret_seeds_selection.skyblock", "Skyblock");
        add("description.confluence.secret_seeds_selection.skyblock", "Enter a realm floating in the sky. With little to start with, you must build up your world from humble beginnings.");
        add("title.confluence.secret_seeds_selection.secret_seed", "Secret Seed");
        add("description.confluence.secret_seeds_selection.secret_seed", "More secret worlds, parallel timelines, and chaotic journeys await your exploration.");
        add("description.confluence.secret_seeds_selection.boulder_world", "A world of more secrets, parallel timelines, and a disorienting journey awaits your exploration.");
        add("title.confluence.secret_seeds_selection.boulder_world", "Boulder World");
        add("description.confluence.secret_seeds_selection.boulder_world", "You know what? The world is actually one giant boulder. So everything in the world is a boulder.");
        add("title.confluence.secret_seeds_selection.really_small", "Tiny Me");
        add("description.confluence.secret_seeds_selection.really_small", "I get all the reasons, but why is this bird so big?");
        add("title.confluence.secret_seeds_selection.too_easy", "Too Easy");
        add("description.confluence.secret_seeds_selection.too_easy", "So, you have elected the way of pain... (World starts in Hardmode)");
        add("title.confluence.secret_seeds_selection.never_sleep", "Never Sleep");
        add("description.confluence.secret_seeds_selection.never_sleep", "Sleeping is a waste of time! We shall never sleep! (Beds cannot be used)");

        add("mural.dungeon.ebony_mural", "Corrosive Worm");
        add("mural.dungeon.crimson_mural", "Mind Rend");

        // Soul Skill
        add("confluence.soul_skill.soul_surge.name", "Soul Surge");
        add("confluence.soul_skill.soul_mark.name", "Soul Mark");
        add("confluence.soul_skill.star_call.name", "Star Call");
        add("confluence.soul_skill.spirit_surge.name", "Spirit Surge");
        add("confluence.soul_skill.surge_blast.name", "Surge Blast");
        add("confluence.soul_skill.soul_drain.name", "Soul Drain");
        add("confluence.soul_skill.spirit_trigger.name", "Spirit Trigger");
        add("confluence.soul_skill.soul_plunder.name", "Soul Plunder");
        add("confluence.soul_skill.confuse_spores.name", "Confuse Spores");
        add("confluence.soul_skill.karma_flame.name", "Karma Flame");
        add("confluence.soul_skill.enhanced_soul.name", "Enhanced Soul");
        add("confluence.soul_skill.empowered_surge.name", "Empowered Surge");
        add("confluence.soul_skill.enhanced_lure.name", "Enhanced Lure");
        add("confluence.soul_skill.profane_soul.name", "Profane Soul");
        add("confluence.soul_skill.star_link.name", "Star Link");
        add("confluence.soul_skill.star_reversal.name", "Star Reversal");
        add("confluence.soul_skill.blood_rage.name", "Blood Rage");
        add("confluence.soul_skill.boiling_blood.name", "Boiling Blood");
        add("confluence.soul_skill.soul_lure.name", "Soul Lure");
        add("confluence.soul_skill.lure_surge.name", "Lure Surge");
        add("confluence.soul_skill.law_of_nature.name", "Law of Nature");
        add("confluence.soul_skill.natures_wrath.name", "Nature's Wrath");

        addAll(ChestBlocks.BLOCKS);
        addAll(CrateBlocks.BLOCKS);
        addAll(DecorativeBlocks.BLOCKS);
        addAll(FunctionalBlocks.BLOCKS);
        addAll(ModBlocks.BLOCKS);
        addAll(NatureBlocks.BLOCKS);
        addAll(OreBlocks.BLOCKS);
        addAll(PotBlocks.BLOCKS);
        addAll(StatueBlocks.BLOCKS);

        addAll(AccessoryItems.ITEMS);
        addAll(ArmorItems.ITEMS);
        addAll(ArrowItems.ITEMS);
        addAll(AxeItems.ITEMS);
        addAll(BaitItems.ITEMS);
        addAll(BoatItems.BOAT_ITEMS);
        addAll(BoatItems.CHEST_BOAT_ITEMS);
        addAll(BowItems.ITEMS);
        addAll(ChainsawItems.ITEMS);
        addAll(ConsumableItems.ITEMS);
        addAll(CrossbowItems.ITEMS);
        addAll(DrillItems.ITEMS);
        addAll(FishingPoleItems.ITEMS);
        addAll(FlailItems.ITEMS);
        addAll(FoodItems.ITEMS);
        addAll(GardenShearsItems.ITEMS);
        addAll(GunItems.ITEMS);
        addAll(HamaxeItems.ITEMS);
        addAll(HoeShovelItems.ITEMS);
        addAll(HammerItems.ITEMS);
        addAll(HoeItems.ITEMS);
        addAll(HookItems.ITEMS);
        addAll(IconItems.ITEMS);
        addAll(LanceItems.ITEMS);
        addAll(LightPetItems.ITEMS);
        addAll(ManaWeaponItems.ITEMS);
        addAll(MaterialItems.ITEMS);
        addAll(MinecartItems.ITEMS);
        addAll(ModItems.ITEMS);
        addAll(ModItems.HIDDEN);
        addAll(ModItems.BLOCK_ITEMS);
        addAll(PaintItems.ITEMS);
        addAll(PickaxeAxeItems.ITEMS);
        addAll(PickaxeItems.ITEMS);
        addAll(PotionItems.ITEMS);
        addAll(QuestedFishes.ITEMS);
        addAll(ShovelItems.ITEMS);
        addAll(SwordItems.ITEMS);
        addAll(SpearItems.ITEMS);
        addAll(ToolItems.ITEMS);
        addAll(TreasureBagItems.ITEMS);
        addAll(VanityArmorItems.ITEMS);

        ModEffects.EFFECTS.getEntries().forEach(effect -> add(effect.get(), LibUtils.toTitleCase(effect.getId().getPath())));
        ModEntities.ENTITIES.getEntries().forEach(entity -> add(entity.get(), LibUtils.toTitleCase(entity.getId().getPath())));

        add(QuestedFishes.CAPN_TUNABEARD.get(), "Cap'n Tunabeard");
        add(FoodItems.PINA_COLADA.get(), "Piña Colada");
        addPotion(PotionItems.ALE.get(), "Minor improvements to melee stats & lowered defense'Down the hatch!'");
        addPotion(PotionItems.LESSER_HEALING_POTION.get(), "Restores 10 health");
        addPotion(PotionItems.HEALING_POTION.get(), "Restores 20 health");
        addPotion(PotionItems.RESTORATION_POTION.get(), "Restores 18 health");
        addPotion(PotionItems.GREATER_HEALING_POTION.get(), "Restores 40 health");
        addPotion(PotionItems.SUPER_HEALING_POTION.get(), "Restores 60 health");
        addPotion(PotionItems.LESSER_MANA_POTION.get(), "Restores 50 mana");
        addPotion(PotionItems.MANA_POTION.get(), "Restores 100 mana");
        addPotion(PotionItems.GREATER_MANA_POTION.get(), "Restores 200 mana");
        addPotion(PotionItems.SUPER_MANA_POTION.get(), "Restores 300 mana");
        addPotion(PotionItems.GRAVITATION_POTION.get(), "Press UP to reverse gravity");
        addPotion(PotionItems.SHINE_POTION.get(), "Emitting light");
        addPotion(PotionItems.IRON_SKIN_POTION.get(), "Increases defense by 4");
        addPotion(PotionItems.WRATH_POTION.get(), "10% increased damage");
        addPotion(PotionItems.TITAN_POTION.get(), "Increases knockback");
        addPotion(PotionItems.BUILDER_POTION.get(), "Increased placement speed and range");
        addPotion(PotionItems.ENDURANCE_POTION.get(), "10% reduced damage");
        addPotion(PotionItems.INFERNO_POTION.get(), "Nearby enemies are ignited");
        addPotion(PotionItems.LIFEFORCE_POTION.get(), "20% increased max life");
        addPotion(PotionItems.FISHING_POTION.get(), "Increases fishing power by 15");
        addPotion(PotionItems.RAGE_POTION.get(), "10% increased critical chance");
        addPotion(PotionItems.MANA_REGENERATION_POTION.get(), "Increased mana regeneration");
        addPotion(PotionItems.THORNS_POTION.get(), "Attacker also take damage");
        addPotion(PotionItems.MAGIC_POWER_POTION.get(), "20% increased magic damage");
        addPotion(PotionItems.OBSIDIAN_SKIN_POTION.get(), "Immune to lava");
        addPotion(PotionItems.LESSER_LUCK_POTION.get(), "Increases the Luck of the user");
        addPotion(PotionItems.LUCK_POTION.get(), "Increases the Luck of the user");
        addPotion(PotionItems.GREATER_LUCK_POTION.get(), "Increases the Luck of the user");
        addPotion(PotionItems.LOVE_POTION.get(), "Throw this to make someone fall in love(or enables animal breeding)");
        addPotion(PotionItems.SWIFTNESS_POTION.get(), "25% increased movement speed");
        addPotion(PotionItems.REGENERATION_POTION.get(), "Provides life regeneration");
        addPotion(PotionItems.FLIPPER_POTION.get(), "Lets you move swiftly in liquids");
        addPotion(PotionItems.ARCHERY_POTION.get(), "10% increased bow damage and 20% increased arrow speed");
        addPotion(PotionItems.HEART_REACH_POTION.get(), "Increased heart pickup range");
        addPotion(PotionItems.GILLS_POTION.get(), "Breathe water instead of air");
        addPotion(PotionItems.INVISIBILITY_POTION.get(), "Grants invisibility and lowers the spawn rate of enemies");
        addPotion(PotionItems.WORMHOLE_POTION.get(), "Teleports the player to a teammate when they click their icon on the map.");
        addPotion(PotionItems.MINING_POTION.get(), "Increases mining speed by 25%");
        addPotion(PotionItems.RECALL_POTION.get(), "Teleports you home");
        addPotion(PotionItems.NIGHT_OWL_POTION.get(), "Grants night vision");
        addPotion(PotionItems.WATER_WALKING_POTION.get(), "Allows the ability to walk on water");
        addPotion(PotionItems.FEATHERFALL_POTION.get(), "Slows falling speed");
        addPotion(PotionItems.RANDOM_TELEPORT_POTION.get(), "Teleports you to a random location");
        addPotion(PotionItems.SPELUNKER_POTION.get(), "Shows the location of treasure and ore");
        addPotion(PotionItems.DANGERSENSE_POTION.get(), "Allows you to see nearby danger sources");
        addPotion(PotionItems.HUNTER_POTION.get(), "Shows the location of enemies");
        addPotion(PotionItems.CRATE_POTION.get(), "Increases chance to get a crate");
        addPotion(PotionItems.CHAOS_POTION.get(), "Grants random effects");
        addPotion(PotionItems.STINK_POTION.get(), "Throw this to make someone smell terrible");
        addPotion(PotionItems.AMMO_RESERVATION_POTION.get(), "20% chance to save ammo");
        addPotion(PotionItems.SUMMONING_POTION.get(), "Increases your max number of minions by 1");
        addPotion(PotionItems.SHIMMER_POTION.get(), "Grants the Shimmer effect");
        addPotion(PotionItems.STRANGE_BREW.get(), "Restores 14–24 health");
        addPotion(PotionItems.FLASK_OF_FIRE.get(), "Melee and Whip attacks set enemies on fire");
        addPotion(PotionItems.FLASK_OF_GOLD.get(), "Melee and Whip attacks make enemies drop more gold");
        addPotion(PotionItems.EGGNOG.get(), "Restores 16 health");
        addPotion(PotionItems.BATTLE_POTION.get(), "Increases enemy spawn rate");
        addPotion(PotionItems.CALMING_POTION.get(), "Decreases enemy spawn rate");
        addPotion(PotionItems.SATIETY_POTION.get(), "Slows hunger depletion");

        addEffect(ModEffects.MANA_SICKNESS.get(), "Magic damage reduced");
        addEffect(ModEffects.SHINE.get(), "Emitting light");
        addEffect(ModEffects.SHIMMER.get(), "You are ethereal!");
        addEffect(ModEffects.EXQUISITELY_STUFFED.get(), "All stats increased");
        addEffect(ModEffects.IRON_SKIN.get(), "Increases defense by 4");
        addEffect(ModEffects.ENDURANCE.get(), "10% reduced damage");
        addEffect(ModEffects.INFERNO.get(), "Nearby enemies are ignited");
        addEffect(ModEffects.LIFE_FORCE.get(), "20% increased max life");
        addEffect(ModEffects.THORNS.get(), "Attackers also take damage");
        addEffect(ModEffects.TITAN.get(), "Increases knockback");
        addEffect(ModEffects.WRATH.get(), "10% increased damage");
        addEffect(ModEffects.BUILDER.get(), "Increased placement speed and range");
        addEffect(ModEffects.BLEEDING.get(), "Cannot regenerate life");
        addEffect(ModEffects.SILENCED.get(), "Cannot use items that require mana");
        addEffect(ModEffects.CURSED.get(), "Cannot use any items");
        addEffect(ModEffects.WITHERED_ARMOR.get(), "Your armor is lowered! Defense reduced by half");
        addEffect(ModEffects.ICHOR.get(), "Reduced defense");
        addEffect(ModEffects.POTION_SICKNESS.get(), "Cannot consume anymore healing items");
        addEffect(ModEffects.BLOOD_BUTCHERED.get(), "Losing life");
        addEffect(ModEffects.TENTACLE_SPIKES.get(), "Losing life");
        addEffect(ModEffects.BROKEN_ARMOR.get(), "Defense is cut in half");
        addEffect(ModEffects.STONED.get(), "You are completely petrified!");
        addEffect(ModEffects.CRATE.get(), "Greater chance of fishing up a crate");
        addEffect(ModEffects.ACID_VENOM.get(), "Losing life; Cannot regenerate life");
        addEffect(ModEffects.CURSED_INFERNO.get(), "losing life");
        addEffect(ModEffects.RAGE.get(), "10% increased critical chance");
        addEffect(ModEffects.FISHING.get(), "Increased fishing power");
        addEffect(ModEffects.LUCK_EFFECT.get(), "You are feeling pretty lucky");
        addEffect(ModEffects.MANA_REGENERATION.get(), "Increased mana regeneration");
        addEffect(ModEffects.STAR_IN_A_BOTTLE.get(), "Increased mana regeneration");
        addEffect(ModEffects.WATER_WALKING.get(), "Can walk on liquids.");
        addEffect(ModEffects.MAGIC_POWER.get(), "20% increased magic damage");
        addEffect(ModEffects.FLIPPER.get(), "Move like normal in water");
        addEffect(ModEffects.SPELUNKER.get(), "Shows the location of treasure and ore; press [%s] for detailed information");
        addEffect(ModEffects.HUNTER.get(), "Shows the location of enemies; press [%s] for detailed information");
        addEffect(ModEffects.DANGER_SENSE.get(), "You can see nearby hazards");
        addEffect(ModEffects.FROZEN.get(), "You can't move!");
        addEffect(ModEffects.STINKY.get(), "You smell terrible");
        addEffect(ModEffects.THE_BAST_DEFENSE.get(), "Defense is increased by 5");
        addEffect(ModEffects.SHARPENED.get(), "Melee weapons have armor penetration");
        addEffect(ModEffects.AMMO_BOX.get(), "20% chance to save ammo");
        addEffect(ModEffects.ARCHERY.get(), "10% increased bow damage and 20% increased arrow speed");
        addEffect(ModEffects.HEART_REACH.get(), "Increased heart pickup range");
        addEffect(ModEffects.OBSIDIAN_SKIN.get(), "Immune to lava");
        addEffect(ModEffects.COZY_FIRE.get(), "Life regen is slightly increased");
        addEffect(ModEffects.HEART_LANTERN.get(), "Life regen is increased ");
        addEffect(ModEffects.BEWITCHED.get(), "Increased max number of minions");
        addEffect(ModEffects.HUNGER_DELAYED.get(), "Slows hunger depletion rate");
        addEffect(ModEffects.CHOKING.get(), "You need water!");
        addEffect(ModEffects.DELICIOUS.get(), "Doubles positive effects, halves negative effects");
        addEffect(ModEffects.LOVE.get(), "You are in love!");
        addEffect(ModEffects.MIDAS.get(), "Drop more money on death");
        addEffect(ModEffects.TIPSY.get(), "Increased melee abilities, lowered defense");
        addEffect(ModEffects.CLAIRVOYANCE.get(), "Magic powers are increased");
        addEffect(ModEffects.HOLY_PROTECTION.get(), "You will dodge the next attack");
        addEffect(ModEffects.TITANIUM_BARRIER.get(), "Defensive shards surround you");
        addEffect(ModEffects.WEAPON_IMBUE_FIRE.get(), "Melee attacks set enemies on fire");
        addEffect(ModEffects.WEAPON_IMBUE_GOLD.get(), "Melee attacks make enemies drop more gold");
        addEffect(ModEffects.FROSTBITE.get(), "Slowly losing life");
        addEffect(ModEffects.SHADOWFLAME.get(), "Losing life");
        addEffect(ModEffects.WATER_CANDLE.get(), "Increased monster spawn rate");
        addEffect(ModEffects.PEACE_CANDLE.get(), "Decreased monster spawn rate");
        addEffect(ModEffects.BATTLE.get(), "Increased enemy spawn rate");
        addEffect(ModEffects.CALM.get(), "Decreased enemy spawn rate");
        addEffect(ModEffects.HAPPY.get(), "Movement speed increased and monster spawns reduced");
        addEffect(ModEffects.ENEMY_BANNER.get(), "Increased damage and defense from the following: %s");
        addEffect(ModEffects.SUMMONING.get(), "Increased your max number of minions by 1");
        addEffect(ModEffects.AROMATIC_SATIATION.get(), "Continuous response to hunger and satiety");

        addEffect(TCEffects.CEREBRAL_MINDTRICK.get(), "Increased critical chance");
        addEffect(TCEffects.HONEY.get(), "Life regeneration is increased");
        addEffect(TCEffects.CONFUSED.get(), "Movement is reversed");
        addEffect(TCEffects.GRAVITATION.get(), "Press UP to reverse gravity");
        addEffect(TCEffects.PALADINS_SHIELD.get(), "25% of damage taken will be redirected to another player");

        addEffect(TEEffects.DEMONIC_THOUGHTS.get(), "Being inflicted with Demonic Thoughts again spawns Eater of Souls");
        addEffect(TEEffects.SUMMON_FOCUS.get(), "Minions deal additional damage");
        addEffect(TEEffects.HELLFIRE.get(), "Losing life");
        addEffect(TEEffects.FROST_BURN.get(), "Losing life; Cannot regenerate life");
        addEffect(TEEffects.CRIMSON_STORM.get(), "You are trapped in the storm, there is no escape.");
        addEffect(TEEffects.HORRIFIED.get(), "You have seen something nasty, there is no escape.");
        addEffect(TEEffects.THE_TONGUE.get(), "You are being sucked into the mouth");
        addEffect(TEEffects.SCARED.get(), "Like a bird startled by the sound of a bow, fleeing in all directions");

        add("item.confluence.spawn_eggs", "%s Spawn Egg");

        // TouhouLittleMaid
        add("task.confluence.use_life_crystal", "Use Life Crystal");
        add("task.confluence.use_life_crystal.desc", "Mail will use life crystal to heal herself");
        add("task.confluence.use_life_crystal.condition.has_life_crystal", "Mainhand holds life crystal");

        PonderHelper.addTranslateKeys(this::add, true);
        WaystonesHelper.addTranslateKeys((block, s) -> add(Util.makeDescriptionId("block", block.getId()), s), true);
        CreateHelper.addTranslateKeys((item, s) -> add(Util.makeDescriptionId("item", item.getId()), s), true);
    }

    private void addDefaultRegistryTranslations(HolderLookup.RegistryLookup<?> dimensions, String dimensionsPath) {
        dimensions.listElements().forEach(dimension -> {
            var key = dimension.getKey();
            if (key == null) return;
            ResourceLocation location = key.location();
            add(String.format("%s.%s", dimensionsPath, location.toLanguageKey()), formatLocation(location));
        });
    }

    @Override
    public void add(String key, String value) {
        if (!((LanguageProviderAccessor) this).getData().containsKey(key)) {
            super.add(key, value);
        }
    }

    private void addPotion(Item potion, String tooltip) {
        add("tooltip." + potion.getDescriptionId() + ".0", tooltip);
    }

    private void addEffect(MobEffect effect, String tooltip) {
        add("tooltip." + effect.getDescriptionId() + ".0", tooltip);
    }

    private String formatLocation(ResourceLocation location) {
        return RecipeDrawerUtils.formatLocationPath(location);
    }

    private String formatString(String name) {
        return RecipeDrawerUtils.formatString(name);
    }

    private void addAll(DeferredRegister.Items register) {
        register.getEntries().forEach(item -> add(item.get(), LibUtils.toTitleCase(item.getId().getPath())));
    }

    private void addAll(DeferredRegister.Blocks register) {
        register.getEntries().forEach(block -> add(block.get(), LibUtils.toTitleCase(block.getId().getPath())));
    }
}
