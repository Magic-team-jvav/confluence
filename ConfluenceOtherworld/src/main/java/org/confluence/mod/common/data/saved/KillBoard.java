package org.confluence.mod.common.data.saved;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.network.s2c.GamePhasePacketS2C;
import org.confluence.terraentity.init.entity.TEBossEntities;

public class KillBoard implements IGlobalData {
    public static final KillBoard INSTANCE = new KillBoard();
    public static final Codec<Object2BooleanMap<EntityType<?>>> DEFEATED_MAP_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Object2BooleanMap<EntityType<?>>, T>> decode(DynamicOps<T> ops, T input) {
            Object2BooleanMap<EntityType<?>> map = new Object2BooleanOpenHashMap<>();
            ops.getMap(input).getOrThrow().entries().forEach(pair -> {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(ops.getStringValue(pair.getFirst()).getOrThrow()));
                boolean defeated = ops.getBooleanValue(pair.getSecond()).getOrThrow();
                map.put(entityType, defeated);
            });
            return DataResult.success(new Pair<>(map, input), Lifecycle.stable());
        }

        @Override
        public <T> DataResult<T> encode(Object2BooleanMap<EntityType<?>> input, DynamicOps<T> ops, T prefix) {
            return DataResult.success(ops.createMap(input.object2BooleanEntrySet().stream().map(entry -> {
                T string = ops.createString(BuiltInRegistries.ENTITY_TYPE.getKey(entry.getKey()).toString());
                T defeated = ops.createBoolean(entry.getBooleanValue());
                return new Pair<>(string, defeated);
            })), Lifecycle.stable());
        }
    };

    private final Object2BooleanMap<EntityType<?>> defeatedMap = new Object2BooleanOpenHashMap<>();
    private GamePhase gamePhase = GamePhase.BEFORE_SKELETRON;

    public boolean isDefeated(EntityType<?> entityType) {
        return defeatedMap.getBoolean(entityType);
    }

    public boolean isAnyDefeated(EntityType<?>... entityTypes) {
        for (EntityType<?> entityType : entityTypes) {
            if (isDefeated(entityType)) return true;
        }
        return false;
    }

    public int countDefeated(EntityType<?>... entityTypes) {
        int count = 0;
        for (EntityType<?> entityType : entityTypes) {
            if (isDefeated(entityType)) count++;
        }
        return count;
    }

    public void defeat(EntityType<?> entityType) {
        defeatedMap.put(entityType, true);
        if (entityType == TEBossEntities.SKELETRON.get()) {
            KillBoard.INSTANCE.setGamePhase(ServerLifecycleHooks.getCurrentServer(), GamePhase.AFTER_SKELETRON);
        }
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(MinecraftServer server, GamePhase gamePhase) {
        this.gamePhase = gamePhase;
        GamePhasePacketS2C.sendToAll(gamePhase);
        if (gamePhase.isGraduated()) {
            ((IMinecraftServer) server).confluence$updateSecretFlag(IWorldOptions.GRADUATED);
        } else if (gamePhase.isHardmode()) {
            ((IMinecraftServer) server).confluence$updateSecretFlag(IWorldOptions.HARDMODE);
        }
    }

    @Override
    public <T> void decode(Dynamic<T> tag) {
        defeatedMap.clear();
        tag.get("defeated_map").orElseEmptyMap().read(DEFEATED_MAP_CODEC).ifSuccess(defeatedMap::putAll);
        this.gamePhase = GamePhase.getById(tag.get("game_phase").asInt(0));
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.put("defeated_map", DEFEATED_MAP_CODEC.encodeStart(NbtOps.INSTANCE, defeatedMap).getOrThrow());
        tag.putInt("game_phase", gamePhase.ordinal());
    }

    @Override
    public String serializeKey() {
        return "confluence:kill_board";
    }

    @Override
    public void clear() {
        defeatedMap.clear();
        this.gamePhase = GamePhase.BEFORE_SKELETRON;
    }
}
