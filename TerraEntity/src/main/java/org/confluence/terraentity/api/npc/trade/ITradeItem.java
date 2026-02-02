package org.confluence.terraentity.api.npc.trade;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

import static org.confluence.terraentity.client.gui.container.TETradeScreen.MENU_LOCATION;

/**
 * 当交易获得的物品是单个物品时继承这个接口
 */
public interface ITradeItem extends ITrade{

    ItemStack result();

    @Override
    default void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        player.getInventory().placeItemBackInInventory(result().copy());


    }

    @OnlyIn(Dist.CLIENT)
    default void renderResult(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, int slotIndex){
        var it = result();

        guiGraphics.renderItem(it, x , y );

        guiGraphics.renderItemDecorations(font, it, x, y);
    }

    @OnlyIn(Dist.CLIENT)
    default void renderResultHover(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY){

        guiGraphics.renderTooltip(font, result(), mouseX, mouseY);
    }



    @OnlyIn(Dist.CLIENT)
    default void renderResultSlot(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, boolean canBuy, Slot slot){
        if(canBuy){
            slot.set(result().copy());
            guiGraphics.blit(MENU_LOCATION,x,y,276,0,35,17,512,256);
        }else{
            slot.set(ItemStack.EMPTY);
            guiGraphics.blit(MENU_LOCATION,x,y,276,17,35,17,512,256);
        }
    }

    default List<ItemStack> normalizeResult(){
        return List.of(result());
    }
}
