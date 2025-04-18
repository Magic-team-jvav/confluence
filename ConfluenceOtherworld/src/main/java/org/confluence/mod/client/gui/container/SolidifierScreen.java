package org.confluence.mod.client.gui.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.lib.client.screen.EitherAmountContainerScreen4x;
import org.confluence.mod.common.menu.SolidifierMenu;

public class SolidifierScreen extends EitherAmountContainerScreen4x<SolidifierMenu> {
    public SolidifierScreen(SolidifierMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
}
