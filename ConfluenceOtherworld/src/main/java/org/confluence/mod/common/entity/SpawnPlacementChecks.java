package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.spawner.NPCSpawner;
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
    private SpawnPlacementChecks() {}

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

    public static boolean checkTownSlimeRescueSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        ServerLevel world = level.getLevel();
        if (!world.dimension().equals(Level.OVERWORLD) || !level.getFluidState(pos).isEmpty())
            return false;
        boolean balloon = type == MonsterEntities.CLUMSY_BALLOON_SLIME.get();
        EntityType<?> rescued = balloon ? NpcEntities.CLUMSY_SLIME.get() : NpcEntities.ELDER_SLIME.get();
        NPCSpawner.Region region = new NPCSpawner.Region(pos);
        if (NPCSpawner.INSTANCE.hasNPCAlive(region, rescued)) return false;
        if (balloon) {
            if (pos.getY() < OverworldUtils.getSpaceY() || !level.canSeeSky(pos)) return false;
        } else if (!KillBoard.INSTANCE.isDefeated(BossEntities.SKELETRON.get())
                || pos.getY() >= OverworldUtils.getUndergroundY() || level.canSeeSky(pos)
                || !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP))
            return false;
        for (Entity entity : world.getAllEntities()) {
            if (entity.getType() == type && entity.isAlive() && region.isOnRegion(entity.chunkPosition()))
                return false;
        }
        return true;
    }

    public static boolean checkGnomeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (level.canSeeSky(pos) || !checkMonsterSpawnRules(type, level, reason, pos, random))
            return false;
        ServerLevel world = level.getLevel();
        var tree = world.registryAccess().registryOrThrow(Registries.STRUCTURE).get(ModStructures.Keys.LIVING_TREE);
        return tree != null && world.structureManager().getStructureWithPieceAt(pos, tree).isValid();
    }

    public static boolean checkRoutineMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getSpaceY() && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkGroundSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        int y = pos.getY();
        return y >= OverworldUtils.getSurfaceY() && y < OverworldUtils.getSpaceY() && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkDesertSpiritSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return biome.is(PortTags.Biomes.IS_DESERT) && (OverworldUtils.isCorruption(biome) || OverworldUtils.isCrimson(biome))
                && checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkWallCreeperSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return (!IMinecraftServer.isHardmode(level.getLevel().getServer()) || random.nextInt(20) == 0)
                && checkCaveMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkRuneWizardSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < (OverworldUtils.getUndergroundY() + level.getMinBuildHeight()) / 2
                && checkCaveMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkWaterBoltMimicSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return checkDungeonMonsterSpawn(type, level, spawnType, pos, random) && WaterBoltMimic.findBookSpace(level, pos) != null;
    }

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

    public static boolean checkAntlionSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (pos.getY() < OverworldUtils.getSurfaceY())
            return !level.canSeeSky(pos) && checkRoutineMonsterSpawn(type, level, spawnType, pos, random);
        return level instanceof Level world && world.isDay() && level.getBlockState(pos.below()).is(net.minecraft.tags.BlockTags.SAND)
                && checkGroundSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkDoctorBonesSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof ServerLevel world && world.isNight()
                && level.getBlockState(pos.below()).is(NatureBlocks.JUNGLE_GRASS_BLOCK.get())
                && checkRoutineMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkAntlionChargerSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getBiome(pos).is(PortTags.Biomes.IS_DESERT)) return false;
        if (pos.getY() < OverworldUtils.getSurfaceY())
            return checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random);
        return !KillBoard.INSTANCE.getGamePhase().isHardmode() && KillBoard.INSTANCE.isDefeated(BossEntities.EYE_OF_CTHULHU.get())
                && checkSandstormSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkSandstormSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return SandstormGameEvent.INSTANCE.started()
                && level.getBiome(pos).is(PortTags.Biomes.IS_DESERT)
                && level.canSeeSky(pos) && level.getBlockState(pos.below()).is(BlockTags.SAND)
                && checkGroundSpawn(type, level, spawnType, pos, random) && hasConnectedSand(level, pos);
    }

    public static boolean checkFungiBulbSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getBiome(pos).is(ModBiomes.GLOWING_MUSHROOM) && !level.getBlockState(pos.below()).is(NatureBlocks.MUSHROOM_GRASS_BLOCK.get()))
            return false;
        if (pos.getY() < OverworldUtils.getSurfaceY() && (!KillBoard.INSTANCE.getGamePhase().isHardmode() || level.canSeeSky(pos)))
            return false;
        return checkRoutineMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkSandSharkSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!checkSandstormSpawn(type, level, spawnType, pos, random)) return false;
        var sand = level.getBlockState(pos.below());
        EntityType<?> expected = sand.is(NatureBlocks.EBONSAND.get()) ? MonsterEntities.BONE_BITER.get()
                : sand.is(NatureBlocks.CRIMSAND.get()) ? MonsterEntities.FLESH_REAVER.get()
                : sand.is(NatureBlocks.PEARLSAND.get()) ? MonsterEntities.CRYSTAL_THRESHER.get() : MonsterEntities.SAND_SHARK.get();
        return type == expected;
    }

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

    public static boolean checkUndergroundMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        int y = pos.getY();
        return y >= OverworldUtils.getUndergroundY() && y < OverworldUtils.getSurfaceY() && !level.canSeeSky(pos) && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkCaveMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getUndergroundY() && !level.canSeeSky(pos) && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkRockGolemSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return level.getBlockState(pos.below()).is(Blocks.STONE) && !biome.is(PortTags.Biomes.IS_SNOWY)
                && !biome.is(PortTags.Biomes.IS_ICY) && checkCaveMonsterSpawn(type, level, spawnType, pos, random);
    }

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

    public static boolean checkDiggerSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        boolean underground = pos.getY() >= OverworldUtils.getUndergroundY();
        return !(underground ? biome.is(PortTags.Biomes.IS_SNOWY) || biome.is(PortTags.Biomes.IS_ICY) : OverworldUtils.isHallow(biome))
                && checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkGiantWormSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return !biome.is(PortTags.Biomes.IS_SNOWY) && !biome.is(PortTags.Biomes.IS_ICY) && checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkTombCrawlerSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!level.getBiome(pos).is(PortTags.Biomes.IS_DESERT)) return false;
        return pos.getY() < OverworldUtils.getSurfaceY() ? checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random) : checkSandstormSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkCorruptionWormSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return OverworldUtils.isCorruption(level.getBiome(pos)) && checkRoutineMonsterSpawn(type, level, spawnType, pos, random);
    }

    /// 地下层与洞穴层共用的放置规则，适用于泰拉中标注为“地下及更深处”的敌怪。
    public static boolean checkBelowSurfaceMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getSurfaceY() && !level.canSeeSky(pos) && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkDungeonMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() >= -35 && pos.getY() <= 40 && !level.canSeeSky(pos) && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkHighLevelMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() >= OverworldUtils.getSpaceY() && pos.getY() < level.getMaxBuildHeight() && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkArchWyvernSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return DateUtils.isXinNian(DateUtils.getLunar()) && checkHighLevelMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkIceElementalSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return checkBelowSurfaceMonsterSpawn(type, level, spawnType, pos, random)
                || level instanceof Level world && world.isNight() && checkGroundSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkIceGolemSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof ServerLevel world) || !world.isRaining() || !level.canSeeSky(pos)
                || !checkSurfaceMobSpawn(type, level, spawnType, pos, random)) return false;
        for (Entity entity : world.getAllEntities()) {
            if (entity.getType() == type && entity.isAlive()) return false;
        }
        return true;
    }

    public static boolean checkNetherMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.dimension() == OverworldUtils.underworld() && checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkFlyingFishSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.isRaining() && checkSurfaceMobSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkDemonEyeSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof Level world) || !checkGroundSpawn(type, level, spawnType, pos, random) || !world.isNight())
            return false;
        if (world.getMoonPhase() == 4) return hasClearColumn(world, pos);
        return world.random.nextInt(99) < 80;
    }

    public static boolean checkWraithSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!(level instanceof Level world) || !world.isNight() || !checkGroundSpawn(type, level, spawnType, pos, random))
            return false;
        return world.getMoonPhase() == 4 || random.nextInt(4) == 0;
    }

    public static boolean checkGhostSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return ModBlockCounters.isGraveyard(level, pos) && checkRoutineMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkWeddingZombieSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (BloodMoonGameEvent.INSTANCE.started() && checkGroundSpawn(type, level, spawnType, pos, random))
            return true;
        return random.nextInt(5) == 0 && checkGhostSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkPossessedArmorSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!checkMonsterSpawnRules(type, level, spawnType, pos, random)) {
            return false;
        }
        int y = pos.getY();
        boolean validAltitude = y >= level.getMinBuildHeight() && (y < OverworldUtils.getSurfaceY() || y < OverworldUtils.getSpaceY() && level instanceof Level world && world.isNight());
        return validAltitude && level instanceof Level world && hasClearColumn(world, pos);
    }

    public static boolean checkWaterMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getSpaceY() && hasDeepWater(level, pos);
    }

    public static boolean checkFungoFishSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(NatureBlocks.MUSHROOM_GRASS_BLOCK.get()) && checkWaterMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkAnglerFishSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        var biome = level.getBiome(pos);
        return (pos.getY() < OverworldUtils.getUndergroundY() && !level.canSeeSky(pos) || biome.is(PortTags.Biomes.IS_JUNGLE) || biome.is(PortTags.Biomes.IS_LUSH))
                && checkWaterMonsterSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkSurfaceWaterMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        int y = pos.getY();
        return y >= OverworldUtils.getSurfaceY() && y < OverworldUtils.getSpaceY() && hasDeepWater(level, pos);
    }

    public static boolean checkUndergroundWaterMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getSurfaceY() && !level.canSeeSky(pos) && hasDeepWater(level, pos);
    }

    public static boolean checkGoblinScoutSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return checkSurfaceDayMobSpawn(type, level, spawnType, pos, random);
    }

    /// 白天地表敌怪不能套用原版怪物亮度门槛，否则会在满足时间条件时反而无法自然生成。
    public static boolean checkSurfaceDayMobSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.isDay() && checkSurfaceMobSpawn(type, level, spawnType, pos, random);
    }

    public static boolean checkSurfaceMobSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        int y = pos.getY();
        return y >= OverworldUtils.getSurfaceY() && y < OverworldUtils.getSpaceY() && Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkRoutineMobSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return pos.getY() < OverworldUtils.getSpaceY() && Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean checkSurfaceNightMonsterSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level instanceof Level world && world.isNight() && checkGroundSpawn(type, level, spawnType, pos, random);
    }

    public static <T extends Mob> SpawnPlacements.SpawnPredicate<T> hardmode(SpawnPlacements.SpawnPredicate<T> predicate) {
        // 包装既有规则而不是复制一份，确保开启困难模式只增加进度门槛，不改变环境语义。
        return (type, level, spawnType, pos, random) -> level instanceof ServerLevel serverLevel
                && IMinecraftServer.isHardmode(serverLevel.getServer())
                && predicate.test(type, level, spawnType, pos, random);
    }

    /// 执行所有敌对生物共用的原版基础放置检查，并按配置决定是否保留亮度门槛。
    ///
    /// 具有额外昼夜、地形或进度条件的实体也必须在自身条件之后调用本方法，不能直接调用
    /// {@link Monster#checkMonsterSpawnRules}，否则它们会绕过统一的亮度配置。
    @SuppressWarnings("unchecked")
    public static boolean checkMonsterSpawnRules(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        // 忽略光照时仍保留 Mob 对脚下方块的生成支撑检查；碰撞和边界由外部生成流程处理。
        if (CommonConfigs.SPAWN_WITHOUT_LIGHT.get()) {
            return Mob.checkMobSpawnRules(type, level, spawnType, pos, random);
        }
        EntityType<? extends Monster> monsterType = (EntityType<? extends Monster>) type;
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

    private static boolean hasDeepWater(ServerLevelAccessor level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER)
                && level.getFluidState(pos.above()).is(FluidTags.WATER);
    }
}
