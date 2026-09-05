package org.confluence.mod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.Confluence;
import org.confluence.mod.network.AskForSoftcorePacket;

import static org.confluence.mod.client.gui.hud.AskForSoftcoreLayer.setAskForSoftcoreLayer;

public class AskForSoftcoreScreen extends Screen {
    private static final ResourceLocation BASE = Confluence.asResource("textures/gui/ask_for_softcore.png");

    private int imageWidth;
    private int imageHeight;
    private int leftPos;
    private int topPos;

    public boolean isChooseSoftcore = false;

    public AskForSoftcoreScreen() {
        super(CommonComponents.EMPTY);
    }

    @Override
    protected void init() {
        super.init();
        this.imageWidth = 190;
        this.imageHeight = 110;
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2 - 33;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(BASE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        guiGraphics.drawString(font, Component.translatable("confluence.difficulty_notice.title"), leftPos + 6, topPos + 4, 0xFFFFFF);
        guiGraphics.drawWordWrap(font, Component.translatable("confluence.difficulty_notice.ask"), leftPos + 7, topPos + 20, imageWidth - 14, 0xFFFFFF);

        // 确认按钮
        int x = leftPos + 6;
        int y = topPos + 113;
        int centerX = width / 2;
        for (int i = 0; i <= 1; i++) {
            y += (28 * i);
            guiGraphics.blit(BASE, x, y, 6,
                    (mouseX > x && mouseX < x + 178 && mouseY > y && mouseY < y + 26) ? 140 : 113,
                    178, 26, 256, 256);
            guiGraphics.drawCenteredString(font,
                    Component.translatable(i == 0 ? "confluence.difficulty_notice.cancel" : "confluence.difficulty_notice.confirm"),
                    centerX, y + (26 - font.lineHeight) / 2, -1);
        }

        // 选项
        y = topPos + 38;
        int x1 = x + 30;
        int y1 = y + 5;
        for (int i = 0; i <= 1; i++) {
            if (i == 1)
                x1 = ((width + imageWidth) / 2) - 79;
            int v = isChooseSoftcore && i == 0 || !isChooseSoftcore && i == 1 ? 92 : 0;
            if (mouseX > x1 && mouseX < x1 + 43 && mouseY > y1 && mouseY < y1 + 45) {
                v += 46;
            }
            guiGraphics.blit(BASE, x1, y1, 213, v, 43, 45, 256, 256);
            guiGraphics.drawCenteredString(font,
                    Component.translatable(i == 0 ? "confluence.difficulty_notice.sure" : "confluence.difficulty_notice.never"),
                    x1 + 21, y1 + 4, 0xFFFFFF);
            guiGraphics.blit(BASE, x1 + 7, y1 + 10, 28 * i, 228, 28, 28, 256, 256);
        }

        guiGraphics.drawWordWrap(font, Component.translatable(isChooseSoftcore ? "confluence.difficulty_notice.sure.tip" : "confluence.difficulty_notice.never.tip"),
                leftPos + 7, topPos + 97, imageWidth - 14, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = leftPos + 6;
        int y = topPos + 113;
        for (int i = 0; i <= 1; i++) {
            y += 28 * i;
            if (mouseX > x && mouseX < x + 178 && mouseY > y && mouseY < y + 26) {
                getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, i));
                if (i == 1)
                    PacketDistributor.sendToServer(new AskForSoftcorePacket(isChooseSoftcore));
                setAskForSoftcoreLayer(false);
                onClose();
                return true;
            }
        }

        y = topPos + 38;
        int x1 = x + 30;
        int y1 = y + 5;

        for (int i = 0; i <= 1; i++) {
            if (i == 1)
                x1 = ((width + imageWidth) / 2) - 70;
            if (mouseX > x1 && mouseX < x1 + 43 && mouseY > y1 && mouseY < y1 + 45) {
                isChooseSoftcore = i == 0;
                getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 2));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
