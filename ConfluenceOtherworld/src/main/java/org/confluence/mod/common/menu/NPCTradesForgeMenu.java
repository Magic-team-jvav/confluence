package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.trade.ITradeHolder;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.menu.TETradesMenu;
import org.confluence.terraentity.mixed.IPlayer;
import org.jetbrains.annotations.Nullable;

public class NPCTradesForgeMenu extends TETradesMenu {

    public boolean forge;

    public NPCTradesForgeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, false);
        if(((IPlayer)playerInventory.player).terra_entity$getTradeHolder() instanceof AbstractTerraNPC npc && npc.getType() == TENpcEntities.GOBLIN_TINKERER.get()){
            this.forge = true;
        }

    }

    public NPCTradesForgeMenu(int containerId, Inventory playerInventory, @Nullable ITradeHolder NPCTrades, boolean forge) {
        super(ModMenuTypes.NPC_TRADES_MENU.get(), containerId, playerInventory, NPCTrades);
        this.forge = forge;
    }

}
