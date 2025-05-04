package com.xiaohunao.xhn_lib.common.events;

import com.xiaohunao.xhn_lib.example.init.ModRegistries;
import com.xiaohunao.xhn_lib.example.quality.ItemQuality;
import com.xiaohunao.xhn_lib.example.quality.QualityLoader;
import com.xiaohunao.xhn_lib.example.quality.QualityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

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


        Set<Map.Entry<ResourceKey<ItemQuality>, ItemQuality>> entries = ModRegistries.ITEM_QUALITIES.entrySet();
        System.out.println(entries);
    }
    
    /**
     * 输出品质信息到日志
     */
    private static void logQuality(String name, ItemQuality quality) {

    }
}
