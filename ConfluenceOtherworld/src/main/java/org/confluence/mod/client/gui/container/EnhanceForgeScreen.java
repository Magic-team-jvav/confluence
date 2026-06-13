package org.confluence.mod.client.gui.container;

import PortLib.extensions.net.minecraft.client.gui.GuiGraphics.PortGuiGraphicsExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.EnhancedForgeMenu;
import org.mesdag.portlib.client.gui.components.PortSprite;

public abstract class EnhanceForgeScreen<M extends EnhancedForgeMenu> extends AbstractContainerScreen<M> {
    public static final ResourceLocation SUPER_LIT_PROGRESS = Confluence.asResource("textures/gui/container/super_lit_progress.png");
    public static final PortSprite BURN_PROGRESS_SPRITE = new PortSprite(ResourceLocation.withDefaultNamespace("container/furnace/burn_progress"), 24, 16);
    public static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/hellforge.png");

    public EnhanceForgeScreen(M menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = imageWidth - font.width(title) - 8;
        this.inventoryLabelX = imageWidth - font.width(playerInventoryTitle) - 8;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (menu.isLit()) {
            int l = Mth.ceil(menu.getLitProgress() * 16);
            guiGraphics.blit(SUPER_LIT_PROGRESS, leftPos + 57, topPos + 59, 0, 0, 14, 14, 14, 14);
            guiGraphics.blit(BACKGROUND, leftPos + 56, topPos + 75, 177, 0, l, 3, 256, 256);
        }
        int j1 = Mth.ceil(menu.getBurnProgress() * 24);
        PortGuiGraphicsExtension.blitSprite(guiGraphics, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, leftPos + 91, topPos + 34, j1, 16);
    }
}
