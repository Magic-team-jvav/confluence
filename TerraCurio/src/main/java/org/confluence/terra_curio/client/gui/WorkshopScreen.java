package org.confluence.terra_curio.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.menu.WorkshopMenu;
import org.jetbrains.annotations.NotNull;

public class WorkshopScreen extends AbstractContainerScreen<WorkshopMenu> {
    private static final ResourceLocation BACKGROUND = TerraCurio.asResource("textures/gui/container/workshop.png");
    private boolean upButtonClicked = false;
    private ItemStack upItem = null;
    private boolean downButtonClicked = false;
    private ItemStack downItem = null;

    public WorkshopScreen(WorkshopMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
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
        if (isOverUpButton(pMouseX - leftPos, pMouseY - topPos)) {
            if (upItem == null) this.upItem = menu.getUpResult();
            pGuiGraphics.renderFakeItem(upItem, leftPos + 125, topPos + 35);
            this.downItem = null;
        } else if (isOverDownButton(pMouseX - leftPos, pMouseY - topPos)) {
            if (downItem == null) this.downItem = menu.getDownResult();
            pGuiGraphics.renderFakeItem(downItem, leftPos + 125, topPos + 35);
            this.upItem = null;
        } else {
            pGuiGraphics.renderFakeItem(menu.getSlot(0).getItem(), leftPos + 125, topPos + 35);
            this.upItem = null;
            this.downItem = null;
        }
        if (menu.getRecipesAmount() > 0) {
            String text = menu.getCurrentIndex() + 1 + "/" + menu.getRecipesAmount();
            pGuiGraphics.drawString(font, text, leftPos + 144, topPos + 37 + (16 - font.lineHeight) / 2, 4210752, false);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (upButtonClicked) {
            pGuiGraphics.blit(BACKGROUND, leftPos + 128, topPos + 23, 177, 0, 10, 8);
        } else if (downButtonClicked) {
            pGuiGraphics.blit(BACKGROUND, leftPos + 128, topPos + 54, 177, 8, 10, 9);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (isOverUpButton((int) pMouseX - leftPos, (int) pMouseY - topPos)) {
            int upIndex = menu.getUpIndex();
            if (menu.clickMenuButton(minecraft.player, upIndex)) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, upIndex);
                this.upButtonClicked = true;
                this.downButtonClicked = false;
                this.upItem = null;
                return true;
            }
            return false;
        } else if (isOverDownButton((int) pMouseX - leftPos, (int) pMouseY - topPos)) {
            int downIndex = menu.getDownIndex();
            if (menu.clickMenuButton(minecraft.player, downIndex)) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, downIndex);
                this.upButtonClicked = false;
                this.downButtonClicked = true;
                this.downItem = null;
                return true;
            }
            return false;
        } else {
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.upButtonClicked = false;
        this.downButtonClicked = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private static boolean isOverUpButton(int x, int y) {
        return x >= 128 && x <= 138 && y >= 23 && y <= 30;
    }

    private static boolean isOverDownButton(int x, int y) {
        return x >= 128 && x <= 138 && y >= 54 && y <= 61;
    }
}
