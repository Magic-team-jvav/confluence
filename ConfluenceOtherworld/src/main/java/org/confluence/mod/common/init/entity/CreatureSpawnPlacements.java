package org.confluence.mod.common.init.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.SpawnPlacementChecks;
import org.confluence.mod.common.entity.animal.Fairy;
import org.confluence.mod.common.entity.animal.Worm;
import org.confluence.mod.common.entity.monster.GraniteElemental;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
import org.confluence.mod.common.entity.monster.slime.BaseSlime;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.OverworldUtils;
import org.mesdag.portlib.event.entity.PortRegisterSpawnPlacementsEvent;
import org.mesdag.portlib.wrapper.world.entity.PortSpawnPlacementType;
import org.mesdag.portlib.wrapper.world.entity.PortSpawnPlacementTypes;

/// 所有进入自然生成数据的生物放置规则注册中心。
///
/// 注册通过 PortLib 事件完成。任何被
/// 生物群系修饰器列为自然生成候选的实体，都必须在这里获得明确的放置类型和最终谓词；否则
/// 数据包看似包含该生物，运行时却可能沿用错误规则或完全无法生成。
///
/// 实体按生态角色和游戏进度分组。相同语义共用一个谓词，困难模式组再由
/// {@link SpawnPlacementChecks#hardmode(SpawnPlacements.SpawnPredicate)} 叠加进度门槛。
/// {@link PortRegisterSpawnPlacementsEvent.Operation#REPLACE} 用于明确覆盖默认规则，避免模组加载
/// 顺序导致多个谓词以不可预测方式组合。
public final class CreatureSpawnPlacements {
    /**
     * 七彩草蛉只限制同一活动区域内的数量，不让远处神圣地玩家互相影响。
     */
    private static final double LACEWING_POPULATION_RADIUS = 96.0;
    private static final double LACEWING_POPULATION_HEIGHT = 48.0;

    private CreatureSpawnPlacements() {}

    public static void register(PortRegisterSpawnPlacementsEvent event) {
        registerCritters(event);
        registerSlimes(event);
        registerPreHardmodeMonsters(event);
        registerHardmodeMonsters(event);
    }

    private static void registerCritters(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.ON_GROUND, Animal::checkAnimalSpawnRules,
                CritterEntities.BUNNY,
                CritterEntities.EXPLOSIVE_BUNNY, CritterEntities.HOSTILE_BUNNY,
                CritterEntities.BIRD, CritterEntities.BLUE_JAY, CritterEntities.CARDINAL,
                CritterEntities.SQUIRREL, CritterEntities.RED_SQUIRREL,
                CritterEntities.DUCK,
                CritterEntities.GLOWING_SNAIL, CritterEntities.GRUBBY,
                CritterEntities.MAGGOT, CritterEntities.SLUGGY,
                CritterEntities.SNAIL, CritterEntities.SCORPION,
                CritterEntities.GRASSHOPPER);
        event.register(CritterEntities.CRAB.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, CreatureSpawnPlacements::checkCavernCritterSpawn,
                CritterEntities.JEWEL_BUNNY, CritterEntities.JEWEL_SQUIRREL);
        group(event, PortSpawnPlacementTypes.ON_GROUND, CreatureSpawnPlacements::checkSurfaceDayCritterSpawn,
                CritterEntities.BUTTERFLY, CritterEntities.DRAGONFLY);
        event.register(CritterEntities.FAIRY.get(), PortSpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Fairy::checkFairySpawn, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.FEALING.get(), PortSpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, CreatureSpawnPlacements::checkNetherDayCritterSpawn,
                CritterEntities.HELL_BUTTERFLY, CritterEntities.MAGMA_SNAIL);
        event.register(CritterEntities.LADYBUG.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CreatureSpawnPlacements::checkLadybugSpawn, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.PRISMATIC_LACEWING.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CreatureSpawnPlacements::checkPrismaticLacewingSpawn, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CritterEntities.WORM.get(), PortSpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CreatureSpawnPlacements::checkWormSpawn, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static void registerSlimes(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.ON_GROUND, BaseSlime::checkSlimeSpawn,
                MonsterEntities.BLUE_SLIME, MonsterEntities.GREEN_SLIME,
                MonsterEntities.PURPLE_SLIME, MonsterEntities.PINK_SLIME,
                MonsterEntities.DESERT_SLIME, MonsterEntities.JUNGLE_SLIME,
                MonsterEntities.ICE_SLIME, MonsterEntities.TROPIC_SLIME,
                MonsterEntities.YELLOW_SLIME, MonsterEntities.RED_SLIME,
                MonsterEntities.BLACK_SLIME, MonsterEntities.MOTHER_SLIME, MonsterEntities.LAVA_SLIME,
                MonsterEntities.SWAMP_SLIME, MonsterEntities.DUNGEON_SLIME,
                MonsterEntities.GREEN_DUMPLING_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkUndergroundMonsterSpawn, MonsterEntities.SPIKED_JUNGLE_SLIME, MonsterEntities.SPIKED_ICE_SLIME);
    }

