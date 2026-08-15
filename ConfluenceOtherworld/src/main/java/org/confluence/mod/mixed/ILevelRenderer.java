package org.confluence.mod.mixed;

import net.minecraft.client.Minecraft;
import org.confluence.lib.util.LibUtils;

public interface ILevelRenderer {
    boolean IS_SODIUM_LOADED = LibUtils.isModLoaded("sodium");

    void confluence$rebuildAllChunks();

// Sodium 专用的单区块重建入口暂不启用；当前渲染刷新统一走全区块重建，避免跨渲染器分支产生不一致。
//    static void scheduleRebuildForChunk(int x, int y, int z) {
//        SodiumWorldRenderer.instance().scheduleRebuildForChunk(x, y, z, false);
//    }

    static void rebuildAllChunks() {
        ((ILevelRenderer) Minecraft.getInstance().levelRenderer).confluence$rebuildAllChunks();
    }
}
