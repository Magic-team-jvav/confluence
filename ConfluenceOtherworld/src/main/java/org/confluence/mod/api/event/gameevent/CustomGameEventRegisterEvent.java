package org.confluence.mod.api.event.gameevent;

import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.confluence.mod.common.gameevent.GameEvent;

import java.util.Map;

public class CustomGameEventRegisterEvent extends Event implements IModBusEvent {
    private final Map<ResourceKey<? extends GameEvent>, GameEvent> map;

    public CustomGameEventRegisterEvent(Map<ResourceKey<? extends GameEvent>, GameEvent> map) {
        this.map = map;
    }

    public <E extends GameEvent> void register(ResourceKey<E> key, E event) {
        if (map.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate GameEvent: " + key);
        }
        map.put(key, event);
    }
}
