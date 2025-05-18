package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.CookingPotMenu;

public class CookingPotScreen extends AbstractContainerScreen<CookingPotMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/cooking_pot.png");

    public CookingPotScreen(CookingPotMenu menu, Inventory playerInventory, Component title) {
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
        int width = Mth.ceil(menu.getBurnProgress() * 46);
        guiGraphics.blit(BACKGROUND, leftPos + 78, topPos + 36, 177, 17, width, 15);
        ItemStack heatSourceItem = menu.getHeatSourceItem();
        if (heatSourceItem.isEmpty()) {
            guiGraphics.blit(BACKGROUND, leftPos + 92, topPos + 51, 177, 0, 16, 16);
        } else {
            guiGraphics.renderItem(heatSourceItem, leftPos + 92, topPos + 51);
        }
        if (!menu.getSlot(4).hasItem()) {
            guiGraphics.blit(BACKGROUND, leftPos + 92, topPos + 20, 194, 0, 16, 16);
        }
    }
}
