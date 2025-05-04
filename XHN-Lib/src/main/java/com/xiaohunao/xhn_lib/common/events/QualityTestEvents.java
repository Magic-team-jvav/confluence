package com.xiaohunao.xhn_lib.common.events;

import com.xiaohunao.xhn_lib.example.quality.ItemQuality;
import com.xiaohunao.xhn_lib.example.quality.QualityLoader;
import com.xiaohunao.xhn_lib.example.quality.QualityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 品质测试事件处理器
 * 用于测试品质系统是否正常工作
 */
public class QualityTestEvents {
    private static final Logger LOGGER = LoggerFactory.getLogger("QualityTest");
    
    /**
     * 处理右键点击方块事件
     * 当玩家右键点击方块时，输出所有已注册的品质信息
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState blockState = level.getBlockState(pos);
        
        // 只在服务器端处理
        if (level.isClientSide()) {
            return;
        }
        
        // 输出方块信息
        LOGGER.info("玩家 {} 右键点击了方块: {} 在位置: {}", 
                player.getName().getString(), 
                blockState.getBlock().getName().getString(), 
                pos);
        
        // 输出注册系统信息
        LOGGER.info("===== 注册系统信息 =====");
        LOGGER.info("注册系统类型: {}", QualityRegistry.QUALITIES.getClass().getSimpleName());
        LOGGER.info("静态条目数量: {}", QualityRegistry.QUALITIES.getStaticEntries().size());
        LOGGER.info("动态条目数量: {}", QualityRegistry.QUALITIES.getDynamicEntries().size());
        LOGGER.info("总条目数量: {}", QualityRegistry.QUALITIES.getStaticEntries().size() + QualityRegistry.QUALITIES.getDynamicEntries().size());
        
        // 输出所有静态注册的品质信息
        LOGGER.info("\n===== 静态注册的品质 =====");
        logQuality("普通", QualityRegistry.COMMON.get());
        logQuality("优秀", QualityRegistry.UNCOMMON.get());
        logQuality("稀有", QualityRegistry.RARE.get());
        logQuality("史诗", QualityRegistry.EPIC.get());
        logQuality("传说", QualityRegistry.LEGENDARY.get());
        
        // 输出静态注册的神圣品质信息
        LOGGER.info("\n===== 静态注册的神圣品质 =====");
        ItemQuality divineQuality = QualityRegistry.getQuality("divine");
        if (divineQuality != null && divineQuality != QualityRegistry.COMMON.get()) {
            logQuality("神圣", divineQuality);
            LOGGER.info("神圣品质注册成功!");
        } else {
            LOGGER.error("神圣品质未成功注册!");
        }
        
        // 输出数据包加载的品质信息
        LOGGER.info("\n===== 数据包加载的品质 =====");
        
        // 测试预注册的远古品质
        ItemQuality ancientQuality = QualityRegistry.getQuality("ancient");
        if (ancientQuality != null && ancientQuality != QualityRegistry.COMMON.get()) {
            logQuality("远古", ancientQuality);
            LOGGER.info("远古品质注册成功!");
            
            // 检查是否为预注册的条目
            boolean isDynamicallyRegistered = QualityRegistry.QUALITIES.isDynamicallyRegistered("ancient");
            boolean isStaticallyRegistered = QualityRegistry.QUALITIES.getStaticEntry("ancient").isPresent();
            LOGGER.info("远古品质是否为动态注册: {}", isDynamicallyRegistered);
            LOGGER.info("远古品质是否为静态预注册: {}", isStaticallyRegistered);
            LOGGER.info("注意: 远古品质是静态预注册的，但其数据来自数据包");
        } else {
            LOGGER.error("远古品质未成功注册!");
        }
        
        // 检查品质加载器中的数据
        if (QualityRegistry.getQualityLoader() != null) {
            QualityLoader loader = QualityRegistry.getQualityLoader();
            Map<String, ItemQuality> loadedQualities = loader.getAllLoadedQualities();
            
            LOGGER.info("\n===== 品质加载器信息 =====");
            LOGGER.info("加载器类型: {}", loader.getClass().getSimpleName());
            LOGGER.info("已加载品质数量: {}", loadedQualities.size());
            
            // 输出所有加载的品质
            for (Map.Entry<String, ItemQuality> entry : loadedQualities.entrySet()) {
                LOGGER.info("\n加载的品质 ID: {}", entry.getKey());
                logQuality(entry.getValue().getName(), entry.getValue());
            }
        } else {
            LOGGER.error("品质加载器未初始化!");
        }
        
        // 向玩家发送消息
        player.sendSystemMessage(Component.literal("品质系统测试已完成，请查看服务器日志！").withStyle(ChatFormatting.GREEN));
        player.sendSystemMessage(Component.literal("数据包加载系统测试已完成！").withStyle(ChatFormatting.GOLD));
    }
    
    /**
     * 输出品质信息到日志
     */
    private static void logQuality(String name, ItemQuality quality) {
        LOGGER.info("{} 品质: ID={}, 名称={}, 等级={}, 颜色={}, 加成={}%", 
                name,
                quality.getId(), 
                quality.getName(), 
                quality.getLevel(), 
                quality.getColor().getName(), 
                quality.getStatBonus() * 100);
    }
}
