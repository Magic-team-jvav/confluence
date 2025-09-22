package org.confluence.mod.client.effect.connected;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class SpriteShiftEntry {
    protected StitchedSprite original;
    protected StitchedSprite target;

    public void set(ResourceLocation originalTextureLocation, ResourceLocation targetTextureLocation) {
        this.original = new StitchedSprite(originalTextureLocation);
        this.target = new StitchedSprite(targetTextureLocation);
    }

    public ResourceLocation getOriginalResourceLocation() {
        return original.getLocation();
    }

    public ResourceLocation getTargetResourceLocation() {
        return target.getLocation();
    }

    public TextureAtlasSprite getOriginal() {
        return original.get();
    }

    public TextureAtlasSprite getTarget() {
        return target.get();
    }

    public float getTargetU(float localU) {
        return getTarget().getU(getUnInterpolatedU(getOriginal(), localU));
    }

    public float getTargetV(float localV) {
        return getTarget().getV(getUnInterpolatedV(getOriginal(), localV));
    }

    /**
     * @removed <code>* 16.0F</code>
     * @see TextureAtlasSprite#getU(float)
     */
    public static float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
        float f = sprite.getU1() - sprite.getU0();
        return (u - sprite.getU0()) / f;
    }

    /**
     * @removed <code>* 16.0F</code>
     * @see TextureAtlasSprite#getV(float)
     */
    public static float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
        float f = sprite.getV1() - sprite.getV0();
        return (v - sprite.getV0()) / f;
    }
}
