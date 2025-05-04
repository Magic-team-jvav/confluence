package com.xiaohunao.xhn_lib;

import com.xiaohunao.xhn_lib.common.events.QualityTestEvents;
import com.xiaohunao.xhn_lib.common.serialization.DynamicContentHelper;
import com.xiaohunao.xhn_lib.example.init.ModRegistries;
import com.xiaohunao.xhn_lib.example.quality.QualityRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * XHN Lib
 * 
 * @author Xiaohunao
 */
@Mod(XHN_Lib.MODID)
public class XHN_Lib {
    public static final String MODID = "xhn_lib";


    public XHN_Lib(IEventBus modEventBus, ModContainer modContainer) {
        // 注册自定义注册表
        modEventBus.addListener(ModRegistries::registerRegistries);
        
        // 初始化品质系统
        QualityRegistry.init(modEventBus);
        
        // 注册测试事件
        NeoForge.EVENT_BUS.register(QualityTestEvents.class);
        
        // 注册通用设置事件
        modEventBus.addListener(this::onCommonSetup);
        
        // 注册客户端重载监听器事件处理器
        modEventBus.addListener(DynamicContentHelper::registerClientReloadListeners);
    }
    
    /**
     * 通用设置事件处理
     * 在这里注册所有动态加载器
     */
    private void onCommonSetup(final FMLCommonSetupEvent event) {
        // 注册所有动态加载器
        event.enqueueWork(DynamicContentHelper::registerAllDynamicLoaders);
        
        // 执行所有后置任务
        event.enqueueWork(DynamicContentHelper::executePostSetupTasks);
    }
    

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static String asDescriptionId(String path) {
        return MODID + "." + path;
    }

    public static <T> ResourceKey<Registry<T>> asResourceKey(String path) {
        return ResourceKey.createRegistryKey(asResource(path));
    }
    public static <T> ResourceKey<T> asResourceKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, asResource(path));
    }
}
