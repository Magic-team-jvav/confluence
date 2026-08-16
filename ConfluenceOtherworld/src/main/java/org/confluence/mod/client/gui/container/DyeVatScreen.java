package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.menu.DyeVatMenu;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;

public class DyeVatScreen extends AbstractContainerScreen<DyeVatMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/dye_vat.png");

    public DyeVatScreen(DyeVatMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.translatable("button.confluence.dye_mix"), button -> {
            LocalPlayer player = minecraft.player;
            if (player != null) {
                ItemStack stack = player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.DYE_MIX_MENU, stack);
            }
        }).width(48).pos(leftPos + 109, topPos + 58).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
