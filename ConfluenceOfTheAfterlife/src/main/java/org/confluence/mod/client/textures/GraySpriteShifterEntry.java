package org.confluence.mod.client.textures;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.connected.SpriteShiftEntry;

import java.util.Hashtable;
import java.util.Map;

public record GraySpriteShifterEntry(TextureAtlasSprite original, TextureAtlasSprite gray, TextureAtlasSprite negative) {
    public static final Map<ResourceLocation, GraySpriteShifterEntry> ALL = new Hashtable<>();

    public float getTargetU(TextureAtlasSprite sprite, float localU) {
        return sprite.getU(SpriteShiftEntry.getUnInterpolatedU(original, localU));
    }

    public float getTargetV(TextureAtlasSprite sprite, float localV) {
        return sprite.getV(SpriteShiftEntry.getUnInterpolatedV(original, localV));
    }
}
