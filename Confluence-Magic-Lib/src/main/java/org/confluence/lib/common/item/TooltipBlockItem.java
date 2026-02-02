package org.confluence.lib.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;

import java.util.Collections;
import java.util.List;

public class TooltipBlockItem extends BlockItem {
    private final List<Component> tooltips;

    public TooltipBlockItem(Block block, Properties properties, ModRarity rarity, List<Component> tooltips) {
        super(block, properties.component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.tooltips = tooltips;
    }

    public TooltipBlockItem(Block block, Properties properties, ModRarity rarity, Component tooltip) {
        this(block, properties, rarity, Collections.singletonList(tooltip));
    }

    public TooltipBlockItem(Block block, Properties properties, ModRarity rarity, String tooltip) {
        this(block, properties, rarity, Collections.singletonList(Component.translatable(tooltip).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(tooltips);
    }
}
