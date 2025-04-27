package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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

    protected void addResultSlot(){
        this.addSlot(new Slot(this.container, 0, 238, 37){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }
            @Override
            public void onTake(Player player, ItemStack stack){
//                var d  = ((IPlayer)playerInventory.player).terra_entity$getDaveTrades();
//                if(selectedMerchantIndex >= 0 && selectedMerchantIndex < d.trades().size()){
//                    PacketDistributor.sendToServer(new NPCShopPacket((ITrade) d.trades().get(selectedMerchantIndex)));
//                }
                super.onTake(player, stack);
            }
        });
    }

}
