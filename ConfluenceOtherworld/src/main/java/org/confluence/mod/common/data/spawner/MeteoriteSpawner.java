package org.confluence.mod.common.data.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.common.worldgen.biome.MiniBiome;
import org.confluence.mod.common.entity.monster.MeteorHead;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.util.ModMiniBiomes;

/// **陨石迷你群系**里生成流星头。
///
/// 骨架照抄 {@link SpaceSpawner}（自定义 `CustomSpawner` + 节流 + 逐玩家判定 + 逐候选位置校验），
/// 区别只有判定条件：这里查的是迷你生物群系标记。
///
/// ## 三个要点
///
/// 1. **判定放在玩家身上，不是生成位置上。** 泰拉的生物群系本来就是以玩家为中心的
///    （`SceneMetrics` 扫的是玩家周围的窗口），所以「玩家在陨石区」就算，流星头在附近冒出来。
/// 2. **查询是窗口求和，必须节流。** 一次 {@link MiniBiome#markersAt} 要遍历窗口里所有 section，
///    所以本 spawner 自己按秒级间隔节流，而且每次尝试每个玩家只查一次。
///    要更精细就自己按玩家缓存（见 `MiniBiome#windowCounts`）。
/// 3. **强度可以拿来当渐变旋钮。** `marker.influence()` 是 0..1，
///    陨石块越多，一次冒出来的流星头越多 —— 这就是「渐变」在玩法上的用法。
public class MeteoriteSpawner implements CustomSpawner {
    /// 两次尝试的间隔（秒）。泰拉的刷怪频率也是秒级，不是每 tick。
    public static final int MIN_INTERVAL_SECONDS = 8;
    public static final int MAX_INTERVAL_SECONDS = 20;
    /// 玩家周围流星头的数量上限
    public static final int MAX_NEARBY = 6;
    /// 生成尝试的水平半径（方块）
    public static final int SPAWN_RADIUS = 16;
    /// 生成尝试的垂直范围（±）
    public static final int SPAWN_HALF_HEIGHT = 8;

    private int nextTick;

    @Override
    public int tick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
        if (!spawnEnemies || level.players().isEmpty()) return 0;
        RandomSource random = level.random;
        if (--nextTick > 0) return 0;
        nextTick = (MIN_INTERVAL_SECONDS + random.nextInt(MAX_INTERVAL_SECONDS - MIN_INTERVAL_SECONDS + 1)) * 20;

        int spawned = 0;
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) continue;
            // 节流之后，每个玩家每次尝试只查一次迷你群系
            MiniBiome.Marker marker = MiniBiome.primaryAt(level, player.blockPosition());
            if (marker == null || !marker.id().equals(ModMiniBiomes.METEORITE.id())) continue;
            // 交错时想拿全部命中就用 markersAt(...)，再自己按 id 挑
            int amount = 1 + Mth.floor(marker.influence() * 2.0F);
            spawned += spawnAround(level, player, amount, random);
        }
        return spawned;
    }

    private int spawnAround(ServerLevel level, ServerPlayer player, int amount, RandomSource random) {
        EntityType<MeteorHead> type = MonsterEntities.METEOR_HEAD.get();
        BlockPos origin = player.blockPosition();
        if (level.getEntities(type, new AABB(origin).inflate(SPAWN_RADIUS * 2.0), EntitySelector.NO_SPECTATORS).size() >= MAX_NEARBY) {
            return 0;
        }

        int spawned = 0;
        DifficultyInstance difficulty = level.getCurrentDifficultyAt(origin);
        SpawnGroupData groupData = null;
        for (int attempt = 0; attempt < 12 && spawned < amount; attempt++) {
            BlockPos pos = origin.offset(
                    random.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS,
                    random.nextInt(SPAWN_HALF_HEIGHT * 2 + 1) - SPAWN_HALF_HEIGHT,
                    random.nextInt(SPAWN_RADIUS * 2 + 1) - SPAWN_RADIUS
            );
            if (!level.isLoaded(pos)) continue;
            BlockState state = level.getBlockState(pos);
            FluidState fluid = level.getFluidState(pos);
            // 流星头是飞行怪，只需要一个空的可生成方块（与 SpaceSpawner 里哈比的做法一致）
            if (!NaturalSpawner.isValidEmptySpawnBlock(level, pos, state, fluid, type)) continue;
            if (!SpawnPlacements.checkSpawnRules(type, level, MobSpawnType.NATURAL, pos, random)) continue;

            Entity entity = type.create(level);
            if (entity == null) continue;
            entity.moveTo(pos, 0.0F, 0.0F);
            if (entity instanceof Mob mob) {
                groupData = mob.finalizeSpawn(level, difficulty, MobSpawnType.NATURAL, groupData, null);
            }
            level.addFreshEntityWithPassengers(entity);
            spawned++;
        }
        return spawned;
    }
}
