package org.confluence.terraentity.menu;

import net.minecraft.world.entity.player.Inventory;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.init.TEMenus;
import org.jetbrains.annotations.Nullable;

/**
 * 这个类提供简单统一的菜单界面{@link ITrade}
 */
public class SimpleTradeMenu extends TETradesMenu {

    public SimpleTradeMenu(int containerId, Inventory playerInventory) {
        super(TEMenus.SIMPLE_NPC_TRADES_MENU.get(), containerId, playerInventory);
    }

    public SimpleTradeMenu(int containerId, Inventory playerInventory, @Nullable ITradeHolder NPCTrades) {
        super(TEMenus.SIMPLE_NPC_TRADES_MENU.get(), containerId, playerInventory, NPCTrades);
    }
}
