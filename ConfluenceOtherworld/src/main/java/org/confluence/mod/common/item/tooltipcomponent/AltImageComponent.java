package org.confluence.mod.common.item.tooltipcomponent;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record AltImageComponent(ItemStack stack) implements TooltipComponent {
    public static AltImageComponent of(Item item) {
        return new AltImageComponent(item.getDefaultInstance());
    }
}
