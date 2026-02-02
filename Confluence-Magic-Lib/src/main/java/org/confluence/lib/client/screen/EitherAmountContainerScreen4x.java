package org.confluence.lib.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.menu.EitherAmountContainerMenu4x;

public class EitherAmountContainerScreen4x<M extends EitherAmountContainerMenu4x<?, ?, ?, ?>> extends AbstractContainerScreen<M> {
    public static final ResourceLocation BACKGROUND = ConfluenceMagicLib.asResource("textures/gui/container/normal4x.png");
    private float titleScale = 1;
    private boolean upButtonClicked = false;
    private boolean downButtonClicked = false;
    private ResourceLocation background;

    public EitherAmountContainerScreen4x(M menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        int titleWidth = font.width(title);
        if (titleWidth > 68) {
            this.titleScale = 68.0F / titleWidth;
            this.titleLabelX = imageWidth - 76;
        } else {
            this.titleLabelX = imageWidth - titleWidth - 8;
        }
        this.inventoryLabelX = imageWidth - font.width(playerInventoryTitle) - 8;
        this.background = background();
    }

    protected ResourceLocation background() {
        return BACKGROUND;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(titleLabelX, titleLabelY, 0);
        pose.scale(titleScale, titleScale, titleScale);
        guiGraphics.drawString(font, title, 0, 0, 4210752, false);
        pose.popPose();
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 4210752, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        menu.resultSlot.isActive = true;
        if (menu.getRecipesAmount() > 1) {
            String text = menu.getCurrentIndex() + 1 + "/" + menu.getRecipesAmount();
            guiGraphics.drawString(font, text, leftPos + 154, topPos + 37 + (16 - font.lineHeight) / 2, 4210752, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (menu.getRecipesAmount() > 1) {
            if (upButtonClicked) {
                guiGraphics.blit(background, leftPos + 135, topPos + 21, 188, 1, 10, 7);
            } else {
                guiGraphics.blit(background, leftPos + 135, topPos + 20, 177, 0, 10, 8);
            }
            if (downButtonClicked) {
                guiGraphics.blit(background, leftPos + 135, topPos + 61, 188, 10, 10, 7);
            } else {
                guiGraphics.blit(background, leftPos + 135, topPos + 60, 177, 9, 10, 8);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (menu.getRecipesAmount() > 1) {
            if (isOverUpButton((int) mouseX - leftPos, (int) mouseY - topPos)) {
                int upIndex = menu.getUpIndex();
                if (menu.clickMenuButton(minecraft.player, upIndex)) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, upIndex);
                    this.upButtonClicked = true;
                    this.downButtonClicked = false;
                    return true;
                }
                return false;
            } else if (isOverDownButton((int) mouseX - leftPos, (int) mouseY - topPos)) {
                int downIndex = menu.getDownIndex();
                if (menu.clickMenuButton(minecraft.player, downIndex)) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, downIndex);
                    this.upButtonClicked = false;
                    this.downButtonClicked = true;
                    return true;
                }
                return false;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.upButtonClicked = false;
        this.downButtonClicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private static boolean isOverUpButton(int x, int y) {
        return x >= 135 && x <= 145 && y >= 20 && y <= 28;
    }

    private static boolean isOverDownButton(int x, int y) {
        return x >= 135 && x <= 145 && y >= 60 && y <= 68;
    }
}
