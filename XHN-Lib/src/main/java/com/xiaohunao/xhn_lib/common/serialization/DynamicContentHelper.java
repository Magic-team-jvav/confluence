package com.xiaohunao.xhn_lib.common.serialization;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DynamicContentHelper {
    private static final List<AbstractDynamicLoader> DYNAMIC_LOADERS = new ArrayList<>();


    public static <T> void registerDynamicLoader(AbstractDynamicLoader loader) {
        DYNAMIC_LOADERS.add(loader);
    }


    /**
     * 注册所有动态加载器和数据包加载器
     * 这个方法应该在模组初始化时被调用
     */
    public static void registerAllDynamicLoaders() {
        for (AbstractDynamicLoader loader : DYNAMIC_LOADERS) {
            Consumer<AddReloadListenerEvent> serverRegistration = event -> {
                event.addListener(loader);
            };

            NeoForge.EVENT_BUS.addListener(serverRegistration);
        }

    }
}
