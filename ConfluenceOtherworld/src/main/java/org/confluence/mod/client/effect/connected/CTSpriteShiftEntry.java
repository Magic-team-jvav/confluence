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

    /**
     * @replaced <code>(localU) * 16</code> to <code>localU</code>
     * @see TextureAtlasSprite#getU(float)
     */
    public float getTargetU(float localU, int index) {
        float uOffset = (float) (index % type.getSheetSize());
        return getTarget().getU((getUnInterpolatedU(getOriginal(), localU) + uOffset) / ((float) type.getSheetSize()));
    }

    /**
     * @replaced <code>(localV) * 16</code> to <code>localV</code>
     * @see TextureAtlasSprite#getV(float)
     */
    public float getTargetV(float localV, int index) {
        float vOffset = (float) (index / type.getSheetSize());
        return getTarget().getV((getUnInterpolatedV(getOriginal(), localV) + vOffset) / ((float) type.getSheetSize()));
    }

    // Confluence Custom Start
    public float getSelectedTargetU(float localU, int index, int selected, int width) {
        int sheetSize = type.getSheetSize();
        int total = sheetSize * width;
        float uOffset = (index % sheetSize) + (total - sheetSize) * selected;
        return getTarget().getU((getUnInterpolatedU(getOriginal(), localU) + uOffset) / total);
    }
}
