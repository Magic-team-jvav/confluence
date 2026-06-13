package org.confluence.mod.client.effect.connected;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public class SpriteShiftEntry {
    protected StitchedSprite original;
    protected StitchedSprite[] target;

    public void set(ResourceLocation originalTextureLocation, ResourceLocation... targetTextureLocation) {
        this.original = new StitchedSprite(originalTextureLocation);
        this.target = Arrays.stream(targetTextureLocation).map(StitchedSprite::new).toArray(StitchedSprite[]::new);
    }

    public int getTextureAmount() {
        return target.length;
    }

    public ResourceLocation getOriginalResourceLocation() {
        return original.getLocation();
    }

    public ResourceLocation getTargetResourceLocation(int index) {
        return target[index].getLocation();
    }

    public TextureAtlasSprite getOriginal() {
        return original.get();
    }

    public TextureAtlasSprite getTarget(int targetIndex) {
        return target[targetIndex].get();
    }

    public float getTargetU(float localU, int targetIndex) {
        return getTarget(targetIndex).getU(getUnInterpolatedU(getOriginal(), localU));
    }

    public float getTargetV(float localV, int targetIndex) {
        return getTarget(targetIndex).getV(getUnInterpolatedV(getOriginal(), localV));
    }

    /// @see TextureAtlasSprite#getU(float)
    public static float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
        float f = sprite.getU1() - sprite.getU0();
        return (u - sprite.getU0()) / f;
    }

    /// @see TextureAtlasSprite#getV(float)
    public static float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
        float f = sprite.getV1() - sprite.getV0();
        return (v - sprite.getV0()) / f;
    }
}
