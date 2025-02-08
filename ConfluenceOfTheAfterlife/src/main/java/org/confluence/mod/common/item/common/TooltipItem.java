package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.ArrayList;
import java.util.List;

public class TooltipItem extends CustomRarityItem {
    private final List<Component> tooltips;

    public TooltipItem(Properties properties, ModRarity rarity, List<Component> tooltips) {
        super(properties, rarity);
        this.tooltips = tooltips;
    }

    public TooltipItem(Properties properties, ModRarity rarity, Component tooltip) {
        this(properties, rarity, List.of(tooltip));
    }

    public TooltipItem(Properties properties, ModRarity rarity, String tooltip) {
        this(properties, rarity, List.of(Component.translatable(tooltip).withStyle(ChatFormatting.DARK_GRAY)));
    }

    public List<Component> getTooltips() {
        return tooltips;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(tooltips);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static List<Component> getTooltipsFromString(String id, int lineCount){
        List<Component> components = new ArrayList<>();
        for (int i = 1; i <= lineCount; i++){
            components.add(Component.translatable("item.confluence." + id + ".tooltip." + i).withStyle(ChatFormatting.DARK_GRAY));
        }
        return components;
    }
}
