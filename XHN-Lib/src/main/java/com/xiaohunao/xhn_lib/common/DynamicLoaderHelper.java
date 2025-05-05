package com.xiaohunao.xhn_lib.common;

import com.xiaohunao.xhn_lib.api.AbstractDynamicLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DynamicLoaderHelper {
    private static final List<AbstractDynamicLoader<?>> DYNAMIC_LOADERS = new ArrayList<>();


    public static <T> void registerDynamicLoader(AbstractDynamicLoader<?> loader) {
        DYNAMIC_LOADERS.add(loader);
    }

    public static void registerAllDynamicLoaders() {
        for (AbstractDynamicLoader<?> loader : DYNAMIC_LOADERS) {
            Consumer<AddReloadListenerEvent> serverRegistration = event -> {
                event.addListener(loader);
            };

            NeoForge.EVENT_BUS.addListener(serverRegistration);
        }

    }
}
