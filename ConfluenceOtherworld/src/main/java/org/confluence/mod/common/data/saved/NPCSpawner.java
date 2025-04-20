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

import java.util.HashMap;
import java.util.Map;

public class NPCSpawner {
    public static final NPCSpawner INSTANCE = new NPCSpawner();
    public static final Codec<Map<Region, Object2BooleanMap<EntityType<?>>>> NPC_LIVES_CODEC = new Codec<>() {
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

    private final Map<Region, Object2BooleanMap<EntityType<?>>> npcLives = new HashMap<>();

    public boolean hasNPCSpawned(Region region, EntityType<?> entityType) {
        return npcLives.computeIfAbsent(region, region1 -> new Object2BooleanOpenHashMap<>()).getOrDefault(entityType, false);
    }

    public void setNPCSpawned(Region region, EntityType<?> entityType, boolean spawned) {
        npcLives.computeIfAbsent(region, region1 -> new Object2BooleanOpenHashMap<>()).put(entityType, spawned);
    }

    public void moveNPCToAnotherRegion(EntityType<?> entityType, Region from, Region to) {
        if (hasNPCSpawned(from, entityType)) {
            setNPCSpawned(from, entityType, false);
            setNPCSpawned(to, entityType, true);
        }
    }

    public void onNPCRemoved(Entity entity) {
        if (entity instanceof IAbstractTerraNPC npc) {
            setNPCSpawned(npc.confluence$getRegion(), entity.getType(), false);
        }
    }

    public <T> void decode(Dynamic<T> tag) {
        Dynamic<T> dynamic = tag.get("confluence:npc_spawner").orElseEmptyMap();
        dynamic.get("npc_lives").orElseEmptyMap().read(NPC_LIVES_CODEC).ifSuccess(result -> {
            npcLives.clear();
            npcLives.putAll(result);
        });
    }

    public void encode(CompoundTag nbt) {
        CompoundTag tag = new CompoundTag();
        tag.put("npc_lives", NPC_LIVES_CODEC.encodeStart(NbtOps.INSTANCE, npcLives).getOrThrow());
        nbt.put("confluence:npc_spawner", tag);
    }

    public void trySpawnGuide(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        if (serverLevel.dimension() == Level.OVERWORLD) {
            Region region = new Region(serverPlayer.chunkPosition());
            if (!hasNPCSpawned(region, TENpcEntities.GUIDE.get())) {
                AbstractTerraNPC npc = TENpcEntities.GUIDE.get().create(serverLevel);
                if (npc != null) {
                    npc.setPos(serverLevel.getLevelData().getSpawnPos().getCenter());
                    IAbstractTerraNPC.of(npc).confluence$setRegion(region);
                    serverPlayer.level().addFreshEntity(npc);
                    setNPCSpawned(region, TENpcEntities.GUIDE.get(), true);
                }
            }
        }
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
