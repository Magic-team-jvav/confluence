package org.confluence.mod.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.EnvironmentalMachines;
import org.confluence.mod.common.entity.monster.MonsterAttributeScaling;
import org.confluence.mod.common.entity.npc.NPCAttackBlacklist;
import org.confluence.mod.common.wind.WindMath;
import org.confluence.mod.common.wind.WindState;
import org.confluence.mod.network.s2c.DragonChargePlayerConfigPacketS2C;

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
    private static ConfigValue<List<? extends String>> AMMO_SLOTS_BLACKLIST;
    public static BooleanValue TERRA_STYLE_EXPLOSION;
    public static BooleanValue TERRA_STYLE_FIRE_DAMAGE;
    public static BooleanValue NPC_INVULNERABLE_TO_PLAYER;
    public static BooleanValue VILLAGER_INVULNERABLE_TO_PLAYER;
    public static BooleanValue PREVENT_ENEMY_FRIENDLY_FIRE;
    public static BooleanValue PREVENT_ENEMY_HURTING_PASSIVE_MOBS;
    public static BooleanValue ALLOWS_VANILLA_ENTITIES_TO_PERFORM_STAGE_ATTRIBUTES;
    public static BooleanValue ENHANCE_ALL_MONSTER;
    public static DoubleValue MONSTER_ATTRIBUTES_MULTIPLIER_HEALTH;
    public static DoubleValue MONSTER_ATTRIBUTES_MULTIPLIER_DAMAGE;
    public static DoubleValue MONSTER_ATTRIBUTES_MULTIPLIER_ARMOR;
    public static DoubleValue MONSTER_ATTRIBUTES_MULTIPLIER_ARMOR_TOUGHNESS;
    public static DoubleValue MONSTER_ATTRIBUTES_MULTIPLIER_MOVEMENT_SPEED;
    public static DoubleValue MONSTER_ATTRIBUTES_MULTIPLIER_FLYING_SPEED;
    public static DoubleValue MONSTER_ATTRIBUTES_MULTIPLIER_KNOCKBACK_RESISTANCE;
    public static DoubleValue MONSTER_ATTRIBUTES_MULTIPLIER_FOLLOW_RANGE;
    private static BooleanValue DRAGON_CHARGE_PLAYER;
    public static BooleanValue STOP_ASK_FOR_SOFTCORE;
    public static BooleanValue TERRA_STYLE_LIGHTNING_BOLT;
    public static IntValue TERRA_STYLE_LIGHTNING_BOLT_FREQUENCY_MULTIPLIER;
    /// 泰拉闪电束开启时生效：闪电落地不再点燃方块。
    public static BooleanValue TERRA_STYLE_LIGHTNING_BOLT_NO_FIRE;
    public static BooleanValue AUTO_SWING_ALL_SWORDS;
    public static BooleanValue AUTO_RELEASE_ALL_BOWS;
    public static BooleanValue AUTO_FIRE_ALL_GUNS;

    public static BooleanValue FLETCHING_MENU;
    public static BooleanValue SHIMMER_DECOMPOSE;
    public static BooleanValue SHIMMER_DECOMPOSE_FIRST_TAG_ITEM;
    public static BooleanValue ALTAR_TIPS;

    public static BooleanValue DO_FALLING_STAR_SPAWNING;
    public static IntValue FALLING_STAR_INTERVAL;
    /// 是否让本体敌怪跳过原版亮度门槛。
    ///
    /// 该开关只移除暗度判断，方块碰撞、世界边界、刷怪类型和各实体自己的高度、天气及维度
    /// 条件仍由放置谓词继续校验。
    public static BooleanValue SPAWN_WITHOUT_LIGHT;

    public static BooleanValue DO_NPC_SPAWNING;
    public static IntValue NPC_SPAWN_INTERVAL;
    public static BooleanValue BROADCAST_NPC_MSG;
    public static BooleanValue TOWN_SUPPRESSES_ENEMY_SPAWNING;
    public static BooleanValue NPC_HAPPINESS_AFFECTS_PRICES;
    public static BooleanValue BROADCAST_NPC_DEATH_MSG;
    public static BooleanValue NPC_AUTO_RESPAWN;
    public static BooleanValue SPECIAL_SEED_BOSS_BEHAVIORS;
    public static BooleanValue ALLOW_SLEEP_DURING_BLOOD_MOON;
    private static ConfigValue<List<? extends String>> NPC_ATTACK_BLACKLIST;

    public static BooleanValue EYE_OF_CTHULHU_NATURE_SPAWNING;
    public static BooleanValue DEERCLOPS_NATURE_SPAWNING;

    public static BooleanValue DO_METEORITE_SPAWNING;

    // ===== 风（Weather） =====
    /// 风系统总开关。关闭后退化为「无风」，所有消费端静默。
    public static BooleanValue DO_WIND;
    /// 是否启用温度噪声方向场。关闭 = 全局恒向（便于对比调试）。
    public static BooleanValue WIND_DIRECTION_NOISE;
    /// 温度噪声造成的风向偏差上限（度）。0 = 全局恒向。
    public static DoubleValue WIND_DIRECTION_AMPLITUDE;
    /// 每次重掷抽到「大风天档」（>= 20 mph）的概率。
    public static DoubleValue WIND_WINDY_CHANCE;
    /// 沙尘暴期间强制维持的风速（m/s），默认 30 mph。
    public static DoubleValue WIND_SANDSTORM_FORCED_SPEED;
    /// 沙尘暴是否推实体。
    public static BooleanValue SANDSTORM_PUSHES_ENTITIES;
    /// 暴风雪是否推实体。
    public static BooleanValue BLIZZARD_PUSHES_ENTITIES;

    public static BooleanValue PLAYER_DROPS_MONEY;
    public static BooleanValue SHOW_MONEY_DROPS;
    public static BooleanValue DROPS_TOMBSTONE;
    public static IntValue DEFAULT_RESPAWN_TIME_MIN;
    public static IntValue DEFAULT_RESPAWN_TIME_MAX;
    public static IntValue BOSS_RESPAWN_TIME_MIN;
    public static IntValue BOSS_RESPAWN_TIME_MAX;
    public static BooleanValue BOSS_CLEAR_WHEN_NO_TARGET;
    public static BooleanValue KING_SLIME_LARGE_MINIONS;
    public static BooleanValue ALLOW_FLESH_BOSSES_OUTSIDE_UNDERWORLD;
    public static DoubleValue BOSS_ATTRIBUTES_MULTIPLIER_HEALTH;
    public static DoubleValue BOSS_ATTRIBUTES_MULTIPLIER_DAMAGE;
    public static DoubleValue BOSS_ATTRIBUTES_MULTIPLIER_ARMOR;
    public static DoubleValue BOSS_ATTRIBUTES_MULTIPLIER_ARMOR_TOUGHNESS;
    public static DoubleValue BOSS_ATTRIBUTES_MULTIPLIER_MOVEMENT_SPEED;
    public static DoubleValue BOSS_ATTRIBUTES_MULTIPLIER_FLYING_SPEED;
    public static DoubleValue BOSS_ATTRIBUTES_MULTIPLIER_KNOCKBACK_RESISTANCE;
    public static DoubleValue BOSS_ATTRIBUTES_MULTIPLIER_FOLLOW_RANGE;

    public static BooleanValue WRAPPED_CRIMSON_HEART;
    /// 醉酒世界（种子 5162020）是否抬升海平面以上的地面高度。
    public static BooleanValue DRUNK_WORLD_RAISE_SURFACE;
    public static BooleanValue INSTANTLY_HARDMODE_CONVERSION;
    public static IntValue CLENTAMINATOR_RANGE_CHUNKS;
    public static IntValue TERRAFORMER_RANGE_CHUNKS;
    public static BooleanValue ENVIRONMENTAL_MACHINE_MIXED_EXPLOSION;

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
    public static Set<TagKey<Item>> ammoSlotsTagBlackList = Set.of(Tags.Items.SEEDS);

    public static ModConfigSpec SPEC;

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
        MonsterAttributeScaling.reload();
        NPCAttackBlacklist.reload(NPC_ATTACK_BLACKLIST.get());

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

    public static void register(ModContainer container) {
        Builder builder = new Builder();
        {
            builder.push("Gameplay");
            ENEMY_DROPS_MONEY = builder.define("enemyDropsMoney", true);
            AUTO_STACK_GELS_COLOR = builder.define("autoStackGelsColor", true);
            RETURN_POTION_GLASS_BOTTLE = builder.define("returnPotionGlassBottle", false);
            RIGHT_CLICK_RIDE_MINECART = builder.define("rightClickRideMinecart", true);
            ANNOUNCEMENT_BOX_DISTANCE = builder.defineInRange("announcementBoxDistance", 128, 0, Integer.MAX_VALUE);
            ALERT_PLAYER_IN_DUNGEON = builder.define("alertPlayerDungeon", false);
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
            STOP_ASK_FOR_SOFTCORE = builder.define("stopAskForSoftcore", false);
            {
                builder.push("AutomaticWeaponUse");
                AUTO_SWING_ALL_SWORDS = builder.define("autoSwingAllSwords", false);
                AUTO_RELEASE_ALL_BOWS = builder.define("autoReleaseAllBows", false);
                AUTO_FIRE_ALL_GUNS = builder.define("autoFireAllGuns", false);
                builder.pop();
            }
            builder.pop();
        }
        {
            builder.push("NPC");
            NPC_INVULNERABLE_TO_PLAYER = builder.define("npcInvulnerableToPlayer", true);
            VILLAGER_INVULNERABLE_TO_PLAYER = builder.comment("Protect villagers from player-owned damage, except damage caused by creative players.")
                    .define("villagerInvulnerableToPlayer", true);
            NPC_ATTACK_BLACKLIST = builder.defineListAllowEmpty("npcAttackBlacklist", List::of, () -> "[#]namespace:path", value -> value instanceof String entry && NPCAttackBlacklist.isValid(entry));
            DO_NPC_SPAWNING = builder.define("doNPCSpawning", true);
            NPC_SPAWN_INTERVAL = builder.defineInRange("npcSpawnInterval", 2400, 20, 20000);
            BROADCAST_NPC_MSG = builder.define("broadcastNpcMsg", true);
            TOWN_SUPPRESSES_ENEMY_SPAWNING = builder.comment("Allow town residents to suppress natural enemy spawning.").define("townSuppressesEnemySpawning", true);
            NPC_HAPPINESS_AFFECTS_PRICES = builder.comment("Apply NPC happiness multipliers to buying and selling prices.").define("npcHappinessAffectsPrices", true);
            NPC_AUTO_RESPAWN = builder.comment("Allow deceased resident professions to return automatically in their region. Does not prevent first arrivals or rescues.").define("npcAutoRespawn", true);
            BROADCAST_NPC_DEATH_MSG = builder.comment("Broadcast NPC death messages independently of arrival and departure messages.").define("broadcastNpcDeathMsg", true);
            builder.pop();
        }
        {
            builder.push("Enemies");
            PREVENT_ENEMY_FRIENDLY_FIRE = builder.comment("Enable the existing enemy faction friendly-fire protection. Does not change enemy targeting.")
                    .define("preventEnemyFriendlyFire", true);
            PREVENT_ENEMY_HURTING_PASSIVE_MOBS = builder.comment("Prevent enemies from hurting passive creatures (animals, water animals and ambient creatures). Players, villagers, NPCs, summons and environmental damage are unaffected.")
                    .define("preventEnemyHurtingPassiveMobs", false);
            {
                builder.push("Attributes");
                ALLOWS_VANILLA_ENTITIES_TO_PERFORM_STAGE_ATTRIBUTES = builder.define("allowsVanillaEntitiesToPerformStageAttributes", false);
                ENHANCE_ALL_MONSTER = builder
                        .comment("Apply these multipliers to vanilla and other mods' hostile mobs as well. Otherwise only Confluence enemies are affected. Bosses, summons and friendly creatures are excluded.")
                        .define("enhanceAllMonster", false);
                MONSTER_ATTRIBUTES_MULTIPLIER_HEALTH = builder
                        .comment("Maximum health multiplier. Does not refill wounded enemies when reloaded.")
                        .defineInRange("monsterAttributesMultiplierHealth", 1.0D, 0.0625D, 100.0D);
                MONSTER_ATTRIBUTES_MULTIPLIER_DAMAGE = builder
                        .comment("Attack damage attribute multiplier. Projectiles derived from that attribute inherit it once; independently configured projectile damage is unchanged.")
                        .defineInRange("monsterAttributesMultiplierDamage", 1.0D, 0.0625D, 100.0D);
                MONSTER_ATTRIBUTES_MULTIPLIER_ARMOR = builder
                        .comment("Armor multiplier. Multiplication cannot give armor to an enemy whose armor is zero.")
                        .defineInRange("monsterAttributesMultiplierArmor", 1.0D, 0.0D, 100.0D);
                MONSTER_ATTRIBUTES_MULTIPLIER_ARMOR_TOUGHNESS = builder
                        .comment("Armor toughness multiplier. Zero toughness remains zero.")
                        .defineInRange("monsterAttributesMultiplierArmorToughness", 1.0D, 0.0D, 100.0D);
                MONSTER_ATTRIBUTES_MULTIPLIER_MOVEMENT_SPEED = builder
                        .comment("Movement speed attribute multiplier; does not alter AI movement implemented with fixed velocities.")
                        .defineInRange("monsterAttributesMultiplierMovementSpeed", 1.0D, 0.0D, 100.0D);
                MONSTER_ATTRIBUTES_MULTIPLIER_FLYING_SPEED = builder
                        .comment("Flying speed attribute multiplier; only affects enemies that use this attribute.")
                        .defineInRange("monsterAttributesMultiplierFlyingSpeed", 1.0D, 0.0D, 100.0D);
                MONSTER_ATTRIBUTES_MULTIPLIER_KNOCKBACK_RESISTANCE = builder
                        .comment("Knockback resistance multiplier, clamped by the attribute's own allowed range.")
                        .defineInRange("monsterAttributesMultiplierKnockbackResistance", 1.0D, 0.0D, 100.0D);
                MONSTER_ATTRIBUTES_MULTIPLIER_FOLLOW_RANGE = builder
                        .comment("Follow range attribute multiplier; existing line-of-sight and AI-specific range checks still apply.")
                        .defineInRange("monsterAttributesMultiplierFollowRange", 1.0D, 0.0D, 100.0D);
                builder.pop();
            }
            builder.pop();
        }
        {
            builder.push("Boss");
            {
                builder.push("Attributes");
                BOSS_ATTRIBUTES_MULTIPLIER_HEALTH = builder
                        .comment("Maximum health multiplier for Confluence bosses. Reloading does not heal existing bosses.")
                        .defineInRange("bossAttributesMultiplierHealth", 1.0D, 0.0625D, 10.0D);
                BOSS_ATTRIBUTES_MULTIPLIER_DAMAGE = builder
                        .comment("Damage multiplier for Confluence bosses, including contact attacks and projectiles attributed to the boss. Applied once before damage reduction.")
                        .defineInRange("bossAttributesMultiplierDamage", 1.0D, 0.0625D, 10.0D);
                BOSS_ATTRIBUTES_MULTIPLIER_ARMOR = builder
                        .comment("Armor multiplier. Zero armor remains zero.")
                        .defineInRange("bossAttributesMultiplierArmor", 1.0D, 0.0D, 100.0D);
                BOSS_ATTRIBUTES_MULTIPLIER_ARMOR_TOUGHNESS = builder
                        .comment("Armor toughness multiplier. Zero toughness remains zero.")
                        .defineInRange("bossAttributesMultiplierArmorToughness", 1.0D, 0.0D, 100.0D);
                BOSS_ATTRIBUTES_MULTIPLIER_MOVEMENT_SPEED = builder
                        .comment("Movement speed attribute multiplier; fixed AI velocities are unchanged.")
                        .defineInRange("bossAttributesMultiplierMovementSpeed", 1.0D, 0.0D, 100.0D);
                BOSS_ATTRIBUTES_MULTIPLIER_FLYING_SPEED = builder
                        .comment("Flying speed attribute multiplier; only affects bosses that use this attribute.")
                        .defineInRange("bossAttributesMultiplierFlyingSpeed", 1.0D, 0.0D, 100.0D);
                BOSS_ATTRIBUTES_MULTIPLIER_KNOCKBACK_RESISTANCE = builder
                        .comment("Knockback resistance multiplier, clamped by the attribute's allowed range.")
                        .defineInRange("bossAttributesMultiplierKnockbackResistance", 1.0D, 0.0D, 100.0D);
                BOSS_ATTRIBUTES_MULTIPLIER_FOLLOW_RANGE = builder
                        .comment("Follow range attribute multiplier; line-of-sight and AI-specific range checks still apply.")
                        .defineInRange("bossAttributesMultiplierFollowRange", 1.0D, 0.0D, 100.0D);
                builder.pop();
            }
            {
                builder.push("Behavior");
                SPECIAL_SEED_BOSS_BEHAVIORS = builder.comment("Enable additional boss behaviors from special world seeds. Does not change difficulty scaling.").define("specialSeedBossBehaviors", true);
                ALLOW_FLESH_BOSSES_OUTSIDE_UNDERWORLD = builder.define("allowFleshBossesOutsideUnderworld", false);
                BOSS_CLEAR_WHEN_NO_TARGET = builder.define("bossClearWhenNoTarget", true);
                KING_SLIME_LARGE_MINIONS = builder.define("kingSlimeLargeMinions", false);
                DRAGON_CHARGE_PLAYER = builder.define("dragonChargePlayer", true);
                builder.pop();
            }
            {
                builder.push("Spawning");
                EYE_OF_CTHULHU_NATURE_SPAWNING = builder.define("eyeOfCthulhuNatureSpawning", true);
                DEERCLOPS_NATURE_SPAWNING = builder.define("deerclopsNatureSpawning", true);
                builder.pop();
            }
            builder.pop();
        }
        {
            builder.push("Weather");
            {
                builder.push("LightningBolt");
                TERRA_STYLE_LIGHTNING_BOLT = builder.define("terraStyleLightningBolt", true);
                TERRA_STYLE_LIGHTNING_BOLT_FREQUENCY_MULTIPLIER = builder.defineInRange("terraStyleLightningBoltFrequencyMultiplier", 10, 1, 1000);
                TERRA_STYLE_LIGHTNING_BOLT_NO_FIRE = builder.define("terraStyleLightningBoltNoFire", true);
                builder.pop();
            }
            DO_WIND = builder.define("doWind", true);
            WIND_DIRECTION_NOISE = builder.define("windDirectionNoise", true);
            WIND_DIRECTION_AMPLITUDE = builder.defineInRange("windDirectionAmplitude", 90.0D, 0.0D, 180.0D);
            WIND_WINDY_CHANCE = builder.defineInRange("windWindyChance", (double) WindState.WINDY_CHANCE, 0.0D, 1.0D);
            WIND_SANDSTORM_FORCED_SPEED = builder.defineInRange("windSandstormForcedSpeed", (double) WindMath.SANDSTORM_FORCED_SPEED, 0.0D, 100.0D);
            SANDSTORM_PUSHES_ENTITIES = builder.define("sandstormPushesEntities", true);
            BLIZZARD_PUSHES_ENTITIES = builder.define("blizzardPushesEntities", true);
            builder.pop();
        }
        {
            builder.push("Recipe");
            FLETCHING_MENU = builder.define("fletchingMenu", true);
            SHIMMER_DECOMPOSE = builder.define("shimmerDecompose", true);
            SHIMMER_DECOMPOSE_FIRST_TAG_ITEM = builder.define("shimmerDecomposeFirstTagItem", false);
            ALTAR_TIPS = builder.define("altarTips", true);
            builder.pop();
        }
        {
            builder.push("Spawning");
            SPAWN_WITHOUT_LIGHT = builder.define("spawnWithoutLight", true);
            {
                builder.push("Falling Star");
                DO_FALLING_STAR_SPAWNING = builder.define("doFallingStarSpawning", true);
                FALLING_STAR_INTERVAL = builder.defineInRange("fallingStarInterval", 2400, 20, 20000);
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
            builder.push("WorldRules");
            ENVIRONMENTAL_MACHINE_MIXED_EXPLOSION = builder.comment("Allow explosions and terrain contamination when starting a machine with mixed solutions.")
                    .define("environmentalMachineMixedExplosion", true);
            INSTANTLY_HARDMODE_CONVERSION = builder.define("instantlyHardmodeConversion", false);
            CLENTAMINATOR_RANGE_CHUNKS = builder.comment("Maintained square side length in chunks. Independent of the Terraformer.")
                    .defineInRange("clentaminatorRangeChunks", EnvironmentalMachines.DEFAULT_CLENTAMINATOR_RANGE_CHUNKS, EnvironmentalMachines.MIN_RANGE_CHUNKS, EnvironmentalMachines.MAX_RANGE_CHUNKS);
            TERRAFORMER_RANGE_CHUNKS = builder.comment("Maintained square side length in chunks. Independent of the Clentaminator.")
                    .defineInRange("terraformerRangeChunks", EnvironmentalMachines.DEFAULT_TERRAFORMER_RANGE_CHUNKS, EnvironmentalMachines.MIN_RANGE_CHUNKS, EnvironmentalMachines.MAX_RANGE_CHUNKS);
            builder.pop();
        }
        {
            builder.push("WorldGeneration");
            WRAPPED_CRIMSON_HEART = builder.define("wrappedCrimsonHeart", false);
            DRUNK_WORLD_RAISE_SURFACE = builder.comment("Whether the Drunk World (seed 5162020) raises terrain height above sea level.")
                    .define("drunkWorldRaiseSurface", true);
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
                ALLOW_SLEEP_DURING_BLOOD_MOON = builder.comment("Allow sleeping during a blood moon. Other sleeping restrictions still apply.").define("allowSleepDuringBloodMoon", false);
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
        container.registerConfig(ModConfig.Type.COMMON, SPEC = builder.build());
    }
}
