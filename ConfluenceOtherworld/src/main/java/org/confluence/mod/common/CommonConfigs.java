package org.confluence.mod.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
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

    public static BooleanValue FLETCHING_MENU;
    public static BooleanValue SHIMMER_DECOMPOSE;
    public static BooleanValue BREWING_STAND_RECIPE;
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
    public static BooleanValue XAEROS_MAP_WORMHOLE_POTION;
    public static BooleanValue XAEROS_MAP_PYLON_WAYPOINT;
    public static BooleanValue WAYSTONES_PYLON_NON_COST;

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
            AMMO_SLOTS_BLACKLIST = BUILDER.defineListAllowEmpty("ammoSlotsBlacklist", () -> List.of("confluence:falling_star", "#c:seeds"), () -> "[#]namespace:path", o -> {
                if (o instanceof String s) {
                    if (s.startsWith("#")) {
                        return ResourceLocation.tryParse(s.substring(1)) != null;
                    }
                    return ResourceLocation.tryParse(s) != null;
                }
                return false;
            });
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
                BROADCAST_NPC_MSG = BUILDER.define("broadcastNpcMsg", true);
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
            {
                BUILDER.push("ArsNouveau");
                CONVERT_ARS_NOUVEAU_MANA = BUILDER.define("convertArsNouveauMana", false);
                BUILDER.pop();
            }
            {
                BUILDER.push("IronsSpell");
                CONVERT_IRONS_SPELL_MANA = BUILDER.define("convertIronsSpellMana", false);
                BUILDER.pop();
            }
            {
                BUILDER.push("FTB");
                FTB_CHUNKS_WORMHOLE_POTION = BUILDER.define("ftbChunksWormholePotion", true);
                BUILDER.pop();
            }
            {
                BUILDER.push("Xaero");
                XAEROS_MAP_WORMHOLE_POTION = BUILDER.define("xaerosMapWormholePotion", true);
                XAEROS_MAP_PYLON_WAYPOINT = BUILDER.define("xaerosMapPylonWaypoint", true);
                BUILDER.pop();
            }
            {
                BUILDER.push("Waystones");
                WAYSTONES_PYLON_NON_COST = BUILDER.define("waystonesPylonNonCost", true);
                BUILDER.pop();
            }
            BUILDER.pop();
        }
        container.registerConfig(ModConfig.Type.COMMON, BUILDER.build());
    }
}
