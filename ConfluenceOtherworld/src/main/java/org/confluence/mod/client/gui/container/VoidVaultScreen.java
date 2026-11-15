package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.VoidVaultMenu;

public class VoidVaultScreen extends AbstractContainerScreen<VoidVaultMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/void_bag.png");

    public VoidVaultScreen(VoidVaultMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 198;
        imageHeight = 228;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {

        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
