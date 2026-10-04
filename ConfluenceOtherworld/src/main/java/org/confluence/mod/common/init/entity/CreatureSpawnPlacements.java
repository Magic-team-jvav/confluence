package org.confluence.mod.common.init.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.entity.SpawnPlacementChecks;

/// 所有进入自然生成数据的生物放置规则注册中心；这里只登记，不实现条件。
///
/// 1.20 侧本类通过 PortLib 的 `PortRegisterSpawnPlacementsEvent` 注册；
/// 1.21 侧是对应的原生事件 {@link RegisterSpawnPlacementsEvent}
/// （本仓库既有写法见 `ModEvents#registerSpawnReplacements`，本类由它调用）。
///
/// **本类目前是小动物那一半 + 已落地物种的增量项**：`registerCritters(...)` 完整，
/// `registerPreHardmodeMonsters` / `registerHardmodeMonsters` 只登记 1.21 `MonsterEntities`
/// 已经存在的成员（WP2 剩余物种批补入大风气球怪与狼人）。
///
/// 1.20 的其余项引用 `MonsterEntities.*` 的成员，而 1.21 的 `MonsterEntities` 还在增量生长，
/// 整篇搬会被 159 文件闭包挡住（实测见 `notes/WP5C-SUBSET.md` 第三节）—— 那几组随种族批次补。
///
/// 实体按生态角色和游戏进度分组。相同语义共用 {@link SpawnPlacementChecks} 中的谓词，困难模式组再由
/// {@link SpawnPlacementChecks#hardmode(SpawnPlacements.SpawnPredicate)} 叠加进度门槛。
/// {@link RegisterSpawnPlacementsEvent.Operation#REPLACE} 用于明确覆盖默认规则，避免模组加载
/// 顺序导致多个谓词以不可预测方式组合。
public final class CreatureSpawnPlacements {
    private CreatureSpawnPlacements() {}

    public static void register(RegisterSpawnPlacementsEvent event) {
        registerCritters(event);
        registerPreHardmodeMonsters(event);
        registerHardmodeMonsters(event);
    }

