package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TooltipItem extends Item {

    private final List<Component> tooltips;

    public TooltipItem(Properties properties, List<Component> tooltips) {
        super(properties);
        this.tooltips = tooltips;
    }

    public TooltipItem(Properties properties, Component tooltip) {
        this(properties, List.of(tooltip));
    }

    public TooltipItem(Properties properties, String tooltip) {
        this(properties, List.of(Component.translatable(tooltip).withStyle(ChatFormatting.DARK_GRAY)));
    }

    public List<Component> getTooltips() {
        return tooltips;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(tooltips);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
