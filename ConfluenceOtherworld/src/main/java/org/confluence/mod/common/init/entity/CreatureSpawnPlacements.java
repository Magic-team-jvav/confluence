package org.confluence.mod.common.init.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.entity.SpawnPlacementChecks;
import org.confluence.mod.common.entity.monster.DemonEye;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;
import org.confluence.mod.common.entity.monster.slime.BaseSlime;
import org.mesdag.portlib.event.entity.PortRegisterSpawnPlacementsEvent;
import org.mesdag.portlib.wrapper.world.entity.PortSpawnPlacementType;
import org.mesdag.portlib.wrapper.world.entity.PortSpawnPlacementTypes;

/**
 * 所有进入自然生成数据的生物放置规则注册中心。
 *
 * <p>注册通过 PortLib 事件完成，保持 Forge 1.20.1 与 NeoForge 1.21.1 的结构对应。任何被
 * 生物群系修饰器列为自然生成候选的实体，都必须在这里获得明确的放置类型和最终谓词；否则
 * 数据包看似包含该生物，运行时却可能沿用错误规则或完全无法生成。</p>
 *
 * <p>实体按生态角色和游戏进度分组。相同语义共用一个谓词，困难模式组再由
 * {@link SpawnPlacementChecks#hardmode(SpawnPlacements.SpawnPredicate)} 叠加进度门槛。
 * {@link PortRegisterSpawnPlacementsEvent.Operation#REPLACE} 用于明确覆盖默认规则，避免模组加载
 * 顺序导致多个谓词以不可预测方式组合。</p>
 */
public final class CreatureSpawnPlacements {
    private CreatureSpawnPlacements() {}

    public static void register(PortRegisterSpawnPlacementsEvent event) {
        registerCritters(event);
        registerSlimes(event);
        registerPreHardmodeMonsters(event);
        registerHardmodeMonsters(event);
    }

