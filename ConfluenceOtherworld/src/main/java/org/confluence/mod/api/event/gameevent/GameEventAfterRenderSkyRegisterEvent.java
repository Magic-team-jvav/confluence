package org.confluence.mod.api.event.gameevent;

import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.confluence.mod.client.gameevent.AfterRenderSky;
import org.confluence.mod.common.gameevent.GameEvent;

import java.util.Map;

public class GameEventAfterRenderSkyRegisterEvent extends Event implements IModBusEvent {
    private final Map<ResourceKey<? extends GameEvent>, AfterRenderSky> map;

    public GameEventAfterRenderSkyRegisterEvent(Map<ResourceKey<? extends GameEvent>, AfterRenderSky> map) {
        this.map = map;
    }

    public <E extends GameEvent> void register(ResourceKey<E> key, AfterRenderSky afterRenderSky) {
        if (map.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated Callback: " + key);
        }
        map.put(key, afterRenderSky);
    }
}
