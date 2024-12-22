package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.HellforgeMenu;
import org.jetbrains.annotations.NotNull;

public class HellforgeScreen extends AbstractContainerScreen<HellforgeMenu> {
    private static final ResourceLocation LIT_PROGRESS_SPRITE = Confluence.asResource("textures/gui/container/super_lit_progress.png");
    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/hellforge.png");

    public HellforgeScreen(HellforgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = imageWidth - font.width(title) - 8;
        this.inventoryLabelX = imageWidth - font.width(playerInventoryTitle) - 8;
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (menu.isLit()) {
            int l = Mth.ceil(menu.getLitProgress() * 13.0F) + 1;
            guiGraphics.blit(LIT_PROGRESS_SPRITE, leftPos + 57, topPos + 58 + 14 - l, 0, 14 - l, 14, l, 14, 14);
        }
        int j1 = Mth.ceil(menu.getBurnProgress() * 24.0F);
        guiGraphics.blitSprite(BURN_PROGRESS_SPRITE, 24, 16, 0, 0, leftPos + 91, topPos + 34, j1, 16);
    }
}
