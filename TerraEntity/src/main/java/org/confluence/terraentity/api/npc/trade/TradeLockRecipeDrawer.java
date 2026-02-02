package org.confluence.terraentity.api.npc.trade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public abstract class TradeLockRecipeDrawer {
    public static final TradeLockRecipeDrawer EMPTY = new TradeLockRecipeDrawer() {
        @Override
        public int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {return y;}
    };

    /**
     * draws a list of trade locks with a given text and tooltip returning the Y offset where drawing ended
     */
    protected int drawRecipeLocks(List<ITradeLock> locks, GuiGraphics guiGraphics, int x, int y, String symbol, int mouseX, int mouseY, String tooltipText) {
        Font font = Minecraft.getInstance().font;
        var symbolWidth  = font.width(symbol);
        int size = getRecipeSize();
        var symbolX = (int) (x + Math.ceil((size - symbolWidth) / 2));
        for (ITradeLock tradeLock : locks) {
            var drawer = tradeLock.getCodec().drawer();
            var oldY = y;
            y = drawer.drawRecipe(tradeLock, guiGraphics, x + size, y, mouseX, mouseY);
            if (y != oldY) {
                drawTooltip(guiGraphics, x, oldY, size, size, mouseX, mouseY, tooltipText);
                guiGraphics.drawString(font, symbol, symbolX, oldY, 0xFFFFFF);
            }
        }
        return y;
    }

    protected void drawTooltip(GuiGraphics guiGraphics, int x, int y, int width, int height, int mouseX, int mouseY, String text) {
        if (mouseX > x && mouseX <= x + width && mouseY > y && mouseY <= y + height) {
            Font font = Minecraft.getInstance().font;
            var textWidth = font.width(text);
            guiGraphics.renderTooltip(font, Component.literal(text), mouseX - textWidth, mouseY);
        }
    }

    protected void drawTooltip(GuiGraphics guiGraphics, int x, int y, int width, int height, int mouseX, int mouseY, List<Component> lines) {
        if (mouseX > x && mouseX <= x + width && mouseY > y && mouseY <= y + height) {
            Font font = Minecraft.getInstance().font;
            int longestLineWidth = 0;
            for (Component line : lines) {
                int lineWidth = font.width(line);
                if (lineWidth > longestLineWidth) {
                    longestLineWidth = lineWidth;
                }
            }

            guiGraphics.renderTooltip(font, lines, Optional.empty(), mouseX - longestLineWidth, mouseY);
        }
    }

    /**
     * draws a trade lock recipe and return the Y offset where drawing ended
     */
    public abstract int drawRecipe(@NotNull ITradeLock lock, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY);

    protected int getRecipeSize() {
        return 8;
    }
}
