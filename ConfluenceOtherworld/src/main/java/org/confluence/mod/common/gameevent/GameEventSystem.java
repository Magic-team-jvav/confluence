package org.confluence.mod.common.gameevent;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.network.s2c.GameEventSyncPacketS2C;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// 事件系统重写到这
public final class GameEventSystem implements IGlobalData {
    public static final GameEventSystem INSTANCE = new GameEventSystem();
    public static final ResourceKey<GameEvent> ALL_EVENT_KEY = GameEvent.createKey(Confluence.asResource("all_event"));

    private final Map<ResourceKey<? extends GameEvent>, GameEvent> events = Util.make(new IdentityHashMap<>(), map -> {
        map.put(SlimeRainGameEvent.KEY, SlimeRainGameEvent.INSTANCE);
        map.put(BloodMoonGameEvent.KEY, BloodMoonGameEvent.INSTANCE);
        map.put(GoblinArmyGameEvent.KEY, GoblinArmyGameEvent.INSTANCE);
        // todo 事件注册
    });

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
        for (GameEvent event : getAllEventInstances()) {
            event.tick();
            if (event.started()) {
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

    public int getStatedEventAmount() {
        int amount = 0;
        for (GameEvent event : getAllEventInstances()) {
            if (event.started()) {
                ++amount;
            }
        }
        return amount;
    }

    public boolean anyEventStarted() {
        for (GameEvent event : getAllEventInstances()) {
            if (event.started()) {
                return true;
            }
        }
        return false;
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
}
