package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.mixed.IPlayer;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;

public class GuideToEnvironmentalPreservationItem extends TooltipItem implements IFunctionCouldEnable {
    public GuideToEnvironmentalPreservationItem() {
        super(new Properties().stacksTo(1), ModRarity.BLUE, getTooltipsFromString("guide_to_environmental_preservation", 2, ChatFormatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof IPlayer player) {
            player.confluence$setCouldDamageEnvironment(!isEnabled(stack));
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        return isEnabled(pStack) ? super.getName(pStack) : Component.translatable(getDescriptionId(pStack) + ".disable");
    }
}
