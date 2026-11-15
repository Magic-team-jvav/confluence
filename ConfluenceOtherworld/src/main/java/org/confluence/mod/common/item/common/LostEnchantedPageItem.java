package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;

import java.util.List;

/// 蕴魔书页：散落的书页，用来给星体研究汇编装订。
///
/// 本身没有右键功能，装订靠星体研究汇编的 `overrideOtherStackedOnMe`：
/// 在物品栏里左键拾起书页，再左键点击汇编即可。
public class LostEnchantedPageItem extends CustomRarityItem {
    public LostEnchantedPageItem(ModRarity rarity) {
        super(rarity);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.lost_enchanted_page.0").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.lost_enchanted_page.1").withStyle(ChatFormatting.DARK_GRAY));
    }
}
