package org.confluence.mod.api.event;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public class CustomWorldIconRegisterEvent extends Event implements IModBusEvent {
    private final LongSet exists;
    private final Long2ObjectMap<ResourceLocation> toAdd = new Long2ObjectOpenHashMap<>();

    public CustomWorldIconRegisterEvent(Long2ObjectMap<ResourceLocation> exists) {
        this.exists = new LongOpenHashSet(exists.keySet());
    }

    public void register(long flag, ResourceLocation worldIcon) {
        if (!exists.contains(flag)) {
            toAdd.put(flag, worldIcon);
        }
    }

    @ApiStatus.Internal
    public Long2ObjectMap<ResourceLocation> getToAdd() {
        return toAdd;
    }
}
