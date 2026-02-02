package org.confluence.terra_furniture.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.lib.client.screen.EitherAmountContainerScreen4x;
import org.confluence.terra_furniture.common.menu.IceMachineMenu;

public class IceMachineScreen extends EitherAmountContainerScreen4x<IceMachineMenu> {
    public IceMachineScreen(IceMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
