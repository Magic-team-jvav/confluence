package com.xiaohunao.xhn_lib.common.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 数据包加载器
 * 用于从数据包加载JSON数据并处理
 */
public abstract class DataPackLoader extends SimpleJsonResourceReloadListener {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DataPackLoader.class);
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    // 存储加载的数据
    protected final Map<ResourceLocation, JsonElement> loadedData = new HashMap<>();
    
    // 数据处理回调
    private Consumer<Map<ResourceLocation, JsonElement>> dataProcessor;
    
    /**
     * 创建一个新的数据包加载器
     *
     * @param folderName 数据包文件夹名称
     */
    public DataPackLoader(String folderName) {
        super(GSON, folderName);
    }
    
    /**
     * 设置数据处理回调
     * 
     * @param processor 数据处理回调
     */
    public void setDataProcessor(Consumer<Map<ResourceLocation, JsonElement>> processor) {
        this.dataProcessor = processor;
    }
    
    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> data, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        LOGGER.info("加载数据包数据: {}", this.getClass().getSimpleName());
        
        // 清理之前加载的数据
        loadedData.clear();
        loadedData.putAll(data);
        
        // 处理加载的数据
        if (dataProcessor != null) {
            dataProcessor.accept(data);
        }
        
        LOGGER.info("数据包数据加载完成，共加载 {} 个条目", data.size());
    }
    
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
     * 获取加载的数据
     *
     * @return 加载的数据
     */
    public Map<ResourceLocation, JsonElement> getLoadedData() {
        return loadedData;
    }
}
