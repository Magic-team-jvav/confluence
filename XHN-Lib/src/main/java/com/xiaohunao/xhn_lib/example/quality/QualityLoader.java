package com.xiaohunao.xhn_lib.example.quality;

import com.google.gson.JsonElement;
import com.xiaohunao.xhn_lib.common.serialization.AbstractDynamicLoader;
import com.xiaohunao.xhn_lib.common.serialization.DynamicContentType;
import com.xiaohunao.xhn_lib.common.serialization.IDynamicSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 品质加载器
 * 用于从JSON资源加载和注册物品品质
 */
public class QualityLoader extends AbstractDynamicLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(QualityLoader.class);
    
    /**
     * 创建一个新的品质加载器
     */
    public QualityLoader() {
        super("item_qualities");
    }
    
    // 存储加载的品质数据
    private final Map<String, ItemQuality> loadedQualities = new HashMap<>();
    
    // 存储预注册的品质ID
    private final Set<String> preregisteredQualityIds = new HashSet<>();
    
    /**
     * 预注册所有预期的JSON品质
     * 这个方法应该在游戏加载阶段调用
     */
    public void preRegisterQualities() {
        // 预注册已知的JSON品质，使用可变实现以支持动态更新
        QualityRegistry.QUALITIES.registerStatic("ancient", () -> 
            new MutableItemQualityImpl("ancient", "远古", 6, ChatFormatting.DARK_RED, 2.0f)
        );
        
        // 如果有其他预期的JSON品质，也可以在这里预注册
    }
    
    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        LOGGER.info("加载品质数据...");
        
        // 清理之前加载的品质数据
        loadedQualities.clear();
        
        // 处理每个JSON资源
        resources.forEach((resourceLocation, jsonElement) -> {
            try {
                // 解析JSON为品质对象
                IDynamicSerializer<ItemQuality> serializer = new DynamicContentType.Builder<ItemQuality>().codec(ItemQuality.QUALITY_CODEC).build().getSerializer();
                ItemQuality quality = serializer.read(resourceLocation, jsonElement);
                
                if (quality != null) {
                    // 提取品质ID
                    String qualityId = resourceLocation.getPath();
                    LOGGER.info("从JSON加载品质: {}，等级: {}，颜色: {}", quality.getName(), quality.getLevel(), quality.getColor().getName());
                    
                    // 存储品质数据
                    loadedQualities.put(qualityId, quality);
                    
                    // 更新预注册的品质数据
                    // 注意：如果该品质已经静态注册，我们只更新其数据，而不尝试重新注册
                    updateQualityData(qualityId, quality);
                }
            } catch (Exception e) {
                LOGGER.error("加载品质时出错: " + resourceLocation, e);
            }
        });
        
        LOGGER.info("品质数据加载完成，共加载 {} 个品质", loadedQualities.size());
    }
    
    /**
     * 更新预注册的品质数据
     * 这个方法不会注册新的品质，而是更新已经预注册的品质的数据
     * 
     * @param qualityId 品质ID
     * @param quality 新的品质数据
     */
    private void updateQualityData(String qualityId, ItemQuality quality) {
        // 检查该品质是否已经静态注册
        boolean isStaticallyRegistered = QualityRegistry.QUALITIES.getStaticEntry(qualityId).isPresent();
        
        if (isStaticallyRegistered) {
            LOGGER.info("更新预注册的品质数据: {}", qualityId);
            
            // 获取预注册的品质
            ItemQuality registeredQuality = QualityRegistry.getQuality(qualityId);
            
            // 如果ItemQuality类有更新方法，则调用它
            if (registeredQuality instanceof MutableItemQuality) {
                MutableItemQuality mutableQuality = (MutableItemQuality) registeredQuality;
                mutableQuality.updateFrom(quality);
                LOGGER.info("成功更新品质数据: {} (等级: {}, 颜色: {}, 加成: {}%)", 
                        quality.getName(), quality.getLevel(), quality.getColor().getName(), quality.getStatBonus() * 100);
            } else {
                // 如果不支持直接更新，则记录警告
                LOGGER.warn("无法更新品质 {} 的数据，因为它不是可变的品质类型", qualityId);
                
                // 在这里，我们可以实现一个替代方案，例如将更新后的数据存储在一个单独的映射中
                // 然后在需要使用品质数据的地方优先使用这个映射中的数据
                // 但这需要修改品质系统的其他部分，超出了当前范围
            }
        } else {
            LOGGER.warn("无法更新品质 {} 的数据，因为它没有预注册", qualityId);
        }
    }
    
    /**
     * 获取加载的品质数据
     * 
     * @param qualityId 品质ID
     * @return 品质数据，如果不存在则返回null
     */
    public ItemQuality getLoadedQuality(String qualityId) {
        return loadedQualities.get(qualityId);
    }
    
    /**
     * 获取所有加载的品质
     * 
     * @return 所有加载的品质映射
     */
    public Map<String, ItemQuality> getAllLoadedQualities() {
        return Collections.unmodifiableMap(loadedQualities);
    }
    
    @Override
    public boolean shouldRegisterOnClient() {
        // 品质数据在客户端和服务器端都需要
        return true;
    }
}
