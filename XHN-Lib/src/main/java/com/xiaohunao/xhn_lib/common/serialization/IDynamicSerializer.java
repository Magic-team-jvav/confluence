package com.xiaohunao.xhn_lib.common.serialization;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * 动态内容序列化器接口
 * 用于将动态内容序列化为JSON或从 JSON 反序列化
 * 
 * @param <T> 内容类型
 */
public interface IDynamicSerializer<T> {
    /**
     * 从 JSON 读取内容
     * 
     * @param id 内容的资源位置
     * @param json JSON 元素
     * @return 内容实例，如果解析失败则返回null
     */
    @Nullable
    T read(ResourceLocation id, JsonElement json);
    
    /**
     * 将内容写入 JSON
     * 
     * @param content 要序列化的内容
     * @return JSON 元素，如果序列化失败则返回null
     */
    @Nullable
    JsonElement write(T content);

}
