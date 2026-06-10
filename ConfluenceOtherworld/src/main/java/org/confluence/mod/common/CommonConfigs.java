package org.confluence.mod.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.network.s2c.DragonChargePlayerConfigPacketS2C;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CommonConfigs {
    public static BooleanValue ENEMY_DROPS_MONEY;
    public static BooleanValue AUTO_STACK_GELS_COLOR;
    public static BooleanValue RETURN_POTION_GLASS_BOTTLE;
    public static BooleanValue RIGHT_CLICK_RIDE_MINECART;
    public static IntValue ANNOUNCEMENT_BOX_DISTANCE;
    public static BooleanValue ALERT_PLAYER_IN_DUNGEON;
    public static BooleanValue STAR_PHASE;
    private static ConfigValue<List<? extends String>> AMMO_SLOTS_BLACKLIST;
    public static BooleanValue TERRA_STYLE_EXPLOSION;
    public static BooleanValue TERRA_STYLE_FIRE_DAMAGE;
    public static BooleanValue NPC_INVULNERABLE_TO_PLAYER;
    public static BooleanValue ALLOWS_VANILLA_ENTITIES_TO_PERFORM_STAGE_ATTRIBUTES;
    private static BooleanValue DRAGON_CHARGE_PLAYER;
    public static BooleanValue STOP_ASK_FOR_SOFTCORE;
    public static BooleanValue TERRA_STYLE_LIGHTNING_BOLT;
    public static IntValue TERRA_STYLE_LIGHTNING_BOLT_FREQUENCY_MULTIPLIER;

    public static BooleanValue FLETCHING_MENU;
    public static BooleanValue SHIMMER_DECOMPOSE;
    public static BooleanValue ALTAR_TIPS;

    public static BooleanValue DO_FALLING_STAR_SPAWNING;
    public static IntValue FALLING_STAR_INTERVAL;

    public static BooleanValue DO_NPC_SPAWNING;
    public static IntValue NPC_SPAWN_INTERVAL;
    public static BooleanValue BROADCAST_NPC_MSG;

    public static BooleanValue EYE_OF_CTHULHU_NATURE_SPAWNING;
    public static BooleanValue DEERCLOPS_NATURE_SPAWNING;

    public static BooleanValue DO_METEORITE_SPAWNING;

    public static BooleanValue PLAYER_DROPS_MONEY;
    public static BooleanValue SHOW_MONEY_DROPS;
    public static BooleanValue DROPS_TOMBSTONE;
    public static IntValue DEFAULT_RESPAWN_TIME_MIN;
    public static IntValue DEFAULT_RESPAWN_TIME_MAX;
    public static IntValue BOSS_RESPAWN_TIME_MIN;
    public static IntValue BOSS_RESPAWN_TIME_MAX;

    public static BooleanValue WRAPPED_CRIMSON_HEART;
    public static BooleanValue INSTANTLY_HARDMODE_CONVERSION;

    public static BooleanValue CONVERT_ARS_NOUVEAU_MANA;
    public static BooleanValue CONVERT_IRONS_SPELL_MANA;
    public static BooleanValue FTB_CHUNKS_WORMHOLE_POTION;
    public static BooleanValue WAYSTONES_PYLON_NON_COST;

    public static IntValue SLIME_RAIN_EVENT_MAX_ENEMIES_BASE;
    public static IntValue SLIME_RAIN_EVENT_MAX_ENEMIES_PER_PLAYER;
    public static DoubleValue SLIME_RAIN_EVENT_SPAWN_INTERVAL_FACTOR;
    public static IntValue SLIME_RAIN_EVENT_SPAWN_GROUP_SIZE;
    public static IntValue SLIME_RAIN_EVENT_KING_SLIME_SPAWN_REQUIRED_KILL_COUNT;
    public static IntValue SLIME_RAIN_EVENT_REQUIRED_PLAYER_MAX_HEALTH;
    public static IntValue SLIME_RAIN_EVENT_REQUIRED_PLAYER_ARMOR;
    public static IntValue SLIME_RAIN_EVENT_FREQUENCY;

    public static IntValue BLOOD_MOON_EVENT_MAX_ENEMIES_BASE;
    public static IntValue BLOOD_MOON_EVENT_MAX_ENEMIES_PER_PLAYER;
    public static DoubleValue BLOOD_MOON_EVENT_SPAWN_ENEMIES_INTERVAL_FACTOR;
    public static IntValue BLOOD_MOON_EVENT_REQUIRED_PLAYER_MAX_HEALTH;
    public static IntValue BLOOD_MOON_EVENT_REQUIRED_PLAYER_ARMOR;
    public static IntValue BLOOD_MOON_EVENT_INVERT_CHANCE;

    public static IntValue GOBLIN_ARMY_EVENT_MAX_ENEMIES_BASE;
    public static IntValue GOBLIN_ARMY_EVENT_MAX_ENEMIES_PER_PLAYER;
    public static DoubleValue GOBLIN_ARMY_EVENT_SPAWN_ENEMIES_INTERVAL_FACTOR;
    public static IntValue GOBLIN_ARMY_EVENT_REQUIRED_PLAYER_MAX_HEALTH;
    public static IntValue GOBLIN_ARMY_EVENT_REQUIRED_PLAYER_ARMOR;
    public static IntValue GOBLIN_ARMY_EVENT_INVERT_CHANCE;
    public static IntValue GOBLIN_ARMY_EVENT_DEFEATED_INVERT_CHANCE;
    public static IntValue GOBLIN_ARMY_EVENT_HARDMODE_INVERT_CHANCE;
    public static IntValue GOBLIN_ARMY_EVENT_HARDMODE_DEFEATED_INVERT_CHANCE;
    public static IntValue GOBLIN_ARMY_EVENT_REQUIRED_KILL_COUNT_BASE;
    public static IntValue GOBLIN_ARMY_EVENT_REQUIRED_KILL_COUNT_PER_PLAYER;

    public static DoubleValue METEOR_SHOWER_EVENT_FALLING_STAR_SPAWN_SPEED_MULTIPLIER;
    public static IntValue METEOR_SHOWER_EVENT_FREQUENCY;
    public static IntValue METEOR_SHOWER_EVENT_CELEBRATIONMK10_FREQUENCY;
    public static IntValue METEOR_SHOWER_EVENT_MAX_ENCHANTED_NIGHTCRAWLERS_BASE;
    public static IntValue METEOR_SHOWER_EVENT_MAX_ENCHANTED_NIGHTCRAWLERS_PER_PLAYER;
    public static IntValue METEOR_SHOWER_EVENT_SPAWN_ENCHANTED_NIGHTCRAWLERS_INTERVAL_FACTOR;

    public static Set<ResourceKey<Item>> ammoSlotsItemBlackList = Set.of(Confluence.asResourceKey(Registries.ITEM, "falling_star"));
    public static Set<TagKey<Item>> ammoSlotsTagBlackList = Set.of(PortTags.Items.SEEDS);

    private static boolean isSingleplayerOwner = true;
    private static boolean dragonChargePlayer = true;

    public static void onLoad() {
        Set<ResourceKey<Item>> a = new HashSet<>();
        Set<TagKey<Item>> b = new HashSet<>();
        for (String s : AMMO_SLOTS_BLACKLIST.get()) {
            if (s.startsWith("#")) {
                b.add(TagKey.create(Registries.ITEM, ResourceLocation.parse(s.substring(1))));
            } else {
                a.add(ResourceKey.create(Registries.ITEM, ResourceLocation.parse(s)));
            }
        }
        ammoSlotsItemBlackList = a;
        ammoSlotsTagBlackList = b;

        if (isSingleplayerOwner) {
            dragonChargePlayer = DRAGON_CHARGE_PLAYER.get();
            DragonChargePlayerConfigPacketS2C.sendToAll();
        }
    }

    public static void handleDragonChargePlayer(boolean enabled) {
        isSingleplayerOwner = false;
        dragonChargePlayer = enabled;
    }

    public static void reset() {
        isSingleplayerOwner = true;
        dragonChargePlayer = DRAGON_CHARGE_PLAYER.get();
    }

    public static boolean isDragonChargePlayer() {
        return dragonChargePlayer;
    }

    public static void register(FMLJavaModLoadingContext context) {
        Builder builder = new Builder();
        {
            builder.push("Gameplay");
            ENEMY_DROPS_MONEY = builder.define("enemyDropsMoney", true);
            AUTO_STACK_GELS_COLOR = builder.define("autoStackGelsColor", true);
            RETURN_POTION_GLASS_BOTTLE = builder.define("returnPotionGlassBottle", false);
            RIGHT_CLICK_RIDE_MINECART = builder.define("rightClickRideMinecart", true);
            ANNOUNCEMENT_BOX_DISTANCE = builder.defineInRange("announcementBoxDistance", 128, 0, Integer.MAX_VALUE);
            ALERT_PLAYER_IN_DUNGEON = builder.define("alertPlayerDungeon", false);
            STAR_PHASE = builder.define("starPhase", false);
            AMMO_SLOTS_BLACKLIST = builder.defineListAllowEmpty("ammoSlotsBlacklist", () -> List.of("confluence:falling_star", "#c:seeds"), o -> {
                if (o instanceof String s) {
                    if (s.startsWith("#")) {
                        return ResourceLocation.tryParse(s.substring(1)) != null;
                    }
                    return ResourceLocation.tryParse(s) != null;
                }
                return false;
            });
            TERRA_STYLE_EXPLOSION = builder.define("terraStyleExplosion", true);
            TERRA_STYLE_FIRE_DAMAGE = builder.define("terraStyleFireDamage", true);
            NPC_INVULNERABLE_TO_PLAYER = builder.define("npcInvulnerableToPlayer", true);
            ALLOWS_VANILLA_ENTITIES_TO_PERFORM_STAGE_ATTRIBUTES = builder.define("allowsVanillaEntitiesToPerformStageAttributes", false);
            DRAGON_CHARGE_PLAYER = builder.define("dragonChargePlayer", true);
            STOP_ASK_FOR_SOFTCORE = builder.define("stopAskForSoftcore", false);
            {
                builder.push("LightningBolt");
                TERRA_STYLE_LIGHTNING_BOLT = builder.define("terraStyleLightningBolt", true);
                TERRA_STYLE_LIGHTNING_BOLT_FREQUENCY_MULTIPLIER = builder.defineInRange("terraStyleLightningBoltFrequencyMultiplier", 10, 1, 1000);
                builder.pop();
            }
            builder.pop();
        }
        {
            builder.push("Recipe");
            FLETCHING_MENU = builder.define("fletchingMenu", true);
            SHIMMER_DECOMPOSE = builder.define("shimmerDecompose", true);
            ALTAR_TIPS = builder.define("altarTips", true);
            builder.pop();
        }
        {
            builder.push("Spawning");
            {
                builder.push("Falling Star");
                DO_FALLING_STAR_SPAWNING = builder.define("doFallingStarSpawning", true);
                FALLING_STAR_INTERVAL = builder.defineInRange("fallingStarInterval", 2400, 20, 20000);
                builder.pop();
            }
            {
                builder.push("NPC");
                DO_NPC_SPAWNING = builder.define("doNPCSpawning", true);
                NPC_SPAWN_INTERVAL = builder.defineInRange("npcSpawnInterval", 2400, 20, 20000);
                BROADCAST_NPC_MSG = builder.define("broadcastNpcMsg", true);
                builder.pop();
            }
            {
                builder.push("Boss");
                EYE_OF_CTHULHU_NATURE_SPAWNING = builder.define("eyeOfCthulhuNatureSpawning", true);
                DEERCLOPS_NATURE_SPAWNING = builder.define("deerclopsNatureSpawning", true);
                builder.pop();
            }
            DO_METEORITE_SPAWNING = builder.define("doMeteoriteSpawning", true);
            builder.pop();
        }
        {
            builder.push("PlayerDeath");
            PLAYER_DROPS_MONEY = builder.define("playerDropsMoney", true);
            SHOW_MONEY_DROPS = builder.define("showMoneyDrops", true);
            DROPS_TOMBSTONE = builder.define("dropsTombstone", true);
            DEFAULT_RESPAWN_TIME_MIN = builder.defineInRange("defaultRespawnTimeMin", 3, 0, Integer.MAX_VALUE);
            DEFAULT_RESPAWN_TIME_MAX = builder.defineInRange("defaultRespawnTimeMax", 8, 0, Integer.MAX_VALUE);
            BOSS_RESPAWN_TIME_MIN = builder.defineInRange("bossRespawnTimeMin", 9, 0, Integer.MAX_VALUE);
            BOSS_RESPAWN_TIME_MAX = builder.defineInRange("bossRespawnTimeMax", 18, 0, Integer.MAX_VALUE);
            builder.pop();
        }
        {
            builder.push("WorldGeneration");
            WRAPPED_CRIMSON_HEART = builder.define("wrappedCrimsonHeart", false);
            INSTANTLY_HARDMODE_CONVERSION = builder.define("instantlyHardmodeConversion", false);
            builder.pop();
        }
        {
            builder.push("Compatibility");
            {
                builder.push("ArsNouveau");
                CONVERT_ARS_NOUVEAU_MANA = builder.define("convertArsNouveauMana", false);
                builder.pop();
            }
            {
                builder.push("IronsSpell");
                CONVERT_IRONS_SPELL_MANA = builder.define("convertIronsSpellMana", false);
                builder.pop();
            }
            {
                builder.push("FTB");
                FTB_CHUNKS_WORMHOLE_POTION = builder.define("ftbChunksWormholePotion", true);
                builder.pop();
            }
            {
                builder.push("Waystones");
                WAYSTONES_PYLON_NON_COST = builder.define("waystonesPylonNonCost", true);
                builder.pop();
            }
            builder.pop();
        }
        {
            builder.push("GameEvent");
            {
                builder.push("SlimeRain");
                SLIME_RAIN_EVENT_MAX_ENEMIES_BASE = builder.defineInRange("slimeRainEventMaxEnemiesBase", 25, 1, 1024);
                SLIME_RAIN_EVENT_MAX_ENEMIES_PER_PLAYER = builder.defineInRange("slimeRainEventPerPlayer", 25, 1, 1024);
                SLIME_RAIN_EVENT_SPAWN_INTERVAL_FACTOR = builder.defineInRange("slimeRainEventSpawnIntervalFactor", 1, 0.1, 10);
                SLIME_RAIN_EVENT_SPAWN_GROUP_SIZE = builder.defineInRange("slimeRainEventSpawnGroupSize", 4, 1, 1024);
                SLIME_RAIN_EVENT_KING_SLIME_SPAWN_REQUIRED_KILL_COUNT = builder.defineInRange("slimeRainEventKingSlimeSpawnRequiredKillCount", 150, 1, 1024);
                SLIME_RAIN_EVENT_REQUIRED_PLAYER_MAX_HEALTH = builder.defineInRange("slimeRainEventRequiredPlayerMaxHealth", 28, 0, 1024);
                SLIME_RAIN_EVENT_REQUIRED_PLAYER_ARMOR = builder.defineInRange("slimeRainEventRequiredPlayerArmor", 14, 0, 1024);
                SLIME_RAIN_EVENT_FREQUENCY = builder.defineInRange("slimeRainEventFrequency", 675000, 1, Integer.MAX_VALUE);
                builder.pop();
            }
            {
                builder.push("BloodMoon");
                BLOOD_MOON_EVENT_MAX_ENEMIES_BASE = builder.defineInRange("bloodMoonEventMaxEnemiesBase", 30, 1, 1024);
                BLOOD_MOON_EVENT_MAX_ENEMIES_PER_PLAYER = builder.defineInRange("bloodMoonEventMaxEnemiesPerPlayer", 30, 1, 1024);
                BLOOD_MOON_EVENT_SPAWN_ENEMIES_INTERVAL_FACTOR = builder.defineInRange("bloodMoonEventSpawnEnemiesIntervalFactor", 1.5, 0.1, 10);
                BLOOD_MOON_EVENT_REQUIRED_PLAYER_MAX_HEALTH = builder.defineInRange("bloodMoonEventRequiredPlayerMaxHealth", 24, 0, 1024);
                BLOOD_MOON_EVENT_REQUIRED_PLAYER_ARMOR = builder.defineInRange("bloodMoonEventRequiredPlayerArmor", 16, 0, 1024);
                BLOOD_MOON_EVENT_INVERT_CHANCE = builder.defineInRange("bloodMoonEventInvertChance", 14, 1, 1024);
                builder.pop();
            }
            {
                builder.push("GoblinArmy");
                GOBLIN_ARMY_EVENT_MAX_ENEMIES_BASE = builder.defineInRange("goblinArmyEventMaxEnemiesBase", 30, 1, 1024);
                GOBLIN_ARMY_EVENT_MAX_ENEMIES_PER_PLAYER = builder.defineInRange("goblinArmyEventMaxEnemiesPerPlayer", 30, 1, 1024);
                GOBLIN_ARMY_EVENT_SPAWN_ENEMIES_INTERVAL_FACTOR = builder.defineInRange("goblinArmyEventSpawnEnemiesIntervalFactor", 1.5, 0.1, 10);
                GOBLIN_ARMY_EVENT_REQUIRED_PLAYER_MAX_HEALTH = builder.defineInRange("goblinArmyEventRequiredPlayerMaxHealth", 24, 0, 1024);
                GOBLIN_ARMY_EVENT_REQUIRED_PLAYER_ARMOR = builder.defineInRange("goblinArmyEventRequiredPlayerArmor", 16, 0, 1024);
                GOBLIN_ARMY_EVENT_INVERT_CHANCE = builder.defineInRange("goblinArmyEventInvertChance", 3, 1, 1024);
                GOBLIN_ARMY_EVENT_DEFEATED_INVERT_CHANCE = builder.defineInRange("goblinArmyDefeatedEventInvertChance", 30, 1, 1024);
                GOBLIN_ARMY_EVENT_HARDMODE_INVERT_CHANCE = builder.defineInRange("goblinArmyHardmodeEventInvertChance", 3, 1, 1024);
                GOBLIN_ARMY_EVENT_HARDMODE_DEFEATED_INVERT_CHANCE = builder.defineInRange("goblinArmyEventHardmodeDefeatedInvertChance", 60, 1, 1024);
                GOBLIN_ARMY_EVENT_REQUIRED_KILL_COUNT_BASE = builder.defineInRange("goblinArmyEventRequiredKillCountBase", 80, 1, 1024);
                GOBLIN_ARMY_EVENT_REQUIRED_KILL_COUNT_PER_PLAYER = builder.defineInRange("goblinArmyEventRequiredKillCountPerPlayer", 40, 1, 1024);
                builder.pop();
            }
            {
                builder.push("MeteorShower");
                METEOR_SHOWER_EVENT_FALLING_STAR_SPAWN_SPEED_MULTIPLIER = builder.defineInRange("meteorShowerEventFallingStarSpawnSpeedMultiplier", 3.0, 2.0, 4.0);
                METEOR_SHOWER_EVENT_FREQUENCY = builder.defineInRange("meteorShowerEventFrequency", 10, 1, Integer.MAX_VALUE);
                METEOR_SHOWER_EVENT_CELEBRATIONMK10_FREQUENCY = builder.defineInRange("meteorShowerEventCelebrationMK10Frequency", 5, 1, Integer.MAX_VALUE);
                METEOR_SHOWER_EVENT_MAX_ENCHANTED_NIGHTCRAWLERS_BASE = builder.defineInRange("meteorShowerEventMaxEnchantedNightcrawlersBase", 1, 1, 1024);
                METEOR_SHOWER_EVENT_MAX_ENCHANTED_NIGHTCRAWLERS_PER_PLAYER = builder.defineInRange("meteorShowerEventMaxEnchantedNightcrawlersPerPlayer", 2, 1, 1024);
                METEOR_SHOWER_EVENT_SPAWN_ENCHANTED_NIGHTCRAWLERS_INTERVAL_FACTOR = builder.defineInRange("meteorShowerEventSpawnEnchantedNightcrawlersIntervalFactor", 20, 1, 1024);
                builder.pop();
            }
            builder.pop();
        }
        context.registerConfig(ModConfig.Type.COMMON, builder.build());
    }
}
