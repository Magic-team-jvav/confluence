package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.SkyMillMenu;
import org.confluence.mod.common.recipe.SkyMillRecipe;

import java.util.List;

public class SkyMillScreen extends AbstractContainerScreen<SkyMillMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/sky_mill.png");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int RECIPES_COLUMNS = 4;
    private static final int RECIPES_ROWS = 3;
    private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
    private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
    private static final int SCROLLER_FULL_HEIGHT = 54;
    private static final int RECIPES_X = 87;
    private static final int RECIPES_Y = 14;
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public SkyMillScreen(SkyMillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        pMenu.registerUpdateListener(this::containerChanged);
        --this.titleLabelY;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = imageWidth - font.width(title) - 8;
        this.inventoryLabelX = imageWidth - font.width(playerInventoryTitle) - 8;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int k = (int) (41.0F * scrollOffs);
        pGuiGraphics.blit(BACKGROUND, leftPos + 154, topPos + SCROLLER_HEIGHT + k, 176 + (isScrollBarActive() ? 0 : SCROLLER_WIDTH), 0, 12, SCROLLER_HEIGHT);
        int l = leftPos + RECIPES_X;
        int i1 = topPos + RECIPES_Y;
        int j1 = startIndex + SCROLLER_WIDTH;
        renderButtons(pGuiGraphics, pMouseX, pMouseY, l, i1, j1);
        renderRecipes(pGuiGraphics, l, i1, j1);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        if (displayRecipes) {
            int i = leftPos + RECIPES_X;
            int j = topPos + RECIPES_Y;
            int k = startIndex + SCROLLER_WIDTH;
            List<SkyMillRecipe> list = menu.getRecipes();

            for (int l = startIndex; l < k && l < menu.getNumRecipes(); ++l) {
                int i1 = l - startIndex;
                int j1 = i + i1 % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
                int k1 = j + i1 / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT + 2;
                if (pX >= j1 && pX < j1 + RECIPES_IMAGE_SIZE_WIDTH && pY >= k1 && pY < k1 + RECIPES_IMAGE_SIZE_HEIGHT) {
                    pGuiGraphics.renderTooltip(font, list.get(l).value().getResultItem(minecraft.level.registryAccess()), pX, pY);
                }
            }
        }
    }

    private void renderButtons(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pX, int pY, int pLastVisibleElementIndex) {
        for (int i = startIndex; i < pLastVisibleElementIndex && i < menu.getNumRecipes(); ++i) {
            int j = i - startIndex;
            int k = pX + j % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int l = j / RECIPES_COLUMNS;
            int i1 = pY + l * RECIPES_IMAGE_SIZE_HEIGHT + 2;
            int j1 = imageHeight;
            if (i == menu.getSelectedRecipeIndex()) {
                j1 += RECIPES_IMAGE_SIZE_HEIGHT;
            } else if (pMouseX >= k && pMouseY >= i1 && pMouseX < k + RECIPES_IMAGE_SIZE_WIDTH && pMouseY < i1 + RECIPES_IMAGE_SIZE_HEIGHT) {
                j1 += 36;
            }
            pGuiGraphics.blit(BACKGROUND, k, i1 - 1, 0, j1, RECIPES_IMAGE_SIZE_WIDTH, RECIPES_IMAGE_SIZE_HEIGHT);
        }
    }

    private void renderRecipes(GuiGraphics pGuiGraphics, int pX, int pY, int pStartIndex) {
        List<RecipeHolder<SkyMillRecipe>> list = menu.getRecipes();

        for (int i = startIndex; i < pStartIndex && i < menu.getNumRecipes(); ++i) {
            int j = i - startIndex;
            int k = pX + j % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
            int l = j / RECIPES_COLUMNS;
            int i1 = pY + l * RECIPES_IMAGE_SIZE_HEIGHT + 2;
            pGuiGraphics.renderItem(list.get(i).value().getResultItem(minecraft.level.registryAccess()), k, i1);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.scrolling = false;
        if (displayRecipes) {
            int i = leftPos + RECIPES_X;
            int j = topPos + RECIPES_Y;
            int k = startIndex + SCROLLER_WIDTH;

            for (int l = startIndex; l < k; ++l) {
                int i1 = l - startIndex;
                double d0 = pMouseX - (double) (i + i1 % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH);
                double d1 = pMouseY - (double) (j + i1 / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT);
                if (d0 >= 0.0D && d1 >= 0.0D && d0 < RECIPES_IMAGE_SIZE_WIDTH && d1 < RECIPES_IMAGE_SIZE_HEIGHT && menu.clickMenuButton(minecraft.player, l)) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, l);
                    return true;
                }
            }

            i = leftPos + 119;
            j = topPos + 9;
            if (pMouseX >= (double) i && pMouseX < (double) (i + SCROLLER_WIDTH) && pMouseY >= (double) j && pMouseY < (double) (j + SCROLLER_FULL_HEIGHT)) {
                this.scrolling = true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (scrolling && isScrollBarActive()) {
            int i = topPos + RECIPES_Y;
            int j = i + SCROLLER_FULL_HEIGHT;
            this.scrollOffs = ((float) pMouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(scrollOffs, 0.0F, 1.0F);
            this.startIndex = (int) ((double) (scrollOffs * (float) getOffscreenRows()) + 0.5D) * RECIPES_COLUMNS;
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (isScrollBarActive()) {
            int i = getOffscreenRows();
            float f = (float) pDelta / (float) i;
            this.scrollOffs = Mth.clamp(scrollOffs - f, 0.0F, 1.0F);
            this.startIndex = (int) ((double) (scrollOffs * (float) i) + 0.5D) * RECIPES_COLUMNS;
        }
        return true;
    }

    private boolean isScrollBarActive() {
        return displayRecipes && menu.getNumRecipes() > SCROLLER_WIDTH;
    }

    protected int getOffscreenRows() {
        return (menu.getNumRecipes() + RECIPES_COLUMNS - 1) / RECIPES_COLUMNS - RECIPES_ROWS;
    }

    private void containerChanged() {
        this.displayRecipes = menu.hasInputItem();
        if (!displayRecipes) {
            this.scrollOffs = 0.0F;
            this.startIndex = 0;
        }
    }
}
