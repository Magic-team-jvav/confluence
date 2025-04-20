package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.terraentity.entity.npc.ITradeHolder;
import org.confluence.terraentity.menu.TETradesMenu;
import org.jetbrains.annotations.Nullable;

public class NPCTradesForgeMenu extends TETradesMenu {

    public boolean forge;

    public NPCTradesForgeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, false);

    }

    public NPCTradesForgeMenu(int containerId, Inventory playerInventory, @Nullable ITradeHolder NPCTrades, boolean forge) {
        super(ModMenuTypes.NPC_TRADES_MENU.get(), containerId, playerInventory, NPCTrades);
        this.forge = forge;
    }

}
