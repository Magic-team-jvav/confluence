package org.confluence.mod.common.init.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.entity.SpawnPlacementChecks;
import org.mesdag.portlib.event.entity.PortRegisterSpawnPlacementsEvent;
import org.mesdag.portlib.wrapper.world.entity.PortSpawnPlacementType;
import org.mesdag.portlib.wrapper.world.entity.PortSpawnPlacementTypes;

/// 所有进入自然生成数据的生物放置规则注册中心；这里只登记，不实现条件。
///
/// 注册通过 PortLib 事件完成。任何被
/// 生物群系修饰器列为自然生成候选的实体，都必须在这里获得明确的放置类型和最终谓词；否则
/// 数据包看似包含该生物，运行时却可能沿用错误规则或完全无法生成。
///
/// 实体按生态角色和游戏进度分组。相同语义共用 SpawnPlacementChecks 中的谓词，困难模式组再由
/// {@link SpawnPlacementChecks#hardmode(SpawnPlacements.SpawnPredicate)} 叠加进度门槛。
/// {@link PortRegisterSpawnPlacementsEvent.Operation#REPLACE} 用于明确覆盖默认规则，避免模组加载
/// 顺序导致多个谓词以不可预测方式组合。
public final class CreatureSpawnPlacements {
    private CreatureSpawnPlacements() {}

    public static void register(PortRegisterSpawnPlacementsEvent event) {
        registerCritters(event);
        registerSlimes(event);
        registerPreHardmodeMonsters(event);
        registerHardmodeMonsters(event);
    }

