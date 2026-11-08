package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.common.menu.BrewingVialMenu;

/// 炼药瓶的界面：直接复用炼药桌的背景与布局。
public class BrewingVialScreen extends AbstractContainerScreen<BrewingVialMenu> {
    public BrewingVialScreen(BrewingVialMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        renderBackground(guiGraphics);
        AlchemyTableScreen.renderTableBackground(guiGraphics, menu, leftPos, topPos, imageWidth, imageHeight);
    }
}
