package org.confluence.lib.util;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.dimension.LevelStem;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.mixed.IChunkSpawnDataAccess;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// 生物刷新工具类
public final class NaturalSpawnerUtil {
    private static final Map<ResourceKey<Level>, Long2ObjectMap<ChunkSpawnData>> CHUNK_DATA = new IdentityHashMap<>();
    private static Set<ResourceKey<Level>> UNKNOWN_DIMENSIONS;
    private static int maxDataDistance;

    public static NaturalSpawnerUtil.ChunkSpawnData getChunkSpawnData(ResourceKey<Level> dimension, ChunkPos pos) {
        Long2ObjectMap<ChunkSpawnData> map = CHUNK_DATA.get(dimension);
        return map == null ? ChunkSpawnData.DEFAULT : map.getOrDefault(pos.toLong(), ChunkSpawnData.DEFAULT);
    }

    public static @Nullable Long2ObjectMap<ChunkSpawnData> getDimensionChunkSpawnData(ResourceKey<Level> dimension) {
        return CHUNK_DATA.get(dimension);
    }

    public static void init(MinecraftServer server) {
        Registry<LevelStem> registry = server.registries().compositeAccess().registryOrThrow(Registries.LEVEL_STEM);
        for (ResourceKey<LevelStem> key : registry.registryKeySet()) {
            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, key.location());
            CHUNK_DATA.put(dimension, new Long2ObjectOpenHashMap<>());
        }
        maxDataDistance = Arrays.stream(NaturalSpawner.SPAWNING_CATEGORIES)
                .mapToInt(MobCategory::getDespawnDistance)
                .max().orElse(128) / 16 + 1;
    }

    public static void update(MinecraftServer server) {
        if (server.getWorldData().overworldData().getGameTime() % 100 == 4) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ResourceKey<Level> dimension = player.level().dimension();
                if (!CHUNK_DATA.containsKey(dimension)) {
                    if (UNKNOWN_DIMENSIONS == null) {
                        UNKNOWN_DIMENSIONS = new HashSet<>();
                    }
                    if (UNKNOWN_DIMENSIONS.add(dimension)) {
                        ConfluenceMagicLib.LOGGER.warn("Why there's a new dimension '{}' here?", dimension.location());
                    }
                    continue;
                }
                Long2ObjectMap<ChunkSpawnData> map = new Long2ObjectOpenHashMap<>();
                double count = player.getAttributeValue(ConfluenceMagicLib.MOB_SPAWN_COUNT_MULTIPLIER);
                double speed = player.getAttributeValue(ConfluenceMagicLib.MOB_SPAWN_SPEED_MULTIPLIER);
                ChunkSpawnData spawnData = new ChunkSpawnData(count, speed);
                ChunkPos pos = player.chunkPosition();
                double lambda = 1.0F / maxDataDistance;
                for (int i = pos.x - maxDataDistance; i <= pos.x + maxDataDistance; i++) {
                    for (int j = pos.z - maxDataDistance; j <= pos.z + maxDataDistance; j++) {
                        double factor = Mth.clamp(1 - lambda * (Math.abs(i - pos.x) + Math.abs(j - pos.z)), 0, 1); // 随曼哈顿距离衰减的量
                        long p = ChunkPos.asLong(i, j);
                        ChunkSpawnData data = map.get(p);
                        if (data == null) {
                            map.put(p, spawnData);
                        } else {
                            map.put(p, new ChunkSpawnData(
                                    data.countMultiplier() + (count * factor - 1),
                                    data.speedMultiplier() + (speed * factor - 1)
                            ));
                        }
                    }
                }
                CHUNK_DATA.put(dimension, map);
            }
        }
    }

    public static void clear() {
        CHUNK_DATA.clear();
    }

    public static boolean modifySpeed(
            boolean original,
            MobCategory category,
            int k,
            LocalIntRef frequency,
            LocalBooleanRef obtained
    ) {
        if (obtained.get()) {
            int f = frequency.get();
            if (f == 0) {
                return original;
            }
            return k < f;
        }

        ChunkSpawnData data = IChunkSpawnDataAccess.of(category).confluence$getData();
        if (data == ChunkSpawnData.DEFAULT) {
            return original;
        }

        int f = data.getSpeed(getOriginalSpeed());
        frequency.set(f);
        obtained.set(true);
        return k < f;
    }

    private static int getOriginalSpeed() {
        return 3;
    }

    public record ChunkSpawnData(double countMultiplier, double speedMultiplier) {
        public static final ChunkSpawnData DEFAULT = new ChunkSpawnData(1, 1);

        public ChunkSpawnData {
            countMultiplier = Math.max(countMultiplier, 0);
            speedMultiplier = Math.max(speedMultiplier, 0);
        }

        public int getCount(int original) {
            double m = countMultiplier();
            if (m == 1) return original;
            return Mth.ceil(original * m);
        }

        public int getSpeed(int original) {
            double m = speedMultiplier();
            if (m == 1) return original;
            return Mth.ceil(original * m);
        }
    }
}
