package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.menu.PiggyBankMenu;

public class PiggyBankScreen extends AbstractContainerScreen<PiggyBankMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/piggy_bank.png");

    public PiggyBankScreen(PiggyBankMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 114 + menu.getRowCount() * 18;
        this.inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (width - imageWidth) / 2;
        int j = (height - imageHeight) / 2;
        guiGraphics.blit(BACKGROUND, i, j, 0, 0, imageWidth, imageHeight);
        guiGraphics.blit(BACKGROUND, i - 33, j + 10, 224, 0, 32, 86);
        ExtraInventory extraInventory = menu.getExtraInventory();
        for (int k = 0; k < ExtraInventory.SIZE_COINS; k++) {
            if (extraInventory.getCoins(k).isEmpty()) {
                guiGraphics.blit(BACKGROUND, i - 25, j + 18 + k * 18, 207, 0, 16, 16);
            }
        }
    }
}
