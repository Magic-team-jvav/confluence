package com.xiaohunao.xhn_lib.common.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.xiaohunao.xhn_lib.common.init.FlexibleRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DynamicSerializerLoader<T> extends AbstractDynamicLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicSerializerLoader.class);

    protected final IDynamicSerializer<T> serializer;


    public DynamicSerializerLoader(IDynamicSerializer<T> serializer, String folderName) {
        super(folderName);
        this.serializer = serializer;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> resources,
                         @NotNull ResourceManager resourceManager,
                         @NotNull ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation location = entry.getKey();
            String path = location.getPath();
            String registryName = path.substring(path.lastIndexOf('/') + 1);

            try {
                JsonElement jsonElement = entry.getValue();

                T content = serializer.read(location, jsonElement);
                if (content == null) {
                    continue;
                }

            } catch (Exception ignored) {
                LOGGER.error("Failed to load {}", location, ignored);
            }
        }
    }

    /**
     * 获取此加载器的内容序列化器
     *
     * @return 内容序列化器
     */
    public IDynamicSerializer<T> getSerializer() {
        return serializer;
    }
}
