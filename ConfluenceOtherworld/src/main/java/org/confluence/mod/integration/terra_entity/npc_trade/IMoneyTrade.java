package org.confluence.mod.integration.terra_entity.npc_trade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terraentity.entity.npc.mood.NPCMood;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.stream.IntStream;

import static org.confluence.mod.integration.terra_entity.npc_trade.Util.coinItem;
import static org.confluence.terraentity.client.gui.container.TETradeScreen.MENU_LOCATION;

public interface IMoneyTrade extends ITrade {

    long cost();


    default long getCost(@Nullable Player player,  ITradeHolder npc){
        float discount = 1;
        NPCMood mood = npc.getMood();
        if(mood!= null){
            discount = 100.0f / mood.getValue();
        }
        return (long) (cost() * discount);
    }

    default boolean canTrade(Player player, ITradeHolder npc, int index) {
        return PlayerUtils.getMoney(player) >= getCost(player, npc);
    }

    @Override
    default void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        long cost = this.getCost(player, npc);
        if(PlayerUtils.tryCostMoney(player, cost)) {
            onTradeSuccess(player, npc, index, cost);
        }
    }

    /**
     * 重复确认钱币足够
     */
    void onTradeSuccess(ServerPlayer player, ITradeHolder npc, int index, long cost);

    @OnlyIn(Dist.CLIENT)
    @Override
    default void renderCosts(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY) {

        guiGraphics.blit(MENU_LOCATION,startx + 113,starty + 16,434,59,78,57,512,256);

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

        int[] coins = PlayerUtils.decodeCoin(getCost(Minecraft.getInstance().player, npc));
        x = startx + 165;
        y = starty + 6 + 13* 3;
        int index = -1;
         int i = 0;
        for (int coin : coins) {
//            guiGraphics.renderItem(coinItem.get(3-k).getDefaultInstance(), x, y );
            String s = String.valueOf(coin);
            guiGraphics.drawString(font, String.valueOf(coin), x + 4 - s.length() * 3, y + 16, Color.orange.getRGB(), true);
            y -= 13;
            if(coin >= 1){
                index = i;
            }
            i++;
        }
        if(index >= 0){
            int w = 34;
            int sx = 443 + ((index & 1) == 0? 0: 35);
            int sy = 118 + ((index & 10) == 0? 0: 35);
            guiGraphics.blit(MENU_LOCATION,startx + 117,starty + 28,sx,sy,w,w,512,256);
        }
    }


    @Override
    default List<Ingredient> normalizeCost(){
        int []coins = PlayerUtils.decodeCoin(this.cost());
        return IntStream.range(0,coins.length).mapToObj(i-> AmountIngredient.of(coins[i], PlayerUtils.INDEX_2_COIN.apply(i))).filter(i->!i.isEmpty()).toList();

    }
}
