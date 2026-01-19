package org.confluence.mod.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.Confluence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CommonConfigs {
    public static BooleanValue DROP_MONEY;
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

    public static BooleanValue FLETCHING_MENU;
    public static BooleanValue SHIMMER_DECOMPOSE;
    public static BooleanValue ALTAR_TIPS;

    public static BooleanValue DO_FALLING_STAR_SPAWNING;
    public static IntValue FALLING_STAR_INTERVAL;
    public static BooleanValue DO_NPC_SPAWNING;
    public static IntValue NPC_SPAWN_INTERVAL;
    public static BooleanValue DO_METEORITE_SPAWNING;
    public static BooleanValue BROADCAST_NPC_MSG;

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

    public static DoubleValue METEOR_SHOWER_EVENT_FALLING_STAR_SPAWN_SPEED_MULTIPLIER;
    public static IntValue METEOR_SHOWER_EVENT_FREQUENCY;
    public static IntValue METEOR_SHOWER_EVENT_FREQUENCY_CELEBRATIONMK10;

    public static Set<ResourceKey<Item>> ammoSlotsItemBlackList = Set.of(Confluence.asResourceKey(Registries.ITEM, "falling_star"));
    public static Set<TagKey<Item>> ammoSlotsTagBlackList = Set.of(Tags.Items.SEEDS);

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
    }

    public static void register(ModContainer container) {
        Builder builder = new Builder();
        {
            builder.push("Gameplay");
            DROP_MONEY = builder.define("dropsMoney", true);
            AUTO_STACK_GELS_COLOR = builder.define("autoStackGelsColor", true);
            RETURN_POTION_GLASS_BOTTLE = builder.define("returnPotionGlassBottle", false);
            RIGHT_CLICK_RIDE_MINECART = builder.define("rightClickRideMinecart", true);
            ANNOUNCEMENT_BOX_DISTANCE = builder.defineInRange("announcementBoxDistance", 128, 0, Integer.MAX_VALUE);
            ALERT_PLAYER_IN_DUNGEON = builder.define("alertPlayerDungeon", false);
            STAR_PHASE = builder.define("starPhase", false);
            AMMO_SLOTS_BLACKLIST = builder.defineListAllowEmpty("ammoSlotsBlacklist", () -> List.of("confluence:falling_star", "#c:seeds"), () -> "[#]namespace:path", o -> {
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
            DO_METEORITE_SPAWNING = builder.define("doMeteoriteSpawning", true);
            builder.pop();
        }
        {
            builder.push("PlayerDeath");
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
//            {
//                builder.push("SlimeRain");
//
//                builder.pop();
//            }
//            {
//                builder.push("BloodMoon");
//
//                builder.pop();
//            }
//            {
//                builder.push("GoblinArmy");
//
//                builder.pop();
//            }
            {
                builder.push("MeteorShower");
                METEOR_SHOWER_EVENT_FALLING_STAR_SPAWN_SPEED_MULTIPLIER = builder.defineInRange("meteorShowerEventFallingStarSpawnSpeedMultiplier", 3.0, 2.0, 4.0);
                METEOR_SHOWER_EVENT_FREQUENCY = builder.defineInRange("meteorShowerEventFrequency", 10, 1, Integer.MAX_VALUE);
                METEOR_SHOWER_EVENT_FREQUENCY_CELEBRATIONMK10 = builder.defineInRange("meteorShowerEventFrequencyCelebrationMK10", 5, 1, Integer.MAX_VALUE);
                builder.pop();
            }
            builder.pop();
        }
        container.registerConfig(ModConfig.Type.COMMON, builder.build());
    }
}