    private static void registerPreHardmodeMonsters(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDemonEyeSpawn, MonsterEntities.DEMON_EYE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, Zombie::checkZombieSpawnRules, MonsterEntities.ZOMBIE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkRoutineMonsterSpawn,
                MonsterEntities.BLOODY_SPORE,
                MonsterEntities.FACE_MONSTER, MonsterEntities.SPORE_SKELETON,
                MonsterEntities.DECAYEDER, MonsterEntities.CRIMERA,
                MonsterEntities.EATER_OF_SOULS, MonsterEntities.BLOOD_CRAWLER,
                MonsterEntities.JUNGLE_BAT);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkBelowSurfaceMonsterSpawn,
                MonsterEntities.DEVOURER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGhostSpawn, MonsterEntities.GHOST);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGroundSpawn,
                MonsterEntities.BLOOD_ZOMBIE, MonsterEntities.SPORE_ZOMBIE,
                MonsterEntities.HAT_SPORE_ZOMBIE, MonsterEntities.SNATCHER,
                MonsterEntities.DRIPPLER, MonsterEntities.GOBLIN_SORCERER,
                MonsterEntities.GOBLIN_PEON, MonsterEntities.GOBLIN_ARCHER,
                MonsterEntities.GOBLIN_WARRIOR, MonsterEntities.GOBLIN_THIEF,
                MonsterEntities.ANGER_GOBLIN);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGoblinScoutSpawn, MonsterEntities.GOBLIN_SCOUT);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkUndergroundMonsterSpawn,
                MonsterEntities.MAN_EATER, MonsterEntities.SNOW_FLINX, MonsterEntities.HORNET,
                MonsterEntities.ICE_BAT, MonsterEntities.SPORE_BAT, MonsterEntities.UNDEAD_VIKING);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDungeonMonsterSpawn,
                MonsterEntities.BASE_BONES, MonsterEntities.ANGER_BONES, MonsterEntities.SHORT_BONES,
                MonsterEntities.BIG_BONES, MonsterEntities.BIG_ANGER_BONES,
                MonsterEntities.BIG_MUSCLE_ANGER_BONES, MonsterEntities.BIG_HELMET_ANGER_BONES,
                MonsterEntities.CURSED_SKULL, MonsterEntities.DARK_CASTER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn,
                MonsterEntities.GIANT_WORM, MonsterEntities.TOMB_CRAWLER,
                MonsterEntities.GIANT_SHELLY, MonsterEntities.CRAWDAD, MonsterEntities.NYMPH, MonsterEntities.CAVE_BAT);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkNetherMonsterSpawn, MonsterEntities.BONE_SERPENT, MonsterEntities.WITHER_BONE_SERPENT, MonsterEntities.HELL_BAT, MonsterEntities.FIRE_IMP);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkFlyingFishSpawn, MonsterEntities.FLYING_FISH);

        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkHighLevelMonsterSpawn, MonsterEntities.HARPY);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkRoutineMonsterSpawn, MonsterEntities.METEOR_HEAD);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkNetherMonsterSpawn, MonsterEntities.DEMON, MonsterEntities.VOODOO_DEMON);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkUndergroundMonsterSpawn, MonsterEntities.ANTLION_SWARMER, MonsterEntities.GIANT_ANTLION_SWARMER);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, GraniteElemental::checkSpawn, MonsterEntities.GRANITE_ELEMENTAL);

        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkWaterMonsterSpawn, MonsterEntities.PIRANHA);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkSurfaceWaterMonsterSpawn, MonsterEntities.SHARK, MonsterEntities.PINK_JELLYFISH);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkUndergroundWaterMonsterSpawn, MonsterEntities.BLUE_JELLYFISH);
    }

    private static void registerHardmodeMonsters(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkHighLevelMonsterSpawn), MonsterEntities.WYVERN, MonsterEntities.ARCH_WYVERN);
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
                SpawnPlacementChecks.hardmode(BaseSlime::checkSlimeSpawn), MonsterEntities.CRIMSLIME, MonsterEntities.CORRUPT_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.CHAOS_ELEMENTAL);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn),
                MonsterEntities.LUMINOUS_SLIME,
                MonsterEntities.DARK_LAMIA, MonsterEntities.LIGHT_LAMIA,
                MonsterEntities.GHOUL, MonsterEntities.TAINTED_GHOUL,
                MonsterEntities.VILE_GHOUL, MonsterEntities.DREAMER_GHOUL,
                MonsterEntities.SAND_POACHER);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkGroundSpawn),
                MonsterEntities.WOODEN_MIMIC);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.GIANT_BAT);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                atLeast(GamePhase.PLANTERA, SpawnPlacementChecks::checkDungeonMonsterSpawn),
                MonsterEntities.PALADIN, MonsterEntities.BONE_LEE,
                MonsterEntities.NECROMANCER, MonsterEntities.DIABOLIST,
                MonsterEntities.RAGGED_CASTER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkPossessedArmorSpawn), MonsterEntities.POSSESS_ARMOR);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkWraithSpawn), MonsterEntities.WRAITH);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkBelowSurfaceMonsterSpawn),
                MonsterEntities.GOLDEN_MIMIC, MonsterEntities.ICE_MIMIC,
                MonsterEntities.CRIMSON_MIMIC, MonsterEntities.CORRUPT_MIMIC,
                MonsterEntities.HALLOWED_MIMIC, MonsterEntities.JUNGLE_MIMIC);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkNetherMonsterSpawn), MonsterEntities.SHADOW_MIMIC);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn),
                MonsterEntities.MUMMY, MonsterEntities.DARK_MUMMY,
                MonsterEntities.BLOOD_MUMMY, MonsterEntities.LIGHT_MUMMY,
                MonsterEntities.HERPLING);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkUndergroundWaterMonsterSpawn), MonsterEntities.GREEN_JELLYFISH);
        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks.hardmode(SpawnPlacementChecks::checkWaterMonsterSpawn), MonsterEntities.ARAPAIMA, MonsterEntities.BLOOD_FEEDER);
    }

    private static boolean checkWormSpawn(EntityType<Worm> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        int y = pos.getY();
        int surfaceY = OverworldUtils.getSurfaceY();
        if (y > surfaceY && y < OverworldUtils.getSpaceY() && ModUtils.isRainingAt(serverLevel, pos))
            return true;
        return y > OverworldUtils.getUndergroundY() && y < surfaceY;
    }

    private static boolean checkCavernCritterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getUndergroundY() && Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    private static boolean checkSurfaceDayCritterSpawn(EntityType<? extends Animal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof ServerLevel serverLevel && serverLevel.isDay() && pos.getY() > OverworldUtils.getSurfaceY() && Animal.checkAnimalSpawnRules(type, level, spawnType, pos, random);
    }

    private static boolean checkNetherDayCritterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof ServerLevel serverLevel && serverLevel.getServer().overworld().isDay() && Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    private static boolean checkLadybugSpawn(EntityType<? extends Animal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        ConfluenceData data = ConfluenceData.get(serverLevel);
        float windSquared = data.getWindSpeedX() * data.getWindSpeedX() + data.getWindSpeedZ() * data.getWindSpeedZ();
        return windSquared >= 0.25F && checkSurfaceDayCritterSpawn(type, level, spawnType, pos, random);
    }

    private static boolean checkPrismaticLacewingSpawn(EntityType<? extends Animal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel serverLevel)
                || !KillBoard.INSTANCE.getGamePhase().isAtLeast(GamePhase.PLANTERA)
                || !LibDateUtils.isWithinDayTime(LibDateUtils._19$30, LibDateUtils._00$00, serverLevel)
                || pos.getY() <= OverworldUtils.getSurfaceY()) {
            return false;
        }
        AABB populationArea = new AABB(pos).inflate(LACEWING_POPULATION_RADIUS, LACEWING_POPULATION_HEIGHT, LACEWING_POPULATION_RADIUS);
        if (!serverLevel.getEntities(type, populationArea, LivingEntity::isAlive).isEmpty())
            return false;
        return Animal.checkAnimalSpawnRules(type, level, spawnType, pos, random);
    }

    private static <T extends Entity> SpawnPlacements.SpawnPredicate<T> atLeast(GamePhase phase, SpawnPlacements.SpawnPredicate<T> predicate) {
        return (type, level, spawnType, pos, random) -> KillBoard.INSTANCE.getGamePhase().isAtLeast(phase)
                && predicate.test(type, level, spawnType, pos, random);
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
