package org.confluence.mod.common;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public final class CommonConfigs {
    public static BooleanValue DROP_MONEY;
    public static BooleanValue AUTO_STACK_GELS_COLOR;
    public static BooleanValue RETURN_POTION_GLASS_BOTTLE;
    public static BooleanValue RIGHT_CLICK_RIDE_MINECART;
    public static IntValue ANNOUNCEMENT_BOX_DISTANCE;
    public static BooleanValue ALERT_PLAYER_IN_DUNGEON;
    public static BooleanValue STAR_PHASE;

    public static BooleanValue FLETCHING_MENU;
    public static BooleanValue SHIMMER_DECOMPOSE;
    public static BooleanValue BREWING_STAND_RECIPE;
    public static BooleanValue ALTAR_TIPS;

    public static BooleanValue DO_FALLING_STAR_SPAWNING;
    public static IntValue FALLING_STAR_INTERVAL;
    public static BooleanValue DO_NPC_SPAWNING;
    public static IntValue NPC_SPAWN_INTERVAL;
    public static BooleanValue DO_METEORITE_SPAWNING;

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
    public static BooleanValue XAEROS_MAP_WORMHOLE_POTION;

    public static void register(ModContainer container) {
        Builder BUILDER = new Builder();
        {
            BUILDER.push("Gameplay");
            DROP_MONEY = BUILDER.define("dropsMoney", true);
            AUTO_STACK_GELS_COLOR = BUILDER.define("autoStackGelsColor", true);
            RETURN_POTION_GLASS_BOTTLE = BUILDER.define("returnPotionGlassBottle", false);
            RIGHT_CLICK_RIDE_MINECART = BUILDER.define("rightClickRideMinecart", true);
            ANNOUNCEMENT_BOX_DISTANCE = BUILDER.defineInRange("announcementBoxDistance", 128, 0, Integer.MAX_VALUE);
            ALERT_PLAYER_IN_DUNGEON = BUILDER.define("alertPlayerDungeon", false);
            STAR_PHASE = BUILDER.define("starPhase", false);
            BUILDER.pop();
        }
        {
            BUILDER.push("Recipe");
            FLETCHING_MENU = BUILDER.define("fletchingMenu", true);
            SHIMMER_DECOMPOSE = BUILDER.define("shimmerDecompose", true);
            BREWING_STAND_RECIPE = BUILDER.define("brewingStandRecipe", false);
            ALTAR_TIPS = BUILDER.define("altarTips", true);
            BUILDER.pop();
        }
        {
            BUILDER.push("Spawning");
            {
                BUILDER.push("Falling Star");
                DO_FALLING_STAR_SPAWNING = BUILDER.define("doFallingStarSpawning", true);
                FALLING_STAR_INTERVAL = BUILDER.defineInRange("fallingStarInterval", 2400, 20, 20000);
                BUILDER.pop();
            }
            {
                BUILDER.push("NPC");
                DO_NPC_SPAWNING = BUILDER.define("doNPCSpawning", true);
                NPC_SPAWN_INTERVAL = BUILDER.defineInRange("npcSpawnInterval", 2400, 20, 20000);
                BUILDER.pop();
            }
            DO_METEORITE_SPAWNING = BUILDER.define("doMeteoriteSpawning", true);
            BUILDER.pop();
        }
        {
            BUILDER.push("PlayerDeath");
            SHOW_MONEY_DROPS = BUILDER.define("showMoneyDrops", true);
            DROPS_TOMBSTONE = BUILDER.define("dropsTombstone", true);
            DEFAULT_RESPAWN_TIME_MIN = BUILDER.defineInRange("defaultRespawnTimeMin", 3, 0, Integer.MAX_VALUE);
            DEFAULT_RESPAWN_TIME_MAX = BUILDER.defineInRange("defaultRespawnTimeMax", 8, 0, Integer.MAX_VALUE);
            BOSS_RESPAWN_TIME_MIN = BUILDER.defineInRange("bossRespawnTimeMin", 9, 0, Integer.MAX_VALUE);
            BOSS_RESPAWN_TIME_MAX = BUILDER.defineInRange("bossRespawnTimeMax", 18, 0, Integer.MAX_VALUE);
            BUILDER.pop();
        }
        {
            BUILDER.push("WorldGeneration");
            WRAPPED_CRIMSON_HEART = BUILDER.define("wrappedCrimsonHeart", false);
            INSTANTLY_HARDMODE_CONVERSION = BUILDER.define("instantlyHardmodeConversion", false);
            BUILDER.pop();
        }
        {
            BUILDER.push("Compatibility");
            CONVERT_ARS_NOUVEAU_MANA = BUILDER.define("convertArsNouveauMana", false);
            CONVERT_IRONS_SPELL_MANA = BUILDER.define("convertIronsSpellMana", false);
            FTB_CHUNKS_WORMHOLE_POTION = BUILDER.define("ftbChunksWormholePotion", true);
            XAEROS_MAP_WORMHOLE_POTION = BUILDER.define("xaerosMapWormholePotion", true);
            BUILDER.pop();
        }
        container.registerConfig(ModConfig.Type.COMMON, BUILDER.build());
    }
}
