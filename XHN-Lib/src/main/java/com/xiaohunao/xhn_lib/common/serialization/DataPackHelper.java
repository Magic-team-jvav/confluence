package com.xiaohunao.xhn_lib.common.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据包辅助类
 * 提供加载和处理数据包资源的工具方法
 */
public class DataPackHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataPackHelper.class);
    
    /**
     * 从资源管理器加载指定文件夹下的所有JSON资源
     *
     * @param resourceManager 资源管理器
     * @param folderName 文件夹名称
     * @return 资源位置到JSON元素的映射
     */
    public static Map<ResourceLocation, JsonElement> loadJsonResources(ResourceManager resourceManager, String folderName) {
        Map<ResourceLocation, JsonElement> result = new HashMap<>();
        
        // 获取指定文件夹下的所有资源
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(folderName, location -> location.getPath().endsWith(".json"));
        
        // 处理每个资源
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            Resource resource = entry.getValue();
            
            try (InputStream inputStream = resource.open();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                
                // 解析JSON
                JsonElement jsonElement = JsonParser.parseReader(reader);
                
                // 创建一个干净的资源位置（去掉文件夹前缀和.json后缀）
                String path = resourceLocation.getPath();
                path = path.substring(folderName.length() + 1, path.length() - 5); // +1 for the slash, -5 for .json
                // 使用ResourceLocation.parse方法，这在Minecraft 1.21.1中支持
                ResourceLocation cleanLocation = ResourceLocation.parse(resourceLocation.getNamespace() + ":" + path);
                
                // 添加到结果中
                result.put(cleanLocation, jsonElement);
            } catch (IOException e) {
                LOGGER.error("Error loading JSON resource: " + resourceLocation, e);
            }
        }
        
        return result;
    }
    
    /**
     * 从资源管理器加载单个JSON资源
     *
     * @param resourceManager 资源管理器
     * @param location 资源位置
     * @return JSON元素，如果加载失败则返回null
     */
    public static JsonElement loadJsonResource(ResourceManager resourceManager, ResourceLocation location) {
        try {
            Resource resource = resourceManager.getResource(location).orElse(null);
            if (resource == null) {
                return null;
            }
            
            try (InputStream inputStream = resource.open();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                
                // 解析JSON
                return JsonParser.parseReader(reader);
            }
        } catch (IOException e) {
            LOGGER.error("Error loading JSON resource: " + location, e);
            return null;
        }
    }
}
