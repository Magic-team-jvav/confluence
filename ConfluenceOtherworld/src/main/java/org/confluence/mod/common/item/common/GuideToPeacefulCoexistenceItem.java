package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.mixed.IPlayer;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;

public class GuideToPeacefulCoexistenceItem extends TooltipItem implements IFunctionCouldEnable {
    public GuideToPeacefulCoexistenceItem() {
        super(new Properties().stacksTo(1), ModRarity.GREEN, getTooltipsFromString("guide_to_peaceful_coexistence", 3, ChatFormatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof IPlayer player) {
            boolean could = !isEnabled(stack);
            player.confluence$setCouldDamageEnvironment(could);
            if (entity instanceof IServerPlayer serverPlayer) {
                serverPlayer.confluence$setCouldHurtCritter(could);
            }
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        return isEnabled(pStack) ? super.getName(pStack) : Component.translatable(getDescriptionId(pStack) + ".disable");
    }
}
