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

public interface ITradeItemList extends ITrade {

    List<ItemStack> result();

    @Override
    default void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        for(ItemStack stack : result()){{
            player.getInventory().placeItemBackInInventory(stack.copy());
        }}
    }

    @OnlyIn(Dist.CLIENT)
    default void renderResult(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, int index){

        int size = result().size();
        int i = (int) (System.currentTimeMillis() % 1000000L / 1000 % size);
        var it = result().get(i);

        guiGraphics.renderItem(it, x , y );

        guiGraphics.renderItemDecorations(font, it, x, y);
    }

    @OnlyIn(Dist.CLIENT)
    default void renderResultHover(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY){
        int size = result().size();
        int i = (int) (System.currentTimeMillis() % 1000000L / 1000 % size);
        var it = result().get(i);
        guiGraphics.renderTooltip(font, it, mouseX, mouseY);
    }



    @OnlyIn(Dist.CLIENT)
    default void renderResultSlot(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, boolean canBuy, Slot slot){
        if(canBuy){
            guiGraphics.blit(MENU_LOCATION,x,y,276,0,35,17,512,256);

            int size = result().size();
            int i = (int) (System.currentTimeMillis() % 1000000L / 1000 % size);
            var it = result().get(i);
            slot.set(it.copy());
            int w = 16 / size;
            x = x + 35 + ((size & 1) == 0? w / 2 : 0);
            for(int j = 0; j < size; j++){
                ItemStack stack = result().get(j);
                int x1 = x + (j - size / 2) * w;
                guiGraphics.renderItem(stack, x1, y);
                guiGraphics.renderItemDecorations(font, stack, x1, y);
            }

        }else{

            guiGraphics.blit(MENU_LOCATION,x,y,276,17,35,17,512,256);
        }
        slot.set(ItemStack.EMPTY);
    }

    default List<ItemStack> normalizeResult(){
        return result();
    }
}
