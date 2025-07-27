package org.confluence.mod.common.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.gui.container.WithForgeTradeScreen;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.integration.terra_entity.npc_trade.SellTrade;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.menu.TETradesMenu;
import org.confluence.terraentity.mixed.IPlayer;
import org.confluence.terraentity.network.c2s.NPCShopPacket;
import org.confluence.terraentity.utils.AdapterUtils;
import org.jetbrains.annotations.Nullable;

public class NPCTradesForgeMenu extends TETradesMenu {

    public boolean forge;

    public NPCTradesForgeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, false);
        ITradeHolder holder = ((IPlayer) playerInventory.player).terra_entity$getTradeHolder();
        if (holder instanceof AbstractTerraNPC npc && npc.getType() == TENpcEntities.GOBLIN_TINKERER.get()) {
            this.forge = true;
        } else {
            try {
                if (holder != null && holder.getClass().isAssignableFrom(Class.forName("com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid"))) {
                    this.forge = true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

    }

    public NPCTradesForgeMenu(int containerId, Inventory playerInventory, @Nullable ITradeHolder NPCTrades, boolean forge) {
        super(ModMenuTypes.NPC_TRADES_MENU.get(), containerId, playerInventory, NPCTrades);
        this.forge = forge;
    }

    @Override
    protected void addResultSlot() {
        this.addSlot(new Slot(this.container, 0, 238, 37) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
//                var d  = ((IPlayer)playerInventory.player).terra_entity$getDaveTrades();
//                if(selectedMerchantIndex >= 0 && selectedMerchantIndex < d.trades().size()){
//                    PacketDistributor.sendToServer(new NPCShopPacket((ITrade) d.trades().get(selectedMerchantIndex)));
//                }
                super.onTake(player, stack);
            }
        });
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if(player.isLocalPlayer()) {
            if(slotId >0 && slotId <= 36 && WithForgeTradeScreen.CTRL_PRESSED){ // 快速售卖
                ItemStack stack = this.getSlot(slotId).getItem();

                if(ValueComponent.getValue(stack, 0) > 0) {
                    if (Minecraft.getInstance().screen instanceof WithForgeTradeScreen screen) {
                        ITradeHolder holder = ((IPlayer) player).terra_entity$getTradeHolder();

                        if (holder != null && screen.shopItem >= 0 && holder.getTradeManager().availableTrades().get(screen.shopItem) instanceof SellTrade sell) {
                            sell.onClick(0, 0, button, 1000 + slotId, this.getSlot(slotId));
                        }
                    }
                }
            }
        }
        super.clicked(slotId, button, clickType, player);
    }
}