package org.confluence.mod.mixed;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.Minecraft;
import org.confluence.lib.util.LibUtils;

public interface ILevelRenderer {
    boolean IS_SODIUM_LOADED = LibUtils.isModLoaded("sodium");

    void confluence$rebuildAllChunks();

    static void scheduleRebuildForChunk(int x, int y, int z) {
        SodiumWorldRenderer.instance().scheduleRebuildForChunk(x, y, z, false);
    }

    static void rebuildAllChunks() {
        ((ILevelRenderer) Minecraft.getInstance().levelRenderer).confluence$rebuildAllChunks();
    }
}
