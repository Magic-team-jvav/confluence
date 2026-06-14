package org.confluence.mod.integration.terra_entity.npc_trade;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.entity.npc.mood.NPCMood;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.confluence.terraentity.client.gui.container.TETradeScreen.MENU_LOCATION;

public interface IMoneyTrade extends ITrade {
    long cost();

    default long getCost(@Nullable Player player, ITradeHolder npc) {
        float discount = 1;
        NPCMood mood = npc.getMood();
        if (mood != null) {
            discount = 100.0f / mood.getValue();
        }
        return (long) (cost() * discount);
    }

    default boolean canTrade(Player player, ITradeHolder npc, int index) {
        return PlayerUtils.getMoney(player, true) >= getCost(player, npc);
    }

    @Override
    default void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        long cost = this.getCost(player, npc);
        if (PlayerUtils.tryCostMoney(player, cost, true)) {
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
        guiGraphics.blit(MENU_LOCATION, startx + 113, starty + 16, 434, 59, 78, 57, 512, 256);

        // 左上方背包的金币
        LocalPlayer player = Minecraft.getInstance().player;
        Coins myCoins = player == null ? null : PlayerUtils.getCoins(player, true);

        x = startx + 5;
        y = starty - 20;
        if (myCoins != null) {
            for (Object2IntMap.Entry<CoinItem> entry : myCoins.platinum2CopperEntries()) {
                ItemStack stack = new ItemStack(entry.getKey(), entry.getIntValue());
                int count = stack.getCount();
                guiGraphics.renderItem(stack, x, y);
                float scale = (count < 1000) ? 1.0F : 0.75F;
                int moveX = (count < 1000) ? 0 : 6;
                int moveY = (count < 1000) ? 0 : 5;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(scale, scale, 1.0F);
                guiGraphics.renderItemDecorations(font, stack, (int) (x / scale) + moveX, (int) (y / scale) + moveY);
                guiGraphics.pose().popPose();
                x += (count == 0) ? 0 : 20;
            }
        }

        // 物品花费
        Coins coins = PlayerUtils.decodeCoin(getCost(player, npc));
        x = startx + 165;
        y = starty + 6 + 13 * 3;
        int index = -1;
        int i = 0;
        for (Integer coin : coins.copper2Platinum()) {
            String s = coin.toString();
            guiGraphics.drawString(font, s, x + 4 - s.length() * 3, y + 16, 0xFFFFC800, true);
            y -= 13;
            if (coin >= 1) {
                index = i;
            }
            i++;
        }
        if (index >= 0) {
            int w = 34;
            int sx = 443 + ((index & 1) == 0 ? 0 : 35);
            int sy = 118 + ((index & 10) == 0 ? 0 : 35);
            guiGraphics.blit(MENU_LOCATION, startx + 117, starty + 28, sx, sy, w, w, 512, 256);
        }
    }


    @Override
    default List<Ingredient> normalizeCost() {
        return PlayerUtils.decodeCoin(this.cost()).copper2PlatinumEntries().stream()
                .filter(entry -> entry.getIntValue() > 0)
                .map(entry -> AmountIngredient.of(entry.getIntValue(), entry.getKey()))
                .toList();
    }
}
