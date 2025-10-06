package org.confluence.mod.client.connected;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CTTypeRegistry {
	private static final Map<ResourceLocation, CTType> TYPES = new HashMap<>();

	public static void register(CTType type) {
		ResourceLocation id = type.getId();
		if (TYPES.containsKey(id))
			throw new IllegalArgumentException("Tried to override CTType registration for id '" + id + "'. This is not supported!");
		TYPES.put(id, type);
	}

	@Nullable
	public static CTType get(ResourceLocation id) {
		return TYPES.get(id);
	}
}