    private static void registerCritters(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.ON_GROUND, Animal::checkAnimalSpawnRules,
                CritterEntities.BUNNY, CritterEntities.JEWEL_BUNNY,
                CritterEntities.EXPLOSIVE_BUNNY, CritterEntities.HOSTILE_BUNNY,
                CritterEntities.BIRD, CritterEntities.BLUE_JAY, CritterEntities.CARDINAL,
                CritterEntities.SQUIRREL, CritterEntities.RED_SQUIRREL,
                CritterEntities.JEWEL_SQUIRREL, CritterEntities.DUCK, CritterEntities.CRAB,
                CritterEntities.WORM, CritterEntities.BUTTERFLY, CritterEntities.FAIRY,
                CritterEntities.FEALING, CritterEntities.GLOWING_SNAIL, CritterEntities.GRUBBY,
                CritterEntities.MAGGOT, CritterEntities.SLUGGY,
                CritterEntities.SNAIL, CritterEntities.SCORPION,
                CritterEntities.PRISMATIC_LACEWING, CritterEntities.DRAGONFLY,
                CritterEntities.GRASSHOPPER, CritterEntities.LADYBUG);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks::checkNetherCritterSpawn,
                CritterEntities.MAGMA_SNAIL, CritterEntities.HELL_BUTTERFLY);
    }

    private static void registerSlimes(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.ON_GROUND, BaseSlime::checkSlimeSpawn,
                MonsterEntities.BLUE_SLIME, MonsterEntities.GREEN_SLIME,
                MonsterEntities.PURPLE_SLIME, MonsterEntities.PINK_SLIME,
                MonsterEntities.DESERT_SLIME, MonsterEntities.JUNGLE_SLIME,
                MonsterEntities.ICE_SLIME, MonsterEntities.TROPIC_SLIME,
                MonsterEntities.YELLOW_SLIME, MonsterEntities.RED_SLIME,
                MonsterEntities.BLACK_SLIME, MonsterEntities.LAVA_SLIME,
                MonsterEntities.SWAMP_SLIME, MonsterEntities.DUNGEON_SLIME,
                MonsterEntities.GREEN_DUMPLING_SLIME);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkUndergroundMonsterSpawn,
                MonsterEntities.SPIKED_JUNGLE_SLIME, MonsterEntities.SPIKED_ICE_SLIME);
    }

    private static void registerPreHardmodeMonsters(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.ON_GROUND, DemonEye::checkDemonEyeSpawnRules,
                MonsterEntities.DEMON_EYE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, Zombie::checkZombieSpawnRules,
                MonsterEntities.ZOMBIE);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkRoutineMonsterSpawn,
                MonsterEntities.BLOODY_SPORE,
                MonsterEntities.FACE_MONSTER, MonsterEntities.SPORE_SKELETON,
                MonsterEntities.DECAYEDER, MonsterEntities.CRIMERA,
                MonsterEntities.EATER_OF_SOULS, MonsterEntities.METEOR_HEAD,
                MonsterEntities.DEVOURER, MonsterEntities.JUNGLE_BAT);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks::checkGraveyardMonsterSpawn,
                MonsterEntities.GHOST);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkBloodCrawlerSpawn,
                MonsterEntities.BLOOD_CRAWLER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGroundSpawn,
                MonsterEntities.BLOOD_ZOMBIE, MonsterEntities.SPORE_ZOMBIE,
                MonsterEntities.HAT_SPORE_ZOMBIE, MonsterEntities.SNATCHER,
                MonsterEntities.DRIPPLER, MonsterEntities.GOBLIN_SORCERER,
                MonsterEntities.GOBLIN_PEON, MonsterEntities.GOBLIN_ARCHER,
                MonsterEntities.GOBLIN_WARRIOR, MonsterEntities.GOBLIN_THIEF,
                MonsterEntities.ANGER_GOBLIN);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkGoblinScoutSpawn,
                MonsterEntities.GOBLIN_SCOUT);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkUndergroundMonsterSpawn,
                MonsterEntities.GIANT_SHELLY, MonsterEntities.CRAWDAD, MonsterEntities.NYMPH,
                MonsterEntities.MAN_EATER, MonsterEntities.SNOW_FLINX, MonsterEntities.HORNET,
                MonsterEntities.CAVE_BAT, MonsterEntities.ICE_BAT, MonsterEntities.SPORE_BAT,
                MonsterEntities.BASE_BONES, MonsterEntities.UNDEAD_VIKING);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkDungeonMonsterSpawn,
                MonsterEntities.ANGER_BONES, MonsterEntities.SHORT_BONES,
                MonsterEntities.BIG_BONES, MonsterEntities.BIG_ANGER_BONES,
                MonsterEntities.BIG_MUSCLE_ANGER_BONES, MonsterEntities.BIG_HELMET_ANGER_BONES,
                MonsterEntities.CURSED_SKULL, MonsterEntities.DARK_CASTER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkCaveMonsterSpawn,
                MonsterEntities.GIANT_WORM, MonsterEntities.TOMB_CRAWLER);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkNetherMonsterSpawn,
                MonsterEntities.BONE_SERPENT, MonsterEntities.WITHER_BONE_SERPENT,
                MonsterEntities.HELL_BAT, MonsterEntities.FIRE_IMP);
        group(event, PortSpawnPlacementTypes.ON_GROUND, SpawnPlacementChecks::checkFlyingFishSpawn,
                MonsterEntities.FLYING_FISH);

        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkHighLevelMonsterSpawn,
                MonsterEntities.HARPY);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkNetherMonsterSpawn,
                MonsterEntities.DEMON, MonsterEntities.VOODOO_DEMON,
                MonsterEntities.WANDERING_EYE_FISH);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS, SpawnPlacementChecks::checkUndergroundMonsterSpawn,
                MonsterEntities.ANTLION_SWARMER, MonsterEntities.GIANT_ANTLION_SWARMER,
                MonsterEntities.GRANITE_ELEMENTAL);

        group(event, PortSpawnPlacementTypes.IN_WATER, SpawnPlacementChecks::checkWaterMonsterSpawn,
                MonsterEntities.PIRANHA, MonsterEntities.SHARK,
                MonsterEntities.BLUE_JELLYFISH, MonsterEntities.PINK_JELLYFISH);
    }

    private static void registerHardmodeMonsters(PortRegisterSpawnPlacementsEvent event) {
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkHighLevelMonsterSpawn),
                MonsterEntities.WYVERN, MonsterEntities.ARCH_WYVERN);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn),
                MonsterEntities.CORRUPTOR, MonsterEntities.ENCHANTED_SWORD,
                MonsterEntities.GIANT_FLYING_FOX, MonsterEntities.SLIMER);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkGroundSpawn),
                MonsterEntities.PIXIE, MonsterEntities.CRIMSLIME, MonsterEntities.CORRUPT_SLIME,
                MonsterEntities.GIANT_TORTOISE, MonsterEntities.UNICORN,
                MonsterEntities.GASTROPOD, MonsterEntities.CHAOS_ELEMENTAL);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkUndergroundMonsterSpawn),
                MonsterEntities.LUMINOUS_SLIME, MonsterEntities.WOODEN_MIMIC,
                MonsterEntities.DARK_LAMIA, MonsterEntities.LIGHT_LAMIA,
                MonsterEntities.GHOUL, MonsterEntities.TAINTED_GHOUL,
                MonsterEntities.VILE_GHOUL, MonsterEntities.DREAMER_GHOUL,
                MonsterEntities.SAND_POACHER, MonsterEntities.GIANT_BAT);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkDungeonMonsterSpawn),
                MonsterEntities.PALADIN, MonsterEntities.BONE_LEE,
                MonsterEntities.NECROMANCER, MonsterEntities.DIABOLIST,
                MonsterEntities.RAGGED_CASTER);
        group(event, PortSpawnPlacementTypes.NO_RESTRICTIONS,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkDungeonMonsterSpawn),
                MonsterEntities.BLAZING_WHEEL, MonsterEntities.SPIKE_BALL);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkPossessedArmorSpawn),
                MonsterEntities.POSSESS_ARMOR);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkNightSurfaceMonsterSpawn),
                MonsterEntities.WRAITH);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkCaveMonsterSpawn),
                MonsterEntities.GOLDEN_MIMIC, MonsterEntities.ICE_MIMIC,
                MonsterEntities.CRIMSON_MIMIC, MonsterEntities.CORRUPT_MIMIC,
                MonsterEntities.HALLOWED_MIMIC, MonsterEntities.JUNGLE_MIMIC);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkNetherMonsterSpawn),
                MonsterEntities.SHADOW_MIMIC);
        group(event, PortSpawnPlacementTypes.ON_GROUND,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkRoutineMonsterSpawn),
                MonsterEntities.MUMMY, MonsterEntities.DARK_MUMMY,
                MonsterEntities.BLOOD_MUMMY, MonsterEntities.LIGHT_MUMMY,
                MonsterEntities.DERPLING, MonsterEntities.HERPLING);
        group(event, PortSpawnPlacementTypes.IN_WATER,
                SpawnPlacementChecks.<Mob>hardmode(SpawnPlacementChecks::checkWaterMonsterSpawn),
                MonsterEntities.GREEN_JELLYFISH, MonsterEntities.ARAPAIMA,
                MonsterEntities.BLOOD_FEEDER);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void group(PortRegisterSpawnPlacementsEvent event,
                              PortSpawnPlacementType placement,
                              SpawnPlacements.SpawnPredicate predicate,
                              RegistryObject<? extends EntityType<?>>... types) {
        // 泛型在 RegistryObject<?> 边界被擦除；所有调用点都只传入 Mob 类型，并由测试审计覆盖。
        for (RegistryObject<? extends EntityType<?>> type : types) {
            event.register((EntityType) type.get(), placement, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    predicate, PortRegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
    }
}
