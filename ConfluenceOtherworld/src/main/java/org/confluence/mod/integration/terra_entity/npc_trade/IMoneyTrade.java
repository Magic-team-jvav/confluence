package org.confluence.mod.integration.terra_entity.npc_trade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terraentity.registries.npc_trade.ITrade;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

import static org.confluence.mod.integration.terra_entity.npc_trade.Util.coinItem;

public interface IMoneyTrade extends ITrade {

    long cost();


    default long getCost(@Nullable Player player){
        return cost();
    }

    default boolean canTrade(Player player) {
        return PlayerUtils.getMoney(player) >= getCost(player);
    }

    @Override
    default void onTrade(ServerPlayer player) {
        if(PlayerUtils.tryCostMoney(player, this.getCost(player))) {
            onTradeSuccess(player);
        }
    }

    /**
     * 重复确认钱币足够
     */
    void onTradeSuccess(ServerPlayer player);

    @OnlyIn(Dist.CLIENT)
    @Override
    default void renderCosts(GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY) {
        // 左上方背包的金币
        int []myCoins;
        if (Minecraft.getInstance().player != null) {
            myCoins = PlayerUtils.getCoins(Minecraft.getInstance().player);
        }else{
            myCoins = new int[]{};
        }
        x = startx + 5;
        y = starty - 20;
        if(coinItem == null){
            coinItem = List.of(
                    ModItems.PLATINUM_COIN.get(),
                    ModItems.GOLDEN_COIN.get(),
                    ModItems.SILVER_COIN.get(),
                    ModItems.COPPER_COIN.get());
        }
        for( int i = 0; i < myCoins.length; i++){
            ItemStack stack = new ItemStack(coinItem.get(i), myCoins[i]);
            int count = stack.getCount();
            guiGraphics.renderItem(stack, x, y );
            float scale = (count < 1000) ? 1.0F : 0.75F;
            int moveX = (count < 1000) ? 0 : 6;
            int moveY = (count < 1000) ? 0 : 5;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(scale, scale, 1.0F);
            guiGraphics.renderItemDecorations(font, stack, (int) (x / scale) + moveX, (int) (y / scale) + moveY);
            guiGraphics.pose().popPose();
            x+= (count == 0) ? 0 : 20;
        }

        // 物品花费
        int[] coins = PlayerUtils.decodeCoin(getCost(Minecraft.getInstance().player));
        x = startx + 120;
        y = starty + 21;

        for(int k = 0; k < coins.length; k++){
            guiGraphics.renderItem(coinItem.get(3-k).getDefaultInstance(), x, y );
            guiGraphics.drawString(font, String.valueOf(coins[k]), x+4, y+16 , Color.orange.getRGB(), true);
            x+=20;
            if( k % 3 == 2){
                y += 25;
                x = startx + 130;
            }
        }
    }




}
