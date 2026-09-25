package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.entity.animal.Worm;
import org.confluence.mod.common.entity.monster.WaterBoltMimic;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.gameevent.SandstormGameEvent;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModBlockCounters;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.util.DateUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.OverworldUtils;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/// 自然生成使用的公共环境校验集合。
///
/// 生物群系修饰器只负责把实体类型放入某个生物群系的候选表，真正生成前仍会经过这里注册的
/// 放置规则。因此高度、维度、昼夜、天气、视野和困难模式等硬约束必须集中在此处，不能只依赖
/// JSON 中的权重或生物群系选择。
///
/// 各方法负责环境与支撑条件；实体遮挡检查仍由后续生成流程执行。
/// 水生敌怪使用水域条件，不能复用要求脚下支撑方块的陆生 Mob 规则。
public final class SpawnPlacementChecks {
    /// 草蛉与花岗精只限制局部活动区，不让远处独立洞穴互相占用名额。
    private static final double LACEWING_POPULATION_RADIUS = 96.0;
    private static final double LACEWING_POPULATION_HEIGHT = 48.0;
    private static final double GRANITE_POPULATION_RADIUS = 64.0;
    private static final double GRANITE_POPULATION_HEIGHT = 32.0;

    private SpawnPlacementChecks() {}

    /// 史莱姆：注册处按生态角色分组，每个规则只处理一种环境语义。
    /// 普通地表史莱姆仅在白天、露天且有地面支撑的位置生成。
    public static boolean checkSurfaceDaySlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.canSeeSky(pos) && checkSurfaceDayMobSpawn(type, level, spawnType, pos, random);
    }

    /// 冰雪史莱姆沿用地表白天规则，并要求雪原或冰雪群系。
    public static boolean checkSnowySurfaceDaySlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isSnowy(level.getBiome(pos)) && checkSurfaceDaySlimeSpawn(type, level, spawnType, pos, random);
    }

    /// 丛林史莱姆沿用地表白天规则；即便群系带繁茂标签，也仍受地表高度约束。
    public static boolean checkJungleSurfaceDaySlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return (OverworldUtils.isJungle(biome) || biome.is(PortTags.Biomes.IS_LUSH))
                && checkSurfaceDaySlimeSpawn(type, level, spawnType, pos, random);
    }

    /// 黄、红史莱姆限定在地表与洞穴分界之间的浅层地下。
    public static boolean checkShallowUndergroundSlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() >= OverworldUtils.getUndergroundY() && pos.getY() < OverworldUtils.getSurfaceY()
                && level.getBrightness(LightLayer.SKY, pos) == 0
                && checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 黑史莱姆和史莱姆之母只在无天光的洞穴层生成。
    public static boolean checkCaveSlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBrightness(LightLayer.SKY, pos) == 0
                && checkCaveMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 尖刺冰雪史莱姆只在洞穴层的雪原或冰雪群系生成。
    public static boolean checkSnowyCaveSlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isSnowy(level.getBiome(pos)) && checkCaveSlimeSpawn(type, level, spawnType, pos, random);
    }

    /// 尖刺丛林史莱姆只在地下丛林对应群系生成。
    public static boolean checkJungleUndergroundSlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return (OverworldUtils.isJungle(biome) || biome.is(PortTags.Biomes.IS_LUSH))
                && level.getBrightness(LightLayer.SKY, pos) == 0
                && checkUndergroundMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 沙史莱姆只在地下沙漠对应群系生成。
    public static boolean checkDesertUndergroundSlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isDesert(level.getBiome(pos)) && level.getBrightness(LightLayer.SKY, pos) == 0
                && checkUndergroundMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 地牢史莱姆使用地牢敌怪的地下规则，候选来源由地牢结构限定。
    public static boolean checkDungeonSlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBrightness(LightLayer.SKY, pos) == 0
                && checkDungeonMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 本模组青团史莱姆按图鉴设定仅在清明时节的地表白天生成。
    public static boolean checkGreenDumplingSlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return DateUtils.isQingMing(DateUtils.getLunar()) && checkSurfaceDaySlimeSpawn(type, level, spawnType, pos, random);
    }

    /// 小动物：天气、日期、区域数量与高度限制集中在这里，注册类只负责绑定。
    /// 仅执行原版 Mob 的基础地面支撑检查，供不需要敌怪亮度规则的小动物使用。
    public static boolean checkMobSpawnRules(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    /// 仅执行原版 Animal 的基础环境检查，供普通陆生小动物使用。
    public static boolean checkAnimalSpawnRules(EntityType<? extends Animal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return Animal.checkAnimalSpawnRules(type, level, spawnType, pos, random);
    }

    /// 仙灵限定在地下层下半段与洞穴层，且位置不能露天。
    public static boolean checkFairySpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        int lowerUndergroundBoundary = (OverworldUtils.getSurfaceY() + OverworldUtils.getUndergroundY()) / 2;
        return pos.getY() < lowerUndergroundBoundary && !level.canSeeSky(pos)
                && checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    /// 企鹅只在露天雪原地表生成，群系由候选表限定。
    public static boolean checkSurfacePenguinSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() >= OverworldUtils.getSurfaceY() && level.canSeeSky(pos)
                && checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    /// 虫子在浅层地下常驻，降雨时也允许出现在露天地表。
    public static boolean checkWormSpawn(EntityType<Worm> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        int y = pos.getY();
        int surfaceY = OverworldUtils.getSurfaceY();
        if (y > surfaceY && y < OverworldUtils.getSpaceY() && ModUtils.isRainingAt(serverLevel, pos))
            return true;
        return y > OverworldUtils.getUndergroundY() && y < surfaceY;
    }

    /// 宝石兔与宝石松鼠只在洞穴层生成。
    public static boolean checkCavernCritterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getUndergroundY() && checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    /// 地表日间小动物共用的时间与高度检查。
    public static boolean checkSurfaceDayCritterSpawn(EntityType<? extends Animal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof ServerLevel serverLevel && serverLevel.isDay()
                && pos.getY() > OverworldUtils.getSurfaceY() && checkAnimalSpawnRules(type, level, spawnType, pos, random);
    }

    /// 地狱小动物以主世界昼夜为准，白天才自然生成。
    public static boolean checkNetherDayCritterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof ServerLevel serverLevel && serverLevel.getServer().overworld().isDay()
                && checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    /// 瓢虫要求地表晴朗白天且风速达到有风天气门槛。
    public static boolean checkLadybugSpawn(EntityType<? extends Animal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        ConfluenceData data = ConfluenceData.get(serverLevel);
        float windSquared = data.getWindSpeedX() * data.getWindSpeedX() + data.getWindSpeedZ() * data.getWindSpeedZ();
        return windSquared >= 0.25F && checkSurfaceDayCritterSpawn(type, level, spawnType, pos, random);
    }

    /// 用世界种子和游戏日固定当天是否为臭虫日，避免每次尝试重新随机。
    private static boolean isStinkbugDay(ServerLevel level) {
        return RandomSource.create(level.getSeed() ^ (level.getDayTime() / 24000L)).nextInt(3) == 0;
    }

    /// 蝴蝶与臭虫共用的晴朗、无墓地和低风速条件。
    private static boolean checkCalmDayCritterSpawn(EntityType<? extends Animal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel world) || world.isRaining() || !checkSurfaceDayCritterSpawn(type, level, spawnType, pos, random))
            return false;
        if (ModBlockCounters.isGraveyard(level, pos)) return false;
        ConfluenceData data = ConfluenceData.get(world);
        return data.getWindSpeedX() * data.getWindSpeedX() + data.getWindSpeedZ() * data.getWindSpeedZ() < 0.25F;
    }

    /// 蝴蝶只在非臭虫日满足平静白天条件时生成。
    public static boolean checkButterflySpawn(EntityType<? extends Animal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof ServerLevel world && !isStinkbugDay(world)
                && checkCalmDayCritterSpawn(type, level, spawnType, pos, random);
    }

    /// 臭虫只在臭虫日满足平静白天条件时生成。
    public static boolean checkStinkbugSpawn(EntityType<? extends Animal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof ServerLevel world && isStinkbugDay(world)
                && checkCalmDayCritterSpawn(type, level, spawnType, pos, random);
    }

    /// 萤火虫只在普通群系的地表静风夜晚生成。
    public static boolean checkFireflySpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return !OverworldUtils.isHallow(level.getBiome(pos)) && checkGlowBugEnvironment(type, level, spawnType, pos, random);
    }

    /// 闪电萤火虫只在神圣群系的地表静风夜晚生成。
    public static boolean checkLightningBugSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isHallow(level.getBiome(pos)) && checkGlowBugEnvironment(type, level, spawnType, pos, random);
    }

    /// 两种夜行发光虫共用的晴朗、静风和无墓地条件。
    private static boolean checkGlowBugEnvironment(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel world) || !world.isNight() || world.isRaining()
                || pos.getY() <= OverworldUtils.getSurfaceY() || !level.canSeeSky(pos))
            return false;
        if (ModBlockCounters.isGraveyard(level, pos)) return false;
        ConfluenceData data = ConfluenceData.get(world);
        if (data.getWindSpeedX() * data.getWindSpeedX() + data.getWindSpeedZ() * data.getWindSpeedZ() >= 0.25F)
            return false;
        return checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    /// 松露虫只在地下发光蘑菇群系生成，困难模式门槛由注册处叠加。
    public static boolean checkTruffleWormSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBiome(pos).is(ModBiomes.GLOWING_MUSHROOM)
                && pos.getY() < OverworldUtils.getSurfaceY() && !level.canSeeSky(pos)
                && checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    /// 七彩草蛉要求世纪之花后、指定夜间时段与局部区域数量上限。
    public static boolean checkPrismaticLacewingSpawn(EntityType<? extends Animal> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel serverLevel)
                || !KillBoard.INSTANCE.getGamePhase().isAtLeast(GamePhase.PLANTERA)
                || !LibDateUtils.isWithinDayTime(LibDateUtils._19$30, LibDateUtils._00$00, serverLevel)
                || pos.getY() <= OverworldUtils.getSurfaceY()) return false;
        AABB populationArea = new AABB(pos).inflate(LACEWING_POPULATION_RADIUS, LACEWING_POPULATION_HEIGHT, LACEWING_POPULATION_RADIUS);
        return serverLevel.getEntities(type, populationArea, LivingEntity::isAlive).isEmpty()
                && checkAnimalSpawnRules(type, level, spawnType, pos, random);
    }

    /// 神秘青蛙只在主世界地表丛林生成，且同一区域不重复出现。
    public static boolean checkMysticFrogSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (!level.getLevel().dimension().equals(Level.OVERWORLD) || !level.getBiome(pos).is(PortTags.Biomes.IS_JUNGLE)
                || pos.getY() < OverworldUtils.getSurfaceY() || random.nextInt(30) != 0
                || !Mob.checkMobSpawnRules(type, level, reason, pos, random)) return false;
        NPCSpawner.Region region = new NPCSpawner.Region(pos);
        if (NPCSpawner.INSTANCE.hasNPCAlive(region, NpcEntities.MYSTIC_SLIME.get())) return false;
        for (Entity entity : level.getLevel().getAllEntities()) {
            if (entity.getType() == CritterEntities.MYSTIC_FROG.get() && entity.isAlive()
                    && region.isOnRegion(entity.chunkPosition())) return false;
        }
        return true;
    }

    /// 旧摇摇箱仅在骷髅王后、洞穴层的牢固地面出现。
    public static boolean checkOldShakingChestSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return KillBoard.INSTANCE.isDefeated(BossEntities.SKELETRON.get())
                && pos.getY() < OverworldUtils.getUndergroundY() && !level.canSeeSky(pos)
                && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)
                && canSpawnTownSlimeRescue(type, level, pos, NpcEntities.ELDER_SLIME.get());
    }

    /// 笨拙气球史莱姆仅在露天太空层出现。
    public static boolean checkClumsyBalloonSlimeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return pos.getY() >= OverworldUtils.getSpaceY() && level.canSeeSky(pos)
                && canSpawnTownSlimeRescue(type, level, pos, NpcEntities.CLUMSY_SLIME.get());
    }

    /// 救援对象未入住且同一区域没有重复待救实体时才允许生成。
    private static boolean canSpawnTownSlimeRescue(EntityType<? extends Mob> type, ServerLevelAccessor level, BlockPos pos, EntityType<?> rescued) {
        ServerLevel world = level.getLevel();
        if (!world.dimension().equals(Level.OVERWORLD) || !level.getFluidState(pos).isEmpty())
            return false;
        NPCSpawner.Region region = new NPCSpawner.Region(pos);
        if (NPCSpawner.INSTANCE.hasNPCAlive(region, rescued)) return false;
        for (Entity entity : world.getAllEntities()) {
            if (entity.getType() == type && entity.isAlive() && region.isOnRegion(entity.chunkPosition()))
                return false;
        }
        return true;
    }

    /// 侏儒只在不露天的生命树结构内生成。
    public static boolean checkGnomeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (level.canSeeSky(pos) || !checkMonsterSpawnRules(type, level, reason, pos, random))
            return false;
        ServerLevel world = level.getLevel();
        var tree = world.registryAccess().registryOrThrow(Registries.STRUCTURE).get(ModStructures.Keys.LIVING_TREE);
        return tree != null && world.structureManager().getStructureWithPieceAt(pos, tree).isValid();
    }

    /// 普通敌怪的宽松高度入口，适用于由候选群系另行限定环境的类型。
    public static boolean checkRoutineMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getSpaceY() && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /// 普通僵尸只在地表夜晚生成，并执行统一敌怪基础检查。
    public static boolean checkZombieSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return !LibDateUtils.isDay(level) && pos.getY() >= OverworldUtils.getSurfaceY()
                && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /// 地表敌怪只允许在地表高度以上、太空高度以下生成。
    public static boolean checkGroundSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        int y = pos.getY();
        return y >= OverworldUtils.getSurfaceY() && y < OverworldUtils.getSpaceY() && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /// 沙漠幽魂仅在困难模式的腐化或猩红地下沙漠生成。
    public static boolean checkDesertSpiritSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return biome.is(PortTags.Biomes.IS_DESERT) && (OverworldUtils.isCorruption(biome) || OverworldUtils.isCrimson(biome))
                && checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 墙爬虫在洞穴蜘蛛窝生成，困难模式中进一步降低尝试成功率。
    public static boolean checkWallCreeperSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return (!IMinecraftServer.isHardmode(level.getLevel().getServer()) || random.nextInt(20) == 0)
                && checkCaveMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 符文巫师只在洞穴层的较深半段生成。
    public static boolean checkRuneWizardSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < (OverworldUtils.getUndergroundY() + level.getMinBuildHeight()) / 2
                && checkCaveMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 水矢宝箱怪需通过地牢判定，并且附近存在可附着的书架空间。
    public static boolean checkWaterBoltMimicSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return checkDungeonMonsterSpawn(type, level, spawnType, pos, random) && WaterBoltMimic.findBookSpace(level, pos) != null;
    }

    /// 愤怒雨云只在非雪原的露天地表雨天生成，且限制全世界同时存在数量。
    public static boolean checkAngryNimbusSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel world) || !world.isRaining() || !level.canSeeSky(pos)
                || level.getBiome(pos).is(PortTags.Biomes.IS_SNOWY) || level.getBiome(pos).is(PortTags.Biomes.IS_ICY)
                || !checkGroundSpawn(type, level, spawnType, pos, random)) return false;
        int count = 0;
        for (Entity entity : world.getAllEntities()) {
            if (entity.getType() == MonsterEntities.ANGRY_NIMBUS.get() && entity.isAlive() && ++count >= 2)
                return false;
        }
        return true;
    }

    /// 蚁狮在地下沙漠常驻，地表则要求白天和脚下沙块。
    public static boolean checkAntlionSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (pos.getY() < OverworldUtils.getSurfaceY())
            return !level.canSeeSky(pos) && checkRoutineMonsterSpawn(type, level, spawnType, pos, random);
        return level instanceof Level world && world.isDay() && level.getBlockState(pos.below()).is(net.minecraft.tags.BlockTags.SAND)
                && checkGroundSpawn(type, level, spawnType, pos, random);
    }

    /// 骷髅博士要求夜晚且脚下是丛林草。
    public static boolean checkDoctorBonesSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof ServerLevel world && world.isNight()
                && level.getBlockState(pos.below()).is(NatureBlocks.JUNGLE_GRASS_BLOCK.get())
                && checkRoutineMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 蚁狮马在地下沙漠常驻，困难模式前沙尘暴时也可到地表。
    public static boolean checkAntlionChargerSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getBiome(pos).is(PortTags.Biomes.IS_DESERT)) return false;
        if (pos.getY() < OverworldUtils.getSurfaceY())
            return checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random);
        return !KillBoard.INSTANCE.getGamePhase().isHardmode()
                && checkSandstormSpawn(type, level, spawnType, pos, random);
    }

    /// 爬藤怪限定于地下腐化群系。
    public static boolean checkClingerSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isCorruption(level.getBiome(pos))
                && checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 花岗精只在洞穴层生成，同一洞穴活动范围最多存在一只。
    public static boolean checkGraniteElementalSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!checkCaveMonsterSpawn(type, level, spawnType, pos, random)) return false;
        if (!(level instanceof ServerLevel serverLevel)) return true;
        AABB activeArea = new AABB(pos).inflate(GRANITE_POPULATION_RADIUS, GRANITE_POPULATION_HEIGHT, GRANITE_POPULATION_RADIUS);
        return serverLevel.getEntities(type, activeArea, LivingEntity::isAlive).isEmpty();
    }

    /// 蚁狮蜂在地下沙漠常驻，地表仅在沙尘暴期间生成。
    public static boolean checkAntlionSwarmerSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getBiome(pos).is(PortTags.Biomes.IS_DESERT)) return false;
        return pos.getY() < OverworldUtils.getSurfaceY()
                ? checkUndergroundMonsterSpawn(type, level, spawnType, pos, random)
                : checkSandstormSpawn(type, level, spawnType, pos, random);
    }

    /// 沙尘暴敌怪要求事件进行中、露天地表和足够连续的沙块。
    public static boolean checkSandstormSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return SandstormGameEvent.INSTANCE.started()
                && level.getBiome(pos).is(PortTags.Biomes.IS_DESERT)
                && level.canSeeSky(pos) && level.getBlockState(pos.below()).is(BlockTags.SAND)
                && checkGroundSpawn(type, level, spawnType, pos, random) && hasConnectedSand(level, pos);
    }

    /// 真菌球怪在地表蘑菇群系生成，困难模式后也可进入地下。
    public static boolean checkFungiBulbSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getBiome(pos).is(ModBiomes.GLOWING_MUSHROOM) && !level.getBlockState(pos.below()).is(NatureBlocks.MUSHROOM_GRASS_BLOCK.get()))
            return false;
        if (pos.getY() < OverworldUtils.getSurfaceY() && (!KillBoard.INSTANCE.getGamePhase().isHardmode() || level.canSeeSky(pos)))
            return false;
        return checkRoutineMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 普通沙鲨在未被转化的沙块上生成。
    public static boolean checkPlainSandSharkSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var sand = level.getBlockState(pos.below());
        return !sand.is(NatureBlocks.EBONSAND.get()) && !sand.is(NatureBlocks.CRIMSAND.get())
                && !sand.is(NatureBlocks.PEARLSAND.get())
                && checkSandstormSpawn(type, level, spawnType, pos, random);
    }

    /// 噬骨沙鲨只在黑檀沙上生成。
    public static boolean checkCorruptSandSharkSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(NatureBlocks.EBONSAND.get())
                && checkSandstormSpawn(type, level, spawnType, pos, random);
    }

    /// 戮血沙鲨只在猩红沙上生成。
    public static boolean checkCrimsonSandSharkSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(NatureBlocks.CRIMSAND.get())
                && checkSandstormSpawn(type, level, spawnType, pos, random);
    }

    /// 水晶沙鲨只在珍珠沙上生成。
    public static boolean checkHallowSandSharkSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(NatureBlocks.PEARLSAND.get())
                && checkSandstormSpawn(type, level, spawnType, pos, random);
    }

    /// 从生成点脚下向有限范围搜索连续沙块，避免单块沙触发沙尘暴敌怪。
    private static boolean hasConnectedSand(ServerLevelAccessor level, BlockPos pos) {
        ArrayDeque<BlockPos> pending = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        pending.add(pos.below());
        int count = 0;
        while (!pending.isEmpty()) {
            BlockPos next = pending.removeFirst();
            if (Math.abs(next.getX() - pos.getX()) > 4 || Math.abs(next.getZ() - pos.getZ()) > 4 || next.getY() >= pos.getY() || next.getY() < pos.getY() - 5
                    || !visited.add(next) || !level.getBlockState(next).is(BlockTags.SAND))
                continue;
            if (++count >= 40) return true;
            for (Direction direction : Direction.values()) pending.add(next.relative(direction));
        }
        return false;
    }

    /// 愤怒蒲公英要求晴朗有风白天、草地，且风向将其吹向玩家。
    public static boolean checkAngryDandelionSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel world) || !world.isDay() || world.isRaining() || !world.canSeeSky(pos)
                || !level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)
                || !checkGroundSpawn(type, level, spawnType, pos, random)) return false;
        ConfluenceData data = ConfluenceData.get(world);
        float windX = data.getWindSpeedX();
        float windZ = data.getWindSpeedZ();
        if (windX * windX + windZ * windZ < 0.25F) return false;
        var player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 128.0, false);
        return player != null && (player.getX() - pos.getX() - 0.5) * windX + (player.getZ() - pos.getZ() - 0.5) * windZ > 0.0;
    }

    /// 地下生物群系规则覆盖浅层地下到洞穴底部，不把 Y=0 误作下界。
    public static boolean checkUndergroundMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        int y = pos.getY();
        return y >= OverworldUtils.getCaveY() && y < OverworldUtils.getSurfaceY()
                && !level.canSeeSky(pos) && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /// 地下沙漠的通用环境。普通、邪恶和神圣变体由下面各自的谓词再判定。
    /// 地下沙漠通用规则；各变体另叠加纯净、腐化、猩红或神圣判定。
    public static boolean checkDesertUndergroundMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isDesert(level.getBiome(pos))
                && checkUndergroundMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 普通食尸鬼只在未被邪恶或神圣转化的地下沙漠生成。
    public static boolean checkPureDesertUndergroundSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return !OverworldUtils.isCorruption(biome) && !OverworldUtils.isCrimson(biome) && !OverworldUtils.isHallow(biome)
                && checkDesertUndergroundMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 光明拉弥亚与沙贼只在纯净或神圣地下沙漠生成。
    public static boolean checkPureOrHallowDesertUndergroundSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return !OverworldUtils.isCorruption(biome) && !OverworldUtils.isCrimson(biome)
                && checkDesertUndergroundMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 暗黑拉弥亚只在腐化或猩红地下沙漠生成。
    public static boolean checkEvilDesertUndergroundSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return (OverworldUtils.isCorruption(biome) || OverworldUtils.isCrimson(biome))
                && checkDesertUndergroundMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 腐恶食尸鬼只在腐化地下沙漠生成。
    public static boolean checkCorruptDesertUndergroundSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isCorruption(level.getBiome(pos))
                && checkDesertUndergroundMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 红染食尸鬼只在猩红地下沙漠生成。
    public static boolean checkCrimsonDesertUndergroundSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isCrimson(level.getBiome(pos))
                && checkDesertUndergroundMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 神梦食尸鬼只在神圣地下沙漠生成。
    public static boolean checkHallowDesertUndergroundSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isHallow(level.getBiome(pos))
                && checkDesertUndergroundMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 泰拉木乃伊按脚下沙块分型，不按昼夜分型；普通沙、黑檀沙、猩红沙、珍珠沙分别登记。
    /// 普通木乃伊要求脚下是普通沙块。
    public static boolean checkSandMummySpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return checkMummyOnSand(type, level, spawnType, pos, random, Blocks.SAND);
    }

    /// 暗黑木乃伊要求脚下是黑檀沙块。
    public static boolean checkEbonsandMummySpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return checkMummyOnSand(type, level, spawnType, pos, random, NatureBlocks.EBONSAND.get());
    }

    /// 血木乃伊要求脚下是猩红沙块。
    public static boolean checkCrimsandMummySpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return checkMummyOnSand(type, level, spawnType, pos, random, NatureBlocks.CRIMSAND.get());
    }

    /// 光明木乃伊要求脚下是珍珠沙块。
    public static boolean checkPearlsandMummySpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return checkMummyOnSand(type, level, spawnType, pos, random, NatureBlocks.PEARLSAND.get());
    }

    /// 木乃伊共用的沙漠、沙块和基础支撑检查，不施加夜晚限制。
    private static boolean checkMummyOnSand(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random, net.minecraft.world.level.block.Block sand) {
        return OverworldUtils.isDesert(level.getBiome(pos)) && pos.getY() < OverworldUtils.getSpaceY()
                && level.getBlockState(pos.below()).is(sand)
                && Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    /// 洞穴层敌怪要求低于地下与洞穴分界，且不能露天。
    public static boolean checkCaveMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getUndergroundY() && !level.canSeeSky(pos) && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /// 石巨人只在非雪原的石块地面洞穴生成。
    public static boolean checkRockGolemSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return level.getBlockState(pos.below()).is(Blocks.STONE) && !biome.is(PortTags.Biomes.IS_SNOWY)
                && !biome.is(PortTags.Biomes.IS_ICY) && checkCaveMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 蒂姆只在深层洞穴生成，玩家穿法袍会提高出现机会。
    public static boolean checkTimSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (pos.getY() >= (OverworldUtils.getUndergroundY() + level.getMinBuildHeight()) / 2
                || !checkCaveMonsterSpawn(type, level, spawnType, pos, random)) return false;
        var player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 128.0, false);
        if (player != null) {
            var robe = player.getItemBySlot(EquipmentSlot.CHEST);
            if (robe.is(ModTags.Items.ROBE) && !robe.is(ArmorItems.MYSTIC_ROBE.get())
                    && !player.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.WIZARD_HAT.get()))
                return true;
        }
        return random.nextInt(5) == 0;
    }

    /// 沙虫按深度排除雪原或神圣区域，再使用地下通用规则。
    public static boolean checkDiggerSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        boolean underground = pos.getY() >= OverworldUtils.getUndergroundY();
        return !(underground ? biome.is(PortTags.Biomes.IS_SNOWY) || biome.is(PortTags.Biomes.IS_ICY) : OverworldUtils.isHallow(biome))
                && checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 巨型蠕虫只在非雪原的地下及洞穴层生成。
    public static boolean checkGiantWormSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return !biome.is(PortTags.Biomes.IS_SNOWY) && !biome.is(PortTags.Biomes.IS_ICY) && checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 墓穴爬虫在地下沙漠常驻，沙尘暴时可到地表。
    public static boolean checkTombCrawlerSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getBiome(pos).is(PortTags.Biomes.IS_DESERT)) return false;
        return pos.getY() < OverworldUtils.getSurfaceY() ? checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random) : checkSandstormSpawn(type, level, spawnType, pos, random);
    }

    /// 腐化蠕虫要求腐化群系，并使用普通敌怪生成高度。
    public static boolean checkCorruptionWormSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isCorruption(level.getBiome(pos)) && checkRoutineMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 地下层与洞穴层共用的放置规则，适用于泰拉中标注为“地下及更深处”的敌怪。
    /// 地下与洞穴共用规则，仅要求低于地表且不能露天。
    public static boolean checkBelowSurfaceMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getSurfaceY() && !level.canSeeSky(pos) && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /// 普通地牢敌怪仅在击败骷髅王后、地牢地下区域生成；此前由地牢守卫机制接管。
    public static boolean checkDungeonMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return KillBoard.INSTANCE.isDefeated(BossEntities.SKELETRON.get())
                && pos.getY() < OverworldUtils.getSurfaceY() && !level.canSeeSky(pos)
                && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /// 鸟妖与飞龙等高空敌怪要求太空层高度。
    public static boolean checkHighLevelMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() >= OverworldUtils.getSpaceY() && pos.getY() < level.getMaxBuildHeight() && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /// 巨型飞龙除太空条件外还要求春节时段。
    public static boolean checkArchWyvernSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return DateUtils.isXinNian(DateUtils.getLunar()) && checkHighLevelMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 冰雪精在地下冰雪群系或夜晚雪原地表生成。
    public static boolean checkIceElementalSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random)
                || level instanceof Level world && world.isNight() && checkGroundSpawn(type, level, spawnType, pos, random);
    }

    /// 冰雪巨人只在露天雪原暴雪期间生成，且同一世界限制数量。
    public static boolean checkIceGolemSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel world) || !world.isRaining() || !level.canSeeSky(pos)
                || !checkSurfaceMobSpawn(type, level, spawnType, pos, random)) return false;
        for (Entity entity : world.getAllEntities()) {
            if (entity.getType() == type && entity.isAlive()) return false;
        }
        return true;
    }

    /// 地狱敌怪只在本模组映射的地狱维度生成。
    public static boolean checkNetherMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.dimension() == OverworldUtils.underworld() && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    /// 熔岩蝙蝠和红魔鬼要求至少击败一个机械 Boss。
    public static boolean checkPostMechanicalNetherSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return KillBoard.INSTANCE.isAnyMechBossDefeated() && checkNetherMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 飞鱼只在雨天的地表生成。
    public static boolean checkFlyingFishSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.isRaining() && checkSurfaceMobSpawn(type, level, spawnType, pos, random);
    }

    /// 恶魔眼只在地表夜晚生成，并处理新月时无遮挡高度条件。
    public static boolean checkDemonEyeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof Level world) || !checkGroundSpawn(type, level, spawnType, pos, random) || !world.isNight())
            return false;
        if (world.getMoonPhase() == 4) return hasClearColumn(world, pos);
        return world.random.nextInt(99) < 80;
    }

    /// 幻灵只在地表夜晚生成，新月时更常见。
    public static boolean checkWraithSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof Level world) || !world.isNight() || !checkGroundSpawn(type, level, spawnType, pos, random))
            return false;
        return world.getMoonPhase() == 4 || random.nextInt(4) == 0;
    }

    /// 狼人只在满月的地表夜晚生成。
    public static boolean checkWerewolfSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof ServerLevel world && world.isNight() && world.getMoonPhase() == 0
                && checkGroundSpawn(type, level, spawnType, pos, random);
    }

    /// 鬼魂要求墓地环境。
    public static boolean checkGhostSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return ModBlockCounters.isGraveyard(level, pos) && checkRoutineMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 新郎与新娘在血月地表或低概率墓地环境生成。
    public static boolean checkWeddingZombieSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (BloodMoonGameEvent.INSTANCE.started() && checkGroundSpawn(type, level, spawnType, pos, random))
            return true;
        return random.nextInt(5) == 0 && checkGhostSpawn(type, level, spawnType, pos, random);
    }

    /// 附身盔甲在夜晚地表或地下生成，头顶需有无遮挡纵向空间。
    public static boolean checkPossessedArmorSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!checkMonsterSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }
        int y = pos.getY();
        boolean validAltitude = y >= level.getMinBuildHeight() && (y < OverworldUtils.getSurfaceY() || y < OverworldUtils.getSpaceY() && level instanceof Level world && world.isNight());
        return validAltitude && level instanceof Level world && hasClearColumn(world, pos);
    }

    /// 水生敌怪的通用规则要求双层水体，不使用陆生怪物的脚下支撑检查。
    public static boolean checkWaterMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getSpaceY() && hasDeepWater(level, pos);
    }

    /// 腐化水生敌怪要求腐化群系与双层水体。
    public static boolean checkCorruptionWaterMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isCorruption(level.getBiome(pos)) && checkWaterMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 猩红水生敌怪要求猩红群系与双层水体。
    public static boolean checkCrimsonWaterMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isCrimson(level.getBiome(pos)) && checkWaterMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 蘑菇鱼要求水体下方为蘑菇草块。
    public static boolean checkFungoFishSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(NatureBlocks.MUSHROOM_GRASS_BLOCK.get()) && checkWaterMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 鮟鱇鱼要求洞穴水体，或丛林及繁茂洞穴水体。
    public static boolean checkAnglerFishSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return (pos.getY() < OverworldUtils.getUndergroundY() && !level.canSeeSky(pos) || biome.is(PortTags.Biomes.IS_JUNGLE) || biome.is(PortTags.Biomes.IS_LUSH))
                && checkWaterMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 海洋等地表水生敌怪要求地表高度的深水体。
    public static boolean checkSurfaceWaterMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        int y = pos.getY();
        return y >= OverworldUtils.getSurfaceY() && y < OverworldUtils.getSpaceY() && hasDeepWater(level, pos);
    }

    /// 地下水母等敌怪要求地表以下且不露天的深水体。
    public static boolean checkUndergroundWaterMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getSurfaceY() && !level.canSeeSky(pos) && hasDeepWater(level, pos);
    }

    /// 哥布林侦察兵沿用地表白天规则。
    public static boolean checkGoblinScoutSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return checkSurfaceDayMobSpawn(type, level, spawnType, pos, random);
    }

    /// 白天地表敌怪不能套用原版怪物亮度门槛，否则会在满足时间条件时反而无法自然生成。
    /// 白天地表敌怪使用 Mob 基础检查，避免原版怪物亮度门槛阻止生成。
    public static boolean checkSurfaceDayMobSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.isDay() && checkSurfaceMobSpawn(type, level, spawnType, pos, random);
    }

    /// 不限昼夜的地表生物只检查高度与原版 Mob 基础条件。
    public static boolean checkSurfaceMobSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        int y = pos.getY();
        return y >= OverworldUtils.getSurfaceY() && y < OverworldUtils.getSpaceY() && Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    /// 不限昼夜与地层的普通 Mob 在太空层以下执行基础检查。
    public static boolean checkRoutineMobSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getSpaceY() && Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    /// 夜晚地表敌怪要求夜间和地表高度。
    public static boolean checkSurfaceNightMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.isNight() && checkGroundSpawn(type, level, spawnType, pos, random);
    }

    /// 为已有环境规则叠加困难模式门槛，避免复制环境判定。
    public static <T extends Mob> SpawnPlacements.SpawnPredicate<T> hardmode(SpawnPlacements.SpawnPredicate<T> predicate) {
        // 包装既有规则而不是复制一份，确保开启困难模式只增加进度门槛，不改变环境语义。
        return (type, level, spawnType, pos, random) -> level instanceof ServerLevel serverLevel
                && IMinecraftServer.isHardmode(serverLevel.getServer())
                && predicate.test(type, level, spawnType, pos, random);
    }

    /// 为已有环境规则叠加指定游戏阶段门槛。
    public static <T extends Mob> SpawnPlacements.SpawnPredicate<T> atLeast(GamePhase phase, SpawnPlacements.SpawnPredicate<T> predicate) {
        return (type, level, spawnType, pos, random) -> KillBoard.INSTANCE.getGamePhase().isAtLeast(phase)
                && predicate.test(type, level, spawnType, pos, random);
    }

    /// 执行所有敌对生物共用的原版基础放置检查，并按配置决定是否保留亮度门槛。
    ///
    /// 具有额外昼夜、地形或进度条件的实体也必须在自身条件之后调用本方法，不能直接调用
    /// {@link Monster#checkMonsterSpawnRules}，否则它们会绕过统一的亮度配置。
    /// 统一应用敌怪基础支撑与可配置的亮度规则。
    @SuppressWarnings("unchecked")
    public static boolean checkMonsterSpawnRules(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        // 忽略光照时仍保留 Mob 对脚下方块的生成支撑检查；碰撞和边界由外部生成流程处理。
        if (CommonConfigs.SPAWN_WITHOUT_LIGHT.get()) {
            return Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
        }
        EntityType<? extends Monster> monsterType = (EntityType<? extends Monster>) type;
        return Monster.checkMonsterSpawnRules(monsterType, level, spawnType, pos, random);
    }

    /// 检查生成点上方到建筑高度上限没有完整碰撞方块。
    private static boolean hasClearColumn(Level level, BlockPos pos) {
        // 附身盔甲需要无遮挡的纵向空间；遇到第一个完整碰撞方块即可提前失败。
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (cursor.getY() < level.getMaxBuildHeight()) {
            if (level.getBlockState(cursor).isCollisionShapeFullBlock(level, cursor)) return false;
            cursor.move(0, 1, 0);
        }
        return true;
    }

    /// 检查生成点及其上方都有水，避免单层浅水生成水生敌怪。
    private static boolean hasDeepWater(ServerLevelAccessor level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER)
                && level.getFluidState(pos.above()).is(FluidTags.WATER);
    }
}
