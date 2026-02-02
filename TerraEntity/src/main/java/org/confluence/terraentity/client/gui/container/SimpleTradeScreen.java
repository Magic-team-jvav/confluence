package org.confluence.terraentity.client.gui.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.menu.SimpleTradeMenu;

/**
 * 这个类提供简单统一的菜单界面{@link ITrade}
 */
public class SimpleTradeScreen extends TETradeScreen<SimpleTradeMenu> {
    public SimpleTradeScreen(SimpleTradeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }


}
