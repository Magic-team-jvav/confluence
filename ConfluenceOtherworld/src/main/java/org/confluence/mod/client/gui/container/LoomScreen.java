package org.confluence.mod.client.gui.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.lib.client.screen.EitherAmountContainerScreen4x;
import org.confluence.mod.common.menu.LoomMenu;

public class LoomScreen extends EitherAmountContainerScreen4x<LoomMenu> {
    public LoomScreen(LoomMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
