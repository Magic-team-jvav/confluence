package org.confluence.mod.api.event.gameevent;

import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.confluence.mod.client.handler.ClientGameEventSystem;
import org.confluence.mod.common.gameevent.GameEvent;

import java.util.Map;

public class GameEventSyncCallbackRegisterEvent extends Event implements IModBusEvent {
    private final Map<ResourceKey<? extends GameEvent>, ClientGameEventSystem.GameEventSyncCallback> map;

    public GameEventSyncCallbackRegisterEvent(Map<ResourceKey<? extends GameEvent>, ClientGameEventSystem.GameEventSyncCallback> map) {
        this.map = map;
    }

    public <E extends GameEvent> void register(ResourceKey<E> key, ClientGameEventSystem.GameEventSyncCallback callback) {
        if (map.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated Callback: " + key);
        }
        map.put(key, callback);
    }
}
