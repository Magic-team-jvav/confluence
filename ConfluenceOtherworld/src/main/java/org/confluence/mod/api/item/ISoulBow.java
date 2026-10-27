package org.confluence.mod.api.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/// 魂师弓的标记接口：击杀额外收魂、灵魂足够时增伤都由 {@link org.confluence.mod.common.event.game.entity.LivingEntityEvents} 统一结算。
///
/// 新增灵魂弓时只需让物品类实现本接口，无需再改事件。
public interface ISoulBow {
    static boolean isSoulBow(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ISoulBow;
    }

    /// 灵魂弓系列的说明行（两把弓效果完全一致，只是物品名不同）。
    ///
    /// 翻译键按物品各自登记：`tooltip.<物品翻译键>.0/1/2`。
    static void appendSoulBowTooltips(ItemStack stack, List<Component> tooltip) {
        String key = "tooltip." + stack.getItem().getDescriptionId();
        tooltip.add(Component.translatable(key + ".0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(key + ".1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(key + ".2").withStyle(ChatFormatting.GRAY));
    }
}
