package org.confluence.lib.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;

import java.util.List;

public class FunctionItem extends TooltipItem implements IFunctionCouldEnable {
    public FunctionItem(Properties properties, ModRarity rarity, List<Component> tooltips) {
        super(properties, rarity, tooltips);
    }

    public FunctionItem(Properties properties, ModRarity rarity, Component tooltip) {
        super(properties, rarity, tooltip);
    }

    public FunctionItem(Properties properties, ModRarity rarity, String tooltip) {
        super(properties, rarity, tooltip);
    }

    @Override
    public Component getName(ItemStack stack) {
        return isEnabled(stack) ? super.getName(stack) : Component.translatable(getDescriptionId(stack) + ".disable");
    }
}
