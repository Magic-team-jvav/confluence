package org.confluence.mod.client.gui.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.integration.terra_entity.npc_trade.SellTrade;
import org.confluence.terraentity.client.gui.container.TETradeScreen;
import org.confluence.mod.common.menu.NPCTradesForgeMenu;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;
import org.confluence.terraentity.api.trade.ITrade;


public class WithForgeTradeScreen extends TETradeScreen<NPCTradesForgeMenu> {

    ImageButton forgeBt;
    public ItemStack sellItem;

    public WithForgeTradeScreen(NPCTradesForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 290;
        this.inventoryLabelX = 107;

    }

    @Override
    protected void init() {
        super.init();

        if(menu.forge){
            forgeBt = new ImageButton(leftPos + 2, topPos + 2, 16, 16, ModClientSetups.EXTRA_INVENTORY_BUTTON, button -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack stack = player.containerMenu.getCarried();
                    player.containerMenu.setCarried(ItemStack.EMPTY);

                    OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.NPC_REFORGE_MENU, stack);
                }
            });
            addRenderableWidget(forgeBt);
        }
        this.sellItem = ItemStack.EMPTY;
    }

    protected boolean canSelect(int index,double mouseX, double mouseY, int button) {

        Slot slot = this.menu.slots.get(0);
        ITrade lastTrade = null;
        if(this.shopItem >= 0){
            lastTrade = this.menu.NPCTrades.trades().get(this.shopItem);
        }
        if(index == -1 ){
            // 如果没有选中交易项
            if(lastTrade instanceof SellTrade sell){
                // 如果已经选中卖出
                if(mouseX - leftPos >=127 && mouseX - leftPos<= 143.5 && mouseY - topPos>= 40.5 && mouseY - topPos<= 50.5){
                    sell.onClick(mouseX, mouseY, button, this.shopItem, this.menu.slots.get(0));
                }
                return false;
            }
            return true;
        }
        ITrade thisTrade = this.menu.NPCTrades.trades().get(index);
        if(thisTrade instanceof SellTrade){
            // 这一个是出售
            if(!slot.getItem().isEmpty()){
                // 槽内有物品
                return false;
            }
            if(!(lastTrade instanceof SellTrade)){
                slot.set(ItemStack.EMPTY);
            }

            return true;
        }

        if(lastTrade instanceof SellTrade && !slot.getItem().isEmpty()){
            // 上一个是出售，但是槽内有物品
            return false;
        }
        // 槽内没有物品
        return true;
    }
}
