package org.confluence.mod.common.gameevent;

import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/// 事件系统重写到这
public final class GameEventSystem implements IGlobalData {
    public static final GameEventSystem INSTANCE = new GameEventSystem();

    private final Map<ResourceKey<? extends GameEvent>, GameEvent> events = Util.make(ImmutableMap.<ResourceKey<? extends GameEvent>, GameEvent>builder(), builder -> {
        builder.put(SlimeRainGameEvent.KEY, SlimeRainGameEvent.INSTANCE);
        // todo 事件注册
    }).build();

    private GameEventSystem() {}

    public void open(MinecraftServer server) {
        for (GameEvent event : events.values()) {
            event.open(server);
        }
    }

    public void close(MinecraftServer server) {
        for (GameEvent event : events.values()) {
            event.close(server);
        }
    }

    public void tick() {
        for (GameEvent event : events.values()) {
            event.tick();
            if (event.started()) {
                if (event.canEnd()) {
                    event.onEnd();
                }
            } else {
                if (event.canStart()) {
                    event.onStart();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public @Nullable <E extends GameEvent> E getEventInstance(ResourceKey<E> key) {
        return (E) events.get(key);
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
