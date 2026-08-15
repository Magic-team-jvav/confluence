package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.mixed.ILevelChunkSection;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.util.DynamicBiomeUtils;

/**
 * 自然生成使用的公共环境校验集合。
 *
 * <p>生物群系修饰器只负责把实体类型放入某个生物群系的候选表，真正生成前仍会经过这里注册的
 * 放置规则。因此高度、维度、昼夜、天气、视野和困难模式等硬约束必须集中在此处，不能只依赖
 * JSON 中的权重或生物群系选择。</p>
 *
 * <p>各方法先施加泰拉瑞亚语义对应的额外门槛，再委托原版怪物或水生动物规则完成亮度、碰撞、
 * 流体等基础检查。这样可以复用原版兼容逻辑，同时保证 1.20.1 与 1.21.1 反向同步时只需比较
 * 一套明确的生成语义。</p>
 */
public final class SpawnPlacementChecks {
    private SpawnPlacementChecks() {}

    public static boolean checkRoutineMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                   MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < 260 && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkGroundSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                           MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() >= 60 && pos.getY() < 260
                && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkUndergroundMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                       MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() >= -55 && pos.getY() <= 30
                && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkCaveMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() >= -55 && pos.getY() <= -20
                && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkDungeonMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                   MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() >= -35 && pos.getY() <= 40
                && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkHighLevelMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                     MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() >= 280 && pos.getY() < level.getMaxBuildHeight()
                && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkNetherMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                  MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.dimension() == Level.NETHER
                && pos.getY() >= 30 && pos.getY() <= 100
                && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /**
     * 检查墓地敌怪的自然生成条件。
     *
     * <p>墓地是按区块段实时统计出的环境，而不是一个可直接写入生物群系修饰器的固定群系。
     * 因此实体仍需进入主世界候选表，再在最终放置检查中同时验证墓地环境与原版怪物规则。</p>
     */
    public static boolean checkGraveyardMonsterSpawn(
            EntityType<? extends Mob> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random) {
        if (!(level instanceof Level world)) {
            return false;
        }
        ILevelChunkSection section = DynamicBiomeUtils.getISection(world, pos);
        return section != null && section.confluence$isGraveyard()
                && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /**
     * 检查下界小动物的放置条件。
     *
     * <p>熔岩小动物不能复用 {@link net.minecraft.world.entity.animal.Animal#checkAnimalSpawnRules}：
     * 后者要求较高亮度与普通动物可生成方块，会让灰烬群系中的候选项始终无法落地。这里保留
     * 原版实体碰撞和承载方块检查，只额外限定下界与地狱高度带。</p>
     */
    public static boolean checkNetherCritterSpawn(
            EntityType<? extends Mob> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random) {
        return level instanceof Level world && world.dimension() == Level.NETHER
                && pos.getY() >= 30 && pos.getY() <= 100
                && Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkFlyingFishSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                               MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.isRaining()
                && checkGroundSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkBloodCrawlerSpawn(EntityType<? extends Mob> type,
                                                 ServerLevelAccessor level,
                                                 MobSpawnType spawnType,
                                                 BlockPos pos,
                                                 RandomSource random) {
        // 1.21 的血爬虫无视亮度，但只允许通过自然生成流程出现。
        return spawnType == MobSpawnType.NATURAL;
    }

    public static boolean checkNightSurfaceMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                        MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.isNight() && level.canSeeSky(pos)
                && checkGroundSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkPossessedArmorSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                   MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!checkMonsterSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }
        int y = pos.getY();
        // 地下可全天生成；地表高度带仅允许夜晚生成。
        boolean validAltitude = y >= level.getMinBuildHeight()
                && (y < 40 || y < 260 && level instanceof Level world && world.isNight());
        return validAltitude && level instanceof Level world && hasClearColumn(world, pos);
    }

    @SuppressWarnings("unchecked")
    public static boolean checkWaterMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                 MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return WaterAnimal.checkSurfaceWaterAnimalSpawnRules(
                (EntityType<? extends WaterAnimal>) (EntityType<?>) type,
                level, spawnType, pos, random);
    }

    public static boolean checkGoblinScoutSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.isDay()
                && checkGroundSpawn(type, level, spawnType, pos, random);
    }

    public static <T extends Entity> SpawnPlacements.SpawnPredicate<T> hardmode(
            SpawnPlacements.SpawnPredicate<T> predicate) {
        // 包装既有规则而不是复制一份，确保开启困难模式只增加进度门槛，不改变环境语义。
        return (type, level, spawnType, pos, random) -> level instanceof ServerLevel serverLevel
                && IMinecraftServer.isHardmode(serverLevel.getServer())
                && predicate.test(type, level, spawnType, pos, random);
    }

    /**
     * 执行所有敌对生物共用的原版基础放置检查，并按配置决定是否保留亮度门槛。
     *
     * <p>具有额外昼夜、地形或进度条件的实体也必须在自身条件之后调用本方法，不能直接调用
     * {@link Monster#checkMonsterSpawnRules}，否则它们会绕过统一的亮度配置。</p>
     */
    @SuppressWarnings("unchecked")
    public static boolean checkMonsterSpawnRules(
            EntityType<? extends Mob> type, ServerLevelAccessor level,
            MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        // 忽略光照时仍调用原版 Mob 规则，保留碰撞、刷怪方块和世界边界等基础安全检查。
        if (CommonConfigs.SPAWN_WITHOUT_LIGHT.get()) {
            return Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
        }
        EntityType<? extends Monster> monsterType =
                (EntityType<? extends Monster>) (EntityType<?>) type;
        return Monster.checkMonsterSpawnRules(monsterType, level, spawnType, pos, random);
    }

    private static boolean hasClearColumn(Level level, BlockPos pos) {
        // 附身盔甲需要无遮挡的纵向空间；遇到第一个完整碰撞方块即可提前失败。
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (cursor.getY() < level.getMaxBuildHeight()) {
            if (level.getBlockState(cursor).isCollisionShapeFullBlock(level, cursor)) return false;
            cursor.move(0, 1, 0);
        }
        return true;
    }
}
