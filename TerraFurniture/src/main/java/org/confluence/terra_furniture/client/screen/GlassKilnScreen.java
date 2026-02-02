package org.confluence.terra_furniture.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.menu.GlassKilnMenu;

public class GlassKilnScreen extends AbstractContainerScreen<GlassKilnMenu> {
    private static final ResourceLocation BACKGROUND = TerraFurniture.asResource("textures/gui/container/glass_kiln.png");

    public GlassKilnScreen(GlassKilnMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        this.imageHeight = 190;
        this.inventoryLabelY = this.imageHeight - 94;
        super.init();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int bp = (int) (menu.getBurnProgress() * 24);
        guiGraphics.blit(BACKGROUND, leftPos + 97, topPos + 47, 177, 0, bp, 16);
        int lp = (int) (menu.getLitProgress() * 14);
        guiGraphics.blit(BACKGROUND, leftPos + 133, topPos + 52 + 14 - lp, 177, 17 + 14 - lp, 14, lp);
    }
}