    private static void registerCritters(RegisterSpawnPlacementsEvent event) {
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkSurfaceWaterMonsterSpawn, CritterEntities.GOLDFISH);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSurfacePenguinSpawn, CritterEntities.PENGUIN);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkMobSpawnRules, CritterEntities.GLOWING_MOOSHROOM, CritterEntities.GLOWING_CLUCKSHROOM, CritterEntities.CLUCKSHROOM);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkFireflySpawn, CritterEntities.FIREFLY);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkLightningBugSpawn, CritterEntities.LIGHTNING_BUG);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkTruffleWormSpawn), CritterEntities.TRUFFLE_WORM);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkAnimalSpawnRules,
                CritterEntities.CLOUD_SHEEP,
                CritterEntities.BUNNY,
                CritterEntities.EXPLOSIVE_BUNNY, CritterEntities.HOSTILE_BUNNY,
                CritterEntities.BIRD, CritterEntities.BLUE_JAY, CritterEntities.CARDINAL,
                CritterEntities.SQUIRREL, CritterEntities.RED_SQUIRREL,
                CritterEntities.DUCK,
                CritterEntities.GLOWING_SNAIL, CritterEntities.GRUBBY,
                CritterEntities.MAGGOT, CritterEntities.SLUGGY,
                CritterEntities.SNAIL, CritterEntities.SCORPION,
                CritterEntities.GRASSHOPPER);
        event.register(CritterEntities.CRAB.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCavernCritterSpawn,
                CritterEntities.JEWEL_BUNNY, CritterEntities.JEWEL_SQUIRREL);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSurfaceDayCritterSpawn,
                CritterEntities.DRAGONFLY);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkButterflySpawn, CritterEntities.BUTTERFLY);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkStinkbugSpawn, CritterEntities.STINKBUG);
        event.register(CritterEntities.FAIRY.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkFairySpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.FEALING.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkNetherDayCritterSpawn,
                CritterEntities.HELL_BUTTERFLY, CritterEntities.MAGMA_SNAIL);
        event.register(CritterEntities.LADYBUG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkLadybugSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.PRISMATIC_LACEWING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkPrismaticLacewingSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.WORM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkWormSpawn, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static void registerPreHardmodeMonsters(RegisterSpawnPlacementsEvent event) {
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkAngryDandelionSpawn, MonsterEntities.ANGRY_DANDELION, MonsterEntities.WINDY_BALLOON);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGnomeSpawn, MonsterEntities.GNOME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkMysticFrogSpawn, CritterEntities.MYSTIC_FROG);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkOldShakingChestSpawn, MonsterEntities.OLD_SHAKING_CHEST);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkUndergroundMonsterSpawn, MonsterEntities.SPORE_BAT, MonsterEntities.SPORE_SKELETON);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn, MonsterEntities.ICE_BAT, MonsterEntities.CAVE_BAT);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkNetherMonsterSpawn, MonsterEntities.HELL_BAT, MonsterEntities.BONE_SERPENT, MonsterEntities.WITHER_BONE_SERPENT);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCorruptionWormSpawn, MonsterEntities.DEVOURER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGiantWormSpawn, MonsterEntities.GIANT_WORM);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkDiggerSpawn), MonsterEntities.DIGGER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCorruptionWormSpawn), MonsterEntities.WORLD_FEEDER);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkHighLevelMonsterSpawn), MonsterEntities.WYVERN);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkArchWyvernSpawn), MonsterEntities.ARCH_WYVERN);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGhostSpawn, MonsterEntities.GHOST);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkRoutineMonsterSpawn, MonsterEntities.METEOR_HEAD);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn, MonsterEntities.GRANITE_GOLEM);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkSurfaceWaterMonsterSpawn, MonsterEntities.SHARK);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkSurfaceDayMobSpawn), MonsterEntities.DERPLING);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn), MonsterEntities.HERPLING);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPureOrHallowDesertUndergroundSpawn), MonsterEntities.SAND_POACHER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn), MonsterEntities.ICE_TORTOISE);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDungeonMonsterSpawn, MonsterEntities.CURSED_SKULL);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGroundSpawn, MonsterEntities.DRIPPLER);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkUndergroundWaterMonsterSpawn, MonsterEntities.BLUE_JELLYFISH);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkUndergroundWaterMonsterSpawn), MonsterEntities.GREEN_JELLYFISH);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkAnglerFishSpawn), MonsterEntities.ANGLER_FISH);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkGraniteElementalSpawn, MonsterEntities.GRANITE_ELEMENTAL);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn, MonsterEntities.GIANT_SHELLY, MonsterEntities.CRAWDAD);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkRoutineMonsterSpawn, MonsterEntities.DECAYEDER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDungeonMonsterSpawn,
                MonsterEntities.BASE_BONES, MonsterEntities.ANGER_BONES, MonsterEntities.SHORT_BONES,
                MonsterEntities.BIG_BONES, MonsterEntities.BIG_ANGER_BONES,
                MonsterEntities.BIG_MUSCLE_ANGER_BONES, MonsterEntities.BIG_HELMET_ANGER_BONES);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGroundSpawn,
                MonsterEntities.SPORE_ZOMBIE, MonsterEntities.HAT_SPORE_ZOMBIE,
                MonsterEntities.GOBLIN_PEON, MonsterEntities.GOBLIN_ARCHER,
                MonsterEntities.GOBLIN_WARRIOR, MonsterEntities.GOBLIN_THIEF,
                MonsterEntities.ANGER_GOBLIN, MonsterEntities.BLOOD_ZOMBIE);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGoblinScoutSpawn, MonsterEntities.GOBLIN_SCOUT);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkZombieSpawn, MonsterEntities.ZOMBIE);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDoctorBonesSpawn, MonsterEntities.DOCTOR_BONES);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkWeddingZombieSpawn, MonsterEntities.THE_GROOM, MonsterEntities.THE_BRIDE);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkFlyingFishSpawn, MonsterEntities.FLYING_FISH);
    }

    private static void registerHardmodeMonsters(RegisterSpawnPlacementsEvent event) {
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkWerewolfSpawn), MonsterEntities.WEREWOLF);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn), MonsterEntities.ILLUMINANT_BAT, MonsterEntities.ARMORED_VIKING);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn,
                MonsterEntities.SNOW_FLINX, MonsterEntities.ICE_BAT, MonsterEntities.UNDEAD_VIKING);
        group(event, SpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPureDesertUndergroundSpawn), MonsterEntities.GHOUL);
        group(event, SpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPureOrHallowDesertUndergroundSpawn), MonsterEntities.LIGHT_LAMIA);
        group(event, SpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkEvilDesertUndergroundSpawn), MonsterEntities.DARK_LAMIA);
        group(event, SpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCorruptDesertUndergroundSpawn), MonsterEntities.VILE_GHOUL);
        group(event, SpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCrimsonDesertUndergroundSpawn), MonsterEntities.TAINTED_GHOUL);
        group(event, SpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkHallowDesertUndergroundSpawn), MonsterEntities.DREAMER_GHOUL);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkSandMummySpawn), MonsterEntities.MUMMY);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkEbonsandMummySpawn), MonsterEntities.DARK_MUMMY);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCrimsandMummySpawn), MonsterEntities.BLOOD_MUMMY);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPearlsandMummySpawn), MonsterEntities.LIGHT_MUMMY);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn), MonsterEntities.BASILISK);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkPostMechanicalNetherSpawn, MonsterEntities.LAVA_BAT);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkSurfaceNightMonsterSpawn), MonsterEntities.GIANT_FLYING_FOX);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn), MonsterEntities.GIANT_BAT, MonsterEntities.ICE_MIMIC, MonsterEntities.ARMORED_SKELETON);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkGroundSpawn), MonsterEntities.WOODEN_MIMIC);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkBelowSurfaceMonsterSpawn),
                MonsterEntities.GOLDEN_MIMIC, MonsterEntities.CRIMSON_MIMIC, MonsterEntities.CORRUPT_MIMIC,
                MonsterEntities.HALLOWED_MIMIC, MonsterEntities.JUNGLE_MIMIC);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkNetherMonsterSpawn), MonsterEntities.SHADOW_MIMIC);

        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSurfaceDaySlimeSpawn,
                MonsterEntities.BLUE_SLIME, MonsterEntities.GREEN_SLIME, MonsterEntities.PURPLE_SLIME, MonsterEntities.PINK_SLIME, MonsterEntities.SWAMP_SLIME, MonsterEntities.TROPIC_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSnowySurfaceDaySlimeSpawn,
                MonsterEntities.ICE_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkJungleSurfaceDaySlimeSpawn,
                MonsterEntities.JUNGLE_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkShallowUndergroundSlimeSpawn,
                MonsterEntities.YELLOW_SLIME, MonsterEntities.RED_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveSlimeSpawn,
                MonsterEntities.BLACK_SLIME, MonsterEntities.MOTHER_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSnowyCaveSlimeSpawn,
                MonsterEntities.SPIKED_ICE_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkJungleUndergroundSlimeSpawn,
                MonsterEntities.SPIKED_JUNGLE_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDesertUndergroundSlimeSpawn,
                MonsterEntities.DESERT_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDungeonSlimeSpawn,
                MonsterEntities.DUNGEON_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkNetherMonsterSpawn,
                MonsterEntities.LAVA_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGreenDumplingSlimeSpawn,
                MonsterEntities.GREEN_DUMPLING_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDemonEyeSpawn,
                MonsterEntities.DEMON_EYE);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkRoutineMonsterSpawn,
                MonsterEntities.BLOODY_SPORE, MonsterEntities.FACE_MONSTER, MonsterEntities.CRIMERA, MonsterEntities.EATER_OF_SOULS, MonsterEntities.BLOOD_CRAWLER, MonsterEntities.JUNGLE_BAT);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkAntlionChargerSpawn,
                MonsterEntities.ANTLION_CHARGER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkTimSpawn,
                MonsterEntities.TIM);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkClumsyBalloonSlimeSpawn,
                MonsterEntities.CLUMSY_BALLOON_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkGroundSpawn),
                MonsterEntities.GOBLIN_WARLOCK);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkGroundSpawn),
                MonsterEntities.PIRATE_DECKHAND, MonsterEntities.PIRATE_CORSAIR, MonsterEntities.PIRATE_DEADEYE, MonsterEntities.PIRATE_CROSSBOWER, MonsterEntities.PIRATE_CAPTAIN);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSandstormSpawn,
                MonsterEntities.ANGRY_TUMBLER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPlainSandSharkSpawn),
                MonsterEntities.SAND_SHARK);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCorruptSandSharkSpawn),
                MonsterEntities.BONE_BITER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCrimsonSandSharkSpawn),
                MonsterEntities.FLESH_REAVER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkHallowSandSharkSpawn),
                MonsterEntities.CRYSTAL_THRESHER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkFungiBulbSpawn,
                MonsterEntities.FUNGI_BULB);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkFungiBulbSpawn),
                MonsterEntities.GIANT_FUNGI_BULB);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkClingerSpawn),
                MonsterEntities.CLINGER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGroundSpawn,
                MonsterEntities.SNATCHER, MonsterEntities.GOBLIN_SORCERER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkUndergroundMonsterSpawn,
                MonsterEntities.MAN_EATER, MonsterEntities.HORNET);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDungeonMonsterSpawn,
                MonsterEntities.DARK_CASTER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkTombCrawlerSpawn,
                MonsterEntities.TOMB_CRAWLER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn,
                MonsterEntities.NYMPH);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkNetherMonsterSpawn,
                MonsterEntities.FIRE_IMP);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkHighLevelMonsterSpawn,
                MonsterEntities.HARPY);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkNetherMonsterSpawn,
                MonsterEntities.DEMON, MonsterEntities.VOODOO_DEMON);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkAntlionSwarmerSpawn,
                MonsterEntities.ANTLION_SWARMER, MonsterEntities.GIANT_ANTLION_SWARMER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn,
                MonsterEntities.HOPLITE);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkAntlionSpawn,
                MonsterEntities.ANTLION);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkDesertSpiritSpawn),
                MonsterEntities.DESERT_SPIRIT);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkWallCreeperSpawn,
                MonsterEntities.WALL_CREEPER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.BLACK_RECLUSE);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRuneWizardSpawn),
                MonsterEntities.RUNE_WIZARD);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkWaterBoltMimicSpawn,
                MonsterEntities.WATER_BOLT_MIMIC);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkAngryNimbusSpawn),
                MonsterEntities.ANGRY_NIMBUS);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkWaterMonsterSpawn,
                MonsterEntities.PIRANHA);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkSurfaceWaterMonsterSpawn,
                MonsterEntities.PINK_JELLYFISH);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.ICY_MERMAN);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn),
                MonsterEntities.MOSS_HORNET, MonsterEntities.JUNGLE_CREEPER);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPostMechanicalNetherSpawn),
                MonsterEntities.RED_DEVIL);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkIceElementalSpawn),
                MonsterEntities.ICE_ELEMENTAL);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkIceGolemSpawn),
                MonsterEntities.ICE_GOLEM);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn),
                MonsterEntities.CORRUPTOR, MonsterEntities.ENCHANTED_SWORD, MonsterEntities.SLIMER);
        group(event, SpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkSurfaceNightMonsterSpawn),
                MonsterEntities.GASTROPOD);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkSurfaceMobSpawn),
                MonsterEntities.PIXIE, MonsterEntities.UNICORN);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRoutineMobSpawn),
                MonsterEntities.GIANT_TORTOISE);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn),
                MonsterEntities.CRIMSLIME, MonsterEntities.CORRUPT_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.CHAOS_ELEMENTAL);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.LUMINOUS_SLIME);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRockGolemSpawn),
                MonsterEntities.ROCK_GOLEM);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.atLeast(GamePhase.PLANTERA, SpawnPlacementChecks::checkDungeonMonsterSpawn),
                MonsterEntities.PALADIN, MonsterEntities.BONE_LEE, MonsterEntities.NECROMANCER, MonsterEntities.DIABOLIST, MonsterEntities.RAGGED_CASTER);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPossessedArmorSpawn),
                MonsterEntities.POSSESS_ARMOR);
        group(event, SpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkWraithSpawn),
                MonsterEntities.WRAITH);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCrimsonWaterMonsterSpawn),
                MonsterEntities.BLOOD_JELLY);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkFungoFishSpawn),
                MonsterEntities.FUNGO_FISH);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkCorruptionWaterMonsterSpawn,
                MonsterEntities.CORRUPT_GOLDFISH);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkCrimsonWaterMonsterSpawn,
                MonsterEntities.VICIOUS_GOLDFISH);
        group(event, SpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkWaterMonsterSpawn),
                MonsterEntities.ARAPAIMA, MonsterEntities.BLOOD_FEEDER);
    }

    @SafeVarargs
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void group(RegisterSpawnPlacementsEvent event, SpawnPlacementType placement, SpawnPlacements.SpawnPredicate predicate, DeferredHolder<EntityType<?>, ? extends EntityType<? extends Mob>>... types) {
        for (DeferredHolder<EntityType<?>, ? extends EntityType<? extends Mob>> type : types) {
            event.register((EntityType) type.get(), placement, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, predicate, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
    }
}
