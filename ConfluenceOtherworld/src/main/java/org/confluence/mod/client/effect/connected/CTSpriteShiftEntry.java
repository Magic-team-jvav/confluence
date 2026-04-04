package org.confluence.mod.client.effect.connected;


import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class CTSpriteShiftEntry extends SpriteShiftEntry {
    protected final CTType type;

    public CTSpriteShiftEntry(CTType type) {
        this.type = type;
    }

    public CTType getType() {
        return type;
    }

    /// @replaced <code>(localU) * 16</code> to <code>localU</code>
    /// @see TextureAtlasSprite#getU(float)
    public float getTargetU(float localU, int indicesIndex, int targetIndex) {
        float uOffset = (float) (indicesIndex % type.getSheetSize());
        return getTarget(targetIndex).getU((getUnInterpolatedU(getOriginal(), localU) + uOffset) / ((float) type.getSheetSize()));
    }

    /// @replaced <code>(localV) * 16</code> to <code>localV</code>
    /// @see TextureAtlasSprite#getV(float)
    public float getTargetV(float localV, int indicesIndex, int targetIndex) {
        float vOffset = (float) (indicesIndex / type.getSheetSize());
        return getTarget(targetIndex).getV((getUnInterpolatedV(getOriginal(), localV) + vOffset) / ((float) type.getSheetSize()));
    }
}
