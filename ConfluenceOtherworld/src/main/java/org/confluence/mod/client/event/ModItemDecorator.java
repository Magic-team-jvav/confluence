package org.confluence.mod.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.IItemDecorator;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.mixed.IPlayer;
import org.confluence.mod.util.ClientUtils;
import org.confluence.mod.util.RepeaterContentsComponentHandler;

import java.util.Iterator;

public final class ModItemDecorator {
    static final IItemDecorator FISHING_POLE_DECORATOR = (guiGraphics, font, itemStack, x, y) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getInventory().getSelected() == itemStack) {
            ItemStack bait = IPlayer.of(player).confluence$getCurrentBait();
            if (!bait.isEmpty()) {
                ClientUtils.renderBait(guiGraphics, bait, x, y);
            }
        }
        return false;
    };

    static final IItemDecorator REPEATER_AMMO = (guiGraphics, font, itemStack, x, y) -> {
        if (itemStack.getItem() instanceof BaseTerraRepeaterItem && itemStack.getCapability(Capabilities.ItemHandler.ITEM) instanceof RepeaterContentsComponentHandler handler) {
            Iterator<ItemStack> itemIterator = handler.getAllItemIterator();
            if (itemIterator.hasNext()) {
                ItemStack stack = itemIterator.next();
                if (!stack.isEmpty()) {
                    ClientUtils.renderBait(guiGraphics, stack, x, y);
                }
            }
        }
        return false;
    };
}
