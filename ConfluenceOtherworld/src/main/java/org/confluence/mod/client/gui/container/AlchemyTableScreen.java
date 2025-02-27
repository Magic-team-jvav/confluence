package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.AlchemyTableMenu;
import org.jetbrains.annotations.NotNull;

import static org.confluence.mod.common.menu.AlchemyTableMenu.CORD;

public class AlchemyTableScreen extends AbstractContainerScreen<AlchemyTableMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/alchemy_table.png");

    public AlchemyTableScreen(AlchemyTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (menu.input.getItem(0).isEmpty()) {
            guiGraphics.blit(BACKGROUND, leftPos + 80, topPos + 17, 178, 17, 16, 16);
        }
        for (int i = 0; i < 6; i++) {
            if (menu.input.getItem(i + 1).isEmpty()) {
                int[] cord = CORD[i];
                guiGraphics.blit(BACKGROUND, leftPos + cord[0], topPos + cord[1], 178, 0, 16, 16);
            }
        }
    }
}
