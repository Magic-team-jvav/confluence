package org.confluence.lib.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.lib.common.component.ModRarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TooltipItem extends CustomRarityItem {
    private final List<Component> tooltips;

    public TooltipItem(Properties properties, ModRarity rarity, List<Component> tooltips) {
        super(properties, rarity);
        this.tooltips = tooltips;
    }

    public TooltipItem(Properties properties, ModRarity rarity, Component tooltip) {
        this(properties, rarity, Collections.singletonList(tooltip));
    }

    public TooltipItem(Properties properties, ModRarity rarity, String tooltip) {
        this(properties, rarity, Collections.singletonList(Component.translatable(tooltip).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(tooltips);
    }

    public static List<Component> getTooltipsFromString(String id, int lineCount, ChatFormatting chatFormatting) {
        if (lineCount == 1) {
            return Collections.singletonList(Component.translatable("tooltip.item.confluence." + id + ".0").withStyle(chatFormatting));
        }
        List<Component> components = new ArrayList<>();
        for (int i = 0; i < lineCount; i++) {
            components.add(Component.translatable("tooltip.item.confluence." + id + "." + i).withStyle(chatFormatting));
        }
        return components;
    }
}
