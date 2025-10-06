package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.menu.NPCTradesForgeMenu;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.mod.network.c2s.SellTradePacketC2S;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.client.gui.container.TETradeScreen;
import org.confluence.terraentity.entity.npc.mood.NPCMood;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

import static org.confluence.terraentity.client.gui.container.TETradeScreen.MENU_LOCATION;

public class SellTrade implements ITrade {
    public static final SellTrade INSTANCE = new SellTrade();
    public static final MapCodec<SellTrade> CODEC = Codec.of(Encoder.empty(), Decoder.unit(INSTANCE));

    private SellTrade() {}

    public long getCost(@Nullable Player player, ITradeHolder npc, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        float discount = 1;
        NPCMood mood = npc.getMood();
        if (mood != null) {
            discount = mood.getValue() / 100f;
        }
        return (long) (ValueComponent.getValue(stack, 0) * discount);
    }

    // 让交易始终能够通过
    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return true;
    }

    // 因为复用的slot，所以不能复用onTrade函数，用onSell代替
    @Override
    public void onTrade(ServerPlayer player, ITradeHolder npc, int index) {}

    @Override
    public List<ITrade> getAllSupportedTrades() {
        return List.of();
    }

    @Override
    public List<Ingredient> normalizeCost() {
        return List.of();
    }

    @Override
    public List<ItemStack> normalizeResult() {
        return List.of();
    }

    public void onSell(ServerPlayer player, ITradeHolder npc, int index) {
        if (player.containerMenu instanceof NPCTradesForgeMenu menu) {
            ItemStack stack;
            if (index > 1000) { //  // 约定1000以上为快速交易，此时index - 1000为此时menu点击的物品
                stack = menu.slots.get(index - 1000).getItem();
            } else {
                stack = menu.slots.getFirst().getItem();
            }
            Coins coins = PlayerUtils.decodeCoin(ValueComponent.getValue(stack, 0));
            for (Object2IntMap.Entry<CoinItem> entry : coins.copper2PlatinumEntries()) {
                player.getInventory().add(new ItemStack(entry.getKey(), entry.getIntValue()));
            }
            stack.setCount(0);
        }
    }

    @Override
    public @Nullable TradeProperties properties() {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderCosts(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY) {
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
        if (Minecraft.getInstance().screen instanceof TETradeScreen<?> screen) {
            Coins coins = PlayerUtils.decodeCoin(getCost(player, npc, screen.getMenu().slots.getFirst().getItem()));
            x = startx + 165;
            y = starty + 6 + 13 * 3;
            int index = -1;
            int i = 0;
            for (Integer coin : coins.copper2Platinum()) {
                String s = coin.toString();
                guiGraphics.drawString(font, s, x + 4 - s.length() * 3, y + 16, Color.orange.getRGB(), true);
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
                int leftPos = screen.getGuiLeft();
                int topPos = screen.getGuiTop();
                if (mouseX - leftPos >= 127 && mouseX - leftPos <= 143.5 && mouseY - topPos >= 40.5 && mouseY - topPos <= 50.5) {
                    guiGraphics.fillGradient(RenderType.guiOverlay(), leftPos + 126, topPos + 39, leftPos + 143, topPos + 53, 0x80808000, -2130706433, 0);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderResult(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, int slotIndex) {
        int size = 4;
        int i = (int) (npc.level().getDayTime() / 40 % size);
        var it = PlayerUtils.INDEX_2_COIN.apply(i);

        guiGraphics.renderItem(it.getDefaultInstance(), x, y);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderResultHover(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY) {}

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderResultSlot(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, boolean canBuy, Slot slot) {
        guiGraphics.fill(x, y, x + 23, y + 17, 0xFFc6c6c6);
        guiGraphics.blitSprite(Confluence.asResource("widget/arrow_left"), x, y, 22, 17);
    }

    // 点击金钱图标时发送交易请求
    @Override
    public void onClick(double mouseX, double mouseY, int button, int index, Slot slot) {
        if (slot.hasItem() || index > 1000) {
            PacketDistributor.sendToServer(new SellTradePacketC2S(index));
        }
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.SELL_TRADE.get();
    }
}
