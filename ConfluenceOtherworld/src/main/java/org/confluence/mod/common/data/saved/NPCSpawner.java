package org.confluence.mod.common.data.saved;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.confluence.mod.mixed.IAbstractTerraNPC;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.init.entity.TENpcEntities;

import java.util.*;

/**
 * 注：NPC默认生成在对应玩家出生点
 */
public class NPCSpawner {
    public static final NPCSpawner INSTANCE = new NPCSpawner();
    public static final Codec<Map<Region, Object2BooleanMap<EntityType<?>>>> NPC_ALIVE_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Map<Region, Object2BooleanMap<EntityType<?>>>, T>> decode(DynamicOps<T> ops, T input) {
            Map<Region, Object2BooleanMap<EntityType<?>>> map = new HashMap<>();
            ops.getMap(input).getOrThrow().entries().forEach(pair -> {
                Region region = new Region(new ChunkPos(Long.parseLong(ops.getStringValue(pair.getFirst()).getOrThrow())));
                Object2BooleanMap<EntityType<?>> map1 = new Object2BooleanOpenHashMap<>();
                ops.getMap(pair.getSecond()).getOrThrow().entries().forEach(pair1 -> {
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(ops.getStringValue(pair1.getFirst()).getOrThrow()));
                    boolean spawned = ops.getBooleanValue(pair1.getSecond()).getOrThrow();
                    map1.put(entityType, spawned);
                });
                map.put(region, map1);
            });
            return DataResult.success(new Pair<>(map, input));
        }

        @Override
        public <T> DataResult<T> encode(Map<Region, Object2BooleanMap<EntityType<?>>> input, DynamicOps<T> ops, T prefix) {
            return DataResult.success(ops.createMap(input.entrySet().stream().map(entry -> {
                T string = ops.createString(Long.toString(entry.getKey().toLong()));
                T map = ops.createMap(entry.getValue().object2BooleanEntrySet().stream().map(entry1 -> {
                    T string1 = ops.createString(BuiltInRegistries.ENTITY_TYPE.getKey(entry1.getKey()).toString());
                    T aBoolean = ops.createBoolean(entry1.getBooleanValue());
                    return new Pair<>(string1, aBoolean);
                }));
                return new Pair<>(string, map);
            })));
        }
    };
    public static final Codec<Set<EntityType<?>>> NPC_SPAWNED_CODEC = BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().xmap(HashSet::new, ArrayList::new);

    private final Map<Region, Object2BooleanMap<EntityType<?>>> npcAlive = new HashMap<>();
    /**
     * 生成过的NPC，可用于NPC复活而无需再次满足条件
     */
    private final Set<EntityType<?>> npcSpawned = new HashSet<>();

    public int getAliveNpcCount(Region region) {
        Object2BooleanMap<EntityType<?>> map = npcAlive.get(region);
        if (map == null) return 0;
        int count = 0;
        for (boolean alive : map.values()) if (alive) count++;
        return count;
    }

    public boolean hasNPCAlive(Region region, EntityType<?> entityType) {
        return npcAlive.computeIfAbsent(region, region1 -> new Object2BooleanOpenHashMap<>()).getOrDefault(entityType, false);
    }

    public void setNPCAlive(Region region, EntityType<?> entityType, boolean spawned) {
        npcAlive.computeIfAbsent(region, region1 -> new Object2BooleanOpenHashMap<>()).put(entityType, spawned);
        if (spawned) {
            npcSpawned.add(entityType);
        }
    }

    public void moveNPCToAnotherRegion(EntityType<?> entityType, Region from, Region to) {
        if (hasNPCAlive(from, entityType)) {
            setNPCAlive(from, entityType, false);
            setNPCAlive(to, entityType, true);
        }
    }

    public void onNPCAdded(Entity entity) {
        if (entity instanceof IAbstractTerraNPC npc) {
            npc.confluence$setRegion(new Region(entity.chunkPosition()));
            setNPCAlive(npc.confluence$getRegion(), entity.getType(), true);
        }
    }

    public void onNPCRemoved(Entity entity) {
        if (entity instanceof IAbstractTerraNPC npc) {
            setNPCAlive(npc.confluence$getRegion(), entity.getType(), false);
        }
    }

    public <T> void decode(Dynamic<T> tag) {
        Dynamic<T> dynamic = tag.get("confluence:npc_spawner").orElseEmptyMap();
        dynamic.get("npc_alive").orElseEmptyMap().read(NPC_ALIVE_CODEC).ifSuccess(result -> {
            npcAlive.clear();
            npcAlive.putAll(result);
        });
        dynamic.get("npc_spawned").orElseEmptyList().read(NPC_SPAWNED_CODEC).ifSuccess(result -> {
            npcSpawned.clear();
            npcSpawned.addAll(result);
        });
    }

    public void encode(CompoundTag nbt) {
        CompoundTag tag = new CompoundTag();
        tag.put("npc_alive", NPC_ALIVE_CODEC.encodeStart(NbtOps.INSTANCE, npcAlive).getOrThrow());
        tag.put("npc_spawned", NPC_SPAWNED_CODEC.encodeStart(NbtOps.INSTANCE, npcSpawned).getOrThrow());
        nbt.put("confluence:npc_spawner", tag);
    }

    public void trySpawnGuide(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        if (serverLevel.dimension() == Level.OVERWORLD) {
            BlockPos pos = getNpcSpawnPos(serverPlayer);
            if (hasNPCAlive(new Region(pos), TENpcEntities.GUIDE.get())) return;
            AbstractTerraNPC npc = TENpcEntities.GUIDE.get().create(serverLevel);
            if (npc == null) return;
            npc.setPos(pos.getCenter());
            serverPlayer.level().addFreshEntity(npc);
            onNPCAdded(npc);
        }
    }

    public void checkNpcRespawn(ServerLevel serverLevel) {
        for (ServerPlayer player : serverLevel.players()) {
            for (EntityType<?> entityType : npcSpawned) { // 省去了两分钟冷却
                BlockPos pos = getNpcSpawnPos(player);
                if (hasNPCAlive(new Region(pos), entityType)) continue;
                Entity entity = entityType.create(serverLevel);
                if (entity == null) continue;
                entity.setPos(pos.getCenter());
                serverLevel.addFreshEntity(entity);
                onNPCAdded(entity);
            }
        }
    }

    public static BlockPos getNpcSpawnPos(ServerPlayer player) {
        return player.getRespawnPosition() == null ? player.level().getLevelData().getSpawnPos() : player.getRespawnPosition();
    }

    public record Region(int x, int z) {
        public static final Codec<Region> CODEC = Codec.LONG.xmap(Region::new, Region::toLong);

        public Region(BlockPos pos) {
            this(new ChunkPos(pos));
        }

        public Region(long packed) {
            this(new ChunkPos(packed));
        }

        public Region(ChunkPos pos) {
            this(((pos.x + 8) >> 4 << 4) - 8, ((pos.z + 8) >> 4 << 4) - 8);
        }

        public boolean isOnRegion(BlockPos pos) {
            return isOnRegion(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        }

        public boolean isOnRegion(ChunkPos pos) {
            return isOnRegion(pos.x, pos.z);
        }

        public boolean isOnRegion(int chunkX, int chunkZ) {
            return chunkX >= x && chunkX < x + 16 && chunkZ >= z && chunkZ < z + 16;
        }

        public long toLong() {
            return ChunkPos.asLong(x, z);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            return o instanceof Region(int x1, int z1) && x1 == x && z1 == z;
        }

        @Override
        public int hashCode() {
            return ChunkPos.hash(x, z);
        }

        @Override
        public String toString() {
            return "Region(x=[" + x + ", " + (x + 15) + "], z=[" + z + ", " + (z + 15) + "])";
        }
    }
}
