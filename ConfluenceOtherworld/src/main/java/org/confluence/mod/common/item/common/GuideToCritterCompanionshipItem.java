package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;

public class GuideToCritterCompanionshipItem extends TooltipItem implements IFunctionCouldEnable {
    public GuideToCritterCompanionshipItem() {
        super(new Properties().stacksTo(1), ModRarity.BLUE, getTooltipsFromString("guide_to_critter_companionship", 2, ChatFormatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof IServerPlayer serverPlayer) {
            serverPlayer.confluence$setCouldHurtCritter(!isEnabled(stack));
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        return isEnabled(pStack) ? super.getName(pStack) : Component.translatable(getDescriptionId(pStack) + ".disable");
    }
}
