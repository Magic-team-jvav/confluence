package org.confluence.mod.client.gui.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.AchievementScreen;
import org.confluence.mod.common.menu.CrystalBallMenu;
import org.confluence.mod.network.c2s.KeyRequestPacketC2S;
import org.mesdag.portlib.client.gui.components.PortImageButton;

public class CrystalBallScreen extends AbstractContainerScreen<CrystalBallMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/crystal_ball.png");

    public CrystalBallScreen(CrystalBallMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new PortImageButton(leftPos + 136, topPos + 35, 16, 16, /* todo */ AchievementScreen.SPRITES, button -> KeyRequestPacketC2S.requestClairvoyance()));
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
