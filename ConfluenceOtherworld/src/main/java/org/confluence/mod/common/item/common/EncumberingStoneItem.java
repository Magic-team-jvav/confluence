package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.FunctionItem;
import org.confluence.mod.mixed.IServerPlayer;

public class EncumberingStoneItem extends FunctionItem {
    public EncumberingStoneItem() {
        super(new Properties().stacksTo(1), ModRarity.BLUE, getTooltipsFromString("encumbering_stone", 3, ChatFormatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof IServerPlayer serverPlayer) {
            serverPlayer.confluence$setCouldPickupItem(!isEnabled(stack));
        }
    }
}
