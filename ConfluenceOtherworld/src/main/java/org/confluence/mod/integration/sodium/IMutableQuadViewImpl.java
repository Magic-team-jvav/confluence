package org.confluence.mod.integration.sodium;

import org.confluence.mod.client.effect.textures.GraySpriteShifterEntry;
import org.jetbrains.annotations.Nullable;

public interface IMutableQuadViewImpl {
    void confluence$color(int vertexIndex, int color);

    @Nullable GraySpriteShifterEntry confluence$getEntry();
}
