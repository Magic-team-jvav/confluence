package org.confluence.mod.api.event;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public class CustomWorldIconRegisterEvent extends Event implements IModBusEvent {
    private final Long2ObjectMap<ResourceLocation> exists;
    private final Long2ObjectMap<ResourceLocation> toAdd = new Long2ObjectOpenHashMap<>();

    public CustomWorldIconRegisterEvent(Long2ObjectMap<ResourceLocation> exists) {
        this.exists = new Long2ObjectOpenHashMap<>(exists);
    }

    public void register(long flag, ResourceLocation worldIcon) {
        if (!exists.containsKey(flag)) {
            toAdd.put(flag, worldIcon);
        }
    }

    @ApiStatus.Internal
    public Long2ObjectMap<ResourceLocation> getToAdd() {
        return toAdd;
    }
}
