package org.confluence.mod.common.item.fishing;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

import java.util.List;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public class BaitItem extends Item implements IBait {
    private final float bonus;

    public BaitItem(ModRarity rarity, float bonus) {
        super(new Properties().component(TCDataComponentTypes.MOD_RARITY, rarity));
        this.bonus = bonus;
    }

    @Override
    public float getBaitBonus() {
        return bonus;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("info.confluence.bait", ATTRIBUTE_MODIFIER_FORMAT.format(getBaitBonus() * 100.0)).withStyle(style -> style.withColor(ChatFormatting.BLUE)));
    }
}
