package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.terraentity.menu.TETradesMenu;
import org.confluence.terraentity.entity.npc.NPCTrades;
import org.jetbrains.annotations.Nullable;

public class NPCTradesMenu extends TETradesMenu {

    public boolean forge;

    public NPCTradesMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, false);

    }

    public NPCTradesMenu(int containerId, Inventory playerInventory, @Nullable NPCTrades NPCTrades, boolean forge) {
        super(ModMenuTypes.NPC_TRADES_MENU.get(), containerId, playerInventory, NPCTrades);
        this.forge = forge;

    }


    public void playTradeSound() {

    }

}
