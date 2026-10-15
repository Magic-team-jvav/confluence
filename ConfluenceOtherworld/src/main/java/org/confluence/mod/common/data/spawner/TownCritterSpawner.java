package org.confluence.mod.common.data.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.entity.npc.TownPopulation;
import org.confluence.mod.common.init.ModMiniBiomes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.entity.DevelopmentSpawnPolicy;
import org.confluence.mod.util.OverworldUtils;

import java.util.List;

/// 城镇额外小动物从当地群系的自然生成表选择，沿用物种的天气、时段和放置规则。
public final class TownCritterSpawner implements CustomSpawner {
    private int nextTick;

    @Override
    public int tick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
        if (!spawnFriendlies || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING))
            return 0;
        if (--nextTick > 0) return 0;
        nextTick = 20;
        int spawned = 0;
        for (ServerPlayer player : level.players()) {
            if (!player.isAlive() || player.isSpectator() || player.isCreative()) continue;
            double chance = TownPopulation.critterChance(player);
            if (chance <= 0 || level.random.nextDouble() >= chance) continue;
            double capFactor = level.dimension() == OverworldUtils.underworld() ? 0.5 : 0.6;
            int cap = (int) (MobCategory.CREATURE.getMaxInstancesPerChunk() * capFactor);
            AABB area = new AABB(player.blockPosition()).inflate(ModMiniBiomes.TOWN.horizontalRadius(),
                    ModMiniBiomes.TOWN.verticalRadius(), ModMiniBiomes.TOWN.horizontalRadius());
            long nearby = level.getEntitiesOfClass(Mob.class, area, mob -> mob.isAlive()
                    && mob.getType().is(ModTags.EntityTypes.TOWN_CRITTERS)).size();
            if (nearby >= cap) continue;
            if (spawnAround(level, player)) spawned++;
        }
        return spawned;
    }

    private static boolean spawnAround(ServerLevel level, ServerPlayer player) {
        RandomSource random = level.random;
        for (int attempt = 0; attempt < 4; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 24 + random.nextInt(25);
            BlockPos column = player.blockPosition().offset((int) (Math.cos(angle) * radius), 0,
                    (int) (Math.sin(angle) * radius));
            if (!level.isLoaded(column)) continue;
            BlockPos pos;
            if (level.dimension() != OverworldUtils.underworld() && player.getY() >= OverworldUtils.getSurfaceY()) {
                pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column);
            } else {
                pos = findGround(level, column);
                if (pos == null) continue;
            }
            if (!level.getWorldBorder().isWithinBounds(pos) || player.distanceToSqr(pos.getX() + 0.5,
                    pos.getY(), pos.getZ() + 0.5) < 24 * 24) continue;
            List<MobSpawnSettings.SpawnerData> pool = level.getBiome(pos).value().getMobSettings()
                    .getMobs(MobCategory.CREATURE).unwrap().stream()
                    .filter(data -> data.type.is(ModTags.EntityTypes.TOWN_CRITTERS)
                            && DevelopmentSpawnPolicy.allowsAutomaticSpawn(data.type)
                            && (data.type != CritterEntities.WORM.get() || level.isRaining())).toList();
            int total = pool.stream().mapToInt(data -> data.getWeight().asInt()).sum();
            if (total <= 0) continue;
            int choice = random.nextInt(total);
            MobSpawnSettings.SpawnerData selected = null;
            for (MobSpawnSettings.SpawnerData candidate : pool) {
                choice -= candidate.getWeight().asInt();
                if (choice < 0) {
                    selected = candidate;
                    break;
                }
            }
            if (selected == null || !SpawnPlacements.checkSpawnRules(selected.type, level, MobSpawnType.NATURAL, pos, random))
                continue;
            if (!(selected.type.create(level) instanceof Mob mob)) continue;
            mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, random.nextFloat() * 360, 0);
            if (!mob.checkSpawnRules(level, MobSpawnType.NATURAL) || !mob.checkSpawnObstruction(level))
                continue;
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
            if (level.addFreshEntity(mob)) return true;
        }
        return false;
    }

    private static BlockPos findGround(ServerLevel level, BlockPos column) {
        BlockPos.MutableBlockPos pos = column.offset(0, 8, 0).mutable();
        for (int depth = 0; depth < 16; depth++, pos.move(0, -1, 0)) {
            if (level.isOutsideBuildHeight(pos)) continue;
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isCollisionShapeFullBlock(level, pos.below()))
                return pos.immutable();
        }
        return null;
    }
}
