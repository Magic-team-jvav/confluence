package org.confluence.mod.common.gameevent;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModLoader;
import org.confluence.lib.common.data.saved.IGlobalData;
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
        ModLoader.postEvent(new CustomGameEventRegisterEvent(map));
    });
    private transient int startedEventAmount;
    private transient int startedNonEnvEventAmount;

    private GameEventSystem() {}

    public void playerLoggedIn(ServerPlayer player) {
        List<ResourceKey<? extends GameEvent>> keys = new ArrayList<>(events.size());
        for (GameEvent event : getAllEventInstances()) {
            if (event.started()) {
                keys.add(event.key());
            }
        }
        GameEventSyncPacketS2C.sentToClient(player, true, keys);
    }

    public void playerLoggedOut(ServerPlayer player) {
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

    public static boolean anyInvasionStarted() {
        return GoblinArmyGameEvent.INSTANCE.started(); // todo 雪人，海盗，火星
    }

    public static boolean shouldDenyNatureSpawn() {
        return GoblinArmyGameEvent.INSTANCE.started(); // todo 雪人，海盗，火星，日食，四柱
    }
}
