package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.FletchingTableMenu;

public class FletchingTableScreen extends AbstractContainerScreen<FletchingTableMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/fletching_table.png");

    public FletchingTableScreen(FletchingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (menu.input.getTail().isEmpty()) {
            guiGraphics.blit(BACKGROUND, leftPos + 30, topPos + 53, 177, 34, 16, 16);
        }
        if (menu.input.getBody().isEmpty()) {
            guiGraphics.blit(BACKGROUND, leftPos + 48, topPos + 35, 177, 17, 16, 16);
        }
        if (menu.input.getHead().isEmpty()) {
            guiGraphics.blit(BACKGROUND, leftPos + 66, topPos + 17, 177, 0, 16, 16);
        }
    }
}
