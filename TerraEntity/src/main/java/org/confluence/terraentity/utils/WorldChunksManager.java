package org.confluence.terraentity.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界区块管理器 - 管理多个世界的强制加载区块
 * 注意：所有方法应在服务器主线程调用
 */
public class WorldChunksManager {
    // 使用线程安全的映射存储世界数据
    private static final Map<ServerLevel, ConcurrentHashMap<ChunkPos, ChunkInfo>> WORLD_DATA = new ConcurrentHashMap<>();
    private static final int DEFAULT_LOAD_DURATION = 30; // 默认加载持续时间（秒）

    /**
     * 区块信息内部数据结构
     */
    public static class ChunkInfo {
        private final String worldId;
        private final ChunkPos pos;
        private long expiryTick;
        private final long savedTick;

        public ChunkInfo(String worldId, ChunkPos pos, long expiryTick, long savedTick) {
            this.worldId = Objects.requireNonNull(worldId);
            this.pos = Objects.requireNonNull(pos);
            this.expiryTick = expiryTick;
            this.savedTick = savedTick;
        }

        public String getWorldId() { return worldId; }
        public ChunkPos getPos() { return pos; }
        public long getExpiryTick() { return expiryTick; }
        public long getSavedTick() { return savedTick; }

        public void setExpiryTick(long expiryTick) { this.expiryTick = expiryTick; }
    }

    /**
     * 获取世界的区块数据映射
     */
    private static ConcurrentHashMap<ChunkPos, ChunkInfo> getWorldChunkData(ServerLevel world) {
        return WORLD_DATA.computeIfAbsent(world, k -> new ConcurrentHashMap<>());
    }

    /**
     * 设置区块强制加载
     * @param world 目标世界
     * @param pos 区块位置
     */
    public static void forceLoadChunk(ServerLevel world, ChunkPos pos) {
        forceLoadChunk(world, pos, DEFAULT_LOAD_DURATION);
    }

    /**
     * 设置区块强制加载
     * @param world 目标世界
     * @param pos 区块位置
     * @param durationSeconds 加载持续时间（秒）
     */
    public static void forceLoadChunk(ServerLevel world, ChunkPos pos, int durationSeconds) {
        ConcurrentHashMap<ChunkPos, ChunkInfo> chunkDataMap = getWorldChunkData(world);
        long currentTick = world.getServer().getTickCount();
        long expiryTick = currentTick + durationSeconds * 20;

        // 如果区块已存在，则更新过期时间，否则添加新区块
        chunkDataMap.compute(pos, (key, existingInfo) -> {
            if (existingInfo != null) {
                existingInfo.setExpiryTick(expiryTick);
                return existingInfo;
            } else {
                world.setChunkForced(pos.x, pos.z, true);
                return new ChunkInfo(world.dimension().location().toString(), pos, expiryTick, currentTick);
            }
        });
    }

    /**
     * 释放过期的强制加载区块
     * @param world 目标世界
     */
    public static void freeChunks(ServerLevel world) {
        ConcurrentHashMap<ChunkPos, ChunkInfo> chunkDataMap = WORLD_DATA.get(world);
        if (chunkDataMap == null || chunkDataMap.isEmpty()) return;

        long currentTick = world.getServer().getTickCount();
        Iterator<Map.Entry<ChunkPos, ChunkInfo>> iterator = chunkDataMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<ChunkPos, ChunkInfo> entry = iterator.next();
            ChunkInfo info = entry.getValue();
            if (currentTick > info.getExpiryTick()) {
                world.setChunkForced(info.getPos().x, info.getPos().z, false);
                iterator.remove();
            }
        }

        if (chunkDataMap.isEmpty()) {
            WORLD_DATA.remove(world);
        }
    }

    /**
     * 获取区块数据
     * @param world 世界
     * @param pos 区块位置
     * @return 区块信息
     */
    public static Optional<ChunkInfo> getChunkData(ServerLevel world, ChunkPos pos) {
        ConcurrentHashMap<ChunkPos, ChunkInfo> chunkDataMap = WORLD_DATA.get(world);
        if (chunkDataMap == null) return Optional.empty();
        return Optional.ofNullable(chunkDataMap.get(pos));
    }

    /**
     * 卸载区块
     * @param world 世界
     * @param pos 区块位置
     * @return 被卸载的区块信息
     */
    public static Optional<ChunkInfo> unloadChunk(ServerLevel world, ChunkPos pos) {
        ConcurrentHashMap<ChunkPos, ChunkInfo> chunkDataMap = WORLD_DATA.get(world);
        if (chunkDataMap == null) return Optional.empty();

        ChunkInfo info = chunkDataMap.remove(pos);
        if (info != null) {
            world.setChunkForced(pos.x, pos.z, false);

            // 如果世界没有强制加载的区块，移除空映射
            if (chunkDataMap.isEmpty()) {
                WORLD_DATA.remove(world);
            }
        }
        return Optional.ofNullable(info);
    }

    /**
     * 获取所有强制加载的区块
     * @param world 世界
     * @return 区块信息集合
     */
    public static Collection<ChunkInfo> getChunks(ServerLevel world) {
        ConcurrentHashMap<ChunkPos, ChunkInfo> chunkDataMap = WORLD_DATA.get(world);
        return chunkDataMap != null ? Collections.unmodifiableCollection(chunkDataMap.values()) : Collections.emptyList();
    }

    /**
     * 清理指定世界的所有区块数据
     * @param world 要清理的世界
     */
    public static void cleanupWorld(ServerLevel world) {
        ConcurrentHashMap<ChunkPos, ChunkInfo> chunkDataMap = WORLD_DATA.remove(world);
        if (chunkDataMap != null) {
            // 解除所有区块的强制加载
            for (ChunkPos pos : chunkDataMap.keySet()) {
                world.setChunkForced(pos.x, pos.z, false);
            }
        }
    }

    /**
     * 获取所有被管理的世界
     * @return 世界集合
     */
    public static Set<ServerLevel> getManagedWorlds() {
        return Collections.unmodifiableSet(WORLD_DATA.keySet());
    }
}