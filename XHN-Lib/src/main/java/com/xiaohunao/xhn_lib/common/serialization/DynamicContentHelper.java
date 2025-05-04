package com.xiaohunao.xhn_lib.common.serialization;

import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 动态内容帮助类
 * 用于简化动态内容的注册和加载过程
 * 支持资源包和数据包两种加载方式
 */
public class DynamicContentHelper {
    // 存储所有的动态加载器
    private static final List<AbstractDynamicLoader> DYNAMIC_LOADERS = new ArrayList<>();
    
    // 存储所有的数据包加载器
    private static final List<DataPackLoader> DATA_PACK_LOADERS = new ArrayList<>();
    
    // 存储游戏加载后需要执行的任务
    private static final List<Runnable> POST_SETUP_TASKS = new ArrayList<>();
    
    /**
     * 注册一个动态内容加载器
     * 
     * @param loader 要注册的动态内容加载器
     * @param <T> 加载器处理的内容类型
     */
    public static <T> void registerDynamicLoader(AbstractDynamicLoader loader) {
        DYNAMIC_LOADERS.add(loader);
    }
    
    /**
     * 注册一个数据包加载器
     * 
     * @param loader 要注册的数据包加载器
     */
    public static void registerDataPackLoader(DataPackLoader loader) {
        DATA_PACK_LOADERS.add(loader);
    }
    
    /**
     * 注册一个资源重载监听器
     * 
     * @param listener 要注册的资源重载监听器
     */
    public static void registerReloadListener(SimpleJsonResourceReloadListener listener) {
        // 这个方法在服务器端注册资源重载监听器
        Consumer<AddReloadListenerEvent> serverRegistration = event -> event.addListener(listener);
        
        // 在服务器端注册监听器
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(serverRegistration);
    }
    
    // 存储客户端重载监听器
    private static final List<SimpleJsonResourceReloadListener> CLIENT_RELOAD_LISTENERS = new ArrayList<>();
    
    /**
     * 注册一个客户端资源重载监听器
     * 
     * @param listener 要注册的资源重载监听器
     */
    public static void registerClientReloadListener(SimpleJsonResourceReloadListener listener) {
        // 将客户端重载监听器添加到列表中，稍后会注册
        CLIENT_RELOAD_LISTENERS.add(listener);
    }
    
    /**
     * 注册所有客户端重载监听器
     * 这个方法应该在模组总线上注册
     * 
     * @param event 客户端重载监听器注册事件
     */
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        for (SimpleJsonResourceReloadListener listener : CLIENT_RELOAD_LISTENERS) {
            event.registerReloadListener(listener);
        }
    }
    
    /**
     * 注册一个在游戏加载完成后执行的任务
     * 这通常用于在游戏启动后动态注册内容
     * 
     * @param task 要执行的任务
     */
    public static void registerPostSetupTask(Runnable task) {
        POST_SETUP_TASKS.add(task);
    }
    
    /**
     * 执行所有注册的后置任务
     * 这个方法应该在游戏加载完成后被调用
     */
    public static void executePostSetupTasks() {
        for (Runnable task : POST_SETUP_TASKS) {
            task.run();
        }
    }
    
    /**
     * 注册所有动态加载器和数据包加载器
     * 这个方法应该在模组初始化时被调用
     */
    public static void registerAllDynamicLoaders() {
        // 注册资源包加载器
        for (AbstractDynamicLoader loader : DYNAMIC_LOADERS) {
            // 服务器端注册
            registerReloadListener(loader);
            
            // 如果需要在客户端也加载，则添加到客户端监听器列表
            if (loader.shouldRegisterOnClient()) {
                registerClientReloadListener(loader);
            }
        }
        
        // 注册数据包加载器
        for (DataPackLoader loader : DATA_PACK_LOADERS) {
            // 服务器端注册
            registerReloadListener(loader);
            
            // 注册数据包同步事件处理器
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((OnDatapackSyncEvent event) -> {
                loader.onDataPackSync(event);
            });
        }
    }
}
