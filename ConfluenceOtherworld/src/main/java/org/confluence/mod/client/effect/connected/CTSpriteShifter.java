package org.confluence.mod.client.effect.connected;

import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CTSpriteShifter {
	private static final Map<String, CTSpriteShiftEntry> ENTRY_CACHE = new HashMap<>();

	public static CTSpriteShiftEntry getCT(CTType type, ResourceLocation blockTexture, ResourceLocation... connectedTexture) {
		String key = blockTexture + "->" + Arrays.toString(connectedTexture) + "+" + type.getId();
        CTSpriteShiftEntry entry = ENTRY_CACHE.get(key);
        if (entry != null) {
            return entry;
		}
        entry = new CTSpriteShiftEntry(type);
		entry.set(blockTexture, connectedTexture);
		ENTRY_CACHE.put(key, entry);
		return entry;
	}
}
