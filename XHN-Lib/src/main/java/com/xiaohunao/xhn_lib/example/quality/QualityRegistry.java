package com.xiaohunao.xhn_lib.example.quality;

import com.xiaohunao.xhn_lib.XHN_Lib;
import com.xiaohunao.xhn_lib.common.init.FlexibleRegister;
import com.xiaohunao.xhn_lib.common.serialization.DynamicContentHelper;
import com.xiaohunao.xhn_lib.example.init.ModRegistries;
import net.minecraft.ChatFormatting;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;

/**
 * 品质注册表类
 * 管理所有物品品质的注册
 */
public class QualityRegistry {
    // 使用ModRegistries中定义的注册表键
    public static final FlexibleRegister<ItemQuality> QUALITIES = new FlexibleRegister<>(ModRegistries.ITEM_QUALITIES, XHN_Lib.MODID);

    // 静态注册的基础品质
    public static final DeferredHolder<ItemQuality, ItemQuality> COMMON =
            QUALITIES.registerStatic("common", () ->
                    new ItemQuality("common", "普通", 0, ChatFormatting.GRAY, 0.0f));

    public static final DeferredHolder<ItemQuality, ItemQuality> UNCOMMON =
            QUALITIES.registerStatic("uncommon", () ->
                    new ItemQuality("uncommon", "优秀", 1, ChatFormatting.GREEN, 0.1f));

    public static final DeferredHolder<ItemQuality, ItemQuality> RARE =
            QUALITIES.registerStatic("rare", () ->
                    new ItemQuality("rare", "稀有", 2, ChatFormatting.BLUE, 0.25f));

    public static final DeferredHolder<ItemQuality, ItemQuality> EPIC =
            QUALITIES.registerStatic("epic", () ->
                    new ItemQuality("epic", "史诗", 3, ChatFormatting.DARK_PURPLE, 0.5f));

    public static final DeferredHolder<ItemQuality, ItemQuality> LEGENDARY =
            QUALITIES.registerStatic("legendary", () ->
                    new ItemQuality("legendary", "传说", 4, ChatFormatting.GOLD, 1.0f));

    // 存储品质加载器实例
    private static final QualityLoader qualityLoader = new QualityLoader();
    
    /**
     * 初始化品质注册表
     * 
     * @param eventBus NeoForge事件总线
     */
    public static void init(IEventBus eventBus) {
        // 注册静态品质
        QUALITIES.register(eventBus);
        
        // 设置动态加载器
        QUALITIES.setDynamicManager(qualityLoader);
        
        // 注册动态内容加载器
        DynamicContentHelper.<ItemQuality>registerDynamicLoader(qualityLoader);
    }
    
    /**
     * 获取品质加载器
     * 
     * @return 品质加载器实例
     */
    public static QualityLoader getQualityLoader() {
        return qualityLoader;
    }
    
    /**
     * 根据ID获取品质
     * 
     * @param id 品质ID
     * @return 品质对象，如果不存在则返回普通品质
     */
    public static ItemQuality getQuality(String id) {
        // 先检查动态注册的品质
        if (QUALITIES.isDynamicallyRegistered(id)) {
            Optional<FlexibleRegister.FlexibleHolder<ItemQuality, ItemQuality>> holder = QUALITIES.getDynamicEntry(id);
            if (holder.isPresent()) {
                return holder.get().get();
            }
        }
        
        // 然后检查静态注册的品质
        Optional<FlexibleRegister.FlexibleHolder<ItemQuality, ItemQuality>> staticHolder = QUALITIES.getStaticEntry(id);
        if (staticHolder.isPresent()) {
            return staticHolder.get().get();
        }
        
        // 如果没有找到，根据ID返回特定品质或默认品质
        switch (id) {
            case "uncommon":
                return UNCOMMON.get();
            case "rare":
                return RARE.get();
            case "epic":
                return EPIC.get();
            case "legendary":
                return LEGENDARY.get();
            case "divine":
            case "ancient":
            case "common":
            default:
                return COMMON.get();
        }
    }
}
