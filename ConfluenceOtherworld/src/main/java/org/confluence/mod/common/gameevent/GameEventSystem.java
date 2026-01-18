package org.confluence.mod.common.gameevent;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModLoader;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.NaturalSpawnerUtil;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.gameevent.CustomGameEventRegisterEvent;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.network.s2c.GameEventSyncPacketS2C;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class GameEventSystem implements IGlobalData {
    public static final GameEventSystem INSTANCE = new GameEventSystem();
    public static final ResourceKey<GameEvent> ALL_EVENT_KEY = GameEvent.createKey(Confluence.asResource("all_event"));

    private final Map<ResourceKey<? extends GameEvent>, GameEvent> events = Util.make(new IdentityHashMap<>(), map -> {
        map.put(SlimeRainGameEvent.KEY, SlimeRainGameEvent.INSTANCE);
        map.put(BloodMoonGameEvent.KEY, BloodMoonGameEvent.INSTANCE);
        map.put(GoblinArmyGameEvent.KEY, GoblinArmyGameEvent.INSTANCE);
        map.put(MeteorShowerGameEvent.KEY, MeteorShowerGameEvent.INSTANCE);
        map.put(LanternNightGameEvent.KEY, LanternNightGameEvent.INSTANCE);
        map.put(SpecificMoonGameEvent.KEY, SpecificMoonGameEvent.INSTANCE);
        map.put(FrostMoonGameEvent.KEY, FrostMoonGameEvent.INSTANCE);
        map.put(PumpkinMoonGameEvent.KEY, PumpkinMoonGameEvent.INSTANCE);
        ModLoader.postEvent(new CustomGameEventRegisterEvent(map));
    });
    private transient int startedEventAmount;
    private transient int startedNonEnvEventAmount;

    private GameEventSystem() {}

    public void syncAll(ServerPlayer player) {
        List<ResourceKey<? extends GameEvent>> started = new ArrayList<>(events.size());
        for (GameEvent event : getAllEventInstances()) {
            if (event.started()) {
                started.add(event.key());
            }
        }
        GameEventSyncPacketS2C.sentToClient(player, true, started);
    }

    public void clearAll(ServerPlayer player) {
        GameEventSyncPacketS2C.sentToClient(player, false, ALL_EVENT_KEY);
    }

    public void open(MinecraftServer server) {
        for (GameEvent event : getAllEventInstances()) {
            event.open(server);
        }
    }

    public void close(MinecraftServer server) {
        for (GameEvent event : getAllEventInstances()) {
            event.close(server);
        }
    }

    public void tick() {
        int started = 0;
        int nonEnv = 0;
        for (GameEvent event : getAllEventInstances()) {
            event.tick();
            if (event.started()) {
                ++started;
                if (event.isNonEnvEvent()) {
                    ++nonEnv;
                }
                if (event.canEnd()) {
                    event.onEnd();
                    KillBoard.INSTANCE.defeat(event.key());
                    GameEventSyncPacketS2C.sendToAll(false, event.key());
                }
            } else {
                if (event.canStart()) {
                    event.onStart();
                    GameEventSyncPacketS2C.sendToAll(true, event.key());
                }
            }
        }
        this.startedEventAmount = started;
        this.startedNonEnvEventAmount = nonEnv;
    }

    public void countKilled(LivingEntity living) {
        for (GameEvent event : getAllEventInstances()) {
            event.countKilled(living);
        }
    }

    @SuppressWarnings("unchecked")
    public @Nullable <E extends GameEvent> E getEventInstance(ResourceKey<E> key) {
        return (E) events.get(key);
    }

    public boolean isEventStarted(ResourceKey<? extends GameEvent> key) {
        GameEvent event = getEventInstance(key);
        return event != null && event.started();
    }

    public Collection<GameEvent> getAllEventInstances() {
        return events.values();
    }

    public Map<ResourceKey<? extends GameEvent>, GameEvent> getEvents() {
        return events;
    }

    /// 获取正在运行的事件数量
    ///
    /// @param nonEnv 非环境事件
    /// @param reCal  重新计数
    /// @return 数量
    public int getStatedEventAmount(boolean nonEnv, boolean reCal) {
        if (reCal) {
            int started = 0;
            int nonEnvA = 0;
            for (GameEvent event : getAllEventInstances()) {
                if (event.started()) {
                    ++started;
                    if (event.isNonEnvEvent()) {
                        ++nonEnvA;
                    }
                }
            }
            this.startedEventAmount = started;
            this.startedNonEnvEventAmount = nonEnvA;
        }
        return nonEnv ? startedNonEnvEventAmount : startedEventAmount;
    }

    @Override
    public void decode(CompoundTag tag) {
        for (Map.Entry<ResourceKey<? extends GameEvent>, GameEvent> entry : events.entrySet()) {
            entry.getValue().decode(tag.getCompound(entry.getKey().location().toString()));
        }
    }

    @Override
    public void encode(CompoundTag tag) {
        for (Map.Entry<ResourceKey<? extends GameEvent>, GameEvent> entry : events.entrySet()) {
            CompoundTag nbt = new CompoundTag();
            entry.getValue().encode(nbt);
            tag.put(entry.getKey().location().toString(), nbt);
        }
    }

    @Override
    public String serializeKey() {
        return "confluence:game_event_system";
    }

    public static final Map<ResourceKey<? extends GameEvent>, GameEvent> INVASION_EVENTS = Util.make(new IdentityHashMap<>(), map -> {
        map.put(GoblinArmyGameEvent.KEY, GoblinArmyGameEvent.INSTANCE);
        // todo 雪人，海盗，火星
    });

    public static boolean isInvasionEvent(ResourceKey<? extends GameEvent> key) {
        return INVASION_EVENTS.containsKey(key);
    }

    public static boolean anyInvasionStarted() {
        for (GameEvent event : INVASION_EVENTS.values()) {
            if (event.started()) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldDenyNatureSpawn() {
        return anyInvasionStarted(); // todo 日食，四柱
    }

    public static void removeUnTracked(Set<Entity> spawned, ServerLevel level) {
        spawned.removeIf(entity -> {
            if (LibUtils.getChunkIfLoaded(level.getChunkSource(), entity.chunkPosition()) == null) {
                entity.discard();
                return true;
            }
            return entity.isRemoved();
        });
    }

    public static void customSpawner(
            GameEvent event,
            ServerLevel level,
            Set<Entity> spawned,
            int perPlayer,
            float intervalFactor,
            WeightedRandomList<MobSpawnSettings.SpawnerData> spawnerDataList,
            String entityTag,
            boolean setTarget
    ) {
        Long2ObjectMap<NaturalSpawnerUtil.ChunkSpawnData> map = NaturalSpawnerUtil.getDimensionChunkSpawnData(level.dimension());
        if (map == null) {
            event.forceEnd();
            return;
        }
        removeUnTracked(spawned, level);
        List<ServerPlayer> players = level.players();
        if (spawned.size() >= perPlayer + players.size() * perPlayer) return;
        for (ServerPlayer player : players) {
            NaturalSpawnerUtil.ChunkSpawnData data = map.getOrDefault(player.chunkPosition().toLong(), NaturalSpawnerUtil.ChunkSpawnData.DEFAULT);
            double speed = data.speedMultiplier();
            if (speed <= 0) continue;
            int interval = Mth.floor(20 * intervalFactor / speed);
            if (level.random.nextInt(interval) != 0) continue;
            Optional<MobSpawnSettings.SpawnerData> random = spawnerDataList.getRandom(level.random);
            if (random.isEmpty()) continue;
            Vec3 position = player.position();
            MobSpawnSettings.SpawnerData spawnerData = random.get();
            int count = data.getCount(level.random.nextIntBetweenInclusive(spawnerData.minCount, spawnerData.maxCount));
            for (int j = 0; j < count; j++) {
                double x = Mth.nextDouble(level.random, position.x - 32, position.x + 32);
                double z = Mth.nextDouble(level.random, position.z - 32, position.z + 32);
                int cx = SectionPos.blockToSectionCoord(x);
                int cz = SectionPos.blockToSectionCoord(z);
                if (LibUtils.getChunkIfLoaded(level.getChunkSource(), cx, cz) == null) {
                    continue;
                }
                BlockPos pos = NaturalSpawner.getTopNonCollidingPos(level, spawnerData.type, Mth.floor(x), Mth.floor(z));
                Entity entity = spawnerData.type.spawn(level, pos, MobSpawnType.EVENT);
                if (entity != null) {
                    entity.addTag(entityTag);
                    spawned.add(entity);
                    if (setTarget && entity instanceof Mob mob && player.canBeSeenAsEnemy()) {
                        mob.setTarget(player);
                    }
                }
            }
        }
    }
}
