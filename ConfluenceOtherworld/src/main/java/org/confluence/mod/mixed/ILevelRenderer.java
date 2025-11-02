package org.confluence.mod.mixed;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;

public interface ILevelRenderer {
    boolean IS_SODIUM_LOADED = ModList.get().isLoaded("sodium");

    void confluence$rebuildAllChunks();

    static void scheduleRebuildForChunk(int x, int y, int z) {
        SodiumWorldRenderer.instance().scheduleRebuildForChunk(x, y, z, false);
    }

    static void rebuildAllChunks() {
        ((ILevelRenderer) Minecraft.getInstance().levelRenderer).confluence$rebuildAllChunks();
    }
}
