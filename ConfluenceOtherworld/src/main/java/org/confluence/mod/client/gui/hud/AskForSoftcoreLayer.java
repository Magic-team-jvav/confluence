package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.network.AskForSoftcorePacket;

public class AskForSoftcoreLayer implements LayeredDraw.Layer {
    private static final Component ask = Component.translatable("confluence.difficulty_notice.ask");
    private static final Component sure = Component.translatable("confluence.difficulty_notice.sure").withStyle(ChatFormatting.GREEN);
    private static final Component sureTip = Component.translatable("confluence.difficulty_notice.sure.tip");
    private static final Component never = Component.translatable("confluence.difficulty_notice.never").withStyle(ChatFormatting.RED);
    private static final Component neverTip = Component.translatable("confluence.difficulty_notice.never.tip");
    private static final Component tip = Component.translatable("confluence.difficulty_notice.tip");
    private static long stamp = -1;
    private static boolean askForSoftcoreLayer = false;

    public static void setAskForSoftcoreLayer(boolean b) {
        askForSoftcoreLayer = b;
    }

    public static boolean isAskForSoftcoreLayer() {
        return askForSoftcoreLayer;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (isAskForSoftcoreLayer()) {
            Minecraft minecraft = Minecraft.getInstance();
            Window window = minecraft.getWindow();
            if (InputConstants.isKeyDown(window.getWindow(), InputConstants.KEY_ESCAPE)) {
                setAskForSoftcoreLayer(false);
                return;
            }
            MouseHandler handler = minecraft.mouseHandler;
            boolean altDown = Screen.hasAltDown();
            boolean clicked = handler.isLeftPressed();
            if (altDown) {
                handler.releaseMouse();
            } else {
                handler.grabMouse();
            }
            Font font = minecraft.font;
            int x = guiGraphics.guiWidth() / 2;
            int y = guiGraphics.guiHeight() / 2;
            int mouseX = (int) (handler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth());
            int mouseY = (int) (handler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight());

            int aw = font.width(ask);
            int ax = x - aw / 2;
            int lh = font.lineHeight;
            int ty = y - 15 + lh;
            guiGraphics.drawString(font, ask, ax, ty, 0xFFFFFF);

            int tw1 = font.width(sure);
            int tx1 = ax;
            ty += lh;
            int color = (0x90 << 24) + 0x505050;
            guiGraphics.fill(tx1 - 1, ty - 1, tx1 + tw1 + 1, ty + lh + 1, color);
            guiGraphics.drawString(font, sure, tx1, ty, 0xFFFFFF, false);
            if (mouseX >= tx1 - 1 && mouseX <= tx1 + tw1 + 1 && mouseY >= ty - 1 && mouseY <= ty + lh + 1) {
                guiGraphics.renderTooltip(font, sureTip, mouseX, mouseY);
                if (altDown && clicked) {
                    PacketDistributor.sendToServer(new AskForSoftcorePacket(true));
                    setAskForSoftcoreLayer(false);
                    return;
                }
            }

            int tw2 = font.width(never);
            int tx2 = ax + aw - tw2;
            guiGraphics.fill(tx2 - 1, ty - 1, tx2 + tw2 + 1, ty + lh + 1, color);
            guiGraphics.drawString(font, never, tx2, ty, 0xFFFFFF, false);
            if (mouseX >= tx2 - 1 && mouseX <= tx2 + tw2 + 1 && mouseY >= ty - 1 && mouseY <= ty + lh + 1) {
                guiGraphics.renderTooltip(font, neverTip, mouseX, mouseY);
                if (altDown && clicked) {
                    PacketDistributor.sendToServer(new AskForSoftcorePacket(false));
                    setAskForSoftcoreLayer(false);
                    return;
                }
            }

            if (stamp == -1) {
                stamp = System.currentTimeMillis();
            }
            long second = 10 - (System.currentTimeMillis() - stamp) / 1000;
            guiGraphics.drawString(font, tip, x - font.width(tip) / 2, y + 15 - lh, 0xFFFFFF);
            Component remain = Component.literal(second + " s");
            guiGraphics.drawString(font, remain, x - font.width(remain) / 2, y + 15, 0xFFFFFF);
            if (second <= 0) {
                setAskForSoftcoreLayer(false);
            }
        } else if (stamp != -1) {
            stamp = -1;
            Minecraft.getInstance().mouseHandler.grabMouse();
        }
    }
}
