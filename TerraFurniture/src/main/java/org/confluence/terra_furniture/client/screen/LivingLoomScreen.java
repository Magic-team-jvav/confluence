package org.confluence.terra_furniture.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.lib.client.screen.EitherAmountContainerScreen4x;
import org.confluence.terra_furniture.common.menu.LivingLoomMenu;

public class LivingLoomScreen extends EitherAmountContainerScreen4x<LivingLoomMenu> {
    public LivingLoomScreen(LivingLoomMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
