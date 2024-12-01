package org.confluence.mod.client.connected;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class CTSpriteShifter {
	private static final Map<String, SpriteShiftEntry> ENTRY_CACHE = new HashMap<>();

	public static CTSpriteShiftEntry getCT(CTType type, ResourceLocation blockTexture, ResourceLocation connectedTexture) {
		String key = blockTexture + "->" + connectedTexture + "+" + type.getId();
		if (ENTRY_CACHE.containsKey(key)) {
			return (CTSpriteShiftEntry) ENTRY_CACHE.get(key);
		}
		CTSpriteShiftEntry entry = new CTSpriteShiftEntry(type);
		entry.set(blockTexture, connectedTexture);
		ENTRY_CACHE.put(key, entry);
		return entry;
	}

}
