package org.confluence.mod.integration.ponder;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.fml.ModList;

public class PonderHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("ponder");

    public static void registerPlugin() {
        if (IS_LOADED) {
            try {
                PonderIndex.addPlugin(ModPonderPlugin.class.getDeclaredConstructor().newInstance());
            } catch (Exception ignored) {}
        }
    }
}
