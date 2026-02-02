package org.confluence.terraentity.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.confluence.terraentity.config.ServerConfig;

import static net.minecraft.world.entity.Mob.checkMobSpawnRules;

public class SpawnPlacementChecks {

    public static boolean checkTEMonsterWithConfig(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        if (ServerConfig.SPAWN_WITHOUT_LIGHT.get()) {
            return checkMobSpawnRules(type, pLevel, pSpawnType, pPos, pRandom);
        } else {
            return Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom) && checkMobSpawnRules(type, pLevel, pSpawnType, pPos, pRandom);
        }
    }

    public static boolean checkTEMonster(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        return Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom) && checkMobSpawnRules(type, pLevel, pSpawnType, pPos, pRandom);
    }

    public static boolean checkFlyingFishSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }

        // 判断是否下雨
        if (!level.isRaining()) {
            return false;
        }

        int y = pPos.getY();
        if (y < 60 || y >= 260) {
            return false; // 只能生成在 y = 60 到 y = 260 之间
        }

        return true;
    }

    public static boolean checkGroundSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {

        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }
        int y = pPos.getY();
        if (y < 60 || y >= 260) {
            return false; // 只能生成在 y = 60 到 y = 260 之间
        }

        return true;
    }

    public static boolean checkRoutineMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }

        int y = pPos.getY();
        if (y >= 260) {
            return false; // 不能生成在 y = 260 或更高的位置
        }

        return true;
    }

    public static boolean checkGoblinScoutSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }

        int y = pPos.getY();
        if (y < 60 || y >= 260) {
            return false; // 只能生成在 y = 60 到 y = 260 之间
        }

        return level.isDay();
    }

    public static boolean checkOnlyDayMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }

        int y = pPos.getY();
        if (y >= 260) {
            return false; // 不能生成在 y = 260 或更高的位置
        }

        return level.isDay();
    }

    public static boolean checkDemonEyeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level level)) {
            return false;
        }
        if (checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            // 新月100%，其他80%
            if (pPos.getY() >= 60 && pPos.getY() < 260 && level.isNight()) {
                if (level.getMoonPhase() == 4) {
                    for (BlockPos.MutableBlockPos blockPos = pPos.mutable(); blockPos.getY() < level.getMaxBuildHeight(); blockPos.move(0, 1, 0)) {
                        if (level.getBlockState(blockPos).isCollisionShapeFullBlock(level, blockPos)) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return level.random.nextInt(99) < 80;
                }
            }
        }
        return false;
    }

    public static boolean checkPossessArmorSpawnCondition(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level level)) {
            return false;
        }
        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }
        int y = pPos.getY();
        if (y >= -64 && y < 40) {
            for (BlockPos.MutableBlockPos blockPos = pPos.mutable(); blockPos.getY() < level.getMaxBuildHeight(); blockPos.move(0, 1, 0)) {
                if (level.getBlockState(blockPos).isCollisionShapeFullBlock(level, blockPos)) {
                    return false;
                }
            }
            return true;
        } else if (y >= 40 && y < 260 && level.isNight()) {
            for (BlockPos.MutableBlockPos blockPos = pPos.mutable(); blockPos.getY() < level.getMaxBuildHeight(); blockPos.move(0, 1, 0)) {
                if (level.getBlockState(blockPos).isCollisionShapeFullBlock(level, blockPos)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean checkNormalAnimalSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return Animal.checkAnimalSpawnRules(null, pLevel, pSpawnType, pPos, pRandom);
    }


    public static boolean checkUndergroundMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }

        int y = pPos.getY();
        if (y < -55 || y > 30) {
            return false; // 只能生成在 y = -55 到 y = 30 之间
        }

        return true;
    }

    public static boolean checkCaveMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }

        int y = pPos.getY();
        if (y < -55 || y > -20) {
            return false; // 只能生成在 y = -55 到 y = -20 之间
        }

        return true;
    }

    public static boolean checkDungeonMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }

        int y = pPos.getY();
        if (y < -35 || y > 40) {
            return false; // 只能生成在 y = -35 到 y = 40 之间
        }

        return true;
    }

    public static boolean checkHighLevelMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }

        int y = pPos.getY();
        if (y < 280 || y > 320) {
            return false; // 只能生成在 y = 280 到 y = 320 之间
        }

        return true;
    }

    public static boolean checkNetherMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        if (!(pLevel instanceof Level)) {
            return false; // 如果 pLevel 不是 Level 的实例，返回 false
        }

        if (!checkTEMonsterWithConfig(type, pLevel, pSpawnType, pPos, pRandom)) {
            return false;
        }

        int y = pPos.getY();
        if (y < 30 || y > 100) {
            return false; // 只能生成在 y = 30 到 y = 100 之间
        }

        return true;
    }

    public static <T extends Entity> SpawnPlacements.SpawnPredicate<T> checkHardmode(SpawnPlacements.SpawnPredicate<T> predicate) {
        return predicate; // confluence mixin here
    }
}
