package org.confluence.mod.integration.ageratum;

import net.neoforged.bus.api.IEventBus;
import org.confluence.lib.util.LibUtils;

public class AgeratumHelper {
    public static final boolean IS_LOADED = LibUtils.isModLoaded("ageratum");

    public static void register(IEventBus eventBus) {
        if (IS_LOADED) {
            AllAgeratum.register(eventBus);
        }
    }
}