    private static void registerCritters(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkSurfaceWaterMonsterSpawn, CritterEntities.GOLDFISH);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSurfacePenguinSpawn, CritterEntities.PENGUIN);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkMobSpawnRules, CritterEntities.GLOWING_MOOSHROOM, CritterEntities.GLOWING_CLUCKSHROOM, CritterEntities.CLUCKSHROOM);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkFireflySpawn, CritterEntities.FIREFLY);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkLightningBugSpawn, CritterEntities.LIGHTNING_BUG);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkTruffleWormSpawn), CritterEntities.TRUFFLE_WORM);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkAnimalSpawnRules,
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
        event.register(CritterEntities.CRAB.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkMobSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCavernCritterSpawn,
                CritterEntities.JEWEL_BUNNY, CritterEntities.JEWEL_SQUIRREL);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSurfaceDayCritterSpawn,
                CritterEntities.DRAGONFLY);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkButterflySpawn, CritterEntities.BUTTERFLY);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkStinkbugSpawn, CritterEntities.STINKBUG);
        event.register(CritterEntities.FAIRY.get(), PortSpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkFairySpawn, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.FEALING.get(), PortSpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkMobSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkNetherDayCritterSpawn,
                CritterEntities.HELL_BUTTERFLY, CritterEntities.MAGMA_SNAIL);
        event.register(CritterEntities.LADYBUG.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkLadybugSpawn, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.PRISMATIC_LACEWING.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkPrismaticLacewingSpawn, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.WORM.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SpawnPlacementChecks::checkWormSpawn, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static void registerSlimes(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSurfaceDaySlimeSpawn,
                MonsterEntities.BLUE_SLIME, MonsterEntities.GREEN_SLIME, MonsterEntities.PURPLE_SLIME,
                MonsterEntities.PINK_SLIME, MonsterEntities.SWAMP_SLIME, MonsterEntities.TROPIC_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSnowySurfaceDaySlimeSpawn,
                MonsterEntities.ICE_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkJungleSurfaceDaySlimeSpawn,
                MonsterEntities.JUNGLE_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkShallowUndergroundSlimeSpawn,
                MonsterEntities.YELLOW_SLIME, MonsterEntities.RED_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveSlimeSpawn,
                MonsterEntities.BLACK_SLIME, MonsterEntities.MOTHER_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSnowyCaveSlimeSpawn,
                MonsterEntities.SPIKED_ICE_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkJungleUndergroundSlimeSpawn,
                MonsterEntities.SPIKED_JUNGLE_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDesertUndergroundSlimeSpawn,
                MonsterEntities.DESERT_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDungeonSlimeSpawn,
                MonsterEntities.DUNGEON_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkNetherMonsterSpawn,
                MonsterEntities.LAVA_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGreenDumplingSlimeSpawn,
                MonsterEntities.GREEN_DUMPLING_SLIME);
    }

    private static void registerPreHardmodeMonsters(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDemonEyeSpawn, MonsterEntities.DEMON_EYE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkZombieSpawn, MonsterEntities.ZOMBIE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkRoutineMonsterSpawn,
                MonsterEntities.BLOODY_SPORE,
                MonsterEntities.FACE_MONSTER,
                MonsterEntities.DECAYEDER, MonsterEntities.CRIMERA,
                MonsterEntities.EATER_OF_SOULS, MonsterEntities.BLOOD_CRAWLER,
                MonsterEntities.JUNGLE_BAT);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkUndergroundMonsterSpawn,
                MonsterEntities.SPORE_SKELETON);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCorruptionWormSpawn,
                MonsterEntities.DEVOURER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkAntlionChargerSpawn, MonsterEntities.ANTLION_CHARGER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGhostSpawn, MonsterEntities.GHOST);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkTimSpawn, MonsterEntities.TIM);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDoctorBonesSpawn, MonsterEntities.DOCTOR_BONES);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkWeddingZombieSpawn, MonsterEntities.THE_GROOM, MonsterEntities.THE_BRIDE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkAngryDandelionSpawn, MonsterEntities.ANGRY_DANDELION);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkAngryDandelionSpawn, MonsterEntities.WINDY_BALLOON);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGnomeSpawn, MonsterEntities.GNOME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkMysticFrogSpawn, CritterEntities.MYSTIC_FROG);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkOldShakingChestSpawn, MonsterEntities.OLD_SHAKING_CHEST);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkClumsyBalloonSlimeSpawn, MonsterEntities.CLUMSY_BALLOON_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkGroundSpawn), MonsterEntities.GOBLIN_WARLOCK);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkGroundSpawn),
                MonsterEntities.PIRATE_DECKHAND, MonsterEntities.PIRATE_CORSAIR, MonsterEntities.PIRATE_DEADEYE, MonsterEntities.PIRATE_CROSSBOWER, MonsterEntities.PIRATE_CAPTAIN);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkSandstormSpawn, MonsterEntities.ANGRY_TUMBLER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPlainSandSharkSpawn), MonsterEntities.SAND_SHARK);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCorruptSandSharkSpawn), MonsterEntities.BONE_BITER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCrimsonSandSharkSpawn), MonsterEntities.FLESH_REAVER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkHallowSandSharkSpawn), MonsterEntities.CRYSTAL_THRESHER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkFungiBulbSpawn, MonsterEntities.FUNGI_BULB);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkFungiBulbSpawn), MonsterEntities.GIANT_FUNGI_BULB);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkClingerSpawn), MonsterEntities.CLINGER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGroundSpawn,
                MonsterEntities.BLOOD_ZOMBIE, MonsterEntities.SPORE_ZOMBIE,
                MonsterEntities.HAT_SPORE_ZOMBIE, MonsterEntities.SNATCHER,
                MonsterEntities.DRIPPLER, MonsterEntities.GOBLIN_SORCERER,
                MonsterEntities.GOBLIN_PEON, MonsterEntities.GOBLIN_ARCHER,
                MonsterEntities.GOBLIN_WARRIOR, MonsterEntities.GOBLIN_THIEF,
                MonsterEntities.ANGER_GOBLIN);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGoblinScoutSpawn, MonsterEntities.GOBLIN_SCOUT);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkUndergroundMonsterSpawn,
                MonsterEntities.MAN_EATER, MonsterEntities.HORNET, MonsterEntities.SPORE_BAT);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn,
                MonsterEntities.SNOW_FLINX, MonsterEntities.ICE_BAT, MonsterEntities.UNDEAD_VIKING);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDungeonMonsterSpawn,
                MonsterEntities.BASE_BONES, MonsterEntities.ANGER_BONES, MonsterEntities.SHORT_BONES,
                MonsterEntities.BIG_BONES, MonsterEntities.BIG_ANGER_BONES,
                MonsterEntities.BIG_MUSCLE_ANGER_BONES, MonsterEntities.BIG_HELMET_ANGER_BONES,
                MonsterEntities.CURSED_SKULL, MonsterEntities.DARK_CASTER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGiantWormSpawn, MonsterEntities.GIANT_WORM);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkTombCrawlerSpawn, MonsterEntities.TOMB_CRAWLER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn,
                MonsterEntities.GIANT_SHELLY, MonsterEntities.CRAWDAD, MonsterEntities.NYMPH, MonsterEntities.CAVE_BAT);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkNetherMonsterSpawn, MonsterEntities.BONE_SERPENT, MonsterEntities.WITHER_BONE_SERPENT, MonsterEntities.HELL_BAT, MonsterEntities.FIRE_IMP);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkFlyingFishSpawn, MonsterEntities.FLYING_FISH);

        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkHighLevelMonsterSpawn, MonsterEntities.HARPY);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkRoutineMonsterSpawn, MonsterEntities.METEOR_HEAD);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkNetherMonsterSpawn, MonsterEntities.DEMON, MonsterEntities.VOODOO_DEMON);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkAntlionSwarmerSpawn,
                MonsterEntities.ANTLION_SWARMER, MonsterEntities.GIANT_ANTLION_SWARMER);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkGraniteElementalSpawn, MonsterEntities.GRANITE_ELEMENTAL);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn, MonsterEntities.GRANITE_GOLEM, MonsterEntities.HOPLITE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkAntlionSpawn, MonsterEntities.ANTLION);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkDesertSpiritSpawn), MonsterEntities.DESERT_SPIRIT);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkWallCreeperSpawn, MonsterEntities.WALL_CREEPER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn), MonsterEntities.BLACK_RECLUSE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRuneWizardSpawn), MonsterEntities.RUNE_WIZARD);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkWaterBoltMimicSpawn, MonsterEntities.WATER_BOLT_MIMIC);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkAngryNimbusSpawn), MonsterEntities.ANGRY_NIMBUS);

        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkWaterMonsterSpawn, MonsterEntities.PIRANHA);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkSurfaceWaterMonsterSpawn, MonsterEntities.SHARK, MonsterEntities.PINK_JELLYFISH);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkUndergroundWaterMonsterSpawn, MonsterEntities.BLUE_JELLYFISH);
    }

    private static void registerHardmodeMonsters(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkDiggerSpawn), MonsterEntities.DIGGER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCorruptionWormSpawn), MonsterEntities.WORLD_FEEDER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.ARMORED_VIKING, MonsterEntities.ICY_MERMAN, MonsterEntities.ILLUMINANT_BAT);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn),
                MonsterEntities.MOSS_HORNET, MonsterEntities.JUNGLE_CREEPER, MonsterEntities.BASILISK);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkWerewolfSpawn), MonsterEntities.WEREWOLF);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkPostMechanicalNetherSpawn, MonsterEntities.LAVA_BAT);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPostMechanicalNetherSpawn), MonsterEntities.RED_DEVIL);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkIceElementalSpawn), MonsterEntities.ICE_ELEMENTAL);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkIceGolemSpawn), MonsterEntities.ICE_GOLEM);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkHighLevelMonsterSpawn), MonsterEntities.WYVERN);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkArchWyvernSpawn), MonsterEntities.ARCH_WYVERN);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn),
                MonsterEntities.CORRUPTOR, MonsterEntities.ENCHANTED_SWORD,
                MonsterEntities.SLIMER);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkSurfaceNightMonsterSpawn), MonsterEntities.GIANT_FLYING_FOX, MonsterEntities.GASTROPOD);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkSurfaceDayMobSpawn), MonsterEntities.DERPLING);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkSurfaceMobSpawn),
                MonsterEntities.PIXIE, MonsterEntities.UNICORN);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRoutineMobSpawn), MonsterEntities.GIANT_TORTOISE);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn), MonsterEntities.CRIMSLIME, MonsterEntities.CORRUPT_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.CHAOS_ELEMENTAL);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.LUMINOUS_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPureDesertUndergroundSpawn), MonsterEntities.GHOUL);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPureOrHallowDesertUndergroundSpawn),
                MonsterEntities.LIGHT_LAMIA, MonsterEntities.SAND_POACHER);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkEvilDesertUndergroundSpawn), MonsterEntities.DARK_LAMIA);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCorruptDesertUndergroundSpawn), MonsterEntities.VILE_GHOUL);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCrimsonDesertUndergroundSpawn), MonsterEntities.TAINTED_GHOUL);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkHallowDesertUndergroundSpawn), MonsterEntities.DREAMER_GHOUL);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkGroundSpawn),
                MonsterEntities.WOODEN_MIMIC);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.GIANT_BAT, MonsterEntities.ARMORED_SKELETON);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRockGolemSpawn), MonsterEntities.ROCK_GOLEM);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.atLeast(GamePhase.PLANTERA, SpawnPlacementChecks::checkDungeonMonsterSpawn),
                MonsterEntities.PALADIN, MonsterEntities.BONE_LEE,
                MonsterEntities.NECROMANCER, MonsterEntities.DIABOLIST,
                MonsterEntities.RAGGED_CASTER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPossessedArmorSpawn), MonsterEntities.POSSESS_ARMOR);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkWraithSpawn), MonsterEntities.WRAITH);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkBelowSurfaceMonsterSpawn),
                MonsterEntities.GOLDEN_MIMIC,
                MonsterEntities.CRIMSON_MIMIC, MonsterEntities.CORRUPT_MIMIC,
                MonsterEntities.HALLOWED_MIMIC, MonsterEntities.JUNGLE_MIMIC);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.ICE_MIMIC, MonsterEntities.ICE_TORTOISE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkNetherMonsterSpawn), MonsterEntities.SHADOW_MIMIC);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkSandMummySpawn), MonsterEntities.MUMMY);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkEbonsandMummySpawn), MonsterEntities.DARK_MUMMY);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCrimsandMummySpawn), MonsterEntities.BLOOD_MUMMY);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPearlsandMummySpawn), MonsterEntities.LIGHT_MUMMY);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn), MonsterEntities.HERPLING);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkUndergroundWaterMonsterSpawn), MonsterEntities.GREEN_JELLYFISH);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkAnglerFishSpawn), MonsterEntities.ANGLER_FISH);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCrimsonWaterMonsterSpawn), MonsterEntities.BLOOD_JELLY);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkFungoFishSpawn), MonsterEntities.FUNGO_FISH);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkCorruptionWaterMonsterSpawn, MonsterEntities.CORRUPT_GOLDFISH);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkCrimsonWaterMonsterSpawn, MonsterEntities.VICIOUS_GOLDFISH);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkWaterMonsterSpawn), MonsterEntities.ARAPAIMA, MonsterEntities.BLOOD_FEEDER);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void group(PortRegisterSpawnPlacementsEvent event, PortSpawnPlacementType placement, SpawnPlacements.SpawnPredicate predicate, RegistryObject... types) {
        // 可变参数的数组元素必须是可具体化类型，否则每个调用点都会创建未经检查的泛型数组。
        // 这里仅在统一注册边界使用原始 RegistryObject，具体实体类型仍由各注册项自身持有。
        for (RegistryObject type : types) {
            event.register((EntityType) type.get(), placement, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, predicate, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
    }
}
