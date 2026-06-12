package org.confluence.mod.client.effect.connected;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class SpriteShifter {
    private static final Map<String, SpriteShiftEntry> ENTRY_CACHE = new HashMap<>();

    public static SpriteShiftEntry get(ResourceLocation originalLocation, ResourceLocation targetLocation) {
        String key = originalLocation + "->" + targetLocation;
        SpriteShiftEntry entry = ENTRY_CACHE.get(key);
        if (entry != null) {
            return entry;
        }
        entry = new SpriteShiftEntry();
        entry.set(originalLocation, targetLocation);
        ENTRY_CACHE.put(key, entry);
        return entry;
    }
}
