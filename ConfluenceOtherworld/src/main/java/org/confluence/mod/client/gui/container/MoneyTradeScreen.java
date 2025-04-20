package org.confluence.mod.client.gui.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.terraentity.client.gui.container.TETradeScreen;
import org.confluence.terraentity.entity.npc.NPCTrades;
import org.confluence.mod.common.menu.NPCTradesMenu;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.mixed.IPlayer;


public class MoneyTradeScreen extends TETradeScreen<NPCTradesMenu> {

    ImageButton forgeBt;

    public MoneyTradeScreen(NPCTradesMenu menu, Inventory playerInventory, Component title) {
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
                    ((IPlayer) player).terra_entity$setDaveTrades(NPCTrades.getTradeById(TENpcEntities.GUIDE.getId()));
                    OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.NPC_REFORGE_MENU, stack);
                }
            });
            addRenderableWidget(forgeBt);
        }
    }


}
