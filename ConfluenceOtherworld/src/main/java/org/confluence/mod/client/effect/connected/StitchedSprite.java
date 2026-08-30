package org.confluence.mod.client.effect.connected;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.event.ModClientSetups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StitchedSprite {
	private static final Map<ResourceLocation, List<StitchedSprite>> ALL = new HashMap<>();

	protected final ResourceLocation atlasLocation;
	protected final ResourceLocation location;
	protected TextureAtlasSprite sprite;

	public StitchedSprite(ResourceLocation atlas, ResourceLocation location) {
		this.atlasLocation = atlas;
		this.location = location;
		ALL.computeIfAbsent(atlas, $ -> new ArrayList<>()).add(this);
	}

	public StitchedSprite(ResourceLocation location) {
		this(ModClientSetups.VANILLA_BLOCK_ATLAS, location);
	}

	public static void onTextureStitchPost(TextureAtlas atlas) {
		List<StitchedSprite> sprites = ALL.get(atlas.location());
		if (sprites != null) {
			for (StitchedSprite sprite : sprites) {
				sprite.loadSprite(atlas);
			}
		}
	}

	protected void loadSprite(TextureAtlas atlas) {
		this.sprite = atlas.getSprite(location);
	}

	public ResourceLocation getAtlasLocation() {
		return atlasLocation;
	}

	public ResourceLocation getLocation() {
		return location;
	}

	public TextureAtlasSprite get() {
		return sprite;
	}
}
