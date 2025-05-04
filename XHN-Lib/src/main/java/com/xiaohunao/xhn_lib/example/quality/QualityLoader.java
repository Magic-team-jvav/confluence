package com.xiaohunao.xhn_lib.example.quality;

import com.google.gson.JsonElement;
import com.xiaohunao.xhn_lib.common.serialization.AbstractDynamicLoader;
import com.xiaohunao.xhn_lib.common.serialization.DynamicContentType;
import com.xiaohunao.xhn_lib.common.serialization.IDynamicSerializer;
import com.xiaohunao.xhn_lib.example.init.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 品质加载器
 * 用于从JSON资源加载和注册物品品质，支持动态加载和卸载
 */
public class QualityLoader extends AbstractDynamicLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(QualityLoader.class);
    
    // 存储当前加载的品质
    private final Map<ResourceLocation, ItemQuality> loadedQualities = new HashMap<>();
    
    // 跟踪当前加载周期中移除的品质
    private final Set<ResourceLocation> removedQualities = new HashSet<>();
    
    // 序列化器，用于从JSON读取品质
    private final IDynamicSerializer<ItemQuality> serializer;
    
    /**
     * 创建一个新的品质加载器
     */
    public QualityLoader() {
        super("item_qualities");
        this.serializer = new DynamicContentType.Builder<ItemQuality>()
                .codec(ItemQuality.QUALITY_CODEC)
                .build()
                .getSerializer();
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        LOGGER.info("开始加载品质资源，共 {} 个资源", resources.size());
        
        // 保存先前加载的品质，用于检测需要移除的品质
        Set<ResourceLocation> previousQualities = new HashSet<>(loadedQualities.keySet());
        
        // 清除当前加载的品质和移除记录，准备重新加载
        loadedQualities.clear();
        removedQualities.clear();
        
        // 获取注册表并解冻
        MappedRegistry<ItemQuality> registry = (MappedRegistry<ItemQuality>) ModRegistries.ITEM_QUALITIES;
        
        // 注意：unfreeze方法已弃用，但在当前版本中仍需使用
        registry.unfreeze();
        
        try {
            // 创建当前资源位置的集合，用于检测删除的资源
            Set<ResourceLocation> currentResourceLocations = new HashSet<>(resources.keySet());
            
            // 加载新的品质
            loadNewQualities(resources, registry);
            
            // 移除不再存在的品质
            removeObsoleteQualities(previousQualities, currentResourceLocations, registry);
            
            LOGGER.info("品质加载完成，当前共有 {} 个品质", loadedQualities.size());
        } catch (Exception e) {
            LOGGER.error("加载品质时发生错误", e);
        } finally {
            // 确保注册表在处理完成后重新冻结
            registry.freeze();
        }
    }
    
    /**
     * 加载新的品质
     * 
     * @param resources 资源映射
     * @param registry 品质注册表
     */
    private void loadNewQualities(Map<ResourceLocation, JsonElement> resources, Registry<ItemQuality> registry) {
        resources.forEach((resourceLocation, jsonElement) -> {
            try {
                ItemQuality quality = serializer.read(resourceLocation, jsonElement);
                
                if (quality != null) {
                    // 注册到游戏注册表
                    Registry.register(registry, resourceLocation, quality);
                    
                    // 存储在本地映射中
                    loadedQualities.put(resourceLocation, quality);
                    
                    // 通知FlexibleRegister系统
                    String path = resourceLocation.getPath();
                    QualityRegistry.QUALITIES.registerDynamic(path, quality);
                    
                    LOGGER.debug("成功加载品质: {}", resourceLocation);
                }
            } catch (Exception e) {
                LOGGER.error("加载品质时出错: {}", resourceLocation, e);
            }
        });
    }
    
    /**
     * 移除不再存在的品质
     * 
     * @param previousQualities 先前加载的品质
     * @param currentResourceLocations 当前资源位置
     * @param registry 品质注册表
     */
    private void removeObsoleteQualities(Set<ResourceLocation> previousQualities, 
                                         Set<ResourceLocation> currentResourceLocations,
                                         MappedRegistry<ItemQuality> registry) {
        previousQualities.stream()
                .filter(id -> !currentResourceLocations.contains(id))
                .forEach(id -> {
                    // 记录被移除的品质
                    removedQualities.add(id);
                    
                    // 从Registry中取消注册
                    if (unregisterFromRegistry(registry, id)) {
                        // 从FlexibleRegister中移除
                        QualityRegistry.QUALITIES.removeDynamicEntry(id.getPath());
                        
                        LOGGER.info("移除不再存在的品质: {}", id);
                    }
                });
    }
    
    /**
     * 从Registry中取消注册一个品质
     * 
     * @param registry 注册表
     * @param id 品质ID
     * @return 如果成功取消注册则返回true
     */
    @SuppressWarnings("unchecked")
    private boolean unregisterFromRegistry(MappedRegistry<ItemQuality> registry, ResourceLocation id) {
        try {
            // 获取要移除的品质
            ItemQuality quality = registry.get(id);
            if (quality == null) {
                LOGGER.warn("尝试取消注册不存在的品质: {}", id);
                return false;
            }
            
            // 创建ResourceKey
            ResourceKey<ItemQuality> resourceKey = ResourceKey.create(registry.key(), id);
            
            // 移除各种映射中的条目
            boolean success = true;
            success &= removeFromMap(registry, "byKey", resourceKey);
            success &= removeFromMap(registry, "byLocation", id);
            success &= removeFromMap(registry, "byValue", quality);
            success &= removeFromMap(registry, "toId", quality);
            success &= removeFromMap(registry, "registrationInfos", resourceKey);
            success &= removeFromList(registry, "byId", resourceKey);
            
            return success;
        } catch (Exception e) {
            LOGGER.error("从注册表移除品质时出错: {}", id, e);
            return false;
        }
    }
    
    /**
     * 从映射中移除条目
     * 
     * @param registry 注册表
     * @param fieldName 字段名
     * @param key 键
     * @return 如果成功移除则返回true
     */
    @SuppressWarnings("unchecked")
    private boolean removeFromMap(MappedRegistry<ItemQuality> registry, String fieldName, Object key) {
        try {
            Field field = getField(MappedRegistry.class, fieldName);
            Map<Object, Object> map = (Map<Object, Object>) field.get(registry);
            Object removed = map.remove(key);
            
            if (removed != null) {
                LOGGER.debug("成功从{}中移除品质: {}", fieldName, key);
                return true;
            } else {
                LOGGER.warn("未能从{}中移除品质: {}", fieldName, key);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("从{}移除品质时出错: {}", fieldName, key, e);
            return false;
        }
    }
    
    /**
     * 从列表中移除条目
     * 
     * @param registry 注册表
     * @param fieldName 字段名
     * @param resourceKey 资源键
     * @return 如果成功移除则返回true
     */
    @SuppressWarnings("unchecked")
    private boolean removeFromList(MappedRegistry<ItemQuality> registry, String fieldName, ResourceKey<ItemQuality> resourceKey) {
        try {
            Field field = getField(MappedRegistry.class, fieldName);
            Object listObj = field.get(registry);
            
            if (!(listObj instanceof List)) {
                LOGGER.warn("{}字段不是List类型，无法移除品质引用: {}", fieldName, resourceKey);
                return false;
            }
            
            List<?> list = (List<?>) listObj;
            
            // 首先尝试使用标准List接口移除元素
            boolean removed = list.removeIf(item -> {
                if (item instanceof Holder.Reference) {
                    Holder.Reference<?> ref = (Holder.Reference<?>) item;
                    try {
                        return ref.key().equals(resourceKey);
                    } catch (Exception e) {
                        return false;
                    }
                }
                return false;
            });
            
            if (removed) {
                LOGGER.debug("成功从{}列表中移除品质引用: {}", fieldName, resourceKey);
                return true;
            }
            LOGGER.warn("未能在{}中找到品质引用: {}", fieldName, resourceKey);
            return false;
        } catch (Exception e) {
            LOGGER.error("从{}列表移除品质时出错: {}", fieldName, resourceKey, e);
            return false;
        }
    }
    
    /**
     * 获取类的字段并设置为可访问
     */
    private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
    
    /**
     * 获取当前加载周期中已加载的品质
     * 
     * @return 已加载品质的映射
     */
    public Map<ResourceLocation, ItemQuality> getLoadedQualities() {
        return Collections.unmodifiableMap(loadedQualities);
    }
    
    /**
     * 获取当前加载周期中被移除的品质ID
     * 
     * @return 被移除品质ID的集合
     */
    public Set<ResourceLocation> getRemovedQualities() {
        return Collections.unmodifiableSet(removedQualities);
    }
    
    /**
     * 检查品质是否已加载
     * 
     * @param id 品质ID
     * @return 如果品质已加载则返回true
     */
    public boolean isQualityLoaded(ResourceLocation id) {
        return loadedQualities.containsKey(id);
    }
}
