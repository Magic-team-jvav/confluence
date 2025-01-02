package org.confluence.mod.client.textures;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.connected.SpriteShiftEntry;

import java.util.Hashtable;
import java.util.Map;

public record GraySpriteShifterEntry(TextureAtlasSprite original, TextureAtlasSprite target) {
    public static final Map<ResourceLocation, GraySpriteShifterEntry> ALL = new Hashtable<>();

    public float getTargetU(float localU) {
        return target.getU(SpriteShiftEntry.getUnInterpolatedU(original, localU));
    }

    public float getTargetV(float localV) {
        return target.getV(SpriteShiftEntry.getUnInterpolatedV(original, localV));
    }
}
