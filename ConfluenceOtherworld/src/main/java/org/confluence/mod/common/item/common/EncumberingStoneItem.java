package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;

public class EncumberingStoneItem extends TooltipItem implements IFunctionCouldEnable {
    public EncumberingStoneItem() {
        super(new Properties().stacksTo(1), ModRarity.BLUE, getTooltipsFromString("encumbering_stone", 3, ChatFormatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof ServerPlayer serverPlayer) {
            ((IServerPlayer) serverPlayer).confluence$setCouldPickupItem(!isEnabled(stack, null));
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        return isEnabled(pStack, null) ? super.getName(pStack) : Component.translatable(getDescriptionId(pStack) + ".disable");
    }
}
