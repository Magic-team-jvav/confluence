package com.xiaohunao.xhn_lib.common.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 抽象数据包加载器
 * 用于从数据包加载JSON数据并处理
 */
public abstract class AbstractDataPackLoader {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataPackLoader.class);
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    // 数据包文件夹路径
    protected final String folderName;
    
    // 存储加载的数据
    protected final Map<ResourceLocation, JsonElement> loadedData = new HashMap<>();
    
    /**
     * 创建一个新的数据包加载器
     *
     * @param folderName 数据包文件夹名称
     */
    public AbstractDataPackLoader(String folderName) {
        this.folderName = folderName;
    }
    
    /**
     * 加载数据包中的JSON数据
     *
     * @param resourceManager 资源管理器
     * @param executor 执行器
     * @return 完成的未来任务
     */
    public CompletableFuture<Void> loadData(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, JsonElement> map = new HashMap<>();
            
            // 获取指定文件夹中的所有JSON文件
            Map<ResourceLocation, JsonElement> resources = DataPackHelper.loadJsonResources(resourceManager, folderName);
            
            // 处理每个JSON资源
            for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
                ResourceLocation resourceLocation = entry.getKey();
                JsonElement jsonElement = entry.getValue();
                
                try {
                    // 添加到加载的数据中
                    map.put(resourceLocation, jsonElement);
                } catch (Exception e) {
                    LOGGER.error("Error parsing datapack JSON " + resourceLocation, e);
                }
            }
            
            return map;
        }, executor).thenAcceptAsync(map -> {
            this.loadedData.clear();
            this.loadedData.putAll(map);
            
            // 处理加载的数据
            this.processLoadedData(map);
            
            LOGGER.info("Loaded {} {} from datapacks", map.size(), this.folderName);
        }, executor);
    }
    
    /**
     * 处理加载的数据
     * 子类需要实现此方法来处理特定类型的数据
     *
     * @param loadedData 加载的数据
     */
    protected abstract void processLoadedData(Map<ResourceLocation, JsonElement> loadedData);
    
    /**
     * 当数据包同步时调用
     * 用于将数据从服务器同步到客户端
     *
     * @param event 数据包同步事件
     */
    public void onDataPackSync(OnDatapackSyncEvent event) {
        // 默认实现为空，子类可以根据需要覆盖此方法
    }
    
    /**
     * 获取此加载器的文件夹名称
     *
     * @return 文件夹名称
     */
    public String getFolderName() {
        return this.folderName;
    }
}
