package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;

public class NPCTradeScreen extends AbstractContainerScreen<NPCTradeMenu> {
    private static final int TRADE_ROWS = 4;
    private static final int TRADE_COLS = 9;
    private static final int TRADE_SIZE = TRADE_ROWS * TRADE_COLS;

    public NPCTradeScreen(NPCTradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);

        // Tooltip for NPC items (show buy price) and player-sold items (show refund price)
        for (int i = 0; i < TRADE_SIZE; i++) {
            if (isHovering(i % TRADE_COLS, i / TRADE_COLS, mouseX, mouseY)) {
                NPCTradeMenu.SlotState state = menu.getSlotStates().get(i);
                if (state == NPCTradeMenu.SlotState.NPC_ITEM) {
                    var item = menu.getSlot(i).getItem();
                    int value = ValueComponent.getValue(item, 0);
                    if (value > 0) {
                        float mood = menu.getNPC().getMood().getTradePriceMultiplier();
                        int buyPrice = (int) (value * 5 * mood);
                        Coins coins = PlayerUtils.decodeCoin(buyPrice);
                        g.renderTooltip(font, Component.translatable("tooltip.confluence.trade.buy_price",
                                coins.platinum(), coins.gold(), coins.silver(), coins.copper()), mouseX, mouseY);
                    }
                }
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        g.fill(x, y, x + imageWidth, y + imageHeight, 0xFF_C6C6C6);
        g.fill(x + 1, y + 1, x + imageWidth - 1, y + imageHeight - 1, 0xFF_8B8B8B);

        // Trade slots
        for (int row = 0; row < TRADE_ROWS; row++) {
            for (int col = 0; col < TRADE_COLS; col++) {
                int slotX = x + 7 + col * 18;
                int slotY = y + 17 + row * 18;
                g.fill(slotX, slotY, slotX + 16, slotY + 16, 0xFF_373737);
                g.fill(slotX + 1, slotY + 1, slotX + 15, slotY + 15, 0xFF_8B8B8B);
            }
        }
    }

    private boolean isHovering(int col, int row, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2 + 7 + col * 18;
        int y = (height - imageHeight) / 2 + 17 + row * 18;
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }
}
